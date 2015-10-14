import java.util.*;
public class DataObj{
    //TODO : 
    // - Verify no name overlap between Field and Relation
    // - Do this with a static map/table

    //////////
    //Fields//
    //////////
    private String name;
    private List<Field> fields;
    private List<Relation> relations;
    private List<String> display;
    private Field sortBy;//Default to ID field

    

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
        
        //Add ID field
        Field id = new Field("ID",Field.Type.PRIMARY_KEY);
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
}
