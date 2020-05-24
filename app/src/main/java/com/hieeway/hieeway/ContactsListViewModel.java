package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.ContactUser;

import java.util.ArrayList;
import java.util.List;


public class ContactsListViewModel extends ViewModel {

    List<ContactUser> contactUserList = new ArrayList<>();

    private DatabaseReference readRef = FirebaseDatabase.getInstance().getReference("Contacts").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private final ContactsListViewModel.FirebaseQueryLiveData userLiveData = new ContactsListViewModel.FirebaseQueryLiveData(readRef);

    public LiveData<List<ContactUser>> getContacts() {
        return userLiveData;
    }


    //Creating a livedata subclass


    public class FirebaseQueryLiveData extends LiveData<List<ContactUser>> {

        private final Query query;
        private final ContactsListViewModel.FirebaseQueryLiveData.MyValueEventListener listener = new ContactsListViewModel.FirebaseQueryLiveData.MyValueEventListener();


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
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        ContactUser user = snapshot.getValue(ContactUser.class);
                        contactUserList.add(user);

                    }
                    setValue(contactUserList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }
}
