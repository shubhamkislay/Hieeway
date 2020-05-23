package com.shubhamkislay.jetpacklogin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;
import com.shubhamkislay.jetpacklogin.Model.User;


public class YoutubeUrlViewModel extends ViewModel {

    private DatabaseReference readRef;
    private final FirebaseQueryLiveData userLiveData = new FirebaseQueryLiveData(readRef);
    private String userChattingWithId;


    public YoutubeUrlViewModel(String userChattingWithId) {
        this.userChattingWithId = userChattingWithId;

        readRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId);
    }

    public LiveData<String> getYoutubeUrl() {
        return userLiveData;
    }


    //Creating a livedata subclass


    public class FirebaseQueryLiveData extends LiveData<String> {

        private final Query query;
        private final MyValueEventListener listener = new MyValueEventListener();


        public FirebaseQueryLiveData(DatabaseReference ref) {
            this.query = ref;
        }

        @Override
        protected void onActive() {


            query.addValueEventListener(listener);
        }

        @Override
        protected void onInactive() {

            query.removeEventListener(listener);
        }

        private class MyValueEventListener implements ValueEventListener {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatStamp chatStamp = dataSnapshot.getValue(ChatStamp.class);


                    setValue(chatStamp.getYoutubeUrl());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }
}

