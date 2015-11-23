package com.cse379.appsquared;

import java.io.*;
import java.util.*;
import org.apache.commons.io.FileUtils;

//Generates API following CRUD operations

public class ApiCreator {

    //////////
    //Fields//
    //////////
    private static final File sourceConfig = new File("resources/config.js");
    public static int tabDepth = 0;
    public static int queryNo = 0;
    public static int queryNoNext = 1;
	public int LoggedInUser = 1;
    private PrintWriter out;


    ////////////////
    //Constructors//
    ////////////////
    /** Constructor for ApiCreator */
    public ApiCreator(PrintWriter out){
        this.out=out;
    }

    ///////////
    //Methods//
    ///////////
    public void createServerFile(HashMap<String,DataObj> dataObjsMap, HashMap<String, PageObj> pageObjsMap){
        //Copy files over
        try{
            FileUtils.copyFile(sourceConfig,new File("Output/config.js"));
        }catch (IOException e){
			System.out.println("Error copying over config file for");
            System.out.println(e);
        }
        out.write(genDbConnection());
        //For each data object, get object by ID
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
			DataObj curDataObj = (DataObj)one.getValue();
			String name = curDataObj.getName();
			out.write("\n /* " + name + ": CRUD GET, DELETE, UPDATE, POST BY ID*/\n");
            out.write(genGetById(curDataObj));
			out.write(genDelById(curDataObj));
			out.write(genUpdateById(curDataObj));
			out.write(genAdd(curDataObj));
        }
        //Hack to get a necessary API call for OAuth stuff
        out.write(genGetByPageParam(null,new Section("View",
                        new ArrayList<String>(Arrays.asList(new String[] {"OAuthID"})),
                        new ArrayList<String>(Arrays.asList(new String[] {"User"}))),
                    dataObjsMap));
		
