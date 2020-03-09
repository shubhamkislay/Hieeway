package com.shubhamkislay.jetpacklogin;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class ArchiveActivityViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;


    public ArchiveActivityViewModelFactory(String userChattingWithUid) {
        this.userChattingWithUid = userChattingWithUid;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ArchiveActivityViewModel(userChattingWithUid);
    }
}
