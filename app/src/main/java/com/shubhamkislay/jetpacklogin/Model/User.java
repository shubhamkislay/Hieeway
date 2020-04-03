package com.shubhamkislay.jetpacklogin.Model;

public class User {

    private String email;
    private String name;
    private String phonenumber;
    private String username;
    private String userid;
    private String photo;
    private String token;
    private Boolean online;
    private String publicKey;
    private String publicKeyId;
    private String feeling;



    public User() {
    }

    public User(String email,String name, String phonenumber, String username, String userid,String photo, String token, Boolean online, String publicKey, String publicKeyId, String feeling) {
        this.email = email;
        this.phonenumber = phonenumber;
        this.username = username;
        this.userid = userid;
        this.photo = photo;
        this.token = token;
        this.online = online;
        this.publicKey = publicKey;
        this.publicKeyId = publicKeyId;
        this.feeling = feeling;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getPublicKeyId() {
        return publicKeyId;
    }

    public void setPublicKeyId(String publicKeyId) {
        this.publicKeyId = publicKeyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }
}
