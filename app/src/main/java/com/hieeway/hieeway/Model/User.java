package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

import com.spotify.protocol.types.ImageUri;

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
    private String bio;
    private String feelingIcon;
    private Boolean synced;
    private String spotifyId;
    private String spotifySong;
    private String spotifyArtist;
    private ImageUri spotifyCover;
    private Boolean cloud;
    private Boolean deleteUser;



    public User() {
    }

    public User(String email, String name, String phonenumber, String username, String userid, String photo, String token, Boolean online, String publicKey,
                String publicKeyId, String feeling, String bio, String feelingIcon, Boolean synced,
                String spotifyId, String spotifySong, String spotifyArtist, ImageUri spotifyCover, Boolean cloud, Boolean deleteUser) {
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
        this.bio = bio;
        this.feelingIcon = feelingIcon;
        this.synced = synced;
        this.spotifyId = spotifyId;
        this.spotifySong = spotifySong;
        this.spotifyArtist = spotifyArtist;
        this.spotifyCover = spotifyCover;
        this.cloud = cloud;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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

    public String getFeelingIcon() {
        return feelingIcon;
    }

    public void setFeelingIcon(String feelingIcon) {
        this.feelingIcon = feelingIcon;
    }

    public Boolean getSynced() {
        return synced;
    }

    public void setSynced(Boolean synced) {
        this.synced = synced;
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getSpotifySong() {
        return spotifySong;
    }

    public void setSpotifySong(String spotifySong) {
        this.spotifySong = spotifySong;
    }

    public String getSpotifyArtist() {
        return spotifyArtist;
    }

    public void setSpotifyArtist(String spotifyArtist) {
        this.spotifyArtist = spotifyArtist;
    }

    public ImageUri getSpotifyCover() {
        return spotifyCover;
    }

    public void setSpotifyCover(ImageUri spotifyCover) {
        this.spotifyCover = spotifyCover;
    }

    public Boolean getDeleteUser() {
        return deleteUser;
    }

    public void setDeleteUser(Boolean deleteUser) {
        this.deleteUser = deleteUser;
    }

    public Boolean getCloud() {
        return cloud;
    }

    public void setCloud(Boolean cloud) {
        this.cloud = cloud;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // return super.equals(obj);
        User user = (User) obj;

        if (userid.equals(user.getUserid()))
            return true;
        else
            return false;
    }
}
