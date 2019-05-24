package me.zezhen.java.cms.publisher;

import java.util.List;

public class PublisherConfig {

    public static final String TRUST_STORE_SETTING = "javax.net.ssl.trustStore";

    private final boolean cmsUseSSL;
    private final String trustStorePath;

    private final int cmsMessageQueueSize;
    private final int cmsMsgPublishTimeout;

    private final List<String> topics;

    public PublisherConfig(boolean cmsUseSSL, String trustStorePath, int cmsMessageQueueSize,
            int cmsMsgPublishTimeout, List<String> topics) {
        super();
        this.cmsUseSSL = cmsUseSSL;
        this.trustStorePath = trustStorePath;
        this.cmsMessageQueueSize = cmsMessageQueueSize;
        this.cmsMsgPublishTimeout = cmsMsgPublishTimeout;
        this.topics = topics;
    }

    public boolean isCmsUseSSL() {
        return cmsUseSSL;
    }

    public String getTrustStorePath() {
        return trustStorePath;
    }

    public List<String> getTopics() {
        return topics;
    }

    public int getCmsMessageQueueSize() {
        return cmsMessageQueueSize;
    }

    public int getCmsMsgPublishTimeout() {
        return cmsMsgPublishTimeout;
    }

}
