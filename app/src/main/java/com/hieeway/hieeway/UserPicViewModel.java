package com.hieeway.hieeway;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.User;

public class UserPicViewModel extends ViewModel {
    private DatabaseReference readRef;
    private FirebaseQueryLiveData userPic;








    public UserPicViewModel(String userchattingWithId)
    {

        readRef = FirebaseDatabase.getInstance().getReference("Users")
                .child(userchattingWithId);

        userPic = new FirebaseQueryLiveData(readRef);


    }


    public LiveData<User> getUserPic() {
        return userPic;
    }



    //Creating a livedata subclass


    public class FirebaseQueryLiveData extends LiveData<User> {

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

                    User user = dataSnapshot.getValue(User.class);
                    if(user!=null) {
                        setValue(user);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }
}