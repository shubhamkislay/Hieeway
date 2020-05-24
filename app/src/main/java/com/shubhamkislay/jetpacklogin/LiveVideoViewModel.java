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
import com.shubhamkislay.jetpacklogin.Model.LiveMessage;


public class LiveVideoViewModel extends ViewModel {
    private DatabaseReference readRef;



    private final FirebaseQueryLiveData videoLiveData;




    public LiveVideoViewModel(String userchattingWithId)
    {

        readRef = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userchattingWithId);
        videoLiveData = new FirebaseQueryLiveData(readRef);

    }


    public LiveData<LiveMessage> getLiveVideoData() {
        return videoLiveData;
    }



    //Creating a livedata subclass


    public class FirebaseQueryLiveData extends LiveData<LiveMessage> {

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
                if(dataSnapshot.exists()) {

                    LiveMessage liveMessage = dataSnapshot.getValue(LiveMessage.class);
                    //   if(liveMessage!=null)
                    setValue(liveMessage);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }
}

