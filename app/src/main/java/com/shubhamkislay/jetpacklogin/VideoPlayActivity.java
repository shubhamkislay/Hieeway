package com.shubhamkislay.jetpacklogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class VideoPlayActivity extends AppCompatActivity {

    VideoView videoView;
    RelativeLayout progress_layout;
    private Uri videoUri;
    private String userIdChattingWith, sender, mKey, videoUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoView = findViewById(R.id.video_view);

        Intent intent = getIntent();

        progress_layout = findViewById(R.id.progress_layout);


        videoUrl = intent.getStringExtra("videoUrl");
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        sender = intent.getStringExtra("sender");
        mKey = intent.getStringExtra("mKey");


        videoUri = Uri.parse(videoUrl);

        videoView.setVideoURI(videoUri);


        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progress_layout.setVisibility(View.GONE);
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
        });
    }

    private void deleteVideoMessage() {
        FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sender)
                .child(mKey)
                .removeValue();


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("seen", "seen");
        FirebaseDatabase.getInstance().getReference("Messages")
                .child(sender)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mKey)
                .updateChildren(hashMap);

        finish();
    }
}
