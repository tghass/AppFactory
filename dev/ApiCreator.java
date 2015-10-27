
import java.util.*;

//Generates API following CRUD operations

public class ApiCreator {

    public static int tabDepth = 0;
    public static int queryNo = 0;
    public static int queryNoNext = 1;

    public static String createServerFile(HashMap<String,DataObj> dataObjsMap){
        StringBuilder s = new StringBuilder(4096); //TODO: change this #
       
        s.append(genDbConnection());
        //For each data object, get object by ID
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            s.append(genGetById((DataObj)one.getValue()));
        }
		s.append(genListen());
		System.out.println(s.toString());
        return s.toString();
    }
	public static String genListen() {
		StringBuilder s = new StringBuilder(1024);
		s.append("var server = app.listen(3000, function() {\n");
		s.append("\tconsole.log('We have started our server"
				+ "on port 3000');\n");
		s.append("});\n");
		return s.toString();
	}
	
    public static String genDbConnection() {
        StringBuilder s = new StringBuilder(4096); //TODO: change this #
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
        s.append("\t});\n");
        s.append("\n");
		
		
        return s.toString();
    }

    
    //Generate a get by id call for every table.
    //For the tables with foreign keys, this calls function
    // genGetByForeignKey 
    public static String genGetById(DataObj dataObj){
        StringBuilder s = new StringBuilder(1024);
        String tableName = dataObj.getName();
        List<String> displayFields = dataObj.getDisplay();
        StringBuilder fieldList = new StringBuilder(1024);
        boolean firstElt = true;
        for(String field: dataObj.getDisplay()) {
            // not looking for string values, only looking for
            // fields in the table
            if (field.contains("@")) { continue; } //TODO, check if the first char is @ only?

            // ensures the commas are placed appropriately
            if (firstElt) {
                fieldList.append(field);
                firstElt = false;
            }
            else {
                fieldList.append(", " +field);
            }
        }
        s.append("app.get('/" + tableName + "/find/:id', function(req,res) {\n");
		
		//generates the BASIC select * from table where id = ?
        s.append("\tvar query = \""+ selectProperties(dataObj) + 
				" from " + tableName + " as " +tableName+ " where id = ?\";\n");
        s.append("\tcon.query(query, req.params.id, function(err, rows0,fields) {\n");
        s.append("\t\tif(err) throw err;\n");

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
    public static String returnTab(int depth) {
        StringBuilder s = new StringBuilder(128);
        for (int i = 0; i < depth; i++) {
            s.append("\t");
        }
        return s.toString();
    }

	//generates the 'select' part of the SQL select statement
    public static String selectProperties(DataObj d) {
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
    public static String evaluateFK(DataObj d, int depth, String lastQueryFrom, String lastQueryWhere) {
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
