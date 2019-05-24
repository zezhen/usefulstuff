package me.zezhen.java.cms.comsumer;

import me.zezhen.java.cms.comsumer.processor.IProcessor;


public class ConsumerConfig {

    private final String topic;
    private final String clientId;
    private final IProcessor processor;

    public ConsumerConfig(String topic, String clientId, IProcessor processor) {
        super();
        this.topic = topic;
        this.clientId = clientId;
        this.processor = processor;
    }

    public String getTopic() {
        return topic;
    }

    public String getClientId() {
        return clientId;
    }

    public IProcessor getProcessor() {
        return processor;
    }

}
