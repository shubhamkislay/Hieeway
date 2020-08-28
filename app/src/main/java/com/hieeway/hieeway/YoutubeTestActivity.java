package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.hieeway.hieeway.Fragments.YoutubeFragment;

public class YoutubeTestActivity extends AppCompatActivity {

    FrameLayout youtube_fragement_frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_test);


        youtube_fragement_frame = findViewById(R.id.youtube_fragement_frame);

        YoutubeFragment youtubeFragment = new YoutubeFragment();

        FragmentManager menuFragmentManager = getSupportFragmentManager();

        menuFragmentManager.beginTransaction()
                .replace(R.id.youtube_fragement_frame, youtubeFragment).commit();
    }
}
