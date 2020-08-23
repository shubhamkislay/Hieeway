package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.MusicFeedAdapter;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.MusicAdapterItem;
import com.hieeway.hieeway.Model.User;
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

public class MusicFeedActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String CHECKED_TIMESTAMP = "checked_timestamp";
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    final String referrer = "adjust_campaign=com.hieeway.hieeway&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
    final String appPackageName = "com.spotify.music";
    RecyclerView music_recyclerview;
    List<Music> userList;
    int sentListSize;
    Boolean searchedList = false;
    RelativeLayout loading_feed;
    TextView seeing_music_txt, connecting_spotify_txt;
    Boolean remoteReady = false;
    private SpotifyAppRemote mSpotifyAppRemote;
    private MusicFeedAdapter musicFeedAdapter;
    ImageButton new_update;
    RelativeLayout new_update_lay;
    Boolean initialCheck = false;
    private boolean secondCheck = false;
    private boolean listPopulated = false;
    ImageView music_pal;
    private RelativeLayout music_pal_lay;
    private Button music_pal_back;
    private TextView no_music_txt;

    @Override
    protected void onResume() {
        super.onResume();

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

        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;

                        // remoteReady = true;

                        connecting_spotify_txt.setText("Launching Music Beacon...");


                        Intent intent1 = new Intent(MusicFeedActivity.this, MusicBeamService.class);
                        startService(intent1);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                populateMusicList();
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
                                Toast.makeText(MusicFeedActivity.this, "Log in to Spotify", Toast.LENGTH_SHORT).show();
                                AuthenticationRequest.Builder builder =
                                        new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                                builder.setScopes(new String[]{"streaming"});
                                AuthenticationRequest request = builder.build();

                                AuthenticationClient.openLoginActivity(MusicFeedActivity.this, REQUEST_CODE, request);
                            } catch (PackageManager.NameNotFoundException e) {
                                isSpotifyInstalled = false;

                                finish();
                                Toast.makeText(MusicFeedActivity.this, "Install spotify app to continue", Toast.LENGTH_LONG).show();

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
                            Toast.makeText(MusicFeedActivity.this, "Cannot connect with Spotify App", Toast.LENGTH_SHORT).show();
                        }

                        
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

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
        setContentView(R.layout.activity_music_feed);

        // FirebaseDatabase.getInstance().setPersistenceEnabled(false);


        new_update = findViewById(R.id.new_update);

        new_update_lay = findViewById(R.id.new_update_lay);

        connecting_spotify_txt = findViewById(R.id.connecting_spotify_txt);

        music_pal_lay = findViewById(R.id.music_pal_lay);

        music_pal_back = findViewById(R.id.music_pal_back);





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

        music_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(music_recyclerview);

        new_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_update_lay.setVisibility(View.GONE);
                musicFeedAdapter = new MusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                music_recyclerview.setAdapter(musicFeedAdapter);
                musicFeedAdapter.notifyDataSetChanged();
                loading_feed.setVisibility(View.GONE);


            }
        });


        music_pal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MusicFeedActivity.this, MusicPalActivity.class));
            }
        });
        music_pal_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MusicFeedActivity.this, MusicPalActivity.class));
            }
        });

        music_pal_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MusicFeedActivity.this, MusicPalActivity.class));
            }
        });

        new_update_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_update_lay.setVisibility(View.GONE);
                musicFeedAdapter = new MusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                music_recyclerview.setAdapter(musicFeedAdapter);
                musicFeedAdapter.notifyDataSetChanged();
                loading_feed.setVisibility(View.GONE);
            }
        });


    }

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
                                                                            musicFeedAdapter = new MusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                                                                            music_recyclerview.setAdapter(musicFeedAdapter);
                                                                            musicFeedAdapter.notifyDataSetChanged();
                                                                            loading_feed.setVisibility(View.GONE);

                                                                            if (userList.size() > 0)
                                                                                no_music_txt.setVisibility(View.GONE);

                                                                            else
                                                                                no_music_txt.setVisibility(View.VISIBLE);

                                                                        }
                                                                    }, 500);

                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            listPopulated = true;
                                                                        }
                                                                    }, 1500);

                                                                } catch (Exception e) {
                                                                    //
                                                                }

                                                            }

                                                        } catch (Exception e) {
                                                            //
                                                        }



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
                                                                            /*    else
                                                                            if (initialCheck) {
                                                                                if (secondCheck)
                                                                                    new_update_lay.setVisibility(View.VISIBLE);
                                                                                else
                                                                                    secondCheck = true;
                                                                            } else
                                                                                initialCheck = true;*/
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
                            /*new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Collections.sort(userList, Collections.<Music>reverseOrder());
                                    musicFeedAdapter = new MusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                                    music_recyclerview.setAdapter(musicFeedAdapter);
                                    musicFeedAdapter.notifyDataSetChanged();
                                    loading_feed.setVisibility(View.GONE);
                                }
                            }, 500);*/


                            searchedList = true;
                            sentListSize = userList.size();


                        } else {
                            Toast.makeText(MusicFeedActivity.this, "You have no friends to looks for music update\nSearch and add friends from the search tab", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    protected void onPause() {
        super.onPause();
        // FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
