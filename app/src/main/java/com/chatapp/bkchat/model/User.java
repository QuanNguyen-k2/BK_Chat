package com.chatapp.bkchat.model;

public class User {
    public String username;
    public String email;
    public  String description;

    public User() {
    }

    public User(String username, String email,String description) {
        this.username = username;
        this.email = email;
        this.description=description;
    }


}