		//For each page, generate the calls necessery to make
		//the view/create params work
		it = pageObjsMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry one = (Map.Entry)it.next();
			PageObj curPageObj = (PageObj)one.getValue();
			String name = curPageObj.getName();
			out.write("\n /* " + name + ": CRUD GET,DELETE,UPDATE,POST *NOT* BY ID */\n");
			List<Section> sections = curPageObj.getSections();
			for (Section section : sections) {
				switch((Section.Type)section.getType()) {
					case VIEW:
						out.write(genGetByPageParam(curPageObj, section, dataObjsMap));
						break;
					case CREATE:
						//out.write(genPostByPageParam(curPageObj, section));
						break;
					case MODIFY:
						break;
					case DELETE:
						break;
				}		
			}
		}
		out.write(genServerListen());
    }
	
	//From a list of params, this method generates the where clause of the SQL
	//statement. ie) select * from table WHERE ____
	public String parsePageParam(DataObj showObj, List<String> getParams) {
		StringBuilder queryParam = new StringBuilder(512);
		boolean firstIteration = true;
		Modules m = new Modules();
		for (String param : getParams) {
			if (firstIteration) {
				firstIteration = false;
			}
			else {
				queryParam.append(" and ");
			}
			List<Integer> fkIndices = showObj.indexForForeignKeysOfType(param);
			//check if param is a module
			if (m.isModule(param)) {
				String tableName = m.modNameToTableName(param);
				fkIndices = showObj.indexForForeignKeysOfType(tableName);
				if (fkIndices.size()>0) {
					boolean firstIndex = true;
					for (int fkIndex : fkIndices) {
						if (firstIndex) { firstIndex = false; }
						else { queryParam.append(" or "); }
						String foreignKeyField = showObj.getFieldName(fkIndex);
						queryParam.append(showObj.getName() + "."+foreignKeyField + " = ? ");
					}
				}
				//could not find the foreign key because it isthis object
				else {
                    queryParam.append(showObj.getName() + ".ID = ? ");
				}
					
			}
			//else check if param is a field of dataobj
			else if (showObj.isField(param)) {
				queryParam.append(showObj.getName() + "." + param +" = ? ");
			}
			//else check if param is a foreign key dependency of dataobj
			else if (fkIndices.size() > 0) {
				boolean firstIndex = true;			
				for (int fkIndex : fkIndices) {
					if (firstIndex) { firstIndex = false; }
					else { queryParam.append(" or "); }
					String foreignKeyField = showObj.getFieldName(fkIndex);
					queryParam.append(showObj.getName() + "."+foreignKeyField + " = ? ");
				}
			
			}
			//check if param is the same name as that object.
			//TODO: in this case, it's get by id which has already been
			//generated. either skip this generation or keep it
			//for later to make communication with cordova easier
			else if (showObj.getName().equals(param)) {
				queryParam.append(showObj.getName() + ".ID = ? ");
			}
			// else there is no match. really, this is an error in the config.
			// TODO: have parser check that one of these three conditions is true
			// now, handle it such that set select * from x where true
			else {
				queryParam.append(" true ");
			}
		}
		return queryParam.toString();
	}
	
	//makes requests params
	// ie con.query('SELECT * ... WHERE = ?', [requst params], fcn())
	public String makeReqParams(DataObj showObj, List<String> pageParams) { 
		StringBuilder reqParams = new StringBuilder(128);
		reqParams.append("[");
		boolean firstIteration = true;
		Modules m = new Modules();
		for (String param : pageParams) {
			if (firstIteration) { firstIteration = false; }
			else { reqParams.append(","); }
			
			//check if param is a module
			// see how many fkeys this param stands for
			List<Integer> fkIndices;
			if (m.isModule(param)) {
				String tableName = m.modNameToTableName(param);
				fkIndices = showObj.indexForForeignKeysOfType(tableName);
			}
			else {
				fkIndices = showObj.indexForForeignKeysOfType(param);
			}
			//for every fk, add another req param
			//because each fk will have a ? in the where clause
			if (fkIndices.size()>0) {
				boolean firstIndex = true;
				for (int fkIndex : fkIndices) {
					if (firstIndex) { firstIndex = false; }
					else { reqParams.append(" , "); }
					reqParams.append("req.params." + param);
				}
			}
			else {
				reqParams.append("req.params." + param);
			}
		}
		reqParams.append("]");
		return reqParams.toString();
	}
	
	//Generates the get request for items that the config 'page' section
	//requests to be shown given a certain param
	public String genGetByPageParam(PageObj pageObj, Section section, HashMap<String,DataObj> dataObjsMap) {
		StringBuilder s = new StringBuilder(1024);
		List<String> showObjs = section.getShow();
		List<String> pageParams = section.getParams();
		StringBuilder paramQuery = new StringBuilder(256);
		
		//create the end of the url
		for (String param : pageParams) {
			paramQuery.append("/find/"+param+"/:" + param);
		}
		
		for (String tableName : showObjs) {
			DataObj dataObj = dataObjsMap.get(tableName);
			String whereParams = parsePageParam(dataObj, pageParams);
			String reqParams = makeReqParams(dataObj, pageParams);
			s.append("app.get('/" + tableName + paramQuery.toString() +"', function(req,res) {\n");
			
			//know when to return response
			s.append(returnTab(1) + "totalCount = 0;\n");
			s.append(returnTab(1) + "count = 0;\n");
			
			//generates the BASIC select * from table where id = ?
			s.append(returnTab(1) + "var query = \""+ selectProperties(dataObj) + 
					" from " + tableName + " as " +tableName+ " where " + whereParams + "\";\n");
			tabDepth = 0;
			queryNo = 0;
			queryNoNext = 1;
			String fks = evaluateFK(dataObj, 0, "", "", "row.ID");
			s.append(fks);
			if (fks.length() > 0) {
				s.append(returnTab(tabDepth+2) + "count += 1;\n");
			}
			s.append(returnTab(1) + "con.query(query,"+ reqParams+", function(err, rows0,fields) {\n");
			s.append(returnTab(2) +"if(err) throw err;\n");

			//once the BASIC info is returned, each foreign key needs to be 
			//expanded. this is done recursively with evaluateFK()
			
			s.append(returnTab(tabDepth+2) + "if (count == totalCount) {\n");
			s.append(returnTab(tabDepth+3) + "res.jsonp(rows0);\n");
			s.append(returnTab(tabDepth+2) + "}\n");
			
			//adds all of the closing brackets
			while (tabDepth +2> 0) {
				s.append(returnTab(1+tabDepth) + "});\n");
				 if (tabDepth == 1) {
				 		s.append(returnTab(1+tabDepth) + "if (rows0.length == 0) {\n");
						s.append(returnTab(2+tabDepth) + "res.jsonp([])\n");
						s.append(returnTab(1+tabDepth) + "}\n");
					}
				tabDepth -= 1;
			}
			
			s.append("\n");
			}
		return s.toString();
	}
	public String genPostByPageParam(PageObj pageObj, Section section) {
		return "";
	}
	public String genServerListen() {
		StringBuilder s = new StringBuilder(512);
		s.append("var server = app.listen(config.api_port, function() {\n");
		s.append("\tconsole.log('We have started our server"
				+ " on port '+config.api_port);\n");
		s.append("});\n");
		return s.toString();
	}
	
    public String genDbConnection() {
        StringBuilder s = new StringBuilder(1024);
        s.append("//Express is a minimal and flexible Node.js web " 
				+ "application framework that provides a robust set of features" 
				+ "for web and mobile applications\n");
        s.append("var express = require('express');\n");
        s.append("var app = express();\n\n");
        s.append("var config = require('./config.js')");
        s.append("\n//Establish connection to the MySQL database\n");
		SqlGenerator sqlGen = new SqlGenerator();
        s.append("var mysql = require('mysql');\n");
        s.append("var con = mysql.createConnection({\n");
        s.append("    host : config.db_host,\n");
        s.append("    user : config.db_user,\n");
        s.append("    password : config.db_password,\n");
        s.append("    database : config.db_database\n");
        s.append("});\n");

        //Connect to the database
        s.append("con.connect(function(err) {\n");
        s.append("\tif (err) {\n");
        s.append("\t\tconsole.log('Error connection to db');\n");
        s.append("\t\treturn;\n");
        s.append("\t}\n");
        s.append("\tconsole.log('Connection established.');\n");
        s.append("});\n");
        s.append("\n");
        //Get CORS POST working
        s.append("//Important to make POST work\n");
        s.append("// npm install express-cors \n");
        s.append("var cors = require('express-cors')\n");
        s.append(" \n");
        s.append("app.use(function(req,res, next) {\n");
        s.append("    res.header(\"Access-Control-Allow-Origin\", \"*\");\n");
        s.append("    res.header(\"Access-Control-Allow-Headers\", \"Origin, X-Requested-With, Content-Type, Accept\");\n");
        s.append("    res.header(\"Access-Control-Allow-Methods\", \"POST, GET, OPTIONS\");\n");
        s.append("    res.header(\"Access-Control-Max-Age\", \"1000\"); \n");
        s.append("    cors({\n");
        s.append("        allowedOrigins: [ 'localhost:8080' ] });\n");
        s.append("    next();\n");
        s.append("});\n");

		
		
        return s.toString();
    }
	//Generate an update by id call for a table.
	// TODO: right now, it updates all fields except id
	// this should be changed based off of what fields
	// the config file specifies can/should be updated
	public String genUpdateById(DataObj dataObj) {
		StringBuilder s = new StringBuilder(1024);
        String tableName = dataObj.getName();
        s.append("app.post('/" + tableName 
				 + "/update/:id', function(req,res,next) {\n");
		s.append(returnTab(1) + "var id = req.params.id;\n");
		s.append(returnTab(1) + "var " + tableName + " = '';\n");
		s.append(returnTab(1) + "req.on('data', function(data) {\n");
		s.append(returnTab(2) + tableName + " += data;\n");
		s.append(returnTab(1) + "});\n");
		s.append(returnTab(1) + "req.on('end', function() {\n");
		s.append(returnTab(2) + tableName + " = JSON.parse(" + tableName + ");\n");
		s.append(toJsonObj(dataObj));
		s.append(returnTab(2) + "con.query('UPDATE " + tableName
							  + " set ? WHERE id = ?', [data, id]," 
							  + "function(err,rows) {\n");
		s.append(returnTab(3) + "if (err) { console.log('Error updating'); }\n");
		s.append(returnTab(3) + "else {\n");
		s.append(returnTab(4) + "console.log('in update success');\n");
		s.append(returnTab(4) + "res.jsonp(true);\n");
		s.append(returnTab(3) + "}\n");
		s.append(returnTab(2) + "});\n");
		s.append(returnTab(1) + "});\n");
		s.append("});\n\n");
		return s.toString();
	}
	
		//Generate an add by id call for a table.
	public String genAdd(DataObj dataObj) {
		StringBuilder s = new StringBuilder(1024);
        String tableName = dataObj.getName();
        s.append("app.post('/" + tableName + "/add', function(req,res) {\n");
		s.append(returnTab(1) + "var " + tableName + " = '';\n");
		s.append(returnTab(1) + "req.on('data', function(data) {\n");
		s.append(returnTab(2) + tableName + " += data;\n");
		s.append(returnTab(1) + "});\n");
		s.append(returnTab(1) + "req.on('end', function() {\n");
		s.append(returnTab(2) + tableName + " = JSON.parse(" + tableName + ");\n");

		s.append(toJsonObj(dataObj));
		s.append(returnTab(2) + "con.query('INSERT INTO " + tableName 
				+ " set ? ', data, function(err,rows) {\n");
		s.append(returnTab(3) + "if (err) { console.log('Error inserting'); }\n");
		s.append(returnTab(3) + "else {\n");
		s.append(returnTab(4) + "console.log('in insert success');\n");
		s.append(returnTab(4) + "res.jsonp(true);\n");
		s.append(returnTab(3) + "}\n");
		s.append(returnTab(2) + "});\n");
		s.append(returnTab(1) + "});\n");
		s.append("});\n\n");
		return s.toString();
	}
	
	public String toJsonObj(DataObj d){
		StringBuilder s = new StringBuilder(1024);
        s.append(returnTab(2) + "var data = {\n");
	    String name = d.getName();
        boolean firstField = true;
        for (Field f: d.getFields()) {
			//update will not change the auto-created id
			if (f.getName().equals("ID")) { continue; }
            if (firstField) {
                firstField = false;
                s.append(returnTab(3) + f.getName() + " : " 
						+ name + "." + f.getName() +" ");
            }
            else {
                s.append(",\n" + returnTab(3) + f.getName() + " : " 
						+ name + "." + f.getName() +" ");
            }
        }
		s.append("\n" + returnTab(2) + "};\n");
        return s.toString();
    }
	
	//Generate a delete by id call for a table.
	public String genDelById(DataObj dataObj) {
		StringBuilder s = new StringBuilder(1024);
        String tableName = dataObj.getName();
        s.append("app.get('/" + tableName + "/delete/:id', function(req,res,next) {\n");
		s.append(returnTab(1) + "var id = req.params.id;\n");
		s.append(returnTab(1) + "con.query('DELETE FROM " + tableName +" WHERE id = ?', id, function(err,rows) {\n");
		s.append(returnTab(2) + "if (err) { console.log('Error deleting'); }\n");
		s.append(returnTab(2) + "else {\n");
		s.append(returnTab(3) + "console.log('in delete success');\n");
		s.append(returnTab(3) + "res.jsonp(rows);\n"); //TODO: return something else
		s.append(returnTab(2) + "}\n");
		s.append(returnTab(1) + "});\n");
		s.append("});\n\n");
		return s.toString();
	}
    
    //Generate a get by id call for every table.
    //For the tables with foreign keys, this calls function
    // genGetByForeignKey 
    public String genGetById(DataObj dataObj){
        StringBuilder s = new StringBuilder(1024);
        String tableName = dataObj.getName();
       
        s.append("app.get('/" + tableName + "/find/:id', function(req,res) {\n");
		
		//know when to return response
		s.append(returnTab(1) + "totalCount = 0;\n");
		s.append(returnTab(1) + "count = 0;\n");
		
		//generates the BASIC select * from table where id = ?
        s.append(returnTab(1) + "var query = \""+ selectProperties(dataObj) + 
				" from " + tableName + " as " +tableName+ " where id = ?\";\n");
        s.append(returnTab(1) + "con.query(query, req.params.id, function(err, rows0,fields) {\n");
        s.append(returnTab(2) +"if(err) throw err;\n");

		//once the BASIC info is returned, each foreign key needs to be 
		//expanded. this is done recursively with evaluateFK()
        
		tabDepth = 0;
		queryNo = 0;
		queryNoNext = 1;
		s.append(evaluateFK(dataObj,0, "", "", "req.params.id"));
        s.append(returnTab(tabDepth+2) + "res.jsonp(rows0);\n");
		
		//adds all of the closing brackets
        while (tabDepth +2> 0) {
            s.append(returnTab(1+tabDepth) + "});\n");
            tabDepth -= 1;
        }
		
        s.append("\n");
        return s.toString();
    }

	//generates the number of tabs the line needs
    public String returnTab(int depth) {
        StringBuilder s = new StringBuilder(128);
        for (int i = 0; i < depth; i++) {
            s.append("\t");
        }
        return s.toString();
    }

	//generates the 'select' part of the SQL select statement
    public String selectProperties(DataObj d) {
        StringBuilder s = new StringBuilder(1024);
        String name = d.getName();
        s.append("select ");
        boolean firstField = true;
        for (Field f: d.getFields()) {
            if (firstField) {
                firstField = false;
                s.append(name + "." + f.getName() +" ");
            }
            else {
                s.append(", " + name + "." + f.getName() +" ");
            }
        }
        return s.toString();
    }

    //query NO starts at 0
    //queryNoNext starts at 1
    //depth starts at 0
	// DataObj d - data object that's foreign keys are being expanded from ids to JSON objects
	// depth - tab placement
	// queryNo - which query is currently 
    public String evaluateFK(DataObj d, int depth, String lastQueryFrom, String lastQueryWhere, String dbQueryParam) {
        StringBuilder s = new StringBuilder(4028);
        HashMap<DataObj, List<String>> dataObjsMap = new HashMap<DataObj, List<String>>(); 
		//First, evaluate all the foreign keys on the current object
        for (Field f : d.getFields()) {
            if (f.getType() == Field.Type.FOREIGN_KEY) {	
                // get the object corresponding to the foreign key field str
                // so that you know what fields to get in sql statement
                for (DataObj fkObj: d.getDependencies()) {
                    if ((fkObj.getName()).equals(f.getTypeStr())) {
                        String parentRow = "rows" + Integer.toString(queryNo);
                        String fkRow = "rows" + Integer.toString(queryNoNext);
                        String fkName = fkObj.getName();
                        String parName = d.getName();
						
						//The 'from' part of a SQL select FROM where
                        String queryFrom = lastQueryFrom + " "
										 + fkName + " as " + fkName + " ";
						
						//The 'where' part of a SQL select from WHERE
                        String queryWhere = lastQueryWhere + " " 
										  +  fkName + ".id = " + parName + "." + f.getName();
						
						//Recursive calls do need this text
                        if (queryNo == 0) {
                            queryFrom  += " inner join "
									   + parName + " as " + parName + " "; 
                            queryWhere += " and " + parName + ".id = ?";
                        }
						
                        s.append(returnTab(2+depth) + parentRow
													+ ".forEach(function(row) { \n");
                        if (depth == 0) {
							s.append(returnTab(3+depth) +"totalCount += rows0.length;\n");
						}
						s.append(returnTab(3+depth) + "query = \"" 
													+ selectProperties(fkObj) + "\";\n");
                        s.append(returnTab(3+depth) + "query += \"from "+ queryFrom
													+ "\";\n");
                        s.append(returnTab(3+depth) + "query += \"where "+ queryWhere
													+ "\";\n");
                        s.append(returnTab(3+depth) 
									+ "con.query(query, "+dbQueryParam+", function(err, " 
									+ fkRow+",fields) {\n");
                        s.append(returnTab(4+depth) + "if (err) throw err;\n");
                        s.append(returnTab(4+depth) + "row." + f.getName() 
													+ " = " + fkRow+"[0];\n");
						
						// Store this object, so below you can check if it has 
						// foreign keys that have to be evaluated
						ArrayList<String> a = new ArrayList<String>();
						a.add(queryFrom + " inner join ");
						a.add(queryWhere + " and ");
						a.add(Integer.toString(queryNoNext));
                        dataObjsMap.put(fkObj, a); 
                        depth+=2;
                        queryNoNext++;
						break;
                    }
                }
            }
        }
		
		// Second, recursively evaluate all of the foreign key objects 
		// The keys of dataObjsMap are all of the objects that  are 
		// DataObj d's object dependencies
		// The value is an array [0] - queryFrom
		//						 [1] - queryWhere
		//						 [2] - queryNo 
		// The dataObjsMap is populated roughly 10 lines up
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            DataObj key = (DataObj)one.getKey();
			
            List<String> values = (List<String>)dataObjsMap.get(key);
			
			//reset global variable queryNo
			//why are we parsing ints? Need to parse the int because
			//[2] queryNo is an int
			queryNo = Integer.parseInt(values.get(2));
			s.append(evaluateFK(key,depth,(String)values.get(0), (String)values.get(1), dbQueryParam));
        }
        if (depth > tabDepth) {tabDepth = depth;}
        return s.toString();
    }
}
