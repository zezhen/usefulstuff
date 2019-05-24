package me.zezhen.java.cms.publisher.generator;

public interface IGenerator<T> {

    String toMessage(T message);
    
}
