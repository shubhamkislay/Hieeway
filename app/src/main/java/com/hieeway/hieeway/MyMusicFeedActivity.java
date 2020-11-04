package com.hieeway.hieeway;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.MyMusicFeedAdapter;
import com.hieeway.hieeway.Adapters.MyMusicFeedAdapter;
import com.hieeway.hieeway.Adapters.SeenByAdapter;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Interface.MyMusicFeedItemListener;
import com.hieeway.hieeway.Model.DatabaseListener;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.MusicPost;
import com.hieeway.hieeway.Model.SeenByUser;
import com.hieeway.hieeway.Utils.SpotifyMusicPostRemote;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MyMusicFeedActivity extends AppCompatActivity implements MyMusicFeedItemListener {

    private static final int REQUEST_CODE = 1337;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CHECKED_TIMESTAMP = "checked_timestamp";
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    final String referrer = "adjust_campaign=com.hieeway.hieeway&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
    final String appPackageName = "com.spotify.music";
    public static final String USER_ID = "userid";
    public static final String USERNAME = "username";
    RecyclerView music_recyclerview;
    List<Music> userList;
    List<MusicPost> musicPostList;
    int sentListSize;
    Boolean searchedList = false;
    RelativeLayout loading_feed;
    TextView seeing_music_txt, connecting_spotify_txt;
    Boolean remoteReady = false;
    private SpotifyAppRemote mSpotifyAppRemote;
    private MyMusicFeedAdapter musicFeedAdapter;
    ImageButton new_update;
    RelativeLayout new_update_lay;
    Boolean initialCheck = false;
    private boolean secondCheck = false;
    private boolean listPopulated = false;
    ImageView music_pal;
    private RelativeLayout music_pal_lay;
    private Button music_pal_back;
    private TextView no_music_txt;
    private TextView connection_issue_hint;
    private Button connection_error_btn;
    private Boolean connectionSuccessull = false;
    RelativeLayout no_music_layout;
    String otherUserId, otherUserName, otherUserPhoto;
    DatabaseReference musicPostRef;
    private ValueEventListener musicFetchValueEventListener;
    private SharedPreferences sharedPreferences;
    private String userID;
    private String currentUsername;
    private List<DatabaseListener> databaseListeners = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();


    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music_feed);

        // FirebaseDatabase.getInstance().setPersistenceEnabled(false);

        try {
            sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
            userID = sharedPreferences.getString(USER_ID, "");
            currentUsername = sharedPreferences.getString(USERNAME, "");


        } catch (NullPointerException e) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            try {
                currentUsername = sharedPreferences.getString(USERNAME, "");
            } catch (Exception ne) {
                currentUsername = "";
            }
        }


        new_update = findViewById(R.id.new_update);

        new_update_lay = findViewById(R.id.new_update_lay);

        connecting_spotify_txt = findViewById(R.id.connecting_spotify_txt);

        music_pal_lay = findViewById(R.id.music_pal_lay);

        music_pal_back = findViewById(R.id.music_pal_back);

        connection_issue_hint = findViewById(R.id.connection_issue_hint);

        connection_error_btn = findViewById(R.id.connecting_error_btn);

        no_music_layout = findViewById(R.id.no_music_layout);


        connection_error_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connection_issue_hint.setVisibility(View.VISIBLE);
            }
        });


        otherUserId = getIntent().getStringExtra("otherUserId");
        otherUserName = getIntent().getStringExtra("otherUserName");
        otherUserPhoto = getIntent().getStringExtra("otherUserPhoto");

        musicPostRef = FirebaseDatabase.getInstance().getReference("MyMusicPosts")
                .child(userID);


        music_recyclerview = findViewById(R.id.music_recyclerview);
        loading_feed = findViewById(R.id.loading_feed);
        seeing_music_txt = findViewById(R.id.seeing_music_txt);
        no_music_txt = findViewById(R.id.no_music_txt);

        seeing_music_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        connecting_spotify_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_music_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        //new_update.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        music_pal = findViewById(R.id.music_pal);

        userList = new ArrayList<>();
        musicPostList = new ArrayList<>();

        music_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(music_recyclerview);

        SpotifyAppRemote spotifyAppRemote = SpotifyMusicPostRemote.getInstance().getSpotifyAppRemote();

        musicFeedAdapter = new MyMusicFeedAdapter(MyMusicFeedActivity.this,
                userID,
                currentUsername,
                musicPostList,
                MyMusicFeedActivity.this,
                mSpotifyAppRemote,
                otherUserPhoto,
                otherUserName,
                otherUserId
        );
        music_recyclerview.setAdapter(musicFeedAdapter);

        new_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_update_lay.setVisibility(View.GONE);
                musicFeedAdapter = new MyMusicFeedAdapter(MyMusicFeedActivity.this,
                        userID,
                        currentUsername,
                        musicPostList,
                        MyMusicFeedActivity.this,
                        mSpotifyAppRemote,
                        otherUserPhoto,
                        otherUserName,
                        otherUserId
                );
                music_recyclerview.setAdapter(musicFeedAdapter);
                musicFeedAdapter.notifyDataSetChanged();
                loading_feed.setVisibility(View.GONE);


            }
        });


        music_pal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMusicFeedActivity.this, MusicPalActivity.class));
            }
        });
        music_pal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMusicFeedActivity.this, MusicPalActivity.class));
            }
        });

        music_pal_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMusicFeedActivity.this, MusicPalActivity.class));
            }
        });

        new_update_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_update_lay.setVisibility(View.GONE);
                musicFeedAdapter = new MyMusicFeedAdapter(MyMusicFeedActivity.this,
                        userID,
                        currentUsername,
                        musicPostList,
                        MyMusicFeedActivity.this,
                        mSpotifyAppRemote,
                        otherUserPhoto,
                        otherUserName,
                        otherUserId
                );
                music_recyclerview.setAdapter(musicFeedAdapter);
                musicFeedAdapter.notifyDataSetChanged();
                loading_feed.setVisibility(View.GONE);
            }
        });


        userStatusOnDiconnect();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .setPreferredThumbnailImageSize(1500)
                        .setPreferredImageSize(1500)
                        .showAuthView(true)
                        .build();


        try {
            SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
        } catch (Exception e) {

        }
        //if(remoteReady)


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!connectionSuccessull)
                    connection_error_btn.setVisibility(View.VISIBLE);
            }
        }, 10000);


        //SpotifyAppRemote spotifyAppRemote = SpotifyRemoteHelper.getInstance().getSpotifyAppRemote();


        if (spotifyAppRemote == null) {
            SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                    new Connector.ConnectionListener() {

                        @Override
                        public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                            mSpotifyAppRemote = spotifyAppRemote;

                            connectionSuccessull = true;

                            // remoteReady = true;

                            connecting_spotify_txt.setText("Launching Music Beacon...");


                            SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(mSpotifyAppRemote);
/*
                            Intent intent1 = new Intent(MusicFeedActivity.this, MusicBeamService.class);
                            startService(intent1);*/

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //populateMusicList();
                                    populateMusicListForASingleUser();
                                }
                            }, 500);

                            // Toast.makeText(MusicFeedActivity.this,"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                            // Now you can start interacting with App Remote


                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e("MainActivity", throwable.getMessage(), throwable);

                            PackageManager pm = null;
                            try {
                                pm = getPackageManager();

                                boolean isSpotifyInstalled;


                                try {
                                    pm.getPackageInfo("com.spotify.music", 0);
                                    isSpotifyInstalled = true;
                                    //Toast.makeText(MusicFeedActivity.this, "Log in to Spotify", Toast.LENGTH_SHORT).show();
                                    AuthenticationRequest.Builder builder =
                                            new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                                    builder.setScopes(new String[]{"streaming"});
                                    AuthenticationRequest request = builder.build();

                                    AuthenticationClient.openLoginActivity(MyMusicFeedActivity.this, REQUEST_CODE, request);
                                } catch (PackageManager.NameNotFoundException e) {
                                    isSpotifyInstalled = false;

                                    finish();
                                    //Toast.makeText(MusicFeedActivity.this, "Install spotify app to continue", Toast.LENGTH_LONG).show();

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
                            } catch (Exception e) {
                                Toast.makeText(MyMusicFeedActivity.this, "Cannot connect with Spotify App", Toast.LENGTH_SHORT).show();
                            }


                            // Something went wrong when attempting to connect! Handle errors here
                        }
                    });
        } else {
            mSpotifyAppRemote = spotifyAppRemote;
            connectionSuccessull = true;

            // remoteReady = true;

            SpotifyMusicPostRemote.getInstance().setSpotifyAppRemote(spotifyAppRemote);

            connecting_spotify_txt.setText("Launching Music Beacon...");


            //SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(mSpotifyAppRemote);

            /*Intent intent1 = new Intent(MusicFeedActivity.this, MusicBeamService.class);
            startService(intent1);*/

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //populateMusicList();


                    populateMusicListForASingleUser();
                }
            }, 500);


            // Toast.makeText(MusicFeedActivity.this,"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();
            // Now you can start interacting with App Remote


        }

    }

    private void populateMusicListForASingleUser() {

        musicPostList.clear();
        musicFetchValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                //musicPostList.clear();
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                       /* String musicKey = FirebaseDatabase.getInstance().getReference("MusicPost")
                                .child(otherUserId).push().getKey();*/

                        MusicPost musicPost = dataSnapshot.getValue(MusicPost.class);
                        //MusicPost musicPost = new MusicPost();

                        if (!musicPostList.contains(musicPost))
                            musicPostList.add(musicPost);


/*
                        List<String> seenBy  = null;
                        try{
                            seenBy = musicPost.getSeenBy();
                            if(!seenBy.contains(userID))
                                userList.add(new Music(musicPost.getSpotifyId(),
                                        musicPost.getSpotifySong(),
                                        musicPost.getSpotifyArtist(),
                                        musicPost.getSpotifyCover(),
                                        musicPost.getTimestamp(),
                                        otherUserId,
                                        otherUserPhoto,
                                        otherUserName,
                                        musicPost.getPostKey(),
                                        seenBy));
                        }catch (Exception e)
                        {
                            //
                            seenBy = new ArrayList<>();
                            userList.add(new Music(musicPost.getSpotifyId(),
                                    musicPost.getSpotifySong(),
                                    musicPost.getSpotifyArtist(),
                                    musicPost.getSpotifyCover(),
                                    musicPost.getTimestamp(),
                                    otherUserId,
                                    otherUserPhoto,
                                    otherUserName,
                                    musicPost.getPostKey(),
                                    seenBy));
                        }*/
                       /*String seenBy;
                        try {
                            seenBy = musicPost.getSeenBy();
                            if (!seenBy.contains(userID)) {
                                *//*userList.add(new Music(musicPost.getSpotifyId(),
                                        musicPost.getSpotifySong(),
                                        musicPost.getSpotifyArtist(),
                                        musicPost.getSpotifyCover(),
                                        musicPost.getTimestamp(),
                                        otherUserId,
                                        otherUserPhoto,
                                        otherUserName,
                                        musicPost.getPostKey(),
                                        seenBy));*//*
                                musicPostList.add(musicPost);
                                //Toast.makeText(MusicFeedActivity.this,"Music added by checking",Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                               // Toast.makeText(MusicFeedActivity.this,"Music not added by checking "+userID,Toast.LENGTH_SHORT).show();
                            }


                        }catch (Exception e)
                        {
                           // seenBy = new ArrayList<>();;
                            *//*userList.add(new Music(musicPost.getSpotifyId(),
                                    musicPost.getSpotifySong(),
                                    musicPost.getSpotifyArtist(),
                                    musicPost.getSpotifyCover(),
                                    musicPost.getTimestamp(),
                                    otherUserId,
                                    otherUserPhoto,
                                    otherUserName,
                                    musicPost.getPostKey(),
                                    seenBy));*//*

                            musicPostList.add(musicPost);

                           // Toast.makeText(MusicFeedActivity.this,"Music added",Toast.LENGTH_SHORT).show();
                        }*/

                    }
                    Collections.sort(musicPostList, Collections.<MusicPost>reverseOrder());

                    if (musicPostList.size() > 3) {
                        DatabaseReference dataDelRef = FirebaseDatabase.getInstance().getReference();
                        HashMap<String, Object> delHashRef = new HashMap<>();
                        for (int i = 3; i < musicPostList.size(); i++) {
                            delHashRef.put("Post/" + userID + "/" + musicPostList.get(i).getPostKey(), null);
                            delHashRef.put("MusicPost/" + userID + "/" + musicPostList.get(i).getPostKey(), null);
                        }
                        //dataDelRef.updateChildren(delHashRef);
                        musicPostList.subList(3, musicPostList.size()).clear();
                    } else if (musicPostList.size() < 1) {
                        //musicPostRef.addListenerForSingleValueEvent(musicFetchValueEventListener);
                    }


                    /*musicFeedAdapter = new MyMusicFeedAdapter(MusicFeedActivity.this,
                            userID,
                            musicPostList,
                            MusicFeedActivity.this,
                            mSpotifyAppRemote,
                            otherUserPhoto,
                            otherUserName,
                            otherUserId
                    );
                    music_recyclerview.setAdapter(musicFeedAdapter);
                    musicFeedAdapter.notifyDataSetChanged();*/

                    musicFeedAdapter.updateList(musicPostList, mSpotifyAppRemote);
                    loading_feed.setVisibility(View.GONE);

                    // Toast.makeText(MusicFeedActivity.this, "List size: " + userList.size(), Toast.LENGTH_SHORT).show();


                } else {
                    /*musicFeedAdapter = new MyMusicFeedAdapter(MusicFeedActivity.this,
                            userID,
                            musicPostList,
                            MusicFeedActivity.this,
                            mSpotifyAppRemote,
                            otherUserPhoto,
                            otherUserName,
                            otherUserId
                    );
                    music_recyclerview.setAdapter(musicFeedAdapter);
                    musicFeedAdapter.notifyDataSetChanged();*/

                    musicFeedAdapter.updateList(musicPostList, mSpotifyAppRemote);
                    loading_feed.setVisibility(View.GONE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


/*        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dummy", userID);

        FirebaseDatabase.getInstance().getReference("Dummy")
                .child(userID)
                .updateChildren(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            musicPostRef.addListenerForSingleValueEvent(musicFetchValueEventListener);
                        }
                    }
                });*/

        musicPostRef.addValueEventListener(musicFetchValueEventListener);

    }

    /*
        private void populateMusicList() {


            FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            userList.clear();
                            if (dataSnapshot.exists()) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    Friend friend = snapshot.getValue(Friend.class);
                                    if (friend.getStatus().equals("friends")) {

                                        FirebaseDatabase.getInstance().getReference("Music")
                                                .child(friend.getFriendId())
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot musicSnapshot) {
                                                        if (musicSnapshot.exists()) {

                                                            try {
                                                                Music music = musicSnapshot.getValue(Music.class);


                                                                if (!userList.contains(music)) {
                                                                    userList.add(music);
                                                                } else {
                                                                    userList.remove(music);
                                                                    userList.add(music);
                                                                }


                                                                if (searchedList && sentListSize < userList.size()) {
                                                                    try {

                                                                        Collections.sort(userList, Collections.<Music>reverseOrder());

                                                                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                                        editor.putString(CHECKED_TIMESTAMP, userList.get(0).getTimestamp());
                                                                        editor.apply();

                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                musicFeedAdapter = new MyMusicFeedAdapter(MusicFeedActivity.this, userID, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                                                                                music_recyclerview.setAdapter(musicFeedAdapter);
                                                                                musicFeedAdapter.notifyDataSetChanged();
                                                                                loading_feed.setVisibility(View.GONE);


                                                                            }
                                                                        }, 500);

                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                listPopulated = true;


                                                                                if (userList.size() > 0)
                                                                                    no_music_layout.setVisibility(View.GONE);

                                                                                else
                                                                                    no_music_layout.setVisibility(View.VISIBLE);
                                                                            }
                                                                        }, 1500);

                                                                    } catch (Exception e) {
                                                                        //
                                                                    }

                                                                }

                                                            } catch (Exception e) {
                                                                //
                                                            }


                                                        } else {
                                                            no_music_layout.setVisibility(View.VISIBLE);
                                                        }


                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                    }
                                                });

                                        FirebaseDatabase.getInstance().getReference("Music")
                                                .child(friend.getFriendId())
                                                .addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot musicSnapshot) {
                                                        if (musicSnapshot.exists()) {
                                                            Music music = musicSnapshot.getValue(Music.class);


                                                            if (!userList.contains(music)) {
                                                                userList.add(music);
                                                            } else {
                                                                userList.remove(music);
                                                                userList.add(music);
                                                            }


                                                            if (searchedList && sentListSize < userList.size()) {
                                                                try {

                                                                    Collections.sort(userList, Collections.<Music>reverseOrder());

                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {

                                                                            if (listPopulated) {
                                                                                new_update_lay.setVisibility(View.VISIBLE);
                                                                                *//*    else
                                                                            if (initialCheck) {
                                                                                if (secondCheck)
                                                                                    new_update_lay.setVisibility(View.VISIBLE);
                                                                                else
                                                                                    secondCheck = true;
                                                                            } else
                                                                                initialCheck = true;*//*
                                                                        }
                                                                    }
                                                                }, 500);

                                                            } catch (Exception e) {
                                                                //
                                                            }

                                                        }

                                                    }


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });







                                }
                            }


                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            float displayHeight = size.y;

                            loading_feed.animate().translationY(-displayHeight - 100).setDuration(500);
                            *//*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Collections.sort(userList, Collections.<Music>reverseOrder());
                                    musicFeedAdapter = new MyMusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                                    music_recyclerview.setAdapter(musicFeedAdapter);
                                    musicFeedAdapter.notifyDataSetChanged();
                                    loading_feed.setVisibility(View.GONE);
                                }
                            }, 500);*//*


                            searchedList = true;
                            sentListSize = userList.size();


                        } else {
                            Toast.makeText(MusicFeedActivity.this, "You have no friends to looks for music update\nSearch and add friends from the search tab", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

   */
    @Override
    protected void onPause() {
        super.onPause();
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        try {
            musicPostRef.removeEventListener(musicFetchValueEventListener);
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            for (DatabaseListener databaseListener : databaseListeners) {
                databaseListener.getDatabaseReference().removeEventListener(databaseListener.getValueEventListener());
            }
        } catch (Exception e) {
            //
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            //SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
        } catch (Exception e) {

        }
    }

    @Override
    public void loadForPostKey(String postKey) {
        addSeenByList(postKey);
    }

    private void addSeenByList(String postKey) {


        RecyclerView seen_recyclerview;
        List<SeenByUser> stringList;
        SharedPreferences sharedPreferences;
        String currentUser;
        TextView seen_by_txt = findViewById(R.id.seen_by_txt);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        currentUser = sharedPreferences.getString("userid", "");
        stringList = new ArrayList<>();
        seen_recyclerview = findViewById(R.id.seen_recyclerview);

        seen_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        seen_recyclerview.setHasFixedSize(true);

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    stringList.clear();
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SeenByUser userName = snapshot.getValue(SeenByUser.class);
                        stringList.add(userName);
                        ++count;
                    }
                    seen_by_txt.setText("Tap to See \nSeen by " + count);
                    SeenByAdapter seenByAdapter = new SeenByAdapter(MyMusicFeedActivity.this, stringList);
                    seen_recyclerview.setAdapter(seenByAdapter);
                } else {
                    seen_by_txt.setText("Tap to See \nSeen by " + 0);
                    SeenByAdapter seenByAdapter = new SeenByAdapter(MyMusicFeedActivity.this, stringList);
                    seen_recyclerview.setAdapter(seenByAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SeenBy")
                .child(currentUser)
                .child(postKey);

        databaseReference.addValueEventListener(valueEventListener);

        DatabaseListener databaseListener = new DatabaseListener(databaseReference, valueEventListener);
        databaseListeners.add(databaseListener);


    }
}
