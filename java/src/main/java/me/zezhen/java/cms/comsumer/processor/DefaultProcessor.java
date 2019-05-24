package me.zezhen.java.cms.comsumer.processor;


public class DefaultProcessor implements IProcessor {

    @Override
    public boolean process(String t) {
        System.out.println(t);
        return true;
    }

}
