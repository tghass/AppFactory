package com.cse379.appsquared;

import java.io.*;
import java.util.*;

//Generates API following CRUD operations

public class ServiceGenerator {

    //////////
    //Fields//
    //////////



    ////////////////
    //Constructors//
    ////////////////
    /** Constructor for ServiceGenerator */
    public ServiceGenerator(){
	
    }

    ///////////
    //Methods//
    ///////////
	public String declareService(String name) {
		//Declare variable
		String baseUrl = "var "+name+"Service = function(){\n\n"+
							"    var baseUrl = 'http://localhost:3000/';\n";
		return baseUrl;	
	}
	
    public String genGetById(String name){

		//Get by ID
		return "    this.findById = function(id){\n"+
                        "        var deferred = $.Deferred();\n"+
                        "        var url = baseUrl+'"+name+"/find/'+ id;\n"+
                        "        $.ajax({\n"+
                        "            url: url,\n"+
                        "            success: function(data) {\n"+
                        "                $.each(data, function(key, val) {\n"+
                        "                    deferred.resolve(val);\n"+
                        "                });\n"+
                        "            },\n"+
                        "            dataType: 'jsonp',\n"+
                        "            error: function(xhr, status, error) {\n"+
                        "                console.log( xhr.responseText);\n"+
                        "                deferred.reject(\"Transaction Error: \");\n"+
                        "            }\n"+
                        "        }); \n"+
                        "        return deferred.promise();\n"+
                        "    }\n";
    }
	public String genGetByField(String name, String field) {
		return "    this.findBy"+field+" = function(field){\n"+
                        "        var deferred = $.Deferred();\n"+
                        "        var url = baseUrl+'"+name+"/find/"+field+"/'+field;\n"+
                        "        $.ajax({\n"+
                        "            url: url,\n"+
                        "            success: function(data) {\n"+
                        "                $.each(data, function(key, val) {\n"+
                        "                    deferred.resolve(val);\n"+
                        "                });\n"+
                        "            },\n"+
                        "            dataType: 'jsonp',\n"+
                        "            error: function(xhr, status, error) {\n"+
                        "                console.log( xhr.responseText);\n"+
                        "                deferred.reject(\"Transaction Error: \");\n"+
                        "            }\n"+
                        "        }); \n"+
                        "        return deferred.promise();\n"+
                        "    }\n";
	}
	
	public String genAdd(String name) {
		return "//POST REQUESTS \n"+
			    "this.add"+name+" = function(data){\n"+
                        "        var deferred = $.Deferred();\n"+
                        "        var url = baseUrl+'"+name+"/add';\n"+
                        "        var data = JSON.stringify(data);\n"+
                        "        $.ajax({\n"+
                        "            url: url,\n"+
                        "            type: 'post',\n"+
                        "            crossDomain: true,\n"+
                        "            dataType: 'json',\n"+
                        "            contentType: 'application/json; charset=UTF-8',\n"+
                        "            data: data,\n"+
                        "            success: function(res) {\n"+
                        "                console.log('Add successful');\n"+
                        "                deferred.resolve(true);\n"+
                        "            },\n"+
                        "            error: function(xhr, status, error) {\n"+
                        "                console.log( xhr.responseText);\n"+
                        "                deferred.resolve(false);\n"+
                        "            }\n"+
                        "        }); \n"+
                        "        return deferred.promise();\n"+
                        "    }\n";
	}
	
	public String genUpdate(String name) {
		return "//UPDATE REQUESTS \n"+
			    "this.update"+name+" = function(id,data){\n"+
                        "        var deferred = $.Deferred();\n"+
                        "        var url = baseUrl+'"+name+"/update/'+id;\n"+
                        "        var data = JSON.stringify(data);\n"+
                        "        $.ajax({\n"+
                        "            url: url,\n"+
                        "            type: 'post',\n"+
                        "            crossDomain: true,\n"+
                        "            dataType: 'json',\n"+
                        "            contentType: 'application/json; charset=UTF-8',\n"+
                        "            data: data,\n"+
                        "            success: function(res) {\n"+
                        "                console.log('Update successful');\n"+
                        "                deferred.resolve(true);\n"+
                        "            },\n"+
                        "            error: function(xhr, status, error) {\n"+
                        "                console.log( xhr.responseText);\n"+
                        "                deferred.resolve(false);\n"+
                        "            }\n"+
                        "        }); \n"+
                        "        return deferred.promise();\n"+
                        "    }\n";	
	}
	
		public String genDelete(String name) {
			return "//DELETE REQUESTS \n"+
			    "this.del"+name+" = function(id){\n"+
                        "        var deferred = $.Deferred();\n"+
                        "        var url = baseUrl+'"+name+"/delete/'+id;\n"+
                        "        var data = JSON.stringify(data);\n"+
                        "        $.ajax({\n"+
                        "            url: url,\n"+
                        "            dataType: 'jsonp',\n"+
                        "           success: function(data) {\n"+
                        "                console.log('Delete successful');\n"+
                        "                deferred.resolve(true);\n"+
                        "            },\n"+
                        "            error: function(xhr, status, error) {\n"+
                        "                console.log( xhr.responseText);\n"+
                        "                deferred.resolve(false);\n"+
                        "            }\n"+
                        "        }); \n"+
                        "        return deferred.promise();\n"+
                        "    }\n";	
	}
	

}
