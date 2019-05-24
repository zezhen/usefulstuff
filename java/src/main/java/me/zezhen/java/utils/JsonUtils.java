package me.zezhen.java.utils;

import java.io.StringReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

public class JsonUtils {
    
    private static Logger LOG = LoggerFactory.getLogger(JsonUtils.class);

    private static Gson gson = new Gson();
    
    public static <T> T fromJson(String json, Class<? extends T> clazz) {
        if(json == null) {
            return null;
        }
        try {
            return (T) gson.fromJson(new StringReader(json), clazz);
        } catch(RuntimeException e) {
            LOG.warn("parse object from json {} failed.", json, e);
            return null; 
        }
    }
    
    public static String toJson(Object obj) {
        if(obj == null) {
            return "{}";
        }
        try {
            return gson.toJson(obj);
        } catch (RuntimeException e) {
            LOG.warn("convert {} to json failed.", obj, e);
            return "{}";
        }
    }
    
}
