//import java.io.BufferedReader;
//import java.io.FileReader;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Set;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import yahoo.budget.tools.audit.AbstractData;
//import yahoo.budget.tools.audit.AbstractData.CacheData;
//import yahoo.budget.tools.audit.HBaseLoader;
//import yahoo.budget.tools.conf.ToolConfig;
//import yahoo.budget.tools.hbase.HBaseUtils.COLO;
//import yahoo.budget.tools.util.Utils;
//
//
//public class VerifySpendPlans {
//
//    private static Logger LOG = LoggerFactory.getLogger(VerifySpendPlans.class);
//    
//    public static void main(String[] args) throws Exception {
//        
//        if (args == null || args.length < 4) {
//            System.err.println("please input : config,colo,action,start,end[,output_file[,keys]]");
//            return;
//        }
//        String configPath = args[0];
//        String coloStr = args[1];
//        long cacheId = Long.parseLong(args[2]);
//        String tumblrFile = args[3];
//        
//        ToolConfig config = new ToolConfig(configPath);
//        COLO colo = Utils.convertEnum(coloStr, COLO.class, null);
//
//        Map<String, AbstractData> map = HBaseLoader.loadCacheStatus(config, colo, cacheId);
//
//        Set<String> tumblrs = new HashSet<String>();
//        BufferedReader br = new BufferedReader(new FileReader(tumblrFile));
//        String line = null;
//        while((line = br.readLine()) != null) {
//            line = line.trim();
//            if(line.isEmpty()) {
//                continue;
//            }
//            tumblrs.add(line + "_256");
//        }
//        br.close();
//        
//        for(Entry<String, AbstractData> entry : map.entrySet()) {
//            String key = entry.getKey();
//            CacheData data = (CacheData) entry.getValue();
//            
//            if(key.endsWith("256") && "EVEN".equals(data.pacingType)) {
//                if(tumblrs.contains(key)) {
//                    continue;
//                }
//                
//                if(key.endsWith("256") && "MB".equals(data.track)) {
//                    if("EVEN".equals(data.pacingType) && ! "SMOOTH".equals(data.smartAlgo)) {
//                            System.out.println(key + " " + data.timezone + " " + data.pacingType + " " + data.smartAlgo + " " + data.dailyCap);
//                    } else if("EVEN".equals(data.pacingType)) {
//                            System.out.println("!" + key + " " + data.timezone + " " + data.pacingType + " " + data.smartAlgo + " " + data.dailyCap);
//                    } else if(data.dailyCap > 0 || data.dailySpend > 0) {
//                            System.out.println("@" + key + " " + data.timezone + " " + data.pacingType + " " + data.smartAlgo + " " + data.dailyCap);
//                    } else {
//                            System.out.println("#" + key + " " + data.timezone + " " + data.pacingType + " " + data.smartAlgo + " " + data.dailyCap);
//                    }
//                }
//            }
//        }
//    }
//
//}
