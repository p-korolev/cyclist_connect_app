package com.example.seg2105_project_grp_33;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClubOwnerActivity extends AppCompatActivity {
    FirebaseDatabase db;
    DatabaseReference db_events;
    DatabaseReference db_users;
    Button btn_ScheduleEvent;
    Button btn_ContactInfo;
    private ClubOwner currentOwner;
    private String ClubName;
    private List<Event> list_Events;
    private List<String> list_participants;

    private ListView listview_Events;
    private String events_node_name = "scheduled_events";
    private TextView displayed_clubname;
    private TextView displayed_username;
    private TextView displayed_contactname;
    private TextView displayed_contactnumber;
    private TextView displayed_socials;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_owner);
        Bundle bundledArgs = getIntent().getExtras();
        Toast.makeText(ClubOwnerActivity.this, "Entering CLub Owner Activity", Toast.LENGTH_SHORT).show();

        if(bundledArgs != null) {
            db = FirebaseDatabase.getInstance();
            db_events = db.getReference(events_node_name);


            //Get refs to activity objects
            btn_ContactInfo = findViewById(R.id.contactInfoBtn);
            btn_ScheduleEvent = findViewById(R.id.scheduleNewEventBtn);
            listview_Events = (ListView) findViewById(R.id.activeEventsListView);
            list_Events = new ArrayList<>();

            //get user from db
            db_users = db.getReference("users");
            String username = (String) bundledArgs.get("Username");

            Query checkUserDB = db_users.child(username);
            System.out.println("Attempting to find user: "+username+" in db");
            checkUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        System.out.println("FOUND "+username+" IN DB. WRITING.....");
                        currentOwner = snapshot.getValue(ClubOwner.class);
                        assert currentOwner != null;

                        ClubName = currentOwner.getClubName();
                        displayed_clubname = findViewById(R.id.displayedClubNameTextField);
                        displayed_username = findViewById(R.id.displayedUserName);
                        displayed_contactname = findViewById(R.id.displayedContactNameTextField);
                        displayed_contactnumber = findViewById(R.id.displayedContactNumber);
                        displayed_socials = findViewById(R.id.displayedClubLinks);

                        displayed_clubname.setText(ClubName);
                        displayed_username.setText(currentOwner.getUsername());
                        if (!currentOwner.getContactName().equals("null")) {
                            displayed_contactname.setText(currentOwner.getContactName());
                        }
                        if (!currentOwner.getContactNumber().equals("null")) {
                            displayed_contactnumber.setText(currentOwner.getContactNumber());
                        }
                        if (!currentOwner.getSocialLink().equals("null")) {
                            displayed_socials.setText(currentOwner.getSocialLink());
                        }
                    } else {
                        System.out.println("DID NOT FIND "+username+" IN DB.....");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            //initialize active events list views
            listview_Events.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Event event = list_Events.get(i);
                    showUpdateActiveEventDialog(event);
                    return true;
                }
            });

            btn_ScheduleEvent.setOnClickListener(view -> ScheduleEvent());

            btn_ContactInfo.setOnClickListener(view -> UpdateContactInfo());

        }
    }
    @Override
    protected void onStart(){
        super.onStart();

        db_events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_Events.clear();
                ActiveEventList eventAdapter = new ActiveEventList(ClubOwnerActivity.this, list_Events);
                listview_Events.setAdapter(eventAdapter);

                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    //deserializing
                    EventType de_type = new EventType(
                            (String) postSnapshot.child("eventType").child("pace").getValue(),
                            (String) postSnapshot.child("eventType").child("type").getValue(),
                            (String) postSnapshot.child("eventType").child("description").getValue()
                    );
                    UUID de_uid = UUID.fromString( postSnapshot.getKey());
                    String de_loc = postSnapshot.child("location").getValue().toString();
                    String de_date = postSnapshot.child("date").getValue().toString();
                    String de_club = postSnapshot.child("club").getValue().toString();
                    Integer de_capacity = Integer.parseInt(postSnapshot.child("capacity").getValue().toString());
                    Integer de_registrants = Integer.parseInt(postSnapshot.child("numParticipants").getValue().toString());
                    List<String> de_registrant_usernames = (List<String>) postSnapshot.child("registrants").getValue();

                    if (de_club.equals(ClubName)){
                        //adding to list
                        Event event = new Event(de_uid, de_type, de_loc, de_date, de_club,de_capacity);
                        event.setNumParticipants(de_registrants);
                        event.setRegistrants(de_registrant_usernames);
                        System.out.println("Adding "+event+ " To Event List in CLUBOWNER ACTIVITY");
                        list_Events.add(event);
                        eventAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUpdateActiveEventDialog(final Event event){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update_active_event, null);
        dialogBuilder.setView(dialogView);

        EventType event_type_Type = event.getEventType();
        String event_type = event.getType();
        String event_date = event.getDate();
        String event_location = event.getLocation();
        Integer event_capacity = event.getCapacity();
        Integer event_registrants = event.getNumParticipants();
        List<String> event_registered_participants = event.getRegistrants();

        final EditText newDateTextField = dialogView.findViewById(R.id.newDateTextField);
        final EditText newLocationField = dialogView.findViewById(R.id.newLocationTextField);
        final EditText newCapacityTextField = dialogView.findViewById(R.id.newCapacityTextField);
        final Button deleteBtn = dialogView.findViewById(R.id.deleteBtn);
        final Button updateBtn = dialogView.findViewById(R.id.updateBtn);
        final ListView listview_Participants = (ListView) dialogView.findViewById(R.id.ParticipantListView);

        newDateTextField.setText(event_date);
        newLocationField.setText(event_location);
        newCapacityTextField.setText(event_capacity.toString());

        dialogBuilder.setTitle(event_type+" @ "+ event_location);

        RegisteredParticipantsList eventAdapter = new RegisteredParticipantsList(dialogView.getContext(), event.getRegistrants());
        listview_Participants.setAdapter(eventAdapter);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentOwner.deleteScheduledEvent(event);
                dialog.dismiss();
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newDate = newDateTextField.getText().toString().trim();
                String newLocation = newLocationField.getText().toString().trim();
                String newCapacity = newCapacityTextField.getText().toString().trim();
                String clubName = currentOwner.getClubName();
                if((!TextUtils.isEmpty(newDate))&&(!TextUtils.isEmpty(newLocation))&&(!TextUtils.isEmpty(newCapacity))){
                    InputValidationHelper inputCheck = new InputValidationHelper();
                    boolean allowUpdate = true;
                    if(!inputCheck.ValidDate(newDate)) {
                        Toast.makeText(ClubOwnerActivity.this,"Invalid Date. Must be of form: dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                        allowUpdate = false;
                    }
                    if(!inputCheck.ValidInteger(newCapacity)) {
                        Toast.makeText(ClubOwnerActivity.this, "Please enter a valid integer value for event capacity", Toast.LENGTH_SHORT).show();
                        allowUpdate = false;
                    }
                    if(event_registrants > Integer.parseInt(newCapacity)){
                        Toast.makeText(ClubOwnerActivity.this, "Cannot change capacity to less than the number of currently registered", Toast.LENGTH_SHORT).show();
                        allowUpdate = false;
                    }
                    if(allowUpdate == true) {
                        System.out.println("Capacity at "+newCapacity);
                        currentOwner.editScheduledEvent(event,event_type_Type, newLocation, newDate,clubName,Integer.parseInt(newCapacity), event_registrants, event_registered_participants);
                    }
                    dialog.dismiss();
                }
            }
        });

    }

    private void ScheduleEvent(){
        final EventType[] selected_event_type = new EventType[1];

        //init dialog window
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_active_event, null);

        //init collection for event types
        List<EventType> availableEventTypes_List =  new ArrayList<EventType>();
        DatabaseReference db_eventTypes = db.getReference("events");
        //populate list
        db_eventTypes.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot eventType: snapshot.getChildren()){
                    availableEventTypes_List.add(eventType.getValue(EventType.class));
                }
                //populate spinner
                Spinner availableEventTypes_spinner = dialogView.findViewById(R.id.spnEventTypes);
                ArrayAdapter<EventType> adapter = new ArrayAdapter<>(ClubOwnerActivity.this, android.R.layout.simple_spinner_item, availableEventTypes_List);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                availableEventTypes_spinner.setAdapter(adapter);

                //logic for selected event type
                availableEventTypes_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l){
                        selected_event_type[0] = availableEventTypes_List.get(i);
                        Toast.makeText(adapterView.getContext(), "Creating "+ selected_event_type[0].toString(), Toast.LENGTH_SHORT).show();
                        System.out.println("SELECTED TO CREATE "+selected_event_type[0].toString());
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView){

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        final EditText newEventDate = dialogView.findViewById(R.id.newEventDateTextField);
        final EditText newLocation = dialogView.findViewById(R.id.newEventLocationTextField);
        final EditText newCapacity = dialogView.findViewById(R.id.newEventCapacityTextField);
        final Button confirm = dialogView.findViewById(R.id.addNewEventBtn);

        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = newEventDate.getText().toString().trim();
                String location = newLocation.getText().toString().trim();
                String cap = newCapacity.getText().toString().trim();

                InputValidationHelper inputCheck = new InputValidationHelper();
                boolean allowSchedule = true;
                if(inputCheck.NullOrEmptyField(location)) {
                    Toast.makeText(ClubOwnerActivity.this, "Invalid Location", Toast.LENGTH_SHORT).show();
                    allowSchedule = false;
                }
                if(inputCheck.NullOrEmptyField(date) || !inputCheck.ValidDate(date)) {
                    Toast.makeText(ClubOwnerActivity.this, "Invalid Date. Must be of form: dd/MM/yyyy", Toast.LENGTH_SHORT).show();
                    allowSchedule = false;
                }

                if(inputCheck.NullOrEmptyField(cap) || !inputCheck.ValidInteger(cap)) {
                    Toast.makeText(ClubOwnerActivity.this, "Please enter a valid integer value for event capacity", Toast.LENGTH_SHORT).show();
                    allowSchedule = false;
                }


                if(allowSchedule == true) {
                    System.out.println("Capacity at "+cap);
                    currentOwner.scheduleEvent(selected_event_type[0], location, date, ClubName,Integer.parseInt(cap));
                }
                dialog.dismiss();
            }
        });

    }


    private void UpdateContactInfo(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update_contact_info, null);
        dialogBuilder.setView(dialogView);

        final EditText contactNameTextField = dialogView.findViewById(R.id.contactNameTextField);
        final EditText contactPhoneNumber = dialogView.findViewById(R.id.contactPhoneNumber);
        final EditText socialLink = dialogView.findViewById(R.id.socialLink);
        final Button updateBtn = dialogView.findViewById(R.id.updateContactBtn);

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        if (!currentOwner.getContactName().equals("null")) {
            contactNameTextField.setText(currentOwner.getContactName());
        }
        if (!currentOwner.getContactNumber().equals("null")) {
            contactPhoneNumber.setText(currentOwner.getContactNumber());
        }
        if (!currentOwner.getSocialLink().equals("null")) {
            socialLink.setText(currentOwner.getSocialLink());
        }

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = contactNameTextField.getText().toString().trim();
                String number = contactPhoneNumber.getText().toString().trim();
                String socials = socialLink.getText().toString().trim();

                InputValidationHelper inputCheck = new InputValidationHelper();
                boolean allowUpdate = true;
                if(inputCheck.NullOrEmptyField(name)) {
                    Toast.makeText(ClubOwnerActivity.this, "Name cannot be empty!", Toast.LENGTH_SHORT).show();
                    allowUpdate = false;
                }
                if(inputCheck.NullOrEmptyField(number) || !inputCheck.ValidNumber(number)) {
                    Toast.makeText(ClubOwnerActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                    allowUpdate = false;
                }
                if(inputCheck.NullOrEmptyField(socials) || !inputCheck.ValidSocialMedia(socials)) {
                    Toast.makeText(ClubOwnerActivity.this, "Invalid Link to Social Media", Toast.LENGTH_SHORT).show();
                    allowUpdate = false;
                }

                if(allowUpdate == true) {
                    currentOwner.updateContactInfo(name, number, socials);

                    displayed_contactname.setText(name);
                    displayed_contactnumber.setText(number);
                    displayed_socials.setText(socials);

                }
                dialog.dismiss();
            }
        });

    }
}


















































