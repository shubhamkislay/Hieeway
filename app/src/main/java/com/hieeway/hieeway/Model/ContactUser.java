package com.hieeway.hieeway.Model;

public class ContactUser {
    String userID;
    String name;

    public ContactUser(String userID, String name) {
        this.userID = userID;
        this.name = name;
    }

    public ContactUser() {
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
