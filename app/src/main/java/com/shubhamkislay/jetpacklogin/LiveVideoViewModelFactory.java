package com.shubhamkislay.jetpacklogin;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.shubhamkislay.jetpacklogin.Adapters.ChatMessageAdapter;

public class LiveVideoViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;


    public LiveVideoViewModelFactory(String userChattingWithUid) {
        this.userChattingWithUid = userChattingWithUid;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LiveVideoViewModel(userChattingWithUid);
    }
}


