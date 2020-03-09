package com.shubhamkislay.jetpacklogin;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class UserPicViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;


    public UserPicViewModelFactory(String userChattingWithUid) {
        this.userChattingWithUid = userChattingWithUid;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new UserPicViewModel(userChattingWithUid);
    }
}
