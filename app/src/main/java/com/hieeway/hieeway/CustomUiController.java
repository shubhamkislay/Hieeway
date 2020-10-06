package com.hieeway.hieeway;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener;

import java.util.HashMap;

//import static com.hieeway.hieeway.VerticalPageActivity.userIDCHATTINGWITH;
import static com.hieeway.hieeway.VerticalPageActivityPerf.userIDCHATTINGWITH;

public class CustomUiController implements YouTubePlayerListener {

    private final FadeViewHelper fadeViewHelper;
    private YouTubePlayerSeekBar youtube_player_seekbar;
    private boolean isPlaying = false;
    FrameLayout frameLayout;
    RelativeLayout toggle_view;
    RelativeLayout play_pause_btn;
    YouTubePlayer youTubePlayer;
    RelativeLayout text_back, text_back_two;
    private TextView video_title;
    Context context;
    private boolean seekVisible = true;
    private boolean playing = true;
    private ProgressBar buffering_progress;
    private boolean displayPreview = false;


    @SuppressLint("ClickableViewAccessibility")
    public CustomUiController(View customPlayerUI, final YouTubePlayer youTubePlayer, final Context context, String videoID, String videoTitle, String type) {


        this.youTubePlayer = youTubePlayer;
        this.context = context;
        youtube_player_seekbar = customPlayerUI.findViewById(R.id.youtube_player_seekbar);

        frameLayout = customPlayerUI.findViewById(R.id.view_container);

        fadeViewHelper = new FadeViewHelper(frameLayout);

        toggle_view = customPlayerUI.findViewById(R.id.toggle_view);

        play_pause_btn = customPlayerUI.findViewById(R.id.play_pause_btn);

        video_title = customPlayerUI.findViewById(R.id.video_title);

        text_back = customPlayerUI.findViewById(R.id.text_back);

        text_back_two = customPlayerUI.findViewById(R.id.text_back_two);

        //video_title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        buffering_progress = customPlayerUI.findViewById(R.id.buffering_progress);




        customPlayerUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing)
                    fadeViewHelper.toggleVisibility();




                youtube_player_seekbar.setVisibility(View.VISIBLE);
                youtube_player_seekbar.setAlpha(1.0f);
            }
        });


        toggle_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fadeViewHelper.toggleVisibility();
                if (playing)
                    updateControllView();
                //youtube_player_seekbar.setAlpha(1.0f);
            }
        });

        play_pause_btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (playing) {
                    youTubePlayer.pause();
                    // video_title.setVisibility(View.GONE);
                    play_pause_btn.setBackground(context.getResources().getDrawable(R.drawable.play_btn));

                    play_pause_btn.animate().setDuration(300).alpha(1.0f);
                    youtube_player_seekbar.animate().setDuration(300).alpha(1.0f);
                    video_title.animate().setDuration(300).alpha(1.0f);
                    text_back.animate().setDuration(300).alpha(1.0f);
                    text_back_two.animate().setDuration(300).alpha(1.0f);
                    playing = false;

                } else {
                    youTubePlayer.play();
                    //video_title.setVisibility(View.VISIBLE);
                    play_pause_btn.setBackground(context.getResources().getDrawable(R.drawable.pause_btn));
                    playing = true;


                }
            }
        });


        fadeViewHelper.setAnimationDuration(FadeViewHelper.DEFAULT_ANIMATION_DURATION);
        fadeViewHelper.setFadeOutDelay(FadeViewHelper.DEFAULT_FADE_OUT_DELAY);
        fadeViewHelper.setDisabled(true);


        youTubePlayer.addListener(this);
        youTubePlayer.addListener(youtube_player_seekbar);
        youTubePlayer.addListener(fadeViewHelper);



        youtube_player_seekbar.setYoutubePlayerSeekBarListener(new YouTubePlayerSeekBarListener() {
            @Override
            public void seekTo(float v) {


                //youTubePlayer.seekTo(v);

                /*try {
                    youtube_player_preview.seekTo(v);
                }catch (NullPointerException e)
                {

                }*/


                /*HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put("videoSec", v);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(userIDCHATTINGWITH)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(youtubeVideoHash);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIDCHATTINGWITH)
                        .updateChildren(youtubeVideoHash);*/

                if (type.equals("live")) {
                    HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                    youtubeVideoHash.put("videoSec", v);
                    //youtubeVideoHash.put("youtubeID", videoID);

                    FirebaseDatabase.getInstance().getReference("Video")
                            .child(userIDCHATTINGWITH)
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(youtubeVideoHash);

                    FirebaseDatabase.getInstance().getReference("Video")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(userIDCHATTINGWITH)
                            .updateChildren(youtubeVideoHash);
                } else {
                    HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                    youtubeVideoHash.put("videoSec", v);

                    FirebaseDatabase.getInstance().getReference("GroupVideo")
                            .child(type)
                            .updateChildren(youtubeVideoHash);
                }


            }



        });

