package me.zezhen.java.cms;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.DeliveryMode;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import me.zezhen.java.cms.CMSConfigs.CMSConfig;
import me.zezhen.java.cms.publisher.PublisherConfig;

import org.apache.activemq.ActiveMQTopicPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yahoo.cloud.messaging.client.Address;
import com.yahoo.cloud.messaging.client.ClientConfiguration;
import com.yahoo.cloud.messaging.client.ConnectionFactoryLookupService;
import com.yahoo.cloud.messaging.client.tutorial.JmsAdministrativeObject;

/**
 * used by consumer or publisher to connect to CMS
 */
public class CMSClient {

    private static Logger LOG = LoggerFactory.getLogger(CMSClient.class);

    private TopicConnection connection;
    private TopicSession session;
    private Topic topic;
    private TopicPublisher tp;
    private TopicSubscriber ts;

    private ClientConfiguration clientConfig;
    
    private String brokerHost;
    private final CMSConfig cmsConfig;
    private final String principal;
    private final String clientId;
    private final String topicName;
    private boolean isSSLUsed;
    
    private static final long MSG_TIME_TO_LIVE = 3600000; // ms
    
    public static CMSClient getConsumerClient(CMSConfig cmsConfig, String principal, String topicName, String clientId) throws JMSException {
        return new CMSClient(cmsConfig, principal, topicName, clientId);
    }
    
    public static CMSClient getPublisherClient(CMSConfig cmsConfig, String principal, String topicName, String certStorePath) throws JMSException {
        CMSClient client = new CMSClient(cmsConfig, principal, topicName, null);
        if(certStorePath != null) {
            client.prepareCertstore(certStorePath);
            client.setUseSSL(true);
        } else {
            client.setUseSSL(false);
        }
        return client;
    }
    
    private CMSClient(CMSConfig cmsConfig, String principal, String topicName, String clientId)
            throws JMSException {
        this.cmsConfig = cmsConfig;
        this.principal = principal;
        this.clientId = clientId;
        this.topicName = topicName;
    }

    public void connect() throws JMSException {
        if(connection != null) {
            LOG.info("A connection already exist, no need to create another one.");
            return;
        }
        
        String broker = cmsConfig.getBroker();
        int port = cmsConfig.getPort();
        if ("v2".equalsIgnoreCase(cmsConfig.getNsVersion())) {
            try {
                String brokerUri = String.format("http://%s:%d", broker, port);
                clientConfig = new ClientConfiguration(new URL(brokerUri));
            } catch (MalformedURLException e) {
                throw new RuntimeException("CMS Broker HOST is not properly set in configuration");
            }
        } else if ("v1".equalsIgnoreCase(cmsConfig.getNsVersion())) {
            String id = clientId != null ? clientId : cmsConfig.getNamespace();
            clientConfig = new ClientConfiguration(id);
        } else {
            throw new RuntimeException("namespance version should be set to v1/v2 in config.");
        }
        
        /**
         * Add directory service addresses. You may add as many as appropriate. NOTE: the following hostname is
         * hard-coded to connect with CMS sandbox Please change it to proper lookup host name in the specific CMS
         * environment according to your application's deployment plan. The VIP list for different environments can be
         * found here: http://twiki.corp.yahoo.com/view/Messaging/MessagingServiceTutorial#Integration_environments
         */
        clientConfig.addDirectoryServiceAddress(new Address(broker, port));
        /**
         * Set YCA (version 1) Principal to authenticate with the service.
         * 
         * For YCA (version 2), applications MUST obtain certificate and use
         * <code>ClientConfiguration.setCredentials(String)</code> API to set the certificate string as credentials. For
         * example:
         * <p>
         * config.setCredentials(credentials);
         */
        clientConfig.setPrincipal(principal);
        
        clientConfig.setUseSSL(isSSLUsed);

        brokerHost = clientConfig.getDirectoryServiceAddressList().get(0).getHost();
        LOG.info("connection factory lookup for " + brokerHost + "//" + cmsConfig.getNamespace());

        TopicConnectionFactory factory = null;
        if ("v2".equalsIgnoreCase(cmsConfig.getNsVersion())) {
            ConnectionFactoryLookupService lookupService = new ConnectionFactoryLookupService(clientConfig);
            factory = (TopicConnectionFactory) lookupService.lookupNamespace(cmsConfig.getNamespace());
        } else {
            factory = (TopicConnectionFactory) JmsAdministrativeObject.connectionFactoryLookup(clientConfig);
        }

        LOG.info("Creating connection to {}...", brokerHost);
        connection = factory.createTopicConnection();
        if(clientId != null) {
            connection.setClientID(clientId);
        }
        connection.setExceptionListener(new ExceptionListener() {
            public void onException(JMSException e) {
                LOG.error("Catch CMS asynchronous exception, restart connection... ", e);
                try {
                    ts.close();
                    connection.close();
                    connection = null;
                } catch (JMSException ex) {
                    LOG.error("Recreate a new connection fails. ", ex);
                }
            }
        });
        
        LOG.info("create topic session ");
        session = connection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);
        if ("v2".equalsIgnoreCase(cmsConfig.getNsVersion())) {
            String fullyQualifiedDestinationName = String.format("topic://%s/%s", cmsConfig.getNamespace(), topicName);
            topic = session.createTopic(fullyQualifiedDestinationName);
        } else {
            topic = JmsAdministrativeObject.topicLookup(session, clientConfig, topicName);
        }
        
