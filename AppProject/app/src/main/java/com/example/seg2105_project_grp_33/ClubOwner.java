package com.example.seg2105_project_grp_33;

import com.google.firebase.database.DatabaseReference;

import java.util.List;
import java.util.UUID;

public class ClubOwner extends User {
    private String clubName;
    private String contactName;
    private String contactNumber;
    private String socialLink;

    // Constructor for ClubOwner
    public ClubOwner(String pass, String Uname, String clubName) {
        super(pass, Uname);
        this.clubName = clubName;
        this.contactName = "";
        this.contactNumber = "";
        this.socialLink = "";
    }



    public ClubOwner(){}

    // Use @Exclude to ignore the 'role' field
    public String getRole() {return null;}
    public void setRole(String role) {}

    // Getter and Setter for clubName
    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getContactName(){return contactName;}

    public void setContactName(String name){this.contactName = name;}

    public String getContactNumber(){return contactNumber;}

    public void setContactNumber(String number){this.contactNumber = number; }

    public String getSocialLink(){return socialLink;}

    public void setSocialLink(String link){ this.socialLink = link;}

    public void updateContactInfo(String name, String number, String soc){
        DatabaseReference user_contact_name = db.getReference("users").child(this.getUsername()).child("contactName");
        user_contact_name.setValue(name);
        DatabaseReference user_socials = db.getReference("users").child(this.getUsername()).child("socialLink");
        user_socials.setValue(soc);
        DatabaseReference user_contact_number = db.getReference("users").child(this.getUsername()).child("contactNumber");
        user_contact_number.setValue(number);
    }




    //TODO Implement following for D3
    public void editScheduledEvent(Event event, EventType type, String location, String date,
                                   String club, int cap, int num_p, List<String> participants){
        UUID id = event.getId();
        DatabaseReference events =this.db.getReference("scheduled_events");
        Event updated_event = new Event(id, type,location,date,club, cap);
        updated_event.setNumParticipants(num_p);
        updated_event.setRegistrants(participants);
        events.child(String.valueOf(id)).setValue(updated_event);
    }

    public void scheduleEvent(EventType type, String location, String date, String club, int cap){
        UUID id = UUID.randomUUID();
        DatabaseReference events = db.getReference("scheduled_events");
        Event newEvent = new Event(id, type, location, date, club,cap);
        events.child(String.valueOf(id)).setValue(newEvent);
    }

    public void deleteScheduledEvent(Event event){
        UUID id = event.getId();
        DatabaseReference events = db.getReference("scheduled_events");
        events.child(String.valueOf(id)).removeValue();
    }
}