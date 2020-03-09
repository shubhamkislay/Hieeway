package com.shubhamkislay.jetpacklogin;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.OnStateChangeListener;
import com.ebanx.swipebtn.SwipeButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hanks.htextview.line.LineTextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.hanks.htextview.base.HTextView;
import com.jgabrielfreitas.core.BlurImageView;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.ChatStamp;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EphemeralMessageActivity extends AppCompatActivity implements View.OnTouchListener {



    public TextView username, message_counter, message_counter_background, message_no_message_counter_background;
    public TextView message_text;
    public TextView message_text_sender;
    public BlurImageView profile_pic;
    public TypeWriter message_text_typewriter;
    public ProgressBar message_pulse;
    public float opacity=0.0f;
    public EditText message_box;
    public Button sendButton, message_bar, reply_btn, cancel_reply_btn;
    public RelativeLayout bottom_bar, message_counter_layout, message_space, reply_tag, app_context_layout, sender_reply_body, sender_reply_tag;
    public RelativeLayout read_message_reply_layout;
    public int textSize;
    public TextView send_arrow_two, archive_btn;
    public ImageView send_arrow, receiver_arrow;
    public TextView your_sent_msg, back_button;
    private String liveMessage;
    private Button read_message_reply;
    String messageStr;
    public Handler deletionHandler, blinkerHandler;
    public Runnable runnable, blinkerThreadRunnable, blinkerHandlerRunnable;
    public Thread blinkerThread;
    public List<ChatMessage> messageList;
    private String userIdChattingWith;
    public  int blinkInterval;
    public Boolean isSenderReplying = false;
    public ImageView view;
    public int before_count,after_count;
    public Boolean imageReady = true;
    public Boolean imageLoaded = false;
    public String lastMessageID = "none";
    public String senderReplyID = "none";
    public String senderReplyMessage = "none";
    public Boolean isSeeingReply=false;
    public String messageTextOne,messageTextTwo,MessageTextThree;
    public Boolean setChainBtn = false;
    public Button chainBtn;
    SwipeButton swipeButton;
    Boolean swipeState = false;


    public int setAnimationSendingDuration;

    public SoundPool soundPool;
    Boolean readyToCheckSize = false;
    private int delSound1, cantDelSound2, sendSound3,sendingSound4, sendingSound5;



    private EphemeralMessageViewModel ephemeralMessageViewModel;
    public Boolean isMessageRunning = false;
    Window window;
    private Boolean threadPending;
    public String currentUsername;
    public String currentUserPhoto;

    @SuppressLint({"ClickableViewAccessibility", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ephemeral_message);

      //  setContentView(R.layout.new_ephemeral_messaging_layout);


        // sound pool

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
        {

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setMaxStreams(1)
                    .setAudioAttributes(audioAttributes)
                    .build();

        }

        else
        {
            soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0 );
        }







        username = findViewById(R.id.username);

        message_text = findViewById(R.id.message_text);

        your_sent_msg = findViewById(R.id.your_sent_msg);

        read_message_reply = findViewById(R.id.read_message_reply);


        chainBtn = findViewById(R.id.chain_message_btn);


        chainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(setChainBtn) {
                    setChainBtn = false;
                    chainBtn.setAlpha(0.2f);
                    Toast.makeText(EphemeralMessageActivity.this, "Reading messages dechained",Toast.LENGTH_SHORT).show();
                }
                else {
                    setChainBtn = true;
                    chainBtn.setAlpha(1.0f);
                    Toast.makeText(EphemeralMessageActivity.this, "Reading messages chained",Toast.LENGTH_SHORT).show();

                }
            }
        });




        message_text_sender = findViewById(R.id.message_text_sender);

        message_text_typewriter = findViewById(R.id.message_text_type_writer);

        //Add custom font

        /*Typeface custom_font = Typeface.createFromAsset(getAssets(),"fonts/ProximaNova-Black.ttf");

        message_text.setTypeface(custom_font);
        message_text_sender.setTypeface(custom_font);
        message_text_typewriter.setTypeface(custom_font);*/


        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.BLACK);
        }




        sendButton = findViewById(R.id.send_button);

        sender_reply_tag = findViewById(R.id.sender_reply_tag);

        message_box = findViewById(R.id.message_box);

        bottom_bar = findViewById(R.id.bottom_bar);

        message_space = findViewById(R.id.message_space);

        reply_tag = findViewById(R.id.reply_tag);

        message_counter_layout = findViewById(R.id.message_counter_layout);

        message_bar = findViewById(R.id.message_bar);

        message_counter = findViewById(R.id.message_counter);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        message_counter_background  = findViewById(R.id.message_counter_background);

        message_no_message_counter_background = findViewById(R.id.message_no_message_counter_background);


        read_message_reply_layout = findViewById(R.id.read_message_reply_layout);

        profile_pic = findViewById(R.id.profile_pic);

        app_context_layout = findViewById(R.id.app_context_layout);

        sender_reply_body  = findViewById(R.id.sender_reply_body);



        profile_pic.setBlur(0);

        message_pulse =  findViewById(R.id.message_pulse);

        archive_btn = findViewById(R.id.archive);

        back_button = findViewById(R.id.back_button);

        reply_btn = findViewById(R.id.reply_message_btn);

        cancel_reply_btn = findViewById(R.id.cancel_message_reply_btn);

        send_arrow = findViewById(R.id.send_arrow);

        receiver_arrow = findViewById(R.id.receive_arrow);

        send_arrow_two = findViewById(R.id.send_arrow_two);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeButton = findViewById(R.id.swipeBtn);



        swipeButton.setOnStateChangeListener(new OnStateChangeListener() {
            @Override
            public void onStateChange(boolean active) {
                if(active) {
                 //   swipeState = active;
                    setChainBtn = true;
                    readNextMessage();
                    swipeButton.setBackground(getDrawable(R.drawable.message_swipe_bg));
                    Toast.makeText(EphemeralMessageActivity.this, "Message chained", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    setChainBtn = false;
                    Toast.makeText(EphemeralMessageActivity.this, "Message de-chained!", Toast.LENGTH_SHORT).show();
                }

            }
        });



        Intent intent = getIntent();

        final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");


        setAnimationSendingDuration = 600;


        archive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EphemeralMessageActivity.this,ArchiveActivity.class);
                intent.putExtra("userIdChattingWith",userIdChattingWith);

                startActivity(intent);

            }
        });


        reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!lastMessageID.equals("none"))
                {
                    isSenderReplying=true;
                    reply_tag.setVisibility(View.VISIBLE);
                message_box.requestFocus();
                InputMethodManager inputMethodManager =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.toggleSoftInputFromWindow(
                        app_context_layout.getApplicationWindowToken(),
                        InputMethodManager.SHOW_FORCED, 0);
                }


            }
        });


        cancel_reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSenderReplying=false;
                reply_tag.setVisibility(View.GONE);
            }
        });




        messageList = new ArrayList<>();
        before_count = messageList.size();







        send_arrow.setTranslationY(0);
        send_arrow_two.setTranslationY(0);


        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;


        //setting the opacity of profile pic when message is being read..
        setProfilePicOpacity(0.05f);
        /*if(photo.equals("default"))
            profile_pic.setImageResource(R.drawable.profile_pic);*/



        delSound1 = soundPool.load(this,R.raw.communication_channel,1 );
        cantDelSound2 = soundPool.load(this,R.raw.knuckle,1);
        sendSound3 = soundPool.load(this,R.raw.open_ended,1);
        sendingSound4 = soundPool.load(this,R.raw.youwouldntbelieve,1);

        sendingSound5 = soundPool.load(this,R.raw.echoed_ding,1);

        if(!photo.equals("default"))
            Glide.with(EphemeralMessageActivity.this).load(photo).apply(new RequestOptions().override(width,height)).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource){


                if(imageReady)
                {
                    profile_pic.animate().alpha(1.0f).setDuration(750).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            imageLoaded=true;
                        }
                    });

                }




                return false;
            }
        }).into(profile_pic);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    User user = dataSnapshot.getValue(User.class);

                    currentUsername =user.getUsername();
                    currentUserPhoto = user.getPhoto();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




        EphemeralMessageActivityViewModelFactory ephemeralMessageActivityViewModelFactory = new EphemeralMessageActivityViewModelFactory(userIdChattingWith);
        ephemeralMessageViewModel = ViewModelProviders.of(this, ephemeralMessageActivityViewModelFactory).get(EphemeralMessageViewModel.class);
        ephemeralMessageViewModel.getAllMessages().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {

                /*try {

                    updateChatList(messageList.get(messageList.size() - 1));
                }catch (Exception e)
                {
                    //
                }*/


                //Testing purpose only
               // messageList = chatMessageList;





                before_count = messageList.size();

                after_count = filterList(chatMessageList);

                if(after_count>before_count) {
                    soundPool.play(sendingSound4, 1, 1, 0, 0, 1);


                    before_count = after_count;
                }

                else
                    after_count = messageList.size();








                if(messageList.size()<1) {


                    ephemeralMessageViewModel.setChatPending(false);

                    swipeButton.setEnabled(false);

                    swipeButton.setVisibility(View.GONE);


                  //  swipeButton = null;


                    /**
                     *  Automatically queues the messages after button is explicitely triggered,
                     *  after a message in received when this activity is opened,
                     *  unless we add the following
                     *  setChainBtn = false;
                     **/




                    chainBtn.setAlpha(0.2f);
                    //
                  //  ephemeralMessageViewModel.setChatSeen();
                    message_counter_background.setVisibility(View.GONE);
                    message_pulse.setVisibility(View.GONE);
                    message_no_message_counter_background.setVisibility(View.VISIBLE);
                    message_counter.setText("");




                    message_counter.setBackground(getDrawable(R.drawable.message_counter_drawable));
                    if(!isMessageRunning)
                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));




                   // message_counter.setVisibility(View.GONE);
                }
                else {

                  //  ephemeralMessageViewModel.setChatPending(true);

                    swipeButton = findViewById(R.id.swipeBtn);
                    swipeButton.setEnabled(true);
                    swipeButton.setVisibility(View.VISIBLE);
                    message_counter_background.setVisibility(View.VISIBLE);
                    message_no_message_counter_background.setVisibility(View.GONE);
                    message_counter.setBackground(getDrawable(R.drawable.message_counter_new_message_drawable));


                    if(!isMessageRunning) {
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));
                        message_pulse.setVisibility(View.VISIBLE);
                    }

                    if(messageList.size()>99)
                    {
                        message_counter.setText("99+");
                        message_counter.setTextSize(26);
                    }

                    else
                    {
                        message_counter.setText(messageList.size() + "");
                            message_counter.setTextSize(32);
                    }
                }


            //    filterList(chatMessageList);
               // threadPending = ephemeralMessageViewModel.isThreadRunning();
               // setIsMessageRunning(threadPending);



            }
        });

        message_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_box.setEnabled(true);
            }
        });



        //setting the font style of message text
        /*Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/samsungsharpsans-bold.otf");
        username.setTypeface(custom_font);*/


        message_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

               // message_text_sender.setText("");
                /*

                if (s.toString().trim().length() > 0) {

                    if(!getIsMessageRunning()) {
                        sendButton.setEnabled(true);
                        message_text_sender.setText(s);
                        message_text_sender.setAlpha(0.5f);
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));
                        profile_pic.setBlur(7);
                        profile_pic.setAlpha(0.085f);
                    }
                } else {
                    if(!getIsMessageRunning()) {
                        profile_pic.setAlpha(0.5f);
                        profile_pic.setBlur(0);
                      //  message_text_sender.setAlpha(0.5f);
                        if(messageList.size()<1)
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                    }
                    sendButton.setEnabled(false);
                }*/

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // message_text_sender.setText("");
                message_text_sender.setTranslationY(0);
              //  message_text_sender.animateText(s);


                if (s.toString().trim().length() > 0) {

                    imageReady = false;
                    sendButton.setAlpha(1.0f);

                    if (!getIsMessageRunning())
                    {



                        sendButton.setEnabled(true);
                        message_text_sender.setAlpha(0.5f);

                        message_text_sender.setText(s);
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));

                        profile_pic.animate().alpha(opacity).setDuration(250);

                        if(imageLoaded)
                            profile_pic.setBlur(4);


                }



                } else {
                    if(!getIsMessageRunning())
                    {
                       // message_text_sender.setAlpha(0.0f);

                      //  imageReady = false;
                        sendButton.setAlpha(0.15f);

                            profile_pic.animate().alpha(1.0f).setDuration(setAnimationSendingDuration*2+650);

                        if(imageLoaded)
                            profile_pic.setBlur(0);


                   // profile_pic.setBlur(0);
                        if(messageList.size()<1)
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                        //message_text_sender.setText("");

                }
                    sendButton.setEnabled(false);
                  //  message_text_sender.setText(s);
                    message_text_sender.animate().alpha(0.0f).setDuration(250);



                }


            }

            @Override
            public void afterTextChanged(Editable s) {
               // message_text_sender.setText("");
/*

                if (s.toString().trim().length() > 0) {


                    if(!getIsMessageRunning()) {
                        sendButton.setEnabled(true);
                        message_text_sender.setText(s);
                        message_text_sender.setAlpha(0.5f);
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));
                        profile_pic.setBlur(7);
                        profile_pic.setAlpha(0.085f);
                    }

                } else {
                    if(!getIsMessageRunning()) {
                        profile_pic.setBlur(0);
                        profile_pic.setAlpha(0.5f);
                      //  message_text_sender.setAlpha(0.0f);
                        if(messageList.size()<1)
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                    }
                    sendButton.setEnabled(false);

                }

*/

            }
        });




        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                reply_tag.setVisibility(View.GONE);
                reply_btn.setVisibility(View.GONE);



             //   bottom_bar.setBackground(getDrawable(R.color.gradientEnd));



                message_text_sender.setAlpha(1.0f);


                //delSound1, cantDelSound2, sendSound3,sendingSound4, sendingSound5;

                soundPool.play(sendSound3,1 ,1 ,0 ,0 ,1 );




                send_arrow.setTranslationY(0);
                send_arrow_two.setTranslationY(0);

                send_arrow.setAlpha(1.0f);
                send_arrow_two.setAlpha(0.0f);


                messageStr = message_box.getText().toString();


                /**
                 * New Changes
                 * Adding chaining of multiple messages while sending.
                 *
                 *
                 * UI elements included:
                 *  Chain button
                 *  cancel button attached for each message block
                 *
                 *  Data Structure style
                 *   Stack
                 */


                //Begin


                /*ChatMessage chatMessageNew = new ChatMessage();
                if(isSenderReplying) {

                    chatMessageNew.setMessageId("notAssigned");
                    chatMessageNew.setMessageText(messageStr);
                    chatMessageNew.setSenderId("notAssigned");
                    chatMessageNew.setReceiverId("notAssigned");
                    chatMessageNew.setSentStatus("sending");
                    chatMessageNew.setSeen("notseen");
                    chatMessageNew.setPhotourl("none");
                    chatMessageNew.setReplyID(lastMessageID);
                    chatMessageNew.setSenderReplyMessage(senderReplyMessage);

                    lastMessageID="none";
                    isSenderReplying=false;
                }
                else {
                    chatMessageNew.setMessageId("notAssigned");
                    chatMessageNew.setMessageText(messageStr);
                    chatMessageNew.setSenderId("notAssigned");
                    chatMessageNew.setReceiverId("notAssigned");
                    chatMessageNew.setSentStatus("sending");
                    chatMessageNew.setSeen("notseen");
                    chatMessageNew.setPhotourl("none");
                    chatMessageNew.setReplyID("none");
                    chatMessageNew.setSenderReplyMessage("none");

                }*/

                //End


                ChatMessage chatMessage = new ChatMessage();
                if(isSenderReplying) {

                    chatMessage.setMessageId("notAssigned");
                    chatMessage.setMessageText(messageStr);
                    chatMessage.setSenderId("notAssigned");
                    chatMessage.setReceiverId("notAssigned");
                   // chatMessage.setSentStatus("sending");
                    chatMessage.setSeen("notseen");
                    chatMessage.setPhotourl("none");
                    chatMessage.setReplyID(lastMessageID);
                    chatMessage.setSenderReplyMessage(senderReplyMessage);

                    lastMessageID="none";
                    isSenderReplying=false;
                }
                else {
                    chatMessage.setMessageId("notAssigned");
                    chatMessage.setMessageText(messageStr);
                    chatMessage.setSenderId("notAssigned");
                    chatMessage.setReceiverId("notAssigned");
                 //   chatMessage.setSentStatus("sending");
                    chatMessage.setSeen("notseen");
                    chatMessage.setPhotourl("none");
                    chatMessage.setReplyID("none");
                    chatMessage.setSenderReplyMessage("none");

                }



                ephemeralMessageViewModel.sendMessage(chatMessage);
                ephemeralMessageViewModel.createChatListItem(usernameChattingWith,photo,currentUsername,currentUserPhoto);

                message_box.setText("");


                send_arrow_two.animate().translationY(-4000).setDuration(setAnimationSendingDuration);
                send_arrow.animate().translationY(-4000).setDuration(setAnimationSendingDuration);


                message_text_sender.animate().translationY(-4000).setDuration(setAnimationSendingDuration);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        message_text_sender.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);



                        send_arrow_two.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);
                        send_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);


                    }
                }, 100);

                //message_text_sender.setAlpha(0.0f);
                if(messageList.size()<1)
                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                else
                message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));






                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        message_text_sender.setText("");
                      //  profile_pic.animate().alpha(1.0f).setDuration(800);

                        /*if(imageLoaded)
                            profile_pic.setBlur(0);*/

                        imageReady=true;

                    //    bottom_bar.setBackground(getDrawable(R.color.colorBlack));

                    }
                }, setAnimationSendingDuration);



               // message_box.setFocusable(false);
            }
        });



        username.setText(usernameChattingWith);


     //   message_space.setOnTouchListener(this);

        message_counter.setOnTouchListener(this);


     //   blink();

       // readNextMessage();

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(messageList.size()<1)
                    ephemeralMessageViewModel.setChatPending(false);
            }
        },1000);*/




    }

    private void updateChatList(final ChatMessage chatMessage) {




            final DatabaseReference senderReference = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(chatMessage.getReceiverId())
                    .child(chatMessage.getSenderId());

            final DatabaseReference receiverReference = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(chatMessage.getSenderId())
                    .child(chatMessage.getReceiverId());

            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();


            HashMap<String,Object> timeStampHash = new HashMap<>();
            timeStampHash.put("timeStamp", ts);
            timeStampHash.put("id",chatMessage.getReceiverId());
            timeStampHash.put("seen","seen");
            timeStampHash.put("timeStamp", ts);



            HashMap<String,Object> timeStampHashSend = new HashMap<>();
            timeStampHashSend.put("timeStamp", ts);
            timeStampHashSend.put("id",chatMessage.getSenderId());
            timeStampHashSend.put("seen","seen");
            timeStampHashSend.put("timeStamp", ts);


        receiverReference.updateChildren(timeStampHash);
            senderReference.updateChildren(timeStampHashSend);


    }

    private int filterList(List<ChatMessage> chatMessageList) {

        List<ChatMessage> messageListReading = chatMessageList;
        //if(!getIsMessageRunning())
            messageList.clear();

            try {

                for (ChatMessage chatMessage : messageListReading) {
                    if (chatMessage.getSenderId().equals(userIdChattingWith))
                        messageList.add(chatMessage);


                }



            }catch (Exception e)
            {
                Toast.makeText(EphemeralMessageActivity.this,"Internet is crawling! You might get issues using the App",Toast.LENGTH_LONG).show();
            }




            return messageList.size();


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {


            case MotionEvent.ACTION_UP:
                if(!getIsMessageRunning())
                    readNextMessage();

            break;
        }




        return true;
    }

    private void readNextMessage() {
        if(messageList.size()==0)
            Toast.makeText(EphemeralMessageActivity.this,"You have no messages to read!", Toast.LENGTH_SHORT).show();

        else {

            reply_btn.setVisibility(View.GONE);

            receiver_arrow.setTranslationY(0);
            receiver_arrow.setAlpha(1.0f);
            receiver_arrow.animate().translationY(4000).setDuration(setAnimationSendingDuration);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration-125);

                }
            }, 100);

            soundPool.play(delSound1,1 ,1 ,0 ,0 ,1 );

            imageReady = false;
            setIsMessageRunning(true);
            message_text_sender.setAlpha(0.0f);
            message_pulse.setVisibility(View.GONE);
            final ChatMessage chatMessage = messageList.get(0);
            lastMessageID = chatMessage.getMessageId();

            senderReplyID = chatMessage.getReplyID();
            senderReplyMessage = chatMessage.getMessageText();


            message_bar.setBackground(getDrawable(R.drawable.message_identifier_drawable));
            liveMessage = chatMessage.getMessageText();

            blinkInterval = (liveMessage.length()*50+750)/10;





                           /*// This is for type writer
                            message_text_typewriter.setCharacterDelay(75);
                            message_text_typewriter.animate().alpha(1.0f).setDuration(750);
                            message_text_typewriter.animateText(liveMessage);*/

                            deletionHandler = new Handler();
                            runnable = new Runnable() {
                @Override
                public void run() {


                    message_text.animate().alpha(0.0f).setDuration(250);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {


                            if(messageList.size()<1)
                                message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                            else
                                message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                            message_bar.setVisibility(View.VISIBLE);






                            message_text_sender.animate().alpha(0.5f).setDuration(250);
                            username.setAlpha(1.0f);
                            message_text.setText("");
                            message_text.setAlpha(0.0f);


                            setIsMessageRunning(false);
                            if(message_text_sender.getText().length()>0) {
                                message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));
                            }
                            else
                            {
                                if(imageLoaded)
                                    profile_pic.setBlur(0);
                                profile_pic.animate().alpha(1.0f).setDuration(250);
                            }

                            deletionHandler.removeCallbacks(runnable);
                            blinkerHandler.removeCallbacks(blinkerHandlerRunnable);
                            liveMessage = "";


                            if(messageList.size()>0)
                                message_pulse.setVisibility(View.VISIBLE);

                            imageReady = true;

                            if(isSeeingReply) {
                                isSeeingReply = false;
                                your_sent_msg.setText("");
                                sender_reply_body.setVisibility(View.GONE);
                                sender_reply_tag.setVisibility(View.GONE);

                            }
                          //  receiver_arrow.animate().alpha(0.0f).setDuration(3750);

                            if(setChainBtn) {
                                readNextMessage();
                            }
                            else
                            {
                                receiver_arrow.setTranslationY(0);
                                receiver_arrow.setAlpha(1.0f);
                                receiver_arrow.animate().translationY(4000).setDuration(setAnimationSendingDuration);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);

                                    }
                                }, 100);
                            }


                        }
                    }, 250);


                    if(!lastMessageID.equals("none"))
                    {

                        reply_btn.setVisibility(View.VISIBLE);
                     //   sender_reply_tag.setVisibility(View.VISIBLE);

                    }
                    else
                        reply_btn.setVisibility(View.GONE);







                }





            };



                username.setAlpha(0.0f);

            if(imageLoaded)
                profile_pic.setBlur(4);

                profile_pic.animate().alpha(opacity).setDuration(250);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {



                    if(senderReplyID.equals("none")) {


                        blink();
                        message_text.setText(liveMessage);
                       // message_text.animateText(liveMessage);
                        message_text.animate().alpha(1.0f).setDuration(250);

                        ephemeralMessageViewModel.deleteMessage(chatMessage, false);
                        deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);

                    }
                    else
                    {

                        read_message_reply_layout.setVisibility(View.VISIBLE);
                        isSeeingReply=true;
                        your_sent_msg.setText(chatMessage.getSenderReplyMessage());
                        sender_reply_body.setVisibility(View.VISIBLE);
                        sender_reply_tag.setVisibility(View.VISIBLE);

                        read_message_reply_layout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                blink();
                                read_message_reply_layout.setVisibility(View.GONE);
                                ephemeralMessageViewModel.deleteMessage(chatMessage, false);
                                message_text.setText(liveMessage);
                                message_text.animate().alpha(1.0f).setDuration(250);
                                deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);

                            }
                        });

                        read_message_reply.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                blink();
                                isSenderReplying = false;
                                read_message_reply_layout.setVisibility(View.GONE);
                                ephemeralMessageViewModel.deleteMessage(chatMessage, false);
                                message_text.setText(liveMessage);
                                message_text.animate().alpha(1.0f).setDuration(250);
                                deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);

                            }
                        });
                    }

                }
            }, 250);


        }



    }
    private void blink() {
        blinkerHandler = new Handler();



        blinkerHandlerRunnable = new Runnable() {
            @Override
            public void run() {

                if (message_bar.getVisibility() == View.VISIBLE) {
                    message_bar.setVisibility(View.INVISIBLE);
                } else
                {

                    message_bar.setVisibility(View.VISIBLE);
                }

                if(isMessageRunning)
                    blink();

                else
                {
                    blinkerThread.interrupt();
                    blinkerHandler.removeCallbacks(blinkerHandlerRunnable);

                        message_bar.setVisibility(View.VISIBLE);
                }

            }
        };


        blinkerThreadRunnable = new Runnable() {
            @Override
            public void run() {
                int timeToBlink = blinkInterval;    //in milissegunds
                try {
                    Thread.sleep(timeToBlink);
                } catch (Exception e) {
                }
                blinkerHandler.post(blinkerHandlerRunnable);
            }
        };


         blinkerThread = new Thread(blinkerThreadRunnable);
        blinkerThread.start();




    }


    public void setProfilePicOpacity(float opacity)
    {
        this.opacity = opacity;
    }

    public float getProfilePicOpacity()
    {
        return opacity;
    }



    public void setIsMessageRunning(Boolean isMessageRunning) {

        this.isMessageRunning = isMessageRunning;

    }

    public Boolean getIsMessageRunning() {
        return isMessageRunning;
    }


    @Override
    protected void onPause() {
        super.onPause();

        ephemeralMessageViewModel.stopAutodelete();
        try {

            blinkerThread.interrupt();
            blinkerHandler.removeCallbacks(blinkerHandlerRunnable);
        }catch (NullPointerException ne)
        {
            //No need to address
        }


    }
}
