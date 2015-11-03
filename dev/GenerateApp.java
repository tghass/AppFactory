import java.util.*;
import java.io.*;

//Main class to generate the app
//First, parses the config file file
public class GenerateApp {
    private final static String sqlFileName = "sqlDump.sql";
    private final static String serverFileName = "app.js";

	public static void main(String [] args) {
		String filename = args[0];
		Parser parser = new Parser();

		//Parse file: convert JavaScript objects to Java objects (DataObjects class) 
		parser.parseFile(filename); 
		

        //Generate SQL
        try{
            PrintWriter sql = new PrintWriter(
                    new BufferedWriter( new FileWriter(sqlFileName))
                    );
            SqlGenerator sqlGen = new SqlGenerator(sql);
            sqlGen.toSql(parser.getDataObjsMap()); //generate sqldump file
            sql.close();
        }catch(IOException e){
            System.out.println("Can't write SQL code to file "+sqlFileName);
        }


        //Generate Server File for API
        try{
            PrintWriter api = new PrintWriter(
                    new BufferedWriter( new FileWriter(serverFileName))
                    );
            ApiCreator apiGen = new ApiCreator(api);
            apiGen.createServerFile(parser.getDataObjsMap()); //generate sqldump file
            api.close();
        }catch(IOException e){
            System.out.println("Can't write API code to file "+sqlFileName);
        }
		
        System.out.println(parser.getPageObjsMap());
	}

}
