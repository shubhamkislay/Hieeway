package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.MusicFeedAdapter;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.User;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.ArrayList;
import java.util.List;

public class MusicFeedActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    final String appPackageName = "com.spotify.music";
    RecyclerView music_recyclerview;
    List<User> userList;
    int sentListSize;
    Boolean searchedList = false;
    RelativeLayout loading_feed;
    TextView seeing_music_txt;
    Boolean remoteReady = false;
    private SpotifyAppRemote mSpotifyAppRemote;
    private MusicFeedAdapter musicFeedAdapter;

    @Override
    protected void onResume() {
        super.onResume();



        //if(remoteReady)

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_feed);

        // FirebaseDatabase.getInstance().setPersistenceEnabled(false);
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


        SpotifyAppRemote.CONNECTOR.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    @Override
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;

                        // remoteReady = true;

                        populateMusicList();
                        // Toast.makeText(getActivity(),"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                        // Now you can start interacting with App Remote


                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        Log.e("MainActivity", throwable.getMessage(), throwable);

                        Toast.makeText(MusicFeedActivity.this, "Cannot connect to spotify app" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });

        music_recyclerview = findViewById(R.id.music_recyclerview);
        loading_feed = findViewById(R.id.loading_feed);
        seeing_music_txt = findViewById(R.id.seeing_music_txt);

        seeing_music_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        userList = new ArrayList<>();

        music_recyclerview.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(music_recyclerview);


    }

    private void populateMusicList() {


        FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        userList.clear();
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Friend friend = snapshot.getValue(Friend.class);
                                if (friend.getStatus().equals("friends")) {
                                    FirebaseDatabase.getInstance().getReference("Users")
                                            .child(friend.getFriendId())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                                                    if (userSnapshot.exists()) {
                                                        User user = userSnapshot.getValue(User.class);
                                                        try {
                                                            if (user.getSpotifySong().length() > 0) {
                                                                if (!userList.contains(user)) {
                                                                    userList.add(user);
                                                                } else {
                                                                    userList.remove(user);
                                                                    userList.add(user);

                                                                }


                                                                if (searchedList && sentListSize < userList.size()) {
                                                                    try {

                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                musicFeedAdapter = new MusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                                                                                music_recyclerview.setAdapter(musicFeedAdapter);
                                                                                musicFeedAdapter.notifyDataSetChanged();
                                                                                loading_feed.setVisibility(View.GONE);
                                                                            }
                                                                        }, 500);

                                                                    } catch (Exception e) {
                                                                        //
                                                                    }

                                                                }
                                                                // Toast.makeText(MusicFeedActivity.this, "User added: "+user.getUsername(), Toast.LENGTH_LONG).show();
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
                                }
                            }


                            Display display = getWindowManager().getDefaultDisplay();
                            Point size = new Point();
                            display.getSize(size);
                            float displayHeight = size.y;

                            loading_feed.animate().translationY(-displayHeight - 100).setDuration(500);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    musicFeedAdapter = new MusicFeedAdapter(MusicFeedActivity.this, userList, MusicFeedActivity.this, mSpotifyAppRemote);
                                    music_recyclerview.setAdapter(musicFeedAdapter);
                                    musicFeedAdapter.notifyDataSetChanged();
                                    loading_feed.setVisibility(View.GONE);
                                }
                            }, 500);


                            searchedList = true;
                            sentListSize = userList.size();


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
