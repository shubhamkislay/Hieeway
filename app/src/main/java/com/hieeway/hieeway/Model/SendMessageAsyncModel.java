package com.hieeway.hieeway.Model;

import com.google.firebase.database.DatabaseReference;

public class SendMessageAsyncModel {

    private ChatMessage chatMessageSenderCopy;
    private ChatMessage chatMessageReceiverCopy;
    private DatabaseReference senderReference;
    private DatabaseReference receiverReference;
    private String currentID;
    private String userChattingWithId;
    private ChatListItemCreationModel chatListItemCreationModel;

    public SendMessageAsyncModel(ChatMessage chatMessageSenderCopy, ChatMessage chatMessageReceiverCopy,  DatabaseReference senderReference, DatabaseReference receiverReference, String currentID, String userChattingWithId, ChatListItemCreationModel chatListItemCreationModel) {
        this.chatMessageSenderCopy = chatMessageSenderCopy;
        this.senderReference = senderReference;
        this.receiverReference = receiverReference;
        this.currentID = currentID;
        this.userChattingWithId = userChattingWithId;
        this.chatListItemCreationModel = chatListItemCreationModel;
        this.chatMessageReceiverCopy = chatMessageReceiverCopy;
    }

    public SendMessageAsyncModel() {
    }

    public ChatMessage getChatMessageSenderCopy() {
        return chatMessageSenderCopy;
    }

    public void setChatMessageSenderCopy(ChatMessage chatMessageSenderCopy) {
        this.chatMessageSenderCopy = chatMessageSenderCopy;
    }

    public DatabaseReference getSenderReference() {
        return senderReference;
    }

    public void setSenderReference(DatabaseReference senderReference) {
        this.senderReference = senderReference;
    }

    public DatabaseReference getReceiverReference() {
        return receiverReference;
    }

    public void setReceiverReference(DatabaseReference receiverReference) {
        this.receiverReference = receiverReference;
    }

    public String getCurrentID() {
        return currentID;
    }

    public void setCurrentID(String currentID) {
        this.currentID = currentID;
    }

    public String getUserChattingWithId() {
        return userChattingWithId;
    }

    public void setUserChattingWithId(String userChattingWithId) {
        this.userChattingWithId = userChattingWithId;
    }

    public ChatListItemCreationModel getChatListItemCreationModel() {
        return chatListItemCreationModel;
    }

    public void setChatListItemCreationModel(ChatListItemCreationModel chatListItemCreationModel) {
        this.chatListItemCreationModel = chatListItemCreationModel;
    }

    public ChatMessage getChatMessageReceiverCopy() {
        return chatMessageReceiverCopy;
    }

    public void setChatMessageReceiverCopy(ChatMessage chatMessageReceiverCopy) {
        this.chatMessageReceiverCopy = chatMessageReceiverCopy;
    }
}
