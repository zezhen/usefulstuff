package me.zezhen.java.cms.comsumer;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.jms.TopicSubscriber;

import me.zezhen.java.cms.CMSClient;
import me.zezhen.java.cms.CMSConfigs;
import me.zezhen.java.cms.CMSConfigs.CMSConfig;
import me.zezhen.java.cms.comsumer.processor.IProcessor;
import me.zezhen.java.utils.DateUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

/**
 * 
 * example:
 * 
 * List<ConsumerConfig> comsumerConfigs = Lists.newArrayList();
 * IProcessor processor = new DefaultProcessor();
 * comsumerConfigs.add(new ConsumerConfig(topic, clientId, processor));
 * CMSConsumer consumer = new CMSConsumer(cmsConfigs, principal, comsumerConfigs);
 *      consumer.start(durable);
 *
 */
public class CMSConsumer {

    private static Logger LOG = LoggerFactory.getLogger(CMSConsumer.class);

    private final CMSConfigs cmsConfigs;
    private final List<ConsumerConfig> consumerConfigs;
    private static String principal;

    private final Map<String, ConsumerThread> threadsMap;
    private static ExecutorService executorService;
    
    public CMSConsumer(CMSConfigs cmsConfigs, String _principal, List<ConsumerConfig> consumerConfigs) {
        threadsMap = Maps.newHashMap();
        executorService = Executors.newCachedThreadPool();
        principal = _principal;
        this.cmsConfigs = cmsConfigs;
        this.consumerConfigs = consumerConfigs;
    }

    public void start(boolean durable) {

        for (CMSConfig cmsConfig : cmsConfigs.getConfigs()) {
            for (ConsumerConfig consumerConfig : consumerConfigs) {
                String topic = consumerConfig.getTopic();
                String clientId = consumerConfig.getClientId();
                IProcessor processor = consumerConfig.getProcessor();
                ConsumerThread thread = new ConsumerThread(cmsConfig, clientId, topic, processor, durable);
                thread.setYamasAppName("CMSConsumer");
                threadsMap.put(topic, thread);
                executorService.execute(thread);
            }
        }
    }
    
    public void unsubscribe() {
        executorService.shutdown();
        for(ConsumerThread thread : threadsMap.values()) {
            thread.close();
        }
    }
    
    private static class ConsumerThread implements Runnable {

        private boolean isRunning = true;
        private final CMSConfig cmsConfig;
        private final String clientId;
        private final String topicName;
        private String brokerHost;
        private final boolean durable;
        
        private CMSClient cmsClient;

        private final IProcessor processor;
        
        public ConsumerThread(CMSConfig cmsConfig, String clientId, String topicName, IProcessor processor,
                boolean durable) {
            this.cmsConfig = cmsConfig;
            this.clientId = clientId;
            this.durable = durable;
            this.topicName = topicName;
            this.processor = processor;

            try {
                cmsClient = CMSClient.getConsumerClient(cmsConfig, principal, topicName, clientId);
                cmsClient.connect();
            } catch (JMSException e) {
                LOG.error("initialize consumer thread failed.");
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {

            Long msgCounter = 0L;
            int sleepTimesCounter = 0;
            while(true) {
                try {
                    while (true) {
                        if (!isRunning) {
                            try {
                                Thread.sleep(DateUtils.MILLIS_PER_SECOND * (++sleepTimesCounter));
                            } catch (InterruptedException e) {
                            }
                            continue;
                        }
                        sleepTimesCounter = 0;
                        
                        final TopicSubscriber ts = cmsClient.getTopicSubscriber(durable);
                        if(ts == null) {
                            LOG.warn("get TopicSubscriber is null, re-try...");
                            continue;
                        }
                        
                        TextMessage tm = (TextMessage) ts.receive();
                        if(tm == null) {
                            LOG.warn("get TextMessage is null, re-try...");
                            continue;
                        }
                        
                        // yamas.add(String.format("receive.%s.messages", topicName), 1);
                        
                        msgCounter++;
    
                        LOG.debug("receive " + msgCounter + " messages from broker " + cmsConfig.getBroker()
                                + ", current message(" + tm.getText().length() + " Bytes) = " + tm.getText()
                                + ", log timestamp(s) = " + (new Date().getTime() / 1000) + ", ClientID = " + clientId);
    
                        try {
                            processor.process(tm.getText().toString());
                            // yamas.add(String.format("process.%s.message.success", topicName), 1);
                        } catch (Exception ex) {
                            LOG.error("{} process message {} failed.", processor.getClass(), tm.getText().toString());
                            Thread.sleep(DateUtils.MILLIS_PER_SECOND);
                            // yamas.add(String.format("process.%s.message.fail", topicName), 1);
                        }
    
                        if (msgCounter >= Long.MAX_VALUE) {
                            LOG.info("Reset Message Counter.");
                            msgCounter = 0L;
                        }
                        
                        // yamas.flush();
                    }
    
                } catch (Exception e) {
                    LOG.error("Failed to receive msg from broker.", e);
                    // yamas.add("connect.cms.fails", 1);
                    // yamas.flush();
                    cmsClient.closeSubscriber(durable);
                }
            }
        }

        public void setRunState(boolean state) {
            this.isRunning = state;
        }
        
        public void setYamasAppName(String appName) {
            // this.yamas = new YamasMetrics(appName, 5 * 60, true);
        }
        
        public void close() {
            if(cmsClient == null) {
                return;
            }
            try {
                cmsClient.closeSubscriber(durable);
                cmsClient = null;
            } catch (Exception e) {
                LOG.error("Error when closing connection to " + brokerHost, e);
            }
        }
    }
    
    public void resume() {
        for (ConsumerThread t : threadsMap.values()) {
            t.setRunState(true);
        }
    }

    public void pause() {
        for (ConsumerThread t : threadsMap.values()) {
            t.setRunState(false);
        }
    }

}
