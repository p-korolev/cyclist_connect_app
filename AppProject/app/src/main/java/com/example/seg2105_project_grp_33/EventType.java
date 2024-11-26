package com.example.seg2105_project_grp_33;

import androidx.annotation.NonNull;

public class EventType {

    private String Pace;
    private String Type;
    private String Description;

    public EventType(String type,String pace, String description){
        Pace = pace;
        Type = type;
        Description = description;

    }

    public EventType(){};

    //getters
    public String getPace(){
        return Pace;
    }
    public void setPace(String pace){Pace = pace;}
    public String getType(){
        return Type;
    }
    public void setType(String type){Type = type;}

    public String getDescription(){
        return Description;
    }
    public void setDescription(String desc){Description = desc;}

    @NonNull
    @Override
    public String toString() {
        return this.Type+" @ "+this.Pace + " Pace";
    }
}
