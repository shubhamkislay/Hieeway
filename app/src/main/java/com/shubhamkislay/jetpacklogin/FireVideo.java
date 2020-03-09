package com.shubhamkislay.jetpacklogin;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.shubhamkislay.jetpacklogin.Model.LiveMessage;

import java.util.HashMap;
import java.util.Random;

import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.EmojiEditText;
import br.com.instachat.emojilibrary.model.layout.EmojiKeyboardLayout;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

public class FireVideo extends EmojiCompatActivity {

    private RtcEngine mRtcEngine;
    private IRtcEngineEventHandler mRtcEventHandler;
    private DatabaseReference databaseReferenceUserChattingWith;
    private DatabaseReference databaseReferenceUser;
    private  String userChattingWithId;
    private String liveMessageKey;
    private RelativeLayout startLiveVideo, stopLiveVideo;
    private RelativeLayout firstPersonVideo, userChattingPersonVideo;
    private TextView startLiveVideoTextView, stopLiveVideoTextView;
    private String key;
    private Boolean start = true;
    private Boolean flagActivityClosure = false;
    private LiveVideoViewModel liveVideoViewModel;

    public String userIdChattingWith;
    public String usernameChattingWith;
    public String photo;
    public EmojiEditText messageBox;
    public EmojiKeyboardLayout emojiKeyboardLayout;
    public TextView username, senderTextView, receiverTextView;
    public LiveMessagingViewModel liveMessagingViewModel;
    public Boolean truncateString = false;
    public Boolean stopObservingLiveMessaging = false;
    public Button emoji;

