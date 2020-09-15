package com.hieeway.hieeway;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hieeway.hieeway.Adapters.ChatMessageAdapter;
import com.hieeway.hieeway.Model.ChatMessage;
import com.hieeway.hieeway.Model.LiveMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LiveMessagingViewModel extends ViewModel {






    private DatabaseReference usersRef ;
    private final MessageLiveData messageLiveData;

    private String userChattingWithId;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;
    DatabaseReference senderChatCreateRef;
    DatabaseReference receiverChatCreateRef;

    private DatabaseReference sendderUsersRef;
    private DatabaseReference receiverUsersRef;
    private Handler handler;
    private Runnable runnable;
    private String currentUser;
    private Thread thread;
    private Thread newThread;
    private Boolean threadRunning = false;

    List<ChatMessage> messageList = new ArrayList();

    private StringBuffer liveMessage = new StringBuffer();





    public LiveMessagingViewModel(String userChattingWithId) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.userChattingWithId = userChattingWithId;



        usersRef = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(currentUser)
                .child(userChattingWithId);

        messageLiveData = new MessageLiveData(usersRef);

        sendderUsersRef = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(currentUser)
                .child(userChattingWithId);



        receiverUsersRef = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(userChattingWithId)
                .child(currentUser);


        senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(currentUser).child(userChattingWithId);

        receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userChattingWithId).child(currentUser);


        handler = new Handler();

    }


    public void iConnect()
    {
       /*HashMap<String,Object> hashMap  = new HashMap<>();
       hashMap.put("iConnect", true);


       sendderUsersRef.updateChildren(hashMap);
       receiverUsersRef.updateChildren(hashMap);*/



       sendderUsersRef.runTransaction(new Transaction.Handler() {
           @NonNull
           @Override
           public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                int value = 0;

               LiveMessage liveMessage = mutableData.getValue(LiveMessage.class);

               if (liveMessage == null) {
                   return Transaction.success(mutableData);
               }

               else
                   value = liveMessage.getiConnect();

               liveMessage.setiConnect(value+1);
               mutableData.setValue(liveMessage);




               return Transaction.success(mutableData);
           }

           @Override
           public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

           }
       });


        receiverUsersRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                int value = 0;

                LiveMessage liveMessage = mutableData.getValue(LiveMessage.class);

                if (liveMessage == null) {
                    return Transaction.success(mutableData);
                }

                else
                    value = liveMessage.getiConnect();

                liveMessage.setiConnect(value+1);
                mutableData.setValue(liveMessage);




                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

    }


    public LiveData<StringBuffer> getLiveMessage() {
        return messageLiveData;
    }


    public class MessageLiveData extends LiveData<StringBuffer> {

        private final Query query;
        private ChatMessageAdapter chatMessageAdapter;
        private final MineValueEventListener listener = new MineValueEventListener();

        public MessageLiveData(DatabaseReference ref) {
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



        private class MineValueEventListener implements ValueEventListener {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {


                    LiveMessage readMessage = dataSnapshot.getValue(LiveMessage.class);
                    liveMessage.delete(0, liveMessage.length());
                    liveMessage.append(readMessage.getMessageLive());


                    setValue(liveMessage);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }




    }
    public void updateLiveMessage(final String liveMessage1)
    {



        HashMap<String,Object> liveMessageHash = new HashMap<>();
        liveMessageHash.put("messageLive",liveMessage1);
        receiverUsersRef.updateChildren(liveMessageHash);




    }









}
