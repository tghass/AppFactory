package com.cse379.appsquared;

import java.util.*;
import java.io.*;

//Main class to generate the app
//First, parses the config file file
public class GenerateApp {
    private final static String outputFolder = "Output";
    private final static String sqlFileName = outputFolder+"/sqlDump.sql";
    private final static String serverFileName = outputFolder+"/app.js";
    private final static String cordovaFolderName = outputFolder+"/www";

	public static void main(String [] args) {
        System.out.println("-------------------");
		String filename = args[0];
        System.out.print("Parsing config file: "+filename+"...");
		Parser parser = new Parser();
		//Parse file: convert JavaScript objects to Java objects (DataObjects class) 
		parser.parseFile(filename); 
        System.out.println(" done");
        
        //Create the Output folder
        File outputDir = new File(outputFolder);
        if(!outputDir.isDirectory())
            outputDir.mkdir();
		

        //Generate SQL
        try{
            System.out.print("Generating SQL ("+sqlFileName+")...");
            PrintWriter sql = new PrintWriter(
                    new BufferedWriter( new FileWriter(sqlFileName))
                    );
            SqlGenerator sqlGen = new SqlGenerator(sql);
            sqlGen.toSql(parser.getDataObjsMap()); //generate sqldump file
            sql.close();
            System.out.println(" done");
        }catch(IOException e){
            System.out.println("\nCan't write SQL code to file "+sqlFileName);
        }


        //Generate Server File for API
        try{
            System.out.print("Generating API ("+serverFileName+") code...");
            PrintWriter api = new PrintWriter(
                    new BufferedWriter( new FileWriter(serverFileName))
                    );
            ApiCreator apiGen = new ApiCreator(api);
            apiGen.createServerFile(parser.getDataObjsMap()); //generate sqldump file
            api.close();
            System.out.println(" done");
        }catch(IOException e){
            System.out.println("\nCan't write API code to file "+sqlFileName);
        }


        //Generate Server File for API
        System.out.print("Generating Cordova ("+cordovaFolderName+") code...");
        File cordovaDir = new File(cordovaFolderName);
        CordovaGenerator cordGen = new CordovaGenerator(cordovaDir);
        cordGen.createCode(parser.getDataObjsMap(),parser.getPageObjsMap());
        System.out.println(" done");
	}

}
