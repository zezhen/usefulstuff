package me.zezhen.java.cms.bean;

import java.util.ArrayList;
import java.util.List;

import me.zezhen.java.cms.publisher.IMessage;
import me.zezhen.java.utils.JsonUtils;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Lists;


public class AdsMetric implements IMessage {
    
    private List<AdsItem> ads;
    
    public AdsMetric(List<AdsItem> ads) {
        this.ads = ads;
    }
    
    public List<AdsItem> getAdsItems() {
        if(ads == null) {
            return new ArrayList<AdsItem>();
        }
        return ads;
    }
    
    public void addAdsItem(AdsItem adsItem) {
        if(ads == null) {
            ads = Lists.newArrayList();
        }
        ads.add(adsItem);
    }
    
    public static class AdsItem { 
        public Dimension dimension;
    
        // $: total native/search revenue in USD currency.
        // revenue += cstusd
        public double revenue;
        
        // total revenue in US region USD currency
        public double revenueUS;
    
        // $C: revenue from CPC.
        // revenueCPC += cstusd when pt is CPC
        public double revenueCPC;
    
        // $I: revenue from CPI.
        // revenueCPI += cstusd when pt is CPI
        public double revenueCPI;
    
        // $M: revenue from CPM.
        // revenueCPM += cstusd when pt is CPM
        public double revenueCPM;
    
        // S: count of valid ad calls/search,
        // not serve events count, such as test events with invalid section id.
        // serve ++ when event is MBServe or SMServe,
        public long serve;
    
        // SB: count of valid ad calls/searches returning at least one ad.
        // biddedServe ++ when event is MBServe or SMServe and contains demandoffer
        public long biddedServe;
    
        // SA: count of ads served by the valid ad calls.
        // servedAds += ads number in offers when event is MBServe or SMServe
        public long servedAds;
    
        // M: count of valid impression beacons or
        // count of Gemini ads served by SFE as recorded in the merge events.
        // impression ++ when event is MBImpression or SMMerge and adltype == 1 (Yahoo)
        public long impression;
    
        // M$: count of impressions that carry revenue, CPM use case.
        // revenuedImpression ++ when event contains cstusd and pt == CPM
        public long revenuedImpression;
    
        // NM: count of impressions shown in the north positions.
        // northImpression ++ when event contains ppos and its value is n1
        public long northImpression;
    
        // C: count of valid click beacons.
        // click ++ when event is MBClick or SMClick
        public long click;
    
        // C$: count of clicks that carry revenue, CPC use case.
        // revenuedClick ++ when event contains cstusd and pt == CPC
        public long revenuedClick;
    
        // I$: count of valid install beacons, CPI use case, assume all installs are revenue bearing.
        // revenuedInstall ++ when event contains cstusd and pt == CPI
        public long revenuedInstall;
        
        public void add(AdsItem adsItem) {
            if(adsItem == null) {
                return;
            }
            
            this.biddedServe += adsItem.biddedServe;
            this.click += adsItem.click;
            this.impression += adsItem.impression;
            this.northImpression += adsItem.northImpression;
            this.revenue += adsItem.revenue;
            this.revenueUS += adsItem.revenueUS;
            this.revenueCPC += adsItem.revenueCPC;
            this.revenueCPI += adsItem.revenueCPI;
            this.revenueCPM += adsItem.revenueCPM;
            this.revenuedClick += adsItem.revenuedClick;
            this.revenuedImpression += adsItem.revenuedImpression;
            this.revenuedInstall += adsItem.revenuedInstall;
            this.serve += adsItem.serve;
            this.servedAds += adsItem.servedAds;
        }
    }
    
    public static class Dimension {
        public int section;
        public int device;
        public long campaign;
        public int country;
        public long advertiser;
        public String bucketId;
        public String track;
        public long timestamp;
        public String product;

        public Dimension(long campaign, long advertiser, String bucketId, int section, int device, int country,
                String track, long timestamp, String product) {
            super();
            this.campaign = campaign;
            this.advertiser = advertiser;
            this.section = section;
            this.device = device;
            this.country = country;
            this.bucketId = bucketId;
            this.track = track;
            this.timestamp = timestamp;
            this.product = product;
        }
        
        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 31)
                .append(campaign)
                .append(advertiser)
                .append(section)
                .append(device)
                .append(country)
                .append(bucketId)
                .append(track)
                .append(timestamp)
                .append(product)
                .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Dimension)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            
            Dimension dim = (Dimension) obj;
            return new EqualsBuilder()
                .append(campaign, dim.campaign)
                .append(advertiser, dim.advertiser)
                .append(section, dim.section)
                .append(device, dim.device)
                .append(country, dim.country)
                .append(bucketId, dim.bucketId)
                .append(track, dim.track)
                .append(timestamp, dim.timestamp)
                .append(product, dim.product)
                .isEquals();
        }

//        public String generateDimensionKey() {
//            return String.format("%d_%d_%s_%d_%d_%d_%d_%s_%s", campaign, advertiser, bucketId, section, device, country, track, timestamp);
//        }

    }

    @Override
    public int getSignalNum() {
        return ads.size();
    }

    @Override
    public String getMessageTypeName() {
        return "ads";
    }

    @Override
    public String getContent() {
        return JsonUtils.toJson(ads);
    }
    
}

