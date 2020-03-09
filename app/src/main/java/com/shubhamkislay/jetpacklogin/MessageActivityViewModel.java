package com.shubhamkislay.jetpacklogin;

import android.app.Application;
import android.app.ProgressDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.shubhamkislay.jetpacklogin.Adapters.ChatMessageAdapter;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivityViewModel extends ViewModel {



    private DatabaseReference usersRef ;
    private final MessageLiveData messageLiveData;

    private String userChattingWithId;
    private ChatMessageAdapter chatMessageAdapter;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;


    private DatabaseReference sendderUsersRef;
    private DatabaseReference receiverUsersRef;
    private Handler handler;
    private Runnable runnable;
    private String currentUser;
    private Thread thread;
    private Thread newThread;
    private Boolean threadRunning = false;

    List<ChatMessage> messageList = new ArrayList();





    public MessageActivityViewModel(String userChattingWithId, ChatMessageAdapter chatMessageAdapter)
    {
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.userChattingWithId = userChattingWithId;
        this.chatMessageAdapter = chatMessageAdapter;


        usersRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(currentUser)
                .child(userChattingWithId);

        messageLiveData = new MessageLiveData(usersRef);

        sendderUsersRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(currentUser)
                .child(userChattingWithId);



        receiverUsersRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(userChattingWithId)
                .child(currentUser);


        handler = new Handler();







    }

    void filterMessagesTobeDeleted(List<ChatMessage> chatMessageList)
    {
        if(chatMessageList!=null)
            for(final ChatMessage chatMessage:chatMessageList)
            {

                try {

                    sendderUsersRef.child(chatMessage.getMessageId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                if(chatMessage.getReceiverId().equals(currentUser))
                                    autoDeleteMessage(chatMessage);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                }catch(Exception e)
                {
                    //Handle this
                }


            }


    }


     public void sendMessage(ChatMessage chatMessage)
     {
         messageLiveData.sendMessage(chatMessage);
     }

     public void setChatAdapter(ChatMessageAdapter chatMessageAdapter)
     {

         this.chatMessageAdapter = chatMessageAdapter;
         messageLiveData.setChatAdapter(chatMessageAdapter);

     }


     public void autoDeleteMessage(final ChatMessage chatMessage)
     {

         final int TimeInterval = chatMessage.getMessageText().length()*50+1250;

         if(!threadRunning) {
             threadRunning = true;

             runnable = new Runnable() {
                 @Override
                 public void run() {

                     // Thread newThread = new Thread(runnable);
                      newThread = new Thread(new Runnable() {
                         @Override
                         public void run() {

                             HashMap<String, Object> seenhash = new HashMap<>();
                             seenhash.put("seen", "seen");

                             sendderUsersRef.child(chatMessage.getMessageId()).updateChildren(seenhash);

                             /*
                             receiverUsersRef.child(chatMessage.getMessageId()).addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                     if(dataSnapshot.exists())
                                     {
                                         HashMap<String,Object> seeingHash = new HashMap<>();
                                         seeingHash.put("seen","seeing");
                                         receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(seeingHash);
                                     }

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });
                             */
                             HashMap<String, Object> seeingHash = new HashMap<>();
                             seeingHash.put("seen", "seeing");

                             receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(seeingHash);

                             try {
                                 Thread.sleep(TimeInterval);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }

                             threadRunning = false;
                             /*try {
                                 Thread.sleep(1250);
                             } catch (InterruptedException e) {
                                 e.printStackTrace();
                             }*/
                             /*receiverUsersRef.child(chatMessage.getMessageId()).addValueEventListener(new ValueEventListener() {
                                 @Override
                                 public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                     if(dataSnapshot.exists()) {
*/                      if (chatMessage.getPhotourl().equals("none"))
                             {
                             HashMap<String, Object> seenHash = new HashMap<>();
                             seenHash.put("seen", "seen");
                             receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(seenHash);

                                     /*}

                                 }

                                 @Override
                                 public void onCancelled(@NonNull DatabaseError databaseError) {

                                 }
                             });*/


                                 sendderUsersRef.child(chatMessage.getMessageId()).addValueEventListener(new ValueEventListener() {
                                     @Override
                                     public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                         if (dataSnapshot.exists())
                                             sendderUsersRef.child(chatMessage.getMessageId()).removeValue();

                                     }

                                     @Override
                                     public void onCancelled(@NonNull DatabaseError databaseError) {

                                     }
                                 });

                         }



                         }
                     });

                     newThread.start();
                 if(!threadRunning)
                 {
                     newThread.interrupt();

                 }


                 }
             };

             handler.post(runnable);
         }




     }

     public void stopAutodelete()
     {
         handler.removeCallbacks(runnable);
//         newThread.interrupt();
         threadRunning = false;

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




    public LiveData<List<ChatMessage>> getAllMessages(){ return messageLiveData;}



    public class MessageLiveData extends LiveData<List<ChatMessage>>
    {

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

        public void setChatAdapter(ChatMessageAdapter chatMessageAdapter)
        {
            this.chatMessageAdapter = chatMessageAdapter;
        }

        private class MineValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            messageList.clear();
            if(dataSnapshot.exists()) {
                messageList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()) {

                    ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                    messageList.add(chatMessage);
                    try {

                       /* if (chatMessage.getReceiverId().equals(currentUser)) {

                            HashMap<String, Object> updateStatusHash = new HashMap<>();
                            updateStatusHash.put("sentStatus", "sent");
*/

                       //     receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(updateStatusHash);


                      //  }
                    }catch(Exception e)
                    {
                        //Write the solution to handle the solution here


                        chatMessage = null;
                        messageList.remove(chatMessage);

                        chatMessageAdapter.notifyDataSetChanged();

                    }
                }
            }
            setValue(messageList);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {


        }
    }

        public void sendMessage(final ChatMessage chatMessage)
        {


            final String messageKey = sendderUsersRef.push().getKey();

            chatMessage.setSenderId(currentUser);
            chatMessage.setReceiverId(userChattingWithId);
            chatMessage.setMessageId(messageKey);

            final HashMap<String,  Object> sendMessageHash = new HashMap<>();
            sendMessageHash.put("senderId",chatMessage.getSenderId());
            sendMessageHash.put("receiverId",chatMessage.getReceiverId());
            sendMessageHash.put("messageId",chatMessage.getMessageId());
            sendMessageHash.put("messageText",chatMessage.getMessageText());
          //  sendMessageHash.put("sentStatus",chatMessage.getSentStatus());
            sendMessageHash.put("seen", chatMessage.getSeen());
            sendMessageHash.put("photourl",chatMessage.getPhotourl());





            sendderUsersRef.child(messageKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {


                    if(task.isSuccessful()) {
                        sendderUsersRef.child(messageKey).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()) {



                                    final HashMap<String, Object> sentHash = new HashMap<>();
                                    sentHash.put("sentStatus", "sent");
                                    sendderUsersRef.child(messageKey).updateChildren(sentHash);

                                }


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });



                    }

                }
            });
            receiverUsersRef.child(messageKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {

                        final HashMap<String, Object> sentHash = new HashMap<>();
                        sentHash.put("sentStatus", "sent");
                        receiverUsersRef.child(messageKey).updateChildren(sentHash);

                    }
                }
            });


        }


    }



}
