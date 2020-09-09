package com.hieeway.hieeway.Fragments;


import androidx.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hieeway.hieeway.DeleteUserDialog;
import com.hieeway.hieeway.Interface.CloseLiveMessagingLoading;
import com.hieeway.hieeway.Interface.RechargeGemsListener;
import com.hieeway.hieeway.RecchargeGemsDialog;
import com.hieeway.hieeway.RevealReplyActivity;
import com.hieeway.hieeway.Adapters.SendMessageAdapter;
import com.hieeway.hieeway.Interface.MessageHighlightListener;
import com.hieeway.hieeway.Model.ChatMessage;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SettingsActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hieeway.hieeway.MyApplication.notificationIDHashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageFragment extends Fragment implements RechargeGemsListener {

    public RecyclerView recyclerView;
    private String userIdChattingWith, currentUserPrivateKey, currentUserPublicKeyID;
    private SendMessageAdapter sendMessageAdapter;
    private List<ChatMessage> messageList, sendMessagelist, sendingMessageList, readMessageList;
    ItemTouchHelper itemTouchHelper;
    public SoundPool soundPool;
    public Observer<List<ChatMessage>> observer;
    private int delSound1, delsound2;
    private TextView gemSize;
    Boolean isHighlight = false;
    Bundle savedInstance;
    ToggleButton toggleButton;
    MessageHighlightListener messageHighlightListener;
    public DatabaseReference databaseReference;
    public DatabaseReference databaseReferenceReceiveMessage;
    public ValueEventListener valueEventListener, receiverMsgListener;
    public TextView bottomTextRelativeLayout;
    public int gemCount = 0;
    TextView message_log_empty;
    private int sizeBeforeDeleting;
    private int sizeAfterDataChange;
    public boolean swiped=false;
    String usernameChattingWith;
    String photo;
    Boolean blinked = false;
    RelativeLayout rechargeGems;
    private Button revealRequests;

    private String messageID = "default";
    private CloseLiveMessagingLoading closeLiveMessagingLoading;

    public SendMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        View view = inflater.inflate(R.layout.fragment_send_message_perf, container, false);
        recyclerView = view.findViewById(R.id.sent_messages_recycler_view);

        bottomTextRelativeLayout = view.findViewById(R.id.bottom_msg);
        message_log_empty = view.findViewById(R.id.message_log_empty);


        userIdChattingWith = getArguments().getString("userIdChattingWith");
        currentUserPrivateKey = getArguments().getString("currentUserPrivateKey");
        currentUserPublicKeyID = getArguments().getString("currentUserPublicKeyID");
        messageID = getArguments().getString("messageID");
        usernameChattingWith = getArguments().getString("usernameChattingWith");
        // userIdChattingWith = getArguments().getString("userIdChattingWith");
        photo = getArguments().getString("photo");
        rechargeGems = view.findViewById(R.id.relativeLayout7);

        toggleButton = view.findViewById(R.id.toggle_msg_highlight);

        gemSize = view.findViewById(R.id.gems_size);


        revealRequests = view.findViewById(R.id.recharge_gems);


        notificationIDHashMap.put(userIdChattingWith + "numberrevealrequestaccepted", 1);


        sendMessagelist = new ArrayList<>();
        readMessageList = new ArrayList<>();

        sendingMessageList = new ArrayList<>();
        messageList = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();

        } else {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }

        delSound1 = soundPool.load(getActivity(), R.raw.croak, 1);
        delsound2 = soundPool.load(getActivity(), R.raw.pullout, 1);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);


        recyclerView.setLayoutManager(linearLayoutManager);

        message_log_empty.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


      //  observeLiveChatList(userIdChattingWith);
      //  searchChats(userIdChattingWith);




        //archiveActivityViewModel
        bottomTextRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //refreshChat();
                searchChats(userIdChattingWith);
                //Toast.makeText(getContext(),"Swipe right on messages to unsend",Toast.LENGTH_SHORT).show();
            }
        });


        itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT){

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                try {

                    final int positionItem = viewHolder.getAdapterPosition();

                    final ChatMessage message = sendMessagelist.get(positionItem);


                    //      if(message.getSentStatus().equals("sent")) {

                    sizeBeforeDeleting = sendMessagelist.size();

                    swiped = true;
                    //Toast.makeText(getActivity(), "Your swiped status: " + swiped, Toast.LENGTH_SHORT).show();
                    sendMessagelist.remove(message);

                    deleteMessage(message, true);

                    /**
                     * Uncomment the message below to delete the message from the Firebase
                     */


                    sendMessageAdapter.notifyItemRemoved(positionItem);
                    //Toast.makeText(getActivity(), "Message Deleted for all!", Toast.LENGTH_SHORT).show();
                    soundPool.play(delsound2, 1, 1, 0, 0, 1);
                    //    }
/*                else
                {
                    soundPool.play(delSound1, 1, 1, 0, 0, 1);
                    Toast.makeText(getActivity(),"Can't delete! Message is in transition",Toast.LENGTH_SHORT).show();
                    sendMessageAdapter.notifyDataSetChanged();
                }*/


                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteMessage(message, true);
                    }
                },500);*/


                }catch (Exception e)
                {

                }


            }
