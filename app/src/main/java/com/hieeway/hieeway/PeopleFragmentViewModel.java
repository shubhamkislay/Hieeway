package com.hieeway.hieeway;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.List;


public class PeopleFragmentViewModel extends ViewModel {
    private List<User> userList = new ArrayList();


    private DatabaseReference usersRef;
    private final UserListLiveData userListLiveData;


    public PeopleFragmentViewModel() {

        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        userListLiveData = new UserListLiveData(usersRef);

    }


    public LiveData<List<User>> getAllUser() {
        return userListLiveData;
    }


    public class UserListLiveData extends LiveData<List<User>> {

        private final Query query;
        private final MineValueEventListener listener = new MineValueEventListener();


        public UserListLiveData(DatabaseReference ref) {
            this.query = ref;
        }

        @Override
        protected void onActive() {
            //  if(userList.size()<1)
            query.addValueEventListener(listener);
            // firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        }

        @Override
        protected void onInactive() {

            query.removeEventListener(listener);
        }

        private class MineValueEventListener implements ValueEventListener {

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    User user = snapshot.getValue(User.class);


                    assert user != null;
                    assert firebaseUser != null;
                    try {
                        if (!user.getUserid().equals(firebaseUser.getUid()))
                            userList.add(user);
                    }catch (Exception e)
                    {
                       // Toast.makeText(,"Internet is crawling!",Toast.LENGTH_SHORT).show();
                        Log.e("Null Pointer Userid","Check your internet connection");
                    }

                }

                setValue(userList);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }


}
