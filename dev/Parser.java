import org.json.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.util.regex.Pattern;


public class Parser {
    //////////
    //Fields//
    //////////
    private HashMap<String,DataObj> dataObjsMap;
    private HashMap<String,Relation> relationsMap;
    private ArrayList<String> imports;

    ////////////////
    //Constructors//
    ////////////////
    public Parser(){
        dataObjsMap = new HashMap<String,DataObj>();
        relationsMap = new HashMap<String,Relation>();
        imports = new ArrayList<String>();
    }

    ///////////
    //Methods//
    ///////////
    public HashMap<String,DataObj> getDataObjsMap(){ return dataObjsMap;}
    /** Uses a StringBuilder to convert the file into a String and strip all comments */
    public String fileToString(String path) throws IOException{
        String match =  "//.*?\n" +"|"+ //Match single line comments
                        "/\\*.*?\\*/";  //Match multi line comment (/* */)
        Pattern p = Pattern.compile(match,Pattern.DOTALL | Pattern.MULTILINE);
        Scanner reader = new Scanner(new File(path)).useDelimiter(p);

        StringBuilder s = new StringBuilder(1024);
        while(reader.hasNext()){
            s.append(reader.next());
            s.append("\n");
        }
        return s.toString();
    }

    public void parseFile(String filename){
        try{
            String text = fileToString(filename);
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

            //Parse Imports
            parseImports(imports);

            //Parse Data
            parseData(data);
            //Resolve realtions and fields
            resolveRealtions();

            
        }catch(IOException e){
            System.err.println("Error opening up file!");
            e.printStackTrace();
        }
    }
	
    
    public void resolveRealtions(){
        //1) Resolve all fields of D.O.s and Relations
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            String name = (String)one.getKey();
            DataObj data = (DataObj)one.getValue();
            data.resolve(dataObjsMap,relationsMap);
        }
        
        //2) Confirm all Relations are resolved, otherwise report error
        it = relationsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            Relation r = (Relation)one.getValue();
            if(!r.isResolved()){//If we have an unresolved relationship
                System.out.println("Unresolved relationship!");
                System.out.println("Unable to find matching DataObject for relationship:");
                System.out.println(r);
                System.out.println("Make sure that there is a Data Object by the same name");
                System.exit(1);
            }
        }
    }
    /* Parse the JSONObject data field and add it to the HashMap */
    public void parseData(JSONObject data){
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
                    Relation r = new Relation(relationName,objName,toThis);
                    dataObj.addRelation(r);
                    relationsMap.put(relationName,r);
                }
            }
            //TODO add SortBy option
        }
    }
    public void parseImports(JSONArray importsArr){
        for(int i=0; i<importsArr.length(); i++){
            imports.add(importsArr.getString(i));
        }
    }


    /* Confirm that something is true. If it isn't it will output the text
     * followed by a backtrace to see where the error is occuring*/
    public void assertTrue(boolean b,String text){
        if(!b){
            System.out.println(text);
            Thread.currentThread().getStackTrace(); //Print stack strace
            System.exit(1);
        }
    }
}
