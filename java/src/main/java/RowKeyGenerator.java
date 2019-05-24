import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.yahoo.curveball.budget.hbase.CacheRowKey;
import com.yahoo.curveball.budget.hbase.CacheRowKeyConverter;


public class RowKeyGenerator {

    public static void main(String[] args) throws ParseException {

        if(args.length < 3) {
            System.err.println("input ids type version [cahceId]");
            System.exit(1);
        }
        
        String[] ids = args[0].split(",");
        String type = args[1];
        String cacheVersion = args[2];
        
        long timestamp = System.currentTimeMillis();
         
        if(args.length > 3) {
            if(args[3].startsWith("20")) {
                SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddhhmm"); 
                Date date = dt.parse(args[3]);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                timestamp = c.getTimeInMillis();
            } else {
                timestamp = Long.parseLong(args[3]);
            }
        }
        
        String cacheId = String.valueOf(timestamp / 1000 / 900 * 900 - 900);
        
        
        CacheRowKeyConverter converter = new CacheRowKeyConverter();
        
        StringBuilder sb = new StringBuilder();
        for (String id : ids) {
            CacheRowKey key = new CacheRowKey(cacheVersion, cacheId, id, type);
            String row = converter.toRowKeyString(key);
            String command = "get 'budget:spending_plans','" + row + "'";
            sb.append(command);
            sb.append(";");
        }
        String command = sb.substring(0, sb.length() - 1);
        System.out.println(String.format("echo \"%s\" | hbase shell", command));
    }

}
