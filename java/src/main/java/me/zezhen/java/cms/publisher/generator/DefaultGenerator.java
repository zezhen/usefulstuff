package me.zezhen.java.cms.publisher.generator;

import me.zezhen.java.cms.publisher.IMessage;

public class DefaultGenerator implements IGenerator<IMessage> {

    @Override
    public String toMessage(IMessage message) {
        return message.toString();
    }

}
