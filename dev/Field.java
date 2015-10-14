public class Field{

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
}
