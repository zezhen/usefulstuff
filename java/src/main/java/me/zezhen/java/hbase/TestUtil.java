package me.zezhen.java.hbase;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.file.SeekableInput;
import org.apache.avro.generic.GenericContainer;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.Joiner;

public class TestUtil {

    final static Charset UTF8 = Charset.forName("UTF-8");

    /**
     * @param path
     *            The file path to load
     * @return a list of lists of the tab separated tokens in the specified file
     * @throws IOException
     */
    public byte[] loadTestFileAsBytes(String path) throws IOException {
        return loadTestFileAsBytes(new File(path));
    }

    public byte[] loadTestFileAsBytes(File path) throws IOException {
        byte[] out = new byte[(int) path.length()];
        FileInputStream fis = new FileInputStream(path);
        fis.read(out, 0, out.length);
        fis.close();
        return out;
    }

    public void storeTestFileAsGenericRecords(List<GenericContainer> records, File path) throws IOException {
        DatumWriter<GenericContainer> writer = new GenericDatumWriter<GenericContainer>();
        OutputStream fos = new BufferedOutputStream(new FileOutputStream(path));

        @SuppressWarnings("resource")
        DataFileWriter<GenericContainer> dfw = new DataFileWriter<GenericContainer>(writer).create(records.iterator()
                .next().getSchema(), fos);

        for (GenericContainer r : records) {
            dfw.append(r);
        }
        dfw.flush();
        fos.close();
    }

    public List<GenericRecord> loadTestFileAsGenericRecords(File path) throws IOException {
        byte[] asbytes = loadTestFileAsBytes(path);

        DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>();
        SeekableInput bis = new SeekableByteArrayInputStream(asbytes);
        DataFileReader<GenericRecord> dfr = new DataFileReader<GenericRecord>(bis, reader);

        List<GenericRecord> ret = new ArrayList<GenericRecord>();
        while (dfr.hasNext()) {
            ret.add(dfr.next(null));
        }

        return ret;
    }

    public static class SeekableByteArrayInputStream extends ByteArrayInputStream implements SeekableInput {
        public SeekableByteArrayInputStream(byte[] b) {
            super(b);
        }

        public long length() {
            return buf.length;
        }

        public void seek(long p) {
            pos = (int) p;
        }

        public long tell() {
            return pos;
        }
    }

    /**
     * 
     * @param path
     *            The file path to load
     * @return a list of lists of the tab separated tokens in the specified file
     * @throws IOException
     */
    public List<List<String>> loadTestFile(String path) throws IOException {
        return loadTestFile(path, "\t");
    }

    /**
     * Skips # comments and empty lines
     * 
     * @param path
     *            The file to load
     * @param rowdelim
     *            The delimiter to use for parsing row tokens
     * @return a list of lists of delimiter separated tokens
     * @throws IOException
     */
    public List<List<String>> loadTestFile(String path, String rowdelim) throws IOException {
        return loadTestFileUsingCharset(path, rowdelim, Charset.forName("UTF-8"));
    }

    public List<List<String>> loadTestFileUsingCharset(String path, Charset cset) throws IOException {
        return loadTestFileUsingCharset(path, "\t", cset);
    }

    public List<List<String>> loadTestFileUsingCharset(String path, String rowdelim, Charset cset) throws IOException {
        File file = new File(path);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), cset));

        String line = null;
        List<List<String>> rows = new ArrayList<List<String>>();

        try {
            while ((line = br.readLine()) != null) {
                if (emptyString(line)) {
                    continue;
                }
                if (line.indexOf("#") == 0) {
                    continue;
                }
                String[] cols = line.split(rowdelim, -1);

                rows.add(Arrays.asList(cols));
            }
        } finally {
            br.close();
        }
        return rows;
    }

    private boolean emptyString(String line) {
        return line == null || line.trim().equals("");
    }

    public String loadFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), UTF8));
        StringBuffer sb = new StringBuffer();
        char[] cbuf = new char[1024 * 1024];
        int read = -1;
        while ((read = br.read(cbuf)) != -1) {
            sb.append(new String(cbuf, 0, read));
        }
        br.close();
        return sb.toString();
    }

    public String loadFile(String path) throws IOException {
        return loadFile(new File(path));
    }

    public ObjectNode buildUnionValue(String type, JsonNode value) {
        ObjectNode n = new ObjectNode(JsonNodeFactory.instance);
        n.put(type, value);
        return n;
    }

    public String readContents(File file) throws IOException {
        FileInputStream is = new FileInputStream(file);
        byte[] buf = new byte[(int) file.length()];
        try {
            is.read(buf);
        } finally {
            is.close();
        }
        String actual = new String(buf);
        return actual;
    }

    public File createTestFile(File folder, String content) throws IOException {
        return createTestFile(folder, content, true);
    }

    public File createTestFile(File folder, String content, boolean deleteonexit) throws IOException {
        File temp = File.createTempFile("test.", ".file", folder);
        if (deleteonexit) {
            temp.deleteOnExit();
        }
        createFile(temp, content);
        return temp;
    }

    public void createFile(File temp, String content) throws IOException {
        createFile(temp, content, UTF8);
    }

    public void createFile(File file, String content, Charset cset) throws IOException {
        BufferedWriter fo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), cset));
        fo.write(content);
        fo.close();
    }

    private static final AtomicLong foldercounter = new AtomicLong(System.currentTimeMillis());

    public File createTestFolder(String postfix) {
        File f = new File(Joiner.on("/").join("target/tmp/testutil", postfix, foldercounter.getAndIncrement()));
        if(!f.exists()) {
	        if (f.mkdirs()) {
	            f.deleteOnExit();
	            try {
	                Thread.sleep(500);
	            } catch (Exception ex) {
	            }
	            return f;
	        }
        }
        throw new RuntimeException("Unable to create test folder: " + f.getAbsolutePath());
    }

    public File createTestFolder() {
        return createTestFolder("xxx");
    }

    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }
    
    public static void cleanDir(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) {
            return;
        }
        try {
            FileUtils.deleteDirectory(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
