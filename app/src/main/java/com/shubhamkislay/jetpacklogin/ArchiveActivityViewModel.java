package com.shubhamkislay.jetpacklogin;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.LiveMessage;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.List;


public class ArchiveActivityViewModel extends ViewModel {

    private DatabaseReference senderRef;

    private final FirebaseQueryLiveData userLiveData;

    private String userChattingWithId,currentUser;

    public MutableLiveData<List<ChatMessage>> getChatList() {
        return userLiveData;
    }


    public ArchiveActivityViewModel(String userChattingWithId)
    {

        this.userChattingWithId = userChattingWithId;

        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        senderRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId);

        userLiveData = new FirebaseQueryLiveData(senderRef);



    }


    public void deleteMessage(ChatMessage chatMessage,Boolean deleteForAll){

        DatabaseReference deleteMessageSenderRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(currentUser)
                .child(userChattingWithId)
                .child(chatMessage.getMessageId());


        deleteMessageSenderRef.removeValue();


        if(deleteForAll) {
            final DatabaseReference deleteMessageReceiverRef = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(userChattingWithId)
                    .child(currentUser)
                    .child(chatMessage.getMessageId());

            deleteMessageReceiverRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {

                        deleteMessageReceiverRef.removeValue();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }




    }




    //Creating a livedata subclass


    public class FirebaseQueryLiveData extends MutableLiveData<List<ChatMessage>> {



        private List<ChatMessage> chatMessageList = new ArrayList<>();
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

                chatMessageList.clear();

                if(dataSnapshot.exists()) {
                    chatMessageList.clear();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()) {


                        ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);

                        chatMessageList.add(chatMessage);



                    }
                }
                setValue(chatMessageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }
    }
}
