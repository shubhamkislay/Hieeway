package com.shubhamkislay.jetpacklogin.Fragments;


import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shubhamkislay.jetpacklogin.CameraActivity;
import com.shubhamkislay.jetpacklogin.FireVideo;
import com.shubhamkislay.jetpacklogin.Interface.LiveMessageEventListener;
import com.shubhamkislay.jetpacklogin.LiveMessagingViewModel;
import com.shubhamkislay.jetpacklogin.LiveMessagingViewModelFactory;
import com.shubhamkislay.jetpacklogin.LiveVideoViewModel;
import com.shubhamkislay.jetpacklogin.LiveVideoViewModelFactory;
import com.shubhamkislay.jetpacklogin.Model.LiveMessage;
import com.shubhamkislay.jetpacklogin.R;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import br.com.instachat.emojilibrary.model.layout.EmojiCompatActivity;
import br.com.instachat.emojilibrary.model.layout.EmojiEditText;
import br.com.instachat.emojilibrary.model.layout.EmojiKeyboardLayout;
import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveMessageFragment extends Fragment {

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

    private LiveMessageEventListener liveMessageEventListener;
   // public String userIdChattingWith;
    public String usernameChattingWith;
    public String photo;
    public EditText messageBox;
    public TextView username, senderTextView, receiverTextView;
    public LiveMessagingViewModel liveMessagingViewModel;
    public Boolean truncateString = false;
    public Boolean stopObservingLiveMessaging = false;
    public Button emoji;

    public ProgressBar connectingUserVideo, connectedUserVideo ;
    public Handler handler;
    public Runnable runnable, receiverRunnable;
    public Button backBtn;
    public Boolean typing=false, receiving = false;
    public Boolean emojiActive = false;
    public FrameLayout frameLocalContainer;
    public FrameLayout frameRemoteContainer;
    public Boolean checkResult = false;
    LiveMessage checkMessage;
    int beforetextChangeCounter = 0;
    int textChangeCounter = 0;
    int aftertextChangeCounter = 0;


    public LiveMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_page3, container, false);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        final String usernameChattingWith = getArguments().getString("usernameChattingWith");
        userChattingWithId = getArguments().getString("userIdChattingWith");
       // userIdChattingWith = getArguments().getString("userIdChattingWith");
        final String photo = getArguments().getString("photo");


        messageBox = view.findViewById(R.id.message_box);



        firstPersonVideo = view.findViewById(R.id.first_person_video);
        userChattingPersonVideo = view.findViewById(R.id.second_person_video);

        frameRemoteContainer = (FrameLayout) view.findViewById(R.id.remote_video_view_container);

        frameLocalContainer = (FrameLayout) view.findViewById(R.id.local_video_view_container);


        startLiveVideo = view.findViewById(R.id.startLiveVideo);
        stopLiveVideo = view.findViewById(R.id.stopLiveVideo);
        startLiveVideoTextView = view.findViewById(R.id.startLiveVideoText);
        stopLiveVideoTextView = view.findViewById(R.id.stopLiveVideoText);


        connectingUserVideo = view.findViewById(R.id.send_pending);

        connectedUserVideo = view.findViewById(R.id.send_pending_comp);




        username = view.findViewById(R.id.username);

        backBtn = view.findViewById(R.id.back_button);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                getActivity().finish();
            }
        });

        receiverTextView = view.findViewById(R.id.receiver_message);
        senderTextView = view.findViewById(R.id.sender_message);

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

        receiverRunnable = new Runnable() {
            @Override
            public void run() {

                if(!receiving)
                {

                    receiverTextView.setText("");

                }
                else
                {

                }

            }
        };


        username.setText(usernameChattingWith);


        LiveMessagingViewModelFactory liveMessagingViewModelFactory = new LiveMessagingViewModelFactory(userChattingWithId);
        liveMessagingViewModel = ViewModelProviders.of(this, liveMessagingViewModelFactory).get(LiveMessagingViewModel.class);
        liveMessagingViewModel.getLiveMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {

                if(!stopObservingLiveMessaging) {


                    if(s.length()<1) {
                        // receiverTextView.animate().alpha(0.0f).setDuration(250);

                        receiving = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                receiverTextView.setText(s);
                                receiving = false;

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



                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED /*||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED*/)
                    requestAllPermissions();


                else
                {

                    firstPersonVideo.setVisibility(View.VISIBLE);
                    userChattingPersonVideo.setVisibility(View.VISIBLE);
                    startLiveVideo.setVisibility(View.GONE);
                    stopLiveVideo.setVisibility(View.VISIBLE);
                    connectingUserVideo.setVisibility(View.VISIBLE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            setupSignalling(userChattingWithId);
                            flagActivityClosure = true;

                            liveMessagingViewModel.iConnect();
                        }
                    },1000);



                }
                ///////

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
                getActivity().runOnUiThread(new Runnable() {
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
                userChattingPersonVideo.setVisibility(View.GONE);
                connectingUserVideo.setVisibility(View.GONE);
                connectedUserVideo.setVisibility(View.GONE);
                startLiveVideo.setVisibility(View.GONE);


                firstPersonVideo.setVisibility(View.GONE);
                userChattingPersonVideo.setVisibility(View.GONE);
                startLiveVideo.setVisibility(View.VISIBLE);

                startLiveVideoTextView.setText("Live Expressions");
                start = true;
                stopLiveVideo.setVisibility(View.GONE);



                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopSignalling();

                        liveMessageEventListener.changeFragment();
                       // getActivity().finish();

                    }
                },300);
                //getActivity().finish();

            }
        });

        LiveVideoViewModelFactory liveVideoViewModelFactory = new LiveVideoViewModelFactory(userChattingWithId);
          liveVideoViewModel = ViewModelProviders.of(this, liveVideoViewModelFactory).get(LiveVideoViewModel.class);
        liveVideoViewModel.getLiveVideoData().observe(this, new Observer<LiveMessage>() {
            @Override
            public void onChanged(@Nullable LiveMessage liveMessage) {

                checkMessage = liveMessage;




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
                            getActivity().finish();
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

                        Toast.makeText(getContext(),"chatMessage: "+checkMessage.getMessageKey(),Toast.LENGTH_SHORT).show();
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

        //createLiveMessageDbInstance();

        refreshReceiverText();








        return view;
    }

    private void refreshReceiverText() {

        //Log.v("Refresh TextView called","called!");


        if(receiverTextView.getText().toString().length()>0) {



            final String receiverText = receiverTextView.getText().toString();

           // Log.v("Refresh TextView called", receiverText);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (receiverTextView.getText().toString().equals(receiverText))
                        receiverTextView.setText("");

                    refreshReceiverText();

                }
            }, 1000);
        }
        else
        {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshReceiverText();
                }
            },500);
        }


    }

    public void createLiveMessageDbInstance() {
        try {


            if (!checkForExistingRequest()) {
                databaseReferenceUser = FirebaseDatabase.getInstance().getReference("LiveMessages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userChattingWithId);

                databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference("LiveMessages")
                        .child(userChattingWithId)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("messageKey","");
                hashMap.put("messageLive","");
                hashMap.put("iConnect", 0);
                hashMap.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReferenceUserChattingWith.updateChildren(hashMap);
                databaseReferenceUser.updateChildren(hashMap);
                Toast.makeText(getContext(), "Happy Texting!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
//            Toast.makeText(getContext(),"Null pointer on current user in liveVideoChat Fragment",Toast.LENGTH_SHORT).show();
        }
    }

    public void setLiveMessageEventListener(LiveMessageEventListener liveMessageEventListener)
    {
        this.liveMessageEventListener = liveMessageEventListener;
    }

    private void requestAllPermissions() {

        Dexter.withActivity(getActivity())
                .withPermissions(/*Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,*/
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if(report.areAllPermissionsGranted())
                        {

                            ///
                            setupSignalling(userChattingWithId);
                            flagActivityClosure = true;
                            startLiveVideo.setVisibility(View.GONE);


                            liveMessagingViewModel.iConnect();

                            firstPersonVideo.setVisibility(View.VISIBLE);
                            //userChattingPersonVideo.setVisibility(View.VISIBLE);
                            connectingUserVideo.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            Toast.makeText(getActivity(), "Permission not given!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private Boolean checkForExistingRequest()
    {
        LiveVideoViewModelFactory liveVideoViewModelFactory = new LiveVideoViewModelFactory(userChattingWithId);
        liveVideoViewModel = ViewModelProviders.of(this, liveVideoViewModelFactory).get(LiveVideoViewModel.class);
        liveVideoViewModel.getLiveVideoData().observe(this, new Observer<LiveMessage>() {
            @Override
            public void onChanged(@Nullable LiveMessage liveMessage) {

                 checkMessage = liveMessage;




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
                            getActivity().finish();
                            // Toast.makeText(FireVideo.this, "Live Video Stopped!", Toast.LENGTH_SHORT).show();

                        else {
                            //    Toast.makeText(FireVideo.this, "Start Live Video", Toast.LENGTH_SHORT).show();
                            startLiveVideoTextView.setText("Live Expressions");
                            start = true;
                            stopLiveVideo.setVisibility(View.GONE);
                            checkResult = false;
                        }


                    }


                    else
                    {
                            /*if(!flagActivityClosure)
                                Toast.makeText(FireVideo.this,"This person is calling you!",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FireVideo.this,"Calling...",Toast.LENGTH_SHORT).show();*/
                        Toast.makeText(getContext(),"chatMessage: "+checkMessage.getMessageKey(),Toast.LENGTH_SHORT).show();
                        key = liveMessage.getMessageKey();
                        startLiveVideoTextView.setText("JOIN");
                        start = false;
                        stopLiveVideo.setVisibility(View.VISIBLE);
                        checkResult = true;

                    }
                }catch (NullPointerException e)
                {

                }

            }
        });

        return checkResult;
    }

   /* private void checkForRequest() {


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

                        Toast.makeText(getContext(),"Start Live Video",Toast.LENGTH_SHORT).show();
                        startLiveVideoTextView.setText("START");
                        start = true;
                    }

                    else
                    {
                        Toast.makeText(getContext(),"This person is calling you!",Toast.LENGTH_SHORT).show();
                        key = liveMessage.getMessageKey();
                        startLiveVideoTextView.setText("JOIN");
                        start = false;
                    }


                }
                else
                {
                    Toast.makeText(getContext(),"Start Live Video",Toast.LENGTH_SHORT).show();
                    startLiveVideoTextView.setText("START");
                    start = true;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }*/


    private void stopSignalling(){

        leaveChannel();
        RtcEngine.destroy();

        mRtcEngine.disableVideo();

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId);

        databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(userChattingWithId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReferenceUserChattingWith.removeValue();
        databaseReferenceUser.removeValue();
       // checkMessage.setMessageKey("");

       // getActivity().finish();



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
            mRtcEngine = RtcEngine.create(getContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            joinChannel();
            setupVideoProfile();



        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupVideoProfile() {
        mRtcEngine.enableVideo();
        mRtcEngine.disableAudio();
        mRtcEngine.enableLocalAudio(false);
        mRtcEngine.stopAudioRecording();
        mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P_3, false);
        setupLocalVideo();
    }

    private void setupLocalVideo() {

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getContext());
        surfaceView.setZOrderMediaOverlay(true);
        frameLocalContainer.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));
    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null,  key, "Extra Optional Data",new Random().nextInt(10000000)+1); // if you do not specify the uid, Agora will assign one.
    }

    private void setupRemoteVideo(int uid) {


        if (frameRemoteContainer.getChildCount() >= 1) {
            return;
        }

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getContext());
        frameRemoteContainer.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        surfaceView.setTag(uid);

    }

    @Override
    public void onPause() {
        super.onPause();

        stopObservingLiveMessaging = true;

        stopSignalling();


        receiverTextView.setText("");
        senderTextView.setText("");


        /**
         * Don't put this code here!
         * getActivity().finish();
         */

        //getActivity().finish();
    }

    private void leaveChannel() {
        mRtcEngine.leaveChannel();
        mRtcEngine.disableVideo();
        mRtcEngine.disableAudio();
        mRtcEngine.stopPreview();

        mRtcEngine.clearVideoWatermarks();
        mRtcEngine.stopPreview();
        RtcEngine.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            //initializeAgoraEngine();

            stopObservingLiveMessaging = false;
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(),"Error was in live chat",Toast.LENGTH_SHORT).show();
        }
    }







}
