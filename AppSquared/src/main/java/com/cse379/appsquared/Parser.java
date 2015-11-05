package com.cse379.appsquared;

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
    private HashMap<String,PageObj> pageObjMap;
    private ArrayList<String> imports;

    ////////////////
    //Constructors//
    ////////////////
    public Parser(){
        dataObjsMap = new HashMap<String,DataObj>();
        relationsMap = new HashMap<String,Relation>();
        pageObjMap = new HashMap<String,PageObj>();
        imports = new ArrayList<String>();
    }

    ///////////
    //Methods//
    ///////////
    public HashMap<String,DataObj> getDataObjsMap(){ return dataObjsMap;}
    public HashMap<String,PageObj> getPageObjsMap(){ return pageObjMap;}
    /** Uses a StringBuilder to convert the file into a String and strip all comments */
    public String fileToString(String path) throws IOException{
        String match =  "//.*?\n" +"|"+ //Match single line comments
                        "/\\*.*?\\*/";  //Match multi line comment (/* */)
        Pattern p = Pattern.compile(match,Pattern.DOTALL | Pattern.MULTILINE);
        Scanner reader = new Scanner(new File(path)).useDelimiter(p);

        StringBuilder s = new StringBuilder(16384);
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

            //Parse Pages
            parsePages(pages);

            //Parse Links
            parseLinks(links);

            
        }catch(IOException e){
            System.err.println("Error opening up file!");
            e.printStackTrace();
        }
    }

    public void parseLinks(JSONObject links){
        Iterator<String> keys = links.keys();
        while(keys.hasNext()){//Loop thru all pages objs
            String objName = (String)keys.next();
            JSONObject obj = links.getJSONObject(objName);
            assertTrue(obj.has("text"),"Link "+objName+" missing 'text'");
            String text = obj.getString("text");
            assertTrue(obj.has("destinationPage"),"Link "+objName+" missing 'destinationPage'");
            String destPg = obj.getString("destinationPage");
            //Add the info to the data obj
            assertTrue(dataObjsMap.containsKey(objName),"No Data Object found by name: "+
                    objName+", declared in links section\n");
            DataObj d = dataObjsMap.get(objName);
            d.addLink(text,destPg);
        }
    }
	
    public void parsePages(JSONObject pages){
        assertTrue(pages.has("Home"),"Must contain a page named 'Home'");
        Iterator<String> keys = pages.keys();
        while(keys.hasNext()){//Loop thruy all pages objs
            String objName = (String)keys.next();

            PageObj pageObj = new PageObj(objName);
            pageObjMap.put(objName,pageObj);

            JSONObject oneObj = pages.getJSONObject(objName);

            //Parse Types
            Iterator<String> fieldIt = oneObj.keys();
            ArrayList<String> params = new ArrayList<String>();
            ArrayList<String> show = new ArrayList<String>();
            while(fieldIt.hasNext()){
                String typeName = (String)fieldIt.next();
                JSONObject oneSection = oneObj.getJSONObject(typeName);
                assertTrue(oneSection.has("params"),"Page "+objName+", Section "+typeName+" missing 'params'");
                JSONArray paramsJson = oneSection.getJSONArray("params");
                assertTrue(oneSection.has("show"),"Page "+objName+", Section "+typeName+" missing 'show'");
                JSONArray showJson = oneSection.getJSONArray("show");
                //Add them to the ALs
                for(int i=0; i<paramsJson.length(); i++){
                    params.add(paramsJson.getString(i));
                }
                for(int i=0; i<showJson.length(); i++){
                    show.add(showJson.getString(i));
                }
                pageObj.addSection(typeName,params,show);
            }
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

        //3 Confirm no name overlap between Fields and Relations
        it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            String name = (String)one.getKey();
            DataObj data = (DataObj)one.getValue();
            List<Field> fields = data.getFields();
            for(Field f : fields){
                assertTrue(!relationsMap.containsKey(f.getName()),
                        "Data Object "+name+" cannot contain a Relation and a Fields by the same name"
                        );
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
            if(oneObj.has("sortBy")){
                dataObj.setSortBy(oneObj.getString("sortBy"));
            }
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
