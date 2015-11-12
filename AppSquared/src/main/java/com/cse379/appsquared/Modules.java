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
	public static boolean isModule(String moduleName) {
		switch (moduleName) {
			case "LoggedInUser":
				return true;
		}
		return false;
	}
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