public class Relation{
    //TODO:
    // - Deal with cases of a&b being same/different objs

    //////////
    //Fields//
    //////////
    private String name;
    private String a;//a to b relationship
    private String b;
    private boolean sameType;
    private boolean resolved;
    

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for Relation */
    public Relation(String name, String d1,String d2){
        this.name=name;
        a=d1;
        b=d2;
        sameType = a.equals(b);
        resolved=false;
    }

    ///////////
    //Methods//
    ///////////
    public String getName(){ return name;}
    public String getA(){ return a;}
    public String getB(){ return b;}


    @Override
    public String toString(){
        return "("+name+", a "+a+" to a "+b+" relation)"; 
    }

    public String toSql(){
        return "";
    }
    public void markResolved(){ resolved=true;}
    public boolean isResolved(){ return resolved;}
}
