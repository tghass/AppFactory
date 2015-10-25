	import java.util.*;
	
	public class ApiCreator {
		
		
			public static String createServerFile(HashMap<String,DataObj> dataObjsMap){
				String db = genDbConnection();
				System.out.println(db);
				StringBuilder s = new StringBuilder(4096); //TODO: change this #
				//For each data object, get object by ID
				Iterator it = dataObjsMap.entrySet().iterator();
				while(it.hasNext()){
					Map.Entry one = (Map.Entry)it.next();
					s.append(genGetById((DataObj)one.getValue()));
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
			
			public static String genGetByFK(DataObj dataObj) {
				System.out.println(dataObj.getName() + " has the following dependencies ");
					for (Field f: dataObj.getFields()) {
						if (f.getType() == Field.Type.FOREIGN_KEY) {
							System.out.println(f.getName() + f.getTypeStr());
						}
					}
				
				return "";
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
					if (field.contains("@")) { continue; }
					
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
				s.append("\tvar query = \"select "+ fieldList.toString() + " from " + tableName + " where id = ?\";\n");
				s.append("\tcon.query(query, req.params.id, function(err, rows,fields) {\n");
				s.append("\t\tif(err) throw err;\n");
				s.append("\t\tres.jsonp(rows);\n");
				s.append("\t});\n");
				s.append("});\n");
				s.append("\n");
			
			
				return s.toString();
			}
	}
	