import java.util.*;
<<<<<<< HEAD

public class ApiCreator {

    public static int tabDepth = 0;
    public static int queryNo = 0;
    public static int queryNoNext = 1;

    public static String createServerFile(HashMap<String,DataObj> dataObjsMap){
        StringBuilder s = new StringBuilder(4096); //TODO: change this #
       
        s.append(genDbConnection());
=======
public class ApiCreator {

    public static int tabDepth = 0;

    public static String createServerFile(HashMap<String,DataObj> dataObjsMap){
        StringBuilder s = new StringBuilder(4096); //TODO: change this #
        String db = genDbConnection();
        s.append(s);
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
        //For each data object, get object by ID
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            s.append(genGetById((DataObj)one.getValue()));
<<<<<<< HEAD
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
=======
            List<DataObj> foreignKeyObjs = ((DataObj)one.getValue()).getDependencies();
            if (!foreignKeyObjs.isEmpty()) {
                s.append(genGetByFK(((DataObj)one.getValue())));
            }
        }
        return s.toString();
    }

    public static String genDbConnection() {
        StringBuilder s = new StringBuilder(4096); //TODO: change this #
        s.append("//Express is a minimal and flexible Node.js web application framework that provides a robust set of features for web and mobile applications\n");
        s.append("var express = require('express');\n");
        s.append("var app = express();\n");
        s.append("\n//Establish connection to the MySQL database\n");
        //TODO: replace with generated host,user,password, info
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
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
<<<<<<< HEAD
		
		
        return s.toString();
    }

    
=======
        return s.toString();
    }

    //What is this supposed to do?
    public static String genGetByFK(DataObj dataObj) {
        for (Field f: dataObj.getFields()) {
            if (f.getType() == Field.Type.FOREIGN_KEY) {
                System.out.println(f.getName() + f.getTypeStr());
            }
        }

        return "";
    }
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
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
<<<<<<< HEAD
		
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
=======
        s.append("\tvar query = \""+ selectProperties(dataObj) + " from " + tableName + " as " +tableName.charAt(0)+ " where id = ?\";\n");
        s.append("\tcon.query(query, req.params.id, function(err, rows0,fields) {\n");
        s.append("\t\tif(err) throw err;\n");

        s.append(evaluateFK(dataObj,0,0,1, "", ""));

        s.append(returnTab(tabDepth+4) + "res.jsonp(rows0);\n");
        while (tabDepth +4> 0) {
            s.append(returnTab(3+tabDepth) + "});\n");
            tabDepth -= 1;
        }
        System.out.println(s.toString());

        s.append("\n");


        return s.toString();
    }

>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
    public static String returnTab(int depth) {
        StringBuilder s = new StringBuilder(128);
        for (int i = 0; i < depth; i++) {
            s.append("\t");
        }
        return s.toString();
    }