/*
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof CartAdapter.MyViewHolder) return 0;
                return super.getSwipeDirs(recyclerView, viewHolder);
            }*/


        });
        itemTouchHelper.attachToRecyclerView(recyclerView);



        //searchChats(userIdChattingWith);


        // countGems(userIdChattingWith);

        revealRequests.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            for(DataSnapshot snapshot: dataSnapshot.getChildren())
                            {

                                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                                try {

                                    if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        databaseReference.child(chatMessage.getMessageId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                sendMessagelist.clear();
                                                addListToRecyclerView(sendMessagelist);
                                            }
                                        });
                                        sendMessagelist.clear();
                                        addListToRecyclerView(sendMessagelist);

                                    }
                                }catch (Exception e)
                                {
                                    //
                                }


                            }

                            final DatabaseReference updateGemsRef = FirebaseDatabase.getInstance().getReference("ChatList")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIdChattingWith);

                            HashMap<String,Object> updateHash = new HashMap<>();

                            updateHash.put("gemCount",0);
                            updateGemsRef.updateChildren(updateHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    HashMap<String,Object> updateHash = new HashMap<>();

                                    updateHash.put("gemCount",2);
                                    updateGemsRef.updateChildren(updateHash);
                                }
                            });


                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                DatabaseReference updateGemsRef = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);

                HashMap<String,Object> updateHash = new HashMap<>();

                updateHash.put("gemCount",2);
                updateGemsRef.updateChildren(updateHash);*/
               // sendMessageAdapter.notifyItemRangeRemoved(0,sendMessagelist.size());

                Intent intent = new Intent(getContext(), RevealReplyActivity.class);



          intent.putExtra("userIdChattingWith",userIdChattingWith);
          intent.putExtra("currentUserPrivateKey",currentUserPrivateKey);
          intent.putExtra("currentUserPublicKeyID",currentUserPublicKeyID);
                intent.putExtra("photo", photo);
                intent.putExtra("username", usernameChattingWith);






          startActivity(intent);







            }
        });

      //  addListToRecyclerView(sendMessagelist);

        // searchChats(userIdChattingWith);


        rechargeGems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rechargeReadGems();

                RecchargeGemsDialog recchargeGemsDialog = new RecchargeGemsDialog(getActivity(), SendMessageFragment.this, userIdChattingWith);

                recchargeGemsDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                recchargeGemsDialog.setCancelable(false);
                recchargeGemsDialog.setCanceledOnTouchOutside(false);
                recchargeGemsDialog.show();
            }
        });




        return view;
    }


    private void rechargeReadGems() {

        TaskCompletionSource<Boolean> booleanTaskCompletionSource = new TaskCompletionSource<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                                try {

                                    if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        databaseReference.child(chatMessage.getMessageId()).removeValue();

                                    }
                                } catch (Exception e) {
                                    //
                                }


                            }


                            booleanTaskCompletionSource.setResult(true);


                        } else {
                            booleanTaskCompletionSource.setResult(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();


        Task<Boolean> booleanTask = booleanTaskCompletionSource.getTask();


        booleanTask.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.isSuccessful()) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            sendMessagelist.clear();
                            addListToRecyclerView(sendMessagelist);
                            DatabaseReference updateGemsRef = FirebaseDatabase.getInstance().getReference("ChatList")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIdChattingWith);

                            HashMap<String, Object> updateHash = new HashMap<>();

                            updateHash.put("gemCount", 2);
                            updateGemsRef.updateChildren(updateHash);
                        }
                    }, 500);


                }

            }
        });


    }

    private void countGems(final String userIdChattingWith) {

        DatabaseReference deleteMessageSenderRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);


        deleteMessageSenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notificationIDHashMap.put(userIdChattingWith + "numberrevealrequestaccepted", 0);

                if(dataSnapshot.exists()) {
                    ChatStamp chatStamp = dataSnapshot.getValue(ChatStamp.class);
                    try {
                        gemSize.setText(""+chatStamp.getGemCount());
                        gemCount = chatStamp.getGemCount();
                    } catch (NullPointerException e) {
                        gemSize.setText("0");
                        gemCount = 0;
                    }
                }
                else{

                    gemSize.setText("0");
                    gemCount = 0;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void deleteMessage(final ChatMessage chatMessage, final Boolean deleteForAll) {

        DatabaseReference deleteMessageSenderRef = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith)
                .child(chatMessage.getMessageId());

        DatabaseReference reportReceiverReference = FirebaseDatabase.getInstance().getReference("Reports")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(chatMessage.getMessageId());

        DatabaseReference reportSenderReference = FirebaseDatabase.getInstance().getReference("Reports")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith)
                .child(chatMessage.getMessageId());


        deleteMessageSenderRef.removeValue();
        reportReceiverReference.removeValue();
        reportSenderReference.removeValue();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String deleteUrl = "default";

                try {
                    if (!chatMessage.getPhotourl().equals("default") || !chatMessage.getPhotourl().equals("played")
                            || !chatMessage.getVideourl().equals("default") || !chatMessage.getVideourl().equals("played")
                            || !chatMessage.getAudiourl().equals("default") || !chatMessage.getAudiourl().equals("played")) {
                        if (!chatMessage.getPhotourl().equals("default") || !chatMessage.getPhotourl().equals("played"))
                            deleteUrl = chatMessage.getPhotourl();
                        else if (!chatMessage.getVideourl().equals("default") || !chatMessage.getVideourl().equals("played"))
                            deleteUrl = chatMessage.getVideourl();
                        else if (!chatMessage.getAudiourl().equals("default") || !chatMessage.getAudiourl().equals("played"))
                            deleteUrl = chatMessage.getAudiourl();

                        if (!deleteUrl.equals("default")) {
                            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(deleteUrl);
                            photoRef.delete();
                        }

                    }
                } catch (Exception e) {
                    //
                }

            }
        }).start();





        if (deleteForAll) {
            final DatabaseReference deleteMessageReceiverRef = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(userIdChattingWith)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
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


    private void filterList(List<ChatMessage> messageList) throws NullPointerException {

        sendMessagelist.clear();


        try {


            TaskCompletionSource<List<ChatMessage>> listTaskCompletionSource = new TaskCompletionSource<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        sendMessagelist.clear();

                        readMessageList.clear();
                        sendingMessageList.clear();
                        for (ChatMessage chatMessage : messageList) {

                            if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                                if (currentUserPublicKeyID.equals(chatMessage.getPublicKeyID())) {
                                    sendMessagelist.add(chatMessage);

                                    if (chatMessage.getSentStatus().equals("sending"))
                                        sendingMessageList.add(chatMessage);


                                }

                            } else if (chatMessage.getSenderId().equals(userIdChattingWith)) {
                                readMessageList.add(chatMessage);
                            }

                        }

                        listTaskCompletionSource.setResult(sendMessagelist);
                    } catch (Exception e) {

                    }

                }
            }).start();

            Task<List<ChatMessage>> listTask = listTaskCompletionSource.getTask();

            listTask.addOnCompleteListener(new OnCompleteListener<List<ChatMessage>>() {
                @Override
                public void onComplete(@NonNull Task<List<ChatMessage>> task) {

                    try {
                        closeLiveMessagingLoading.turnOffLoadingScreen();
                    } catch (Exception e) {
                        //
                    }

                    if (task.isSuccessful()) {
                        sizeAfterDataChange = sendMessagelist.size();

                        if (sizeAfterDataChange < 1)
                            message_log_empty.setVisibility(View.VISIBLE);

                        try {
                            if (sizeBeforeDeleting <= sizeAfterDataChange) {
                                refreshChat(
                                        sendMessagelist);
                                //Toast.makeText(getContext(),"Refresh Called",Toast.LENGTH_SHORT).show();

                                sizeBeforeDeleting = sizeAfterDataChange;
                            } else {
                                // sizeBeforeDeleting = sizeAfterDataChange;
                            }


                        } catch (Exception e) {
                            //refreshChat();

                        }
                    } else
                        try {

                        } catch (Exception e) {
                            //
                        }

                }

            });

            // addListToRecyclerView(sendMessagelist);
            //refreshChat();


        } catch (Exception e) {

        }


    }

    private void addListToRecyclerView(List<ChatMessage> sendMessagelist) {

        if (sendMessagelist.size() < 1)
            message_log_empty.setVisibility(View.VISIBLE);

        else
            message_log_empty.setVisibility(View.GONE);

        //checking_layout.setVisibility(View.GONE);
        sendMessageAdapter = new SendMessageAdapter(getActivity(), getContext(), sendMessagelist, userIdChattingWith, readMessageList, gemCount, currentUserPrivateKey, currentUserPublicKeyID, messageID);
        recyclerView.setAdapter(sendMessageAdapter);
        recyclerView.scrollToPosition(sendMessagelist.size() - 1);
        if (!messageID.equals("default") && !blinked) {
            recyclerView.scrollToPosition(sendMessageAdapter.getItemPosition(messageID));
            blinked = true;
        }
       // sendMessageAdapter.notifyDataSetChanged();


    }

    public void notifyAdapter() {
        try {
            onCreate(savedInstance);
        }catch (Exception e)
        {

        }
    }

    /*public void setParentFragmentData(String userIdChattingWith)
    {
        this.userIdChattingWith = userIdChattingWith;
    }*/

    public void setMessageList(List<ChatMessage> refreshedList) {
        messageList = refreshedList;


        filterList(messageList);
    }


    public void searchChats(String userIdChattingWith) {

        messageList.clear();

        bottomTextRelativeLayout.animate().alpha(1.0f).setDuration(1200);

        try {

            this.userIdChattingWith = userIdChattingWith;
            databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith);


            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    /*TaskCompletionSource<List<ChatMessage>> listTaskCompletionSource = new TaskCompletionSource<>();

                    new Thread(new Runnable() {
                        @Override
                        public void run() {*/


                    messageList.clear();
                    sendMessagelist.clear();

                    messageList.clear();
                    if (dataSnapshot.exists()) {
                        messageList.clear();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {


                            ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);

                            messageList.add(chatMessage);


                        }


                        filterList(messageList);

                        try {

                            checkPresence();
                        } catch (Exception e) {

                        }


                    } else {

                        message_log_empty.setVisibility(View.VISIBLE);

                    }
                    // listTaskCompletionSource.setResult(messageList);

                    try {
                        closeLiveMessagingLoading.turnOffLoadingScreen();
                    } catch (Exception e) {

                    }


                    /*    }
                    }).start();*/

                    /*Task<List<ChatMessage>> listTask = listTaskCompletionSource.getTask();

                    listTask.addOnCompleteListener(new OnCompleteListener<List<ChatMessage>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<ChatMessage>> task) {*/

                     /*   }
                    });
*/


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };


            databaseReference.addValueEventListener(valueEventListener);

        }catch (Exception e)
        {
            Toast.makeText(getActivity(), "Null pointer on user ID", Toast.LENGTH_SHORT).show();
        }
    }

    public void removeListeners() {

        try {
            bottomTextRelativeLayout.setAlpha(0.0f);
            databaseReference.removeEventListener(valueEventListener);
        }catch (Exception e)
        {

            //
        }

    }

    public void refreshChat(List<ChatMessage> refreshlist)
    {

        sendMessagelist = refreshlist;


        addListToRecyclerView(refreshlist);
        try {
           // bottomTextRelativeLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.colorBlack));
        }catch (Exception ee)
        {
            //
        }

        /*databaseReference.addListenerForSingleValueEvent(valueEventListener);
        databaseReference.removeEventListener(valueEventListener);*/
    }
    public void checkPresence() {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    final boolean connected = snapshot.getValue(Boolean.class);
                    if (connected) {


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (connected) {
                                    try {


                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                for (ChatMessage chatMessage : sendingMessageList) {
                                                    if (chatMessage.getSentStatus().equals("sending")) {
                                                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                                                .child("Messages")
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(userIdChattingWith)
                                                                .child(chatMessage.getMessageId());
                                                        HashMap<String, Object> hashMap = new HashMap<>();
                                                        hashMap.put("messageId", chatMessage.getMessageId());

                                                        databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                HashMap<String, Object> hashMap1 = new HashMap<>();
                                                                hashMap1.put("sentStatus", "sent");

                                                                databaseReference.updateChildren(hashMap1);

                                                            }
                                                        });
                                                    }

                                                }
                                            }
                                        }).start();




                                    } catch (Exception e) {

                                    }
                                }

                            }
                        }, 3000);


                    } else {


                    }
                }catch (Exception e)
                {
                    //
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log.w(TAG, "Listener was cancelled");
            }
        });
    }

    public void setMessageHighlightListener(MessageHighlightListener messageHighlightListener) {
        this.messageHighlightListener = messageHighlightListener;

    }

    public void setCloseLiveMessagingLoadingListener(CloseLiveMessagingLoading closeLiveMessagingLoading) {
        this.closeLiveMessagingLoading = closeLiveMessagingLoading;
    }

/*    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser)
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    countGems(userIdChattingWith);
                    searchChats(userIdChattingWith);
                }
            },0);

        }
        else
        {
            try {
                checking_layout.setVisibility(View.VISIBLE);
            }catch (Exception e)
            {
                //
            }
        }
    }*/

    @Override
    public void onPause() {


        super.onPause();
        notificationIDHashMap.put(userIdChattingWith + "numberrevealrequestaccepted", 0);
        messageID = "default";
    }

    @Override
    public void onResume() {
        super.onResume();
        countGems(userIdChattingWith);
        searchChats(userIdChattingWith);
        /*try {
            checking_layout.setVisibility(View.VISIBLE);
        }catch (Exception e)
        {
            //
        }*/


    }

    @Override
    public void rechargeGems() {

        //rechargeReadGems();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendMessagelist.clear();
                addListToRecyclerView(sendMessagelist);
                DatabaseReference updateGemsRef = FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);

                HashMap<String, Object> updateHash = new HashMap<>();

                updateHash.put("gemCount", 2);
                updateGemsRef.updateChildren(updateHash);
            }
        }, 500);
    }
}
