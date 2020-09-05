package com.hieeway.hieeway.Model;

public class LiveMessage {


    private String messageLive;
    private String messageKey;
    private int iConnect;
    private String senderId;
    private String loadedVideo;


    public LiveMessage()
    {}


    public LiveMessage(String messageLive, String messageKey, int iConnect, String senderId, String loadedVideo)
    {

        this.messageLive = messageLive;
        this.messageKey = messageKey;
        this.iConnect = iConnect;
        this.senderId = senderId;
        this.loadedVideo = loadedVideo;

    }

    public String getMessageLive() {
        return messageLive;
    }

    public void setMessageLive(String messageLive) {
        this.messageLive = messageLive;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }


    public int getiConnect() {
        return iConnect;
    }

    public void setiConnect(int iConnect) {
        this.iConnect = iConnect;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getLoadedVideo() {
        return loadedVideo;
    }

    public void setLoadedVideo(String loadedVideo) {
        this.loadedVideo = loadedVideo;
    }
}
