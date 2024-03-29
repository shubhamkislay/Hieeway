package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

import java.sql.Timestamp;

public class ChatStamp implements Comparable<ChatStamp>{



    private String timeStamp;
    private String id;
    private String photo;
    private String seen;
    private String username;
    private Boolean chatPending;
    private int gemCount;
    private String youtubeUrl;
    private float videoSec;
    private Boolean present;
    private long localuserstamp;
    private long otheruserstamp;
    private String activePhoto;


    public ChatStamp() {
    }


    public ChatStamp(String timeStamp, String id, String photo, String username, String seen, Boolean chatPending,
                     int gemCount, String youtubeUrl, float videoSec, Boolean present, long localuserstamp, long otheruserstamp, String activePhoto) {
        this.timeStamp = timeStamp;
        this.id = id;
        this.photo = photo;
        this.username = username;
        this.seen = seen;
        this.chatPending = chatPending;
        this.gemCount = gemCount;
        this.youtubeUrl = youtubeUrl;
        this.videoSec = videoSec;
        this.present = present;
        this.localuserstamp = localuserstamp;
        this.otheruserstamp = otheruserstamp;
        this.activePhoto = activePhoto;



    }


    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public Boolean getChatPending() {
        return chatPending;
    }

    public void setChatPending(Boolean chatPending) {
        this.chatPending = chatPending;
    }

    @Override
    public int compareTo(ChatStamp o) {


        try {
            return this.getTimeStamp().compareTo(o.getTimeStamp());
        } catch (Exception e) {
            return 0;
        }

    }




    public int getGemCount() {
        return gemCount;
    }

    public void setGemCount(int gemCount) {
        this.gemCount = gemCount;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public float getVideoSec() {
        return videoSec;
    }

    public void setVideoSec(float videoSec) {
        this.videoSec = videoSec;
    }

    public Boolean getPresent() {
        return present;
    }

    public void setPresent(Boolean present) {
        this.present = present;
    }

    public long getLocaluserstamp() {
        return localuserstamp;
    }

    public void setLocaluserstamp(long localuserstamp) {
        this.localuserstamp = localuserstamp;
    }

    public String getActivePhoto() {
        return activePhoto;
    }

    public void setActivePhoto(String activePhoto) {
        this.activePhoto = activePhoto;
    }

    public long getOtheruserstamp() {
        return otheruserstamp;
    }

    public void setOtheruserstamp(long otheruserstamp) {
        this.otheruserstamp = otheruserstamp;
    }
}
