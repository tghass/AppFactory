package com.cse379.appsquared;

import java.util.*;
public class PageObj{

    //////////
    //Fields//
    //////////
    private String name;
    private ArrayList<Section> sections;
    

    ////////////////
    //Constructors//
    ////////////////

    /** Constructor for PageObj */
    public PageObj(String name){
        this.name=name;
        sections=new ArrayList<Section>();
    }

    ///////////
    //Methods//
    ///////////
    public boolean addSection(String type, ArrayList<String> params, ArrayList<String> show){
        return sections.add(new Section(type, params,show));
    }
    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(1024);
        s.append("Name: "+name+"\n");
        for(Section se : sections){
           s.append("Secion:\n"+se.toString());
        }
        return s.toString();
    }
}
class Section{
    public static enum Type {
        VIEW,
        CREATE,
        MODIFY,
        DELETE
    }

    private Type type;
    private ArrayList<String> params;
    private ArrayList<String> show;

    public Section(String type, ArrayList<String> params, ArrayList<String> show){
        switch (type.toLowerCase()){
            case "view":
                this.type = Type.VIEW;
                break;
            case "create":
                this.type = Type.CREATE;
                break;
            case "modify":
                this.type = Type.MODIFY;
                break;
            case "delete":
                this.type = Type.DELETE;
                break;

            default:
                System.out.println("Error, Unknown type in Page Object:"+type);
                System.exit(1);
                break;
        }

        this.params=params;
        this.show=show;

    }

    @Override
    public String toString(){
        StringBuilder s = new StringBuilder(1024);
        s.append("Type: "+type.name()+"\n");
        s.append("Params: ");
        for(String p : params){
            s.append(p+", ");
        }s.append("\n");
        s.append("Show: ");
        for(String p : show){
            s.append(p+", ");
        }s.append("\n");
        
        return s.toString();
    }
}

