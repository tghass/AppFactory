import java.util.*;
public class SqlGenerator{
    public static final String DEFAULTS = "DEFAULT NULL";
    public static final String DEFAULTS_ID = "INT(12) NOT NULL AUTO_INCREMENT";

    ///////////
    //Methods//
    ///////////
	public static String toSql(HashMap<String,DataObj> dataObjsMap){
        StringBuilder s = new StringBuilder(4096);
        s.append("CREATE DATABASE  IF NOT EXISTS `").append("DBNAME").append("`;\n");
        s.append("USE `").append("DBNAME").append("`;\n");
        Iterator it = dataObjsMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry one = (Map.Entry)it.next();
            s.append(SqlGenerator.toSql(((DataObj)one.getValue())));
        }
        return s.toString();
    }
    public static String toSql(DataObj d){
        StringBuilder s = new StringBuilder(1024);
        String name = d.getName();
        if(!d.isConverted()){
            //Do all dependecies first
            for(DataObj obj: d.getDependencies()){
                s.append(SqlGenerator.toSql(obj));
            }
            //Now this one
            s.append("DROP TABLE IF EXISTS `").append(name).append("`;\n");
            s.append("CREATE TABLE `").append(name).append("`(\n");
                for(Field f : d.getFields()){//Add fields
                    s.append("").append(SqlGenerator.toSql(f));
                }
                s.append("  PRIMARY KEY (`").append(DataObj.ID).append("`),\n");
                for(Field f : d.getFields()){//Add foreign keys
                    s.append("").append(SqlGenerator.foreignKeySql(f));
                }
            s.setCharAt(s.lastIndexOf(","),' ');
            s.append(");\n");
            d.markConverted();//Only do it once!
        }
        return s.toString();
    }
    public static String toSql(Field f){
        StringBuilder s = new StringBuilder(512);
        s.append("  `").append(f.getName()).append("` ");    //`field`
        if(f.getType() == Field.Type.PRIMARY_KEY){//The ID field
            s.append(DEFAULTS_ID);      //Defaults for ID field
        }
        else{
            s.append(typeToSql(f.getType())).append(" ");  //varchar(512) for example
            s.append(DEFAULTS);                     //Add defaults for each field
        }
        s.append(",\n");                            //Add comma and new line
        return s.toString();
    }
    public static String foreignKeySql(Field f){
        if(f.getType() == Field.Type.FOREIGN_KEY){
            StringBuilder s = new StringBuilder(512);
            s.append("  FOREIGN KEY (`").append(f.getName()).append("`)\n");
            s.append("    REFERENCES ").append(f.getTypeStr())
                .append("(`").append(DataObj.ID).append("`)\n");
            s.append("    ON DELETE CASCADE,\n");
            return s.toString();

        }
        return "";
    }
    private static String typeToSql(Field.Type t){
        switch (t){
            case DATE:
                return "DATE";
            case STRING:
                return "VARCHAR(64)";
            case LONG_STRING:
                return "VARCHAR(1024)";
            case PRIMARY_KEY:
                return "INT";
            case FOREIGN_KEY:
                return "INT";
            case LONG:
                return "BIGINT";
            case INT:
                return "INT";
            case FLOAT:
                return "FLOAT";
            case BOOLEAN:
                return "TINYINT";

            default:
                System.out.println("Error, unknown "+t.name());
                System.exit(1);
        }
        return "Error";//Unreachable
    }
}
