package com.shubhamkislay.jetpacklogin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Interface.RevealOptionListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class RevealMessageDialog extends Dialog {




    public Context context;
    public Dialog d;
    private ChatMessage chatMessage;
    private TextView request_to_reveal, reveal_message, reveal_message_description, options_txt;
    private RevealOptionListener revealOptionListener;
    private RelativeLayout reveal_message_layout,request_to_reveal_layout,no_reveal_layout, search_layout;
    private String  userIdChattingWith;
    private int gemCount;
    private int position;
    String[] returnMsg = {"",""};
    private int found = 0;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public ValueEventListener reportValueEventListener;
    public ValueEventListener eventListener;
    private String replyingToId;
    private String gotReplyId;
    private String searchMessageId;
    private String messageText;
    private String messageTextTwo;
    private String messageTextThree;
    private int messageTextSize;
    private String messageId;
    private Boolean getIfMessageTwo = false;
    private Boolean getIfMessageThree = false;
    private Boolean dataExists = false;
    public DatabaseReference reportReferenceOne;
    public Boolean changeViews = false;
    ProgressBar progressBar;
    TextView search_text;
    Boolean messagePass;
    public ChatMessage searchedMessage;
    private Activity activity;


    String publicKeyText, privateKeyText, otherUserPublicKey, otherUserPublicKeyID, publicKeyId, tagType;


    public RevealMessageDialog(Activity activity,Context context, ChatMessage chatMessage,String userIdChattingWith, int gemCount, int position, String searchMessageId, Boolean messagePass, String tagType) {

        super(context);
        this.context=context;
        this.chatMessage = chatMessage;
        this.userIdChattingWith = userIdChattingWith;
        this.gemCount = gemCount;
        this.activity = activity;
        this.position = position;
        replyingToId = chatMessage.getReplyID();
        gotReplyId = chatMessage.getGotReplyID();
        this.searchMessageId = searchMessageId;
        this.messagePass = messagePass;
        this.tagType = tagType;



        //revealMessageActivity();


    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);



        setContentView(R.layout.reveal_message_dialog);



        request_to_reveal = findViewById(R.id.request_to_reveal);
        reveal_message_description = findViewById(R.id.reveal_message_description);

        reveal_message = findViewById(R.id.reveal_message);

        search_layout = findViewById(R.id.searching_layout);

        progressBar = findViewById(R.id.search_progress);

        options_txt = findViewById(R.id.options_txt);

        search_text = findViewById(R.id.searching);


        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        publicKeyText = sharedPreferences.getString(PUBLIC_KEY,null);
        publicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID,null);


        request_to_reveal_layout = findViewById(R.id.request_to_reveal_layout);

        reveal_message_layout = findViewById(R.id.reveal_message_layout);

        no_reveal_layout = findViewById(R.id.no_reveal_layout);
