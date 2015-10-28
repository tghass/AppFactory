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
    private String sortBy;//Default to ID field
    private List<DataObj> dependencies;
    private boolean convertedToSql;
    private String linkField;
    private String linkPage;

    

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
        setSortBy(DataObj.ID);

        linkField="";
        linkPage="";
    }

    ///////////
    //Methods//
    ///////////
    public void addLink(String field, String page){
        linkField=field;
        linkPage=page;
    }
    public String getLinkField(){return linkField;}
    public String getLinkPage(){return linkPage;}
    public boolean hasLink(){//UNTESTED
        return !("".equals(linkField) || "".equals(linkPage));
    }
    public boolean isConverted(){return convertedToSql;}
    public void markConverted(){ convertedToSql=true;}
    public boolean addRelation(Relation n){
        return relations.add(n);
    }
    public boolean addDisplay(String n){
        return display.add(n);
    }
    public boolean addField(Field n){
        return fields.add(n);
    }
    public void setSortBy(String n){
        sortBy=n;
    }
    public String getName(){ return name;}
    public List<Field> getFields(){ return fields;}
    public List<DataObj> getDependencies(){ return dependencies;}
	public List<String> getDisplay() { return display; }
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
        s.append("\nSortBy: ").append(sortBy).append("\n");
        if(hasLink()){
            s.append("Link: ").append(linkField+" to "+linkPage).append("\n");
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
