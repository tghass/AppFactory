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
    public String getParamsString(){
        StringBuilder params = new StringBuilder(64);//Do the params
        for(String param : getParams()){
            params.append(param+", ");
        }
        int indexOfLastComma = params.lastIndexOf(",");
        if(indexOfLastComma>=0)
            params.setCharAt(indexOfLastComma,' ');//Remove last ,
        return params.toString();
    }
    public List<String> getParams(){
        //TODO, what about multiple of the same type
        Set params = new HashSet<String>();
        for(Section s : sections){
            for(String param : s.getParams()){
                params.add(param);
            }
        }
        return new ArrayList<String>(params);
    }
    /* Return list of strings. These are the objs that should be viewed,created, etc */
    public List<String> getShow(Section.Type type){
        Set show = new HashSet<String>();
        for(Section s : sections){
            if(s.getType()==type){
                for(String param : s.getShow()){
                    show.add(param);
                }
            }
        }
        return new ArrayList<String>(show);

    }
    public String getShowString(String appendedText){
        StringBuilder params = new StringBuilder(64);//Add params to view call
        //Add view show
		params.append("{");
        for(Section.Type type : Section.Type.values()){
            if(getShow(type).size()==0){
                continue;
            }
            //We don't need to pass any data in here
            if(type==Section.Type.CREATE || type==Section.Type.DELETE){
                params.append(type.name()+": \"");
                params.append(String.join("\", \"", getShow(type)));
                params.append("\",");
                continue;
            }
            params.append(type.name()+": {");
            for(String param : getShow(type)){
                params.append(param+":"+param+appendedText+", ");
            }
            int indexOfLastComma = params.lastIndexOf(",");
            if(indexOfLastComma>=0)
                params.deleteCharAt(indexOfLastComma);//Remove last ,
            params.append("},");
        }
        int indexOfLastComma = params.lastIndexOf(",");
        if(indexOfLastComma>=0)
            params.deleteCharAt(indexOfLastComma);//Remove last ,
        params.append("}");
		return params.toString();
    }
	
    public String getName(){ return name;}
    public List<Section> getSections(){ return sections;}
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
