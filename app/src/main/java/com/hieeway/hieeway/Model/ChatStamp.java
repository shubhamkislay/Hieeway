package com.hieeway.hieeway.Model;

public class ChatStamp implements Comparable<ChatStamp>{



    private String timeStamp;
    private String id;
    private String photo;
    private String seen;
    private String username;
    private Boolean chatPending;
    private int gemCount;
    private String youtubeUrl;


    public ChatStamp() {
    }


    public ChatStamp(String timeStamp, String id, String photo, String username, String seen, Boolean chatPending, int gemCount, String youtubeUrl) {
        this.timeStamp = timeStamp;
        this.id = id;
        this.photo = photo;
        this.username = username;
        this.seen = seen;
        this.chatPending = chatPending;
        this.gemCount = gemCount;
        this.youtubeUrl = youtubeUrl;

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
        return this.getTimeStamp().compareTo(o.getTimeStamp());
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
}
