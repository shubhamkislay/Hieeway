package com.hieeway.hieeway;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

public class EphemeralMessageActivityViewModelFactory implements ViewModelProvider.Factory {


    private String userChattingWithUid;


    public EphemeralMessageActivityViewModelFactory(String userChattingWithUid) {
        this.userChattingWithUid = userChattingWithUid;
    }


    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new EphemeralMessageViewModel(userChattingWithUid);
    }
}

