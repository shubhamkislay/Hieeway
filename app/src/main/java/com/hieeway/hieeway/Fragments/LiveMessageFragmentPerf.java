package com.hieeway.hieeway.Fragments;


import android.Manifest;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
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
import com.hieeway.hieeway.Adapters.SpotifySearchAdapter;
import com.hieeway.hieeway.CameraRequestDialog;
import com.hieeway.hieeway.CustomUiController;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Interface.CameraPermissionListener;
import com.hieeway.hieeway.Interface.CloseLiveMessagingLoading;
import com.hieeway.hieeway.Interface.FiltersListFragmentListener;
import com.hieeway.hieeway.Interface.SpotifySongSelectedListener;
import com.hieeway.hieeway.LiveMessageActiveService;
import com.hieeway.hieeway.Model.SpotiySearchItem;
import com.hieeway.hieeway.Model.YoutubeSync;
import com.hieeway.hieeway.VerticalPageActivityPerf;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.ncorti.slidetoact.SlideToActView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.PlayerUiController;
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
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
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
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;

import static android.content.Context.MODE_PRIVATE;
import static com.hieeway.hieeway.VerticalPageActivityPerf.OTHER_USER_PUBLIC_KEY;
import static com.hieeway.hieeway.VerticalPageActivityPerf.USER_PRIVATE_KEY;
import static com.hieeway.hieeway.VerticalPageActivityPerf.VIDEO_CHECK_TAG;
import static com.hieeway.hieeway.VerticalPageActivityPerf.userIDCHATTINGWITH;
import static com.hieeway.hieeway.VerticalPageActivityPerf.userNameChattingWith;

/**
 * A simple {@link Fragment} subclass.
 */
