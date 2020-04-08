package com.shubhamkislay.jetpacklogin;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import com.shubhamkislay.jetpacklogin.Adapters.ChatMessageAdapter;

public class MessageActivityViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;
    private ChatMessageAdapter chatMessageAdapter;

    public MessageActivityViewModelFactory(String userChattingWithUid, ChatMessageAdapter chatMessageAdapter) {
        this.userChattingWithUid = userChattingWithUid;
        this.chatMessageAdapter = chatMessageAdapter;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MessageActivityViewModel(userChattingWithUid,chatMessageAdapter);
    }
}
