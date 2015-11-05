package com.cse379.appsquared;

import java.io.*;
public class CordovaGenerator{

    //////////
    //Fields//
    //////////
    public static final String REF_DIR = "Reference/www";
    
    private File outputDir;

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for CordovaGenerator */
    public CordovaGenerator(File out){
        outputDir=out;
        outputDir.mkdir();//Make the directory
        //Copy over the reference directory
    }

    ///////////
    //Methods//
    ///////////
}
