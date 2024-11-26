package com.example.seg2105_project_grp_33;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


public class UserActivity extends AppCompatActivity {
    private ListView listview_Events;
    private List<Event> list_events;

    private TextView displayedUserName;
    private Spinner searchCriteria;
    private Spinner searchOptions;
    FirebaseDatabase db;
    DatabaseReference db_events;
    DatabaseReference db_users;

    Participant current_user;

    List<String> searchOptionStrings = new ArrayList<>(Arrays.asList("None"));

    String criteria = "None";

    String option = "None";




    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        Bundle bundledArgs = getIntent().getExtras();
        Toast.makeText(UserActivity.this, "Entering Participant Activity", Toast.LENGTH_SHORT).show();
        db = FirebaseDatabase.getInstance();
        db_events = db.getReference("scheduled_events");
        db_users = db.getReference("users");
        listview_Events = (ListView) findViewById(R.id.availableEventsListView);
        searchCriteria = findViewById(R.id.searchCriteriaSpinner);
        searchOptions = findViewById(R.id.searchOptionsSpinner);
        displayedUserName = findViewById(R.id.userNameTextField);

        list_events = new ArrayList<>();

        //initialize spinners
        ArrayAdapter<CharSequence> criteriaAdapter = ArrayAdapter.createFromResource(this, R.array.eventSearchOptions, android.R.layout.simple_spinner_item);
        criteriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchCriteria.setAdapter(criteriaAdapter);

        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchOptionStrings);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        searchOptions.setAdapter(optionsAdapter);

        searchCriteria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //get search criteria
                criteria = adapterView.getItemAtPosition(i).toString();
                System.out.println("CRITERIA SET TO "+criteria);
                //refresh options spinner
                refreshOptionsSpinner(criteria);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        searchOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                option = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //initialize user
        if(bundledArgs != null){
            String username = (String) bundledArgs.get("Username");
            //get user from db
            Query checkUserDB = db_users.child(username);
            checkUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()){
                        System.out.println("FOUND USER" + username);
                        current_user = snapshot.getValue(Participant.class);
                        if(current_user == null){
                            System.out.println("User is null");
                        } else {
                            displayedUserName.setText(current_user.getUsername());
                        }
                    } else {
                        System.out.println("Couldn't find user "+username);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }


        listview_Events.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Event event = list_events.get(i);
                showRegisterEventDialog(event);
                return true;
            }
        });

    }

    @Override
    protected void onStart(){
        super.onStart();

        db_events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                list_events.clear();
                ActiveEventList eventAdapter = new ActiveEventList(UserActivity.this, list_events);
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

                    Event current_event = new Event(de_uid, de_type, de_loc, de_date, de_club,de_capacity);
                    current_event.setNumParticipants(de_registrants);
                    current_event.setRegistrants(de_registrant_usernames);

                    System.out.println("Found event: "+current_event.toString());

                    //add based on criteria and option
                    if (criteria.equals("Club Name")){
                        if(option.equals(de_club)){
                            list_events.add(current_event);
                        }
                    } else if (criteria.equals("Event Type")) {
                        if(option.equals(de_type.getType())){
                            list_events.add(current_event);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void refreshOptionsSpinner(String criteria){
        searchOptionStrings.clear();

        switch(criteria) {
            case "Club Name":
                db_users.orderByChild("role").equalTo("Club Owner").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot user: snapshot.getChildren()){
                            searchOptionStrings.add((String) user.child("clubName").getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
                break;

            case "Event Type":
                DatabaseReference event_Types = db.getReference("events");
                event_Types.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot postSnapshot: snapshot.getChildren()){
                            searchOptionStrings.add((String) postSnapshot.child("type").getValue());
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
            default:
                searchOptionStrings.clear();
                searchOptionStrings.add("None");
        }
        ArrayAdapter<String> optionsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, searchOptionStrings);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        searchOptions.setAdapter(optionsAdapter);
    }


    private void showRegisterEventDialog(final Event event){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder( this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_delete_user, null);
        dialogBuilder.setView(dialogView);

        final Button buttonRegister = dialogView.findViewById(R.id.buttonDelete);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        buttonRegister.setText("Register");
        dialogBuilder.setTitle("Register for " + event.getType() + "?");
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean result = event.addParticipant(current_user.getUsername());
                if(result == true) {
                    Toast.makeText(UserActivity.this, "Successfully Registered for Event!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(UserActivity.this, "Error: Event has Reached Capacity!", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(view -> dialog.dismiss());
    }




}