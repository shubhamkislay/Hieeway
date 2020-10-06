package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
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
import com.hieeway.hieeway.Adapters.GroupMessageAdapter;
import com.hieeway.hieeway.Adapters.SpotifySearchAdapter;
import com.hieeway.hieeway.Fragments.LiveMessageFragmentPerf;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Interface.ScrollRecyclerViewListener;
import com.hieeway.hieeway.Interface.SeeAllGroupItemsListener;
import com.hieeway.hieeway.Interface.SpotifySongSelectedListener;
import com.hieeway.hieeway.Model.GroupMessage;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.SpotiySearchItem;
import com.hieeway.hieeway.Model.YoutubeSync;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.hieeway.hieeway.NavButtonTest.USER_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_NAME;
import static com.hieeway.hieeway.NavButtonTest.USER_PHOTO;
import static com.hieeway.hieeway.VerticalPageActivityPerf.userIDCHATTINGWITH;

public class GroupChatActivity extends AppCompatActivity implements ScrollRecyclerViewListener, SpotifySongSelectedListener {

    private RelativeLayout bin;
    private ImageView disablerecord_button;
    private MediaRecorder recorder = null;
    private RelativeLayout equlizer;
    private RelativeLayout equi_one;
    private RelativeLayout equi_two;
    private RelativeLayout equi_three;
    private RelativeLayout equi_four;
    private RelativeLayout equi_five;
    private RecordView recordView;
    private RecordButton recordButton;
    private Button camera_background;
    private EditText message_box;
    private Button camera;
    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean isDisablerecord_button = false;
    private Button send_button;
    private String groupID, groupNameTxt, iconUrl;
    private CircleImageView icon;
    private TextView groupName;
    private static final String YOUTUBEID_TAG = "youtubeID";
    private static final String VIDEOSEC_TAG = "videoSec";
    private static final String VIDEOTITLE_TAG = "videoTitle";
    private static final String NO = "no";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String USERNAME = "username";
    public static final String SPOTIFY_TOKEN = "spotify_token";
    private static final String VIDEO_NODE = "GroupVideo";
    private static final String API_KEY = "AIzaSyDl7rYj9tB9Hn1gp_Oe4TUpEyGbTVYGrZc";
    private RecyclerView message_recycler_View;
    private ValueEventListener valueEventListener;
    private List<GroupMessage> groupMessageList;
    private DatabaseReference groupMessageRef;
    private GroupMessageAdapter groupMessageAdapter;
    private String userID;
    private String userPhoto;
    private String currentUsername;
    private boolean updated = false;
    private Boolean scrollRecyclerView = true;
    private static final int REQUEST_CODE = 1338;
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    RecyclerView video_listView;
    RelativeLayout video_search_progress;
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout bottom_sheet_dialog_layout;
    Button search_video_btn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Button spotify_btn;
    private String spotifyToken;
    private EditText search_video_edittext;
    private SpotifySearchAdapter spotifySearchAdapter;
    private List<SpotiySearchItem> spotiySearchItemList;
    private int displayHeight;
    private Display display;
    private Point size;
    private Button back_btn;
    private SpotifyAppRemote appRemote;
    private static final String defaultVideoID = "default";
    private static final String regex = "http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)";
    private static final String cipherInstancePadding = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
    private static String youtubeUrl = "https://youtube.com/";
    private static String yoututbeHomeURL = "https://youtube.com/";
    private static String youtubeID = "default";
    String loadVideofromUrl;
    Button youtube_button;
    WebView youtube_web_view;
    RelativeLayout youtube_layout;
    com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer mYouTubePlayer;
    private YouTube.Videos.List videoQuery;
    private YouTube youtube;
    private TextView sync_video_txt;
    private RelativeLayout sync_video_layout;
    private String videoID = "kJQP7kiw5Fk";
    private YouTubePlayerView youtube_player_view;

