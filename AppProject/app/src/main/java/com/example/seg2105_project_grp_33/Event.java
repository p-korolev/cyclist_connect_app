package com.example.seg2105_project_grp_33;

import java.util.List;
import java.util.UUID;

public class Event {

    private UUID id;
    private EventType Type;
    private String Location;
    private String Date;
    private String Club;

    private int Capacity;
    private int NumParticipants;

    private List<String> Registrants;

    public Event(UUID uid, EventType type, String location, String date, String club, int cap){
        id = uid;
        Type = type;
        Location = location;
        Date = date;
        Club = club;
        Capacity = cap;
        NumParticipants = 0;

        }

    public Event(){};

    public List<String> getRegistrants(){return Registrants;}

    public void setRegistrants(List<String> registrants){Registrants = registrants;}

    public boolean addParticipant(String p){
        if (Capacity>=(NumParticipants+1)){
            Registrants.add(p);
            NumParticipants = NumParticipants+1;
            return true;
        } else {
            return false;
        }
    }

    public int getCapacity(){return Capacity;}
    public void setCapacity(int i){Capacity = i;}
    public int getNumParticipants(){return NumParticipants;}
    public void setNumParticipants(int i){NumParticipants = i;}

    public UUID getId(){return id;}
    public EventType getEventType(){return Type;}

    public String getType(){return Type.getType();}

    public void setEventType(EventType type){Type = type;}

    public String getLocation(){return Location;}

    public void setLocation(String location){Location = location;}

    public String getDate(){return Date;}

    public void setDate(String date){Date = date;}

    public String getClub(){return Club;}

    public void setClub(String club){Club = club;}

    public String toString(){return Type.toString();}



    // TODO: 2023-11-25 Validate dates and location fields

    private void checkDate(String date){}
    private void checkLocation(String date){}


}
