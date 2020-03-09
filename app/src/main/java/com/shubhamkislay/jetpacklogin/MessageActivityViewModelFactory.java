package com.shubhamkislay.jetpacklogin;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

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
