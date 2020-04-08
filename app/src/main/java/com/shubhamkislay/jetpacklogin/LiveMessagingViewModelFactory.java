package com.shubhamkislay.jetpacklogin;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class LiveMessagingViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;


    public LiveMessagingViewModelFactory(String userChattingWithUid) {
        this.userChattingWithUid = userChattingWithUid;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new LiveMessagingViewModel(userChattingWithUid);
    }
}

