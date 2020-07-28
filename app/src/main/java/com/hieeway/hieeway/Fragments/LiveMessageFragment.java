package com.hieeway.hieeway.Fragments;


import android.Manifest;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CustomUiController;
import com.hieeway.hieeway.LiveMessageActiveService;
import com.hieeway.hieeway.Model.YoutubeSync;
import com.hieeway.hieeway.VerticalPageActivity;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
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
import com.hieeway.hieeway.Model.LiveMessage;
import com.hieeway.hieeway.Model.VideoItem;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.YouTubeConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    // public String userIdChattingWith;
    public String usernameChattingWith;
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
    RelativeLayout control_layout;
    private LiveMessageEventListener liveMessageEventListener;
    DatabaseReference urlRef, seekRef, presenceRef;
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
    ValueEventListener valueEventListener, seekValueEventListener, presentEventListener;
    ListView video_listView;
    EditText search_video_edittext;
    YouTubePlayerView youtube_player_view;
    Button search_video_btn;
    RelativeLayout current_user_blinker, other_user_blinker;
    private String videoID = "kJQP7kiw5Fk";
    private Boolean userPresent = false;
    private static final String API_KEY = "AIzaSyDl7rYj9tB9Hn1gp_Oe4TUpEyGbTVYGrZc";
    private boolean playerInitialised;
    //RelativeLayout bottom_sheet_webview_dialog_layout;
    WebView bottom_sheet_webview_dialog_layout;
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
    WebView youtube_web_view;
    private List<VideoItem> searchResults, durationAddedResult;
    private YouTube.Videos.List videoQuery;
    private YouTubeConfig yc;
    private String videoDuration;
    private boolean videoDurationReady = false;
    private ArrayAdapter<VideoItem> adapter;
    private int time_Iterator = 0;
    private String pattern;
    private Pattern compiledPattern;
    private String youtubeUrl = "https://youtube.com/";
    private String newUrl;
    private String htmlbegin;
    private String htmlend;
    private LinearLayout live_video_control_layout;
    private RelativeLayout live_video_control_btn_lay, connecting_text_lay;
    private TextView connecting_text;
    private SeekBar video_seekbar;
    private boolean blinkReceiver = false;
    private boolean liveChatInitialised = false;
    private String youtubeID = "default";
    private ValueEventListener singleYoutubeListener;
    private DatabaseReference singleSeekRef;
    private float youtubeVideoSec = 0;
    private float youtubeSynSec = 0;
    LinearLayout remoteViewlayout;
    private ImageButton enable_audio;
    private SurfaceView remotesurfaceView;
    private boolean audioEnabled = false;
    private Activity parentActivity;
    private boolean loadWhenInitialised = false;
    private YouTube youtube;
    private String youtubeTitle = " ";
    private String notifyoutubeID = "started";
    private RelativeLayout calling_text_layout;
    private TextView calling_text;
    private ProgressBar ask_progress;
    private boolean canAskuser = false;
    private Handler askHandler;
    private Runnable askRunnable;


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
         *//*
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

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_page3, container, false);
        /**
         * The below code is used to block screenshots
         */
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);

        final String usernameChattingWith = getArguments().getString("usernameChattingWith");
        userChattingWithId = getArguments().getString("userIdChattingWith");
        // userIdChattingWith = getArguments().getString("userIdChattingWith");
        final String photo = getArguments().getString("photo");


        messageBox = view.findViewById(R.id.message_box);
        youtube_layout = view.findViewById(R.id.youtube_layout);
        youtube_player_view = view.findViewById(R.id.youtube_player_view);
        live_video_control_btn_lay = view.findViewById(R.id.live_video_control_btn_lay);
        connecting_text = view.findViewById(R.id.connecting_text);
        connecting_text_lay = view.findViewById(R.id.connecting_text_lay);
        calling_text_layout = view.findViewById(R.id.calling_text_layout);
        calling_text = view.findViewById(R.id.calling_text);
        ask_progress = view.findViewById(R.id.ask_progress);

        youtube_player_seekbar = view.findViewById(R.id.youtube_player_seekbar);
        bottom_sheet_dialog_layout = view.findViewById(R.id.bottom_sheet_dialog_layout);
        // bottom_sheet_webview_dialog_layout = view.findViewById(R.id.bottom_sheet_webview_dialog_layout);
        searchResults = new ArrayList<>();
        durationAddedResult = new ArrayList<>();
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


        startLiveVideo = view.findViewById(R.id.startLiveVideo);
        stopLiveVideo = view.findViewById(R.id.stopLiveVideo);
        startLiveVideoTextView = view.findViewById(R.id.startLiveVideoText);
        stopLiveVideoTextView = view.findViewById(R.id.stopLiveVideoText);
        current_user_blinker = view.findViewById(R.id.current_user_blinker);
        other_user_blinker = view.findViewById(R.id.other_user_blinker);
        video_search_progress = view.findViewById(R.id.video_search_progress);
        sync_video_txt = view.findViewById(R.id.sync_video_txt);
        sync_video_layout = view.findViewById(R.id.sync_video_layout);
        youtube_web_view = view.findViewById(R.id.youtube_web_view);
        bottom_sheet_webview_dialog_layout = youtube_web_view;

        control_layout = view.findViewById(R.id.control_layout);
        live_video_control_layout = view.findViewById(R.id.live_video_control_layout);
        video_seekbar = view.findViewById(R.id.video_seekbar);

        enable_audio = view.findViewById(R.id.enable_audio);


        calling_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canAskuser)
                    askUserToJoin();
            }
        });


        ////bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);

        //bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_webview_dialog_layout);

        //youtube_web_view.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = youtube_web_view.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //  youtube_web_view.setBackgroundColor(getActivity().getResources().getColor(R.color.colorBlack));

        htmlbegin = "<div style=\"background-color:#000000 ;  \">";
        htmlend = " </div>";


        //youtube_web_view.

        pattern = "https?://(?:[0-9A-Z-]+\\.)?(?:youtu\\.be/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|</a>))[?=&+%\\w]*";
        compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);

        youtube = new YouTube.Builder(new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {
            }
        }).setApplicationName(getContext().getString(R.string.app_name)).build();


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float diplayWight = size.x;
        float displayHeight = size.y;

        /*if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            WebSettingsCompat.setForceDark(youtube_web_view.getSettings(), WebSettingsCompat.FORCE_DARK_ON);
        }
*/

        /*youtube_web_view.setWebViewClient(new WebViewClient() {




            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Toast.makeText(getContext(),"URL "+url,Toast.LENGTH_SHORT).show();
                Log.v("WebView OverrideUrl","loaded: "+url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Toast.makeText(getContext(),"URL "+url,Toast.LENGTH_SHORT).show();
                Log.v("WebView PageStarted","loaded: "+url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(getContext(),"URL "+url,Toast.LENGTH_SHORT).show();
                Log.v("WebView PageFinished","loaded: "+url);
                super.onPageFinished(view, url);
            }




        });*/


        final WebViewClient webViewClient = new WebViewClient() {


            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                // Toast.makeText(getActivity(), "" + url, Toast.LENGTH_SHORT).show();

                videoID = "default";
                videoID = getVideoIdFromYoutubeUrl(url);
                if (!videoID.equals("default")) {
                    youtube_web_view.stopLoading();
                    youtube_web_view.loadUrl(youtubeUrl);



                    //  youtube_web_view.loadDataWithBaseURL(youtubeUrl, htmlbegin+ htmlend,
                    //         "text/html", "utf-8", "");


                    // Toast.makeText(getActivity(), "VideoID: " + videoID, Toast.LENGTH_SHORT).show();

                    // Toast.makeText(getActivity(), "Video ID: " + videoID, Toast.LENGTH_SHORT);

                    //  String snippet = retrieveVideoJSON(videoID,"snippet",API_KEY);

                    try {

                        videoQuery = youtube.videos().list("snippet");
                        videoQuery.setKey(API_KEY);
                        //query.setRequestHeaders( )
                        videoQuery.setMaxResults((long) 1);


                        videoQuery.setFields("items(snippet/title)");
                    } catch (IOException e) {
                        Log.d("YC", "Could not initialize: " + e);
                    }

                    youtubeID = videoID;
                    videoQuery.setId(videoID);


                    TaskCompletionSource<Video> videoTaskCompletionSource = new TaskCompletionSource<>();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            VideoListResponse videoListResponse = null;
                            try {
                                videoListResponse = videoQuery.execute();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            try {
                                List<Video> videoResults = videoListResponse.getItems();
                                Video video = videoResults.get(0);

                                videoTaskCompletionSource.setResult(video);
                            } catch (Exception e) {
                                videoTaskCompletionSource.setException(e);
                            }

                        }
                    }).start();

                    Task<Video> videoTask = videoTaskCompletionSource.getTask();


                    videoTask.addOnCompleteListener(new OnCompleteListener<Video>() {
                        @Override
                        public void onComplete(@NonNull Task<Video> task) {


                            try {
                                youtubeTitle = task.getResult().getSnippet().getTitle();
                                if (youtubeTitle == null) {
                                    youtubeTitle = " ";
                                }
                            } catch (Exception e) {
                                youtubeTitle = " ";
                            }

                            HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                            youtubeVideoHash.put("youtubeID", youtubeID);
                            youtubeVideoHash.put("videoSec", 0.0);
                            youtubeVideoHash.put("videoTitle", youtubeTitle);

                            FirebaseDatabase.getInstance().getReference("Video")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIDCHATTINGWITH)
                                    .updateChildren(youtubeVideoHash);

                            FirebaseDatabase.getInstance().getReference("Video")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIDCHATTINGWITH)
                                    .onDisconnect()
                                    .removeValue();


                            FirebaseDatabase.getInstance().getReference("Video")
                                    .child(userIDCHATTINGWITH)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .updateChildren(youtubeVideoHash);


                            Rect outRect = new Rect();

                            //Native UI youtube search using data api
                            //bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);


                    /*bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/

                            youtube_web_view.setVisibility(View.GONE);


                            search_video_edittext.clearFocus();
                            messageBox.requestFocus();
                            messageBox.setCursorVisible(true);

                            bottomSheetVisible = false;


                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    youtubeBottomFragmentStateListener.setDrag(false);
                                }
                            }, 750);

                        }
                    });








                }
                youtubeUrl = url;
                //youtube_web_view.stopLoading();
                // super.doUpdateVisitedHistory(view, url, isReload);
            }
        };

        //youtube_web_view.setWebChromeClient(new WebChromeClient());
        youtube_web_view.setWebViewClient(webViewClient);


        youtube_web_view.loadUrl("https://youtube.com/");

        //youtube_web_view.


        username.setText(userNameChattingWith);
        startLiveVideoTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        stopLiveVideoTextView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        sync_video_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        connecting_text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        calling_text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        getLifecycle().addObserver(youtube_player_view);


        search_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchOnYoutube(search_video_edittext.getText().toString());
                //onUrlPasted(search_video_edittext.getText().toString());
                video_search_progress.setVisibility(View.VISIBLE);
                time_Iterator = 0;
            }
        });


        youtube_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (userPresent) {
                youtubeBottomFragmentStateListener.setDrag(true);
                //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                youtube_web_view.setVisibility(View.VISIBLE);

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.youtube_webview_open);
                youtube_web_view.setAnimation(animation);
                messageBox.clearFocus();
                search_video_edittext.requestFocus();
                search_video_edittext.setCursorVisible(true);

                bottomSheetVisible = true;
                if (isKeyboardOpen) {
                    // youtube_layout.setVisibility(View.GONE);
                }

                /*} else {
                    Toast.makeText(getContext(), usernameChattingWith + " has not joined yet.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        youtube_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    youtube_button.setAlpha(0.6f);

                    youtube_button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    youtube_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                    youtube_button.animate().alpha(1.0f).setDuration(50);


                } else {
                    youtube_button.animate().setDuration(100).alpha(1.0f);

                    youtube_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                }

                return false;
            }

        });

        video_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem videoItem = searchResults.get(position);
                videoID = videoItem.getId();

                // Toast.makeText(getActivity(), "Video ID: " + videoID, Toast.LENGTH_SHORT).show();
                HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put("youtubeUrl", videoID);
                youtubeVideoHash.put("videoSec", 0.0);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIDCHATTINGWITH)
                        .updateChildren(youtubeVideoHash);


                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(userIDCHATTINGWITH)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(youtubeVideoHash);


                Rect outRect = new Rect();

                //Native UI youtube search using data api
                //bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

                /*bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/

                // youtube_web_view.setVisibility(View.GONE);

                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.youtube_webview_close);
                youtube_web_view.setAnimation(animation);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        youtube_web_view.setVisibility(View.GONE);
                    }
                }, 350);

                search_video_edittext.clearFocus();
                messageBox.requestFocus();
                messageBox.setCursorVisible(true);

                bottomSheetVisible = false;


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        youtubeBottomFragmentStateListener.setDrag(false);
                    }
                }, 750);

            }
        });




        enable_audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {

                    if (!audioEnabled) {

                        mRtcEngine.enableLocalAudio(false);
                        mRtcEngine.enableAudio();

                        mYouTubePlayer.setVolume(50);


                        mRtcEngine.muteAllRemoteAudioStreams(false);

                        audioEnabled = true;

                        enable_audio.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.colorRed));
                    } else {
                        mRtcEngine.disableAudio();

                        mYouTubePlayer.setVolume(100);
                        mRtcEngine.muteAllRemoteAudioStreams(true);
                        audioEnabled = false;

                        enable_audio.setBackgroundTintList(getActivity().getResources().getColorStateList(R.color.darkGrey));
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }


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
                    String youtubeUrl = dataSnapshot.getValue(String.class);

                    if (initialiseActivity) {
                        try {
                            //  if (!videoID.equals(chatStamp.getYoutubeUrl())) {

                            videoID = youtubeUrl;


                            if (!videoID.equals("default")) {

                                youtubeID = youtubeUrl;


                                youtube_layout.setVisibility(View.VISIBLE);
                                youtube_player_view.setVisibility(View.VISIBLE);

                                try {
                                    /*if (userPresent == chatStamp.getPresent()) {
                                        //Toast.makeText(getActivity(), "videoID: "+videoID, Toast.LENGTH_SHORT).show();
                                       // mYouTubePlayer.loadVideo(videoID, 0);
                                      //  mYouTubePlayer.play();
                                    }*/
                                    mYouTubePlayer.loadVideo(videoID, 0);
                                    mYouTubePlayer.play();
                                } catch (Exception e) {
                                    // Toast.makeText(getActivity(), "videoID: "+videoID, Toast.LENGTH_SHORT).show();
                                    mYouTubePlayer.loadVideo(videoID, 0);
                                    mYouTubePlayer.play();
                                }


                            }


                            //  }
                        } catch (NullPointerException e) {
                            //
                            videoID = "default";
                            // Toast.makeText(getActivity(), "Youtube URL not available", Toast.LENGTH_SHORT).show();
                        }
                    }


                }
                initialiseActivity = true;


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        presentEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    try {
                        Boolean previousPresence = userPresent;

                        userPresent = dataSnapshot.getValue(Boolean.class);
                        if (!userPresent) {
                            if (previousPresence) {
                                Toast.makeText(getActivity(), userNameChattingWith + " left the live chat", Toast.LENGTH_SHORT).show();
                                calling_text.setText("Ask " + usernameChattingWith + " to join");
                                calling_text.setBackground(parentActivity.getResources().getDrawable(R.drawable.send_message_back_drawable));

                                canAskuser = true;

                                ask_progress.setVisibility(View.GONE);
                            }
                            // seekRef.removeValue();
                            live_video_control_btn_lay.setVisibility(View.GONE);
                            calling_text_layout.setVisibility(View.VISIBLE);
                            // calling_text.setText("Call user to join");
                            //youtube_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkGrey)));
                            //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                            // youtube_web_view.setVisibility(View.GONE);
                            /*Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.youtube_webview_close);
                            youtube_web_view.setAnimation(animation);*/

                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    youtube_web_view.setVisibility(View.GONE);
                                }
                            }, 350);*/
                            //youtube_button.setEnabled(false);
                            if (initialiseActivity) {
                               /* mYouTubePlayer.pause();
                                youtube_layout.setVisibility(View.GONE);*/
                            }

                            leaveChannel();
                            resetLiveVideoViews();


                        } else {
                            Toast.makeText(getActivity(), userNameChattingWith + " joined the live chat! Now you can search and watch videos together", Toast.LENGTH_LONG).show();

                            askHandler.removeCallbacks(askRunnable);
                            // youtubeVideoSec

                            try {
                                if (!youtubeID.equals("default")) {
                                    HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                                    youtubeVideoHash.put("videoSec", youtubeVideoSec);
                                    youtubeVideoHash.put("youtubeID", youtubeID);
                                    youtubeVideoHash.put("videoTitle", youtubeTitle);



                                    FirebaseDatabase.getInstance().getReference("Video")
                                            .child(userIDCHATTINGWITH)
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .updateChildren(youtubeVideoHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            //  mYouTubePlayer.seekTo(0);

                                            FirebaseDatabase.getInstance().getReference("Video")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(userIDCHATTINGWITH)
                                                    .updateChildren(youtubeVideoHash);
                                        }
                                    });
                                }

                            } catch (Exception e) {
                                //
                            }


                            live_video_control_btn_lay.setVisibility(View.VISIBLE);
                            calling_text_layout.setVisibility(View.GONE);
                            youtube_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
                            //youtube_button.setEnabled(true);
                        }
                    } catch (Exception e) {
                        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        // youtube_web_view.setVisibility(View.GONE);
                        Animation animation = AnimationUtils.loadAnimation(parentActivity, R.anim.youtube_webview_close);
                        youtube_web_view.setAnimation(animation);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                youtube_web_view.setVisibility(View.GONE);
                            }
                        }, 350);
                        live_video_control_btn_lay.setVisibility(View.GONE);
                        calling_text_layout.setVisibility(View.VISIBLE);
                        //youtube_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkGrey)));
                        // youtube_button.setEnabled(false);
                    }


                } else {
                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    //youtube_web_view.setVisibility(View.GONE);
                    Animation animation = AnimationUtils.loadAnimation(parentActivity, R.anim.youtube_webview_close);
                    youtube_web_view.setAnimation(animation);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            youtube_web_view.setVisibility(View.GONE);
                        }
                    }, 350);
                    live_video_control_btn_lay.setVisibility(View.GONE);
                    calling_text_layout.setVisibility(View.VISIBLE);
                    //youtube_button.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkGrey)));
                    // youtube_button.setEnabled(false);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        urlRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDCHATTINGWITH)
                .child("youtubeUrl")
        ;

        presenceRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDCHATTINGWITH)
                .child("present")
        ;


        seekRef = FirebaseDatabase.getInstance().getReference("Video")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDCHATTINGWITH);

        seekValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //if (initialiseActivity) {
                    try {
                        YoutubeSync youtubeSync = dataSnapshot.getValue(YoutubeSync.class);




                        try {

                            customUiController.setYoutube_player_seekbarVisibility(false);
                        } catch (Exception e) {

                            // Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        //youtube_player_view.setVisibility(View.VISIBLE);


                        if (!youtubeSync.getYoutubeID().equals("default")) {
                            youtubeID = youtubeSync.getYoutubeID();
                            youtubeSynSec = youtubeSync.getVideoSec();
                        }

                        loadWhenInitialised = false;

                        mYouTubePlayer.loadVideo(youtubeID, youtubeSynSec);
                        mYouTubePlayer.play();
                        sync_video_layout.setVisibility(View.VISIBLE);
                        youtube_player_view.setVisibility(View.INVISIBLE);
                        youtube_layout.setVisibility(View.VISIBLE);
                        // seekRef.removeValue();
                    } catch (Exception e) {
                        // Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                        loadWhenInitialised = true;
                        sync_video_layout.setVisibility(View.VISIBLE);
                        youtube_player_view.setVisibility(View.INVISIBLE);
                        youtube_layout.setVisibility(View.VISIBLE);

                            /*youtube_layout.setVisibility(View.GONE);
                            //youtube_player_view.setVisibility(View.VISIBLE);

                            sync_video_layout.setVisibility(View.GONE);*/

                    }
                    // }
                    initialiseActivity = true;
                } else {
                    /*if(youtubeTitle!=null)
                    {

                        try {
                            try {

                                customUiController.setYoutube_player_seekbarVisibility(false);
                            } catch (Exception e) {

                                // Toast.makeText(getActivity(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                            }
                            youtubeID = notifyoutubeID;
                            mYouTubePlayer.loadVideo(notifyoutubeID, youtubeSynSec);
                            mYouTubePlayer.play();
                            sync_video_layout.setVisibility(View.VISIBLE);
                            youtube_player_view.setVisibility(View.INVISIBLE);
                            youtube_layout.setVisibility(View.VISIBLE);
                        }
                        catch (Exception e)
                        {
                            loadWhenInitialised = true;
                            sync_video_layout.setVisibility(View.VISIBLE);
                            youtube_player_view.setVisibility(View.INVISIBLE);
                            youtube_layout.setVisibility(View.VISIBLE);

                        }
                    }*/

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };



        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    // Log.d(TAG, "connected");

                    if (liveChatInitialised) {
                        HashMap<String, Object> diHash = new HashMap<>();

                        diHash.put("present", true);


                        FirebaseDatabase.getInstance().getReference("ChatList")
                                .child(userIDCHATTINGWITH)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(diHash);

                    }


                    HashMap<String, Object> disconnectHash = new HashMap<>();

                    disconnectHash.put("present", false);
                    disconnectHash.put("photo", photo);


                    DatabaseReference updatePresenceRef = FirebaseDatabase.getInstance().getReference("ChatList")
                            .child(userIDCHATTINGWITH)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

      /*  DatabaseReference updatePresenceRefUser = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDCHATTINGWITH);


        updatePresenceRefUser.onDisconnect()
                .updateChildren(disconnectHash);*/
                    updatePresenceRef.onDisconnect()
                            .updateChildren(disconnectHash);

                    if (userPresent) {
                        live_video_control_btn_lay.setVisibility(View.VISIBLE);
                    }


                } else {
                    // Log.d(TAG, "not connected");
                    // Log.d(TAG, "not connected");

                    if (userPresent) {
                        live_video_control_btn_lay.setVisibility(View.GONE);
                        calling_text_layout.setVisibility(View.GONE);
                    }


                    onDisconnectFromDB();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log.w(TAG, "Listener was cancelled");
            }
        });

        HashMap<String, Object> disconnectHash = new HashMap<>();

        disconnectHash.put("present", false);
        disconnectHash.put("photo", photo);





        final View customUiView = youtube_player_view.inflateCustomPlayerUi(R.layout.youtube_player_custom_view);



        abstractYouTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                mYouTubePlayer = youTubePlayer;
                customUiController = new CustomUiController(customUiView, mYouTubePlayer, getContext(), youtubeID);

                //mYouTubePlayer.addListener(customUiController);
                playerInitialised = true;

                if (loadWhenInitialised) {
                    mYouTubePlayer.loadVideo(youtubeID, youtubeSynSec);
                    mYouTubePlayer.play();
                }


                // mYouTubePlayer.addListener(youtube_player_seekbar);

                //seekRef.addValueEventListener(seekValueEventListener);

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
                    } else {
                        Toast.makeText(getContext(), "customUiController is null", Toast.LENGTH_SHORT).show();
                    }
                    videoStarted = true;
                }
                if (state == PlayerConstants.PlayerState.ENDED) {

                    FirebaseDatabase.getInstance().getReference("Video")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userChattingWithId).removeValue();

                    top_bar.setVisibility(View.VISIBLE);
                    youtube_layout.setVisibility(View.GONE);
                    youtube_player_view.setVisibility(View.GONE);
                    videoStarted = false;
                }

            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                //Toast.makeText(getActivity(),"Time: "+second,Toast.LENGTH_SHORT).show();

                youtubeVideoSec = second;
            }

            @Override
            public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError error) {
                //Toast.makeText(getContext(),"Error: "+error.toString(),Toast.LENGTH_SHORT).show();

            }
        };


        video_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                Toast.makeText(parentActivity, "Seekbar progress: " + progress, Toast.LENGTH_SHORT).show();

                if (progress == 1) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED)
                        requestAllPermissions();


                    else {


                        initialiseLiveVideo();

                    }
                } else {
                    stopSignalling();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        //bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 2 / 5;

        //bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 3 / 7;

        /*bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    youtubeBottomFragmentStateListener.setDrag(true);
                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    messageBox.clearFocus();
                    search_video_edittext.requestFocus();
                    search_video_edittext.setCursorVisible(true);


                    bottomSheetVisible = true;

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });*/

        search_video_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchOnYoutube(v.getText().toString());
                    //onUrlPasted(v.getText().toString());
                    video_search_progress.setVisibility(View.VISIBLE);
                    time_Iterator = 0;
                    return false;
                }
                return true;
            }
        });

        /*search_video_edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
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
        });*/

        /*KeyboardVisibilityEvent.setEventListener(
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

                });*/



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

                } else {
                    handler.removeCallbacks(runnable);
                }
            }
        };

        receiverRunnable = new Runnable() {
            @Override
            public void run() {
                if(!receiving) {

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

                    } else {


                        receiverTextView.setText(decryptRSAToString(s, USER_PRIVATE_KEY));
                        if (!showOtherUserTypingAnimation) {
                            closeBottomSheet();
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

                        leaveChannel();
                        resetLiveVideoViews();
                        /*if (flagActivityClosure)
                            getActivity().finish();

                            startLiveVideoTextView.setText("Live Expressions");
                            start = true;
                            stopLiveVideo.setVisibility(View.GONE);
                        }*/

                        /*if (wasInCall *//*|| checkMessage.getSenderId().equals(userIDCHATTINGWITH)*//*) {
                         *//*firstPersonVideo.setVisibility(View.GONE);
                            userChattingPersonVideo.setVisibility(View.GONE);
                            connectingUserVideo.setVisibility(View.GONE);
                            connectedUserVideo.setVisibility(View.GONE);
                            startLiveVideo.setVisibility(View.GONE);


                            firstPersonVideo.setVisibility(View.GONE);
                            userChattingPersonVideo.setVisibility(View.GONE);
                            startLiveVideo.setVisibility(View.VISIBLE);

                            startLiveVideoTextView.setText("Live Expressions");
                            start = true;
                            stopLiveVideo.setVisibility(View.GONE);*//*

                            leaveChannel();
                            resetLiveVideoViews();
                        }*/


                    } else {

                        //  Toast.makeText(getContext(),"chatMessage: "+checkMessage.getMessageKey(),Toast.LENGTH_SHORT).show();
                        /*if(!flagActivityClosure)
                         */
                        if (video_seekbar.getProgress() == 0
                            /*checkMessage.getSenderId().equals(userIDCHATTINGWITH)*/) {
                            key = liveMessage.getMessageKey();
                            if (!joiningLive) {
                                if (!blinkReceiver) {
                                    blinkReceiver = true;
                                    blinkReceiverLayout();


                                }
                                /*startLiveVideoTextView.setText("JOIN");
                                stopLiveVideo.setVisibility(View.VISIBLE);*/
                            } else {
                                blinkReceiver = false;
                                // stopLiveVideo.setVisibility(View.GONE);
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

    private void askUserToJoin() {
        createLiveMessageDbInstance();

        canAskuser = false;
        calling_text.setBackgroundResource(0);
        ask_progress.setVisibility(View.VISIBLE);

        calling_text.setText("Asking " + usernameChattingWith + " to join");

        askRunnable = new Runnable() {
            @Override
            public void run() {
                calling_text.setText("No reply! Ask " + usernameChattingWith + " to join");
                ask_progress.setVisibility(View.GONE);
                calling_text.setBackground(parentActivity.getResources().getDrawable(R.drawable.send_message_back_drawable));
                canAskuser = true;
            }
        };

        try {
            askHandler.postDelayed(askRunnable, 15000);
        } catch (Exception e) {
            //
        }


    }

    private void onDisconnectFromDB() {

        live_video_control_btn_lay.setVisibility(View.GONE);
        calling_text_layout.setVisibility(View.GONE);

        leaveChannel();
        resetLiveVideoViews();
    }


    public void onUrlPasted(String pastedUrl) {
        videoID = "default";
        videoID = getVideoIdFromYoutubeUrl(pastedUrl);
        if (!videoID.equals("default")) {
            youtube_web_view.stopLoading();


            //  youtube_web_view.loadDataWithBaseURL(youtubeUrl, htmlbegin+ htmlend,
            //         "text/html", "utf-8", "");
            youtube_web_view.loadUrl(youtubeUrl);


            // Toast.makeText(getActivity(), "VideoID: " + videoID, Toast.LENGTH_SHORT).show();

            // Toast.makeText(getActivity(), "Video ID: " + videoID, Toast.LENGTH_SHORT);
            HashMap<String, Object> youtubeVideoHash = new HashMap<>();
            youtubeVideoHash.put("youtubeUrl", videoID);
            youtubeVideoHash.put("videoSec", 0.0);

            FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIDCHATTINGWITH)
                    .updateChildren(youtubeVideoHash);


            FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userIDCHATTINGWITH)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(youtubeVideoHash);


            Rect outRect = new Rect();

            //Native UI youtube search using data api
            //bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

            bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

            //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            //youtube_web_view.setVisibility(View.GONE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.youtube_webview_close);
            youtube_web_view.setAnimation(animation);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    youtube_web_view.setVisibility(View.GONE);
                }
            }, 350);
            search_video_edittext.clearFocus();
            messageBox.requestFocus();
            messageBox.setCursorVisible(true);
            bottomSheetVisible = false;


            search_video_edittext.setText("");


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    youtubeBottomFragmentStateListener.setDrag(false);
                }
            }, 750);

        } else {
            search_video_edittext.setError("invalid url");
        }
        youtubeUrl = pastedUrl;
    }

    private void blinkReceiverLayout() {
        try {
            if (blinkReceiver) {
                live_video_control_layout.setBackground(getResources().getDrawable(R.color.darkGrey));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        live_video_control_layout.setBackground(getResources().getDrawable(R.color.colorBlack));
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blinkReceiverLayout();
                            }
                        }, 500);

                    }
                }, 500);
            } else {
                live_video_control_layout.setBackground(getResources().getDrawable(R.color.colorBlack));
            }
        } catch (Exception e) {

        }
    }


    public String getVideoIdFromYoutubeUrl(String url) {
        String videoId = "default";
        String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            videoId = matcher.group(1);
        }
        return videoId;
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
        video_seekbar.setEnabled(false);
        blinkReceiver = false;
        live_video_control_btn_lay.setVisibility(View.GONE);
        connecting_text_lay.setVisibility(View.VISIBLE);
        calling_text_layout.setVisibility(View.GONE);
        connecting_text.setText("Initiating live expressions...");






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

            //urlRef.addValueEventListener(valueEventListener);
            presenceRef.addValueEventListener(presentEventListener);
            seekRef.onDisconnect().removeValue();
            seekRef.addValueEventListener(seekValueEventListener);

            Intent startLiveActiveServiceIntent = new Intent(parentActivity, LiveMessageActiveService.class);
            startLiveActiveServiceIntent.putExtra("username", usernameChattingWith);
            startLiveActiveServiceIntent.putExtra("userid", userChattingWithId);
            startLiveActiveServiceIntent.putExtra("youtubeID", notifyoutubeID);
            if (youtubeTitle != null) {
                startLiveActiveServiceIntent.putExtra("youtubeTitle", youtubeTitle);
                videoID = notifyoutubeID;

            }

            startLiveActiveServiceIntent.putExtra("photo", photo);


            parentActivity.startService(startLiveActiveServiceIntent);

        } catch (NullPointerException e) {
            // liveMessageEventListener.changeFragment();
            //  try {
            // getActivity().finish();

            Intent intent = new Intent(parentActivity, VerticalPageActivity.class);
            intent.putExtra("username", usernameChattingWith);
            intent.putExtra("userid", userChattingWithId);
            intent.putExtra("photo", photo);
            intent.putExtra("live", "live");

            parentActivity.startActivity(intent);
            parentActivity.finish();
            //} catch (Exception ne) {
            //liveMessageEventListener.changeFragment();


            // }
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
                yc = new YouTubeConfig(getActivity());
                // searchResults = yc.search(keywords);
                searchResults = yc.searchVideo(keywords);
                resultReady = true;

            }
        }.start();
    }

    public void CheckSearchResult() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (resultReady) {
                    //durationAddedResult.clear();

                    updateVideosFound();
                    // checkDurationResult();
                }
                else
                    CheckSearchResult();
            }
        }, 1000);
    }

    private void updateVideosFound() {
        adapter = new ArrayAdapter<VideoItem>(getActivity().getApplicationContext(), R.layout.youtube_video_item, searchResults) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                video_search_progress.setVisibility(View.GONE);
                if (convertView == null) {

                    convertView = getLayoutInflater().inflate(R.layout.youtube_video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);
                TextView duration = convertView.findViewById(R.id.duration);
                TextView view_count = convertView.findViewById(R.id.view_count);

                VideoItem searchResult = searchResults.get(position);

                try {

                    Glide.with(getActivity().getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                } catch (Exception e) {
                    //
                }
                try {
                    view_count.setText(searchResult.getViewCount());
                } catch (NullPointerException e) {

                }
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                if (!searchResult.getDuration().equals("default"))
                    duration.setText(searchResult.getDuration());

                //  populateVideoDuration(duration, searchResult.getId(),searchResult);
                return convertView;
            }
        };

        try {
            video_listView.setAdapter(adapter);
        } catch (NullPointerException e) {
            //
        }
    }

    private void durationUpdatedVideo() {
        adapter = new ArrayAdapter<VideoItem>(getActivity().getApplicationContext(), R.layout.youtube_video_item, durationAddedResult) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                video_search_progress.setVisibility(View.GONE);
                if (convertView == null) {

                    convertView = getLayoutInflater().inflate(R.layout.youtube_video_item, parent, false);
                }
                ImageView thumbnail = (ImageView) convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView) convertView.findViewById(R.id.video_title);
                TextView description = (TextView) convertView.findViewById(R.id.video_description);
                TextView duration = convertView.findViewById(R.id.duration);

                VideoItem searchResult = durationAddedResult.get(position);

                try {

                    Glide.with(getActivity().getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                    title.setText(searchResult.getTitle());
                    description.setText(searchResult.getDescription());
                    if (!searchResult.getDuration().equals("default"))
                        duration.setText(searchResult.getDuration());

                } catch (Exception e) {
                    //
                }

                populateVideoDuration(duration, searchResult.getId(), searchResult);
                return convertView;
            }
        };

        try {
            video_listView.setAdapter(adapter);
        } catch (NullPointerException e) {
            //
        }
    }

    private void populateVideoDuration(final TextView duration, final String videoID, final VideoItem videoItem) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                String durationText = yc.getVideoDuration(videoID);

                videoDuration = durationText;

                int index = searchResults.indexOf(videoItem);

                VideoItem videoItemUpdate = searchResults.get(index);

                videoItemUpdate.setDuration(durationText);
                //durationAddedResult.add(videoItemUpdate);

                searchResults.remove(index);
                searchResults.add(index, videoItemUpdate);

                time_Iterator += 1;
                if (time_Iterator > 5)
                    videoDurationReady = true;
            }
        }).start();

    }

    String retrieveVideoJSON(String videoID, String part, String APIkey) {
        String postURL = "https://www.googleapis.com/youtube/v3/videos?id=" + videoID + "&part=" + part + "&key=" + APIkey;
        String output = "";
        try {
            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String line1;
            while ((line1 = br1.readLine()) != null) {
                output = output + line1;
            }
            conn.disconnect();
            br1.close();

        } catch (IOException e) {
            System.out.println("\ne = " + e.getMessage() + "\n");
        }
        return output;

    }

    private void checkDurationResult() {
        if (videoDurationReady) {
            /*searchResults.clear();
            adapter.clear();
            durationUpdatedVideo();*/
            adapter.notifyDataSetChanged();
            videoDurationReady = false;

        }
        /*else if(time_Iterator%5==0)
        {
            adapter.notifyDataSetChanged();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkDurationResult();
                }
            },1000);
        }*/


        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkDurationResult();
                }
            }, 1000);
        }


    }




    public void setYoutubeBottomFragmentStateListener(YoutubeBottomFragmentStateListener youtubeBottomFragmentStateListener) {
        this.youtubeBottomFragmentStateListener = youtubeBottomFragmentStateListener;
    }

    public void setBottomSheetBehavior(MotionEvent event) {
        try {
            /*if (//bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED
            youtube_web_view.getVisibility() == View.VISIBLE) {

                messageBox.clearFocus();
                search_video_edittext.requestFocus();
                search_video_edittext.setCursorVisible(true);

                Rect outRect = new Rect();
                //bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

               // bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                   Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.youtube_webview_close);
                youtube_web_view.setAnimation(animation);
                new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                youtube_web_view.setVisibility(View.GONE);
            }
        }, 750);
                    search_video_edittext.clearFocus();
                    messageBox.requestFocus();
                    messageBox.setCursorVisible(true);
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
            }*/
        } catch (Exception e) {
            //
        }
    }


    public void closeBottomSheet() {
        youtubeBottomFragmentStateListener.setDrag(false);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (youtube_web_view.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.youtube_webview_close);
            youtube_web_view.setAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    youtube_web_view.setVisibility(View.GONE);
                }
            }, 350);
        }
        search_video_edittext.clearFocus();
        messageBox.requestFocus();
        messageBox.setCursorVisible(true);

    }

    public void showLiveMessageDialog(Activity activity, String live) {

        try {

            LiveMessageRequestDialog liveMessageRequestDialog = new LiveMessageRequestDialog(activity, this, live);
            // LiveMessageRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            liveMessageRequestDialog.setCancelable(false);
            liveMessageRequestDialog.setCanceledOnTouchOutside(false);

            liveMessageRequestDialog.show();
        } catch (NullPointerException e) {
            liveMessageEventListener.changeFragment();

        }
    }

    public void destoryLiveFragment() throws NullPointerException {
        try {

            if (startedLivingMessaging) {
                // Toast.makeText(activity, "Fragment Destroyed", Toast.LENGTH_SHORT).show();


                top_bar.setVisibility(View.VISIBLE);
                youtube_layout.setVisibility(View.GONE);
                youtube_player_view.setVisibility(View.GONE);


                if (videoStarted)
                    mYouTubePlayer.pause();


                presenceRef.removeEventListener(presentEventListener);
                // urlRef.removeEventListener(valueEventListener);
                seekRef.removeEventListener(seekValueEventListener);
                startedLivingMessaging = false;
                initialiseActivity = false;
                initialiseSeekEvent = false;

                stopObservingLiveMessaging = true;
                // Toast.makeText(getActivity(), "Live Messaging ended", Toast.LENGTH_SHORT).show();

                stopSignalling();
                receiverTextView.setText("");
                senderTextView.setText("");


            }
        } catch (Exception e) {
            //
        }
    }

    public void createLiveMessageDbInstance() {
        try {


            // if (!checkResult) {
            databaseReferenceUser = FirebaseDatabase.getInstance().getReference("LiveMessages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIDCHATTINGWITH);

            databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference("LiveMessages")
                    .child(userIDCHATTINGWITH)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("messageKey", "");
            hashMap.put("messageLive", "");
            hashMap.put("iConnect", 0);
            hashMap.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReferenceUser.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    databaseReferenceUser.updateChildren(hashMap);
                }
            });

            databaseReferenceUserChattingWith.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    databaseReferenceUserChattingWith.updateChildren(hashMap);
                }
            });


            //Toast.makeText(getContext(), "Happy Texting!", Toast.LENGTH_SHORT).show();
            //   }
        }catch (Exception e)
        {
//            Toast.makeText(getContext(),"Null pointer on current user in liveVideoChat Fragment",Toast.LENGTH_SHORT).show();
        }
    }

    public void setLiveMessageEventListener(LiveMessageEventListener liveMessageEventListener, Activity parentActivity, String photo, String usernameChattingWith, String userIdChattingWith, String youtubeID, String youtubeTitle) throws NullPointerException
    {
        this.liveMessageEventListener = liveMessageEventListener;
        this.parentActivity = parentActivity;
        this.photo = photo;
        this.notifyoutubeID = youtubeID;
        this.usernameChattingWith = usernameChattingWith;
        this.userChattingWithId = userIdChattingWith;
        this.youtubeTitle = youtubeTitle;


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
            // hashMap.put("senderId",FirebaseAuth.getInstance().getCurrentUser().getUid());

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
                        video_seekbar.setEnabled(true);

                        live_video_control_btn_lay.setVisibility(View.VISIBLE);
                        connecting_text_lay.setVisibility(View.GONE);
                        calling_text_layout.setVisibility(View.GONE);

                        // Toast.makeText(getActivity(), "connected", Toast.LENGTH_SHORT).show();
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
                        // Toast.makeText(getActivity(), userNameChattingWith + " Joined", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                connectingUserVideo.setVisibility(View.INVISIBLE);
                                //enable_audio.setVisibility(View.VISIBLE);



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

                        enable_audio.setVisibility(View.GONE);
                        blinkReceiver = false;
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
                        //Toast.makeText(getActivity(), userNameChattingWith + " has left", Toast.LENGTH_LONG).show();

                        /*video_seekbar.setMax(1);
                        video_seekbar.setProgress(0);*/

                        connecting_text_lay.setVisibility(View.VISIBLE);

                        live_video_control_btn_lay.setVisibility(View.generateViewId());
                        connecting_text.setText("live expression disconnected");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                live_video_control_btn_lay.setVisibility(View.VISIBLE);
                                connecting_text_lay.setVisibility(View.GONE);
                            }
                        }, 500);

                        blinkReceiver = false;

                        video_seekbar.setMax(1);

                        video_seekbar.post(new Runnable() {
                            @Override
                            public void run() {
                                video_seekbar.setProgress(0);
                            }
                        });
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

        video_seekbar.setMax(1);
        video_seekbar.setProgress(0);

        blinkReceiver = false;
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

        //mRtcEngine.


    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, key, "Extra Optional Data", new Random().nextInt(10000000) + 1);
        wasInCall = true;// if you do not specify the uid, Agora will assign one.
    }


    private void setupRemoteVideo(int uid) {

        frameRemoteContainer.removeAllViews();

        //remote_audio_video_view_container.removeAllViews();

        /*if (frameRemoteContainer.getChildCount() >= 1) {
            return;
        }*/


        remotesurfaceView = RtcEngine.CreateRendererView(getContext());

        frameRemoteContainer.addView(remotesurfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(remotesurfaceView, VideoCanvas.RENDER_MODE_ADAPTIVE, uid));
        remotesurfaceView.setTag(uid);



    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            singleSeekRef.removeEventListener(singleYoutubeListener);
        } catch (Exception e) {

        }

        liveChatInitialised = false;
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

                liveChatInitialised = false;

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


        liveChatInitialised = true;


        //.child("youtubeUrl");


        if (notifyoutubeID == null || notifyoutubeID.equals("started") || notifyoutubeID.equals("default")) {
            DatabaseReference senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userChattingWithId);

            DatabaseReference receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userChattingWithId)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            Long tsLong = System.currentTimeMillis() / 1000;
            final String ts = tsLong.toString();


            final HashMap<String, Object> timeStampHash = new HashMap<>();
            timeStampHash.put("timeStamp", ts);
            timeStampHash.put("id", userChattingWithId);
            timeStampHash.put("username", usernameChattingWith);
            timeStampHash.put("photo", photo);
            timeStampHash.put("seen", "notseen");
            timeStampHash.put("chatPending", false);

            senderChatCreateRef.updateChildren(timeStampHash);

            HashMap<String, Object> timeStampHashReceiver = new HashMap<>();

            timeStampHashReceiver.put("timeStamp", ts);
            timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
            timeStampHashReceiver.put("username", usernameChattingWith);
            timeStampHashReceiver.put("photo", photo);
            timeStampHashReceiver.put("seen", "notseen");
            timeStampHashReceiver.put("present", true);
            timeStampHashReceiver.put("chatPending", true);

            Toast.makeText(parentActivity, "notifyoutubeID " + notifyoutubeID, Toast.LENGTH_SHORT).show();


            // if(notifyoutubeID!=null&&!notifyoutubeID.equals("default")&&!notifyoutubeID.equals("started"))
            receiverChatCreateRef.updateChildren(timeStampHashReceiver);
        } else {
            DatabaseReference receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userChattingWithId)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            HashMap<String, Object> timeStampHashReceiver = new HashMap<>();
            timeStampHashReceiver.put("present", true);

            receiverChatCreateRef.updateChildren(timeStampHashReceiver);

        }

        FirebaseDatabase.getInstance().getReference("Video")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userChattingWithId)
                .onDisconnect()
                .removeValue();

        /*else
        {
            FirebaseDatabase.getInstance().getReference("Video")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIDCHATTINGWITH)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                }
            });
        }*/


        calling_text_layout.setVisibility(View.VISIBLE);

        initialiseLiveragment();
        createLiveMessageDbInstance();

        try {
            askRunnable = new Runnable() {
                @Override
                public void run() {
                    calling_text.setText("No reply! Ask " + usernameChattingWith + " to join");
                    calling_text.setBackground(parentActivity.getResources().getDrawable(R.drawable.send_message_back_drawable));
                    canAskuser = true;
                }
            };

            askHandler = new Handler();
            askHandler.postDelayed(askRunnable, 15000);

        } catch (Exception e) {
            //
        }


    }

    @Override
    public void stopLiveMessaging() {
        //  urlRef.removeEventListener(valueEventListener);
        liveChatInitialised = false;
        destoryLiveFragment();
        liveMessageEventListener.changeFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            // destoryLiveFragment();
        } catch (Exception e) {

        }

    }
}