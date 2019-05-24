import com.yahoo.curveball.budget.hbase.CacheRowKey;
import com.yahoo.curveball.budget.hbase.CacheRowKeyConverter;


public class FakeSpendPlans {

    public static void main(String[] args) {
        
        String cacheVersion = args.length > 0 ? args[0] : "2";
        String type = args.length > 1 ? args[1] : "256";
        long total = args.length > 2 ? Long.parseLong(args[2]) : 1000l;
        String cacheId = args.length > 3 ? args[3] : String.valueOf(System.currentTimeMillis() / 1000 / 900 * 900 - 900);
        
        CacheRowKeyConverter converter = new CacheRowKeyConverter();
        
        StringBuilder sb = new StringBuilder();
        
        for (long id = 100; id < total; id ++) {
            CacheRowKey key = new CacheRowKey(cacheVersion, cacheId, ""+id, type);
            String row = converter.toRowKeyString(key);
            String command = "put 'budget:spending_plans','" + row + "','b:sp','{\"S01\":-1.0,\"S02\":-1.0,\"S12\":-1.0,"
                    + "\"shts\":0,\"shets\":0,\"dailyCap\":-1.0,\"dailySpend\":-1.0,\"invalidDailySpend\":-1.0,\"dayBoundary\":0}'";
            sb.append(command);
            sb.append(";");
            command = "put 'budget:spending_plans','" + row + "','b:st','{\"trate\":1.0,\"sn\":1454284800000000031}'";
            sb.append(command);
            sb.append(";");
        }
        sb.append(String.format("put 'budget:spending_plans_meta','%s_latest_spend_plan_start_ts','b:ts','{\"interval_start\":%s,\"plan_gen_start\":%s}'",
                cacheVersion, cacheId, cacheId));
        System.out.println(String.format("echo \"%s\" | hbase shell", sb.toString()));
        
    }

}
