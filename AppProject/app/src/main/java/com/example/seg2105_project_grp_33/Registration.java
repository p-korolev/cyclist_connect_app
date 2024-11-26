package com.example.seg2105_project_grp_33;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    FirebaseDatabase db;
    DatabaseReference ref;

    private String accountType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Spinner spinner = findViewById(R.id.spnAccountType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.accountTypes, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String text = adapterView.getItemAtPosition(i).toString();
        Toast.makeText(adapterView.getContext(), "Creating "+text, Toast.LENGTH_SHORT).show();
        accountType = text;
        SelectAvailableFields();

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void SelectAvailableFields(){
        EditText clubName = (EditText) findViewById(R.id.newClubNameTextField);
        switch (accountType) {
            case "Club Owner":
                clubName.setVisibility(View.VISIBLE);
                break;
            case "Participant":
                clubName.setVisibility(View.INVISIBLE);
                break;

        }
    }

    public void OnRegisterAccountButton(View view){
        EditText userName = (EditText) findViewById(R.id.newUserNameTextField);
        EditText password = (EditText) findViewById(R.id.newPasswordTextField);
        EditText clubName = (EditText) findViewById(R.id.newClubNameTextField);

        String stringUserName = userName.getText().toString();
        String stringPassword = password.getText().toString();
        String stringClubName = clubName.getText().toString();

        InputValidationHelper inputCheck = new InputValidationHelper();
        User newUser = new User();

        if (inputCheck.NullOrEmptyField(stringUserName) || inputCheck.NullOrEmptyField(stringPassword)){
            //username or password fields are invalid
            Toast.makeText(Registration.this, "Please fill in both user name and password", Toast.LENGTH_SHORT).show();
        } else if (!inputCheck.ValidPassword(stringPassword)) {
            //password is of incorrect type
            password.setError("Error: Password is not alphanumeric!");
            Toast.makeText(Registration.this, "Please re-enter a new password", Toast.LENGTH_SHORT).show();
        } else if (accountType.equals("Club Owner") & inputCheck.NullOrEmptyField(stringClubName)){
            //account type is club owner and club name isn't filled out
            Toast.makeText(Registration.this, "Please fill in club name", Toast.LENGTH_SHORT).show();
        } else { // Todo: Add other field validation checks
            switch(accountType){
                case "Club Owner":
                    newUser = new ClubOwner(password.getText().toString(),
                                                          userName.getText().toString(),
                                                          clubName.getText().toString()
                                                         );

                    break;
                case "Participant":
                    newUser = new Participant(password.getText().toString(),
                                                          userName.getText().toString()
                                                         );
                    break;
            }
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);

            db = FirebaseDatabase.getInstance();
            ref = db.getReference("users");
            ref.child(newUser.getUsername()).setValue(newUser);
            ref.child(newUser.getUsername()).child("role").setValue(accountType);

            Toast.makeText(Registration.this,"Registration Successful!", Toast.LENGTH_SHORT).show();
            finish();
        }

    }



}