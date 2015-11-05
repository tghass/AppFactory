package com.cse379.appsquared;

import java.util.*;
public class Section{
    public static enum Type {
        VIEW,
        CREATE,
        MODIFY,
        DELETE
    }

    private Type type;
    private ArrayList<String> params;
    private ArrayList<String> show;

    public Type getType(){return type;}
    public List<String> getParams(){return params;}
    public List<String> getShow(){return show;}

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

