package com.hieeway.hieeway;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.jgabrielfreitas.core.BlurImageView;
import com.hieeway.hieeway.Model.ChatMessage;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.List;

import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.EmojiEditText;
import br.com.instachat.emojilibrary.model.layout.EmojiKeyboardLayout;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class EphemeralChainMessageActivity extends EmojiCompatActivity implements View.OnTouchListener {


    public TextView username, message_counter, message_counter_background, message_no_message_counter_background;

    public TextView message_text_dummy, message_text, message_text_two, message_text_three;
    public TextView message_text_sender_dummy, message_text_sender, message_text_two_sender, message_text_three_sender;
    public BlurImageView profile_pic;
    public TypeWriter message_text_typewriter;
    public ProgressBar message_pulse, chain_pulse, sending_progress_bar;
    public float opacity = 0.0f;
    public EmojiEditText message_box;
    public Button sendButton, message_bar, reply_btn, cancel_reply_btn;
    public RelativeLayout bottom_bar, message_counter_layout, message_space, reply_tag, app_context_layout, sender_reply_body, sender_reply_tag;
    public RelativeLayout read_message_reply_layout;
    public View rootView;
    public int textSize;
    public TextView send_arrow_two, archive_btn;
    public ImageView send_arrow, receiver_arrow;
    public TextView your_sent_msg, back_button;
    private String liveMessage, liveMessageTwo, liveMessageThree;
    private Boolean messageTwoPresent = false, messageThreePresent = false;

    private Button emoji, camera;


    private ImageView read_message_back;
    private Button read_message_reply;
    String messageStr = "", messageStrTwo = "", messageStrThree = "";
    public Handler deletionHandler, blinkerHandler;
    public Runnable runnable, blinkerThreadRunnable, blinkerHandlerRunnable;
    public Thread blinkerThread;
    public List<ChatMessage> messageList, sendMessageList;
    private String userIdChattingWith;
    public int blinkInterval;
    public Boolean isSenderReplying = false;
    public ImageView view;
    public int before_count, after_count;
    public Boolean imageReady = true;
    public Boolean imageLoaded = false;
    public String lastMessageID = "none";
    public String senderReplyID = "none";
    public String senderReplyMessage = "none";
    public Boolean isSeeingReply = false;
    public String messageTextOne, messageTextTwo, MessageTextThree;
    public Boolean setChainBtn = false;
    public static final int MSG_TYPING_BOX_ID = 1;
    public static final int MSG_TYPING_BOX_TWO_ID = 2;
    public static final int MSG_TYPING_BOX_THREE_ID = 3;

    String messageKeySender, messageKeyReceiver;

    public int typingID = 1;

    public Button chainBtn;
    SwipeButton swipeButton;
    Boolean swipeState = false;
    Point size;
    float displayHeight;

    Boolean emojiActive = false;
    EmojiKeyboardLayout emojiKeyboardLayout;


    public int setAnimationSendingDuration;

    public SoundPool soundPool;
    Boolean readyToCheckSize = false;
    private int delSound1, cantDelSound2, sendSound3, sendingSound4, sendingSound5;


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
        setContentView(R.layout.activity_ephemeral_chain_message);

        //  setContentView(R.layout.new_ephemeral_messaging_layout);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);


        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        app_context_layout = findViewById(R.id.app_context_layout);

        rootView = findViewById(R.id.app_context_layout);

        message_box = findViewById(R.id.message_box);

        emoji = findViewById(R.id.emoji);

        emojiKeyboardLayout = findViewById(R.id.emoji_keyboard_layout);

        message_box.setUseSystemDefault(true);
        //message_box.setFocusedByDefault(false);

        Intent intent = getIntent();

        final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");

       /* message_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_box.setFocusable(true);
            }
        });*/




        /*emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EphemeralChainMessageActivity.this,TestJavaEmojiKeyBoard.class));
                //emojiToggle = true;
            }
        });*/

        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!emojiActive) {

                    if (message_box.isSoftKeyboardVisible())
                        message_box.hideSoftKeyboard();

                    emojiKeyboardLayout.prepareKeyboard(EphemeralChainMessageActivity.this, message_box);
                    emojiKeyboardLayout.setVisibility(View.VISIBLE);

                    message_box.setFocusable(true);
                    emoji.setBackground(getDrawable(R.drawable.ic_keyboard));

                    emojiActive = true;
                } else {


                    //  emojiKeyboardLayout.prepareKeyboard(TestJavaEmojiKeyBoard.this, emojiconEditText);
                    emojiKeyboardLayout.setVisibility(View.GONE);

                    message_box.showSoftKeyboard();

                    emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));

                    emojiActive = false;

                }

            }
        });

        camera = findViewById(R.id.camera);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EphemeralChainMessageActivity.this, CameraActivity.class);

                intent.putExtra("userChattingWithId", userIdChattingWith);
                startActivity(intent);
            }
        });


        // The callback can be enabled or disabled here or in handleOnBackPressed()


        message_box.addOnSoftKeyboardListener(new EmojiEditText.OnSoftKeyboardListener() {
            @Override
            public void onSoftKeyboardDisplay() {
                emojiKeyboardLayout.setVisibility(View.GONE);
                emojiActive = false;
                emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));

            }

            @Override
            public void onSoftKeyboardHidden() {


            }
        });









        /*Toast.makeText(EphemeralChainMessageActivity.this,"Height : "+
                size.y+"  Width : "+size.x, Toast.LENGTH_SHORT).show();*/

        // sound pool

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


        username = findViewById(R.id.username);

        message_text = findViewById(R.id.message_text);


        message_text_dummy = findViewById(R.id.message_text_dummy);

        message_text_two = findViewById(R.id.message_text_two);
        message_text_three = findViewById(R.id.message_text_three);

        your_sent_msg = findViewById(R.id.your_sent_msg);

        read_message_reply = findViewById(R.id.read_message_reply);


        chainBtn = findViewById(R.id.chain_message_btn);


        chainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (message_box.getText().toString().length() > 0) {
                    if (typingID == MSG_TYPING_BOX_ID) {
                        soundPool.play(sendingSound5, 1, 1, 0, 0, 1);


                        messageStr = message_box.getText().toString();
                        message_text_sender.setText(message_box.getText());
                        typingID = MSG_TYPING_BOX_TWO_ID;
                        message_box.setText("");
                        message_text_two_sender.setVisibility(View.VISIBLE);

                        message_text_sender_dummy.setVisibility(View.GONE);
                        message_text_sender.setAlpha(0.2f);

                        // message_text_sender.setTextColor(Color.parseColor("#e6e200"));

                        // message_text_two_sender.setLayoutParams;

                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        Toast.makeText(EphemeralChainMessageActivity.this, "Chained", Toast.LENGTH_SHORT).show();

                    } else if (typingID == MSG_TYPING_BOX_TWO_ID) {
                        soundPool.play(sendingSound5, 1, 1, 0, 0, 1);

                        messageStrTwo = message_box.getText().toString();
                        message_text_two_sender.setText(message_box.getText());
                        typingID = MSG_TYPING_BOX_THREE_ID;
                        message_box.setText("");
                        message_text_three_sender.setVisibility(View.VISIBLE);


                        message_text_two_sender.setAlpha(0.2f);
                        // message_text_two_sender.setTextColor(Color.parseColor("#e6e200"));


                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        Toast.makeText(EphemeralChainMessageActivity.this, "Chained", Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });


        message_text_sender = findViewById(R.id.message_text_sender);
        message_text_sender_dummy = findViewById(R.id.message_text_sender_dummy);

        message_text_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (typingID) {
                    case 1:


                        message_text_sender.setAlpha(0.2f);

                        messageStr = message_box.getText().toString();
                        break;

                    case 2:

                        if (message_text_two_sender.getText().length() > 0) {

                            chainBtn.setVisibility(View.GONE);
                            chain_pulse.setVisibility(View.GONE);

                        }


                        message_text_two_sender.setAlpha(0.2f);
                        messageStrTwo = message_box.getText().toString();

                        break;

                    case 3:


                        if (message_text_two_sender.getText().length() > 0) {

                            chainBtn.setVisibility(View.GONE);
                            chain_pulse.setVisibility(View.GONE);

                        }

                        if (message_text_three_sender.getText().length() <= 0)
                            message_text_three_sender.setVisibility(View.GONE);

                        message_text_three_sender.setAlpha(0.2f);
                        messageStrThree = message_box.getText().toString();
                        break;

                }


                typingID = MSG_TYPING_BOX_ID;
                message_text_sender.setFocusable(true);
                message_text_sender.setAlpha(0.5f);

                message_box.setText(message_text_sender.getText());


            }
        });

        message_text_two_sender = findViewById(R.id.message_text_two_sender);

        message_text_two_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (typingID) {
                    case 1:


                        message_text_sender.setAlpha(0.2f);

                        messageStr = message_box.getText().toString();
                        break;

                    case 2:


                        message_text_two_sender.setAlpha(0.2f);
                        messageStrTwo = message_box.getText().toString();

                        break;

                    case 3:
                        if (message_text_three_sender.getText().length() <= 0)
                            message_text_three_sender.setVisibility(View.GONE);

                        message_text_three_sender.setAlpha(0.2f);
                        messageStrThree = message_box.getText().toString();
                        break;

                }


                typingID = MSG_TYPING_BOX_TWO_ID;
                message_text_two_sender.setFocusable(true);
                message_text_two_sender.setAlpha(0.5f);

                message_box.setText(message_text_two_sender.getText());
            }
        });
        message_text_three_sender = findViewById(R.id.message_text_three_sender);

        message_text_three_sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                switch (typingID) {
                    case 1:
                        message_text_sender.setAlpha(0.2f);

                        messageStr = message_box.getText().toString();
                        break;

                    case 2:
                        message_text_two_sender.setAlpha(0.2f);
                        messageStrTwo = message_box.getText().toString();

                        break;

                    case 3:
                        message_text_three_sender.setAlpha(0.2f);
                        messageStrThree = message_box.getText().toString();
                        break;

                }


                typingID = MSG_TYPING_BOX_THREE_ID;
                message_text_three_sender.setFocusable(true);
                message_text_three_sender.setAlpha(0.5f);
                message_box.setText(message_text_three_sender.getText());
            }
        });

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


        bottom_bar = findViewById(R.id.bottom_bar);

        message_space = findViewById(R.id.message_space);

        reply_tag = findViewById(R.id.reply_tag);

        message_counter_layout = findViewById(R.id.message_counter_layout);

        message_bar = findViewById(R.id.message_bar);

        message_counter = findViewById(R.id.message_counter);

        window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        message_counter_background = findViewById(R.id.message_counter_background);

        message_no_message_counter_background = findViewById(R.id.message_no_message_counter_background);


        read_message_reply_layout = findViewById(R.id.read_message_reply_layout);

        profile_pic = findViewById(R.id.profile_pic);

        read_message_back = findViewById(R.id.read_message_back);


        sender_reply_body = findViewById(R.id.sender_reply_body);


        profile_pic.setBlur(0);

        message_pulse = findViewById(R.id.message_pulse);

        sending_progress_bar = findViewById(R.id.send_pending);

        chain_pulse = findViewById(R.id.chain_pulse);

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
                if (active) {
                    //   swipeState = active;
                    setChainBtn = true;
                    if (!isMessageRunning)
                        readNextMessage();
                    swipeButton.setBackground(getDrawable(R.drawable.message_swipe_bg));
                    Toast.makeText(EphemeralChainMessageActivity.this, "Message chained", Toast.LENGTH_SHORT).show();
                } else {
                    setChainBtn = false;
                    Toast.makeText(EphemeralChainMessageActivity.this, "Message de-chained!", Toast.LENGTH_SHORT).show();
                }

            }
        });


        Button liveBtn = findViewById(R.id.photo_btn_bg);


        messageKeySender = FirebaseAuth.getInstance().getUid() + userIdChattingWith;
        messageKeyReceiver = userIdChattingWith + FirebaseAuth.getInstance().getUid();


        liveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent intent1 = new Intent(EphemeralChainMessageActivity.this,LiveMessagingActivity.class);
                intent1.putExtra("username",usernameChattingWith);
                intent1.putExtra("userid",userIdChattingWith);
                intent1.putExtra("photo",photo);

                DatabaseReference senderDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference("LiveMessages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);

                DatabaseReference receiverDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference("LiveMessages")
                        .child(userIdChattingWith)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("messageLive", "");

                senderDatabaseReference.updateChildren(hashMap);
                receiverDatabaseReference.updateChildren(hashMap);*/


                //     startActivity(intent1);






/*
                final DatabaseReference senderDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference("LiveMessages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);

               final String pushKey = senderDatabaseReference.push().getKey();

                        final DatabaseReference receiverDatabaseReference = FirebaseDatabase.getInstance()
                        .getReference("LiveMessages")
                        .child(userIdChattingWith)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

               final  HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("messageLive","");
                hashMap.put("messageKey",messageKeyReceiver);


                senderDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(dataSnapshot.exists()) {
                            try {
                                LiveMessage liveMessage = dataSnapshot.getValue(LiveMessage.class);
                                if (liveMessage.getMessageKey().equals(messageKeySender)) {

                                    Toast.makeText(EphemeralChainMessageActivity.this,"message key matched!!! :D",Toast.LENGTH_SHORT).show();

                                    Intent fireVideoIntent = new Intent(EphemeralChainMessageActivity.this, FireVideo.class);
                                    fireVideoIntent.putExtra("userChattingWithId", userIdChattingWith);
                                    startActivity(fireVideoIntent);
                                } else {

                                    Toast.makeText(EphemeralChainMessageActivity.this,"message key not matching",Toast.LENGTH_SHORT).show();
                                    senderDatabaseReference*//*.child(pushKey)*//*.updateChildren(hashMap);
                                    receiverDatabaseReference*//*.child(pushKey)*//*.updateChildren(hashMap);

                                    Intent fireVideoIntent = new Intent(EphemeralChainMessageActivity.this, FireVideo.class);
                                    fireVideoIntent.putExtra("userChattingWithId", userIdChattingWith);
                                    startActivity(fireVideoIntent);
                                }

                            }catch (NullPointerException e)
                            {
                                //
                                *//*senderDatabaseReference*//**//*.child(pushKey)*//**//*.updateChildren(hashMap);
                                receiverDatabaseReference*//**//*.child(pushKey)*//**//*.updateChildren(hashMap);

                                Intent fireVideoIntent = new Intent(EphemeralChainMessageActivity.this, FireVideo.class);
                                fireVideoIntent.putExtra("userChattingWithId", userIdChattingWith);
                                startActivity(fireVideoIntent);*//*
                            }
                        }

                        else
                        {

                            Toast.makeText(EphemeralChainMessageActivity.this,"Datasnapshot doesnt exist",Toast.LENGTH_SHORT).show();

                            senderDatabaseReference*//*.child(pushKey)*//*.updateChildren(hashMap);
                            receiverDatabaseReference*//*.child(pushKey)*//*.updateChildren(hashMap);

                            Intent fireVideoIntent = new Intent(EphemeralChainMessageActivity.this, FireVideo.class);
                            fireVideoIntent.putExtra("userChattingWithId", userIdChattingWith);
                            startActivity(fireVideoIntent);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/


                Intent fireVideoIntent = new Intent(EphemeralChainMessageActivity.this, FireVideo.class);
                fireVideoIntent.putExtra("userChattingWithId", userIdChattingWith);
                fireVideoIntent.putExtra("username", usernameChattingWith);
                fireVideoIntent.putExtra("photo", photo);
                startActivity(fireVideoIntent);


            }
        });


        setAnimationSendingDuration = 600;


        archive_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(EphemeralChainMessageActivity.this, ArchiveActivity.class);
                intent.putExtra("userIdChattingWith", userIdChattingWith);

                startActivity(intent);

            }
        });


        reply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!lastMessageID.equals("none")) {
                    isSenderReplying = true;
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
                isSenderReplying = false;
                reply_tag.setVisibility(View.GONE);
            }
        });


        messageList = new ArrayList<>();
        sendMessageList = new ArrayList<>();


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


        //  delSound1 = soundPool.load(this,R.raw.communication_channel,1 );
        delSound1 = soundPool.load(this, R.raw.forsure, 1);
        cantDelSound2 = soundPool.load(this, R.raw.knuckle, 1);
        //   sendSound3 = soundPool.load(this,R.raw.open_ended,1);
        sendSound3 = soundPool.load(this, R.raw.justmaybe, 1);
        sendingSound4 = soundPool.load(this, R.raw.youwouldntbelieve, 1);

        sendingSound5 = soundPool.load(this, R.raw.knob, 1);

        if (!photo.equals("default"))
            Glide.with(EphemeralChainMessageActivity.this).load(photo)/*.transition(withCrossFade())*/.apply(new RequestOptions().override(width, height)).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                    if (imageReady) {
                        profile_pic.animate().alpha(1.0f).setDuration(750).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                imageLoaded = true;
                            }
                        });

                     //   read_message_back.animate().alpha(0.0f);

                        read_message_back.setAlpha(0.0f);

                    }


                    return false;
                }
            }).into(profile_pic);


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    User user = dataSnapshot.getValue(User.class);

                    currentUsername = user.getUsername();
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

                /**
                 * Uncomment the following line to only read received messages and not sent messages.
                 */
                after_count = filterList(chatMessageList);


                /*if(after_count>before_count) {
                    soundPool.play(sendingSound4, 1, 1, 0, 0, 1);


                    before_count = after_count;
                }

                else
                    after_count = messageList.size();*/


                if (messageList.size() > 1) {
                    swipeButton = findViewById(R.id.swipeBtn);
                    swipeButton.setEnabled(true);
                    swipeButton.setVisibility(View.VISIBLE);
                }


                if (messageList.size() < 1) {


                    ephemeralMessageViewModel.setChatPending(false);

                    swipeButton.setEnabled(false);

                    swipeButton.setVisibility(View.GONE);

                   // username.setVisibility(View.VISIBLE);


                    //  swipeButton = null;


                    /**
                     *  Automatically queues the messages after button is explicitely triggered,
                     *  after a message in received when this activity is opened,
                     *  unless we add the following
                     *  setChainBtn = false;
                     **/


                    chainBtn.setVisibility(View.GONE);
                    chain_pulse.setVisibility(View.GONE);
                    //
                    //  ephemeralMessageViewModel.setChatSeen();
                    message_counter_background.setVisibility(View.GONE);
                    message_pulse.setVisibility(View.GONE);
                    message_no_message_counter_background.setVisibility(View.VISIBLE);
                    message_counter.setText("");


                    message_counter.setBackground(getDrawable(R.drawable.message_counter_drawable));
                    if (!isMessageRunning)
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));


                    // message_counter.setVisibility(View.GONE);
                } else {

                    //  ephemeralMessageViewModel.setChatPending(true);

                    /*swipeButton = findViewById(R.id.swipeBtn);
                    swipeButton.setEnabled(true);
                    swipeButton.setVisibility(View.VISIBLE);*/
                    message_counter_background.setVisibility(View.VISIBLE);
                    message_no_message_counter_background.setVisibility(View.GONE);
                    message_counter.setBackground(getDrawable(R.drawable.message_counter_new_message_drawable));


                    if (!isMessageRunning) {
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));
                        message_pulse.setVisibility(View.VISIBLE);
                    }

                    if (messageList.size() > 99) {
                        message_counter.setText("99+");
                        message_counter.setTextSize(26);
                    } else {
                        message_counter.setText(messageList.size() + "");
                        message_counter.setTextSize(32);
                    }
                }


                if (sendMessageList.size() > 0) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            int size = sendMessageList.size();
