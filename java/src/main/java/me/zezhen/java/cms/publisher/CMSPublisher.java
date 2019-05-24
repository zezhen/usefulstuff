package me.zezhen.java.cms.publisher;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.JMSException;

import me.zezhen.java.cms.CMSClient;
import me.zezhen.java.cms.CMSConfigs;
import me.zezhen.java.cms.CMSConfigs.CMSConfig;
import me.zezhen.java.cms.publisher.generator.IGenerator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 
 * publisherConfig = new PublisherConfig(cmsUseSSL, trustStorePath, cmsMessageQueueSize,
 *          cmsMsgPublishTimeout, topics);
 * IGenerator<String> generator = new DefaultGenerator();
 * CMSPublisher<String> publisher = CMSPublisher.create(cmsConfigs, principal, publisherConfig, generator);
 * publisher.start();
 *
 * @param <T>
 */
public class CMSPublisher {

    private static Logger LOG = LoggerFactory.getLogger(CMSPublisher.class);

    private final CMSConfigs cmsConfigs;
    private final PublisherConfig publisherConfig;
    private final String principal;

    private final Map<String, PublisherThread> threadsMap;
    private static ExecutorService executorService;
    
    private final IGenerator<IMessage> generator;
    private String appName;
    
    private static CMSPublisher instance;
    
    public static synchronized CMSPublisher getInstance(CMSConfigs cmsConfigs, String _principal, PublisherConfig publisherConfig, IGenerator<IMessage> generator) {
        if(instance == null) {
            instance = new CMSPublisher(cmsConfigs, _principal, publisherConfig, generator);
        }
        return instance;
    }
    
    private CMSPublisher(CMSConfigs cmsConfigs, String _principal, PublisherConfig publisherConfig, IGenerator<IMessage> generator) {
        threadsMap = Maps.newHashMap();
        executorService = Executors.newCachedThreadPool();

        this.principal = _principal;
        this.cmsConfigs = cmsConfigs;
        this.publisherConfig = publisherConfig;
        this.generator = generator;
    }

    public void setYamas(String appName) {
        this.appName = appName;
    }
    
    public void publishMessages(IMessage...messages) {
        for (String topic : publisherConfig.getTopics()) {
            publishMessages(topic, messages);
        }
    }
    
    public void publishMessages(String topic, IMessage...messages) {
        for (IMessage message : messages) {
            for (CMSConfig cmsConfig : cmsConfigs.getConfigs()) {
                String key = cmsConfig.getNamespace() + "//" + topic;
                if (threadsMap.get(key) == null || !threadsMap.get(key).isRunning) {
                    LOG.error("Thread for publishing message to " + key
                            + " is not alive. Try start a new thread...");
                    createPublisherThread(cmsConfig, topic);
                }
                threadsMap.get(key).addMessage(message);
            }
        }
    }
    
    public void clean() {
        for(PublisherThread thread : threadsMap.values()) {
            thread.clean();
        }
    }

    private void createPublisherThread(CMSConfig cmsConfig, String topicName) {
        String key = cmsConfig.getNamespace() + "//" + topicName;
        
        if (threadsMap != null && threadsMap.get(key) != null
                && threadsMap.get(key).isRunning) {
            LOG.error("Thread for publishing message to " + key + " is alive. Will not create a new thread.");
            return;
        }
        
        PublisherThread thread = new PublisherThread(key, cmsConfig, topicName, publisherConfig, appName);
        threadsMap.put(key, thread);
        executorService.execute(thread);
    }

    private class PublisherThread implements Runnable {

        private final PublisherConfig publisherConfig;
        
        private CMSClient cmsClient;
        private final LinkedBlockingQueue<IMessage> messageQueue;
        
        private final String threadId;
        private final String topic;
        private boolean isRunning = true;
        
        private final int MAX_RETRY_PUBLISH_LIMIT = 10;
        private final long CMS_RECONNECT_SLEEP = 1000;
        
        private final String DROP_SIGNAL_METRICS_NAME = "DropSignalCounter";
        private final String SEND_FAILURE_METRICS_NAME = "SendFailureCounter";
        private final String SENDQUEUE_BACKLOG_SIZE_METRICS_NAME = "SendQueueBacklogSize";
        private final String SEND_RETRY_METRICS_NAME = "SendRetryCounter";
        private final String SEND_SUCC_SIGNAL_METRICS_NAME = "SuccessfullySentSignalCounter";
        private final String SEND_SUCC_MESSAGE_METRICS_NAME = "SuccessfullySentMessageCounter";
        private final String PUBLISHER_TO_CMS_LATENCY_NAME = "PublisherToCMSLatency";
        private final String MESSAGE_TO_PUBLISHER_LATENCY_NAME = "MessageToPublisherLatency";
        private final String EVENT_TO_PUBLISHER_LATENCY_NAME = "EventToPublisherLatency";
        
