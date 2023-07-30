package com.example.communityd1.DataClasses;

public class SignupRequests {
    public String name,mail,contact;

    public SignupRequests(String name, String mail, String contact) {
        this.name = name;
        this.mail = mail;
        this.contact = contact;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }
}
