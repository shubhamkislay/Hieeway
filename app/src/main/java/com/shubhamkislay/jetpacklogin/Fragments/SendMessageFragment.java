package com.shubhamkislay.jetpacklogin.Fragments;


import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.RevealReplyActivity;
import com.shubhamkislay.jetpacklogin.Adapters.SendMessageAdapter;
import com.shubhamkislay.jetpacklogin.ArchiveActivityViewModel;
import com.shubhamkislay.jetpacklogin.ArchiveActivityViewModelFactory;
import com.shubhamkislay.jetpacklogin.Interface.MessageHighlightListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;
import com.shubhamkislay.jetpacklogin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageFragment extends Fragment {

    public RecyclerView recyclerView;
    public ArchiveActivityViewModel archiveActivityViewModel;
    private ArchiveActivityViewModelFactory archiveActivityViewModelFactory;
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
    public RelativeLayout bottomTextRelativeLayout;
    public int gemCount = 0;
    private Button recharge_gems;
    private int sizeBeforeDeleting;
    private int sizeAfterDataChange;
    public boolean swiped=false;

    public SendMessageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_message, container, false);
        recyclerView = view.findViewById(R.id.sent_messages_recycler_view);

        bottomTextRelativeLayout = view.findViewById(R.id.bottom_panel);


        userIdChattingWith = getArguments().getString("userIdChattingWith");
        currentUserPrivateKey = getArguments().getString("currentUserPrivateKey");
        currentUserPublicKeyID = getArguments().getString("currentUserPublicKeyID");


        toggleButton = view.findViewById(R.id.toggle_msg_highlight);

        gemSize = view.findViewById(R.id.gems_size);

        recharge_gems = view.findViewById(R.id.recharge_gems);





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


      //  observeLiveChatList(userIdChattingWith);
      //  searchChats(userIdChattingWith);




        //archiveActivityViewModel
        bottomTextRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshChat();
                Toast.makeText(getContext(),"Swipe right on messages to unsend",Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getActivity(), "Your swiped status: " + swiped, Toast.LENGTH_SHORT).show();
                    sendMessagelist.remove(message);

                    deleteMessage(message, true);

                    /**
                     * Uncomment the message below to delete the message from the Firebase
                     */


                    sendMessageAdapter.notifyItemRemoved(positionItem);
                    Toast.makeText(getActivity(), "Message Deleted for all!", Toast.LENGTH_SHORT).show();
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



        countGems(userIdChattingWith);

        recharge_gems.setOnClickListener(new View.OnClickListener() {
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


          startActivity(intent);







            }
        });

      //  addListToRecyclerView(sendMessagelist);

        searchChats(userIdChattingWith);




        return view;
    }

    private void countGems(String userIdChattingWith) {

        DatabaseReference deleteMessageSenderRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);


        deleteMessageSenderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

    public void deleteMessage(ChatMessage chatMessage,Boolean deleteForAll) {

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
        readMessageList.clear();
        sendingMessageList.clear();
        try {

            for (ChatMessage chatMessage : messageList) {

                if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

                    if(currentUserPublicKeyID.equals(chatMessage.getPublicKeyID()))
                    {
                        sendMessagelist.add(chatMessage);

                        if (chatMessage.getSentStatus().equals("sending"))
                            sendingMessageList.add(chatMessage);


                    }

                }

                else if(chatMessage.getSenderId().equals(userIdChattingWith))
                {
                    readMessageList.add(chatMessage);
                }

            }
           // addListToRecyclerView(sendMessagelist);
            //refreshChat();
            sizeAfterDataChange = sendMessagelist.size();
            try{
            if(sizeBeforeDeleting <= sizeAfterDataChange )
            {
                refreshChat();
                //Toast.makeText(getContext(),"Refresh Called",Toast.LENGTH_SHORT).show();

                sizeBeforeDeleting = sizeAfterDataChange;
            }

            else
            {
               // sizeBeforeDeleting = sizeAfterDataChange;
            }

            }catch (Exception e)
            {
                //refreshChat();

            }

/*

            if(!swiped) {

                Toast.makeText(getContext(),"Swiped: "+swiped,Toast.LENGTH_SHORT).show();
                refreshChat();
            }
            else {
                //Toast.makeText(getContext(),"Swiped change: "+swiped,Toast.LENGTH_SHORT).show();
                //swiped = false;
            }
*/


        }catch (Exception e)
        {

            //

        }


    }

    private void addListToRecyclerView(List<ChatMessage> sendMessagelist) {


        sendMessageAdapter = new SendMessageAdapter(getActivity(),getContext(), sendMessagelist,userIdChattingWith,readMessageList,gemCount,currentUserPrivateKey, currentUserPublicKeyID);
        recyclerView.setAdapter(sendMessageAdapter);
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
                        }catch (Exception e)
                        {

                        }
                    }

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

    public void refreshChat()
    {

        addListToRecyclerView(sendMessagelist);
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


}
