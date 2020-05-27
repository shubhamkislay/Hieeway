package com.hieeway.hieeway;


import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener;

import java.util.HashMap;

import static com.hieeway.hieeway.VerticalPageActivity.userIDCHATTINGWITH;

public class CustomUiController {

    private YouTubePlayerSeekBar youtube_player_seekbar;
    private boolean isPlaying = false;

    public CustomUiController(View customPlayerUI, final YouTubePlayer youTubePlayer, final Context context) {


        youtube_player_seekbar = customPlayerUI.findViewById(R.id.youtube_player_seekbar);


        youTubePlayer.addListener(youtube_player_seekbar);


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

    public void setYoutube_player_seekbarVisibility(Boolean isVisible) {
        if (isVisible)
            youtube_player_seekbar.setVisibility(View.VISIBLE);
        else
            youtube_player_seekbar.setVisibility(View.GONE);
    }
}