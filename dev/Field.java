import java.util.*;

public class Field{
    //////////
    //Fields//
    //////////
    private String name;
    private String typeStr;
    private Type type;
    

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

    public String getName(){return name;}
    public String getTypeStr(){return typeStr;}
    public Type getType(){return type;}

    @Override
    public String toString(){
        return "("+name+" of type "+type.name()+")";
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
                //TODO output helpful error msg
                System.out.println("UNRESOLVED FIELD! ->"+this);
            }
        }
        return obj;
    }
}
