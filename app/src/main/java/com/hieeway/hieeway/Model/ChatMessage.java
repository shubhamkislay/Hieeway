package com.hieeway.hieeway.Model;

public class ChatMessage {


    private String senderId;
    private String receiverId;
    private String messageText;
    private String messageId;
    private String sentStatus;
    private String gotReplyID;
    private String seen;
    private String photourl;
    private String audiourl;
    private String videourl;
    private int textSize;
    private String replyID;
    private Boolean replyTag;
    private String publicKeyID;
    private String mediaKey;

    private Boolean showReplyMsg;
    private String replyMsg;
    private String gotReplyMsg;
    private Boolean showGotReplyMsg;
    private String activePhoto;


    private String senderReplyMessage;
    private String timeStamp;


    /**
     * New changes
     *
     * Adding support for message chaining.
     * **/
     //begin

    private Boolean ifMessageTwo;
    private Boolean ifMessageThree;
    private String messageTextTwo;
    private String messageTextThree;



    //end




    public ChatMessage() {
    }


    public ChatMessage(String senderId, String receiverId, String messageText,
                       String messageId, String sentStatus, String seen, String photourl, String replyID,
                       String senderReplyMessage, String messageTextTwo, String messageTextThree,
                       Boolean ifMessageTwo, Boolean ifMessageThree, int textSize, Boolean replyTag, String gotReplyID,
                       Boolean showReplyMsg, Boolean showGotReplyMsg, String replyMsg, String gotReplyMsg, String publicKeyId,
                       String timeStamp, String audiourl, String videourl, String mediaKey, String activePhoto)
    {

        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageText = messageText;
        this.messageId = messageId;
        /*this.sentStatus = sentStatus;*/
        this.seen=seen;
        this.photourl = photourl;
        this.replyID = replyID;
        this.senderReplyMessage = senderReplyMessage;

        this.messageTextTwo = messageTextTwo;
        this.messageTextThree = messageTextThree;
        this.ifMessageTwo = ifMessageTwo;
        this.ifMessageThree = ifMessageThree;

        this.showReplyMsg = showReplyMsg;
        this.showGotReplyMsg = showGotReplyMsg;
        this.replyMsg = replyMsg;
        this.gotReplyMsg = gotReplyMsg;
        this.activePhoto = activePhoto;


        this.textSize = textSize;

        this.replyTag = replyTag;

        this.gotReplyID = gotReplyID;
        this.publicKeyID = publicKeyId;
        this.timeStamp= timeStamp;

        this.audiourl = audiourl;
        this.videourl = videourl;

        this.mediaKey = mediaKey;



    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSentStatus() {
        return sentStatus;
    }

    public void setSentStatus(String sentStatus) {
        this.sentStatus = sentStatus;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getReplyID() {
        return replyID;
    }

    public void setReplyID(String replyID) {
        this.replyID = replyID;
    }

    public String getSenderReplyMessage() {
        return senderReplyMessage;
    }

    public void setSenderReplyMessage(String senderReplyMessage) {
        this.senderReplyMessage = senderReplyMessage;
    }

    public String getMessageTextTwo() {
        return messageTextTwo;
    }
    public String getMessageTextThree() {
        return messageTextThree;
    }

    public void setMessageTextTwo(String messageTextTwo) {
        this.messageTextTwo = messageTextTwo;
    }


    public void setMessageTextThree(String messageTextThree) {
        this.messageTextThree = messageTextThree;
    }


    public Boolean getIfMessageTwo() {
        return ifMessageTwo;
    }

    public void setIfMessageTwo(Boolean ifMessageTwo) {
        this.ifMessageTwo = ifMessageTwo;
    }

    public Boolean getIfMessageThree() {
        return ifMessageThree;
    }

    public void setIfMessageThree(Boolean ifMessageThree) {
        this.ifMessageThree = ifMessageThree;
    }


    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public Boolean getReplyTag() {
        return replyTag;
    }

    public void setReplyTag(Boolean replyTag) {
        this.replyTag = replyTag;
    }

    public String getGotReplyID() {
        return gotReplyID;
    }

    public void setGotReplyID(String gotReplyID) {
        this.gotReplyID = gotReplyID;
    }

    public Boolean getShowReplyMsg() {
        return showReplyMsg;
    }

    public void setShowReplyMsg(Boolean showReplyMsg) {
        this.showReplyMsg = showReplyMsg;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getGotReplyMsg() {
        return gotReplyMsg;
    }

    public void setGotReplyMsg(String gotReplyMsg) {
        this.gotReplyMsg = gotReplyMsg;
    }

    public Boolean getShowGotReplyMsg() {
        return showGotReplyMsg;
    }

    public void setShowGotReplyMsg(Boolean showGotReplyMsg) {
        this.showGotReplyMsg = showGotReplyMsg;
    }

    public String getPublicKeyID() {
        return publicKeyID;
    }

    public void setPublicKeyID(String publicKeyID) {
        this.publicKeyID = publicKeyID;
    }


    public String getAudiourl() {
        return audiourl;
    }

    public void setAudiourl(String audiourl) {
        this.audiourl = audiourl;
    }

    public String getVideourl() {
        return videourl;
    }

    public void setVideourl(String videourl) {
        this.videourl = videourl;
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getActivePhoto() {
        return activePhoto;
    }

    public void setActivePhoto(String activePhoto) {
        this.activePhoto = activePhoto;
    }
}
