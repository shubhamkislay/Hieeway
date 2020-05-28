package com.hieeway.hieeway;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener;

import java.util.HashMap;

import static com.hieeway.hieeway.VerticalPageActivity.userIDCHATTINGWITH;

public class CustomUiController implements YouTubePlayerListener {

    private final FadeViewHelper fadeViewHelper;
    private YouTubePlayerSeekBar youtube_player_seekbar;
    private boolean isPlaying = false;
    FrameLayout frameLayout;
    RelativeLayout toggle_view;
    RelativeLayout play_pause_btn;
    YouTubePlayer youTubePlayer;
    Context context;
    private boolean seekVisible = true;
    private boolean playing = true;


    public CustomUiController(View customPlayerUI, final YouTubePlayer youTubePlayer, final Context context) {


        this.youTubePlayer = youTubePlayer;
        this.context = context;
        youtube_player_seekbar = customPlayerUI.findViewById(R.id.youtube_player_seekbar);

        frameLayout = customPlayerUI.findViewById(R.id.view_container);

        fadeViewHelper = new FadeViewHelper(frameLayout);

        toggle_view = customPlayerUI.findViewById(R.id.toggle_view);

        play_pause_btn = customPlayerUI.findViewById(R.id.play_pause_btn);


        customPlayerUI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fadeViewHelper.toggleVisibility();
                youtube_player_seekbar.setVisibility(View.VISIBLE);
                youtube_player_seekbar.setAlpha(1.0f);
            }
        });

        toggle_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //fadeViewHelper.toggleVisibility();
                updateControllView();
                //youtube_player_seekbar.setAlpha(1.0f);
            }
        });

        play_pause_btn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if (playing) {
                    youTubePlayer.pause();
                    play_pause_btn.setBackground(context.getResources().getDrawable(R.drawable.ic_play_circle_filled_black_24dp));
                    playing = false;
                } else {
                    youTubePlayer.play();
                    play_pause_btn.setBackground(context.getResources().getDrawable(R.drawable.ic_pause_circle_filled_black_24dp));
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


                //  youTubePlayer.seekTo(v);


                HashMap<String, Object> youtubeVideoHash = new HashMap<>();
                youtubeVideoHash.put("videoSec", v);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(userIDCHATTINGWITH)
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .updateChildren(youtubeVideoHash);

                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIDCHATTINGWITH)
                        .updateChildren(youtubeVideoHash);

            }



        });


    }


    public void autoUpdateControlView() {
        play_pause_btn.animate().setDuration(300).alpha(0.0f);
        youtube_player_seekbar.animate().setDuration(300).alpha(0.0f);
        seekVisible = false;
    }


    public void updateControllView() {
        if (!seekVisible) {
            play_pause_btn.animate().setDuration(300).alpha(1.0f);
            youtube_player_seekbar.animate().setDuration(300).alpha(1.0f);
            seekVisible = true;
        } else {
            play_pause_btn.animate().setDuration(300).alpha(0.0f);
            youtube_player_seekbar.animate().setDuration(300).alpha(0.0f);
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