package com.shubhamkislay.jetpacklogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;

public class VideoPlayActivity extends AppCompatActivity implements SurfaceHolder.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener/*, MediaPlayer.OnBufferingUpdateListener*/ {

    VideoView videoView;
    SurfaceView surfaceView;
    RelativeLayout progress_layout, load_layout;
    private Uri videoUri;
    private String userIdChattingWith, sender, mKey, videoUrl;
    TextView loading;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    private PlayerView player_view;
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoView = findViewById(R.id.video_view);

        Intent intent = getIntent();

        progress_layout = findViewById(R.id.progress_layout);
        load_layout = findViewById(R.id.load_layout);


        videoUrl = intent.getStringExtra("videoUrl");
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        sender = intent.getStringExtra("sender");
        mKey = intent.getStringExtra("mKey");

        loading = findViewById(R.id.loading);


        videoUri = Uri.parse(videoUrl);

        player_view = findViewById(R.id.player_view);


        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

        player_view.setPlayer(player);

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "jetpacklogin"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(videoUri);

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);


        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    progress_layout.setVisibility(View.GONE);
                    load_layout.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                }

                if (playbackState == Player.STATE_BUFFERING) {
                    progress_layout.setVisibility(View.VISIBLE);
                    load_layout.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.VISIBLE);
                }

                if (playbackState == Player.STATE_ENDED) {
                    if (sender.equals(userIdChattingWith))
                        deleteVideoMessage();
                    else
                        finish();
                }


            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
/*        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        loading.setText("Loading... "+percent);
                    }
                });



                return false;
            }
        });*/

        /*vidSurface = findViewById(R.id.surfaceView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);*/

        // try {
        //    mediaPlayer = new MediaPlayer();






        /*}
        catch(Exception e){
            e.printStackTrace();
        }*/

        /*videoView.setVideoURI(videoUri);


        //   onBufferingUpdate();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progress_layout.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (sender.equals(userIdChattingWith))
                    deleteVideoMessage();
                else
                    finish();
            }
        });*/


    }

    private void deleteVideoMessage() {
        FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sender)
                .child(mKey)
                .removeValue();


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("seen", "seen");
        hashMap.put("videourl", "played");
        FirebaseDatabase.getInstance().getReference("Messages")
                .child(sender)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mKey)
                .updateChildren(hashMap);
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        photoRef.delete();

        finish();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

      /*  progress_layout.setVisibility(View.GONE);
        loading.setVisibility(View.GONE);
        mp.start();*/
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
/*        mediaPlayer = new MediaPlayer();
        mediaPlayer.setDisplay(holder);

        try {
            mediaPlayer.setDataSource(videoUrl);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);*/
        // mediaPlayer.setOnBufferingUpdateListener(this);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //  mediaPlayer.setDisplay(null);


    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        /*if (sender.equals(userIdChattingWith))
            deleteVideoMessage();
        else
            finish();*/

    }

/*    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        loading.setText("Loading... "+percent);

    }*/

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.release();
            player = null;
            finish();
        }
    }
}
