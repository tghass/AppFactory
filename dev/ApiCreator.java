import java.io.*;
import java.util.*;

//Generates API following CRUD operations

public class ApiCreator {

    //////////
    //Fields//
    //////////
    public static int tabDepth = 0;
    public static int queryNo = 0;
    public static int queryNoNext = 1;

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
    public void createServerFile(HashMap<String,DataObj> dataObjsMap){
        out.write(genDbConnection());
        //For each data object, get object by ID
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
			DataObj curDataObj = (DataObj)one.getValue();
			String name = curDataObj.getName();
			out.write("\n /* " + name + ": CRUD GET, DELETE, UPDATE, POST */\n");
            out.write(genGetById(curDataObj));
			out.write(genDelById(curDataObj));
			out.write(genUpdateById(curDataObj));
			out.write(genAdd(curDataObj));
        }
		out.write(genServerListen());
    }
	public String genServerListen() {
		StringBuilder s = new StringBuilder(1024);
		s.append("var server = app.listen(3000, function() {\n");
		s.append("\tconsole.log('We have started our server"
				+ "on port 3000');\n");
		s.append("});\n");
		return s.toString();
	}
	
    public String genDbConnection() {
        StringBuilder s = new StringBuilder(1024);
        s.append("//Express is a minimal and flexible Node.js web " 
				+ "application framework that provides a robust set of features" 
				+ "for web and mobile applications\n");
        s.append("var express = require('express');\n");
        s.append("var app = express();\n");
        s.append("\n//Establish connection to the MySQL database\n");
		
        //TODO: replace with generated host,user,password, info
        s.append("var mysql = require('mysql');\n");
        s.append("var con = mysql.createConnection({\n");
        s.append("\thost : 'localhost',\n");
        s.append("\tuser : 'root',\n");
        s.append("\tpassword : 'hasskafka',\n");
        s.append("\tdatabase : 'employee'\n");
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
		s.append(returnTab(4) + "res.sendStatus(200);\n"); //TODO: return something else
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
		s.append(returnTab(4) + "res.sendStatus(200);\n"); //TODO: return something else
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
		s.append(evaluateFK(dataObj,0, "", ""));
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
    public String evaluateFK(DataObj d, int depth, String lastQueryFrom, String lastQueryWhere) {
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
                        s.append(returnTab(3+depth) + "query = \"" 
													+ selectProperties(fkObj) + "\";\n");
                        s.append(returnTab(3+depth) + "query += \"from "+ queryFrom
													+ "\";\n");
                        s.append(returnTab(3+depth) + "query += \"where "+ queryWhere
													+ "\";\n");
                        s.append(returnTab(3+depth) 
									+ "con.query(query, req.params.id, function(err, " 
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
			s.append(evaluateFK(key,depth,(String)values.get(0), (String)values.get(1)));
        }
        if (depth > tabDepth) {tabDepth = depth;}
        return s.toString();
    }
}
