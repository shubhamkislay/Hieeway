package com.hieeway.hieeway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.usage.UsageEvents;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.PlayerMessage;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.audio.AudioRendererEventListener;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.text.TextOutput;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ExoPlayerActivity extends AppCompatActivity {
    PlayerView player_view;
    SimpleExoPlayer player;
    private String sentStatus;
    private String videoUri;
    private Intent intent;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    ProgressBar loading_video;
    ProgressBar determinateBar;
    ImageView blast;
    String requestType;
    String postKey;
    private SharedPreferences sharedPreferences;
    private String userID;
    private String currentUsername;
    private String otherUserId;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USERNAME = "username";
    public static final String USER_ID = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_player);


        player_view = findViewById(R.id.player_view);
        loading_video = findViewById(R.id.loading_video);
        determinateBar = findViewById(R.id.determinateBar);
        blast = findViewById(R.id.blast);


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


        intent = getIntent();
        videoUri = intent.getStringExtra("path");
        requestType = intent.getStringExtra("requestType");
        postKey = intent.getStringExtra("postKey");
        otherUserId = intent.getStringExtra("otherUserId");
        //sentStatus = intent.getStringExtra("sentStatus");

        if (!requestType.equals("post"))
            blast.setVisibility(View.INVISIBLE);

        TrackSelector trackSelector = new DefaultTrackSelector();


        DefaultLoadControl loadControl = new DefaultLoadControl.Builder().setBufferDurationsMs(32 * 1024, 64 * 1024, 1024, 1024).createDefaultLoadControl();

        player = new SimpleExoPlayer.Builder(this, new DefaultRenderersFactory(this)).setTrackSelector(trackSelector).setLoadControl(loadControl).build();
        player_view.setPlayer(player);


    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        player_view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    private void initializePlayer() {

        try {

            MediaItem mediaItem = MediaItem.fromUri(videoUri);
            player.setMediaItem(mediaItem);
            player.setPlayWhenReady(playWhenReady);

            player.seekTo(currentWindow, playbackPosition);
            player.prepare();

            player.addListener(new Player.EventListener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == SimpleExoPlayer.STATE_READY) {
                        loading_video.setVisibility(View.INVISIBLE);
                        determinateBar.setMax((int) player.getDuration());
                        getPlayerPosition();

                        FirebaseDatabase.getInstance().getReference("Post")
                                .child(userID)
                                .child(postKey)
                                .removeValue();

                        /**
                         * Add code to delete post media from Firebase storage
                         */

                        HashMap<String, Object> postSeenHash = new HashMap<>();

                        postSeenHash.put("username", currentUsername);
                        //postSeenHash.put("photo", "default");

                        DatabaseReference seenByRef = FirebaseDatabase
                                .getInstance()
                                .getReference("SeenBy")
                                .child(otherUserId);

                        seenByRef
                                .child(postKey)
                                .child(userID)
                                .updateChildren(postSeenHash);


                    } else if (state == SimpleExoPlayer.STATE_BUFFERING)
                        loading_video.setVisibility(View.VISIBLE);
                    else if (state == SimpleExoPlayer.STATE_ENDED) {

                        blast.animate().scaleX(500.0f).scaleY(500.0f).setDuration(750);

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();


                            }
                        }, 750);


                    }
                }

                @Override
                public void onTimelineChanged(Timeline timeline, int reason) {

                }


            });

            player_view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    try {
                        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                            player.pause();
                            //Toast.makeText(ExoPlayerActivity.this,"Down",Toast.LENGTH_SHORT).show();
                        }
                        /*else if (event.getAction() == MotionEvent.ACTION_UP)
                            player.play();*/

                        else if (event.getAction() == MotionEvent.ACTION_UP) {
                            player.play();
                            //Toast.makeText(ExoPlayerActivity.this,"UP",Toast.LENGTH_SHORT).show();
                        }


                    } catch (Exception e) {
                        //
                    }
                    return true;
                }
            });

        } catch (Exception e) {
            //
            finish();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = player.getPlayWhenReady();
            playbackPosition = player.getCurrentPosition();
            currentWindow = player.getCurrentWindowIndex();
            player.release();
            player = null;
        }
    }

    private void getPlayerPosition() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            determinateBar.setProgress((int) (player.getDuration() - player.getCurrentPosition()), true);
                        } else
                            determinateBar.setProgress((int) (player.getDuration() - player.getCurrentPosition()));

                        getPlayerPosition();
                    } catch (Exception e) {
                        //
                    }
                }
            }, 100);
        } catch (Exception e) {
            //
        }
    }
}