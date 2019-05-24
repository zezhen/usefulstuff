package me.zezhen.java.cms.util;

import java.util.List;

import me.zezhen.java.cms.bean.AdsMetric;
import me.zezhen.java.cms.bean.AdsMetric.AdsItem;
import me.zezhen.java.cms.bean.AdsMetric.Dimension;
import me.zezhen.java.cms.proto.CMSAdsMetricProtos.CMSAdsMetric;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;

public class AdsMetricParser {

    private static Logger LOG = LoggerFactory.getLogger(AdsMetricParser.class);

    public static String getByteString(AdsMetric adsMetric) {
        if (adsMetric == null) {
            return null;
        }
        CMSAdsMetric.Builder builder = parse(adsMetric);
        return getByteString(builder);
    }

    private static CMSAdsMetric.Builder parse(AdsMetric adsMetric) {
        CMSAdsMetric.Builder cmsAdsMetric = CMSAdsMetric.newBuilder();

        for(AdsItem adsItem :  adsMetric.getAdsItems()) {
            CMSAdsMetric.AdsItem.Builder aitem = CMSAdsMetric.AdsItem.newBuilder();
            
            Dimension dimension = adsItem.dimension;

            CMSAdsMetric.Dimension.Builder dim = CMSAdsMetric.Dimension.newBuilder();
            dim.setAdvertiser(dimension.advertiser);
            dim.setBucketId(dimension.bucketId);
            dim.setCampaign(dimension.campaign);
            dim.setCountry(dimension.country);
            dim.setDeveice(dimension.device);
            dim.setSection(dimension.section);
            dim.setTimestamp(dimension.timestamp);
            dim.setTrack(dimension.track);
            dim.setProduct(dimension.product);
            aitem.setDimension(dim.build());

            aitem.setBiddedServe(adsItem.biddedServe);
            aitem.setClick(adsItem.click);
            aitem.setImpression(adsItem.impression);
            aitem.setNorthImpression(adsItem.northImpression);
            aitem.setRevenue(adsItem.revenue);
            aitem.setRevenueCPC(adsItem.revenueCPC);
            aitem.setRevenueCPI(adsItem.revenueCPI);
            aitem.setRevenueCPM(adsItem.revenueCPM);
            aitem.setRevenuedClick(adsItem.revenuedClick);
            aitem.setRevenuedImpression(adsItem.revenuedImpression);
            aitem.setRevenuedInstall(adsItem.revenuedInstall);
            aitem.setRevenueUS(adsItem.revenueUS);
            aitem.setServe(adsItem.serve);
            aitem.setServedAds(adsItem.servedAds);
            
            cmsAdsMetric.addAds(aitem);
        }

        return cmsAdsMetric;
    }

    private static String getByteString(CMSAdsMetric.Builder builder) {
        CMSAdsMetric message = builder.build();
        byte[] encodedBytes = Base64.encodeBase64(message.toByteArray());
        return new String(encodedBytes);
    }

    public static AdsMetric parseByteString(String message) {
        if (message == null) {
            return null;
        }
        try {
            byte[] b = Base64.decodeBase64(message.getBytes());
            CMSAdsMetric cmsAdsMetric = CMSAdsMetric.parseFrom(b);
            return toAdsMetric(cmsAdsMetric);
        } catch (InvalidProtocolBufferException e) {
            LOG.error("Failed to parse cms record from {}", message);
            return null;
        }
    }

    private static AdsMetric toAdsMetric(CMSAdsMetric cmsAdsMetric) {
        List<AdsItem> ads = Lists.newArrayList();
        for(CMSAdsMetric.AdsItem cmsAdsItem : cmsAdsMetric.getAdsList()) {
            AdsItem adsItem = new AdsItem();
            CMSAdsMetric.Dimension dim = cmsAdsItem.getDimension();
            adsItem.dimension = new Dimension(dim.getCampaign(), dim.getAdvertiser(), dim.getBucketId(),
                    dim.getSection(), dim.getDeveice(), dim.getCountry(), dim.getTrack(), dim.getTimestamp(), dim.getProduct());

            adsItem.biddedServe = cmsAdsItem.getBiddedServe();
            adsItem.click = cmsAdsItem.getClick();
            adsItem.impression = cmsAdsItem.getImpression();
            adsItem.northImpression = cmsAdsItem.getNorthImpression();
            adsItem.revenue = cmsAdsItem.getRevenue();
            adsItem.revenueCPC = cmsAdsItem.getRevenueCPC();
            adsItem.revenueCPI = cmsAdsItem.getRevenueCPI();
            adsItem.revenueCPM = cmsAdsItem.getRevenueCPM();
            adsItem.revenuedClick = cmsAdsItem.getRevenuedClick();
            adsItem.revenuedImpression = cmsAdsItem.getRevenuedImpression();
            adsItem.revenuedInstall = cmsAdsItem.getRevenuedInstall();
            adsItem.revenueUS = cmsAdsItem.getRevenueUS();
            adsItem.serve = cmsAdsItem.getServe();
            adsItem.servedAds = cmsAdsItem.getServedAds();
            
            ads.add(adsItem);
        }

        return new AdsMetric(ads);
    }
}
