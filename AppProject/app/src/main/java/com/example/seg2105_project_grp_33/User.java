package com.example.seg2105_project_grp_33;

import com.google.firebase.database.FirebaseDatabase;

public class User
{
//User Attributes
    private String password;
    private String username;

    protected FirebaseDatabase db = FirebaseDatabase.getInstance();

//------------------------
// CONSTRUCTOR
//------------------------

    public User() {
        // Default constructor used for Firebase DataSnapshots
    }

    public User(String pass, String Uname)
  
        {
          
            password = pass;
            username = Uname;
        }
     
//------------------------
// INTERFACE
//------------------------
        
    public boolean setPassword(String pass)
    {
        boolean isSet = false;
        password = pass;
        isSet = true;
        return isSet;
    }
        
    public boolean setUsername(String Uname)
    
    {
        boolean isSet = false;
        username = Uname;
        isSet = true;
        return isSet;
    }
        
    public String getPassword()
    {
        return password;
    }
   
    public String getUsername()
    {
        return username;
    }
    
    public void delete()
    {}
        
    public String toString()
    
    {
        return super.toString() + "["+
              
        "password" + ":" + getPassword()+ "," +
                
        "username" + ":" + getUsername()+ "]";
     
    }


}

