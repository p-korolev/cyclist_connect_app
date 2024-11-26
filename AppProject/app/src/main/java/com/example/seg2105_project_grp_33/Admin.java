package com.example.seg2105_project_grp_33;

import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;




public class Admin extends User {
    // Constructor for Admin
    public Admin(String pass, String Uname) {
        super(pass, Uname);
    }

    public Admin(){};

    public void EditEvenType(String event_type, String pace, String description){
        //get reference to events in db
        DatabaseReference ref = this.db.getReference("events").child(event_type);
        EventType updated_event_type = new EventType(event_type, pace, description);
        ref.setValue(updated_event_type);
        return;
    }

    public boolean RemoveEvenType(EventType event){
        try
        {
            DatabaseReference ref = this.db.getReference("events").child(event.getType());
            ref.removeValue();
            return true;
        }
        catch(DatabaseException DB_e)
        {
            return false;
        }
    }
    public void createAndStoreEventType(String type, String pace, String description) {
        // Reference to the 'events' node in the database
        DatabaseReference eventsRef = this.db.getReference("events");

        // Create a new EventType object
        EventType newEvent = new EventType(type, pace, description);

        // Store the new event type in the database under its type name
        eventsRef.child(type).setValue(newEvent);

        // Optionally, display a message or handle the operation's result

    }

    public boolean removeUser(User user) {
        DatabaseReference ref = this.db.getReference("users").child(user.getUsername());
        ref.removeValue();
        return true;
    }

}
