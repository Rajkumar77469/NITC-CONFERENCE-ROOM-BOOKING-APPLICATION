package com.example.communityd1.DataClasses;

public class User {

    String email, name, contact;

    public User(String email,String name, String contact) {
        this.email = email;
        this.name = name;
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