    private boolean bottomSheetVisible = false;
    private AbstractYouTubePlayerListener abstractYouTubePlayerListener;
    private boolean videoLoaded = false;
    private String youtubeTitle = "";
    private CustomUiController customUiController = null;
    private float youtubeVideoSec = 0;
    private float youtubeSynSec = 0;
    private boolean playerInitialised = false;
    private boolean loadWhenInitialised;
    private String videoTitle = "";
    private boolean videoStarted;
    private boolean pauseVideo;
    private ValueEventListener seekValueEventListener;
    private DatabaseReference seekRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        camera_background = findViewById(R.id.camera_background);
        message_box = findViewById(R.id.message_box);
        youtube_button = findViewById(R.id.youtube_btn);
        youtube_web_view = findViewById(R.id.youtube_web_view);
        sync_video_txt = findViewById(R.id.sync_video_txt);
        sync_video_layout = findViewById(R.id.sync_video_layout);
        youtube_player_view = findViewById(R.id.youtube_player_view);
        youtube_layout = findViewById(R.id.youtube_layout);

        youtube_web_view.getSettings().setJavaScriptEnabled(true);

        WebSettings webSettings = youtube_web_view.getSettings();
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);


        send_button = findViewById(R.id.send_button);
        disablerecord_button = (ImageView) findViewById(R.id.disablerecord_button);
        message_recycler_View = (RecyclerView) findViewById(R.id.message_recycler_View);

        search_video_btn = findViewById(R.id.search_video_btn);
        back_btn = findViewById(R.id.back_btn);

        equlizer = findViewById(R.id.equlizer);
        equi_one = findViewById(R.id.equi_one);
        equi_two = findViewById(R.id.equi_two);
        equi_three = findViewById(R.id.equi_three);
        equi_four = findViewById(R.id.equi_four);
        equi_five = findViewById(R.id.equi_five);
        spotify_btn = findViewById(R.id.spotify_btn);
        search_video_edittext = findViewById(R.id.search_video_edittext);
        groupMessageList = new ArrayList<>();
        spotiySearchItemList = new ArrayList<>();
        bottom_sheet_dialog_layout = findViewById(R.id.bottom_sheet_dialog_layout);
        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);

        video_listView = findViewById(R.id.video_listView);
        LinearLayoutManager spotifyLinearLayoutManager = new LinearLayoutManager(GroupChatActivity.this);
        spotifyLinearLayoutManager.setStackFromEnd(false);
        video_listView.setLayoutManager(spotifyLinearLayoutManager);


        icon = findViewById(R.id.group_icon);
        groupName = findViewById(R.id.group_name);
        video_search_progress = findViewById(R.id.video_search_progress);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        userPhoto = sharedPreferences.getString(PHOTO_URL, "default");
        currentUsername = sharedPreferences.getString(USERNAME, "");
        spotifyToken = sharedPreferences.getString(SPOTIFY_TOKEN, "default");

        youtube = new YouTube.Builder(new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {
            }
        }).setApplicationName(GroupChatActivity.this.getString(R.string.app_name)).build();

        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupNameTxt = intent.getStringExtra("groupName");
        iconUrl = intent.getStringExtra("icon");

        groupMessageRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                .child(groupID);


        if (!iconUrl.equals("default"))
            Glide.with(this).load(iconUrl).into(icon);
        else
            Glide.with(this).load(getDrawable(R.drawable.no_profile)).into(icon);
        groupName.setText(groupNameTxt);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        message_recycler_View.setLayoutManager(linearLayoutManager);
        message_recycler_View.scrollToPosition(groupMessageList.size() - 1);

        groupMessageList.clear();
        groupMessageAdapter = new GroupMessageAdapter(
                this,
                groupMessageList,
                userID,
                this,
                groupID,
                SpotifyRemoteHelper.getInstance().getSpotifyAppRemote());

        message_recycler_View.setAdapter(groupMessageAdapter);

        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        int height = displayHeight * 30 / 100;


        try {
            groupName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        } catch (Exception e) {
            //
        }


        recordView.setSmallMicColor(Color.parseColor("#ffffff"));

        // recordView.setSlideToCancelText("TEXT");

        //disable Sounds
        recordView.setSoundEnabled(false);

        //prevent recording under one Second (it's false by default)
        recordView.setLessThanSecondAllowed(false);

        //set Custom sounds onRecord
        //you can pass 0 if you don't want to play sound in certain state
        recordView.setCustomSounds(R.raw.quiet_knock, R.raw.shootem, R.raw.record_error);

        //change slide To Cancel Text Color
        recordView.setSlideToCancelTextColor(Color.parseColor("#ffffff"));
        //change slide To Cancel Arrow Color
        recordView.setSlideToCancelArrowColor(Color.parseColor("#ffffff"));
        //change Counter Time (Chronometer) color
        recordView.setCounterTimeColor(getResources().getColor(R.color.colorPrimaryDark));

        camera = findViewById(R.id.camera);

        spotify_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToSpotify();
            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        youtube_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtube_web_view.setVisibility(View.VISIBLE);
                Animation animation = AnimationUtils.loadAnimation(GroupChatActivity.this, R.anim.youtube_webview_open);
                youtube_web_view.setAnimation(animation);
                message_box.clearFocus();

            }
        });


        //recordButton.setListenForRecord(false);
        /*if (ContextCompat.checkSelfPermission(GroupChatActivity.this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            disablerecord_button.setVisibility(View.VISIBLE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.GONE);
            //recordButton.setEnabled(false);
        } else {
            disablerecord_button.setVisibility(View.GONE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.VISIBLE);
        }*/

        disablerecord_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestAudioPermisson();
                    Toast.makeText(GroupChatActivity.this, "Requesting Audio Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        send_button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    // viewHolder.user_photo.setAlpha(0.4f);

                    send_button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);
                    send_button.animate().scaleX(0.85f).scaleY(0.85f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    send_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                    //         viewHolder.user_photo.animate().alpha(1.0f).setDuration(50);

                    send_button.animate().scaleX(0.85f).scaleY(0.85f).setDuration(0);


                } else {
                    // viewHolder.user_photo.animate().setDuration(50).alpha(1.0f);

                    send_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);

                    send_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                }

                return false;
            }

        });

        final View customUiView = youtube_player_view.inflateCustomPlayerUi(R.layout.youtube_player_custom_view);


        abstractYouTubePlayerListener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);

                mYouTubePlayer = youTubePlayer;
                customUiController = new CustomUiController(customUiView, mYouTubePlayer, GroupChatActivity.this, youtubeID, youtubeTitle);

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
                            .child(groupID).removeValue();

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

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                    requestAllPermissions();


                else
                {
                    Intent intent = new Intent(GroupChatActivity.this, CameraActivity.class);

                    // ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);
                    if (currentUsername != null && currentUserPhoto != null && currentUserActivePhoto != null) {

                        intent.putExtra("userChattingWithId", userIdChattingWith);
                        intent.putExtra("userphoto", photo);
                        intent.putExtra("activePhoto", activePhoto);
                        intent.putExtra("username", usernameChattingWith);
                        intent.putExtra("currentUsername", currentUsername);
                        intent.putExtra("currentUserPhoto", currentUserPhoto);
                        intent.putExtra("currentUserActivePhoto", currentUserActivePhoto);
                        intent.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
                        intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                        intent.putExtra("otherUserPublicKey", otherUserPublicKey);
                        intent.putExtra("currentUserPublicKey", currentUserPublicKey);



                        // intent.putExtra("userChattingWithId", currentUserPhoto);


                        startActivity(intent);
                    } else
                        Toast.makeText(GroupChatActivity.this, "Getting user details", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        search_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //searchOnYoutube(search_video_edittext.getText().toString());
                //onUrlPasted(search_video_edittext.getText().toString());
                bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 3 / 7;
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                bottomSheetVisible = true;
                video_search_progress.setVisibility(View.VISIBLE);


                if (search_video_edittext.getText().toString().length() > 0)
                    findSpotifySong(search_video_edittext.getText().toString());

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
                    bottomSheetVisible = true;
                    bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 3 / 7;
                    //onUrlPasted(v.getText().toString());
                    video_search_progress.setVisibility(View.VISIBLE);

                    return false;
                }
                return true;
            }
        });


        bottom_sheet_dialog_layout.getLayoutParams().height = (int) displayHeight * 1 / 10;

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {

                    //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                    message_box.clearFocus();
                    search_video_edittext.requestFocus();
                    search_video_edittext.setCursorVisible(true);


                    bottomSheetVisible = true;

                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

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

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                //requestAudioPermisson();
                // Toast.makeText(AudioRecorderActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                //  Log.d("RecordButton","RECORD BUTTON CLICKED");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                // Log.d("RecordView", "Basket Animation Finished");
                camera.setVisibility(View.VISIBLE);
                camera_background.setVisibility(View.VISIBLE);
                message_box.setVisibility(View.VISIBLE);

                //message_box_behind.setVisibility(View.VISIBLE);
            }
        });


        recordView.setSlideToCancelText("Slide to cancel");

        //IMPORTANT
        recordButton.setRecordView(recordView);

        seekRef = FirebaseDatabase.getInstance().getReference(VIDEO_NODE)
                .child(groupID);

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


        populateMessages();

        message_recycler_View.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    message_recycler_View.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                message_recycler_View.smoothScrollToPosition(groupMessageList.size() - 1);
                            } catch (Exception e) {

                            }
                        }
                    }, 100);
                }
            }
        });


        youtube_player_view.addYouTubePlayerListener(abstractYouTubePlayerListener);

    }

    private void connectToSpotify() {

        if (sharedPreferences.getString(SPOTIFY_TOKEN, "default").equals("default")) {
            Toast.makeText(GroupChatActivity.this, "Connecting to Spotify...", Toast.LENGTH_LONG).show();
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();

            Toast.makeText(GroupChatActivity.this, "Connecting to Spotify...", Toast.LENGTH_LONG).show();

            AuthenticationClient.openLoginActivity(GroupChatActivity.this, REQUEST_CODE, request);
        } else {
            //Toast.makeText(GroupChatActivity.this,"Connected to Spotify",Toast.LENGTH_SHORT).show();
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetVisible = true;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {

            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response

                    editor.putString(SPOTIFY_TOKEN, response.getAccessToken());
                    editor.apply();
                    spotifyToken = response.getAccessToken();
                    Toast.makeText(GroupChatActivity.this, "Connected to Spotify: " + response.getAccessToken(), Toast.LENGTH_SHORT).show();

                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetVisible = true;
                    if (search_video_edittext.getText().toString().length() > 0)
                        findSpotifySong(search_video_edittext.getText().toString());

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    Toast.makeText(GroupChatActivity.this, "Error " + response.getError(), Toast.LENGTH_SHORT).show();
                    video_search_progress.setVisibility(View.GONE);
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
                    video_search_progress.setVisibility(View.GONE);
                    Toast.makeText(GroupChatActivity.this, "Error " + response.getType().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void populateMessages() {


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    int messages = 0;
                    groupMessageList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GroupMessage groupMessage = dataSnapshot.getValue(GroupMessage.class);

                        //Toast.makeText(GroupChatActivity.this,"Added: " +groupMessage.getMessageText(),Toast.LENGTH_SHORT).show();
                        groupMessageList.add(groupMessage);
                        messages += 1;
                    }


                   // Toast.makeText(GroupChatActivity.this, "Total Messages: " + groupMessageList.size() + "\n Counted: " + messages, Toast.LENGTH_SHORT).show();
                    groupMessageAdapter.updateList(groupMessageList);
                    if (scrollRecyclerView)
                        message_recycler_View.scrollToPosition(groupMessageList.size() - 1);
                   /* groupMessageAdapter = new GroupMessageAdapter(GroupChatActivity.this,groupMessageList,userID);
                    message_recycler_View.setAdapter(groupMessageAdapter);
                    message_recycler_View.scrollToPosition(groupMessageList.size() - 1);*/

                   /*if(updated)
                    groupMessageAdapter.updateList(groupMessageList);
                   else
                   {
                       groupMessageAdapter = new GroupMessageAdapter(GroupChatActivity.this,groupMessageList,userID);
                       message_recycler_View.setAdapter(groupMessageAdapter);
                       updated = true;
                   }*/

                    //message_recycler_View.scrollToPosition(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        groupMessageRef.addValueEventListener(valueEventListener);


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
                    // Toast.makeText(GroupChatActivity.this,"Response: "+task.getResult(),Toast.LENGTH_SHORT).show();
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

                /*Toast.makeText(GroupChatActivity.this, "Track id: "+trackId
                        +"\nArtist: "+artist+"\nSong: "+songName+"\nImage: "+imageUrl, Toast.LENGTH_LONG).show();*/

                SpotiySearchItem spotiySearchItem = new SpotiySearchItem();
                spotiySearchItem.setArtistName(artist);
                spotiySearchItem.setImageUrl(imageUrl);
                spotiySearchItem.setSongName(songName);
                spotiySearchItem.setTrackId(uri);

                spotiySearchItemList.add(spotiySearchItem);


            }


            spotifySearchAdapter = new SpotifySearchAdapter(spotiySearchItemList, GroupChatActivity.this, SpotifyRemoteHelper.getInstance().getSpotifyAppRemote(), iconUrl, groupID, groupNameTxt, GroupChatActivity.this, "group");
            video_listView.setAdapter(spotifySearchAdapter);
            video_search_progress.setVisibility(View.GONE);


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            Toast.makeText(GroupChatActivity.this, "Connecting to spotify: " + e.toString(), Toast.LENGTH_LONG).show();
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();

            AuthenticationClient.openLoginActivity(GroupChatActivity.this, REQUEST_CODE, request);

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
                            .child(groupID)
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


    private void sendMessage() {


        if (message_box.getText().toString().length() > 0) {
            String message = message_box.getText().toString();
            DatabaseReference groupMessageSendRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                    .child(groupID);


            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String messageId = groupMessageSendRef.push().getKey();


            HashMap<String, Object> groupMessageHash = new HashMap<>();
            groupMessageHash.put("messageText", message);
            groupMessageHash.put("senderId", userID);
            groupMessageHash.put("messageId", messageId);
            groupMessageHash.put("timeStamp", timestamp.toString());
            groupMessageHash.put("photo", userPhoto);
            groupMessageHash.put("sentStatus", "sending");
            groupMessageHash.put("username", currentUsername);
            groupMessageHash.put("type", "text");
            //groupMessageHash.put("mediaID", "none");


            groupMessageSendRef.child(messageId).updateChildren(groupMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    HashMap<String, Object> groupMessageUpdateHash = new HashMap<>();
                    groupMessageUpdateHash.put("sentStatus", "sent");
                    groupMessageSendRef.child(messageId).updateChildren(groupMessageUpdateHash);

                }
            });

            message_box.setText("");
        }

    }

    private void requestAudioPermisson() {



        /*Dexter.withActivity(GroupChatActivity.this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        disablerecord_button.setVisibility(View.GONE);
                        recordButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();*/

        Dexter.withActivity(GroupChatActivity.this)
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            disablerecord_button.setVisibility(View.GONE);
                            recordButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(GroupChatActivity.this, "Please give audio record permission", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(GroupChatActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            groupMessageRef.removeEventListener(valueEventListener);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void scrollViewToLastItem(Boolean scrollRecyclerView) {
        this.scrollRecyclerView = scrollRecyclerView;
    }

    @Override
    public void onSongSelected(SpotiySearchItem thumbnailItem) {
        bottomSheetVisible = false;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);


        DatabaseReference groupMessageSendRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                .child(groupID);


        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String messageId = groupMessageSendRef.push().getKey();

        DatabaseReference musicSendRef = FirebaseDatabase.getInstance().getReference("MusicMessage")
                .child(groupID);

        String musicKey = musicSendRef.push().getKey();


        HashMap<String, Object> songHash = new HashMap<>();
        songHash.put("spotifyId", thumbnailItem.getTrackId());
        songHash.put("spotifySong", thumbnailItem.getSongName());
        songHash.put("spotifyArtist", thumbnailItem.getArtistName());
        songHash.put("spotifyCover", thumbnailItem.getImageUrl());
        //songHash.put("username", USER_NAME);
        //songHash.put("userId", USER_ID);
        //songHash.put("userPhoto", USER_PHOTO);
        //songHash.put("musicKey", musicKey);
        // songHash.put("timestamp", timestamp.toString());

        musicSendRef.child(musicKey).updateChildren(songHash);

        //song_name.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));


        HashMap<String, Object> groupMessageHash = new HashMap<>();
        groupMessageHash.put("messageText", "");
        groupMessageHash.put("senderId", userID);
        groupMessageHash.put("messageId", messageId);
        groupMessageHash.put("timeStamp", timestamp.toString());
        groupMessageHash.put("photo", userPhoto);
        groupMessageHash.put("sentStatus", "sending");
        groupMessageHash.put("username", currentUsername);
        groupMessageHash.put("type", "song");
        groupMessageHash.put("mediaID", musicKey);


        groupMessageSendRef.child(messageId).updateChildren(groupMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                HashMap<String, Object> groupMessageUpdateHash = new HashMap<>();
                groupMessageUpdateHash.put("sentStatus", "sent");
                groupMessageSendRef.child(messageId).updateChildren(groupMessageUpdateHash);

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetVisible) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetVisible = false;
        } else
            super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();
        seekRef.addValueEventListener(seekValueEventListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        seekRef.removeEventListener(seekValueEventListener);
    }
}