package me.zezhen.java.cms;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;

public class CMSConfigs implements Serializable {

    private List<CMSConfig> configs;

    public List<CMSConfig> getConfigs() {
        return configs;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static CMSConfigs fromJsonFile(String filePath) {
        Gson gson = new Gson();
        CMSConfigs producers = null;
        try {
            producers = gson.fromJson(new InputStreamReader(new FileInputStream(filePath)), CMSConfigs.class);
        } catch (Exception e) {
            throw new RuntimeException("parse cms config file " + filePath + " failed.", e);
        }
        return producers;
    }

    public static class CMSConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        public static final int DEFAULT_PORT = 4080;
        
        private String nsVersion;
        private String namespace;
        private String broker;
        private int port;

        public String getId() {
            StringBuffer sb = new StringBuffer();
            sb.append(nsVersion.trim());
            sb.append(":");
            sb.append(broker.trim());
            sb.append(":");
            sb.append(namespace.trim());
            
            if(port != 0) {
                sb.append(":");
                sb.append(port);
            }

            return sb.toString();
        }

        public String getNsVersion() {
            return nsVersion.trim();
        }

        public String getNamespace() {
            return namespace.trim();
        }

        public String getBroker() {
            return broker.trim();
        }
        
        public int getPort() {
            return (port == 0) ? DEFAULT_PORT : port;
        }
        
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append("nsVersion:" + nsVersion);
            sb.append(", namespace:" + namespace);
            sb.append(", broker:" + broker);

            return sb.toString();
        }
    }
}