public class LiveMessageFragmentPerf extends Fragment implements LiveMessageRequestListener, CameraPermissionListener, SpotifySongSelectedListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String USERNAME = "username";
    private static final String API_KEY = "AIzaSyDl7rYj9tB9Hn1gp_Oe4TUpEyGbTVYGrZc";
    public static final String SPOTIFY_TOKEN = "spotify_token";
    // public String userIdChattingWith;
    public String usernameChattingWith;
    public TextView message_live_with;
    public String photo;
    public EditText messageBox;
    public TextView username, senderTextView, receiverTextView;
    public LiveMessagingViewModel liveMessagingViewModel;
    public Boolean truncateString = false;
    public Boolean stopObservingLiveMessaging = false;
    public ProgressBar connectingUserVideo, connectedUserVideo;
    public Handler handler;
    public Runnable runnable, receiverRunnable;
    public Button backBtn;
    public Boolean typing = false, receiving = false;
    public Boolean emojiActive = false;
    public FrameLayout frameLocalContainer;
    public FrameLayout frameRemoteContainer;
    public Boolean checkResult = false;
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout bottom_sheet_dialog_layout;
    RelativeLayout video_search_progress;

    DatabaseReference urlRef, seekRef, presenceRef, seekNativeRef;
    //RelativeLayout top_bar;
    YouTubePlayerSeekBar youtube_player_seekbar;
    LiveMessage checkMessage;
    RelativeLayout youtube_layout;
    int beforetextChangeCounter = 0;
    int textChangeCounter = 0;
    int aftertextChangeCounter = 0;
    Boolean resultReady = false;
    Button youtube_button;
    YoutubeBottomFragmentStateListener youtubeBottomFragmentStateListener;
    ValueEventListener valueEventListener, seekValueEventListener, presentEventListener, youtUrlEventListener, seekNativeValueEventListener;
    private static final int REQUEST_CODE = 1338;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    RelativeLayout current_user_blinker, other_user_blinker;
    SlideToActView slideToActView;
    RelativeLayout complete_icon;
    Boolean videoLive = false;
    //RelativeLayout bottom_sheet_webview_dialog_layout;
    WebView bottom_sheet_webview_dialog_layout;
    WebView youtube_web_view;
    LinearLayout remoteViewlayout;
    private IRtcEngineEventHandler mRtcEventHandler;
    private DatabaseReference databaseReferenceUserChattingWith;
    private DatabaseReference databaseReferenceUser;
    private String userChattingWithId;
    private String liveMessageKey;
    private RelativeLayout firstPersonVideo, userChattingPersonVideo;

    private static final String FONT_PATH = "fonts/samsungsharpsans-bold.otf";
    private String key;
    private Boolean start = true;
    private Boolean flagActivityClosure = false;
    private LiveVideoViewModel liveVideoViewModel;
    private LiveMessageEventListener liveMessageEventListener;
    private RtcEngine mRtcEngine = null;
    private RelativeLayout bottom_bar_back, bottom_bar_back_top;
    private String videoID = "kJQP7kiw5Fk";
    private Boolean userPresent = false;
    private boolean playerInitialised;
    private com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer mYouTubePlayer = null;
    private com.google.android.youtube.player.YouTubePlayer nativeYoutubePlayer = null;
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
    private List<VideoItem> searchResults, durationAddedResult;
    private YouTube.Videos.List videoQuery;
    private YouTubeConfig yc;
    private String videoDuration;
    private boolean videoDurationReady = false;
    private ArrayAdapter<VideoItem> adapter;
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    private int time_Iterator = 0;
    private static final String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
    private static final String cipherInstancePadding = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
    private static final String VIDEO_NODE = "Video";
    private static final String defaultVideoID = "default";
    private RelativeLayout live_video_control_btn_lay, connecting_text_lay;
    private TextView connecting_text;
    private SeekBar video_seekbar;
    private boolean blinkReceiver = false;
    private boolean liveChatInitialised = false;
    private static final String USER_PRESENT_TAG = "present";
    private ValueEventListener singleYoutubeListener;
    private DatabaseReference singleSeekRef;
    private float youtubeVideoSec = 0;
    private float youtubeSynSec = 0;
    private ImageButton enable_audio;
    private SurfaceView remotesurfaceView;
    private boolean audioEnabled = false;
    private Activity parentActivity;
    private boolean loadWhenInitialised = false;
    private YouTube youtube;
    private String youtubeTitle = " ";


    private RelativeLayout calling_text_layout;
    private TextView calling_text;
    private ProgressBar ask_progress;
    private boolean canAskuser = false;
    private Handler askHandler;
    private Runnable askRunnable;
    private Display display;
    private Point size;

    private int displayHeight;
    private RelativeLayout other_user_video_wrapper, user_video_wrapper;
    private RelativeLayout live_other_highlight, live_user_highlight;
    private ConstraintLayout parent_layout;
    private SurfaceView localSurfaceView;
    private boolean joined = false;
    private Button online_ring;
    private AnimationDrawable animationDrawable, animationDrawableTop;
    private RelativeLayout live_exp_back;
    private RelativeLayout camera_access_layout;
    private Button camera_access_btn;
    private TextView request_camera_message;
    private String live;
    private FrameLayout youtube_api_player_view;
    private CloseLiveMessagingLoading closeLiveMessagingLoading;
    private boolean nativePlayerInitialized = false;
    private boolean isStartedPlaying = false;
    private boolean localUserChanged = true;
    private boolean buffering = true;
    private boolean videoLoaded = false;
    private static final String CHATLIST_TAG = "ChatList";
    private boolean resumePlay = false;
    private YouTubePlayerView youtube_player_view;
    private boolean pauseVideo;
    private String videoTitle;
    private static final String PHOTO_TAG = "photo";
    private static final String YOUTUBEID_TAG = "youtubeID";
    String loadVideofromUrl;
    private static final String VIDEOSEC_TAG = "videoSec";
    private static final String VIDEOTITLE_TAG = "videoTitle";
    private static final String START_LIVE = "Start Live Expression";
    private static final String LIVE_MESSAGE = "LiveMessages";
    private static final String MESSAGE_KEY_TAG = "messageKey";
    private static final String MESSAGE_LIVE_TAG = "messageLive";
    private static final String iCONNECT_TAG = "iConnect";
    private static final String SENDER_ID_TAG = "senderId";
    private static final String LOADED_VIDEO_TAG = "loadedVideo";
    private static final String EMPTY_STRING = "";
    private static final String TIME_STAMP_TAG = "timeStamp";
    private static final String ID_TAG = "id";
    private static final String USERNAME_TAG = "username";
    private static final String SEEN_TAG = "seen";
    private static final String NOT_SEEN_VAL = "notseen";
    private static final String CHAT_PENDING_TAG = "chatPending";
    private static final String LOCAL_USER_STAMP_TAG = "localuserstamp";
    private static final String OTHER_USER_STAMP_TAG = "otheruserstamp";
    private static final String NO = "no";
    private static String youtubeUrl = "https://youtube.com/";
    private static String yoututbeHomeURL = "https://youtube.com/";
    private static String youtubeID = "default";
    private static String STARTED = "started";
    private static String spotifyToken;
    RecyclerView video_listView;
    //EditText search_video_edittext;
    //YouTubePlayerView youtube_player_view;
    Button search_video_btn;
    private SpotifySearchAdapter spotifySearchAdapter;
    private int nativeYoutubeSync = 0;
    private String notifyoutubeID = STARTED;
    public String curr_id;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button spotify_btn;
    private EditText search_video_edittext;
    private List<SpotiySearchItem> spotiySearchItemList;
    private PlayerUiController playerUiController;


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
                    if (checkMessage.getMessageKey().equals(EMPTY_STRING)) {
                        *//*if (flagActivityClosure)
                            parentActivity.finish();


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

    public LiveMessageFragmentPerf(LiveMessageEventListener liveMessageEventListener, Activity parentActivity, String photo, String usernameChattingWith, String userIdChattingWith, String youtubeID, String youtubeTitle, String loadVideofromUrl) {
        // Required empty public constructor
        this.liveMessageEventListener = liveMessageEventListener;
        this.parentActivity = parentActivity;
        this.photo = photo;
        this.notifyoutubeID = youtubeID;
        this.usernameChattingWith = usernameChattingWith;
        this.userChattingWithId = userIdChattingWith;
        this.youtubeTitle = youtubeTitle;
        this.loadVideofromUrl = loadVideofromUrl;


        SharedPreferences sharedPreferences = parentActivity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        curr_id = sharedPreferences.getString(USER_ID, "");

    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        sharedPreferences = parentActivity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        spotifyToken = sharedPreferences.getString(SPOTIFY_TOKEN, "default");

        View view = inflater.inflate(R.layout.live_fragment_layout_pref, container, false);

        try {
            parentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE);
        } catch (Exception e) {
            //
        }


        //parentActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);


        // userIdChattingWith = getArguments().getString("userIdChattingWith");


        spotiySearchItemList = new ArrayList<>();
        camera_access_btn = view.findViewById(R.id.camera_access_btn);
        camera_access_layout = view.findViewById(R.id.camera_access_layout);
        request_camera_message = view.findViewById(R.id.request_camera_message);

        messageBox = view.findViewById(R.id.message_box);
        youtube_layout = view.findViewById(R.id.youtube_layout);
        youtube_player_view = view.findViewById(R.id.youtube_player_view);
        playerUiController = youtube_player_view.getPlayerUiController();

        live_video_control_btn_lay = view.findViewById(R.id.live_video_control_btn_lay);
        connecting_text = view.findViewById(R.id.connecting_text);
        connecting_text_lay = view.findViewById(R.id.connecting_text_lay);
        calling_text_layout = view.findViewById(R.id.calling_text_layout);
        calling_text = view.findViewById(R.id.calling_text);
        ask_progress = view.findViewById(R.id.ask_progress);
        live_other_highlight = view.findViewById(R.id.live_other_highlight);
        live_user_highlight = view.findViewById(R.id.live_user_highlight);
        online_ring = view.findViewById(R.id.online_ring);
        message_live_with = view.findViewById(R.id.message_live_with);

        youtube_api_player_view = view.findViewById(R.id.youtube_api_player_view);
        slideToActView = view.findViewById(R.id.slideToActView);

        complete_icon = view.findViewById(R.id.complete_icon);
        parent_layout = view.findViewById(R.id.parent_layout);
        bottom_bar_back = view.findViewById(R.id.bottom_bar_back);
        spotify_btn = view.findViewById(R.id.spotify_btn);

        bottom_bar_back_top = view.findViewById(R.id.bottom_bar_back_top);

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
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(false);
        video_listView.setLayoutManager(linearLayoutManager);
        //top_bar = view.findViewById(R.id.top_bar);
        youtube_button = view.findViewById(R.id.youtube_btn);
        firstPersonVideo = view.findViewById(R.id.first_person_video);
        userChattingPersonVideo = view.findViewById(R.id.second_person_video);
        frameRemoteContainer = (FrameLayout) view.findViewById(R.id.remote_video_view_container);
        user_video_wrapper = view.findViewById(R.id.user_video_wrapper);
        other_user_video_wrapper = view.findViewById(R.id.other_user_video_wrapper);

        frameLocalContainer = (FrameLayout) view.findViewById(R.id.local_video_view_container);


        live_exp_back = view.findViewById(R.id.live_exp_back);


        current_user_blinker = view.findViewById(R.id.current_user_blinker);
        other_user_blinker = view.findViewById(R.id.other_user_blinker);
        video_search_progress = view.findViewById(R.id.video_search_progress);
        sync_video_txt = view.findViewById(R.id.sync_video_txt);
        sync_video_layout = view.findViewById(R.id.sync_video_layout);
        youtube_web_view = view.findViewById(R.id.youtube_web_view);
        bottom_sheet_webview_dialog_layout = youtube_web_view;


        video_seekbar = view.findViewById(R.id.video_seekbar);

        enable_audio = view.findViewById(R.id.enable_audio);


        calling_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (canAskuser)
                    askUserToJoin();
            }
        });


        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);

        //bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_webview_dialog_layout);

        youtube_web_view.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = youtube_web_view.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        spotify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToSpotify();
            }
        });
        //youtube_web_view.setBackgroundColor(parentActivity.getResources().getColor(R.color.colorBlack));


        /**
         * Youtube Native code below
         */

        /*YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_api_player_view, youTubePlayerFragment);
        transaction.commit();

        youTubePlayerFragment.initialize(YouTubeConfig.getApiKey(), new com.google.android.youtube.player.YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(com.google.android.youtube.player.YouTubePlayer.Provider provider, com.google.android.youtube.player.YouTubePlayer player, boolean wasRestored) {

                // if (!wasRestored) {


                nativeYoutubePlayer = player;
                nativeYoutubePlayer.setFullscreen(false);
                //mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                //mYouTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

                nativeYoutubePlayer.setShowFullscreenButton(false);

                nativePlayerInitialized = true;

                setPlayBackListener();


            }

            @Override
            public void onInitializationFailure(com.google.android.youtube.player.YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                // TODO Auto-generated method stub

            }
        });
*/




        //youtube_web_view.


        youtube = new YouTube.Builder(new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {
            }
        }).setApplicationName(getContext().getString(R.string.app_name)).build();


        display = parentActivity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        int height = displayHeight * 30 / 100;
        int wrapperHeight = displayHeight * 25 / 100;

        /*frameLocalContainer.getLayoutParams().height =  height;
        frameRemoteContainer.getLayoutParams().height = height;
        other_user_video_wrapper.getLayoutParams().height = height;
        user_video_wrapper.getLayoutParams().height = height;
        firstPersonVideo.getLayoutParams().height = height;
        userChattingPersonVideo.getLayoutParams().height = height;*/

        frameLocalContainer.getLayoutParams().height = height;
        frameRemoteContainer.getLayoutParams().height = height;
        other_user_video_wrapper.getLayoutParams().height = height;
        user_video_wrapper.getLayoutParams().height = height;

        live_other_highlight.getLayoutParams().height = height;
        live_user_highlight.getLayoutParams().height = height;


        frameLocalContainer.getLayoutParams().width = height / 2;
        frameRemoteContainer.getLayoutParams().width = height / 2;


        other_user_video_wrapper.getLayoutParams().width = height * 180 / 320;
        user_video_wrapper.getLayoutParams().width = height * 180 / 320;

        live_other_highlight.getLayoutParams().width = height * 180 / 320;
        live_user_highlight.getLayoutParams().width = height * 180 / 320;

        firstPersonVideo.getLayoutParams().width = height / 2;
        userChattingPersonVideo.getLayoutParams().width = height / 2;


        receiverTextView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            public void onLayoutChange(View v,
                                       int left, int top, int right, int bottom,
                                       int leftWas, int topWas, int rightWas, int bottomWas) {


                int widthWas = rightWas - leftWas; // Right exclusive, left inclusive
                if (v.getWidth() != widthWas) {
                    // Width has changed
                }
                //int height = bottomWas - topWas; // Bottom exclusive, top inclusive
                /*if( v.getHeight() != heightWas )
                {*/
                // Height has changed


                if (receiverTextView.getHeight() < displayHeight * 30 / 100) {

                    ViewGroup.LayoutParams params = other_user_video_wrapper.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    params.width = receiverTextView.getHeight() * 180 / 320;
                    params.height = receiverTextView.getHeight();
                    other_user_video_wrapper.setLayoutParams(params);

                    ViewGroup.LayoutParams param = user_video_wrapper.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    param.width = receiverTextView.getHeight() * 180 / 320;
                    param.height = receiverTextView.getHeight();
                    user_video_wrapper.setLayoutParams(param);

                    ViewGroup.LayoutParams paramL = frameLocalContainer.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramL.width = receiverTextView.getHeight() / 2;
                    paramL.height = receiverTextView.getHeight();
                    frameLocalContainer.setLayoutParams(paramL);

                    ViewGroup.LayoutParams paramR = frameRemoteContainer.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramR.width = receiverTextView.getHeight() / 2;
                    paramR.height = receiverTextView.getHeight();
                    frameRemoteContainer.setLayoutParams(paramR);


                    ViewGroup.LayoutParams paramA = live_other_highlight.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramA.width = receiverTextView.getHeight() * 180 / 320;
                    paramA.height = receiverTextView.getHeight();
                    live_other_highlight.setLayoutParams(paramA);

                    ViewGroup.LayoutParams paramB = live_user_highlight.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramB.width = receiverTextView.getHeight() * 180 / 320;
                    paramB.height = receiverTextView.getHeight();
                    live_user_highlight.setLayoutParams(paramB);


                } else {
                    ViewGroup.LayoutParams params = other_user_video_wrapper.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    params.width = height * 180 / 320;
                    params.height = height;
                    other_user_video_wrapper.setLayoutParams(params);

                    ViewGroup.LayoutParams param = user_video_wrapper.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    param.width = height * 180 / 320;
                    param.height = height;
                    user_video_wrapper.setLayoutParams(param);


                    ViewGroup.LayoutParams paramL = frameLocalContainer.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramL.width = height / 2;
                    paramL.height = height;
                    frameLocalContainer.setLayoutParams(paramL);

                    ViewGroup.LayoutParams paramR = frameRemoteContainer.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramR.width = height / 2;
                    paramR.height = height;
                    frameRemoteContainer.setLayoutParams(paramR);

                    ViewGroup.LayoutParams paramA = live_other_highlight.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramA.width = height * 180 / 320;
                    paramA.height = height;
                    live_other_highlight.setLayoutParams(paramA);

                    ViewGroup.LayoutParams paramB = live_user_highlight.getLayoutParams();
//              Changes the height and width to the specified *pixels*
                    paramB.width = height * 180 / 320;
                    paramB.height = height;
                    live_user_highlight.setLayoutParams(paramB);


                }

                    /*other_user_video_wrapper.getLayoutParams().width = receiverTextView.getHeight()*180/320;
                    user_video_wrapper.getLayoutParams().width = height*180/320;*/

                // }
            }
        });


        FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                .child(curr_id)
                .child(userIDCHATTINGWITH)
                .onDisconnect()
                .removeValue();




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




        });
        */


        final WebViewClient webViewClient = new WebViewClient() {


            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                // Toast.makeText(parentActivity, EMPTY_STRING + url, Toast.LENGTH_SHORT).show();

                videoID = defaultVideoID;
                if (!url.equals(yoututbeHomeURL))
                    getVideoIdFromYoutubeUrl(url);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);


            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
            }
        };

        //youtube_web_view.setWebChromeClient(new WebChromeClient());
        youtube_web_view.setWebViewClient(webViewClient);


        youtube_web_view.loadUrl(yoututbeHomeURL);

        //youtube_web_view.


        messageBox.setHint(userNameChattingWith + " is not live");
        username.setText(userNameChattingWith);


        sync_video_txt.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), FONT_PATH));
        connecting_text.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), FONT_PATH));
        calling_text.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), FONT_PATH));
        request_camera_message.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), FONT_PATH));


        /**
         * uncomment the below code
         */
        getLifecycle().addObserver(youtube_player_view);


        search_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchOnYoutube(search_video_edittext.getText().toString());
                //onUrlPasted(search_video_edittext.getText().toString());
                bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 3 / 7;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                video_search_progress.setVisibility(View.VISIBLE);
                time_Iterator = 0;


                if (search_video_edittext.getText().toString().length() > 0)
                    findSpotifySong(search_video_edittext.getText().toString());

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
                //search_video_edittext.requestFocus();
                //search_video_edittext.setCursorVisible(true);

                //bottomSheetVisible = true;
                if (isKeyboardOpen) {
                    // youtube_layout.setVisibility(View.GONE);
                }

                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //youtube_web_view.loadUrl(yoututbeHomeURL);

                        youtube_web_view.loadUrl(youtubeUrl);
                    }
                },500);*/


                /*} else {
                    Toast.makeText(getContext(), usernameChattingWith + " has not joined yet.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });


        camera_access_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camera_access_layout.setVisibility(View.GONE);
                requestAllPermissionsBeforeStart(parentActivity, live);

            }
        });

       /* youtube_button.setOnTouchListener(new View.OnTouchListener() {
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

        });*/
