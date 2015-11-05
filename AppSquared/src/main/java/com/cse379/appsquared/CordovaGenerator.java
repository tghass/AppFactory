package com.cse379.appsquared;

import java.io.*;
import org.apache.commons.io.FileUtils;
import java.util.*;

public class CordovaGenerator{
    //TODO:
    // - Should use Maven resources for ref_dir and load from compiles JAR

    //////////
    //Fields//
    //////////
    private static final File REF_DIR = new File("resources/www");
    private static final String appJs = "js/app.js";
    
    private File outputDir;

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for CordovaGenerator */
    public CordovaGenerator(File out){
        outputDir=out;
        outputDir.mkdir();//Make the directory
        //Copy over the reference directory
        try{
            FileUtils.copyDirectory(REF_DIR,outputDir);
        }catch (IOException e){
            System.out.println("Error copying over reference dir for cordova");
        }
    }

    ///////////
    //Methods//
    ///////////
    public void createCode(HashMap<String,DataObj> dataObjsMap, 
            HashMap<String,PageObj> pageObjMap){
        createAppJs(pageObjMap);
    }
    public void createAppJs(HashMap<String,PageObj> pageObjMap){
        File appJsFile = new File(outputDir,appJs);
        try{
            PrintWriter appWriter = new PrintWriter(
                    new BufferedWriter( new FileWriter(appJsFile))
                    );
            //OAuth login handler
            appWriter.write("function loginHandler(r){\n"+
                            "    console.log(r);\n"+
                            "    hello(r.network).api('me').then(function(me){\n"+
                            "        console.log(me);\n"+
                            "    });\n"+
                            "}\n");
            //Begin immediate func
            appWriter.write("// We use an 'Immediate Function' to initialize the application to avoid leaving anything behind in the global scope\n"+
                            "(function () {\n"+
                            "\n"+
                            "    // Initate the library\n"+
                            "    hello.init({\n"+
                            //TODO: Parse Oauth tokens from a config file
                            "        google : '164737927993-l34g84brkg96ufe30ve2mjpg6lcen2pg.apps.googleusercontent.com',\n"+
                            "        facebook : '160981280706879',\n"+
                            "        windows : '00000000400D8578'\n"+
                            "    }, {\n"+
                            "        // Define the OAuth2 return URL\n"+
                            "        redirect_uri : 'http://localhost:8080/index.html'//Must remove port to work in cordova\n"+
                            "    });\n");
            //TODO: Initializeall services
            //Add routes
            Iterator it = pageObjMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry one = (Map.Entry)it.next();
                String name = (String)one.getKey();
                PageObj data = (PageObj)one.getValue();
                //Write out this route
                appWriter.write("    \n"+
                                "    router.addRoute('"+(name.equals("Home") ? "" : name)+"' function(){\n"+
                                    //TODO add parameter handling
                                "        $('body').html(new "+name+"View("/*TODO, add params*/+").render().$el);\n"+
                                "    });\n");
            }
            appWriter.write("    \n"+
                            "    router.start();\n"+
                            "    \n"+
                            "}());\n");
            appWriter.close();
        }catch(IOException e){
            System.out.println("\nCan't write API code to file "+appJs);
        }
    }
}