/*//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                                    .getReference("SentStatus")
                                    .child(userIdChattingWith)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(sendMessageList.get(size - 1).getMessageId());

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                    DeliveryStatus deliveryStatus = dataSnapshot.getValue(DeliveryStatus.class);

                                    try {
                                        if (deliveryStatus.getSentStatus().equals("sending")) {
                                            sending_progress_bar.setVisibility(View.VISIBLE);
                                            archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorPending));
                                        } else {
                                            archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorWhite));
                                            sending_progress_bar.setVisibility(View.GONE);
                                        }
                                    }catch (Exception e)
                                    {
                                        archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorWhite));
                                        sending_progress_bar.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });*/
                            if (sendMessageList.get(size - 1).getSentStatus().equals("sending")) {
                                sending_progress_bar.setVisibility(View.VISIBLE);
                                archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorPending));
                            } else {
                                archive_btn.setBackgroundTintList(ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorWhite));
                                sending_progress_bar.setVisibility(View.GONE);
                            }

                        }
                    }, 2000);


                }

                //    filterList(chatMessageList);
                // threadPending = ephemeralMessageViewModel.isThreadRunning();
                // setIsMessageRunning(threadPending);


            }
        });

        /*message_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message_box.setEnabled(true);
            }
        });*/


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
                message_text_two_sender.setTranslationY(0);
                message_text_three_sender.setTranslationY(0);


                //  message_text_sender.animateText(s);


                if (s.toString().trim().length() > 0) {

                    imageReady = false;
                    sendButton.setAlpha(1.0f);

                    if (!getIsMessageRunning()) {


                        sendButton.setEnabled(true);
                        switch (typingID) {
                            case 1:
                                message_text_sender.setAlpha(0.5f);
                                message_text_sender.setText(s);
                                if (message_text_three_sender.getText().length() <= 0 && message_text_two_sender.getText().length() <= 0) {
                                    chainBtn.setVisibility(View.VISIBLE);
                                    chain_pulse.setVisibility(View.VISIBLE);
                                }
                                break;

                            case 2:
                                message_text_two_sender.setAlpha(0.5f);
                                message_text_two_sender.setText(s);
                                if (message_text_three_sender.getText().length() <= 0) {
                                    chainBtn.setVisibility(View.VISIBLE);
                                    chain_pulse.setVisibility(View.VISIBLE);
                                }
                                break;

                            case 3:
                                message_text_three_sender.setAlpha(0.5f);
                                message_text_three_sender.setText(s);
                                break;

                        }
                        message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));

                        profile_pic.animate().alpha(opacity).setDuration(250);
                        read_message_back.animate().alpha(1.0f).setDuration(750);

                        if (imageLoaded)
                            profile_pic.setBlur(4);


                    }


                } else if (s.toString().trim().length() == 0 && typingID == MSG_TYPING_BOX_ID) {
                    if (!getIsMessageRunning()) {
                        // message_text_sender.setAlpha(0.0f);

                        //  imageReady = false;
                        chainBtn.setVisibility(View.GONE);
                        chain_pulse.setVisibility(View.GONE);
                        sendButton.setAlpha(0.15f);

                        profile_pic.animate().alpha(1.0f).setDuration(setAnimationSendingDuration * 2 + 650);
                        read_message_back.setAlpha(0.0f);

                        if (imageLoaded)
                            profile_pic.setBlur(0);


                        // profile_pic.setBlur(0);
                        if (messageList.size() < 1)
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                        else
                            message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                        //message_text_sender.setText("");

                    }

                    switch (typingID) {


                        case 1:
                            sendButton.setEnabled(false);
                            //  message_text_sender.setText(s);
                            message_text_sender.animate().alpha(0.0f).setDuration(250);
                            break;

                        case 2:
                            typingID = 1;
                            break;

                        case 3:
                            typingID = 2;
                            break;

                    }


                } else {
                    switch (typingID) {


                        case MSG_TYPING_BOX_ID:
                            message_text_sender.setText("");
                            break;

                        case MSG_TYPING_BOX_TWO_ID:
                            message_text_two_sender.setText("");
                            break;

                        case MSG_TYPING_BOX_THREE_ID:
                            message_text_three_sender.setText("");
                            break;

                    }
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


                soundPool.play(sendSound3, 1, 1, 0, 0, 1);

                sending_progress_bar.setVisibility(View.VISIBLE);
                archive_btn.setBackgroundTintList
                        (ContextCompat.getColorStateList(EphemeralChainMessageActivity.this, R.color.colorPending));


                message_text_sender.setTextColor(Color.parseColor("#ffffff"));
                message_text_two_sender.setTextColor(Color.parseColor("#ffffff"));

                message_text_two_sender.setVisibility(View.GONE);
                message_text_three_sender.setVisibility(View.GONE);


                if (typingID == MSG_TYPING_BOX_ID)
                    messageStr = message_box.getText().toString();
                else if (typingID == MSG_TYPING_BOX_TWO_ID)
                    messageStrTwo = message_box.getText().toString();
                else
                    messageStrThree = message_box.getText().toString();
                typingID = MSG_TYPING_BOX_ID;

                reply_tag.setVisibility(View.GONE);
                reply_btn.setVisibility(View.GONE);


                //   bottom_bar.setBackground(getDrawable(R.color.gradientEnd));


                message_text_sender.setAlpha(1.0f);

                message_text_two_sender.setAlpha(1.0f);

                message_text_three_sender.setAlpha(1.0f);


                //delSound1, cantDelSound2, sendSound3,sendingSound4, sendingSound5;


                send_arrow.setTranslationY(0);
                send_arrow_two.setTranslationY(0);

                send_arrow.setAlpha(1.0f);
                send_arrow_two.setAlpha(0.0f);


                //messageStr = message_box.getText().toString();

                //messageStr = message_text_sender.getText().toString();


                //messageStrTwo  = message_text_two_sender.getText().toString();

                //messageStrThree = message_text_three_sender.getText().toString();


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


                ChatMessage chatMessageNew = new ChatMessage();
                Boolean setIfMessageTwo = false, setIfMessageThree = false;
                String setMessageTextTwo;
                String setMessageTextThree;
                if (messageStrTwo.length() > 0) {
                    setIfMessageTwo = true;
                    setMessageTextTwo = messageStrTwo;
                    //  Toast.makeText(EphemeralChainMessageActivity.this,"Msg 2: "+setMessageTextTwo, Toast.LENGTH_SHORT);
                } else {
                    setMessageTextTwo = "";
                }

                if (messageStrThree.length() > 0) {
                    setIfMessageThree = true;
                    setMessageTextThree = messageStrThree;
                } else {
                    setMessageTextThree = "";
                }

                if (isSenderReplying) {


                    chatMessageNew.setMessageId("notAssigned");
                    chatMessageNew.setMessageText(messageStr);
                    chatMessageNew.setSenderId("notAssigned");
                    chatMessageNew.setReceiverId("notAssigned");
                    chatMessageNew.setSentStatus("sending");
                    chatMessageNew.setSeen("notseen");
                    chatMessageNew.setPhotourl("none");
                    chatMessageNew.setReplyID(lastMessageID);
                    chatMessageNew.setSenderReplyMessage(senderReplyMessage);
                    chatMessageNew.setIfMessageTwo(setIfMessageTwo);
                    chatMessageNew.setMessageTextTwo(setMessageTextTwo);
                    chatMessageNew.setIfMessageThree(setIfMessageThree);
                    chatMessageNew.setMessageTextThree(setMessageTextThree);

                    lastMessageID = "none";
                    isSenderReplying = false;
                } else {
                    chatMessageNew.setMessageId("notAssigned");
                    chatMessageNew.setMessageText(messageStr);
                    chatMessageNew.setSenderId("notAssigned");
                    chatMessageNew.setReceiverId("notAssigned");
                    chatMessageNew.setSentStatus("sending");
                    chatMessageNew.setSeen("notseen");
                    chatMessageNew.setPhotourl("none");
                    chatMessageNew.setReplyID("none");
                    chatMessageNew.setSenderReplyMessage("none");
                    chatMessageNew.setIfMessageTwo(setIfMessageTwo);
                    chatMessageNew.setMessageTextTwo(setMessageTextTwo);
                    chatMessageNew.setIfMessageThree(setIfMessageThree);
                    chatMessageNew.setMessageTextThree(setMessageTextThree);
                }

                //End


              /*  ChatMessage chatMessage = new ChatMessage();
                if(isSenderReplying) {

                    chatMessage.setMessageId("notAssigned");
                    chatMessage.setMessageText(messageStr);
                    chatMessage.setSenderId("notAssigned");
                    chatMessage.setReceiverId("notAssigned");
                    chatMessage.setSentStatus("sending");
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
                    chatMessage.setSentStatus("sending");
                    chatMessage.setSeen("notseen");
                    chatMessage.setPhotourl("none");
                    chatMessage.setReplyID("none");
                    chatMessage.setSenderReplyMessage("none");

                }*/


                ephemeralMessageViewModel.sendMessage(chatMessageNew);
                ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);

                message_box.setText("");


                send_arrow_two.animate().translationY(-displayHeight).setDuration(setAnimationSendingDuration);
                send_arrow.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);

                /*message_text_sender.setText("  *  *  *  ");
                message_text_two_sender.setText("  *  *  *  ");
                message_text_three_sender.setText("  *  *  *  ");*/

                message_text_sender.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);
                message_text_two_sender.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);
                message_text_three_sender.animate().translationY(-displayHeight - (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        message_text_sender.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);
                        message_text_two_sender.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);
                        message_text_three_sender.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);


                        send_arrow_two.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);
                        send_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);


                    }
                }, 100);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        message_text_dummy.setVisibility(View.VISIBLE);
                        message_text_sender_dummy.setVisibility(View.VISIBLE);

                    }
                }, setAnimationSendingDuration);

                //message_text_sender.setAlpha(0.0f);
                if (messageList.size() < 1)
                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                else
                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        message_text_sender.setText("");
                        message_text_two_sender.setText("");
                        message_text_three_sender.setText("");


                        messageStr = "";
                        messageStrTwo = "";
                        messageStrThree = "";

                        //  profile_pic.animate().alpha(1.0f).setDuration(800);

                        /*if(imageLoaded)
                            profile_pic.setBlur(0);*/

                        imageReady = true;

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


    private int filterList(List<ChatMessage> chatMessageList) {

        List<ChatMessage> messageListReading = chatMessageList;
        //if(!getIsMessageRunning())
        messageList.clear();

        try {

            for (ChatMessage chatMessage : messageListReading) {
                if (chatMessage.getSenderId().equals(userIdChattingWith))
                    messageList.add(chatMessage);

                else if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){

                try {

                    if(chatMessage.getMessageId()!=null)
                        sendMessageList.add(chatMessage);

                    else
                    {
                        messageListReading.remove(chatMessage);
                    }
                }catch (Exception e)
                {
                    messageListReading.remove(chatMessage);
                }

                }


            }


        } catch (Exception e) {
          //  Toast.makeText(EphemeralChainMessageActivity.this, "Internet is crawling! You might get issues using the App", Toast.LENGTH_LONG).show();
        }


        return messageList.size();


    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {


        switch (event.getAction() & MotionEvent.ACTION_MASK) {


            case MotionEvent.ACTION_UP:
                if (!getIsMessageRunning()) {

                    readNextMessage();
                }

                break;
        }


        return true;
    }

    private void readNextMessage() {
        if (messageList.size() == 0) {
            username.setVisibility(View.VISIBLE);
            Toast.makeText(EphemeralChainMessageActivity.this, "You have no messages to read!", Toast.LENGTH_SHORT).show();
        }

        else {
            soundPool.play(delSound1, 1, 1, 0, 0, 1);

            message_text_dummy.setVisibility(View.VISIBLE);

            message_text.setTranslationY(0);
            message_text_two.setTranslationY(0);
            message_text_three.setTranslationY(0);

            reply_btn.setVisibility(View.GONE);
            message_text_two.setVisibility(View.GONE);
            message_text_three.setVisibility(View.GONE);
            messageTwoPresent = false;
            messageThreePresent = false;
            username.setAlpha(0.0f);

            receiver_arrow.setTranslationY(0);
            receiver_arrow.setAlpha(1.0f);
            receiver_arrow.animate().translationY(displayHeight + (displayHeight) / 4).setDuration(setAnimationSendingDuration - 50);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);

                }
            }, 100);


            imageReady = false;
            setIsMessageRunning(true);
            message_text_sender.setAlpha(0.0f);
            message_text_two_sender.setAlpha(0.0f);
            message_text_three_sender.setAlpha(0.0f);
            message_pulse.setVisibility(View.GONE);
            final ChatMessage chatMessage = messageList.get(0);
            lastMessageID = chatMessage.getMessageId();

            senderReplyID = chatMessage.getReplyID();
            senderReplyMessage = chatMessage.getMessageText();


            message_bar.setBackground(getDrawable(R.drawable.message_identifier_drawable));
            liveMessage = chatMessage.getMessageText();
            if (chatMessage.getIfMessageTwo()) {
                liveMessageTwo = chatMessage.getMessageTextTwo();
                messageTwoPresent = true;
            }

            if (chatMessage.getIfMessageThree()) {
                liveMessageThree = chatMessage.getMessageTextThree();
                messageThreePresent = true;
            }


            if (messageThreePresent && messageTwoPresent)
                blinkInterval = (liveMessage.length() * 50 + 750 +
                        liveMessageTwo.length() * 50 + 750 +
                        liveMessageThree.length() * 50 + 750) / 10;

            else if (messageTwoPresent)
                blinkInterval = (liveMessage.length() * 50 + 750 +
                        liveMessageTwo.length() * 50 + 750) / 10;

            else if (messageThreePresent)
                blinkInterval = (liveMessage.length() * 50 + 750 +
                        liveMessageThree.length() * 50 + 750) / 10;

            else
                blinkInterval = (liveMessage.length() * 50 + 750) / 10;






                           /*// This is for type writer
                            message_text_typewriter.setCharacterDelay(75);
                            message_text_typewriter.animate().alpha(1.0f).setDuration(750);
                            message_text_typewriter.animateText(liveMessage);*/

            deletionHandler = new Handler();
            runnable = new Runnable() {
                @Override
                public void run() {


                    if (!messageTwoPresent && !messageThreePresent) {
                        message_text.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);
                        message_text_two.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);
                        message_text_three.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);


                        /*message_text.animate().translationY(3000).setDuration(setAnimationSendingDuration+200);
                        message_text_two.animate().translationY(3000).setDuration(setAnimationSendingDuration+200);
                        message_text_three.animate().translationY(3000).setDuration(setAnimationSendingDuration+200);*/


                        /**
                         * The following commented code is used to animate the receiving arrow in the very end.
                         * Uncomment it to get the effect! :)
                         */

                        /*if(messageList.size()==0) {
                            receiver_arrow.setTranslationY(0);
                            receiver_arrow.setAlpha(1.0f);

                            receiver_arrow.animate().translationY(4000).setDuration(setAnimationSendingDuration);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                    receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);

                                }
                            }, 100);
                        }*/


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                if (messageList.size() < 1)
                                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_no_message));
                                else
                                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_reply_drawable));

                                message_bar.setVisibility(View.VISIBLE);


                                message_text_sender.animate().alpha(0.5f).setDuration(250);
                                if (typingID == MSG_TYPING_BOX_TWO_ID)
                                    message_text_two_sender.animate().alpha(0.5f).setDuration(250);

                                if (typingID == MSG_TYPING_BOX_THREE_ID)
                                    message_text_three_sender.animate().alpha(0.5f).setDuration(250);


                                receiver_arrow.setTranslationY(0);
                                receiver_arrow.setAlpha(1.0f);
                                receiver_arrow.animate().translationY(displayHeight + (displayHeight) / 4).setDuration(setAnimationSendingDuration);
                                receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration);


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        username.setAlpha(1.0f);
                                        message_text.setText("");
                                        message_text.setAlpha(0.0f);

                                        message_text_two.setText("");
                                        message_text_two.setAlpha(0.0f);

                                        message_text_three.setText("");
                                        message_text_three.setAlpha(0.0f);

                                    }
                                }, 250);


                                setIsMessageRunning(false);
                                if (message_text_sender.getText().length() > 0 ||
                                        message_text_two_sender.getText().length() > 0 ||
                                        message_text_three_sender.getText().length() > 0) {
                                    message_bar.setBackground(getDrawable(R.drawable.message_identifier_sender_drawable));
                                } else {
                                    if (imageLoaded)
                                        profile_pic.setBlur(0);
                                    profile_pic.animate().alpha(1.0f).setDuration(setAnimationSendingDuration + 500);
                                    read_message_back.setAlpha(0.0f);
                                    // username.animate().alpha(1.0f).setDuration(setAnimationSendingDuration+500);
                                }

                                deletionHandler.removeCallbacks(runnable);
                                blinkerHandler.removeCallbacks(blinkerHandlerRunnable);
                                liveMessage = "";


                                if (messageList.size() > 0)
                                    message_pulse.setVisibility(View.VISIBLE);

                                imageReady = true;

                                if (isSeeingReply) {
                                    isSeeingReply = false;
                                    your_sent_msg.setText("");
                                    sender_reply_body.setVisibility(View.GONE);
                                    sender_reply_tag.setVisibility(View.GONE);

                                }
                                //  receiver_arrow.animate().alpha(0.0f).setDuration(3750);

                            /*message_text.animate().translationY(3000).setDuration(setAnimationSendingDuration+200);
                            message_text_two.animate().translationY(3000).setDuration(setAnimationSendingDuration+200);
                            message_text_three.animate().translationY(3000).setDuration(setAnimationSendingDuration+200);*/

                                if (setChainBtn) {
                                /*message_text.setTranslationY(0);
                                message_text_two.setTranslationY(0);
                                message_text_three.setTranslationY(0);*/
                                username.setVisibility(View.INVISIBLE);
                                    readNextMessage();
                                } else {
                                    username.setVisibility(View.VISIBLE);
                                /*receiver_arrow.setTranslationY(0);
                                receiver_arrow.setAlpha(1.0f);
                                receiver_arrow.animate().translationY(displayHeight+(displayHeight)/4).setDuration(setAnimationSendingDuration);
                                receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);*/
                                /*new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {


                                        receiver_arrow.animate().alpha(0.0f).setDuration(setAnimationSendingDuration - 125);

                                    }
                                }, 100);*/

                                }


                            }
                        }, setAnimationSendingDuration - 250);

                    } else if (messageTwoPresent) {

                        message_text.setAlpha(0.2f);
                        // message_text.setTextSize(4);

                        message_text_dummy.setVisibility(View.GONE);
                        message_text_two.setVisibility(View.VISIBLE);
                        message_text_two.animate().setDuration(1000).alpha(1.0f);
                        messageTwoPresent = false;
                        deletionHandler.postDelayed(runnable, liveMessageTwo.length() * 75 + 1250);

                    } else if (messageThreePresent) {
                        message_text.setAlpha(0.1f);
                        message_text_two.setAlpha(0.2f);
                        // message_text_two.setTextSize(4);

                        message_text_dummy.setVisibility(View.GONE);
                        message_text_three.setVisibility(View.VISIBLE);
                        message_text_three.animate().setDuration(1000).alpha(1.0f);
                        messageThreePresent = false;
                        deletionHandler.postDelayed(runnable, liveMessageThree.length() * 75 + 1250);
                    }


                    if (!lastMessageID.equals("none")) {

                        reply_btn.setVisibility(View.VISIBLE);
                        //   sender_reply_tag.setVisibility(View.VISIBLE);

                    } else
                        reply_btn.setVisibility(View.GONE);


                }


            };


            username.setAlpha(0.0f);

            if (imageLoaded)
                profile_pic.setBlur(4);

            profile_pic.animate().alpha(opacity).setDuration(250);
            read_message_back.animate().alpha(1.0f).setDuration(250);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    if (senderReplyID.equals("none")) {


                        blink();
                        message_text.setText(liveMessage);
                        if (messageTwoPresent)
                            message_text_two.setText(liveMessageTwo);

                        if (messageThreePresent)
                            message_text_three.setText(liveMessageThree);

                        // message_text.animateText(liveMessage);
                        message_text.animate().alpha(1.0f).setDuration(250);

                        ephemeralMessageViewModel.deleteMessage(chatMessage, false);

                        /*if(!messageTwoPresent||!messageThreePresent)
                            deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 2000);
                        else*/
                        deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);

                    } else {

                        read_message_reply_layout.setVisibility(View.VISIBLE);
                        isSeeingReply = true;
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
                                if (messageTwoPresent)
                                    message_text_two.setText(liveMessageTwo);

                                if (messageThreePresent)
                                    message_text_three.setText(liveMessageThree);

                                message_text.animate().alpha(1.0f).setDuration(250);

                                /*if(!messageTwoPresent||!messageThreePresent)
                                    deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);
                                else*/
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
                                if (messageTwoPresent)
                                    message_text_two.setText(liveMessageTwo);

                                if (messageThreePresent)
                                    message_text_three.setText(liveMessageThree);
                                message_text.animate().alpha(1.0f).setDuration(250);
                                if (!messageTwoPresent || !messageThreePresent)
                                    deletionHandler.postDelayed(runnable, liveMessage.length() * 75 + 750);
                                else
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
                } else {

                    message_bar.setVisibility(View.VISIBLE);
                }

               /* if (read_message_back.getVisibility() == View.VISIBLE) {
                    read_message_back.setVisibility(View.INVISIBLE);
                } else {

                    read_message_back.setVisibility(View.VISIBLE);
                }*/

                if (isMessageRunning)
                    blink();

                else {
                    blinkerThread.interrupt();
                    blinkerHandler.removeCallbacks(blinkerHandlerRunnable);

                    message_bar.setVisibility(View.VISIBLE);
                   // read_message_back.setVisibility(View.VISIBLE);
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


    public void setProfilePicOpacity(float opacity) {
        this.opacity = opacity;
    }

    public float getProfilePicOpacity() {
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
        } catch (NullPointerException ne) {
            //No need to address
        }


    }

/*    @Override
    public void onBackPressed() {

        if (emojiActive) {
            emojiKeyboardLayout.setVisibility(View.GONE);


            emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));

            emojiActive = false;

        } else {
            super.onBackPressed();
        }
        // super.onBackPressed();
    }*/


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK)   {
            if(emojiActive) {
                emojiKeyboardLayout.setVisibility(View.GONE);


                emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));
              //  Toast.makeText(EphemeralChainMessageActivity.this,"is Emoji Active: "+emojiActive,Toast.LENGTH_SHORT).show();

                emojiActive = false;

            }
            else {
                // super.onBackPressed();
              //  Toast.makeText(EphemeralChainMessageActivity.this,"is Emoji Active: "+emojiActive,Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return true;
    }
}