/*

        video_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                VideoItem videoItem = searchResults.get(position);
                videoID = videoItem.getId();

                // Toast.makeText(parentActivity, "Video ID: " + videoID, Toast.LENGTH_SHORT).show();
                HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put("youtubeUrl", videoID);
                youtubeVideoHash.put(VIDEOSEC_TAG, 0.0);

                FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                        .child(curr_id)
                        .child(userIDCHATTINGWITH)
                        .updateChildren(youtubeVideoHash);


                FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                        .child(userIDCHATTINGWITH)
                        .child(curr_id)
                        .updateChildren(youtubeVideoHash);


                Rect outRect = new Rect();

                //Native UI youtube search using data api
                bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

                */
/*bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*//*


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


*/
/**
 * Unable audio feed, uncomment below code
 */
/*

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

                        enable_audio.setBackgroundTintList(parentActivity.getResources().getColorStateList(R.color.colorRed));
                    } else {
                        mRtcEngine.disableAudio();

                        mYouTubePlayer.setVolume(100);
                        mRtcEngine.muteAllRemoteAudioStreams(true);
                        audioEnabled = false;

                        enable_audio.setBackgroundTintList(parentActivity.getResources().getColorStateList(R.color.darkGrey));
                    }

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }


            }
        });
*/




        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                parentActivity.finish();
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


                            if (!videoID.equals(defaultVideoID)) {

                                youtubeID = youtubeUrl;


                                youtube_layout.setVisibility(View.VISIBLE);
                                youtube_player_view.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                    /*if (userPresent == chatStamp.getPresent()) {
                                        //Toast.makeText(parentActivity, "videoID: "+videoID, Toast.LENGTH_SHORT).show();
                                       // mYouTubePlayer.loadVideo(videoID, 0);
                                      //  mYouTubePlayer.play();
                                    }*/


                                            mYouTubePlayer.loadVideo(videoID, 0);
                                            mYouTubePlayer.play();
                                        } catch (Exception e) {
                                            // Toast.makeText(parentActivity, "videoID: "+videoID, Toast.LENGTH_SHORT).show();
                                            mYouTubePlayer.loadVideo(videoID, 0);
                                            mYouTubePlayer.play();
                                        }
                                    }
                                }, 500);


                            }


                            //  }
                        } catch (NullPointerException e) {
                            //
                            videoID = defaultVideoID;
                            // Toast.makeText(parentActivity, "Youtube URL not available", Toast.LENGTH_SHORT).show();
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
                                //  Toast.makeText(parentActivity, userNameChattingWith + " left the live chat", Toast.LENGTH_SHORT).show();


                                try {
                                    animationDrawable.stop();
                                } catch (Exception e) {
                                    //
                                }

                                online_ring.setVisibility(View.INVISIBLE);
                                message_live_with.setText(userNameChattingWith + " has left");
                                message_live_with.setVisibility(View.VISIBLE);

                                bottom_bar_back.setVisibility(View.INVISIBLE);
                                bottom_bar_back_top.setVisibility(View.INVISIBLE);

                                // messageBox.setEnabled(false);

                                messageBox.setHint(userNameChattingWith + " is not live");

                                Snackbar snackbar = Snackbar
                                        .make(parent_layout, userNameChattingWith + " left the live chat", Snackbar.LENGTH_SHORT);
                                View snackBarView = snackbar.getView();
                                snackBarView.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.darkGrey));
                                snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                snackbar.show();


                                calling_text.setText("Ask " + usernameChattingWith + " to join");
                                calling_text.setBackground(parentActivity.getResources().getDrawable(R.drawable.ask_drawable_back));

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

                            Log.v(VIDEO_CHECK_TAG, "presentEventListener calling leaveChannel");
                            leaveChannel();
                            resetLiveVideoViews();


                        } else {
                            // Toast.makeText(parentActivity, userNameChattingWith + " joined the live chat! Now you can search and watch videos together", Toast.LENGTH_LONG).show();

                            LayoutInflater inflater = getLayoutInflater();
                            View layout = inflater.inflate(R.layout.custom_toast,
                                    (ViewGroup) parentActivity.findViewById(R.id.toast_parent));


                            /*TextView toast_message = layout.findViewById(R.id.toast_message);
                            toast_message.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


                            toast_message.setText(userNameChattingWith + " is the live with you! Now you can search and watch videos together");


                            Toast toast = new Toast(parentActivity);
                            toast.setGravity(Gravity.BOTTOM, 0, 150);
                            toast.setDuration(Toast.LENGTH_SHORT);
                            toast.setView(layout);
                            toast.show();*/

                            online_ring.setVisibility(View.VISIBLE);
                            bottom_bar_back.setVisibility(View.VISIBLE);
                            bottom_bar_back_top.setVisibility(View.VISIBLE);

                            messageBox.setHint("Chat here...");


                            animationDrawable = (AnimationDrawable) bottom_bar_back.getBackground();
                            animationDrawableTop = (AnimationDrawable) bottom_bar_back_top.getBackground();

                            animationDrawable.setEnterFadeDuration(1000);
                            animationDrawable.setExitFadeDuration(1000);
                            animationDrawable.start();

                            animationDrawableTop.setEnterFadeDuration(1000);
                            animationDrawableTop.setExitFadeDuration(1000);
                            animationDrawableTop.start();

                            //animationDrawable.stop();

                            animateEyeBlinking();


                            message_live_with.setText("You're Live with " + userNameChattingWith);
                            message_live_with.setVisibility(View.VISIBLE);

                            //messageBox.setEnabled(true);

                            Snackbar snackbar = Snackbar
                                    .make(parent_layout, userNameChattingWith + " is the live with you! Now you can search and watch videos together", Snackbar.LENGTH_SHORT);
                            View snackBarView = snackbar.getView();
                            snackBarView.setBackgroundColor(ContextCompat.getColor(parentActivity, R.color.colorPrimaryDark));
                            snackBarView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            snackbar.show();

                            askHandler.removeCallbacks(askRunnable);
                            // youtubeVideoSec

                            try {
                                if (!youtubeID.equals(defaultVideoID)) {


                                    //Toast.makeText(getContext(), "Video Sec " + videoReSync, Toast.LENGTH_SHORT).show();

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {

                                            int videoReSync = 0;
                                            try {
                                                videoReSync = nativeYoutubePlayer.getCurrentTimeMillis();
                                            } catch (Exception e) {

                                            }
                                            HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                                            youtubeVideoHash.put(VIDEOSEC_TAG, youtubeVideoSec);
                                            //youtubeVideoHash.put(VIDEOSEC_TAG, videoReSync);
                                            youtubeVideoHash.put(YOUTUBEID_TAG, youtubeID);
                                            youtubeVideoHash.put(VIDEOTITLE_TAG, youtubeTitle);


                                            FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                                                    .child(userIDCHATTINGWITH)
                                                    .child(curr_id)
                                                    .updateChildren(youtubeVideoHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //  mYouTubePlayer.seekTo(0);

                                            /*FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                                                    .child(curr_id)
                                                    .child(userIDCHATTINGWITH)
                                                    .updateChildren(youtubeVideoHash);*/
                                                }
                                            });
                                        }
                                    }).start();


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
        urlRef = FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                .child(curr_id)
                .child(userIDCHATTINGWITH)
                .child(YOUTUBEID_TAG);

        presenceRef = FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                .child(curr_id)
                .child(userIDCHATTINGWITH)
                .child(USER_PRESENT_TAG);


        seekRef = FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                .child(curr_id)
                .child(userIDCHATTINGWITH);

        seekNativeRef = FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                .child(curr_id)
                .child(userIDCHATTINGWITH);
        //.child(VIDEOSEC_TAG);

        seekValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //  if (initialiseActivity) {
                    try {
                        YoutubeSync youtubeSync = dataSnapshot.getValue(YoutubeSync.class);

                        try {
                            videoTitle = youtubeSync.getVideoTitle();

                        } catch (Exception e) {
                            //
                            videoTitle = " ";
                        }


                        try {

                            customUiController.setYoutube_player_seekbarVisibility(false);
                            customUiController.setVideo_title(videoTitle);
                        } catch (Exception e) {

                            // Toast.makeText(parentActivity, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                        }

                        //youtube_player_view.setVisibility(View.VISIBLE);

                        try {

                            if (!youtubeSync.getYoutubeID().equals(defaultVideoID)) {
                                youtubeID = youtubeSync.getYoutubeID();
                                youtubeSynSec = youtubeSync.getVideoSec();
                            }
                        } catch (Exception e) {

                        }

                        loadWhenInitialised = false;

                        mYouTubePlayer.loadVideo(youtubeID, youtubeSynSec);
                        mYouTubePlayer.play();
                        sync_video_layout.setVisibility(View.VISIBLE);
                        youtube_player_view.setVisibility(View.VISIBLE);
                        youtube_layout.setVisibility(View.VISIBLE);
                        // seekRef.removeValue();
                    } catch (Exception e) {
                        //  Toast.makeText(parentActivity, "Error: " + e.toString(), Toast.LENGTH_LONG).show();

                        loadWhenInitialised = true;
                        sync_video_layout.setVisibility(View.VISIBLE);
                        youtube_player_view.setVisibility(View.VISIBLE);
                        youtube_layout.setVisibility(View.VISIBLE);

                            /*youtube_layout.setVisibility(View.GONE);
                            //youtube_player_view.setVisibility(View.VISIBLE);

                            sync_video_layout.setVisibility(View.GONE);*/

                    }
                    //     }
                    //   initialiseActivity = true;
                } else {
                    /*if(youtubeTitle!=null)
                    {

                        try {
                            try {

                                customUiController.setYoutube_player_seekbarVisibility(false);
                            } catch (Exception e) {

                                // Toast.makeText(parentActivity, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
/*


        seekNativeValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    //if (initialiseActivity) {
                    try {

                        YoutubeSync youtubeSync = dataSnapshot.getValue(YoutubeSync.class);
                        Integer videoSec = youtubeSync.getVideoSec();
                        loadWhenInitialised = false;


                        if (youtubeID.equals(youtubeSync.getYoutubeID()) && !youtubeID.equals(defaultVideoID)) {
                            localUserChanged = false;
                            if (nativeYoutubeSync != videoSec)
                                nativeYoutubePlayer.seekToMillis(videoSec);

                        } else {
                            youtubeID = youtubeSync.getYoutubeID();

                            if (nativePlayerInitialized) {
                                nativeYoutubeSync = videoSec;
                                youtube_layout.setVisibility(View.VISIBLE);
                                youtube_api_player_view.setVisibility(View.VISIBLE);
                                nativeYoutubePlayer.loadVideo(youtubeID, nativeYoutubeSync);
                                nativeYoutubePlayer.play();
                            }

                        }

                    } catch (Exception e) {

                    }
                    // }
                    initialiseActivity = true;
                } else {
                    */
/*if(youtubeTitle!=null)
                    {

                        try {
                            try {

                                customUiController.setYoutube_player_seekbarVisibility(false);
                            } catch (Exception e) {

                                // Toast.makeText(parentActivity, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
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
                    }*//*


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

*/

        youtUrlEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String videoUrl = snapshot.getValue(String.class);
                    // videoID = videoUrl;
                    youtubeID = videoUrl;

                    if (nativePlayerInitialized)
                        nativeYoutubePlayer.loadVideo(videoUrl);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

                        diHash.put(USER_PRESENT_TAG, true);


                        FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                                .child(userIDCHATTINGWITH)
                                .child(curr_id)
                                .updateChildren(diHash);

                    }


                    HashMap<String, Object> disconnectHash = new HashMap<>();

                    disconnectHash.put(USER_PRESENT_TAG, false);
                    //  disconnectHash.put(PHOTO_TAG, photo);


                    DatabaseReference updatePresenceRef = FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                            .child(userIDCHATTINGWITH)
                            .child(curr_id);

      /*  DatabaseReference updatePresenceRefUser = FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                .child(curr_id)
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




        final View customUiView = youtube_player_view.inflateCustomPlayerUi(R.layout.youtube_player_custom_view);


        abstractYouTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                mYouTubePlayer = youTubePlayer;
                customUiController = new CustomUiController(customUiView, mYouTubePlayer, getContext(), youtubeID, youtubeTitle);

                //mYouTubePlayer.addListener(customUiController);
                playerInitialised = true;

                if (loadWhenInitialised) {
                    mYouTubePlayer.loadVideo(youtubeID, youtubeSynSec);
                    mYouTubePlayer.play();

                    try {
                        customUiController.setVideo_title(videoTitle);
                    } catch (Exception e) {

                    }

                }


                // mYouTubePlayer.addListener(youtube_player_seekbar);

                //seekRef.addValueEventListener(seekValueEventListener);

            }

            @Override
            public void onStateChange(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer, PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                mYouTubePlayer = youTubePlayer;

                if (state == PlayerConstants.PlayerState.PLAYING) {

                    //top_bar.setVisibility(View.GONE);
                    loadVideofromUrl = "yes";
                    if (pauseVideo)
                        mYouTubePlayer.pause();


                    if (customUiController != null) {
                        customUiController.autoUpdateControlView();
                        customUiController.setYoutube_player_seekbarVisibility(true);
                        youtube_player_view.setVisibility(View.VISIBLE);
                        sync_video_layout.setVisibility(View.INVISIBLE);
                        customUiController.setBufferingProgress(View.GONE);
                        customUiController.setPlayPauseBtn(View.VISIBLE);
                    } else {
                        // Toast.makeText(getContext(), "customUiController is null", Toast.LENGTH_SHORT).show();
                    }
                    videoStarted = true;
                }
                if (state == PlayerConstants.PlayerState.ENDED) {


                    FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                            .child(curr_id)
                            .child(userChattingWithId).removeValue();

                    loadVideofromUrl = NO;

                    //Toast.makeText(getContext(), "Video Over", Toast.LENGTH_SHORT).show();
                    //top_bar.setVisibility(View.VISIBLE);

                    youtube_layout.setVisibility(View.GONE);
                    youtube_player_view.setVisibility(View.GONE);
                    videoStarted = false;
                }


                if (state == PlayerConstants.PlayerState.BUFFERING) {

                    customUiController.setBufferingProgress(View.VISIBLE);
                    customUiController.setPlayPauseBtn(View.GONE);


                }

                if (state == PlayerConstants.PlayerState.PAUSED) {

                    customUiController.setBufferingProgress(View.GONE);
                    customUiController.setPlayPauseBtn(View.VISIBLE);

                }


            }

            @Override
            public void onCurrentSecond(YouTubePlayer youTubePlayer, float second) {
                //Toast.makeText(parentActivity,"Time: "+second,Toast.LENGTH_SHORT).show();

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

                //  Toast.makeText(parentActivity, "Seekbar progress: " + progress, Toast.LENGTH_SHORT).show();

                if (progress == 1) {
                    if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.CAMERA)
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


        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideView) {

                complete_icon.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        live_exp_back.setVisibility(View.INVISIBLE);
                        if (!videoLive) {


                            if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED)
                                requestAllPermissions();


                            else {


                                initialiseLiveVideo();

                            }




                            /*complete_icon.setVisibility(View.GONE);
                            slideToActView.setText("Stop Live Expression");

                            slideToActView.resetSlider();
                            slideToActView.setReversed(true);
                            videoLive = true;*/


                        } else {


                            stopSignalling();
                            /*slideToActView.setText(START_LIVE);
                            slideToActView.resetSlider();
                            slideToActView.setReversed(false);

                            videoLive = false;*/
                        }

                    }
                }, 0);


            }
        });


        //bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 2 / 5;

        bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 1 / 10;

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
        });

        search_video_edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // searchOnYoutube(v.getText().toString());

                    if (search_video_edittext.getText().toString().length() > 0)
                        findSpotifySong(search_video_edittext.getText().toString());

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 3 / 7;
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
                parentActivity,
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
                if (!typing) {

                    liveMessagingViewModel.updateLiveMessage(EMPTY_STRING);
                    senderTextView.setText(EMPTY_STRING);
                    messageBox.setText(EMPTY_STRING);
                    showCurrentUserTypingAnimation = false;

                } else {
                    handler.removeCallbacks(runnable);
                }
            }
        };

        receiverRunnable = new Runnable() {
            @Override
            public void run() {
                if (!receiving) {

                    receiverTextView.setText(EMPTY_STRING);
                    showOtherUserTypingAnimation = false;


                } else {
                }
            }
        };


        LiveMessagingViewModelFactory liveMessagingViewModelFactory = new LiveMessagingViewModelFactory(userIDCHATTINGWITH);
        liveMessagingViewModel = ViewModelProviders.of(this, liveMessagingViewModelFactory).get(LiveMessagingViewModel.class);
        liveMessagingViewModel.getLiveMessage().observe(getViewLifecycleOwner(), new Observer<StringBuffer>() {
            @Override
            public void onChanged(@Nullable final StringBuffer s) {

                if (!stopObservingLiveMessaging) {


                    if (s.length() < 1) {
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


                        /*receiverTextView.setText(decryptRSAToString(s, USER_PRIVATE_KEY));
                        if (!showOtherUserTypingAnimation) {
                            closeBottomSheet();
                            showOtherUserTypingAnimation = true;
                            otherUserTypingAnimation();
                        }*/

                        /**
                         * Use below code to decrypt in different thread
                         * and above code for the original version
                         */
                        decryptionCode(s, USER_PRIVATE_KEY);




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

                if (!stopObservingLiveMessaging) {


                    /*if(s.length()<1)
                        senderTextView.animate().alpha(0.0f).setDuration(250);

                    else
                        senderTextView.animate().alpha(1.0f).setDuration(250);*/

                    handler.removeCallbacks(runnable);
                    typing = true;

                    // senderTextView.setAlpha(1.0f);
                    senderTextView.setText(s);


                    if (s.length() > 0) {
                        //liveMessagingViewModel.updateLiveMessage(encryptRSAToString(s.toString(), OTHER_USER_PUBLIC_KEY));
                        /**
                         * uncomment below code to encrypted and send message on different thread and the above code for original version
                         */
                        encryptionCode(s.toString(), OTHER_USER_PUBLIC_KEY);
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

        // setupIRtcEngineEventHandler();

        LiveVideoViewModelFactory liveVideoViewModelFactory = new LiveVideoViewModelFactory(userIDCHATTINGWITH);
        liveVideoViewModel = ViewModelProviders.of(this, liveVideoViewModelFactory).get(LiveVideoViewModel.class);
        liveVideoViewModel.getLiveVideoData().observe(getViewLifecycleOwner(), new Observer<LiveMessage>() {
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
                    if (checkMessage.getMessageKey().equals(EMPTY_STRING)) {

                        Log.v(VIDEO_CHECK_TAG, "LiveVideoViewModel calling leaveChannel");
                        leaveChannel();
                        resetLiveVideoViews();
                        /*if (flagActivityClosure)
                            parentActivity.finish();

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

                    // Toast.makeText(parentActivity,"Video Stopped",Toast.LENGTH_SHORT).show();

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
        // Toast.makeText(parentActivity,"Fragment Initialised",Toast.LENGTH_SHORT).show();

        youtube_player_view.addYouTubePlayerListener(abstractYouTubePlayerListener);


        startLiveMessaging();





        return view;
    }

    private void loadVideo(String url, String videoID) {

        if (!videoID.equals(defaultVideoID)) {
            try {
                youtube_web_view.stopLoading();
                youtube_web_view.loadUrl(youtubeUrl);
            } catch (Exception e) {

            }

            String loadVideoID = videoID;

            videoLoaded = true;

            Rect outRect = new Rect();

            //Native UI youtube search using data api
            //bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);


                    /*bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);*/

            youtube_web_view.setVisibility(View.GONE);


            //search_video_edittext.clearFocus();
            /*messageBox.requestFocus();
            messageBox.setCursorVisible(true);*/

            bottomSheetVisible = false;


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    youtubeBottomFragmentStateListener.setDrag(false);
                }
            }, 0);


            //  youtube_web_view.loadDataWithBaseURL(youtubeUrl, htmlbegin+ htmlend,
            //         "text/html", "utf-8", EMPTY_STRING);


            // Toast.makeText(parentActivity, "VideoID: " + videoID, Toast.LENGTH_SHORT).show();

            // Toast.makeText(parentActivity, "Video ID: " + videoID, Toast.LENGTH_SHORT);

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

            //youtubeID = videoID;
            videoQuery.setId(videoID);

            sync_video_layout.setAlpha(1.0f);
            sync_video_layout.setVisibility(View.VISIBLE);
            youtube_layout.setVisibility(View.VISIBLE);
            //youtube_api_player_view.setVisibility(View.VISIBLE);
            youtube_player_view.setVisibility(View.VISIBLE);


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
                        playerUiController.setVideoTitle(youtubeTitle);

                        if (youtubeTitle == null) {
                            youtubeTitle = " ";
                        }
                    } catch (Exception e) {
                        youtubeTitle = " ";
                    }

                    HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                    youtubeVideoHash.put(YOUTUBEID_TAG, loadVideoID);
                    youtubeVideoHash.put(VIDEOSEC_TAG, 0.0f);
                    youtubeVideoHash.put(VIDEOTITLE_TAG, youtubeTitle);

                    FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                            .child(curr_id)
                            .child(userIDCHATTINGWITH)
                            .updateChildren(youtubeVideoHash);


                    FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                            .child(userIDCHATTINGWITH)
                            .child(curr_id)
                            .updateChildren(youtubeVideoHash);


                }
            });


        }
        youtubeUrl = url;


        /**
         * Show message dialog code added
         */


        // startLiveMessaging();


    }


    private void connectToSpotify() {

        if (sharedPreferences.getString(SPOTIFY_TOKEN, "default").equals("default")) {

            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();

            Toast.makeText(parentActivity, "Connecting to Spotify...", Toast.LENGTH_LONG).show();

            AuthenticationClient.openLoginActivity(parentActivity, REQUEST_CODE, request);
        } else {
            //Toast.makeText(parentActivity,"Connected to Spotify",Toast.LENGTH_SHORT).show();
            youtubeBottomFragmentStateListener.setDrag(true);
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }


    }

    public void getResponseFromHttpUrl(URL url) throws IOException {

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("Accept", "application/json");
        connection.addRequestProperty("Content-Type", "application/json");
        connection.addRequestProperty("Authorization", "Bearer " + spotifyToken);


        TaskCompletionSource<String> stringTaskCompletionSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    InputStream in = connection.getInputStream();

                    Scanner scanner = new Scanner(in);
                    scanner.useDelimiter("\\A");

                    boolean hasInput = scanner.hasNext();
                    if (hasInput) {

                        stringTaskCompletionSource.setResult(scanner.next());
                    } else {
                        stringTaskCompletionSource.setResult(null);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    stringTaskCompletionSource.setResult(null);
                } finally {
                    connection.disconnect();

                }
            }
        }).start();

        Task<String> stringTask = stringTaskCompletionSource.getTask();

        stringTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    // Toast.makeText(parentActivity,"Response: "+task.getResult(),Toast.LENGTH_SHORT).show();
                    getSongFromJSON(task.getResult());
                }
            }
        });


    }

    public void getSongFromJSON(String jasonString) {
        JSONObject jsonRootObject = null;
        try {
            jsonRootObject = new JSONObject(jasonString);
            JSONObject tracksObject = jsonRootObject.getJSONObject("tracks");
            JSONArray jsonArray = tracksObject.optJSONArray("items");

            spotiySearchItemList.clear();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String trackId = jsonObject.optString("id").toString();

                JSONArray jsonArtistObject = jsonObject.optJSONArray("artists");
                String artist = jsonArtistObject.getJSONObject(0).optString("name");

                String songName = jsonObject.optString("name").toString();
                String uri = jsonObject.optString("uri").toString();

                JSONObject jsonAlbumObject = jsonObject.getJSONObject("album");

                JSONArray jsonImageObject = jsonAlbumObject.optJSONArray("images");
                String imageUrl = jsonImageObject.getJSONObject(0).getString("url");

                /*Toast.makeText(parentActivity, "Track id: "+trackId
                        +"\nArtist: "+artist+"\nSong: "+songName+"\nImage: "+imageUrl, Toast.LENGTH_LONG).show();*/

                SpotiySearchItem spotiySearchItem = new SpotiySearchItem();
                spotiySearchItem.setArtistName(artist);
                spotiySearchItem.setImageUrl(imageUrl);
                spotiySearchItem.setSongName(songName);
                spotiySearchItem.setTrackId(uri);

                spotiySearchItemList.add(spotiySearchItem);


            }


            spotifySearchAdapter = new SpotifySearchAdapter(spotiySearchItemList, parentActivity, SpotifyRemoteHelper.getInstance().getSpotifyAppRemote(), photo, userChattingWithId, usernameChattingWith, LiveMessageFragmentPerf.this, "single");
            video_listView.setAdapter(spotifySearchAdapter);
            video_search_progress.setVisibility(View.GONE);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(parentActivity, "Connecting to spotify", Toast.LENGTH_LONG).show();
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(parentActivity, REQUEST_CODE, request);

        }


    }


    public void findSpotifySong(String songName) {
        String filteredSongName = songName.replaceAll(" ", "%20");
        String stringUrl = "https://api.spotify.com/v1/search?q=" + filteredSongName + "&type=track";//&limit=5

        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            if (url != null) {
                getResponseFromHttpUrl(url);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void onFragmentActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        //if (requestCode == REQUEST_CODE) {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

        switch (response.getType()) {
            // Response was successful and contains auth token
            case TOKEN:
                // Handle successful response

                editor.putString(SPOTIFY_TOKEN, response.getAccessToken());
                editor.apply();
                spotifyToken = response.getAccessToken();
                Toast.makeText(parentActivity, "Connected to Spotify: " + response.getAccessToken(), Toast.LENGTH_SHORT).show();
                youtubeBottomFragmentStateListener.setDrag(true);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                if (search_video_edittext.getText().toString().length() > 0)
                    findSpotifySong(search_video_edittext.getText().toString());

                break;

            // Auth flow returned an error
            case ERROR:
                // Handle error response
                Toast.makeText(parentActivity, "Error " + response.getError(), Toast.LENGTH_SHORT).show();
                video_search_progress.setVisibility(View.GONE);
                break;

            // Most likely auth flow was cancelled
            default:
                // Handle other cases
                video_search_progress.setVisibility(View.GONE);
                Toast.makeText(parentActivity, "Error " + response.getType().toString(), Toast.LENGTH_SHORT).show();
        }
        // }
    }

    private void setPlayBackListener() {
        nativeYoutubePlayer.setPlaybackEventListener(new com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isStartedPlaying = true;
                    }
                }, 0);

                // sync_video_layout.setVisibility(View.GONE);
                youtube_layout.setVisibility(View.VISIBLE);
                youtube_api_player_view.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPaused() {


            }

            @Override
            public void onStopped() {

                if (isStartedPlaying && !buffering) {

                    if (youtube_web_view.getVisibility() != View.VISIBLE) {


                        // Toast.makeText(parentActivity, "Player Stopped", Toast.LENGTH_SHORT).show();

                        if (nativeYoutubePlayer.getDurationMillis() == nativeYoutubePlayer.getCurrentTimeMillis()
                                && nativeYoutubePlayer.getDurationMillis() > 0) {

                            Toast.makeText(parentActivity, "Video Duration: " + nativeYoutubePlayer.getDurationMillis()
                                    + "\nCurrent Time: " + nativeYoutubePlayer.getCurrentTimeMillis(), Toast.LENGTH_SHORT).show();
                            FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                                    .child(curr_id)
                                    .child(userChattingWithId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Toast.makeText(parentActivity,"Removed video",Toast.LENGTH_SHORT).show();
                                }
                            });

                            //top_bar.setVisibility(View.VISIBLE);
                            //Toast.makeText(parentActivity,"Video Loaded: "+videoLoaded,Toast.LENGTH_SHORT).show();
                            //if (!videoLoaded) {
                                youtube_layout.setVisibility(View.GONE);
                                youtube_api_player_view.setVisibility(View.GONE);
                            /*} else {
                                videoLoaded = false;
                            }*/

                            videoStarted = false;
                            isStartedPlaying = false;
                        } else {
                            //
                        }
                    } else {
                        //Toast.makeText(parentActivity,"VIDEO STOPPED",Toast.LENGTH_SHORT).show();

                    }
                }
            }

            @Override
            public void onBuffering(boolean b) {

                buffering = b;

            }

            @Override
            public void onSeekTo(int i) {

                // Toast.makeText(getContext(), "Seeked to: " + i, Toast.LENGTH_SHORT).show();

                nativeYoutubeSync = i;

                // if(localUserChanged) {


                HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put(VIDEOSEC_TAG, nativeYoutubeSync);
                youtubeVideoHash.put(YOUTUBEID_TAG, youtubeID);
                youtubeVideoHash.put(VIDEOTITLE_TAG, youtubeTitle);

                FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                        .child(userIDCHATTINGWITH)
                        .child(curr_id)
                        .updateChildren(youtubeVideoHash);
                //   }

                localUserChanged = true;

                /*FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                        .child(curr_id)
                        .child(userIDCHATTINGWITH)
                        .updateChildren(youtubeVideoHash);*/


                // mYouTubePlayer.seekRelativeMillis(-200);
            }
        });
    }

    private void animateEyeBlinking() {

        if (online_ring.getVisibility() == View.VISIBLE) {
            online_ring.setBackground(parentActivity.getResources().getDrawable(R.drawable.eyes_open));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    online_ring.setBackground(parentActivity.getResources().getDrawable(R.drawable.eyes_closed));
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            online_ring.setBackground(parentActivity.getResources().getDrawable(R.drawable.eyes_open));
                            if (online_ring.getVisibility() == View.VISIBLE)
                                animateEyeBlinking();
                        }
                    }, 333);
                }
            }, 5000);
        }
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
                calling_text.setBackground(parentActivity.getResources().getDrawable(R.drawable.ask_drawable_back));
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


        //Log.v(VIDEO_CHECK_TAG, "onDisconnectFromDB calling leaveChannel");
        leaveChannel();
        resetLiveVideoViews();
    }


    private void onJoinSuccess(Boolean automatic) {


        if (automatic) {
            // joined = true;

            //   Toast.makeText(parentActivity, "Channel Joined", Toast.LENGTH_SHORT).show();

            video_seekbar.setEnabled(true);

            live_video_control_btn_lay.setVisibility(View.VISIBLE);
            connecting_text_lay.setVisibility(View.GONE);
            calling_text_layout.setVisibility(View.GONE);

            // Toast.makeText(parentActivity, "connected", Toast.LENGTH_SHORT).show();
            //joiningLive = false;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {


                    complete_icon.setVisibility(View.GONE);
                    slideToActView.setText("Stop Live Expression");

                    slideToActView.resetSlider();
                    slideToActView.setReversed(true);
                    videoLive = true;


                    firstPersonVideo.setVisibility(View.VISIBLE);
                    userChattingPersonVideo.setVisibility(View.VISIBLE);
                    current_user_blinker.setVisibility(View.GONE);
                    other_user_blinker.setVisibility(View.GONE);


                    // connectingUserVideo.setVisibility(View.VISIBLE);


                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if(!joined)

                    onJoinSuccess(true);
                }
            }, 3000);
        }

    }




    private void blinkReceiverLayout() {
        try {
            if (blinkReceiver) {

                // live_video_control_layout.setBackground(getResources().getDrawable(R.color.darkGrey));
                live_exp_back.setVisibility(View.VISIBLE);
                live_exp_back.animate().alpha(0.0f).setDuration(500);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //live_video_control_layout.setBackground(getResources().getDrawable(R.color.colorBlack));
                        live_exp_back.animate().alpha(1.0f).setDuration(500);


                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                blinkReceiverLayout();
                            }
                        }, 500);

                    }
                }, 500);
            } else {
                //live_video_control_layout.setBackground(getResources().getDrawable(R.color.colorBlack));

                live_exp_back.animate().alpha(1.0f).setDuration(500);
                live_exp_back.setVisibility(View.INVISIBLE);
            }
        } catch (Exception e) {

        }
    }


    public void getVideoIdFromYoutubeUrl(String url) {


        TaskCompletionSource<String> stringTaskCompletionSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                String videoId = defaultVideoID;
                Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(url);
                if (matcher.find()) {
                    videoId = matcher.group(1);
                }

                stringTaskCompletionSource.setResult(videoId);
            }
        }).start();


        Task<String> stringTask = stringTaskCompletionSource.getTask();

        stringTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    videoID = task.getResult();

                    loadVideo(url, videoID);
                }

            }
        });


    }

    public void currentUserTypingAnimation() {
        if (showCurrentUserTypingAnimation) {
            current_user_blinker.animate().alpha(0.0f).setDuration(300);
            if (current_user_blinker.getVisibility() == View.GONE)
                live_user_highlight.animate().alpha(1.0f).setDuration(300);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    current_user_blinker.animate().alpha(1.0f).setDuration(300);
                    if (current_user_blinker.getVisibility() == View.GONE)
                        live_user_highlight.animate().alpha(0.0f).setDuration(300);

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
            if (current_user_blinker.getVisibility() == View.GONE)
                live_user_highlight.animate().alpha(0.0f).setDuration(500);

        }
    }

    public void otherUserTypingAnimation() {
        if (showOtherUserTypingAnimation) {
            other_user_blinker.animate().alpha(0.0f).setDuration(150);
            if (other_user_blinker.getVisibility() == View.GONE)
                live_other_highlight.animate().alpha(1.0f).setDuration(150);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    other_user_blinker.animate().alpha(1.0f).setDuration(150);
                    if (other_user_blinker.getVisibility() == View.GONE)
                        live_other_highlight.animate().alpha(0.0f).setDuration(150);
                    try {

                        Vibrator v = (Vibrator) parentActivity.getSystemService(Context.VIBRATOR_SERVICE);

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
            if (other_user_blinker.getVisibility() == View.GONE)
                live_other_highlight.animate().alpha(0.0f).setDuration(150);
        }
    }

    public void initialiseLiveVideo() {
        joiningLive = true;
        video_seekbar.setEnabled(false);
        blinkReceiver = false;


        //live_video_control_btn_lay.setVisibility(View.GONE);
        // connecting_text_lay.setVisibility(View.VISIBLE);
        calling_text_layout.setVisibility(View.GONE);
        //connecting_text.setText("Initiating live expressions...");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setupSignalling(userIDCHATTINGWITH);
                flagActivityClosure = true;
                // liveMessagingViewModel.iConnect();
            }
        }, 0);

    }


    public String encryptRSAToString(String clearText, String publicKey) {
        String encryptedBase64 = EMPTY_STRING;
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(cipherInstancePadding);
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("(\\r|\\n)", EMPTY_STRING);
    }

    public void encryptionCode(String clearText, String publicKey) {


        TaskCompletionSource<String> stringTaskCompletionSource = new TaskCompletionSource<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                String encryptedBase64 = EMPTY_STRING;
                try {
                    KeyFactory keyFac = KeyFactory.getInstance("RSA");
                    KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
                    Key key = keyFac.generatePublic(keySpec);

                    // get an RSA cipher object and print the provider
                    final Cipher cipher = Cipher.getInstance(cipherInstancePadding);
                    // encrypt the plain text using the public key
                    cipher.init(Cipher.ENCRYPT_MODE, key);

                    byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
                    encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
                    String encryptedString = encryptedBase64.replaceAll("(\\r|\\n)", EMPTY_STRING);

                     /*HashMap<String,Object> liveMessageHash = new HashMap<>();
                     liveMessageHash.put(MESSAGE_LIVE_TAG,encryptedString);
                     FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                             .child(userChattingWithId)
                             .child(curr_id).updateChildren(liveMessageHash);*/

                    stringTaskCompletionSource.setResult(encryptedString);
                } catch (Exception e) {
                    stringTaskCompletionSource.setException(new Exception());
                    e.printStackTrace();
                }
            }
        }).start();

        Task<String> stringTask = stringTaskCompletionSource.getTask();

        stringTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String encryptedString = task.getResult();
                    liveMessagingViewModel.updateLiveMessage(encryptedString);
                } else {
                    task.getException().printStackTrace();
                }

            }
        });


    }

    public String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = EMPTY_STRING;
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(cipherInstancePadding);
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

    public void decryptionCode(StringBuffer encryptedBase64, String privateKey) {

        TaskCompletionSource<String> stringTaskCompletionSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                String decryptedString = EMPTY_STRING;
                try {
                    KeyFactory keyFac = KeyFactory.getInstance("RSA");
                    KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
                    Key key = keyFac.generatePrivate(keySpec);

                    // get an RSA cipher object and print the provider
                    final Cipher cipher = Cipher.getInstance(cipherInstancePadding);
                    // encrypt the plain text using the public key
                    cipher.init(Cipher.DECRYPT_MODE, key);

                    String encryptedString = new String(encryptedBase64.toString());
                    byte[] encryptedBytes = Base64.decode(encryptedString, Base64.DEFAULT);
                    encryptedString = null;
                    byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
                    decryptedString = new String(decryptedBytes);
                    stringTaskCompletionSource.setResult(decryptedString);
                } catch (Exception e) {
                    e.printStackTrace();
                    stringTaskCompletionSource.setException(new Exception());
                }
            }
        }).start();

        Task<String> stringTask = stringTaskCompletionSource.getTask();

        stringTask.addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String decryptedString = task.getResult();
                    receiverTextView.setText(decryptedString);
                    if (!showOtherUserTypingAnimation) {
                        closeBottomSheet();
                        showOtherUserTypingAnimation = true;
                        otherUserTypingAnimation();
                    }
                } else {
                    task.getException().printStackTrace();
                }
            }
        });


    }

    public void initialiseLiveragment() {

        pauseVideo = false;
        //Toast.makeText(activity,"Fragment Initialised",Toast.LENGTH_SHORT).show();

        // videoID = defaultVideoID;


        try {

            //urlRef.addValueEventListener(valueEventListener);
            presenceRef.addValueEventListener(presentEventListener);
            seekRef.onDisconnect().removeValue();
            seekRef.addValueEventListener(seekValueEventListener);
            //urlRef.addValueEventListener(youtUrlEventListener);
            //seekNativeRef.addValueEventListener(seekNativeValueEventListener);
            /*Intent startLiveActiveServiceIntent = new Intent(parentActivity, LiveMessageActiveService.class);
            startLiveActiveServiceIntent.putExtra(USERNAME_TAG, usernameChattingWith);
            startLiveActiveServiceIntent.putExtra("userid", userChattingWithId);
            startLiveActiveServiceIntent.putExtra(YOUTUBEID_TAG, notifyoutubeID);
            if (youtubeTitle != null) {
                startLiveActiveServiceIntent.putExtra("youtubeTitle", youtubeTitle);
                videoID = notifyoutubeID;

            }

            startLiveActiveServiceIntent.putExtra(PHOTO_TAG, photo);


            parentActivity.startService(startLiveActiveServiceIntent);*/

        } catch (NullPointerException e) {
            // liveMessageEventListener.changeFragment();
            //  try {
            // parentActivity.finish();

            Intent intent = new Intent(parentActivity, VerticalPageActivityPerf.class);
            intent.putExtra(USERNAME_TAG, usernameChattingWith);
            intent.putExtra("userid", userChattingWithId);
            intent.putExtra(PHOTO_TAG, photo);
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


        if (receiverTextView.getText().toString().length() > 0) {


            final String receiverText = receiverTextView.getText().toString();

            // Log.v("Refresh TextView called", receiverText);

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    if (receiverTextView.getText().toString().equals(receiverText)) {
                        receiverTextView.setText(EMPTY_STRING);
                        showOtherUserTypingAnimation = false;
                    }

                    refreshReceiverText();

                }
            }, 1000);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshReceiverText();
                }
            }, 500);
        }


    }

    private void searchOnYoutube(final String keywords) {
        CheckSearchResult();
        new Thread() {
            public void run() {
                yc = new YouTubeConfig(parentActivity);
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
                } else
                    CheckSearchResult();
            }
        }, 1000);
    }

    private void updateVideosFound() {
        adapter = new ArrayAdapter<VideoItem>(parentActivity.getApplicationContext(), R.layout.youtube_video_item, searchResults) {
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

                    Glide.with(parentActivity.getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                } catch (Exception e) {
                    //
                }
                try {
                    view_count.setText(searchResult.getViewCount());
                } catch (NullPointerException e) {

                }
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                if (!searchResult.getDuration().equals(defaultVideoID))
                    duration.setText(searchResult.getDuration());

                //  populateVideoDuration(duration, searchResult.getId(),searchResult);
                return convertView;
            }
        };

        try {
            // video_listView.setAdapter(adapter);
        } catch (NullPointerException e) {
            //
        }
    }

    private void durationUpdatedVideo() {
        adapter = new ArrayAdapter<VideoItem>(parentActivity.getApplicationContext(), R.layout.youtube_video_item, durationAddedResult) {
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

                    Glide.with(parentActivity.getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                    title.setText(searchResult.getTitle());
                    description.setText(searchResult.getDescription());
                    if (!searchResult.getDuration().equals(defaultVideoID))
                        duration.setText(searchResult.getDuration());

                } catch (Exception e) {
                    //
                }

                populateVideoDuration(duration, searchResult.getId(), searchResult);
                return convertView;
            }
        };

        try {
            // video_listView.setAdapter(adapter);
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
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                messageBox.clearFocus();
                search_video_edittext.requestFocus();
                search_video_edittext.setCursorVisible(true);

                Rect outRect = new Rect();
                bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

                // bottom_sheet_webview_dialog_layout.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                   /*Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.youtube_webview_close);
                youtube_web_view.setAnimation(animation);
                new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                youtube_web_view.setVisibility(View.GONE);
            }
        }, 750);*/
                    search_video_edittext.clearFocus();
                  /*  messageBox.requestFocus();
                    messageBox.setCursorVisible(true);*/
                    bottomSheetVisible = false;

                    if (videoStarted)
                        youtube_layout.setVisibility(View.VISIBLE);
                    // youtubeBottomFragmentStateListener.setDrag(false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            youtubeBottomFragmentStateListener.setDrag(false);
                        }
                    }, 0);

                }
            }
        } catch (Exception e) {
            //
        }
    }


    public void closeBottomSheet() {
        youtubeBottomFragmentStateListener.setDrag(false);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        if (youtube_web_view.getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.youtube_webview_close);
            youtube_web_view.setAnimation(animation);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    youtube_web_view.setVisibility(View.GONE);

                    /*if (resumePlay)
                        nativeYoutubePlayer.play();*/
                }
            }, 350);
        }
        //search_video_edittext.clearFocus();
        messageBox.requestFocus();
        messageBox.setCursorVisible(true);

    }

    public void showLiveMessageDialog(Activity activity, String live, CloseLiveMessagingLoading closeLiveMessagingLoading) {


        this.live = live;
        this.parentActivity = activity;
        this.closeLiveMessagingLoading = closeLiveMessagingLoading;

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            try {
                //closeLiveMessagingLoading.turnOffLoadingScreen();
                CameraRequestDialog cameraRequestDialog = new CameraRequestDialog(activity, LiveMessageFragmentPerf.this);
                cameraRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cameraRequestDialog.setCancelable(false);
                cameraRequestDialog.setCanceledOnTouchOutside(false);

                cameraRequestDialog.show();


            } catch (Exception e) {
                //Toast.makeText(parentActivity,"Error: "+e.toString(),Toast.LENGTH_LONG).show();
                requestAllPermissionsBeforeStart(activity, live);
                //closeLiveMessagingLoading.turnOffLoadingScreen();
            }

        } else {

            try {

                LiveMessageRequestDialog liveMessageRequestDialog = new LiveMessageRequestDialog(activity, LiveMessageFragmentPerf.this, live);
                //liveMessageRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                liveMessageRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                liveMessageRequestDialog.setCancelable(false);
                liveMessageRequestDialog.setCanceledOnTouchOutside(false);


                liveMessageRequestDialog.show();


            } catch (NullPointerException e) {
                liveMessageEventListener.changeFragment();

            }
        }
    }

    public void destoryLiveFragment() throws NullPointerException {
        try {

            //Log.v(VIDEO_CHECK_TAG, "destoryLiveFragment" + " called");

            pauseVideo = true;

            if (startedLivingMessaging) {
                // Toast.makeText(activity, "Fragment Destroyed", Toast.LENGTH_SHORT).show();


                //top_bar.setVisibility(View.VISIBLE);
                youtube_layout.setVisibility(View.GONE);
                //youtube_player_view.setVisibility(View.GONE);

                youtube_api_player_view.setVisibility(View.GONE);


                if (videoStarted)
                    mYouTubePlayer.pause();

                /*try {
                    nativeYoutubePlayer.pause();
                } catch (Exception e) {
                    //
                }*/


                presenceRef.removeEventListener(presentEventListener);
                //urlRef.removeEventListener(youtUrlEventListener);
                seekRef.removeEventListener(seekValueEventListener);
                //seekNativeRef.removeEventListener(seekNativeValueEventListener);

                message_live_with.setVisibility(View.INVISIBLE);
                startedLivingMessaging = false;
                initialiseActivity = false;
                initialiseSeekEvent = false;

                stopObservingLiveMessaging = true;
                // Toast.makeText(parentActivity, "Live Messaging ended", Toast.LENGTH_SHORT).show();

                stopSignalling();
                receiverTextView.setText(EMPTY_STRING);
                senderTextView.setText(EMPTY_STRING);

                RtcEngine.destroy();


            }
        } catch (Exception e) {
            //
            Log.v(VIDEO_CHECK_TAG, "destoryLiveFragment Err" + e.toString());
        }
    }

    public void createLiveMessageDbInstance() {
        try {


            // if (!checkResult) {


            new Thread(new Runnable() {
                @Override
                public void run() {

                    databaseReferenceUser = FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                            .child(curr_id)
                            .child(userIDCHATTINGWITH);

                    databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                            .child(userIDCHATTINGWITH)
                            .child(curr_id);

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put(MESSAGE_KEY_TAG, EMPTY_STRING);
                    hashMap.put(MESSAGE_LIVE_TAG, EMPTY_STRING);
                    hashMap.put(iCONNECT_TAG, 0);
                    hashMap.put(SENDER_ID_TAG, curr_id);
                    hashMap.put(LOADED_VIDEO_TAG, loadVideofromUrl);

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


                }
            }).start();


            //Toast.makeText(getContext(), "Happy Texting!", Toast.LENGTH_SHORT).show();
            //   }
        } catch (Exception e) {
//            Toast.makeText(getContext(),"Null pointer on current user in liveVideoChat Fragment",Toast.LENGTH_SHORT).show();
        }
    }

    public void setLiveMessageEventListener(LiveMessageEventListener liveMessageEventListener, Activity parentActivity, String photo, String usernameChattingWith, String userIdChattingWith, String youtubeID, String youtubeTitle) throws NullPointerException {
        this.liveMessageEventListener = liveMessageEventListener;
        this.parentActivity = parentActivity;
        this.photo = photo;
        this.notifyoutubeID = youtubeID;
        this.usernameChattingWith = usernameChattingWith;
        this.userChattingWithId = userIdChattingWith;
        this.youtubeTitle = youtubeTitle;


    }

    private void requestAllPermissions() {

        Dexter.withActivity(parentActivity)
                .withPermissions(/*Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,*/
                        Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted())
                            initialiseLiveVideo();
                        else {
                            // Toast.makeText(parentActivity, "Permission not given!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(parentActivity, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    private void requestAllPermissionsBeforeStart(final Activity activity, String live) {


        Dexter.withActivity(activity)
                .withPermission(/*Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,*/
                        Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        try {

                            LiveMessageRequestDialog liveMessageRequestDialog = new LiveMessageRequestDialog(activity, LiveMessageFragmentPerf.this, live);
                            // LiveMessageRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            liveMessageRequestDialog.setCancelable(false);
                            liveMessageRequestDialog.setCanceledOnTouchOutside(false);

                            liveMessageRequestDialog.show();
                        } catch (NullPointerException e) {
                            liveMessageEventListener.changeFragment();
                        }

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {


                        if (response.isPermanentlyDenied()) {
                            Toast.makeText(activity, "Allow camera permission, from the permissions menu to continue", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivity(intent);
                        } else
                            liveMessageEventListener.changeFragment();


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void stopSignalling() {

        /// leaveChannel();

        databaseReferenceUser = FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                .child(curr_id)
                .child(userChattingWithId);

        databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                .child(userChattingWithId)
                .child(curr_id);

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(MESSAGE_KEY_TAG, EMPTY_STRING);
        hashMap.put(MESSAGE_LIVE_TAG, EMPTY_STRING);
        hashMap.put(iCONNECT_TAG, 0);
        hashMap.put(SENDER_ID_TAG, EMPTY_STRING);


        databaseReferenceUserChattingWith.updateChildren(hashMap);
        databaseReferenceUser.updateChildren(hashMap);
        // checkMessage.setMessageKey(EMPTY_STRING);

        // parentActivity.finish();


    }

    private void setupSignalling(final String userChattingWithId) {


        if (!start)
            initializeAgoraEngine();


        else {


            databaseReferenceUser = FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                    .child(curr_id)
                    .child(userChattingWithId);

            databaseReferenceUserChattingWith = FirebaseDatabase.getInstance().getReference(LIVE_MESSAGE)
                    .child(userChattingWithId)
                    .child(curr_id);

            HashMap<String, Object> hashMap = new HashMap<>();
            String messageKey = databaseReferenceUser.push().getKey();
            key = messageKey;
            hashMap.put(MESSAGE_KEY_TAG, messageKey);
            hashMap.put(MESSAGE_LIVE_TAG, EMPTY_STRING);
            hashMap.put(iCONNECT_TAG, 0);
            // hashMap.put(SENDER_ID_TAG,curr_id);

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
                Log.i("uid video", uid + EMPTY_STRING);

            }

            @Override
            public void onRemoteVideoStateChanged(int uid, int i1, int i2, int i3) {
                super.onRemoteVideoStateChanged(uid, i1, i2, i3);
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(parentActivity, "onRemoteVideoStateChanged", Toast.LENGTH_SHORT).show();
                        if (mRtcEngine != null)
                            setupRemoteVideo(uid);
                    }
                });
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {


                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.v(VIDEO_CHECK_TAG, "onJoinChannelSuccess event");
                        // Toast.makeText(parentActivity, "Channel Joined", Toast.LENGTH_SHORT).show();

                        video_seekbar.setEnabled(true);

                        live_video_control_btn_lay.setVisibility(View.VISIBLE);
                        connecting_text_lay.setVisibility(View.GONE);
                        calling_text_layout.setVisibility(View.GONE);

                        connectingUserVideo.setVisibility(View.VISIBLE);
                        // Toast.makeText(parentActivity, "connected", Toast.LENGTH_SHORT).show();
                        //joiningLive = false;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {


                                complete_icon.setVisibility(View.GONE);
                                slideToActView.setText("Stop Live Expression");

                                slideToActView.resetSlider();
                                slideToActView.setReversed(true);
                                videoLive = true;


                                firstPersonVideo.setVisibility(View.VISIBLE);
                                userChattingPersonVideo.setVisibility(View.VISIBLE);
                                current_user_blinker.setVisibility(View.GONE);
                                other_user_blinker.setVisibility(View.GONE);



                            }
                        }, 1000);

                    }
                });
            }

            @Override
            public void onUserJoined(int uid, int elapsed) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Toast.makeText(parentActivity, userNameChattingWith + " Joined", Toast.LENGTH_SHORT).show();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                current_user_blinker.setVisibility(View.GONE);
                                other_user_blinker.setVisibility(View.GONE);


                                //connectingUserVideo.setVisibility(View.GONE);
                                //enable_audio.setVisibility(View.VISIBLE);
                                // Toast.makeText(parentActivity, userNameChattingWith + " Joined", Toast.LENGTH_SHORT).show();


                            }
                        }, 1000);

                    }
                });
            }

            @Override
            public void onLeaveChannel(RtcStats stats) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.v(VIDEO_CHECK_TAG, "onLeaveChannel Event");
                        resetLiveVideoViews();

                        joiningLive = false;
                        enable_audio.setVisibility(View.GONE);
                        blinkReceiver = false;
                    }
                });
            }

            @Override
            public void onUserOffline(int uid, int reason) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //stopSignalling();
                        //leaveChannel();
                        //Toast.makeText(parentActivity, userNameChattingWith + " has left", Toast.LENGTH_LONG).show();

                        /*video_seekbar.setMax(1);
                        video_seekbar.setProgress(0);*/
                        joiningLive = false;

                        connecting_text_lay.setVisibility(View.VISIBLE);

                        live_video_control_btn_lay.setVisibility(View.generateViewId());
                        connecting_text.setText("live expression disconnected");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (userPresent)
                                    live_video_control_btn_lay.setVisibility(View.VISIBLE);

                                else
                                    live_video_control_btn_lay.setVisibility(View.GONE);


                                connecting_text_lay.setVisibility(View.GONE);

                            }
                        }, 500);

                        blinkReceiver = false;

                        video_seekbar.setMax(1);

                        video_seekbar.post(new Runnable() {
                            @Override
                            public void run() {


                                complete_icon.setVisibility(View.GONE);
                                slideToActView.setText(START_LIVE);

                                slideToActView.resetSlider();
                                slideToActView.setReversed(false);
                                videoLive = false;


                                video_seekbar.setProgress(0);
                            }
                        });
                    }
                });
                // Toast.makeText(parentActivity,"User left",Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onStreamPublished(String url, int error) {
                super.onStreamPublished(url, error);
                //Toast.makeText(parentActivity, "onFirstRemoteVideoDecoded", Toast.LENGTH_SHORT).show();
            }
        };

        Log.v(VIDEO_CHECK_TAG, "setupIRtcEngineEventHandler");


        //joinChannel();

    }

    private void resetLiveVideoViews() {
        //Toast.makeText(parentActivity, "User left", Toast.LENGTH_SHORT).show();

        firstPersonVideo.setVisibility(View.GONE);
        userChattingPersonVideo.setVisibility(View.GONE);
        connectingUserVideo.setVisibility(View.GONE);
        connectedUserVideo.setVisibility(View.GONE);

        current_user_blinker.setVisibility(View.VISIBLE);
        other_user_blinker.setVisibility(View.VISIBLE);

        firstPersonVideo.setVisibility(View.GONE);
        userChattingPersonVideo.setVisibility(View.GONE);
        current_user_blinker.setVisibility(View.VISIBLE);
        other_user_blinker.setVisibility(View.VISIBLE);
        //startLiveVideo.setVisibility(View.VISIBLE);

        complete_icon.setVisibility(View.GONE);
        slideToActView.setText(START_LIVE);

        slideToActView.resetSlider();
        slideToActView.setReversed(false);
        videoLive = false;


        video_seekbar.setMax(1);
        video_seekbar.setProgress(0);

        blinkReceiver = false;

        start = true;


    }

    private void initializeAgoraEngine() {

        liveMessagingViewModel.iConnect();

        setupIRtcEngineEventHandler();
        try {
            mRtcEngine = RtcEngine.create(getContext(), getString(R.string.agora_app_id), mRtcEventHandler);
            setupVideoProfile();
        } catch (Exception e) {
            e.printStackTrace();

            // Toast.makeText(parentActivity, "Error initializeAgoraEngine(): " + e.toString(), Toast.LENGTH_LONG).show();
            resetLiveVideoViews();
        }

    }

    private void setupVideoProfile() {

        mRtcEngine.enableVideo();
        mRtcEngine.disableAudio();
        mRtcEngine.enableLocalAudio(false);
        mRtcEngine.stopAudioRecording();

        // mRtcEngine.setVideoProfile(Constants.VIDEO_PROFILE_360P_3, false);

        mRtcEngine.setVideoEncoderConfiguration(new VideoEncoderConfiguration(
                VideoEncoderConfiguration.VD_320x180,
                VideoEncoderConfiguration.FRAME_RATE.FRAME_RATE_FPS_30,
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.ORIENTATION_MODE_FIXED_PORTRAIT));
        setupLocalVideo();
    }

    private void setupLocalVideo() {

        localSurfaceView = RtcEngine.CreateRendererView(getContext());

        /*int height = displayHeight*28/100;
        int wrapperHeight = displayHeight*25/100;
        surfaceView.getLayoutParams().height = wrapperHeight;
        surfaceView.getLayoutParams().width =  height/2;*/

        localSurfaceView.setZOrderMediaOverlay(true);

        frameLocalContainer.addView(localSurfaceView);
        mRtcEngine.setupLocalVideo(new VideoCanvas(localSurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0));

        //mRtcEngine.

        joinChannel();


    }

    private void joinChannel() {
        mRtcEngine.joinChannel(null, key, "Extra Optional Data", new Random().nextInt(10000000) + 1);
        wasInCall = true;// if you do not specify the uid, Agora will assign one.

        onJoinSuccess(false);

        // mRtcEngine.set
    }


    private void setupRemoteVideo(int uid) {

        frameRemoteContainer.removeAllViews();

        //remote_audio_video_view_container.removeAllViews();

        /*if (frameRemoteContainer.getChildCount() >= 1) {
            return;
        }*/


        remotesurfaceView = RtcEngine.CreateRendererView(getContext());

        /*int height = displayHeight*28/100;
        int wrapperHeight = displayHeight*25/100;
        remotesurfaceView.getLayoutParams().height = wrapperHeight;
        remotesurfaceView.getLayoutParams().width =  height/2;*/


        frameRemoteContainer.addView(remotesurfaceView);
        mRtcEngine.setupRemoteVideo(new VideoCanvas(remotesurfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid));
        remotesurfaceView.setTag(uid);

        connectingUserVideo.setVisibility(View.GONE);


    }

    private void removeRemoteVideo() {


        if (remotesurfaceView != null) {
            frameRemoteContainer.removeView(remotesurfaceView);
        }

        remotesurfaceView = null;
    }

    private void removeLocalVideo() {

        if (localSurfaceView != null) {
            frameLocalContainer.removeView(localSurfaceView);
        }

        localSurfaceView = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        //Log.v(VIDEO_CHECK_TAG,"onPause"+ " called");
        //Toast.makeText(parentActivity,"onPauseCalled",Toast.LENGTH_SHORT).show();

        // leaveChannel();
        // destoryLiveFragment();



    }


    private void leaveChannel() {
        try {

            if (mRtcEngine != null) {
                mRtcEngine.stopPreview();
                mRtcEngine.disableVideo();
                mRtcEngine.disableAudio();
                mRtcEngine.leaveChannel();

                liveChatInitialised = false;

                removeRemoteVideo();
                removeLocalVideo();

                // mRtcEventHandler = null;

                setupIRtcEngineEventHandler();


            }

            Log.v(VIDEO_CHECK_TAG, "leaveChannel" + " called");


            //  RtcEngine.destroy();
        } catch (NullPointerException e) {
            // Toast.makeText(parentActivity, "Error leaveChannel() " + e.toString(), Toast.LENGTH_LONG).show();
            Log.v(VIDEO_CHECK_TAG, "Error in leaveChannel" + e.toString());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        //Toast.makeText(parentActivity,"onStartCalled",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        // Toast.makeText(parentActivity,"onStopCalled",Toast.LENGTH_SHORT).show();

        try {
            askHandler.removeCallbacks(askRunnable);
            singleSeekRef.removeEventListener(singleYoutubeListener);
        } catch (Exception e) {

            // Log.v(VIDEO_CHECK_TAG,"Error in onPause"+ e.toString());
        }

        liveChatInitialised = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            //youtube_player_view.removeYouTubePlayerListener(abstractYouTubePlayerListener);
            //getLifecycle().removeObserver(youtube_player_view);
            // destoryLiveFragment();
            //leaveChannel();
            // Log.v(VIDEO_CHECK_TAG,"onDestroyView"+ " called");
        } catch (Exception e) {

            //  Log.v(VIDEO_CHECK_TAG, "onDestroyView Err"+ e.toString());
        }
        // Log.v(VIDEO_CHECK_TAG,"onDestroy"+ " called");


        //  Log.v(VIDEO_CHECK_TAG,"onDestroy calling leaveChannel");

        //  leaveChannel();

    }

    @Override
    public void onResume() {
        super.onResume();
        //setupIRtcEngineEventHandler();
        // Toast.makeText(parentActivity,"onResumeCalled",Toast.LENGTH_SHORT).show();


        try {
            //initializeAgoraEngine();


        } catch (Exception e) {
            // Toast.makeText(parentActivity,"Error was in live chat",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void startLiveMessaging() {

        //urlRef.addValueEventListener(valueEventListener);
        /*try {
            closeLiveMessagingLoading.turnOffLoadingScreen();
        }catch (Exception e)
        {

        }*/


        TaskCompletionSource<Boolean> booleanTaskCompletionSource = new TaskCompletionSource<>();


        new Thread(new Runnable() {
            @Override
            public void run() {
                if (notifyoutubeID == null || notifyoutubeID.equals(STARTED) || notifyoutubeID.equals(defaultVideoID)) {
                    DatabaseReference senderChatCreateRef = FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                            .child(curr_id)
                            .child(userChattingWithId);

                    DatabaseReference receiverChatCreateRef = FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                            .child(userChattingWithId)
                            .child(curr_id);

                    Long tsLong = System.currentTimeMillis() / 1000;
                    final String ts = tsLong.toString();


                    SharedPreferences sharedPreferences = parentActivity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                    String userid, username, userphoto;
                    userid = sharedPreferences.getString(USER_ID, curr_id);
                    username = sharedPreferences.getString(USERNAME, EMPTY_STRING);
                    userphoto = sharedPreferences.getString(PHOTO_URL, defaultVideoID);


                    final HashMap<String, Object> timeStampHash = new HashMap<>();
                    timeStampHash.put(TIME_STAMP_TAG, ts);
                    timeStampHash.put(ID_TAG, userChattingWithId);
                    timeStampHash.put(USERNAME_TAG, usernameChattingWith);
                    timeStampHash.put(PHOTO_TAG, photo);
                    timeStampHash.put(SEEN_TAG, NOT_SEEN_VAL);
                    timeStampHash.put(CHAT_PENDING_TAG, false);
                    timeStampHash.put(LOCAL_USER_STAMP_TAG, tsLong);

                    senderChatCreateRef.updateChildren(timeStampHash);

                    HashMap<String, Object> timeStampHashReceiver = new HashMap<>();

                    timeStampHashReceiver.put(TIME_STAMP_TAG, ts);
                    timeStampHashReceiver.put(ID_TAG, userid);
                    timeStampHashReceiver.put(USERNAME_TAG, username);
                    timeStampHashReceiver.put(PHOTO_TAG, userphoto);
                    timeStampHashReceiver.put(SEEN_TAG, NOT_SEEN_VAL);
                    timeStampHashReceiver.put(USER_PRESENT_TAG, true);
                    timeStampHashReceiver.put(CHAT_PENDING_TAG, true);
                    timeStampHashReceiver.put(OTHER_USER_STAMP_TAG, tsLong);


                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);


                    if (!loadVideofromUrl.equals(NO)) {
                        getVideoIdFromYoutubeUrl(loadVideofromUrl);
                    }


                } else {
                    DatabaseReference receiverChatCreateRef = FirebaseDatabase.getInstance().getReference(CHATLIST_TAG)
                            .child(userChattingWithId)
                            .child(curr_id);

                    HashMap<String, Object> timeStampHashReceiver = new HashMap<>();
                    timeStampHashReceiver.put(USER_PRESENT_TAG, true);

                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);


                }

                FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                        .child(curr_id)
                        .child(userChattingWithId)
                        .onDisconnect()
                        .removeValue();


                booleanTaskCompletionSource.setResult(true);
            }
        }).start();


        Task<Boolean> booleanTask = booleanTaskCompletionSource.getTask();

        booleanTask.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {
                if (task.getResult()) {
                    liveChatInitialised = true;

                    try {
                        calling_text_layout.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        //
                    }

                    initialiseLiveragment();
                    createLiveMessageDbInstance();
                    try {
                        //closeLiveMessagingLoading.turnOffLoadingScreen();
                    } catch (Exception e) {
                        //
                    }

                    try {
                        askRunnable = new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    calling_text.setText("No reply! Ask " + usernameChattingWith + " to join");
                                    ask_progress.setVisibility(View.GONE);
                                    calling_text.setBackground(parentActivity.getResources().getDrawable(R.drawable.ask_drawable_back));
                                    canAskuser = true;
                                } catch (Exception e) {
                                    //
                                }
                            }
                        };

                        askHandler = new Handler();
                        askHandler.postDelayed(askRunnable, 15000);

                    } catch (Exception e) {
                        //
                    }

                }
            }
        });


        //.child("youtubeUrl");




        /*else
        {
            FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                    .child(curr_id)
                    .child(userIDCHATTINGWITH)
                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                }
            });
        }*/





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


    }


    @Override
    public void startPermission() {

        requestAllPermissionsBeforeStart(parentActivity, live);
    }

    @Override
    public void onSongSelected(SpotiySearchItem thumbnailItem) {

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        youtubeBottomFragmentStateListener.setDrag(false);
    }
}