package com.shubhamkislay.jetpacklogin;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

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
