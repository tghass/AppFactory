import java.util.*;


//Main class to generate the app
//First, parses the config file file
public class GenerateApp {

	public static void main(String [] args) {
		String filename = args[0];
		Parser parser = new Parser();

		//Parse file: convert JavaScript objects to Java objects (DataObjects class) 
		parser.parseFile(filename); 
		

		//From the DataObjects
		String sqldump = SqlGenerator.toSql(parser.getDataObjsMap()); //generate sqldump file
		
		System.out.println(sqldump);
		
		ApiCreator apiCreator = new ApiCreator();
		//System.out.println(apiCreator.createServerFile(parser.getDataObjsMap()));
		
	}

}
