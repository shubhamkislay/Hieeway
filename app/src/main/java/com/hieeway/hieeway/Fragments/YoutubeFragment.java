package com.hieeway.hieeway.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.YouTubeConfig;

import static com.hieeway.hieeway.R.id.youtube_player_view;


/**
 * A simple {@link Fragment} subclass.
 */
public class YoutubeFragment extends Fragment {

    YouTubePlayer mYouTubePlayer = null;
    Fragment youtube_fragment;
    private FrameLayout youtube_player_view;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private Button play_btn;
    private boolean initialized = false;

    public YoutubeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_youtube, container, false);

        play_btn = view.findViewById(R.id.play_btn);


        youtube_player_view = view.findViewById(R.id.youtube_player_view);


        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                mYouTubePlayer = youTubePlayer;
                mYouTubePlayer.loadVideo("E07s5ZYygMg");
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // youtube_player_view.initialize(YouTubeConfig.getApiKey(),onInitializedListener);

                if (mYouTubePlayer != null)
                    mYouTubePlayer.play();

            }
        });


        /*YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_player_view, youTubePlayerFragment);
        transaction.commit();
*//*

        youTubePlayerFragment.initialize(YouTubeConfig.getApiKey(), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

                // if (!wasRestored) {


                mYouTubePlayer = player;
                mYouTubePlayer.setFullscreen(false);
                //mYouTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
                //mYouTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);

                mYouTubePlayer.setShowFullscreenButton(false);
                mYouTubePlayer.loadVideo("E07s5ZYygMg");
                initialized = true;

                setPlayBackListener();

                //  }

            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                // TODO Auto-generated method stub

            }
        });
*/

        return view;
    }

    private void setPlayBackListener() {
        mYouTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {

            }

            @Override
            public void onPaused() {

            }

            @Override
            public void onStopped() {

                play_btn.setText("Stopped");
            }

            @Override
            public void onBuffering(boolean b) {

                if (b)
                    play_btn.setText("Buffering");
                else
                    play_btn.setText("Playing");
            }

            @Override
            public void onSeekTo(int i) {

                Toast.makeText(getContext(), "Seeked to: " + i, Toast.LENGTH_SHORT).show();


                // mYouTubePlayer.seekRelativeMillis(-200);
            }
        });
    }

}
