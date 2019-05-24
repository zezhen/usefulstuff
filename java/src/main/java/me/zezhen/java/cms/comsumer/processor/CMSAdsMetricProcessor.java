package me.zezhen.java.cms.comsumer.processor;

import me.zezhen.java.cms.bean.AdsMetric;
import me.zezhen.java.cms.util.AdsMetricParser;
import me.zezhen.java.utils.JsonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMSAdsMetricProcessor implements IProcessor {
    private static Logger LOG = LoggerFactory.getLogger(CMSAdsMetricProcessor.class);
    
    private int count = 0;
    
    @Override
    public boolean process(String message) {
        
        AdsMetric adsMetric = AdsMetricParser.parseByteString(message);
        
        if(adsMetric == null) {
            return false;
        }
        
        LOG.info("process {}th message: {}", (++count), JsonUtils.toJson(adsMetric));
        
        return execute(adsMetric);
    }
    
    protected boolean execute(AdsMetric adsMetric) {
        return true;
    }

}
