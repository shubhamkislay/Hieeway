package com.hieeway.hieeway;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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
import com.hieeway.hieeway.Model.ChatStamp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EphemeralMessageViewModel extends ViewModel {






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
    private boolean chatListExists = false;


    public EphemeralMessageViewModel(String userChattingWithId) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        this.userChattingWithId = userChattingWithId;



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


        senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(currentUser).child(userChattingWithId);

        receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userChattingWithId).child(currentUser);


        handler = new Handler();

    }

    void filterMessagesTobeDeleted(List<ChatMessage> chatMessageList) {
        if(chatMessageList!=null)
            for(final ChatMessage chatMessage:chatMessageList)
            {

                try {

                    sendderUsersRef.child(chatMessage.getMessageId()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                //if(chatMessage.getReceiverId().equals(currentUser))
                                    //autoDeleteMessage(chatMessage);
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




    public void sendMessage(ChatMessage chatMessage) {
        messageLiveData.sendMessage(chatMessage);
    }




    public void autoDeleteMessage(final ChatMessage chatMessage) {

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

/*                            HashMap<String, Object> seenhash = new HashMap<>();
                            seenhash.put("seen", "seen");

                            receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(seenhash);*/

                            try {
                                Thread.sleep(TimeInterval);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }



                     if (chatMessage.getPhotourl().equals("none")) {

                                HashMap<String, Object> seenHash = new HashMap<>();
                                seenHash.put("seen", "seen");
                                receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(seenHash);



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
                            threadRunning = false;

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


    public Boolean isThreadRunning()
    {
        return threadRunning;
    }


    public void stopAutodelete()
    {
        handler.removeCallbacks(runnable);
//         newThread.interrupt();
        threadRunning = false;

    }







    public void deleteMessage(ChatMessage chatMessage,Boolean deleteForAll){

        new Thread(new Runnable() {
            @Override
            public void run() {


                DatabaseReference deleteMessageSenderRef = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(currentUser)
                        .child(userChattingWithId)
                        .child(chatMessage.getMessageId());

                if (!deleteForAll) {

                    DatabaseReference SeenMessageSenderRef = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(userChattingWithId)
                            .child(currentUser)
                            .child(chatMessage.getMessageId());

                    HashMap<String, Object> updateSeen = new HashMap<>();
                    updateSeen.put("seen", "seen");
                    updateSeen.put("sentStatus", "sent");

                    SeenMessageSenderRef.updateChildren(updateSeen);

                    //  deleteMessageSenderRef.updateChildren(updateSeen);


                }

                deleteMessageSenderRef.removeValue();

                // setChatPending(false);


                DatabaseReference seenChat = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(currentUser)
                        .child(userChattingWithId);


                HashMap<String, Object> hashMap = new HashMap<>();


                hashMap.put("seen", "seen");
                seenChat.updateChildren(hashMap);


                if (deleteForAll) {
                    final DatabaseReference deleteMessageReceiverRef = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(userChattingWithId)
                            .child(currentUser)
                            .child(chatMessage.getMessageId());

                    deleteMessageReceiverRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                deleteMessageReceiverRef.removeValue();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }


            }
        }).start();


    }




    public LiveData<List<ChatMessage>> getAllMessages(){ return messageLiveData;}

    public void setChatPending(final Boolean chatPending) {


        senderChatCreateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (dataSnapshot.exists()) {
                            HashMap<String, Object> timeStampHashReceiver = new HashMap<>();
                            timeStampHashReceiver.put("chatPending", chatPending);


                            senderChatCreateRef.updateChildren(timeStampHashReceiver);
                        }
                    }
                }).start();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void createChatListItem(final String usernameUserChattingWith, final String userChattingWith_photo, final String currentUserName, final String currentUserPhoto) {

        Long tsLong = System.currentTimeMillis() / 1000;
        final String ts = tsLong.toString();

        final HashMap<String, Object> timeStampHash = new HashMap<>();

        final HashMap<String, Object> timeStampHashReceiver = new HashMap<>();


        senderChatCreateRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                ChatStamp chatStamp = mutableData.getValue(ChatStamp.class);

                if (chatStamp == null) {

                    timeStampHash.put("timeStamp", ts);
                    timeStampHash.put("id", userChattingWithId);
                    timeStampHash.put("username", usernameUserChattingWith);
                    timeStampHash.put("photo", userChattingWith_photo);
                    timeStampHash.put("seen", "notseen");
                    timeStampHash.put("chatPending", false);
                    timeStampHash.put("gemCount", 2);

                    timeStampHashReceiver.put("timeStamp", ts);
                    timeStampHashReceiver.put("id", currentUser);
                    timeStampHashReceiver.put("username", currentUserName);
                    timeStampHashReceiver.put("photo", currentUserPhoto);
                    timeStampHashReceiver.put("seen", "notseen");
                    timeStampHashReceiver.put("chatPending", true);
                    timeStampHashReceiver.put("gemCount", 2);
                    senderChatCreateRef.updateChildren(timeStampHash);
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                } else {
                    timeStampHash.put("timeStamp", ts);
                    timeStampHash.put("id", userChattingWithId);
                    timeStampHash.put("username", usernameUserChattingWith);
                    timeStampHash.put("photo", userChattingWith_photo);
                    timeStampHash.put("seen", "notseen");
                    timeStampHash.put("chatPending", false);

                    timeStampHashReceiver.put("timeStamp", ts);
                    timeStampHashReceiver.put("id", currentUser);
                    timeStampHashReceiver.put("username", currentUserName);
                    timeStampHashReceiver.put("photo", currentUserPhoto);
                    timeStampHashReceiver.put("seen", "notseen");
                    timeStampHashReceiver.put("chatPending", true);

                    senderChatCreateRef.updateChildren(timeStampHash);
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                }

                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });


    }

    public class MessageLiveData extends LiveData<List<ChatMessage>>
    {

        private final Query query;
        private ChatMessageAdapter chatMessageAdapter;
        private final MessageLiveData.MineValueEventListener listener = new MessageLiveData.MineValueEventListener();

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

        public void sendMessage(final ChatMessage chatMessage) {


            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String messageKey = sendderUsersRef.push().getKey();

                    chatMessage.setSenderId(currentUser);
                    chatMessage.setReceiverId(userChattingWithId);
                    chatMessage.setMessageId(messageKey);

                    final HashMap<String, Object> sendMessageHash = new HashMap<>();
                    sendMessageHash.put("senderId", chatMessage.getSenderId());
                    sendMessageHash.put("receiverId", chatMessage.getReceiverId());
                    sendMessageHash.put("messageId", chatMessage.getMessageId());
                    sendMessageHash.put("messageText", chatMessage.getMessageText());
                    sendMessageHash.put("sentStatus", chatMessage.getSentStatus());
                    sendMessageHash.put("seen", chatMessage.getSeen());
                    sendMessageHash.put("photourl", chatMessage.getPhotourl());
                    sendMessageHash.put("textSize", chatMessage.getTextSize());
                    sendMessageHash.put("replyID", chatMessage.getReplyID());
                    sendMessageHash.put("gotReplyID", chatMessage.getReplyID());
                    sendMessageHash.put("senderReplyMessage", chatMessage.getSenderReplyMessage());
                    sendMessageHash.put("replyTag", chatMessage.getReplyTag());
                    sendMessageHash.put("ifMessageTwo", chatMessage.getIfMessageTwo());
                    sendMessageHash.put("messageTextTwo", chatMessage.getMessageTextTwo());
                    sendMessageHash.put("ifMessageThree", chatMessage.getIfMessageThree());
                    sendMessageHash.put("messageTextThree", chatMessage.getMessageTextThree());
                    sendMessageHash.put("showReplyMsg", chatMessage.getShowReplyMsg());
                    sendMessageHash.put("replyMsg", chatMessage.getReplyMsg());
                    sendMessageHash.put("showGotReplyMsg", chatMessage.getShowGotReplyMsg());
                    sendMessageHash.put("gotReplyMsg", chatMessage.getGotReplyMsg());


                    final DatabaseReference messageReportSender = FirebaseDatabase.getInstance().getReference("ReportMessages")
                            .child(currentUser)
                            .child(userChattingWithId);

                    DatabaseReference messageReportReceiver = FirebaseDatabase.getInstance().getReference("ReportMessages")
                            .child(userChattingWithId)
                            .child(currentUser);

                    if (!chatMessage.getReplyID().equals("none")) {
                        DatabaseReference messageReportUpdateReply = FirebaseDatabase.getInstance().getReference("Messages")
                                .child(userChattingWithId)
                                .child(currentUser);

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("gotReplyID", messageKey);
                        hashMap.put("seen", "seen");

                        messageReportUpdateReply.child(chatMessage.getReplyID()).updateChildren(hashMap);
                    }


                    sendderUsersRef.child(messageKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            HashMap<String, Object> sentHash = new HashMap<>();
                            sentHash.put("sentStatus", "sent");
                            // sentHash.put("messageId",messageKey);


                            sendderUsersRef.child(messageKey).updateChildren(sentHash);

                        }
                    });
                    receiverUsersRef.child(messageKey).updateChildren(sendMessageHash);

          /*  messageReportSender.child(messageKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    HashMap<String,Object> sentHash = new HashMap<>();
                    sentHash.put("sentStatus","sent");
                   // sentHash.put("messageId",messageKey);


                 //   sendderUsersRef.child(messageKey).updateChildren(sentHash);

                }
            });
            messageReportReceiver.child(messageKey).updateChildren(sendMessageHash);*/


            /* DatabaseReference senstStatusRef = FirebaseDatabase.getInstance().getReference("SentStatus")
                    .child(userChattingWithId)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(chatMessage.getMessageId());

            DatabaseReference senstStatusRevRef = FirebaseDatabase.getInstance().getReference("SentStatus")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userChattingWithId)
                    .child(chatMessage.getMessageId());

            HashMap<String,Object> senthash = new HashMap<>();

            senthash.put("sentStatus","sending");
            senstStatusRef.updateChildren(senthash);
            senstStatusRevRef.updateChildren(senthash);*/

                }
            }).start();


        }

        private class MineValueEventListener implements ValueEventListener {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                TaskCompletionSource<List<ChatMessage>> listTaskCompletionSource = new TaskCompletionSource<>();

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        messageList.clear();
                        if (dataSnapshot.exists()) {
                            messageList.clear();

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                                messageList.add(chatMessage);
                                try {

                       /* if (chatMessage.getReceiverId().equals(currentUser)) {

                            HashMap<String, Object> updateStatusHash = new HashMap<>();
                            updateStatusHash.put("sentStatus", "sent");
*/

                                    //     receiverUsersRef.child(chatMessage.getMessageId()).updateChildren(updateStatusHash);


                                    //  }
                                } catch (Exception e) {
                                    //Write the solution to handle the solution here


                                    chatMessage = null;
                                    messageList.remove(chatMessage);

                                    //   chatMessageAdapter.notifyDataSetChanged();

                                }
                            }

                            // listTaskCompletionSource.setResult(messageList);
                        }

                        listTaskCompletionSource.setResult(messageList);
                    }
                }).start();

                Task<List<ChatMessage>> task = listTaskCompletionSource.getTask();


                task.addOnCompleteListener(new OnCompleteListener<List<ChatMessage>>() {
                    @Override
                    public void onComplete(@NonNull Task<List<ChatMessage>> task) {

                        setValue(messageList);
                        // }

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {


            }
        }


    }




}
