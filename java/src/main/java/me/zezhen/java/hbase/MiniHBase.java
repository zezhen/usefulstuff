package me.zezhen.java.hbase;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.LocalHBaseCluster;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.util.Bytes;

import com.netflix.curator.test.TestingServer;

public class MiniHBase {

    private static TestUtil util = new TestUtil();

    private Configuration config;

    private File hbaseFolder;

    public int zkPort = 4181;

    private LocalHBaseCluster hbaseMaster;

    private final AtomicBoolean hbaseStarted = new AtomicBoolean(false);

    private final AtomicBoolean zkStarted = new AtomicBoolean(false);

    private HBaseAdmin hbaseAdmin;

    private TestingServer zkServer;

	private String zkHost = "127.0.0.1";

    public MiniHBase(int zkPort) {
        this.zkPort = zkPort;
    }

    public MiniHBase(String zkHost, int zkPort) {
    	this.zkHost = zkHost;
        this.zkPort = zkPort;
    }
    
    public void start() throws Exception {

        startZookeeper();
        startHBase();

    }

    public void close() throws IOException {

        stopHBase();
        stopZookeeper();

    }

    private void startZookeeper() throws Exception {

        if (zkStarted.get()) {
            return;
        }

        zkServer = new TestingServer(zkPort);
        zkStarted.set(true);
    }

    private void stopZookeeper() throws IOException {

        if (!zkStarted.get()) {
            return;
        }
        zkServer.close();
        zkStarted.set(false);
    }

    private void startHBase() throws IOException, InterruptedException {

        if (hbaseStarted.get()) {
            return;
        }

        File folder = util.createTestFolder();
        hbaseFolder = new File(folder, "hbase");
        assert hbaseFolder.mkdirs();

        config = HBaseConfiguration.create();

        config.set(HConstants.HBASE_DIR, "file://" + hbaseFolder.getAbsolutePath());

        config.set("hbase.cluster.distributed", "false");
        config.set("hbase.tmp.dir", "file://" + hbaseFolder.getAbsolutePath());
        config.set("hbase.zookeeper.property.clientPort", zkPort + "");
        config.set("hbase.master.port", "50000");
        config.set("hbase.master.info.port", "-1");
        config.set("hbase.regionserver.port", "50020");
        config.set("hbase.regionserver.info.port", "-1");
        config.set("hbase.zookeeper.quorum", zkHost);

        Iterator<Entry<String, String>> itr = config.iterator();
        System.out.println("Configure dump: ");
        while (itr.hasNext()) {
            Entry<String, String> entry = itr.next();
        }

        assert LocalHBaseCluster.isLocal(config);

        hbaseMaster = new LocalHBaseCluster(config);
        if("127.0.0.1".equals(zkHost)) {
            hbaseMaster.startup();
        }
        hbaseAdmin = new HBaseAdmin(config);

        int tries = 0;

        while (!hbaseAdmin.isMasterRunning() && tries++ < 5) {
            Thread.sleep(500);
        }

        assert tries < 10;

        hbaseStarted.set(true);
    }

    private void stopHBase() throws IOException {
        hbaseMaster.shutdown();
        hbaseFolder.delete();
        hbaseStarted.set(false);

    }

    public void deleteTable(String tableName) throws IOException {

        if (hbaseAdmin.tableExists(tableName)) {
            hbaseAdmin.disableTable(Bytes.toBytes(tableName));
            hbaseAdmin.deleteTable(Bytes.toBytes(tableName));
        }

    }

    public HTable ensureTable(String tableName, String... colfams) throws IOException,
            InterruptedException {

        if (hbaseAdmin.tableExists(tableName)) {
            return new HTable(config, tableName);
        }

        HTableDescriptor d = new HTableDescriptor(tableName);

        for (String fam : colfams) {
            HColumnDescriptor c = new HColumnDescriptor(fam);
            d.addFamily(c);
        }

        hbaseAdmin.createTable(d);
        return new HTable(config, tableName);

    }

    public synchronized void stopHbase() throws IOException {

        if (!hbaseStarted.get()) {
        }

        hbaseMaster.shutdown();
        hbaseStarted.set(false);

    }

    public Configuration getHBaseConfiguration() throws Exception {
        return config;
    }
}