<<<<<<< HEAD
	//generates the 'select' part of the SQL select statement
    public static String selectProperties(DataObj d) {
        StringBuilder s = new StringBuilder(1024);
        String name = d.getName();
=======
    public static String selectProperties(DataObj d) {
        StringBuilder s = new StringBuilder(1024);
        String name = d.getName();
        char firstChar = d.getName().charAt(0);
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
        s.append("select ");
        boolean firstField = true;
        for (Field f: d.getFields()) {
            if (firstField) {
                firstField = false;
<<<<<<< HEAD
                s.append(name + "." + f.getName() +" ");
            }
            else {
                s.append(", " + name + "." + f.getName() +" ");
=======
                s.append(firstChar + "." + f.getName() +" ");
            }
            else {
                s.append(", " + firstChar + "." + f.getName() +" ");
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
            }
        }
        return s.toString();
    }

    //query NO starts at 0
    //queryNoNext starts at 1
    //depth starts at 0
<<<<<<< HEAD
	// DataObj d - data object that's foreign keys are being expanded from ids to JSON objects
	// depth - tab placement
	// queryNo - which query is currently 
    public static String evaluateFK(DataObj d, int depth, String lastQueryFrom, String lastQueryWhere) {
        StringBuilder s = new StringBuilder(4028);
        HashMap<DataObj, List<String>> dataObjsMap = new HashMap<DataObj, List<String>>(); 
		
        //First, evaluate all the foreign keys on the current object
=======
    public static String evaluateFK(DataObj d, int depth, int queryNo, int queryNoNext, String lastQueryFrom, String lastQueryWhere) {
        StringBuilder s = new StringBuilder(4028);
        HashMap<DataObj, List<String>> dataObjsMap = new HashMap<DataObj, List<String>>(); 
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
        for (Field f : d.getFields()) {
            if (f.getType() == Field.Type.FOREIGN_KEY) {	
                // get the object corresponding to the foreign key field str
                // so that you know what fields to get in sql statement
                for (DataObj fkObj: d.getDependencies()) {
                    if ((fkObj.getName()).equals(f.getTypeStr())) {
                        String parentRow = "rows" + Integer.toString(queryNo);
                        String fkRow = "rows" + Integer.toString(queryNoNext);
                        String fkName = fkObj.getName();
<<<<<<< HEAD
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
=======
                        char fkChar = fkName.charAt(0);//What if there are two foreign keys with the same first letter? How about we use the whole fkName?
                        String parName = d.getName();
                        char parChar = parName.charAt(0);
                        String queryFrom = lastQueryFrom + " " + fkName + " as " + fkChar + " ";
                        String queryWhere = lastQueryWhere + " " +  fkChar + ".id = " + parChar + "." + f.getName();
                        if (queryNo == 0) {
                            queryFrom += " inner join " + parName + " as " + parChar + " "; 
                            queryWhere += " and " + parChar + ".id = ?";
                        }
                        s.append(returnTab(2+depth) + parentRow + ".forEach(function(row) { \n");
                        s.append(returnTab(3+depth) + "query = \""+ selectProperties(fkObj) + "\";\n");
                        s.append(returnTab(3+depth) + "query += \"from "+ queryFrom + "\";\n");
                        s.append(returnTab(3+depth) + "query += \"where "+ queryWhere + "\";\n");
                        s.append(returnTab(3+depth) + "con.query(query, req.params.id, function(err, " + fkRow+",fields) {\n");
                        s.append(returnTab(4+depth) + "if (err) throw err;\n");
                        s.append(returnTab(4+depth) + "row." + f.getName()+ " = " + fkRow+"[0];\n");

                        dataObjsMap.put(fkObj, Arrays.asList(queryFrom + " inner join ", queryWhere + " and ", Integer.toString(queryNoNext) )); 
                        depth+=2;
                        queryNoNext++;

                    }
                }

                //first, evaluate all the foreign keys on the current object

            }
        }
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            DataObj key = (DataObj)one.getKey();
<<<<<<< HEAD
			
            List<String> values = (List<String>)dataObjsMap.get(key);
			
			//reset global variable queryNo
			//why are we parsing ints? Need to parse the int because
			//[2] queryNo is an int
			queryNo = Integer.parseInt(values.get(2));
			
			s.append(evaluateFK(key,depth,(String)values.get(0), (String)values.get(1)));
        }
        if (depth > tabDepth) {tabDepth = depth;}
=======
            //UNSAFE OPERATIONS HERE, unchecked cast
            List<String> values = (List<String>)one.getValue();//What is this and why are we parsing ints? This is not obvious
            s.append(evaluateFK(
                        (key),
                        depth,
                        Integer.parseInt(values.get(2)),
                        queryNoNext,
                        values.get(0),
                        values.get(1))
                    );
        }

        tabDepth = depth;
>>>>>>> c95c8dff9358acf77a5ce6915db17d9e38541983
        return s.toString();
    }
}
