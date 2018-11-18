package com.example.owner.real_final.database;

/**
 * Created by vns on 2018-03-24.
 */

public class User {

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    //public String username;
    public String email;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User( String email) {
        //this.username = username;
        this.email = email;
    }

}
