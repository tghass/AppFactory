import org.json.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.util.regex.Pattern;


public class Parser {
    //Variables
    public static HashMap<String,DataObj> dataObjsMap = new HashMap<String,DataObj>();
    public static HashMap<String,Relation> relationsMap = new HashMap<String,Relation>();


    /** Uses a StringBuilder to convert the file into a String and strip all comments */
    public static String fileToString(String path) throws IOException{
        String match =  "//.*?\n" +"|"+ //Match single line comments
                        "/\\*.*?\\*/";
        Pattern p = Pattern.compile(match,Pattern.DOTALL | Pattern.MULTILINE);
        Scanner reader = new Scanner(new File(path)).useDelimiter(p);

        StringBuilder s = new StringBuilder(1024);
        while(reader.hasNext()){
            s.append(reader.next());
            s.append("\n");
        }
        return s.toString();
    }

    public static void main(String[] args){
        /*
        String jsonString = "{\"stat\":  {\"sdr\": \"aa:bb:cc:dd:ee:ff\", \"rcv\": \"aa:bb:cc:dd:ee:ff\", \"time\": \"UTC in millis\", \"type\": 1, \"subt\": 1, \"argv\": [{\"type\": 1, \"val\":\"stackoverflow\"}]}}";
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONObject newJSON = jsonObject.getJSONObject("stat");
        jsonObject = new JSONObject(newJSON.toString());
        System.out.println(jsonObject.getString("rcv"));
        System.out.println(jsonObject.getJSONArray("argv"));
        */

        try{
            String text = fileToString("../config.json");
            JSONObject conf = new JSONObject(text);
            //Four first fileds
            assertTrue(conf.has("import"),"Configuration file missing 'import'");
            JSONArray imports = conf.getJSONArray("import");
            assertTrue(conf.has("data"),"Configuration file missing 'data'");
            JSONObject data = conf.getJSONObject("data");
            assertTrue(conf.has("pages"),"Configuration file missing 'pages'");
            JSONObject pages = conf.getJSONObject("pages");
            assertTrue(conf.has("links"),"Configuration file missing 'links'");
            JSONObject links = conf.getJSONObject("links");
            //Print out data
            parseImports(imports);

            //Iterate thru
            parseData(data);
            Iterator it = dataObjsMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry one = (Map.Entry)it.next();
                System.out.println(one.getValue().toString());
            }
        }catch(IOException e){
            System.err.println("Error opening up file!");
            e.printStackTrace();
        }
    }
    /* Parse the JSONObject data field and add it to the HashMap */
    public static void parseData(JSONObject data){
        Iterator<String> keys = data.keys();
        while(keys.hasNext()){//Loop thruy all data objs
            String objName = (String)keys.next();

            DataObj dataObj = new DataObj(objName);
            dataObjsMap.put(objName,dataObj);

            JSONObject oneObj = data.getJSONObject(objName);

            //Parse fields
            assertTrue(oneObj.has("fields"),"DataObj "+objName+" missing 'fields'");
            JSONObject fields = oneObj.getJSONObject("fields");
            Iterator<String> fieldIt = fields.keys();
            while(fieldIt.hasNext()){
                String fieldName = (String)fieldIt.next();
                String type = fields.getString(fieldName);
                //TODO, check if type is another DataObj
                dataObj.addField(new Field(fieldName,type));
            }
            //Add Display
            assertTrue(oneObj.has("display"),"DataObj "+objName+" missing 'display'");
            JSONArray display = oneObj.getJSONArray("display");
            for(int i=0;i<display.length();i++){
                dataObj.addDisplay(display.getString(i));
            }
            //For Relations (optional field)
            if(oneObj.has("relations")){
                JSONObject relations = oneObj.getJSONObject("relations");
                Iterator<String> relationIt = relations.keys();
                while(relationIt.hasNext()){
                    String relationName = (String)relationIt.next();
                    String toThis = relations.getString(relationName);
                    dataObj.addRelation(new Relation(relationName,objName,toThis));
                }
            }
        }
    }
    public static List<DataObj> parseImports(JSONArray imports){
        System.out.println("IMPORTS:");
        for(int i=0; i<imports.length(); i++){
            System.out.print(imports.getString(i)+",");
        }System.out.println();
        return new ArrayList<DataObj>();
        //TODO, return actual objs
    }


    public static void assertTrue(boolean b,String text){
        if(!b){
            System.out.println(text);
            Thread.currentThread().getStackTrace();
            System.exit(1);
        }
    }
}
