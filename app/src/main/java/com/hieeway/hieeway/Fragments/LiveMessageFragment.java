package com.hieeway.hieeway.Fragments;


import android.Manifest;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CustomUiController;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;
import com.hieeway.hieeway.Interface.LiveMessageEventListener;
import com.hieeway.hieeway.Interface.LiveMessageRequestListener;
import com.hieeway.hieeway.Interface.YoutubeBottomFragmentStateListener;
import com.hieeway.hieeway.LiveMessageRequestDialog;
import com.hieeway.hieeway.LiveMessagingViewModel;
import com.hieeway.hieeway.LiveMessagingViewModelFactory;
import com.hieeway.hieeway.LiveVideoViewModel;
import com.hieeway.hieeway.LiveVideoViewModelFactory;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.LiveMessage;
import com.hieeway.hieeway.Model.VideoItem;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.YouTubeConfig;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;

import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static com.hieeway.hieeway.VerticalPageActivity.OTHER_USER_PUBLIC_KEY;
import static com.hieeway.hieeway.VerticalPageActivity.USER_PRIVATE_KEY;
import static com.hieeway.hieeway.VerticalPageActivity.userIDCHATTINGWITH;
import static com.hieeway.hieeway.VerticalPageActivity.userNameChattingWith;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveMessageFragment extends Fragment implements LiveMessageRequestListener {

    DatabaseReference urlRef, seekRef;
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
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout bottom_sheet_dialog_layout;
    RelativeLayout video_search_progress;
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
    private RtcEngine mRtcEngine = null;
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
    RelativeLayout youtube_layout;
    int beforetextChangeCounter = 0;
    int textChangeCounter = 0;
    int aftertextChangeCounter = 0;
    Boolean resultReady = false;
    RelativeLayout top_bar;
    YouTubePlayerSeekBar youtube_player_seekbar;
    Button youtube_button;
    YoutubeBottomFragmentStateListener youtubeBottomFragmentStateListener;
    ValueEventListener valueEventListener, seekValueEventListener;
    ListView video_listView;
    EditText search_video_edittext;
    YouTubePlayerView youtube_player_view;
    Button search_video_btn;
    RelativeLayout current_user_blinker, other_user_blinker;
    private String videoID = "kJQP7kiw5Fk";
    private boolean playerInitialised;
    private List<VideoItem> searchResults;
    private com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer mYouTubePlayer = null;
    private boolean initialiseActivity = false, initialiseSeekEvent = false;
    private AbstractYouTubePlayerListener abstractYouTubePlayerListener;
    private boolean startedLivingMessaging;
    private boolean videoStarted;
    private boolean wasInCall = false;
    private boolean joiningLive = false;
    private boolean showCurrentUserTypingAnimation = false;
    private boolean showOtherUserTypingAnimation = false;
    private boolean confirmShowOtherUserTypingAnimation = false;
    private boolean bottomSheetVisible = false;
    private boolean isKeyboardOpen = false;
    private CustomUiController customUiController = null;
    private TextView sync_video_txt;
    private RelativeLayout sync_video_layout;


    public LiveMessageFragment() {
        // Required empty public constructor
    }

    /**
     *  Live View Model    checkForExistingRequest()
     */ {
    /*private Boolean checkForExistingRequest()
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
                        *//*if (flagActivityClosure)
                            getActivity().finish();
                            // Toast.makeText(FireVideo.this, "Live Video Stopped!", Toast.LENGTH_SHORT).show();

                        else {
                            //    Toast.makeText(FireVideo.this, "Start Live Video", Toast.LENGTH_SHORT).show();

                            startLiveVideoTextView.setText("Live Expressions");
                            start = true;
                            stopLiveVideo.setVisibility(View.GONE);
                            checkResult = false;
                        }*//*

                        if(wasInCall||checkMessage.getSenderId().equals(userIDCHATTINGWITH))
                        {
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

                            leaveChannel();
                        }


                    }


                    else
                    {
                            *//*if(!flagActivityClosure)
                                Toast.makeText(FireVideo.this,"This person is calling you!",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FireVideo.this,"Calling...",Toast.LENGTH_SHORT).show();*//*
                       // Toast.makeText(getContext(),"chatMessage: "+checkMessage.getMessageKey(),Toast.LENGTH_SHORT).show();

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
    }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_page3, container, false);
        /**
         * The below code is used to block screenshots
         */
        /*getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);*/

        final String usernameChattingWith = getArguments().getString("usernameChattingWith");
        userChattingWithId = getArguments().getString("userIdChattingWith");
       // userIdChattingWith = getArguments().getString("userIdChattingWith");
        final String photo = getArguments().getString("photo");


        messageBox = view.findViewById(R.id.message_box);
        youtube_layout = view.findViewById(R.id.youtube_layout);
        youtube_player_view = view.findViewById(R.id.youtube_player_view);
        youtube_player_seekbar = view.findViewById(R.id.youtube_player_seekbar);
        bottom_sheet_dialog_layout = view.findViewById(R.id.bottom_sheet_dialog_layout);
        searchResults = new ArrayList<>();
        search_video_edittext = view.findViewById(R.id.search_video_edittext);
        search_video_btn = view.findViewById(R.id.search_video_btn);
        username = view.findViewById(R.id.username);
        connectingUserVideo = view.findViewById(R.id.send_pending);
        connectedUserVideo = view.findViewById(R.id.send_pending_comp);
        receiverTextView = view.findViewById(R.id.receiver_message);
        senderTextView = view.findViewById(R.id.sender_message);
        backBtn = view.findViewById(R.id.back_button);
        video_listView = view.findViewById(R.id.video_listView);
        top_bar = view.findViewById(R.id.top_bar);
        youtube_button = view.findViewById(R.id.youtube_btn);
        firstPersonVideo = view.findViewById(R.id.first_person_video);
        userChattingPersonVideo = view.findViewById(R.id.second_person_video);
        frameRemoteContainer = (FrameLayout) view.findViewById(R.id.remote_video_view_container);
        frameLocalContainer = (FrameLayout) view.findViewById(R.id.local_video_view_container);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);
        startLiveVideo = view.findViewById(R.id.startLiveVideo);
        stopLiveVideo = view.findViewById(R.id.stopLiveVideo);
        startLiveVideoTextView = view.findViewById(R.id.startLiveVideoText);
        stopLiveVideoTextView = view.findViewById(R.id.stopLiveVideoText);
        current_user_blinker = view.findViewById(R.id.current_user_blinker);
        other_user_blinker = view.findViewById(R.id.other_user_blinker);
        video_search_progress = view.findViewById(R.id.video_search_progress);
        sync_video_txt = view.findViewById(R.id.sync_video_txt);
        sync_video_layout = view.findViewById(R.id.sync_video_layout);





        username.setText(userNameChattingWith);
        startLiveVideoTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        stopLiveVideoTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        sync_video_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        getLifecycle().addObserver(youtube_player_view);


        search_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnYoutube(search_video_edittext.getText().toString());
                video_search_progress.setVisibility(View.VISIBLE);
            }
        });


        youtube_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeBottomFragmentStateListener.setDrag(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetVisible = true;
                if (isKeyboardOpen) {
                    youtube_layout.setVisibility(View.GONE);
                }
            }
        });

        video_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem videoItem = searchResults.get(position);
                videoID = videoItem.getId();

                Toast.makeText(getActivity(), "Video ID: " + videoID, Toast.LENGTH_SHORT);
                HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put("youtubeUrl", videoID);
                youtubeVideoHash.put("videoSec", 0.0);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIDCHATTINGWITH)
                        .updateChildren(youtubeVideoHash);
                //  Toast.makeText(YoutubePlayerActivity.this,"Set Video cued",Toast.LENGTH_SHORT).show();

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(userIDCHATTINGWITH)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(youtubeVideoHash);


                Rect outRect = new Rect();
                bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                bottomSheetVisible = false;


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        youtubeBottomFragmentStateListener.setDrag(false);
                    }
                }, 750);

            }
        });

        stopLiveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*firstPersonVideo.setVisibility(View.GONE);
                userChattingPersonVideo.setVisibility(View.GONE);
                connectingUserVideo.setVisibility(View.GONE);
                connectedUserVideo.setVisibility(View.GONE);
                startLiveVideo.setVisibility(View.GONE);


                firstPersonVideo.setVisibility(View.GONE);
                userChattingPersonVideo.setVisibility(View.GONE);
                startLiveVideo.setVisibility(View.VISIBLE);

                startLiveVideoTextView.setText("Live Expressions");
                start = true;
                stopLiveVideo.setVisibility(View.GONE);*/

                /*firstPersonVideo.setVisibility(View.GONE);
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

                leaveChannel();*/


                stopLiveVideoTextView.setText("Disconnecting...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopSignalling();

                        /*try {
                            liveMessageEventListener.changeFragment();
                        } catch (Exception e) {
                            //
                            getActivity().finish();
                        }*/
                        // getActivity().finish();

                    }
                }, 300);
                //getActivity().finish();

            }
        });

        startLiveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED)
                    requestAllPermissions();


                else {


                    initialiseLiveVideo();

                }
                ///////

            }
        });


        /*username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtube_layout.setVisibility(View.VISIBLE);
                youtube_player_view.setVisibility(View.VISIBLE);


                if (youtube_player_view != null) {
                    mYouTubePlayer.loadVideo(videoID, 0);
                    mYouTubePlayer.play();
                }

                // youtube_player_view.initialize(onInitializedListener,true);
            }
        });*/


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                getActivity().finish();
            }
        });


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ChatStamp chatStamp = dataSnapshot.getValue(ChatStamp.class);

                    if (initialiseActivity) {
                        try {
                            //  if (!videoID.equals(chatStamp.getYoutubeUrl())) {

                            videoID = chatStamp.getYoutubeUrl();

                            if (!videoID.equals("default")) {

                                youtube_layout.setVisibility(View.VISIBLE);
                                youtube_player_view.setVisibility(View.VISIBLE);

                                mYouTubePlayer.loadVideo(videoID, 0);
                                mYouTubePlayer.play();
                            }


                            //  }
                        } catch (NullPointerException e) {
                            //
                            // videoID = "default";
                            // Toast.makeText(getActivity(), "Youtube URL not available", Toast.LENGTH_SHORT).show();
                        }
                    }

                    /*String youtubeUrl = dataSnapshot.getValue(String.class);

                    if(initialiseActivity)
                    {
                        try{

                            videoID = youtubeUrl;

                            if(!videoID.equals("default"))
                            {

                                youtube_layout.setVisibility(View.VISIBLE);
                                youtube_player_view.setVisibility(View.VISIBLE);

                                mYouTubePlayer.loadVideo(videoID, 0);
                                mYouTubePlayer.play();
                            }




                        }catch (NullPointerException e)
                        {

                        }
                    }*/


                }
                initialiseActivity = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        urlRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDCHATTINGWITH)
        //.child("youtubeUrl")
        ;

        seekValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    ChatStamp seek = dataSnapshot.getValue(ChatStamp.class);

                    if (initialiseSeekEvent) {
                        try {


                            if (seek.getVideoSec() != 0.0) {
                                // Toast.makeText(getActivity(),"Seek to: "+seek.getVideoSec(),Toast.LENGTH_SHORT).show();
                                //mYouTubePlayer.loadVideo(videoID,seek.getVideoSec());
                                if (playerInitialised) {

                                    youtube_player_view.setVisibility(View.INVISIBLE);

                                    sync_video_layout.setVisibility(View.VISIBLE);
                                    customUiController.setYoutube_player_seekbarVisibility(false);
                                    mYouTubePlayer.loadVideo(videoID, seek.getVideoSec());
                                    mYouTubePlayer.play();
                                }


                            }


                        } catch (NullPointerException e) {

                        }
                    }


                }
                initialiseSeekEvent = true;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        seekRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDCHATTINGWITH);
        //.child("videoSec");


        final View customUiView = youtube_player_view.inflateCustomPlayerUi(R.layout.youtube_player_custom_view);




        abstractYouTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                mYouTubePlayer = youTubePlayer;
                customUiController = new CustomUiController(customUiView, mYouTubePlayer, getContext());

                //mYouTubePlayer.addListener(customUiController);
                playerInitialised = true;
                // mYouTubePlayer.addListener(youtube_player_seekbar);

            }

            @Override
            public void onStateChange(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                mYouTubePlayer = youTubePlayer;

                if (state == PlayerConstants.PlayerState.PLAYING) {
                    top_bar.setVisibility(View.GONE);


                    if (customUiController != null) {
                        customUiController.autoUpdateControlView();
                        customUiController.setYoutube_player_seekbarVisibility(true);
                        youtube_player_view.setVisibility(View.VISIBLE);
                        sync_video_layout.setVisibility(View.INVISIBLE);
                    }
                    videoStarted = true;
                }
                if (state == PlayerConstants.PlayerState.ENDED) {

                    top_bar.setVisibility(View.VISIBLE);
                    youtube_layout.setVisibility(View.GONE);
                    youtube_player_view.setVisibility(View.GONE);
                    videoStarted = false;
                }

            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                //Toast.makeText(getActivity(),"Time: "+second,Toast.LENGTH_SHORT).show();
            }


        };


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayHeight = size.y;


        bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 2 / 5;

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    youtubeBottomFragmentStateListener.setDrag(true);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetVisible = true;

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

        search_video_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchOnYoutube(v.getText().toString());
                    video_search_progress.setVisibility(View.VISIBLE);
                    return false;
                }
                return true;
            }
        });

        search_video_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    //got focus
                    youtube_layout.setVisibility(View.GONE);
                } else {
                    //lost focus
                    if (videoStarted)
                        youtube_layout.setVisibility(View.VISIBLE);
                }
            }
        });

        KeyboardVisibilityEvent.setEventListener(
                getActivity(),
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        // some code depending on keyboard visiblity status
                        isKeyboardOpen = isOpen;
                        if (isOpen && bottomSheetVisible) {
                            //got focus
                            youtube_layout.setVisibility(View.GONE);
                        } else {
                            //lost focus
                            if (videoStarted)
                                youtube_layout.setVisibility(View.VISIBLE);
                        }
                    }

                });



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
                    showCurrentUserTypingAnimation = false;

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
                    showOtherUserTypingAnimation = false;


                } else {
                }
            }
        };


        LiveMessagingViewModelFactory liveMessagingViewModelFactory = new LiveMessagingViewModelFactory(userIDCHATTINGWITH);
        liveMessagingViewModel = ViewModelProviders.of(this, liveMessagingViewModelFactory).get(LiveMessagingViewModel.class);
        liveMessagingViewModel.getLiveMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {

                if(!stopObservingLiveMessaging) {


                    if(s.length()<1) {
                        // receiverTextView.animate().alpha(0.0f).setDuration(250);

                        confirmShowOtherUserTypingAnimation = false;

                        /*receiving = true;


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                receiverTextView.setText(s);
                                receiving = false;


                            }
                        }, 500);

                        receiverTextView.setText(s);*/

                    }

                    else
                    {


                        receiverTextView.setText(decryptRSAToString(s, USER_PRIVATE_KEY));
                        if (!showOtherUserTypingAnimation) {
                            showOtherUserTypingAnimation = true;
                            otherUserTypingAnimation();
                        }
                        //  receiverTextView.animate().alpha(1.0f).setDuration(250);

                    }
                }

            }
        });

        /*receiverTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.length()>0)
                    confirmShowOtherUserTypingAnimation = true;
                else
                    confirmShowOtherUserTypingAnimation = false;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });*/
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


                    if (s.length() > 0) {
                        liveMessagingViewModel.updateLiveMessage(encryptRSAToString(s.toString(), OTHER_USER_PUBLIC_KEY));
                        if (!showCurrentUserTypingAnimation) {
                            showCurrentUserTypingAnimation = true;
                            currentUserTypingAnimation();
                        }
                    }

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

        setupIRtcEngineEventHandler();

        LiveVideoViewModelFactory liveVideoViewModelFactory = new LiveVideoViewModelFactory(userIDCHATTINGWITH);
        liveVideoViewModel = ViewModelProviders.of(this, liveVideoViewModelFactory).get(LiveVideoViewModel.class);
        liveVideoViewModel.getLiveVideoData().observe(this, new Observer<LiveMessage>() {
            @Override
            public void onChanged(@Nullable LiveMessage liveMessage) {

                checkMessage = liveMessage;



/*

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
*/


                try {
                    if (checkMessage.getMessageKey().equals("")) {
                        /*if (flagActivityClosure)
                            getActivity().finish();
                            // Toast.makeText(FireVideo.this, "Live Video Stopped!", Toast.LENGTH_SHORT).show();

                        else {
                            //    Toast.makeText(FireVideo.this, "Start Live Video", Toast.LENGTH_SHORT).show();
                            startLiveVideoTextView.setText("Live Expressions");
                            start = true;
                            stopLiveVideo.setVisibility(View.GONE);
                        }*/

                        if (wasInCall || checkMessage.getSenderId().equals(userIDCHATTINGWITH)) {
                            /*firstPersonVideo.setVisibility(View.GONE);
                            userChattingPersonVideo.setVisibility(View.GONE);
                            connectingUserVideo.setVisibility(View.GONE);
                            connectedUserVideo.setVisibility(View.GONE);
                            startLiveVideo.setVisibility(View.GONE);


                            firstPersonVideo.setVisibility(View.GONE);
                            userChattingPersonVideo.setVisibility(View.GONE);
                            startLiveVideo.setVisibility(View.VISIBLE);

                            startLiveVideoTextView.setText("Live Expressions");
                            start = true;
                            stopLiveVideo.setVisibility(View.GONE);*/

                            leaveChannel();
                        }


                    }
                    else
                    {

                        //  Toast.makeText(getContext(),"chatMessage: "+checkMessage.getMessageKey(),Toast.LENGTH_SHORT).show();
                            /*if(!flagActivityClosure)
                                Toast.makeText(FireVideo.this,"This person is calling you!",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FireVideo.this,"Calling...",Toast.LENGTH_SHORT).show();*/
                        if (checkMessage.getSenderId().equals(userIDCHATTINGWITH)) {
                            key = liveMessage.getMessageKey();
                            if (!joiningLive) {
                                startLiveVideoTextView.setText("JOIN");
                                stopLiveVideo.setVisibility(View.VISIBLE);
                            } else {
                                stopLiveVideo.setVisibility(View.GONE);
                            }
                            start = false;

                            checkResult = true;
                        }

                    }
                } catch (NullPointerException e) {

                    // Toast.makeText(getActivity(),"Video Stopped",Toast.LENGTH_SHORT).show();

                    /*if(wasInCall)
                    {
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

                        leaveChannel();
                    }*/


                }

            }
        });

        //createLiveMessageDbInstance();

        refreshReceiverText();
        // Toast.makeText(getActivity(),"Fragment Initialised",Toast.LENGTH_SHORT).show();

        youtube_player_view.addYouTubePlayerListener(abstractYouTubePlayerListener);
        return view;
    }

    public void currentUserTypingAnimation() {
        if (showCurrentUserTypingAnimation) {
            current_user_blinker.animate().alpha(0.0f).setDuration(300);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    current_user_blinker.animate().alpha(1.0f).setDuration(300);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (showCurrentUserTypingAnimation)
                                currentUserTypingAnimation();
                        }
                    }, 300);

                }
            }, 300);
        } else {
            current_user_blinker.animate().alpha(1.0f).setDuration(500);
        }
    }

    public void otherUserTypingAnimation() {
        if (showOtherUserTypingAnimation) {
            other_user_blinker.animate().alpha(0.0f).setDuration(150);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    other_user_blinker.animate().alpha(1.0f).setDuration(150);
                    try {

                        Vibrator v = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

// Vibrate for 500 milliseconds
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            v.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
                        } else {
                            //deprecated in API 26
                            v.vibrate(40);
                        }
                    } catch (Exception e) {
                        //

                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (showOtherUserTypingAnimation)
                                otherUserTypingAnimation();
                        }
                    }, 150);

                }
            }, 150);
        } else {
            other_user_blinker.animate().alpha(1.0f).setDuration(150);
        }
    }

    public void initialiseLiveVideo() {
        joiningLive = true;
        startLiveVideoTextView.setText("Connecting...");
        stopLiveVideo.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupSignalling(userIDCHATTINGWITH);
                flagActivityClosure = true;
                liveMessagingViewModel.iConnect();
            }
        }, 300);

    }

    public String encryptRSAToString(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("(\\r|\\n)", "");
    }

    public String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }

    public void initialiseLiveragment() {
        //Toast.makeText(activity,"Fragment Initialised",Toast.LENGTH_SHORT).show();

        // videoID = "default";


        try {
            urlRef.addValueEventListener(valueEventListener);
            seekRef.addValueEventListener(seekValueEventListener);
        } catch (NullPointerException e) {
            liveMessageEventListener.changeFragment();
        }
        startedLivingMessaging = true;

        stopObservingLiveMessaging = false;
    }

    private void refreshReceiverText() {

        //Log.v("Refresh TextView called","called!");


        if(receiverTextView.getText().toString().length()>0) {



            final String receiverText = receiverTextView.getText().toString();

           // Log.v("Refresh TextView called", receiverText);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (receiverTextView.getText().toString().equals(receiverText)) {
                        receiverTextView.setText("");
                        showOtherUserTypingAnimation = false;
                    }

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

    private void searchOnYoutube(final String keywords) {
        CheckSearchResult();
        new Thread() {
            public void run() {
                YouTubeConfig yc = new YouTubeConfig(getActivity());
                searchResults = yc.search(keywords);
                resultReady = true;

            }
        }.start();
    }

    public void CheckSearchResult() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (resultReady)
                    updateVideosFound();
                else
                    CheckSearchResult();
            }
        }, 1000);
    }

    private void updateVideosFound() {
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getActivity().getApplicationContext(), R.layout.youtube_video_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                video_search_progress.setVisibility(View.GONE);
                if (convertView == null) {

                    convertView = getLayoutInflater().inflate(R.layout.youtube_video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);


                Glide.with(getActivity().getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        try {
            video_listView.setAdapter(adapter);
        } catch (NullPointerException e) {
            //
        }
    }

    public void setYoutubeBottomFragmentStateListener(YoutubeBottomFragmentStateListener youtubeBottomFragmentStateListener) {
        this.youtubeBottomFragmentStateListener = youtubeBottomFragmentStateListener;
    }

    public void setBottomSheetBehavior(MotionEvent event) {
        try {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    bottomSheetVisible = false;
                    if (videoStarted)
                        youtube_layout.setVisibility(View.VISIBLE);
                    // youtubeBottomFragmentStateListener.setDrag(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            youtubeBottomFragmentStateListener.setDrag(false);
                        }
                    }, 750);

                }
            }
        } catch (Exception e) {
            //
        }
    }


    public void closeBottomSheet() {
        youtubeBottomFragmentStateListener.setDrag(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    public void showLiveMessageDialog(Activity activity) {

        try {

            LiveMessageRequestDialog liveMessageRequestDialog = new LiveMessageRequestDialog(activity, this);
            // LiveMessageRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            liveMessageRequestDialog.setCancelable(false);
            liveMessageRequestDialog.setCanceledOnTouchOutside(false);

            liveMessageRequestDialog.show();
        } catch (NullPointerException e) {
            liveMessageEventListener.changeFragment();

        }
    }

    public void destoryLiveFragment() {

        if (startedLivingMessaging) {
            // Toast.makeText(activity, "Fragment Destroyed", Toast.LENGTH_SHORT).show();

            top_bar.setVisibility(View.VISIBLE);
            youtube_layout.setVisibility(View.GONE);
            youtube_player_view.setVisibility(View.GONE);

            if (videoStarted)
                mYouTubePlayer.pause();

            HashMap<String, Object> youtubeVideoHash = new HashMap<>();
            youtubeVideoHash.put("youtubeUrl", "default");

            FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIDCHATTINGWITH)
                    .updateChildren(youtubeVideoHash);
            //  Toast.makeText(YoutubePlayerActivity.this,"Set Video cued",Toast.LENGTH_SHORT).show();

            FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userIDCHATTINGWITH)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(youtubeVideoHash);


            urlRef.removeEventListener(valueEventListener);
            seekRef.removeEventListener(seekValueEventListener);
            startedLivingMessaging = false;
            initialiseActivity = false;
            initialiseSeekEvent = false;

            stopObservingLiveMessaging = true;
            Toast.makeText(getActivity(), "Live Messaging ended", Toast.LENGTH_SHORT).show();

            stopSignalling();
            receiverTextView.setText("");
            senderTextView.setText("");

        }
    }

    public void createLiveMessageDbInstance() {
        try {


            if (!checkResult) {
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
                //Toast.makeText(getContext(), "Happy Texting!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e)
        {
//            Toast.makeText(getContext(),"Null pointer on current user in liveVideoChat Fragment",Toast.LENGTH_SHORT).show();
        }
    }

    public void setLiveMessageEventListener(LiveMessageEventListener liveMessageEventListener) throws NullPointerException
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
                            initialiseLiveVideo();
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

    private void stopSignalling(){

        /// leaveChannel();

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId);

        databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(userChattingWithId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("messageKey", "");
        hashMap.put("messageLive", "");
        hashMap.put("iConnect", 0);
        hashMap.put("senderId", "");


        databaseReferenceUserChattingWith.updateChildren(hashMap);
        databaseReferenceUser.updateChildren(hashMap);
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

    private void setupIRtcEngineEventHandler() {
        mRtcEventHandler = new IRtcEngineEventHandler() {
            @Override
            public void onFirstRemoteVideoDecoded(final int uid, int width, int height, int elapsed) {
                Log.i("uid video", uid + "");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setupRemoteVideo(uid);
                    }
                });
            }


            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {


                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "connected", Toast.LENGTH_SHORT).show();
                        joiningLive = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                firstPersonVideo.setVisibility(View.VISIBLE);
                                userChattingPersonVideo.setVisibility(View.VISIBLE);
                                startLiveVideo.setVisibility(View.GONE);
                                stopLiveVideo.setVisibility(View.VISIBLE);
                                connectingUserVideo.setVisibility(View.VISIBLE);


                            }
                        }, 1000);

                    }
                });
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), userNameChattingWith + " Joined", Toast.LENGTH_SHORT).show();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectingUserVideo.setVisibility(View.INVISIBLE);


                            }
                        }, 1000);

                    }
                });
            }

            @Override
            public void onLeaveChannel(RtcStats stats) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetLiveVideoViews();
                    }
                });
            }

            @Override
            public void onUserOffline(int uid, int reason) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stopSignalling();
                        //leaveChannel();
                        Toast.makeText(getActivity(), userNameChattingWith + " has left", Toast.LENGTH_LONG).show();
                    }
                });
                // Toast.makeText(getActivity(),"User left",Toast.LENGTH_SHORT).show();

            }

        };
    }

    private void resetLiveVideoViews() {
        //Toast.makeText(getActivity(), "User left", Toast.LENGTH_SHORT).show();

        firstPersonVideo.setVisibility(View.GONE);
        userChattingPersonVideo.setVisibility(View.GONE);
        connectingUserVideo.setVisibility(View.GONE);
        connectedUserVideo.setVisibility(View.GONE);
        startLiveVideo.setVisibility(View.GONE);

        firstPersonVideo.setVisibility(View.GONE);
        userChattingPersonVideo.setVisibility(View.GONE);
        startLiveVideo.setVisibility(View.VISIBLE);

        startLiveVideoTextView.setText("Live Expressions");
        stopLiveVideoTextView.setText("Stop");
        start = true;
        stopLiveVideo.setVisibility(View.GONE);

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

        // mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P_3, false);

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_240x180,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_15,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        setupLocalVideo();
    }

    private void setupLocalVideo() {

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getContext());
        surfaceView.setZOrderMediaOverlay(true);
        frameLocalContainer.addView(surfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, 0));


    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, key, "Extra Optional Data", new Random().nextInt(10000000) + 1);
        wasInCall = true;// if you do not specify the uid, Agora will assign one.
    }


    private void setupRemoteVideo(int uid) {

        frameRemoteContainer.removeAllViews();

        /*if (frameRemoteContainer.getChildCount() >= 1) {
            return;
        }*/

        SurfaceView surfaceView = RtcEngine.CreateRendererView(getContext());

        frameRemoteContainer.addView(surfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        surfaceView.setTag(uid);

    }

    @Override
    public void onPause() {
        super.onPause();



        /**
         * Don't put this code here!
         * getActivity().finish();
         */

        //getActivity().finish();
    }


    private void leaveChannel() {
        try {

            if (mRtcEngine != null) {
                mRtcEngine.stopPreview();
                mRtcEngine.disableVideo();
                mRtcEngine.disableAudio();
                mRtcEngine.leaveChannel();


                mRtcEventHandler = null;

                setupIRtcEngineEventHandler();


            }


            //  RtcEngine.destroy();
        }catch (NullPointerException e)
        {
            Toast.makeText(getActivity(), "Error leaveChannelMethod " + e.toString(), Toast.LENGTH_LONG).show();
            Log.v("leaveChannel", e.toString());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(getActivity(),"onStartCalled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        //Toast.makeText(getActivity(),"onStopCalled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        youtube_player_view.removeYouTubePlayerListener(abstractYouTubePlayerListener);
        getLifecycle().removeObserver(youtube_player_view);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Toast.makeText(getActivity(),"onResumeCalled",Toast.LENGTH_SHORT).show();
        try {
            //initializeAgoraEngine();


        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(),"Error was in live chat",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startLiveMessaging() {

        //urlRef.addValueEventListener(valueEventListener);

        initialiseLiveragment();
        createLiveMessageDbInstance();


    }

    @Override
    public void stopLiveMessaging() {
        //  urlRef.removeEventListener(valueEventListener);
        destoryLiveFragment();
        liveMessageEventListener.changeFragment();
    }


}