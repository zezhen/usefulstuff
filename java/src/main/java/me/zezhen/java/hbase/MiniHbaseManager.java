package me.zezhen.java.hbase;

import java.io.IOException;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.zookeeper.server.quorum.QuorumPeerConfig.ConfigException;

import com.yahoo.curveball.budget.hbase.IHBaseDAO;


public class MiniHbaseManager {

    IHBaseDAO hbaseProxy;
    private MiniHBase miniHbase;
    private static MiniHbaseManager instance = new MiniHbaseManager();

    private int port = 0;
    private final String host = "127.0.0.1";

    public MiniHbaseManager() {
    }

    public static MiniHbaseManager getInstance() {
        return instance;
    }

    public void createTable(String tableName, String... families) throws IOException, ConfigException,
            InterruptedException {
        miniHbase.ensureTable(tableName, families);
    }

    public void cleanup() throws IOException {
        miniHbase.close();
    }

    public void startHbase() throws Exception {
        if (miniHbase != null) {
            return;
        }
        while (true) {
            this.port = ((int) (Math.random() * 100)) + 2181;
            try {
                TelnetClient client = new TelnetClient();
                client.setDefaultTimeout(1000);
                client.connect("127.0.0.1", port);
            } catch (IOException e) {
                break;
            }
        }

        miniHbase = new MiniHBase(port);
        miniHbase.start();
    }
    public void startHbase(int port) throws Exception {
    	startHbase("127.0.0.1", port);
    }
    public void startHbase(String host, int port) throws Exception {
        if (miniHbase != null) {
            return;
        }

        miniHbase = new MiniHBase(host, port);
        miniHbase.start();
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

}
