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
    private static final String jsFolder = "js/";
    private static final String templateFolder = "templates/";
    private static final String appJs = jsFolder+"app.js";
    
    private File outputDir;

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for CordovaGenerator */
    public CordovaGenerator(File out){
        outputDir=out;
        outputDir.mkdir();//Make the directory
        //Clean the dir the copy over the reference directory
        try{
            FileUtils.cleanDirectory(outputDir);
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
        createViewFiles(pageObjMap);
        createTemplates(pageObjMap);
        createIndexHtml(pageObjMap);
    }
    private void createIndexHtml(HashMap<String,PageObj> pageObjMap){
        try{
            File index = new File(outputDir,"index.html");
            PrintWriter indexWriter = new PrintWriter(
                    new BufferedWriter( new FileWriter(index))
                    );
            //Write beginning and head
            indexWriter.write("<!DOCTYPE html>\n"+
                              "<html>\n"+
                              "<head>\n"+
                              "\n"+
                              "    <meta charset=\"utf-8\">\n"+
                              "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no, minimum-scale=1.0, maximum-scale=1.0\">\n"+
                              "    <link href=\"assets/ratchet/css/ratchet.css\" rel=\"stylesheet\">\n"+
                              "</head>\n\n");
            //Write body
            indexWriter.write("<body>\n"+
                              "\n"+
                              "    <script type=\"text/javascript\" src=\"cordova.js\"></script>\n"+
                              "	   <script src=\"lib/hello.js\"></script>\n"+
                              "    <script src=\"lib/jquery.js\"></script>\n"+
                              "    <script src=\"lib/router.js\"></script>\n"+
                              "    <script src=\"lib/ejs.js\"></script>\n\n");
            //TODO, include services
            //Include Views
            Iterator it = pageObjMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry one = (Map.Entry)it.next();
                String name = (String)one.getKey();
                indexWriter.write("    <script src=\""+jsFolder+name+"View.js\"></script>\n");
            }
            //Include app.js
            indexWriter.write("    <script src=\""+appJs+"\"></script>\n");
            //End body and html
            indexWriter.write("</body>\n</html>");

            indexWriter.close();
        }catch(IOException e){
            System.out.println("\nCan't write Cordova code to file index.html");
        }
    }
    private void createTemplates(HashMap<String,PageObj> pageObjMap){
        Iterator it = pageObjMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            String name = (String)one.getKey();
            PageObj data = (PageObj)one.getValue();
            File thisTemplate = new File(outputDir,templateFolder+name+".ejs");
            try{
                PrintWriter templateWriter = new PrintWriter(
                        new BufferedWriter( new FileWriter(thisTemplate))
                        );
                //Header
                templateWriter.write("<header class=\"bar bar-nav\">\n"+
                                     "    <h1 class=\"title\">"+name+"</h1>\n"+
                                     "</header>\n");
                //Body
                templateWriter.write("<div id=\"maincontent\" class=\"content\">\n"+
                                     "</div>");

                templateWriter.close();
            }catch(IOException e){
                System.out.println("\nCan't write Cordova code to file "+templateFolder+name+".ejs");
            }
        }

    }
    private void createViewFiles(HashMap<String,PageObj> pageObjMap){
        Iterator it = pageObjMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            String name = (String)one.getKey();
            PageObj data = (PageObj)one.getValue();
            File thisView = new File(outputDir,jsFolder+name+"View.js");
            try{
                PrintWriter viewWriter = new PrintWriter(
                        new BufferedWriter( new FileWriter(thisView))
                        );
                
                //Obj decl
                viewWriter.write("var "+name+"View = function("/*TODO add params */+"){\n");
                //Template func
                viewWriter.write("    this.template = function(){\n"+
                                 "        return new EJS({url:'templates/"+name+"'}).render({"+/*TODO add params */"});\n"+
                                 "    }\n\n");
                //Render func
                viewWriter.write("    this.render = function(){\n"+
                                 "        this.$el.html(this.template());\n"+
                                 "        return this;\n"+
                                 "    }\n\n");
                //Initialize func
                viewWriter.write("    this.initialize = function(){\n"+
                                 "        this.$el = $('<div/>');\n"+
                                 /* TODO add necessary code to get needed data */
                                 "    }\n\n");
                //Call initialize
                viewWriter.write("    this.initialize();\n");
                //End bracket
                viewWriter.write("}");

                viewWriter.close();
            }catch(IOException e){
                System.out.println("\nCan't write Cordova code to file "+jsFolder+name+"View.js");
            }
        }
    }
    private void createAppJs(HashMap<String,PageObj> pageObjMap){
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
                                "    router.addRoute('"+(name.equals("Home") ? "" : name)+"', function(){\n"+
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
            System.out.println("\nCan't write Cordova code to file "+appJs);
        }
    }
}
