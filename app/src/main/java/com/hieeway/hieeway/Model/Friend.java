package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.C;

public class Friend {


    private String friendId;
    private String username;
    private String status;
    private String photo;
    private String activePhoto;


    public Friend(String friendId, String status, String username, String photo, String activePhoto) {
        this.friendId = friendId;
        this.status = status;
        this.username = username;
        this.photo = photo;
        this.activePhoto = activePhoto;
    }

    public Friend() {
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public String getActivePhoto() {
        return activePhoto;
    }

    public void setActivePhoto(String activePhoto) {
        this.activePhoto = activePhoto;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        Friend o = (Friend) obj;
        return this.getFriendId().equals(o.getFriendId());
    }
}
