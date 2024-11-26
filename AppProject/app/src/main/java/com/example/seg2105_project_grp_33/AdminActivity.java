package com.example.seg2105_project_grp_33;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

public class AdminActivity extends AppCompatActivity {

    FirebaseDatabase db;
    DatabaseReference ref;
    DatabaseReference evnt;
    Button buttonAddEvent;
    private Admin currentAdmin;
    private List<User> listUsers;
    private List<EventType> listEvents;
    private ListView listViewUsers;
    private ListView listViewEvents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        Intent adminLogon = getIntent();
        Bundle bundledArgs = adminLogon.getExtras();
        Toast.makeText(AdminActivity.this, "Made it to admin activity", Toast.LENGTH_SHORT).show();

        if(bundledArgs != null) {
            buttonAddEvent = findViewById(R.id.AddEventBtn);
            listViewUsers = (ListView) findViewById(R.id.ListViewUsers);
            listViewEvents = (ListView) findViewById(R.id.ListViewEvents);
            listUsers = new ArrayList<>();
            listEvents = new ArrayList<>();

            String username = (String) bundledArgs.get("Username");
            db = FirebaseDatabase.getInstance();
            ref = db.getReference("users");
            evnt = db.getReference("events");

            Query checkUserDB = ref.orderByChild("username").equalTo(username);
            checkUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot userSnapshot: snapshot.getChildren()) {
                        currentAdmin = userSnapshot.getValue(Admin.class);
                        assert currentAdmin != null;
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {}
            });

            listViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    User user = listUsers.get(i);
                    showUserDeleteDialog(user);
                    return true;
                }
            });

            listViewEvents.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    EventType event = listEvents.get(i);
                    showUpdateDeleteDialog(event);
                    return true;
                }
            });
            buttonAddEvent.setOnClickListener(view -> AddEvent());
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listUsers.clear();
                UserList userAdapter = new UserList(AdminActivity.this, listUsers);
                listViewUsers.setAdapter(userAdapter);
                System.out.println("CREATED LIST OF USERS");

                for(DataSnapshot postSnapshot: snapshot.getChildren()) {
                    System.out.println("GETTING VALUE");

                    String userType = postSnapshot.child("role").getValue(String.class);
                    String password = postSnapshot.child("password").getValue(String.class);
                    String username = postSnapshot.child("username").getValue(String.class);

                    System.out.println("GOT VALUE OF ROLE "+userType);

                    if(userType.equals("Admin")) {
                        continue;
                    } else if (userType.equals("Participant")) {
                        System.out.println("FOUND PARTICIPANT");

                        Participant user = new Participant(password, username);

                        listUsers.add(user);
                        userAdapter.notifyDataSetChanged();
                    } else if (userType.equals("Club Owner")){
                        String clubname = postSnapshot.child("clubName").getValue(String.class);
                        System.out.println("FOUND OWNER");

                        ClubOwner user = new ClubOwner(password, username, clubname);

                        listUsers.add(user);
                        userAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        evnt.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listEvents.clear();
                EventList eventAdapter = new EventList(AdminActivity.this, listEvents);
                listViewEvents.setAdapter(eventAdapter);

                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    EventType event = postSnapshot.getValue(EventType.class);
                    listEvents.add(event);
                    eventAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showUserDeleteDialog(final User user) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_delete_user, null);
        dialogBuilder.setView(dialogView);

        final Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonCancel);

        dialogBuilder.setTitle("Delete User?");
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAdmin.removeUser(user);
                dialog.dismiss();
            }
        });

        buttonCancel.setOnClickListener(view -> dialog.dismiss());
    }
    private void showUpdateDeleteDialog(final EventType eventType) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update_event_type, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextPace = dialogView.findViewById(R.id.editTextPace);
        final EditText editTextType = dialogView.findViewById(R.id.editTextType);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        final Button buttonUpdate = dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonDelete = dialogView.findViewById(R.id.buttonDelete);

        dialogBuilder.setTitle(eventType.getType());
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editTextType.getText().toString().trim();
                String pace = editTextPace.getText().toString().trim();
                String description = editTextDescription.getText().toString();

                InputValidationHelper inputCheck = new InputValidationHelper();
                boolean allowUpdate = true;
                if(inputCheck.NullOrEmptyField(type) || inputCheck.NullOrEmptyField(pace) || inputCheck.NullOrEmptyField(description)) {
                    Toast.makeText(AdminActivity.this, "Event text fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    allowUpdate = false;
                }
                if (allowUpdate == true) {
                    currentAdmin.EditEvenType(type, pace, description);
                    dialog.dismiss();
                }
            }
        });

        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentAdmin.RemoveEvenType(eventType);
                dialog.dismiss();
            }
        });
    }
    private void AddEvent() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_update_event_type, null);
        dialogBuilder.setView(dialogView);

        final EditText editTextPace = dialogView.findViewById(R.id.editTextPace);
        final EditText editTextType = dialogView.findViewById(R.id.editTextType);
        final EditText editTextDescription = dialogView.findViewById(R.id.editTextDescription);
        final Button buttonAddEvent = dialogView.findViewById(R.id.buttonUpdate);
        final Button buttonCancel = dialogView.findViewById(R.id.buttonDelete);

        buttonAddEvent.setText("Add Event");
        buttonCancel.setText("Cancel");
        dialogBuilder.setTitle("Add new Event");
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        buttonAddEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type = editTextType.getText().toString().trim();
                String pace = editTextPace.getText().toString().trim();
                String description = editTextDescription.getText().toString();

                InputValidationHelper inputCheck = new InputValidationHelper();
                boolean allowAddEvent = true;
                if(inputCheck.NullOrEmptyField(type) || inputCheck.NullOrEmptyField(pace) || inputCheck.NullOrEmptyField(description)) {
                    Toast.makeText(AdminActivity.this, "Event text fields cannot be empty!", Toast.LENGTH_SHORT).show();
                    allowAddEvent = false;
                }
                if (allowAddEvent == true) {
                    currentAdmin.createAndStoreEventType(type, pace, description);
                    dialog.dismiss();
                }
            }
        });

        buttonCancel.setOnClickListener(view -> dialog.dismiss());
    }
}