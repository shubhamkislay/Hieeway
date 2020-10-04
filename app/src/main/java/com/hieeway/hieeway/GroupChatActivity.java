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
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
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
import com.hieeway.hieeway.Interface.SpotifySongSelectedListener;
import com.hieeway.hieeway.Model.GroupMessage;
import com.hieeway.hieeway.Model.SpotiySearchItem;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
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

import de.hdodenhof.circleimageview.CircleImageView;

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
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String USERNAME = "username";
    public static final String SPOTIFY_TOKEN = "spotify_token";
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
    private boolean bottomSheetVisible = false;

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

        send_button = findViewById(R.id.send_button);
        disablerecord_button = (ImageView) findViewById(R.id.disablerecord_button);
        message_recycler_View = (RecyclerView) findViewById(R.id.message_recycler_View);

        search_video_btn = findViewById(R.id.search_video_btn);

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
        currentUsername = sharedPreferences.getString(PHOTO_URL, "default");
        spotifyToken = sharedPreferences.getString(SPOTIFY_TOKEN, "default");


        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupNameTxt = intent.getStringExtra("groupName");
        iconUrl = intent.getStringExtra("icon");

        groupMessageRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                .child(groupID);


        Glide.with(this).load(iconUrl).into(icon);
        groupName.setText(groupNameTxt);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        message_recycler_View.setLayoutManager(linearLayoutManager);
        message_recycler_View.scrollToPosition(groupMessageList.size() - 1);

        groupMessageList.clear();
        groupMessageAdapter = new GroupMessageAdapter(this, groupMessageList, userID, this);

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
    public void onSongSelected() {
        bottomSheetVisible = false;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetVisible) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            bottomSheetVisible = false;
        } else
            super.onBackPressed();

    }
}