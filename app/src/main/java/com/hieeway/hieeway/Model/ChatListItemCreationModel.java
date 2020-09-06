package com.hieeway.hieeway.Model;

import com.google.firebase.database.DatabaseReference;

public class ChatListItemCreationModel {

    private String usernameUserChattingWith;
    private String userChattingWith_photo;
    private String userChattingWith_active_photo;
    private String currentUserName;
    private String currentUserPhoto;
    private String currentUserActivePhoto;
    private DatabaseReference senderChatCreateRef;
    private DatabaseReference receiverChatCreateRef;

    public ChatListItemCreationModel(String usernameUserChattingWith, String userChattingWith_photo, String currentUserName, String currentUserPhoto, DatabaseReference senderChatCreateRef, DatabaseReference receiverChatCreateRef) {
        this.usernameUserChattingWith = usernameUserChattingWith;
        this.userChattingWith_photo = userChattingWith_photo;
        this.currentUserName = currentUserName;
        this.currentUserPhoto = currentUserPhoto;
        this.senderChatCreateRef = senderChatCreateRef;
        this.receiverChatCreateRef = receiverChatCreateRef;
    }

    public ChatListItemCreationModel() {
    }

    public String getUsernameUserChattingWith() {
        return usernameUserChattingWith;
    }

    public void setUsernameUserChattingWith(String usernameUserChattingWith) {
        this.usernameUserChattingWith = usernameUserChattingWith;
    }

    public String getUserChattingWith_photo() {
        return userChattingWith_photo;
    }

    public void setUserChattingWith_photo(String userChattingWith_photo) {
        this.userChattingWith_photo = userChattingWith_photo;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getCurrentUserPhoto() {
        return currentUserPhoto;
    }

    public void setCurrentUserPhoto(String currentUserPhoto) {
        this.currentUserPhoto = currentUserPhoto;
    }

    public DatabaseReference getSenderChatCreateRef() {
        return senderChatCreateRef;
    }

    public void setSenderChatCreateRef(DatabaseReference senderChatCreateRef) {
        this.senderChatCreateRef = senderChatCreateRef;
    }

    public DatabaseReference getReceiverChatCreateRef() {
        return receiverChatCreateRef;
    }

    public void setReceiverChatCreateRef(DatabaseReference receiverChatCreateRef) {
        this.receiverChatCreateRef = receiverChatCreateRef;
    }

    public String getCurrentUserActivePhoto() {
        return currentUserActivePhoto;
    }

    public void setCurrentUserActivePhoto(String currentUserActivePhoto) {
        this.currentUserActivePhoto = currentUserActivePhoto;
    }


    public String getUserChattingWith_active_photo() {
        return userChattingWith_active_photo;
    }

    public void setUserChattingWith_active_photo(String userChattingWith_active_photo) {
        this.userChattingWith_active_photo = userChattingWith_active_photo;
    }
}
