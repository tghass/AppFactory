package com.cse379.appsquared;

import java.io.*;
import org.apache.commons.io.FileUtils;
public class CordovaGenerator{

    //////////
    //Fields//
    //////////
    public static final File REF_DIR = new File("resources/www");
    
    private File outputDir;

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for CordovaGenerator */
    public CordovaGenerator(File out){
        System.out.println("HERE!!!");
        outputDir=out;
        outputDir.mkdir();//Make the directory
        //Copy over the reference directory
        try{
            System.out.println(REF_DIR.exists());
            FileUtils.copyDirectory(REF_DIR,outputDir);
        }catch (IOException e){
            System.out.println("Error copying over reference dir for cordova");
        }
    }

    ///////////
    //Methods//
    ///////////
}
