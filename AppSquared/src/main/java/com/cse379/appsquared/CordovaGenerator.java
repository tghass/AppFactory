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
    private static final String serviceFolder = jsFolder+"services/";
    
    private File outputDir;

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for CordovaGenerator */
    public CordovaGenerator(File out){
        outputDir=out;
        if (!outputDir.isDirectory()) {
			outputDir.mkdir();//Make the directory
		}
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
		
        createAppJs(pageObjMap, dataObjsMap);
        createViewFiles(pageObjMap);
        createTemplates(pageObjMap,dataObjsMap);
        createIndexHtml(pageObjMap,dataObjsMap);
        createServices(pageObjMap, dataObjsMap);
    }
    /* Create a service file for every Data Object*/
    private void createServices(HashMap<String, PageObj> pageObjsMap, 
		HashMap<String,DataObj> dataObjsMap){
		
        Iterator it = dataObjsMap.entrySet().iterator();
        ServiceGenerator servGen = new ServiceGenerator();
        	
			
		while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            String name = (String)one.getKey();
            DataObj data = (DataObj)one.getValue();
            try{
				PrintWriter serviceWriter = new PrintWriter(
                        new BufferedWriter( new FileWriter(new File(outputDir,serviceFolder+name+"Service.js")))
                        );	
				serviceWriter.write(servGen.declareService(name));
				serviceWriter.write(servGen.genGetById(name));
                serviceWriter.write(servGen.genAdd(name));
                serviceWriter.write(servGen.genUpdate(name));
                serviceWriter.write(servGen.genDelete(name));
				
				//Look for all instances of a page's SHOW and find by its params
				Iterator pageIt = pageObjsMap.entrySet().iterator();
				while (pageIt.hasNext()) {
					Map.Entry pOne = (Map.Entry)pageIt.next();
					PageObj curPageObj = (PageObj)pOne.getValue();
					List<Section> sections = curPageObj.getSections();
					for (Section section : sections) {
						if (section.getType() == Section.Type.VIEW && section.getShow().contains(name)) {
							serviceWriter.write(servGen.genGetByField(name,section.getParams()));
						}
					}
				}
                //Hack to get OAuth stuff working
                serviceWriter.write(servGen.genGetByField("User",new ArrayList<String>(Arrays.asList(new String[] {"OAuthID"}))));

				//Close file
				serviceWriter.write("}");
				serviceWriter.close();
               
            }catch(IOException e){
                System.out.println("\nCan't write Cordova code to file "+serviceFolder+name+"Service.js");
            }
        }
		
		
			
			
    }
	
    private void createIndexHtml(HashMap<String,PageObj> pageObjMap,HashMap<String,DataObj> dataObjsMap){
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
                              "    <script src=\"lib/hello.js\"></script>\n"+
                              "    <script src=\"lib/jquery.js\"></script>\n"+
                              "    <script src=\"lib/router.js\"></script>\n"+
                              "    <script src=\"lib/ejs.js\"></script>\n\n"+
                              "    <div id='container'></div>\n");
            //Include Views
            Iterator it = pageObjMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry one = (Map.Entry)it.next();
                String name = (String)one.getKey();
                indexWriter.write("    <script src=\""+jsFolder+name+"View.js\"></script>\n");
            }
            indexWriter.write("\n");
            //Include services
            it = dataObjsMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry one = (Map.Entry)it.next();
                String name = (String)one.getKey();
                indexWriter.write("    <script src=\""+serviceFolder+name+"Service.js\"></script>\n");
            }
            //Inlude login capabilities
            indexWriter.write("    <script src=\"js/Login.js\"></script>\n");
            //Include app.js
            indexWriter.write("    <script src=\""+appJs+"\"></script>\n");
            //End body and html
            indexWriter.write("</body>\n</html>");

            indexWriter.close();
        }catch(IOException e){
            System.out.println("\nCan't write Cordova code to file index.html");
        }
    }
    private void createTemplates(HashMap<String,PageObj> pageObjMap,HashMap<String,DataObj> dataObjsMap){
        Iterator it = pageObjMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            String name = (String)one.getKey();
            PageObj page = (PageObj)one.getValue();
            File thisTemplate = new File(outputDir,templateFolder+name+".ejs");
            try{
                PrintWriter templateWriter = new PrintWriter(
                        new BufferedWriter( new FileWriter(thisTemplate))
                        );
                //Header
                templateWriter.write("<header class=\"bar bar-nav\">\n"+
                                     "    <a id=\"login\" class=\"icon pull-right\" href=\"#\">Log In</a>\n"+
                                     "    <h1 class=\"title\">"+name+"</h1>\n"+
                                     "</header>\n");
                //Body
                templateWriter.write("<div id=\"maincontent\" class=\"content\">\n");
                //Just output params for now
                for(String param : page.getShow(Section.Type.VIEW)){
                    List<String> display = dataObjsMap.get(param).getDisplay();
                    templateWriter.write("    <p><%= JSON.stringify(VIEW."+param+")%></p>\n");
                    for(String di : display){
                        if(di.startsWith("@"))
                            templateWriter.write("    <p>"+di.substring(1)+"</p>\n");
                        else{
                            //if(dataObjsMap.contains(di)){//We are displaying a 
                            templateWriter.write("    <p><%= VIEW."+param+"."+di+"%></p>\n");
                        }
                    }
                    templateWriter.write("    <br>\n\n");
                }
                for(String param : page.getShow(Section.Type.CREATE)){
                    templateWriter.write("    <p> Create -> "+param+"</p>\n");
                }
                for(String param : page.getShow(Section.Type.MODIFY)){
                    templateWriter.write("    <p><%= JSON.stringify(MODIFY."+param+")%></p>\n");
                }
                for(String param : page.getShow(Section.Type.DELETE)){
                    templateWriter.write("    <p><%= JSON.stringify(DELETE."+param+")%></p>\n");
                }
                templateWriter.write("</div>");

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
            PageObj page = (PageObj)one.getValue();
            File thisView = new File(outputDir,jsFolder+name+"View.js");
            try{
                PrintWriter viewWriter = new PrintWriter(
                        new BufferedWriter( new FileWriter(thisView))
                        );
                
                //Obj decl
                viewWriter.write("var "+name+"View = function(");
                viewWriter.write(page.getParamsString()+"){\n");
                //Template func
                viewWriter.write("    this.template = function(){\n"+
                                 "        return new EJS({url:'templates/"+name+"'}).render(");
                StringBuilder params = new StringBuilder(64);
                for(String param : page.getParams()){
                    params.append("{VIEW"+":"+param+".VIEW}, ");
                }
                int indexOfLastComma = params.lastIndexOf(",");
                if(indexOfLastComma>=0)
                    params.setCharAt(indexOfLastComma,' ');//Remove last ,
                viewWriter.write(params.toString()+");\n");
                viewWriter.write("    }\n\n");
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
    private void createAppJs(HashMap<String,PageObj> pageObjMap, HashMap<String, DataObj> dataObjsMap){
        File appJsFile = new File(outputDir,appJs);
        try{
            PrintWriter appWriter = new PrintWriter(
                    new BufferedWriter( new FileWriter(appJsFile))
                    );
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
							
            //Initialize all services
			Iterator it = dataObjsMap.entrySet().iterator();
			while(it.hasNext()){
				Map.Entry one = (Map.Entry)it.next();
				String name = (String)one.getKey();
				DataObj data = (DataObj)one.getValue();
				appWriter.write("    var service"+name+"= new "+name+"Service();\n");
			}
			
            //Add routes
            it = pageObjMap.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry one = (Map.Entry)it.next();
                String pageName = (String)one.getKey();
                PageObj page = (PageObj)one.getValue();
                //Write out this route
                appWriter.write("    \n"+
                                "    router.addRoute('");
                StringBuilder params = new StringBuilder(64);//Add params to url
                params.append((pageName.equals("Home") ? "" : pageName));
				

                for(String param : page.getParams()){
                    if(param.equals("LoggedInUser"))
                        continue;
                    params.append("/"+param+"/:"+param);
                }
                appWriter.write(params.toString());
                appWriter.write("', function(");
                params = new StringBuilder(64);//Do the params
                for(String param : page.getParams()){
                    if(param.equals("LoggedInUser"))
                        continue;
                    params.append(param+",");
                }
                int indexOfLastComma = params.lastIndexOf(",");
                if(indexOfLastComma>=0)
                    params.deleteCharAt(indexOfLastComma);//Remove last ,
                appWriter.write(params.toString());
                appWriter.write("){\n");
			
				int numQueries = 0;
				
				//Determine which service has the correct function	
				//for each Show object
				for (Section s: page.getSections()) {
					if ( s.getType() == Section.Type.VIEW) {
						for (String serviceName: s.getShow()) {
							// Call the service function that queries the database for data obj based on param
							if (page.getParams().size() > 0) { 
                                appWriter.write("        service"+serviceName+"."); 
                            }
							for(String param : page.getParams()){
								appWriter.write("findBy"+param+"("+param+").done(function("+serviceName+"Response){ \n");
								numQueries++;
							}
						}
					}
				}
				
				// need to use appended text so that incoming query variable
				// does not get confused with query response
				appWriter.write("            $('#container').html(new "+pageName+"View("+page.getShowString("Response")+
                                ").render().$el);\n"+
                                "        setLoginButton();\n");

								
				//only add closing brackets for the params that we are querying against	
				for (int i = 0; i < numQueries; i++) {
					appWriter.write("        });\n");
				}
				appWriter.write("        });\n");
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
