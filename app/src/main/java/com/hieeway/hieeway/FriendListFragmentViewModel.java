package com.hieeway.hieeway;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import android.os.AsyncTask;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.FriendListValues;
import com.hieeway.hieeway.Model.SearchChatsAsyncTaskModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class FriendListFragmentViewModel extends ViewModel {
    public String userChatid;
    public List<Friend> chatStampList;
    private List<Friend> userList = new ArrayList();
    private DatabaseReference usersRef;
    private UserListLiveData userListLiveData;


    public FriendListFragmentViewModel() {

        // usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        // readUsers();
        userListLiveData = new UserListLiveData(usersRef);

        chatStampList = new ArrayList<>();

    }


    public LiveData<FriendListValues> getAllUser() {
        return userListLiveData;
    }


    public class UserListLiveData extends LiveData<FriendListValues> {

        private final Query query;
        private final MineValueEventListener listener = new MineValueEventListener();


        public UserListLiveData(DatabaseReference ref) {
            this.query = ref;
        }

        @Override
        protected void onActive() {
            //  if(userList.size()<1)

            SearchChatsAsyncTaskModel searchChatsAsyncTaskModel = new SearchChatsAsyncTaskModel(query, listener);

            // query.addValueEventListener(listener);
            new SearchFriendsAsyncTask().execute(searchChatsAsyncTaskModel);
            // firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        }

        @Override
        protected void onInactive() {

            query.removeEventListener(listener);
        }

        private class MineValueEventListener implements ValueEventListener {

            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                TaskCompletionSource<FriendListValues> taskCompletionSource = new TaskCompletionSource<>();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userList.clear();

                        int friendRequestsCounter = 0;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            try {

                                Friend friend = snapshot.getValue(Friend.class);

                                if (friend.getStatus().equals("friends")) {

                                    try {
                                        if (!friend.getFriendId().equals(firebaseUser.getUid()))
                                            userList.add(friend);

                                    } catch (Exception e) {

                                    }
                                } else if (friend.getStatus().equals("got")) {

                                    friendRequestsCounter += 1;
                                }

                            } catch (Exception e) {
                                //
                            }


                        }

                        FriendListValues friendListValues = new FriendListValues();
                        friendListValues.setFriendList(userList);
                        friendListValues.setFriendRequestsCounter(friendRequestsCounter);

                        taskCompletionSource.setResult(friendListValues);

                    }
                }).start();

                Task<FriendListValues> task = taskCompletionSource.getTask();

                task.addOnCompleteListener(new OnCompleteListener<FriendListValues>() {
                    @Override
                    public void onComplete(@NonNull Task<FriendListValues> task) {

                        if (task.isSuccessful()) {

                            setValue(task.getResult());
                        }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }

    private class SearchFriendsAsyncTask extends AsyncTask<SearchChatsAsyncTaskModel, Void, Void> {

        @Override
        protected Void doInBackground(SearchChatsAsyncTaskModel... searchChatsAsyncTaskModels) {

            SearchChatsAsyncTaskModel searchChatsAsyncTaskModel = searchChatsAsyncTaskModels[0];
            searchChatsAsyncTaskModel.getQuery().addValueEventListener(searchChatsAsyncTaskModel.getValueEventListener());


            return null;
        }
    }


}