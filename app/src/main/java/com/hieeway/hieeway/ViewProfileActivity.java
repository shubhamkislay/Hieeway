package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hieeway.hieeway.Model.User;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.types.ImageUri;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.HashMap;

public class ViewProfileActivity extends AppCompatActivity {


    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    ImageView profile_pic_background, center_dp;
    TextView username, name, feeling_icon, feeling_txt, bio_txt, emoji_icon;
    String feelingEmoji;
    RelativeLayout emoji_holder_layout;
    RelativeLayout ring_blinker_layout;
    String friendStatus;
    String usernameText;
    String photourl;
    String currentUsername;
    Button friend_btn_cancel;
    String feeling_text_label;
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    final String appPackageName = "com.spotify.music";
    final String referrer = "adjust_campaign=com.hieeway.hieeway&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
    Point size;
    float displayHeight;
    ImageView spotify_cover;
    TextView song_name, artist_name;
    String songId = null, songName = null, artistName = null;
    ImageUri songUri = null;
    RelativeLayout connect_spotify;
    private SpotifyAppRemote mSpotifyAppRemote;


    String userId;
    Button friend_btn, start_chat;
    private ValueEventListener valueEventListener;
    private DatabaseReference findUserDataReference;


    ImageView spotify_icon;
    RelativeLayout music_layout, music_loading_layout;
    private TextView music_loading_txt;
    private RelativeLayout connect_spotify_layout;
    private TextView connect_spotify_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        username = findViewById(R.id.username);
        profile_pic_background = findViewById(R.id.profile_pic_background);
        center_dp = findViewById(R.id.center_dp);
        name = findViewById(R.id.name);
        feeling_icon = findViewById(R.id.feeling_icon);
        bio_txt = findViewById(R.id.bio_txt);
        emoji_holder_layout = findViewById(R.id.emoji_holder_layout);
        ring_blinker_layout = findViewById(R.id.ring_blinker_layout);
        emoji_icon = findViewById(R.id.emoji_icon);
        feeling_txt = findViewById(R.id.feeling_txt);
        spotify_cover = findViewById(R.id.spotify_cover);
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.artist_name);

        spotify_icon = findViewById(R.id.spotify_icon);

        friend_btn = findViewById(R.id.friend_btn);
        friend_btn_cancel = findViewById(R.id.friend_btn_cancel);
        start_chat = findViewById(R.id.start_chat);
        music_loading_txt = findViewById(R.id.music_loading_txt);

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        music_loading_layout = findViewById(R.id.music_loading_layout);
        connect_spotify_layout = findViewById(R.id.connect_spotify_layout);
        connect_spotify_text = findViewById(R.id.connect_spotify_text);

        music_layout = findViewById(R.id.music_layout);


        connect_spotify_text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        music_loading_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feeling_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        username.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));
        bio_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        friend_btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        friend_btn_cancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        start_chat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        connect_spotify = findViewById(R.id.connect_spotify);


        Intent intent = getIntent();

        username.setText(intent.getStringExtra("username"));
        name.setText(intent.getStringExtra("name"));

        photourl = intent.getStringExtra("photourl");
        userId = intent.getStringExtra("userId");
        currentUsername = intent.getStringExtra("currentUsername");

        usernameText = intent.getStringExtra("username");

        center_dp.getLayoutParams().height = (int) displayHeight / 4;
        center_dp.getLayoutParams().width = (int) displayHeight / 8;


        Glide.with(this).load(photourl.replace("s96-c", "s384-c")).into(center_dp);
        Glide.with(this).load(photourl.replace("s96-c", "s384-c")).into(profile_pic_background);


        checkUserData(intent);


        friend_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest();
            }
        });

        start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, VerticalPageActivity.class);
                intent.putExtra("username", usernameText);
                intent.putExtra("userid", userId);
                intent.putExtra("photo", photourl);
                intent.putExtra("live", "no");
                //   intent.putExtra("otherUserPublicKey",chatStamp.getPublicKey());

                startActivity(intent);
                finish();
            }
        });


        spotify_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSpotifyAppRemote.getPlayerApi().play(songId);
                } catch (Exception e) {
                    Toast.makeText(ViewProfileActivity.this, "Cannot play this song", Toast.LENGTH_SHORT).show();

                }
            }
        });

        spotify_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }
                } catch (Exception e) {

                }
            }
        });


        connect_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm = getPackageManager();
                boolean isSpotifyInstalled;


                try {
                    pm.getPackageInfo("com.spotify.music", 0);
                    isSpotifyInstalled = true;
                    Toast.makeText(ViewProfileActivity.this, "Spotify is installed", Toast.LENGTH_SHORT).show();
                    AuthenticationRequest.Builder builder =
                            new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                    builder.setScopes(new String[]{"streaming"});
                    AuthenticationRequest request = builder.build();

                    AuthenticationClient.openLoginActivity(ViewProfileActivity.this, REQUEST_CODE, request);
                } catch (PackageManager.NameNotFoundException e) {
                    isSpotifyInstalled = false;

                    Toast.makeText(ViewProfileActivity.this, "Spotify is not installed", Toast.LENGTH_SHORT).show();

                    try {
                        Uri uri = Uri.parse("market://details")
                                .buildUpon()
                                .appendQueryParameter("id", appPackageName)
                                .appendQueryParameter("referrer", referrer)
                                .build();
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    } catch (android.content.ActivityNotFoundException ignored) {
                        Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                                .buildUpon()
                                .appendQueryParameter("id", appPackageName)
                                .appendQueryParameter("referrer", referrer)
                                .build();
                        startActivity(new Intent(Intent.ACTION_VIEW, uri));
                    }

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    connect_spotify.setVisibility(View.GONE);
                    Toast.makeText(ViewProfileActivity.this, "Connected to spotify :D", Toast.LENGTH_SHORT).show();

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response

                    connect_spotify.setVisibility(View.VISIBLE);
                    Toast.makeText(ViewProfileActivity.this, "Cannot connect to spotify " + response.getError(), Toast.LENGTH_SHORT).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }
    private void checkUserData(Intent intent) {
        // try {

        feeling_txt.setText(intent.getStringExtra("feeling_txt"));
        bio_txt.setText(intent.getStringExtra("bio_txt"));
        friendStatus = intent.getStringExtra("friendStatus");
        feelingEmoji = intent.getStringExtra("feelingEmoji");
        feeling_text_label = intent.getStringExtra("feeling_txt");
        try {
            if (feeling_text_label.length() < 1)
                feeling_txt.setText(HAPPY);
        } catch (Exception e) {
            feeling_text_label = "happy";
            feeling_txt.setText(HAPPY);
        }
/*        }catch (Exception e)
        {
            //
            //   Toast.makeText(ViewProfileActivity.this,"Data unavailable",Toast.LENGTH_SHORT).show();
            //friendStatus = "friends";
            loadNullValue(userId,FirebaseAuth.getInstance().getCurrentUser().getUid());
            return;
        }
        try {*/
        if (friendStatus.equals("friends")
                || friendStatus.equals("notFriend")
                || friendStatus.equals("got")
                || friendStatus.equals("requested"))
            setUserData();
        // friendStatus.length();


    }

    private void loadNullValue(final String userId, String uid) {

/*        DatabaseReference findFriendShipReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(uid)
                .child(userId);

        findFriendShipReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    try {
                        friendStatus = friend.getStatus();
                    }catch (NullPointerException e)
                    {
                        friendStatus = "notFriend";
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        findUserDataReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId);


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);

                    try {
                        songId = user.getSpotifyId();
                        songName = user.getSpotifySong();
                        artistName = user.getSpotifyArtist();
                        songUri = user.getSpotifyCover();

                        if (songName.length() > 0) {

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    music_layout.setVisibility(View.VISIBLE);
                                }
                            }, 1000);

                        }
                    } catch (Exception e) {
                        //
                        music_layout.setVisibility(View.GONE);
                    }


                    try {
                        feeling_txt.setText(user.getFeeling());
                    } catch (Exception e) {
                        feeling_txt.setText(HAPPY);
                    }

                    bio_txt.setText(user.getBio());

                    try {
                        feelingEmoji = user.getFeelingIcon();
                    } catch (Exception e) {
                        feelingEmoji = "default";
                    }

                    try {
                        feeling_text_label = user.getFeeling();
                    } catch (Exception e) {
                        feeling_text_label = HAPPY;
                    }

                    setUserData();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        findUserDataReference.addValueEventListener(valueEventListener);




    }

    public void setUserData() {
        // try {
        if (friendStatus.equals("friends")) {
            feeling_txt.setVisibility(View.VISIBLE);
            emoji_holder_layout.setVisibility(View.VISIBLE);
            friend_btn.setText("Remove Friend");

            startBlinking();


            ConnectionParams connectionParams =
                    new ConnectionParams.Builder(CLIENT_ID)
                            .setRedirectUri(REDIRECT_URI)
                            .setPreferredThumbnailImageSize(500)
                            .setPreferredImageSize(500)
                            .showAuthView(true)
                            .build();


            SpotifyAppRemote.CONNECTOR.connect(ViewProfileActivity.this, connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;

                            connect_spotify.setVisibility(View.GONE);
                            connect_spotify_layout.setVisibility(View.GONE);
                            spotify_icon.setVisibility(View.VISIBLE);
                            music_loading_layout.setVisibility(View.GONE);
                            // Toast.makeText(getActivity(),"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                            // Now you can start interacting with App Remote




                            //Toast.makeText(SpotifyActivity.this,"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                            // Now you can start interacting with App Remote

                            try {
                                song_name.setText(songName);
                                artist_name.setText(artistName);

                                mSpotifyAppRemote.getImagesApi().getImage(songUri)
                                        .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                            @Override
                                            public void onResult(Bitmap bitmap) {

                                                spotify_cover.setImageBitmap(bitmap);
                                            }
                                        });
                            } catch (Exception e) {
                                //
                            }

                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            // spotifyConnected = false;
                            //song_name.setText(songName);
                            //artist_name.setText(artistName);
                            connect_spotify.setVisibility(View.VISIBLE);
                            connect_spotify_layout.setVisibility(View.VISIBLE);
                            spotify_icon.setVisibility(View.GONE);
                            music_loading_layout.setVisibility(View.GONE);

                            music_layout.setVisibility(View.VISIBLE);



                            // Toast.makeText(ViewProfileActivity.this, "Cannot connect to spotify automatically :( " + throwable.getStackTrace(), Toast.LENGTH_SHORT).show();
                            // Something went wrong when attempting to connect! Handle errors here
                        }
                    });



            try {
                if (feelingEmoji.equals("default")) {
                    emoji_icon.setVisibility(View.INVISIBLE);
                    feeling_icon.setVisibility(View.VISIBLE);
                    setFeelingEmoji(feeling_text_label);
                } else {
                    emoji_icon.setVisibility(View.VISIBLE);
                    feeling_icon.setVisibility(View.INVISIBLE);
                    emoji_icon.setText(feelingEmoji);

                }
            } catch (Exception e) {
                loadNullValue(userId, FirebaseAuth.getInstance().getCurrentUser().getUid());
                feelingEmoji = "default";
                emoji_icon.setVisibility(View.INVISIBLE);
                feeling_icon.setVisibility(View.VISIBLE);
                setFeelingEmoji(HAPPY);

            }
        } else {
            if (friendStatus.equals("requested")) {
                friend_btn.setText("Cancel Request");
            } else if (friendStatus.equals("got")) {
                friend_btn.setText("Accept");
                friend_btn_cancel.setVisibility(View.VISIBLE);
            } else if (friendStatus.equals("notFriend")) {
                friend_btn.setText("Add Friend");
                friend_btn.setTextColor(getResources().getColor(R.color.colorBlack));
                friend_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            }
        }

        friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (friendStatus) {
                    case "requested":
                        cancelRequest();
                        break;

                    case "got":
                        acceptRequest();
                        friend_btn_cancel.setVisibility(View.GONE);
                        start_chat.setVisibility(View.VISIBLE);
                        break;

                    case "friends":
                        removeFriend();
                        break;

                    case "notFriend":
                        addFriend();
                        break;
                }

            }
        });/*
        }catch (Exception e)
        {
            Toast.makeText(ViewProfileActivity.this,"Data is not present",Toast.LENGTH_SHORT).show();
            loadNullValue(userId,FirebaseAuth.getInstance().getCurrentUser().getUid());
            }*/


    }

    private void addFriend() {

        friend_btn.setText("Cancel Request");
        friend_btn.setTextColor(getResources().getColor(R.color.colorWhite));
        friend_btn.setBackgroundTintList(null);

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("friendId", userId);
        hashMap.put("status", "requested");
        hashMap.put("username", usernameText);

        requestSentReference.updateChildren(hashMap);


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> receiveHashMap = new HashMap<>();


        receiveHashMap.put("friendId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        receiveHashMap.put("status", "got");
        receiveHashMap.put("username", currentUsername);

        requestReceiveReference.updateChildren(receiveHashMap);

        friendStatus = "requested";
    }

    private void removeFriend() {
        feeling_txt.setVisibility(View.INVISIBLE);
        emoji_holder_layout.setVisibility(View.INVISIBLE);
        start_chat.setVisibility(View.GONE);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(usernameText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(ViewProfileActivity.this, "unsubscribed to " + usernameText, Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelRequest();


    }

    private void acceptRequest() {

        feeling_txt.setVisibility(View.VISIBLE);
        emoji_holder_layout.setVisibility(View.VISIBLE);
        friend_btn.setText("Remove Friend");
        friend_btn.setTextColor(getResources().getColor(R.color.colorWhite));
        friend_btn.setBackgroundTintList(null);


        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("friendId", userId);
        hashMap.put("status", "friends");
        hashMap.put("username", usernameText);

        requestSentReference.updateChildren(hashMap);

        FirebaseMessaging.getInstance().subscribeToTopic(usernameText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewProfileActivity.this, "Friend request accepted", Toast.LENGTH_SHORT).show();
                }
            }
        });


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> receiveHashMap = new HashMap<>();


        receiveHashMap.put("friendId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        receiveHashMap.put("status", "friends");
        receiveHashMap.put("username", currentUsername);

        requestReceiveReference.updateChildren(receiveHashMap);

        friendStatus = "friends";


        try {
            if (feelingEmoji.equals("default")) {
                emoji_icon.setVisibility(View.INVISIBLE);
                feeling_icon.setVisibility(View.VISIBLE);
                setFeelingEmoji(feeling_text_label);
            } else {
                emoji_icon.setVisibility(View.VISIBLE);
                feeling_icon.setVisibility(View.INVISIBLE);
                emoji_icon.setText(feelingEmoji);
            }
        } catch (Exception e) {
            emoji_icon.setVisibility(View.INVISIBLE);
            feeling_icon.setVisibility(View.VISIBLE);
            setFeelingEmoji(HAPPY);
        }
        startBlinking();
    }

    private void cancelRequest() {

        friend_btn.setText("Add Friend");
        friend_btn.setTextColor(getResources().getColor(R.color.colorBlack));
        friend_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        requestSentReference.removeValue();


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        requestReceiveReference.removeValue();

        friendStatus = "notFriend";
    }

    private void setFeelingEmoji(String feeling) {

        switch (feeling) {
            case HAPPY:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                feeling_txt.setText(HAPPY);
                break;
            case SAD:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                feeling_txt.setText(SAD);
                break;
            case BORED:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                feeling_txt.setText(BORED);
                break;
            case ANGRY:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                feeling_txt.setText(ANGRY);
                break;
            case EXCITED:
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                feeling_txt.setText(EXCITED);
                break;
            case CONFUSED:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                feeling_txt.setText(CONFUSED);
                break;
        }
    }

    private void startBlinking() {
/*        ring_blinker_layout.animate().alpha(0.0f).setDuration(950);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ring_blinker_layout.animate().alpha(1.0f).setDuration(950);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ring_blinker_layout.animate().alpha(0.0f).setDuration(950);
                        startBlinking();
                    }
                }, 1000);
            }
        }, 1000);*/
    }


    @Override
    protected void onPause() {
        super.onPause();
        findUserDataReference.removeEventListener(valueEventListener);
    }
}