        // this buckets for record latency between cms publisher to server, unit is millis
        private final double[] latencyHistogramBuckets = new double[]{0, 10, 100, 1000, 5000, 30000};
        
        private int numDropSignal;
        private int numSendFails;
        private int numSendQueueBacklogSize;
        

        public PublisherThread(String threadId, CMSConfig cmsConfig, String topic, PublisherConfig publisherConfig, String appName) {
            this.threadId = threadId;
            this.topic = topic;
            this.publisherConfig = publisherConfig;
            this.messageQueue = new LinkedBlockingQueue<IMessage>();
            try {
                String trustStorePath = publisherConfig.isCmsUseSSL() ? publisherConfig.getTrustStorePath() : null;
                cmsClient = CMSClient.getPublisherClient(cmsConfig, principal, topic, trustStorePath);
                cmsClient.connect();
            } catch (JMSException e) {
                LOG.error("initialize consumer thread failed.");
                throw new RuntimeException(e);
            }
            if(appName == null) {
                // dummy yamas
                // yamas = new YamasMetrics("", false);
            } else {
                // yamas = new YamasMetrics(appName, true);
            }
        }

        public synchronized void addMessage(IMessage message) {
            int queueSize = messageQueue.size();
            if (queueSize < publisherConfig.getCmsMessageQueueSize()) {
                messageQueue.add(message);
            } else {
                IMessage messageDrop;
                try {
                    messageDrop = messageQueue.take();
                    LOG.error("Message queue of " + threadId + " is full. Drop message: " + generator.toMessage(messageDrop));
                    numDropSignal += messageDrop.getSignalNum();
                    messageQueue.add(message);
                } catch (InterruptedException e) {
                    LOG.error("Got InterruptedException when get message from queue.");
                }
            }
            numSendQueueBacklogSize = messageQueue.size();
        }
        
        @Override
        public void run() {
            isRunning = true;
            long msgCounter = 0L;
            
            try {
                while (true) {
                    
                    IMessage message = null;
                    try {
                        // waiting if necessary until an element becomes available
                        message = messageQueue.take();
                    } catch (InterruptedException e1) {
                        LOG.error("Got InterruptedException when get message from queue.");
                        message = null;
                    }
                    
                    if(message == null) {
                        continue;
                    }
                    
                    // yamas.clearAllDimension();
                    // yamas.addDimension("MessageType", message.getMessageTypeName());
                    long publishTimeMillis = System.currentTimeMillis();
                    
                    int retry = 0;
                    while(++ retry <= MAX_RETRY_PUBLISH_LIMIT) {
                        String content = generator.toMessage(message);
                        LOG.debug("{} Publish message {} to topic {}.", threadId, content, topic);
                        if(cmsClient.publish(content, publisherConfig.getCmsMsgPublishTimeout())) {
                            LOG.info("Successfully send {} to topic {}", content, topic);
                            break;
                        }
                        
                        ++ numSendFails;

                        try {
                            Thread.sleep(CMS_RECONNECT_SLEEP);
                        } catch (InterruptedException ex) {
                            LOG.error("Got InterruptedException in publishing message.");
                        }
                    }
                    
                    if (retry <= MAX_RETRY_PUBLISH_LIMIT) {
                        // yamas.add(SEND_SUCC_MESSAGE_METRICS_NAME, 1);
                        // yamas.add(SEND_SUCC_SIGNAL_METRICS_NAME, message.getSignalNum());
                    } else {
                        LOG.error("Drop unsent messages {}, should be sent to {} on topic: {}.",
                                message.toString(), threadId, topic);
                        numDropSignal += message.getSignalNum();
                    } 
                    retry = 0;
                    
                    long publishLatency = System.currentTimeMillis() - publishTimeMillis;
                    
                    // yamas.setHistogram(PUBLISHER_TO_CMS_LATENCY_NAME, publishLatency, latencyHistogramBuckets);
                    // yamas.add(DROP_SIGNAL_METRICS_NAME, numDropSignal);
                    // yamas.set(SENDQUEUE_BACKLOG_SIZE_METRICS_NAME, numSendQueueBacklogSize);
                    // yamas.add(SEND_FAILURE_METRICS_NAME, numSendFails);
                    numDropSignal = 0;
                    numSendQueueBacklogSize = 0;
                    numSendFails = 0;
                }
            } finally {
                cmsClient.closePublisher();
                isRunning = false;
            }
            
        }
        
        public void clean() {
            messageQueue.clear();
        }

    }

}
