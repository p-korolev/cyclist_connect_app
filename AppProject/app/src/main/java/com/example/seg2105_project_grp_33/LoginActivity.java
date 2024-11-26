package com.example.seg2105_project_grp_33;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private TextView registrationTxt;
    FirebaseDatabase db;
    DatabaseReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialization for text fields and button
        usernameInput = findViewById(R.id.usernameInputText);
        passwordInput = findViewById(R.id.passwordInputText);
        loginBtn = findViewById(R.id.loginBtn);
        registrationTxt = findViewById(R.id.registrationText);
    }

    public void onClickLogin(View v) {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        InputValidationHelper inputCheck = new InputValidationHelper();
        boolean allowLogin = true;

        if(inputCheck.NullOrEmptyField(username) || inputCheck.NullOrEmptyField(password)) {
            Toast.makeText(LoginActivity.this, "Username and Password cannot be empty!", Toast.LENGTH_SHORT).show();
            allowLogin = false;
        }
        if(!inputCheck.ValidPassword(password)) {
            passwordInput.setError("Error: Password is not alphanumeric!");
            allowLogin = false;
        }
        if(allowLogin == true) {
            loginUser(username, password);
        }
    }

    public void onClickRegistration(View v) {
        //intent to move over to RegistrationActivity
        startActivity(new Intent(getApplicationContext(), Registration.class));
    }

    private void loginUser(String user, String pass) {

        db = FirebaseDatabase.getInstance();
        ref = db.getReference("users");
        Query checkUserDB = ref.orderByChild("username").equalTo(user);
        checkUserDB.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    usernameInput.setError(null);
                    String storedDBPassword = snapshot.child(user).child("password").getValue(String.class);
                    if(storedDBPassword.equals(pass)) {
                        usernameInput.setError(null);
                        String storedRole = snapshot.child(user).child("role").getValue(String.class);

                        if(user.equals("admin")) {
                            Intent adminActivity = new Intent(getApplicationContext(), AdminActivity.class);
                            adminActivity.putExtra("Username", user);
                            adminActivity.putExtra("Role", storedRole);
                            startActivity(adminActivity);
                        } else if (storedRole.equals("Club Owner")) {
                            Intent clubownerActivity = new Intent(getApplicationContext(), ClubOwnerActivity.class);
                            clubownerActivity.putExtra("Username", user);
                            clubownerActivity.putExtra("Role", storedRole);
                            startActivity(clubownerActivity);
                            
                        } else {
                            Intent userActivity = new Intent(getApplicationContext(), UserActivity.class);
                            userActivity.putExtra("Username", user);
                            userActivity.putExtra("Role", storedRole);
                            startActivity(userActivity);
                        }
                    } else {
                        passwordInput.setError("Invalid Password");
                        passwordInput.requestFocus();
                    }
                } else {
                    usernameInput.setError("Invalid Username");
                    usernameInput.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}