        connection.start();
        LOG.info("broketHost: {}, clientId: {}, topic: {}, namespace {}", brokerHost, clientId, topicName, cmsConfig.getNamespace());
    }

    public void setUseSSL(boolean isSSLUsed) {
        this.isSSLUsed = isSSLUsed;
    }
    
    public boolean publish(String message, int cmsMsgPublishTimeout) {
        if(connection == null || session == null) {
            try {
                connect();
            } catch (JMSException e) {
                LOG.error("connect cms failed.", e);
                return false;
            }
            
        }
        boolean sendSuccess = true;
        if (message != null) {
            // use ByteMessage replacing of TextMessage to support ProtoBuf
            try {
                TopicPublisher publisher = getTopicPublisher(cmsMsgPublishTimeout);
                TextMessage tm = session.createTextMessage(message);
                publisher.publish(tm, DeliveryMode.PERSISTENT, 4, MSG_TIME_TO_LIVE);
            } catch (Exception e) { // catch Request Timeout exception
                LOG.warn("Failed to send messages {} to topic {}.", message, topicName, e);
                closePublisher();
                sendSuccess = false;
            }
        }
        return sendSuccess;
    }
    
    public TopicSubscriber getTopicSubscriber(boolean durable) throws JMSException {
        if(connection == null || ts == null) {
            checkConnection();
            LOG.info("creating durable [{}] subscribe...", durable);
            ts = durable ? session.createDurableSubscriber(topic, clientId) : session.createSubscriber(topic);
        }
        return ts;
    }
    
    public TopicPublisher getTopicPublisher(int cmsMsgPublishTimeout) throws JMSException {
        if(connection == null || tp == null) {
            checkConnection();
            LOG.info("creating publisher with send timeout {}...", cmsMsgPublishTimeout);
            tp = session.createPublisher(topic);
            ((ActiveMQTopicPublisher) tp).setSendTimeout(cmsMsgPublishTimeout);
        }
        return tp;
    }
    
    private void checkConnection() throws JMSException {
        if(connection == null) {
            LOG.info("connection is null, re-connect");
            if(session != null) {
                try {
                    LOG.info("close session...");
                    session.close();
                } catch(JMSException e) {
                }
                session = null;
            }
            connect();
        }
    }

    public void closeSubscriber(boolean durable) {
        LOG.info("close subscriber...");
        if (ts != null) {
            try {
                ts.close();
            } catch (JMSException e) {
                LOG.warn("close topic subscriber error.", e);
            }
            ts = null;
        }
        try {
            if (session != null && durable) {
                session.unsubscribe(clientId);
                LOG.info("Successfully unsubscribe " + clientId + " from broker " + brokerHost);
            }
        } catch (Exception unsubscribeEx) {
            LOG.error("Failed to unsubscribe clientID " + clientId + " from broker " + brokerHost
                    + ", will try again...", unsubscribeEx);
        }
        if(session != null) {
            try {
                session.close();
            } catch (JMSException e) {
                LOG.warn("close session error.", e);
            }
            session = null;
        }
        if (connection != null) {
            try {
                connection.close();
            } catch (JMSException e) {
                LOG.warn("close connection error.", e);
            }
            connection = null;
        }
    }
    
    public void closePublisher() {
        LOG.info("close publisher...");
        try {
            tp.close();
            tp = null;
        } catch (JMSException e) {
            LOG.warn("close topic publisher error.", e);
        }
        
        try {
            session.close();
        } catch (JMSException e) {
            LOG.warn("close session error.", e);
        }
        session = null;
        try {
            connection.close();
        } catch (JMSException e) {
            LOG.warn("close connection error.", e);
        }
        connection = null;
        
    }
    
    private void prepareCertstore(String certStorePath) {
        
        String newTrustStoreFile = certStorePath;
        if (newTrustStoreFile.indexOf("/certstore.jks") < 0) {
            newTrustStoreFile += "/certstore.jks";
        }
        String trustStore = newTrustStoreFile;

        String sysTrustStore = System.getProperty(PublisherConfig.TRUST_STORE_SETTING);
        if (sysTrustStore == null || "".equals(sysTrustStore) || !sysTrustStore.equals(trustStore)) {
            LOG.info(PublisherConfig.TRUST_STORE_SETTING + " was " + sysTrustStore + " and will be set to "
                    + trustStore);
            System.setProperty(PublisherConfig.TRUST_STORE_SETTING, trustStore);
        }

        // extract SSL trust store and copy to user defined path
        InputStream is = null;
        OutputStream os = null;
        if (!new File(newTrustStoreFile).exists()) {
            new File(newTrustStoreFile).getParentFile().mkdirs();
            
            try {
                is = this.getClass().getResourceAsStream("/jks/certstore.jks");
                os = new FileOutputStream(newTrustStoreFile);
                byte[] buf = new byte[1024];
                int count = 0;
                while ((count = is.read(buf)) > 0) {
                    os.write(buf, 0, count);
                }
            } catch (IOException ex) {
                if (!new File(newTrustStoreFile).exists()) {
                    throw new RuntimeException("Prepare CMS SSL Trust Store IOException", ex);
                }
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                    if (os != null) {
                        os.close();
                    }
                } catch (IOException e) {
                    LOG.warn("close in/output stream failed.", e);
                }
            }

            if (!new File(newTrustStoreFile).exists()) {
                throw new RuntimeException("the trust store file " + newTrustStoreFile + " is not exist!");
            }
        }
    }
    
}
