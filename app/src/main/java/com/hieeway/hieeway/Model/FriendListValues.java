package com.hieeway.hieeway.Model;

import java.util.List;

public class FriendListValues {

    private List<Friend> friendList;
    private int friendRequestsCounter;


    public List<Friend> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<Friend> friendList) {
        this.friendList = friendList;
    }

    public int getFriendRequestsCounter() {
        return friendRequestsCounter;
    }

    public void setFriendRequestsCounter(int friendRequestsCounter) {
        this.friendRequestsCounter = friendRequestsCounter;
    }
}
