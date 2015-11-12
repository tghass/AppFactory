package com.cse379.appsquared;

import java.io.*;
import java.util.*;

public class Modules {

    //////////
    //Fields//
    //////////
    private String name;


    ////////////////
    //Constructors//
    ////////////////
    /** Constructor for Modules */
    public Modules(String name){
        this.name=name;
    }
	public Modules(){
    }
	

    ///////////
    //Methods//
    ///////////
	
	//TODO make modules enums
	
	//Determines whether a String is a module 
	public static boolean isModule(String moduleName) {
		switch (moduleName) {
			case "LoggedInUser":
				return true;
		}
		return false;
	}
	
	//From a module, returns the table that it refers to
	public static String modNameToTableName(String moduleName) {
		String relatedTable= "";
		switch (moduleName) {
			case "LoggedInUser":
				relatedTable = "User";
				break;
		}
		return relatedTable;
	}
}