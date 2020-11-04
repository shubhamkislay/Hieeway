package com.hieeway.hieeway;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import android.content.SharedPreferences;
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
import com.hieeway.hieeway.Model.SearchChatsAsyncTaskModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class ChatsFragmentViewModel extends ViewModel {
    private List<ChatStamp> userList = new ArrayList();

    public String userChatid;


    private DatabaseReference usersRef;
    private UserListLiveData userListLiveData;
    public List<ChatStamp> chatStampList;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    private String userID;


    public ChatsFragmentViewModel() {

        // usersRef = FirebaseDatabase.getInstance().getReference("Users");
        usersRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        // readUsers();
        userListLiveData = new UserListLiveData(usersRef);

        chatStampList = new ArrayList<>();

    }


    void readUsers()
    {



        final DatabaseReference chatListRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());



        chatListRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                chatStampList.clear();

                if(dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ChatStamp chatStamp = snapshot.getValue(ChatStamp.class);

                        chatStampList.add(chatStamp);

                    }

                    Collections.reverse(chatStampList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


     //   sortUsers();




    }




    public LiveData<List<ChatStamp>> getAllUser() {
        return userListLiveData;
    }


    public class UserListLiveData extends LiveData<List<ChatStamp>> {

        private final Query query;
        private final MineValueEventListener listener = new MineValueEventListener();


        public UserListLiveData(DatabaseReference ref) {
            this.query = ref;
        }

        @Override
        protected void onActive() {
            //  if(userList.size()<1)

            SearchChatsAsyncTaskModel searchChatsAsyncTaskModel = new SearchChatsAsyncTaskModel(query,listener);

           // query.addValueEventListener(listener);
            new SearchChatsAsyncTask().execute(searchChatsAsyncTaskModel);
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


                TaskCompletionSource<List<ChatStamp>> taskCompletionSource = new TaskCompletionSource<>();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        userList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            try {

                                ChatStamp chatStamp = snapshot.getValue(ChatStamp.class);

                                try {
                                    if (!chatStamp.getId().equals(firebaseUser.getUid()))
                                        userList.add(chatStamp);

                                }catch (Exception e) {

                                }

                            }catch (Exception e) {
                                //
                            }


                        }

                        taskCompletionSource.setResult(userList);

                    }
                }).start();

                Task<List<ChatStamp>> task = taskCompletionSource.getTask();

                task.addOnCompleteListener(new OnCompleteListener<List<ChatStamp>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<ChatStamp>> task) {

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

    private class SearchChatsAsyncTask extends AsyncTask<SearchChatsAsyncTaskModel,Void,Void>
    {

        @Override
        protected Void doInBackground(SearchChatsAsyncTaskModel... searchChatsAsyncTaskModels) {

            SearchChatsAsyncTaskModel searchChatsAsyncTaskModel = searchChatsAsyncTaskModels[0]
;
            searchChatsAsyncTaskModel.getQuery().addValueEventListener(searchChatsAsyncTaskModel.getValueEventListener());


            return null;
        }
    }


}