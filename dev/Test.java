import org.json.*;
import java.util.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;


public class Test {
    /* Untested */
    public static String fileToString(String path, Charset encoding) throws IOException{
        //Parse out comments here
        //Set up scanner the deliminates by \n, //, /*,/*, then check the 
        //beginning of the string accordingly
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public static void main(String[] args){
        String jsonString = "{\"stat\":  {\"sdr\": \"aa:bb:cc:dd:ee:ff\", \"rcv\": \"aa:bb:cc:dd:ee:ff\", \"time\": \"UTC in millis\", \"type\": 1, \"subt\": 1, \"argv\": [{\"type\": 1, \"val\":\"stackoverflow\"}]}}";
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject newJSON = jsonObject.getJSONObject("stat");
        System.out.println(newJSON.toString());
        jsonObject = new JSONObject(newJSON.toString());
        System.out.println(jsonObject.getString("rcv"));
        System.out.println(jsonObject.getJSONArray("argv"));

        try{
            System.out.println(fileToString("Test.java",StandardCharsets.UTF_8));
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