/*        youtube_player_seekbar.getSeekBar().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_MOVE){
                   // Toast.makeText(context,"Duration: "+youtube_player_seekbar.getVideoCurrentTimeTextView().getText(),Toast.LENGTH_SHORT).show();
                    //seekbar_time.setText(""+youtube_player_seekbar.getVideoCurrentTimeTextView().getText());
                    //seekbar_time.setText(""+youtube_player_seekbar.getVideoCurrentTimeTextView().getText());
                    try {
                        *//*if(youtube_player_preview!=null) {
                            Float seekTime = Float.parseFloat(youtube_player_seekbar.getVideoCurrentTimeTextView().getText().toString().replace(":", "."));
                            youtube_player_preview.pause();
                            youtube_player_preview.seekTo(seekTime);
                            youtube_player_preview.play();
                           // youtube_player_preview.pause();


                            youtube_player_preview.loadVideo(videoID,seekTime);
                            youtube_player_preview.play();
                        }*//*
                        Float seekTime = Float.parseFloat(youtube_player_seekbar.getVideoCurrentTimeTextView().getText().toString().replace(":", "."));

                        youTubePlayer.loadVideo(videoID,seekTime);
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(context,"Error preview: "+e.toString(),Toast.LENGTH_SHORT).show();
                    }
                    return false;
                }

                return false;
            }
        });*/


    }


    public void setVideo_title(String videoTitle) {
        video_title.setText(videoTitle);
    }


    public void setBufferingProgress(int visible) {
        buffering_progress.setVisibility(visible);
    }

    public void setPlayPauseBtn(int visible) {
        play_pause_btn.setVisibility(visible);
    }

    public void autoUpdateControlView() {
        play_pause_btn.animate().setDuration(300).alpha(0.0f);
        youtube_player_seekbar.animate().setDuration(300).alpha(0.0f);
        video_title.animate().setDuration(300).alpha(0.0f);
        text_back.animate().setDuration(300).alpha(0.0f);
        text_back_two.animate().setDuration(300).alpha(0.0f);
        seekVisible = false;
    }


    public void updateControllView() {
        if (!seekVisible) {
            play_pause_btn.animate().setDuration(300).alpha(1.0f);
            youtube_player_seekbar.animate().setDuration(300).alpha(1.0f);
            video_title.animate().setDuration(300).alpha(1.0f);
            text_back.animate().setDuration(300).alpha(1.0f);
            text_back_two.animate().setDuration(300).alpha(1.0f);
            seekVisible = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (playing) {
                        play_pause_btn.animate().setDuration(300).alpha(0.0f);
                        youtube_player_seekbar.animate().setDuration(300).alpha(0.0f);
                        video_title.animate().setDuration(300).alpha(0.0f);
                        text_back.animate().setDuration(300).alpha(0.0f);
                        text_back_two.animate().setDuration(300).alpha(0.0f);
                        seekVisible = false;
                    }
                }
            }, 2000);
        } else {
            play_pause_btn.animate().setDuration(300).alpha(0.0f);
            youtube_player_seekbar.animate().setDuration(300).alpha(0.0f);
            video_title.animate().setDuration(300).alpha(0.0f);
            text_back.animate().setDuration(300).alpha(0.0f);
            text_back_two.animate().setDuration(300).alpha(0.0f);
            seekVisible = false;
        }
    }

    public void setYoutube_player_seekbarVisibility(Boolean isVisible) {
        if (isVisible)
            youtube_player_seekbar.setVisibility(View.VISIBLE);
        else
            youtube_player_seekbar.setVisibility(View.GONE);
    }

    @Override
    public void onApiChange(YouTubePlayer youTubePlayer) {

    }

    @Override
    public void onCurrentSecond(YouTubePlayer youTubePlayer, float v) {

    }

    @Override
    public void onError(YouTubePlayer youTubePlayer, PlayerConstants.PlayerError playerError) {

    }

    @Override
    public void onPlaybackQualityChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackQuality playbackQuality) {

    }

    @Override
    public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {

    }

    @Override
    public void onReady(YouTubePlayer youTubePlayer) {

    }

    @Override
    public void onStateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlayerState playerState) {


    }

    @Override
    public void onVideoDuration(YouTubePlayer youTubePlayer, float v) {

    }

    @Override
    public void onVideoId(YouTubePlayer youTubePlayer, String s) {

    }

    @Override
    public void onVideoLoadedFraction(YouTubePlayer youTubePlayer, float v) {

    }
}