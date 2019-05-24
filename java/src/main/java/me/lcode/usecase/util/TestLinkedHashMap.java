package me.lcode.usecase.util;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.common.collect.Maps;


public class TestLinkedHashMap {

    public static void main(String[] args) {
        
        LinkedHashMap<String, String> map = Maps.newLinkedHashMap(); 
        map.put("1", "1");
        map.put("3", "2");
        map.put("2", "3");
        Iterator<Entry<String,String>> iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Entry<String,String> entry = iter.next();
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
        
        map.put("1", "4");
        map.put("3", "5");
        iter = map.entrySet().iterator();
        while(iter.hasNext()) {
            Entry<String,String> entry = iter.next();
            System.out.println(entry.getKey() + ", " + entry.getValue());
        }
    }

}
