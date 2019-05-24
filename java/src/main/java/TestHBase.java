//import java.io.IOException;
//
//import yahoo.budget.common.utils.hbase.HBaseDAO;
//import yahoo.budget.common.utils.hbase.HBaseStorageConfig;
//import yahoo.budget.common.utils.hbase.IHBaseDAO;
//import yahoo.budget.common.utils.hbase.RowData;
//
//public class TestHBase {
//
//    public static void main(String[] args) throws IOException {
//
//        String hbaseSiteXmlFile = args[0];
//        String keytab = args[1];
//        String principal = args[2];
//
//        HBaseStorageConfig hBaseStorageConfig = new HBaseStorageConfig(hbaseSiteXmlFile);
//        hBaseStorageConfig.setPrincipal(principal);
//        hBaseStorageConfig.setKeytab(keytab);
//        IHBaseDAO hbaseDAO = HBaseDAO.getInstance(hBaseStorageConfig);
//
//        // IHBaseDAO hbaseDAO = HBaseDAO.getInstance(new HBaseStorageConfig("127.0.0.1", 2181));
//        hbaseDAO.write("budget:spending_plans", "b", "test_row", "col", "value");
//        RowData data = hbaseDAO.read("budget:spending_plans", "b", "test_row");
//        String value = data.getStringByField("col");
//        System.out.println("========================");
//        System.out.println(value);
//        System.out.println("========================");
//    }
//
//}

//import java.io.IOException;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.hbase.HBaseConfiguration;
//import org.apache.hadoop.hbase.client.Delete;
//import org.apache.hadoop.hbase.client.Get;
//import org.apache.hadoop.hbase.client.HTable;
//import org.apache.hadoop.hbase.client.Put;
//import org.apache.hadoop.hbase.client.Result;
//import org.apache.hadoop.hbase.security.User;
//import org.apache.hadoop.hbase.util.Bytes;
//
//public class TestHBase {
//
//    private final static String HBASE_ZOOKEEPER_CLIPORT = "hbase.zookeeper.property.clientPort";
//    private final static String HBASE_ZOOKEEPER_QUORUM = "hbase.zookeeper.quorum";
//    private final static String HBASE_MASTER = "hbase.master";
//    
//    public static void main(String[] args) throws IOException {
//
//        String zkQuorum = args[0];
//        String zkPort = args[1];
//        
//        Configuration config = HBaseConfiguration.create();
//        config.set(HBASE_ZOOKEEPER_CLIPORT, zkPort);
//
//        config.set(HBASE_ZOOKEEPER_QUORUM, zkQuorum);
//        
//        config.set(HBASE_MASTER, zkQuorum + ":" + zkPort);
//
//        System.out.println(User.isHBaseSecurityEnabled(config));
//
//        String rowkey = "01|1            _256|1311231605";
//        HTable table = new HTable(config, "test");
//        Put put = new Put(rowkey.getBytes());
//        put.add("cf".getBytes(), "click".getBytes(), "123".getBytes());
//        table.put(put);
//
//        Get get = new Get(rowkey.getBytes());
//        Result result = table.get(get);
//        String value = Bytes.toString(result.getValue("b".getBytes(), "click".getBytes()));
//        System.out.println(value);
//
//        Delete delete = new Delete(rowkey.getBytes());
//        table.delete(delete);
//
//        table.close();
//    }
//
//}