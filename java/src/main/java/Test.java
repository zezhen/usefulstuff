import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.yahoo.curveball.budget.cms.CMSRecordProtos.CMSMessage;
import com.yahoo.curveball.budget.cms.CMSRecordProtos.CMSMessage.ParentLevel;
import com.yahoo.curveball.budget.cms.CMSRecordProtos.CMSMessage.SignalInfo;
import com.yahoo.curveball.budget.hbase.CacheRowKey;
import com.yahoo.curveball.budget.hbase.CacheRowKeyConverter;

public class Test implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = -5169941539104841813L;
    static TT tt = new TT();
    private static Logger LOG = LoggerFactory.getLogger(Test.class);

    public static void main(String[] args) throws Exception {
        Pattern p = Pattern.compile(".*\\.(.*)\\.yahoo.com");
        Matcher m = p.matcher("fe47.cbs.cb.bf1.yahoo.com");
        if(m.matches()) {
            System.out.println(m.group(1));
        } else {
            System.out.println("unknown");
        }
        
    }
    
    public static long replaceCacheId(long sn, long cacheId) {
        long ISSN_CACHE_ID_OFFSET = 100000000000L;
        return cacheId / 100 * ISSN_CACHE_ID_OFFSET + sn %  ISSN_CACHE_ID_OFFSET;
    }
    
    public static  <T> T parse(Object o) {
        return parse(o, null);
    }
    
    public static  <T> T parse(Object o, T defaultValue) {
        return o != null ? (T) o : defaultValue; 
    }
    
    public static String evaluate(String multiLayerBucketId) {
        String[] arr = new String[]{"CBMATCH", "CBCLKB", "CBGPA", "CBREL", "CBOPT,CBMATCH_T", 
                                  "CBCLKB_T", "CBGPA_T", "CBREL_T", "CBOPT_T,CBMATCH_M", "CBCLKB_M", 
                                  "CBGPA_M", "CBREL_M", "CBOPT_M", "CB_M", "CB_T", "MSFT", "ADMR"};
        Set<String> buckets = new HashSet<String>(Arrays.asList(arr));
        
        Set<String> hits = new HashSet<String>();
        for(String kv : multiLayerBucketId.split("&")) {
            System.out.println(kv);
            String[] kvs = kv.split("=");
            if(kvs.length >= 2 && buckets.contains(kvs[0])) {
                hits.add(kvs[1]);
            }
        }
        List<String> ret = new ArrayList<String>(hits);
        Collections.sort(ret);
        StringBuilder sb = new StringBuilder(",");
        for(String bucket : ret) {
            sb.append(bucket);
            sb.append(",");
        }
        return sb.toString();
    }

    final List<String> list = Collections.synchronizedList(new ArrayList<String>());
    
    void testQueue() {
        
        
        
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        
        new Thread() {
            @Override
            public void run() {
                synchronized(list) {
                for(String item : list) {
                    System.out.println(item);
                    System.out.println("=========" + list.size());
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                list.clear();
                System.out.println("=========>>>>>>" + list.size());
                }
            }
        }.start();
        for(int i =0; i < 1000; i++) {
            System.out.println(">>>>>>>>>>>>>>>>>" + i);
            list.add("" + i);
        }
        
        System.out.println("=========#########" + list.size());
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("=========#########" + list.get(0));
        System.out.println("=========#########" + list.size());
    }

//    public static List<RowData> run(ExecutorService executor, final String flag) {
//        Future<List<RowData>> future = executor.submit(new Callable<List<RowData>>() {
//            public List<RowData> call() throws Exception {
//                System.out.println("pre: " + flag);
//                Thread.sleep(10 * 1000);
//                System.out.println("post: " + flag);
//                return null;
//            }
//        });
//        try {
//            return future.get(3 * 1000, TimeUnit.MILLISECONDS);
//        } catch (TimeoutException e) {
//            future.cancel(true);
//            System.out.println("timeout: " + flag);
//        } catch (Exception e) {
//            future.cancel(true);
//            System.out.println("exception: " + flag);
//        }
//        return null;
//    }

    public static long getDayStart(String timezone, Long utcTime) {
        TimeZone tz = TimeZone.getTimeZone(timezone);
        Calendar cal = Calendar.getInstance(tz);
        Date date = new Date(utcTime);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTimeInMillis();
    }

    public static Long getDayStart2(String timezoneString, Long utcTimestamp) throws Exception {

        TimeZone utcTimezone = TimeZone.getTimeZone("UTC");
        TimeZone localTimezone = TimeZone.getTimeZone(timezoneString);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(localTimezone);

        Date date = new Date(utcTimestamp);
        calendar.setTime(date);
        long day = 24l * 60 * 60 * 1000;
        int offset = localTimezone.getOffset(utcTimestamp);
        Long localDayStart = ((utcTimestamp + offset) / day) * day;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setTimeZone(utcTimezone);
        String localTime = dateFormat.format(localDayStart);

        dateFormat.setTimeZone(localTimezone);
        return dateFormat.parse(localTime).getTime();
    }

    static void test_timer() {
        long current = System.currentTimeMillis();
        current = current / 1000 / 60 * 1000 * 60 + 1000;
        System.out.println(current);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int i = 0;
            @Override
            public void run() {
                System.out.println(System.currentTimeMillis());
                if(++i == 4) {
                    try {
                        Thread.sleep(1500l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        timer.schedule(task, new Date(current), 1000);
    }
    
    static void test_scheduledExecutor() {
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(3);
        
        long current = System.currentTimeMillis();
        long first = current / 1000 * 1000 + 4000;
        System.out.println(current);
        System.out.println(first);
        final int[] i = new int[]{0};
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println(System.currentTimeMillis());
                if(++i[0] == 4) {
                    try {
                        Thread.sleep(1500l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, first - current, 1000, TimeUnit.MILLISECONDS);
        
        try {
            Thread.sleep(6500l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        scheduledExecutor.shutdown();
        
        scheduledExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                System.out.println(System.currentTimeMillis());
                if(++i[0] == 4) {
                    try {
                        Thread.sleep(1500l);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, first - current, 1000, TimeUnit.MILLISECONDS);
    }

    static void test() {
        double revenue = 0, revenueCPC = 0, revenueCPI = 0, revenueCPM = 0;
        int revenuedImpression = 0, northImpression = 0;
        int biddedServe = 0;
        int revenuedClick = 0, revenuedInstall = 0;
        int servedAds = 0, serve = 0, impression = 0, click = 0;
        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String timestamp = formater.format(System.currentTimeMillis());

        Map<String, Object> metricsMap = Maps.newHashMap();
        metricsMap.put("biddedServe", biddedServe);
        metricsMap.put("click", click);
        metricsMap.put("impression", impression);
        metricsMap.put("northImpression", northImpression);
        metricsMap.put("revenue", revenue);
        metricsMap.put("revenueCPC", revenueCPC);
        metricsMap.put("revenueCPI", revenueCPI);
        metricsMap.put("revenueCPM", revenueCPM);
        metricsMap.put("revenuedClick", revenuedClick);
        metricsMap.put("revenuedImpression", revenuedImpression);
        metricsMap.put("revenuedInstall", revenuedInstall);
        metricsMap.put("serve", serve);
        metricsMap.put("servedAds", servedAds);
        revenue += revenue;

        metricsMap.put("track", "cbmb");
        metricsMap.put("country", "country");
        metricsMap.put("campaign", "campaign");
        metricsMap.put("advertiser", "advertiser");
        metricsMap.put("device", "device");
        metricsMap.put("section", "section");
        metricsMap.put("timestamp", timestamp);
        System.out.println(new Gson().toJson(metricsMap));
    }

    static void copy() {

//        long start = DateUtils.getDateMills("201604071855", "yyyyMMddHHmm");
//        long end = DateUtils.getDateMills("201604072030", "yyyyMMddHHmm");
//
//        for (long ts = start; ts <= end; ts += 300 * 1000) {
//            //String s = DateUtils.getFormatDate(ts - 300 * 1000, "yyyyMMddHHmm");
//            String e = DateUtils.getFormatDate(ts, "yyyy-MM-dd HH:mm");
//            String cmd = String.format("/home/y/bin64/perl /home/y/bin64/curveball_budget_data_check/data_copy.pl all '%s' bf1 gq1", e);
//            System.out.println(cmd);
//        }
    }

    static int hashCode(String str) {
        char[] value = str.toCharArray();
        int h = 0;
        if (h == 0 && value.length > 0) {
            char val[] = value;

            for (int i = 0; i < value.length; i++) {
                h = 31 * h + val[i];
                System.out.println(val[i] + " " + h);
            }
        }
        return h;
    }
    
    static void index() {
//        for(int i = 1; i < 1000; i++) {
//            System.out.print(i + " ");
//            System.out.println(Math.abs((i + "_256").hashCode()) % 20);
//        }
        int i = 170;
//        System.out.println(Math.abs((i + "_256").hashCode()) % 20);
        System.out.println(Math.abs(hashCode(i + "_256")) % 20);
    }

    static void cacherowkey() {
        CacheRowKeyConverter converter = new CacheRowKeyConverter();

        String cacheId = "1463594400";
        String[] ids = "2".split(",");
        for (String id : ids) {
            CacheRowKey key = new CacheRowKey("3", cacheId, id, "257");
            String row = converter.toRowKeyString(key);
            String command = "put 'budget:spending_plans','" + row + "'";
            System.out.println(command);
        }
    }
    
    static void testBuilder() {
        CMSMessage.SignalInfo.Builder info = CMSMessage.SignalInfo.newBuilder();
//        info.setParentId(signalInfo.getParentId());
//
//        info.setMessageType(cmsRecord.getType());
//        info.setParentLevel(CMSMessage.ParentLevel.valueOf(signalInfo.getParentLevel()));
//
//        info.setThrottleRate(signalInfo.getThrottleRate());
//        info.setSequenceId(signalInfo.getSequenceId());
//        info.setPlanType(signalInfo.getPlanType());
//        if (signalInfo.getAdvertiserType() != null) {
//            // set proper advertiser type
//            info.setAdvertiserType(signalInfo.getAdvertiserType());
//        }
//        cmsMessage.addSignalInfo();
        info.setSequenceId(1l);
        info.setParentId(1l);
        info.setParentLevel(ParentLevel.CAMPAIGN);
        info.setThrottleRate(1f);
        SignalInfo si = info.build();
        System.out.println(si.toString());
    }

    private static String time() {
        DateFormat formater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = formater.format(Calendar.getInstance().getTime());
        return time;
    }
}

class TT {

    private final String s = "1";
    private double d;
    private final Enum e = Enum.E1;

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}

enum Enum {
    E1, E2
}
