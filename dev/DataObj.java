import java.util.*;
public class DataObj{
    //TODO : 
    // - Verify no name overlap between Field and Relation
    // - Do this with a static map/table
    // - Add boolean field (converted) that tells if it has been turned into SQL
    //  - What about circular relationships

    //////////
    //Fields//
    //////////
    public static final String ID = "ID";

    private String name;
    private List<Field> fields;
    private List<Relation> relations;
    private List<String> display;
    private Field sortBy;//Default to ID field
    private List<DataObj> dependencies;
    private boolean convertedToSql;

    

    ////////////////
    //Constructors//
    ////////////////
    /** Constructor for DataObj */
    public DataObj(String name){
        this.name=name;
        //Initialize lists
        fields = new ArrayList<Field>();
        relations = new ArrayList<Relation>();
        display = new ArrayList<String>();
        dependencies = new ArrayList<DataObj>();
        convertedToSql=false;
        
        //Add ID field
        Field id = new Field(DataObj.ID);
        addField(id);
        //Set default sortyBy to the ID field
        setSortBy(id);
    }

    ///////////
    //Methods//
    ///////////
    public boolean addRelation(Relation n){
        return relations.add(n);
    }
    public boolean addDisplay(String n){
        return display.add(n);
    }
    public boolean addField(Field n){
        return fields.add(n);
    }
    public void setSortBy(Field n){
        sortBy=n;
    }
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(1024);
        s.append("\nName: ").append(name);
        s.append("\nFields: ");
        for(Field f : fields){
            s.append(f.toString()).append(",");
        }
        s.append("\nRealtions: ");
        for(Relation r : relations){
            s.append(r.toString()).append(",");
        }
        s.append("\nDisplay: ");
        for(String d : display){
            s.append(d).append(",");
        }
        s.append("\nSortBy: ").append(sortBy.toString()).append("\n");

        return s.toString();
    }

    public String toSql(){
        StringBuilder s = new StringBuilder(1024);
        if(!convertedToSql){
            //Do all dependecies first
            for(DataObj d: dependencies){
                s.append(d.toSql());
            }
            //Now this one
            s.append("DROP TABLE IF EXISTS `").append(name).append("`;\n");
            s.append("CREATE TABLE `").append(name).append("`(\n");
                for(Field f : fields){//Add fields
                    s.append("").append(f.toSql());
                }
                s.append("  PRIMARY KEY (`").append(DataObj.ID).append("`),\n");
                for(Field f : fields){//Add foreign keys
                    s.append("").append(f.foreignKeySql());
                }
            s.setCharAt(s.lastIndexOf(","),' ');
            s.append(");\n");
            convertedToSql=true;//Only do it once!
        }
        return s.toString();
    }

    public void resolve(HashMap<String,DataObj> dataObjsMap,HashMap<String,Relation> relationsMap){
        //Loop through all relations
        //Try to find the Relation with this name
        if(relationsMap.containsKey(name)){
            Relation r = relationsMap.get(name);
            //Set up the relation in the D.O.
            Field f1 = new Field(r.getName()+"1",r.getA());
            Field f2 = new Field(r.getName()+"2",r.getB());
            //Add the new fileds
            addField(f1);
            addField(f2);
            r.markResolved();//The relation is now resolved
        }
        //Loop through all fields
        for(Field f : fields){
            //Try to find the missing field
            String obj = f.resolve(dataObjsMap);
            if(!obj.equals("")){
                //Add it as a dependency
                dependencies.add(dataObjsMap.get(obj));
            }
        }
    }
}
