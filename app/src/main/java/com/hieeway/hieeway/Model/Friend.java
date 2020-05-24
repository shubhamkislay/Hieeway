package com.hieeway.hieeway.Model;

public class Friend {


    private String friendId;
    private String username;
    private String status;


    public Friend(String friendId, String status, String username) {
        this.friendId = friendId;
        this.status = status;
        this.username = username;
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
}