/*
        reveal_message.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        reveal_message_description.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans.otf"));
        options_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));*/

       /* deleteForAll.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        deleteForMe.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        optionsTag.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));*/
        // deleteForAllDesc.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans.otf"));
        // deleteForMeDesc.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans.otf"));

        reveal_message.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    reveal_message_layout.setAlpha(0.5f);
                    reveal_message_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    reveal_message_layout.setAlpha(1.0f);
                    reveal_message_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });

        reveal_message_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    reveal_message_layout.setAlpha(0.5f);
                    reveal_message_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    reveal_message_layout.setAlpha(1.0f);
                    reveal_message_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });
        reveal_message_description.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    reveal_message_layout.setAlpha(0.5f);
                    reveal_message_layout.animate().scaleY(0.9f).scaleX(0.9f).setDuration(0);
                }
                if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    reveal_message_layout.setAlpha(1.0f);
                    reveal_message_layout.animate().scaleY(1.0f).scaleX(1.0f).setDuration(50);
                }

                return false;
            }
        });


        reveal_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //revealMessage();
                //revealMessageNew();
                //revealMessageActivity();
                reveal_message.setEnabled(false);
                reveal_message_layout.setEnabled(false);
                reveal_message_description.setEnabled(false);
                dismiss();
                openRevealActivity();
                dismiss();
            }
        });

        reveal_message_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //revealMessage();
                //revealMessageNew();
                //revealMessageActivity();
                reveal_message.setEnabled(false);
                reveal_message_layout.setEnabled(false);
                reveal_message_description.setEnabled(false);
                dismiss();
                openRevealActivity();


            }
        });

        search_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*                changeViews = true;
                search_text.setText("Searching...");
                progressBar.setVisibility(View.VISIBLE);*/
                reveal_message.setEnabled(false);
                reveal_message_layout.setEnabled(false);
                reveal_message_description.setEnabled(false);
                dismiss();
                openRevealActivity();


            }
        });


        request_to_reveal_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requestToReveal();
            }
        });
        request_to_reveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestToReveal();
            }
        });


        showViews();



    }

    private void showViews() {
        if(!chatMessage.getReplyID().equals("none")||!chatMessage.getGotReplyID().equals("none"))
        {


            if(gemCount>0) {
                request_to_reveal_layout.setVisibility(View.GONE);
                search_layout.setVisibility(View.GONE);
                no_reveal_layout.setVisibility(View.GONE);
                reveal_message_layout.setVisibility(View.VISIBLE);
            }

            else {

                if(messagePass)
                {
                    request_to_reveal_layout.setVisibility(View.GONE);
                    search_layout.setVisibility(View.GONE);
                    no_reveal_layout.setVisibility(View.GONE);
                    reveal_message_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    search_layout.setVisibility(View.GONE);
                    request_to_reveal_layout.setVisibility(View.VISIBLE);
                    reveal_message_layout.setVisibility(View.GONE);
                    no_reveal_layout.setVisibility(View.GONE);
                }

            }
        }
        else if(chatMessage.getReplyID().equals("none")&&chatMessage.getGotReplyID().equals("none"))
        {
            no_reveal_layout.setVisibility(View.VISIBLE);
            search_layout.setVisibility(View.GONE);
            request_to_reveal_layout.setVisibility(View.GONE);
            reveal_message_layout.setVisibility(View.GONE);
        }
    }

    public void setListener(RevealOptionListener revealOptionListener) {
        this.revealOptionListener = revealOptionListener;
    }





    public void openRevealActivity()
    {

        //dismiss();

        hide();

        dismiss();


        revealOptionListener.openRevealActivity(userIdChattingWith,searchMessageId,messagePass,chatMessage.getMessageId(),tagType);




/*
    new Handler().postDelayed(new Runnable() {
    @Override
    public void run() {


*/
/*
        final Intent intent = new Intent(activity, RevealedMessageActivity.class);

        intent.putExtra("userIdChattingWith", userIdChattingWith);
        intent.putExtra("searchMessageId", searchMessageId);
        intent.putExtra("messagePass",messagePass);
        intent.putExtra("chatMessageId",chatMessage.getMessageId());
        intent.putExtra("tagType",tagType);

        getContext().startActivity(intent);

       // dismiss();*//*


        }
        }, 200);
*/



    }

    private void requestToReveal()
    {

        Toast.makeText(context,"Request Sent!",Toast.LENGTH_SHORT).show();

        final DatabaseReference requestMessageRef = FirebaseDatabase.getInstance().getReference("MessageRequests")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

             final DatabaseReference searchMessage = FirebaseDatabase.getInstance().getReference("Reports")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(searchMessageId);

        searchMessage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    searchedMessage = dataSnapshot.getValue(ChatMessage.class);

                    if(tagType.equals("gotReply"))
                        searchedMessage.setShowReplyMsg(true);

                    else if(tagType.equals("reply"))
                        searchedMessage.setShowGotReplyMsg(true);

                    final HashMap<String,  Object> sendMessageHash = new HashMap<>();
                    sendMessageHash.put("publicKeyID",searchedMessage.getPublicKeyID());
                    sendMessageHash.put("senderId",searchedMessage.getSenderId());
                    sendMessageHash.put("receiverId",searchedMessage.getReceiverId());
                    sendMessageHash.put("messageId",searchedMessage.getMessageId());
                    sendMessageHash.put("messageText",searchedMessage.getMessageText());
                    sendMessageHash.put("sentStatus",searchedMessage.getSentStatus());
                    sendMessageHash.put("seen", searchedMessage.getSeen());
                    sendMessageHash.put("photourl",searchedMessage.getPhotourl());
                    sendMessageHash.put("textSize", searchedMessage.getTextSize());
                    sendMessageHash.put("replyID",searchedMessage.getReplyID());
                    sendMessageHash.put("gotReplyID",searchedMessage.getGotReplyID());
                    sendMessageHash.put("senderReplyMessage",searchedMessage.getSenderReplyMessage());
                    sendMessageHash.put("replyTag",searchedMessage.getReplyTag());
                    sendMessageHash.put("ifMessageTwo",searchedMessage.getIfMessageTwo());
                    sendMessageHash.put("messageTextTwo",searchedMessage.getMessageTextTwo());
                    sendMessageHash.put("ifMessageThree",searchedMessage.getIfMessageThree());
                    sendMessageHash.put("messageTextThree",searchedMessage.getMessageTextThree());
                    sendMessageHash.put("showReplyMsg",searchedMessage.getShowReplyMsg());
                    sendMessageHash.put("replyMsg",searchedMessage.getReplyMsg());
                    sendMessageHash.put("showGotReplyMsg",searchedMessage.getShowGotReplyMsg());
                    sendMessageHash.put("gotReplyMsg",searchedMessage.getGotReplyMsg());
                    sendMessageHash.put("timeStamp",searchedMessage.getTimestamp());

                    requestMessageRef.child(searchedMessage.getMessageId()).updateChildren(sendMessageHash);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        dismiss();

    }


}
