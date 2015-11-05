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
