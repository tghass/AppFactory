public class Relation{
    //TODO:
    // - Deal with cases of a&b being same/different objs

    //////////
    //Fields//
    //////////
    public String name;
    public String a;//a to b relationship
    public String b;
    

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for Relation */
    public Relation(String name, String d1,String d2){
        this.name=name;
        a=d1;
        b=d2;
    }

    ///////////
    //Methods//
    ///////////
    @Override
    public String toString(){
        return "("+name+", a "+a+" to a "+b+" relation)"; 
    }
}
