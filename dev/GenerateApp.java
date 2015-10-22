import java.util.*;


//Main class to generate the app
//First, parses the config file file
public class GenerateApp {

	public static String toSql(HashMap<String,DataObj> dataObjsMap){
        StringBuilder s = new StringBuilder(4096);
        s.append("CREATE DATABASE  IF NOT EXISTS `").append("DBNAME").append("`;\n");
        s.append("USE `").append("DBNAME").append("`;\n");
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            s.append(((DataObj)one.getValue()).toSql());
        }
        return s.toString();
    }
	
	
	
	public static void main(String [] args) {
		String filename = args[0]; //TODO: accept file from command line
		Parser parser = new Parser();

		//Parse file: convert JavaScript objects to Java objects (DataObjects class) 
		parser.parseFile(filename); 
		
		//From the DataObjects
		String sqldump = toSql(parser.dataObjsMap); //generate sqldump file
		
		System.out.println(sqldump);
	}

}