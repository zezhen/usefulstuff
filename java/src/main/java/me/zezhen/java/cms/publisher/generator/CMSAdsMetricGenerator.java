package me.zezhen.java.cms.publisher.generator;

import me.zezhen.java.cms.bean.AdsMetric;
import me.zezhen.java.cms.publisher.IMessage;
import me.zezhen.java.cms.util.AdsMetricParser;

public class CMSAdsMetricGenerator implements IGenerator<IMessage> {

    @Override
    public String toMessage(IMessage adsMetric) {
        return AdsMetricParser.getByteString((AdsMetric) adsMetric);
    }

}
