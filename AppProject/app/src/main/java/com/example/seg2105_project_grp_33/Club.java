package com.example.seg2105_project_grp_33;

public class Club {
    private ClubOwner owner;
    private String name;

    public Club(ClubOwner C_own)
    {
        owner = C_own;
        name = C_own.getClubName();
    }

    public ClubOwner getOwner() {
        return owner;
    }

    public String getName() {
        return name;
    }
}


