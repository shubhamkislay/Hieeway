package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class YoutubeUrlViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;


    public YoutubeUrlViewModelFactory(String userChattingWithUid) {
        this.userChattingWithUid = userChattingWithUid;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new YoutubeUrlViewModel(userChattingWithUid);
    }
}
