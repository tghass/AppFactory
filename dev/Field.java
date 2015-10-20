import java.util.*;

public class Field{
    public static final String DEFAULTS = "DEFAULT NULL";
    public static final String DEFAULTS_ID = "INT(12) NOT NULL AUTO_INCREMENT";

    //////////
    //Fields//
    //////////
    public String name;
    public String typeStr;
    public Type type;
    

    public static enum Type{
        DATE,
        STRING,
        LONG_STRING,
        PRIMARY_KEY,
        FOREIGN_KEY,
        LONG,
        INT,
        FLOAT,
        BOOLEAN,
        UNRESOLVED
    }
    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for Field */
    public Field(String name){//Default to ID
        this.name=name;
        this.type=Type.PRIMARY_KEY;
        this.typeStr=type.name();
    }
    public Field(String name, Type type){
        this.name=name;
        this.typeStr=type.name();
        this.type=type;
    }
    public Field(String name, String type){
        this.name=name;
        this.typeStr=type;
        this.type=getType(type);
    }

    ///////////
    //Methods//
    ///////////
    public String typeToSql(Type t){
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
    public Type getType(String type){
        switch (type.toLowerCase()){
            case "date":
                return Type.DATE;
            case "string":
                return Type.STRING;
            case "long string":
                return Type.LONG_STRING;
            case "long":
                return Type.LONG;
            case "int":
                return Type.INT;
            case "float":
                return Type.FLOAT;
            case "boolean":
                return Type.BOOLEAN;

            default:
                return Type.UNRESOLVED;
        }
    }

    @Override
    public String toString(){
        return "("+name+" of type "+type.name()+")";
    }

    public String toSql(){
        StringBuilder s = new StringBuilder(512);
        s.append("  `").append(name).append("` ");    //`field`
        if(type == Type.PRIMARY_KEY){//The ID field
            s.append(DEFAULTS_ID);      //Defaults for ID field
        }
        else{
            s.append(typeToSql(type)).append(" ");  //varchar(512) for example
            s.append(DEFAULTS);                     //Add defaults for each field
        }
        s.append(",\n");                            //Add comma and new line
        return s.toString();
    }
    public String foreignKeySql(){
        if(type == Type.FOREIGN_KEY){
            StringBuilder s = new StringBuilder(512);
            s.append("  FOREIGN KEY (`").append(name).append("`)\n");
            s.append("    REFERENCES ").append(typeStr)
                .append("(`").append(DataObj.ID).append("`)\n");
            s.append("    ON DELETE CASCADE,\n");
            return s.toString();

        }
        return "";
    }
    public String resolve(HashMap<String,DataObj> dataObjsMap){
        String obj="";
        if(type==Type.UNRESOLVED){//Let's try to resolve it
            //Reference to another Data Obj?
            if(dataObjsMap.containsKey(typeStr)){//It is a reference to another D.O.
                type=Type.FOREIGN_KEY;
                obj = typeStr;
            }
            else{
                System.out.println("UNRESOLVED FIELD! ->"+this);
            }
        }
        return obj;
    }
}