    public ProgressBar connectingUserVideo, connectedUserVideo ;
    public Handler handler;
    public Runnable runnable;
    public Button backBtn;
    public Boolean typing=false;
    public Boolean emojiActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fire_video);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);



        Intent intent = getIntent();
        userChattingWithId = intent.getStringExtra("userChattingWithId");
        userIdChattingWith = intent.getStringExtra("userChattingWithId");
        usernameChattingWith = intent.getStringExtra("username");
        photo = intent.getStringExtra("photo");

        emojiKeyboardLayout = findViewById(R.id.emoji_keyboard_layout);
        emoji = findViewById(R.id.emoji);
        messageBox = findViewById(R.id.message_box);



        firstPersonVideo = findViewById(R.id.first_person_video);
        userChattingPersonVideo = findViewById(R.id.second_person_video);


        startLiveVideo = findViewById(R.id.startLiveVideo);
        stopLiveVideo = findViewById(R.id.stopLiveVideo);
        startLiveVideoTextView = findViewById(R.id.startLiveVideoText);
        stopLiveVideoTextView = findViewById(R.id.stopLiveVideoText);


        connectingUserVideo = findViewById(R.id.send_pending);

        connectedUserVideo = findViewById(R.id.send_pending_comp);


        emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!emojiActive) {

                    if (messageBox.isSoftKeyboardVisible())
                        messageBox.hideSoftKeyboard();

                    emojiKeyboardLayout.prepareKeyboard(FireVideo.this, messageBox);
                    emojiKeyboardLayout.setVisibility(View.VISIBLE);

                    messageBox.setFocusable(true);
                    emoji.setBackground(getDrawable(R.drawable.ic_keyboard));

                    emojiActive = true;
                }

                else
                {


                    //  emojiKeyboardLayout.prepareKeyboard(TestJavaEmojiKeyBoard.this, emojiconEditText);
                    emojiKeyboardLayout.setVisibility(View.GONE);

                    messageBox.showSoftKeyboard();

                    emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));

                    emojiActive = false;

                }

            }
        });


        messageBox.addOnSoftKeyboardListener(new EmojiEditText.OnSoftKeyboardListener() {
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










        //liveActivity




        username = findViewById(R.id.username);

        backBtn = findViewById(R.id.back_button);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                finish();
            }
        });

        receiverTextView = findViewById(R.id.receiver_message);
        senderTextView = findViewById(R.id.sender_message);

        /*senderTextView.setAlpha(0);
        receiverTextView.setAlpha(0);*/

        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                if(!typing) {

                    liveMessagingViewModel.updateLiveMessage("");
                    senderTextView.setText("");
                    messageBox.setText("");
                }
                else
                {
                    handler.removeCallbacks(runnable);
                }

            }
        };






        username.setText(usernameChattingWith);



        LiveMessagingViewModelFactory liveMessagingViewModelFactory = new LiveMessagingViewModelFactory(userIdChattingWith);
        liveMessagingViewModel = ViewModelProviders.of(this, liveMessagingViewModelFactory).get(LiveMessagingViewModel.class);
        liveMessagingViewModel.getLiveMessage().observe(FireVideo.this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {

                if(!stopObservingLiveMessaging) {


                if(s.length()<1) {
                   // receiverTextView.animate().alpha(0.0f).setDuration(250);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            receiverTextView.setText(s);

                        }
                    }, 500);

                    receiverTextView.setText(s);

                }

                else
                {

                    receiverTextView.setText(s);
                  //  receiverTextView.animate().alpha(1.0f).setDuration(250);

                          }
                }



            }
        });



        startLiveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupSignalling(userChattingWithId);
                flagActivityClosure = true;
                startLiveVideo.setVisibility(View.GONE);


                liveMessagingViewModel.iConnect();

                firstPersonVideo.setVisibility(View.VISIBLE);
                //userChattingPersonVideo.setVisibility(View.VISIBLE);
                connectingUserVideo.setVisibility(View.VISIBLE);
            }
        });




        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {




                if(!stopObservingLiveMessaging) {


                    /*if(s.length()<1)
                        senderTextView.animate().alpha(0.0f).setDuration(250);

                    else
                        senderTextView.animate().alpha(1.0f).setDuration(250);*/

                    handler.removeCallbacks(runnable);
                    typing = true;

                   // senderTextView.setAlpha(1.0f);
                    senderTextView.setText(s);

                    liveMessagingViewModel.updateLiveMessage(s.toString());

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            typing = false;
                            handler.postDelayed(runnable, 1000);

                        }
                    }, 1000);

                }







            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });







        mRtcEventHandler = new IRtcEngineEventHandler() {


            @Override
            public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
                Log.i("uid video",uid+"");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(uid);
                    }
                });
            }


        };

        stopLiveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstPersonVideo.setVisibility(View.GONE);
               // userChattingPersonVideo.setVisibility(View.GONE);
                connectingUserVideo.setVisibility(View.GONE);
                connectedUserVideo.setVisibility(View.GONE);
                stopSignalling();
                startLiveVideo.setVisibility(View.GONE);
                stopLiveVideo.setVisibility(View.GONE);

            }
        });


        LiveVideoViewModelFactory liveVideoViewModelFactory = new LiveVideoViewModelFactory(userChattingWithId);
        liveVideoViewModel = ViewModelProviders.of(this, liveVideoViewModelFactory).get(LiveVideoViewModel.class);
        liveVideoViewModel.getLiveVideoData().observe(this, new Observer<LiveMessage>() {
                    @Override
                    public void onChanged(@Nullable LiveMessage liveMessage) {

                        LiveMessage checkMessage = liveMessage;




                        if(checkMessage.getiConnect() == 1)
                        {
                            connectedUserVideo.setVisibility(View.GONE);
                            connectingUserVideo.setVisibility(View.VISIBLE);
                            userChattingPersonVideo.setVisibility(View.VISIBLE);
                        }

                        else if(checkMessage.getiConnect() == 0) {
                            connectedUserVideo.setVisibility(View.GONE);
                            connectingUserVideo.setVisibility(View.GONE);
                            userChattingPersonVideo.setVisibility(View.GONE);
                        }

                        else
                        {
                        //    connectedUserVideo.setVisibility(View.VISIBLE);
                            userChattingPersonVideo.setVisibility(View.VISIBLE);
                            connectingUserVideo.setVisibility(View.GONE);
                        }



                        try {
                            if (checkMessage.getMessageKey().equals("")) {
                                if (flagActivityClosure)
                                    finish();
                                    // Toast.makeText(FireVideo.this, "Live Video Stopped!", Toast.LENGTH_SHORT).show();

                                else {
                                    //    Toast.makeText(FireVideo.this, "Start Live Video", Toast.LENGTH_SHORT).show();
                                    startLiveVideoTextView.setText("Live Expressions");
                                    start = true;
                                    stopLiveVideo.setVisibility(View.GONE);
                                }


                            }


                        else
                        {
                            /*if(!flagActivityClosure)
                                Toast.makeText(FireVideo.this,"This person is calling you!",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FireVideo.this,"Calling...",Toast.LENGTH_SHORT).show();*/
                            key = liveMessage.getMessageKey();
                            startLiveVideoTextView.setText("JOIN");
                            start = false;
                            stopLiveVideo.setVisibility(View.VISIBLE);

                        }
                        }catch (NullPointerException e)
                        {

                        }

                    }
                });


           //     checkForRequest();

        createLiveMessageDbInstance();




    }

    private void createLiveMessageDbInstance() {
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId);

        databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(userChattingWithId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("messageKey","");
        hashMap.put("messageLive","");
        hashMap.put("iConnect",0);
        hashMap.put("senderId",FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReferenceUserChattingWith.updateChildren(hashMap);
        databaseReferenceUser.updateChildren(hashMap);
    }

    private void checkForRequest() {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    LiveMessage liveMessage = dataSnapshot.getValue(LiveMessage.class);
                    if(liveMessage.getMessageKey().equals("")) {

                        Toast.makeText(FireVideo.this,"Start Live Video",Toast.LENGTH_SHORT).show();
                        startLiveVideoTextView.setText("START");
                        start = true;
                    }

                    else
                    {
                        Toast.makeText(FireVideo.this,"This person is calling you!",Toast.LENGTH_SHORT).show();
                        key = liveMessage.getMessageKey();
                        startLiveVideoTextView.setText("JOIN");
                        start = false;
                    }


                }
                else
                {
                    Toast.makeText(FireVideo.this,"Start Live Video",Toast.LENGTH_SHORT).show();
                    startLiveVideoTextView.setText("START");
                    start = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    private void stopSignalling(){

        leaveChannel();
        RtcEngine.destroy();

        mRtcEngine.disableVideo();

        databaseReferenceUserChattingWith.removeValue();
        databaseReferenceUser.removeValue();

        finish();



    }

    private void setupSignalling(final String userChattingWithId) {


        if(!start)
            initializeAgoraEngine();


        else {


            databaseReferenceUser = FirebaseDatabase.getInstance().getReference("LiveMessages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userChattingWithId);

            databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference("LiveMessages")
                    .child(userChattingWithId)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String,Object> hashMap = new HashMap<>();
            String messageKey = databaseReferenceUser.push().getKey();
            key = messageKey;
            hashMap.put("messageKey",messageKey);
            hashMap.put("messageLive","");
            hashMap.put("iConnect",0);
            hashMap.put("senderId",FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReferenceUserChattingWith.updateChildren(hashMap);
            databaseReferenceUser.updateChildren(hashMap);

            initializeAgoraEngine();



        }

     //   key = liveMessage.getMessageKey();





    }

    private void initializeAgoraEngine() {
        try {
            mRtcEngine = RtcEngine.create(getBaseContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            joinChannel();
            setupLocalVideo();
            setupVideoProfile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.disableAudio();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P_3, false);
    }

    private void setupLocalVideo() {
        FrameLayout container = (FrameLayout) findViewById(R.id.local_video_view_container);
        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        surfaceView.setZOrderMediaOverlay(true);
        container.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null,  key, "Extra Optional Data",new Random().nextInt(10000000)+1); // if you do not specify the uid, Agora will assign one.
    }

    private void setupRemoteVideo(int uid) {
        FrameLayout container = (FrameLayout) findViewById(R.id.remote_video_view_container);

        if (container.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getBaseContext());
        container.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        surfaceView.setTag(uid);

    }

    @Override
    protected void onPause() {
        super.onPause();

        stopObservingLiveMessaging = true;

        stopSignalling();

        finish();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
        RtcEngine.destroy();
        mRtcEngine.disableVideo();
        mRtcEngine.disableAudio();
        mRtcEngine.clearVideoWatermarks();
        mRtcEngine.stopPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initializeAgoraEngine();

        stopObservingLiveMessaging = false;
    }

   /* @Override
    public void onBackPressed() {

        if(emojiActive) {
            emojiKeyboardLayout.setVisibility(View.GONE);


            emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));
            Toast.makeText(FireVideo.this,"is Emoji Active: "+emojiActive,Toast.LENGTH_SHORT).show();

            emojiActive = false;

        }
        else {
           // super.onBackPressed();
            Toast.makeText(FireVideo.this,"is Emoji Active: "+emojiActive,Toast.LENGTH_SHORT).show();
            finish();
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        //replaces the default 'Back' button action
        if(keyCode== KeyEvent.KEYCODE_BACK)   {
            if(emojiActive) {
                emojiKeyboardLayout.setVisibility(View.GONE);


                emoji.setBackground(getDrawable(R.drawable.ic_insert_emoticon_white_24dp));
                Toast.makeText(FireVideo.this,"is Emoji Active: "+emojiActive,Toast.LENGTH_SHORT).show();

                emojiActive = false;

            }
            else {
                // super.onBackPressed();
                Toast.makeText(FireVideo.this,"is Emoji Active: "+emojiActive,Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        return true;
    }
}