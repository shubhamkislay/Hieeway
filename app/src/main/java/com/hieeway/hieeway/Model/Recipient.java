package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

public class Recipient {
    private String username;
    private String userid;
    private String photo;
    private String feeling;
    private String feelingIcon;
    private long localuserstamp;
    private long otheruserstamp;
    private Boolean manual;


    public Recipient(String username, String userid, String photo, String feeling, String feelingIcon, long localuserstamp, long otheruserstamp, Boolean manual) {
        this.username = username;
        this.userid = userid;
        this.photo = photo;
        this.feeling = feeling;
        this.feelingIcon = feelingIcon;
        this.localuserstamp = localuserstamp;
        this.otheruserstamp = otheruserstamp;
        this.manual = manual;
    }

    public long getLocaluserstamp() {
        return localuserstamp;
    }

    public void setLocaluserstamp(long localuserstamp) {
        this.localuserstamp = localuserstamp;
    }

    public long getOtheruserstamp() {
        return otheruserstamp;
    }

    public void setOtheruserstamp(long otheruserstamp) {
        this.otheruserstamp = otheruserstamp;
    }

    public Recipient() {
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

    public Boolean getManual() {
        return manual;
    }

    public void setManual(Boolean manual) {
        this.manual = manual;
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        Recipient recipient = (Recipient) obj;
        return this.getUserid().equals(recipient.getUserid());
    }
}
