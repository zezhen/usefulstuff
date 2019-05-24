import java.io.IOException;

//import com.yahoo.curveball.budget.cms.bean.CMSRecord;
//import com.yahoo.curveball.budget.cms.util.CMSRecordParser;
//import com.yahoo.curveball.budget.common.utils.JsonUtils;


public class ParseBudgetBlob {

    public static void main(String[] args) throws IOException {

        if(args.length < 2) {
            System.err.println("please timestamp, budget blob");
            System.exit(1);
        }

        String timestamp = args[0];
        String[] budgetBlobs = args[1].split("AND");

        System.out.print(timestamp);
        for(String blob : budgetBlobs) {
//            ByteArrayInputStream bytesIS = new ByteArrayInputStream(blob.getBytes("UTF-8"));
//            BufferedReader br = new BufferedReader(new InputStreamReader(bytesIS));
//            blob = br.readLine();
            
//            String str = new String(blob.getBytes(), StandardCharsets.UTF_8);
//            CMSRecord cmsRecord = CMSRecordParser.parseByteString(blob);
//            System.out.print("\t" + blob + "\t" + JsonUtils.toJson(cmsRecord));
//            br.close();
        }

        System.out.println();

        
    }

}