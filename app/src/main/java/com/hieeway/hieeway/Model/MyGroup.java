package com.hieeway.hieeway.Model;

public class MyGroup implements Comparable<MyGroup> {
    private String groupID;
    private String icon;
    private String groupName;
    private String timeStamp;
    private String sender;
    private String type;
    private String key;

    public MyGroup() {
    }

    public MyGroup(String groupID, String icon, String groupName) {
        this.groupID = groupID;
        this.icon = icon;
        this.groupName = groupName;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    @Override
    public int compareTo(MyGroup o) {

        try {
            return this.getTimeStamp().compareTo(o.getTimeStamp());
        } catch (Exception e) {
            return 0;
        }

    }


}
