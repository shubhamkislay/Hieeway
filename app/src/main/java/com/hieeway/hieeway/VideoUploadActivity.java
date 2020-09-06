package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import java.io.FileOutputStream;
import java.io.InputStream;

public class VideoUploadActivity extends AppCompatActivity {

    Uri videoUri, encryptedUri;
    VideoView videoView;
    String userChattingWithId, usernameChattingWith, currentUserPhoto, currentUsername, currentUserPublicKeyID, otherUserPublicKeyID, userphotoUrl;
    Button send_video_btn;
    String mKey;
    private InputStream inputStream;
    private FileOutputStream fos;
    private int read;
    private boolean readyToUpload = false;
    private boolean encrpted = false;
    private String activePhoto;
    private String currentUserActivePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);


        videoView = findViewById(R.id.video_view);

        Intent intent = getIntent();
        videoUri = Uri.parse(intent.getStringExtra("videoUri"));
        userChattingWithId = intent.getStringExtra("userChattingWithId");
        currentUserPhoto = intent.getStringExtra("currentUserPhoto");
        currentUsername = intent.getStringExtra("currentUsername");
        userphotoUrl = intent.getStringExtra("userphotoUrl");
        currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
        otherUserPublicKeyID = intent.getStringExtra("otherUserPublicKeyID");
        activePhoto = intent.getStringExtra("activePhoto");
        currentUserActivePhoto = intent.getStringExtra("activePhoto");
        usernameChattingWith = intent.getStringExtra("usernameChattingWith");
        mKey = intent.getStringExtra("mKey");
        send_video_btn = findViewById(R.id.send_video_btn);

        // MediaController controller = new MediaController(this);

        videoView.setVideoURI(videoUri);
        // videoView.setMediaController(controller);
        videoView.start();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                videoView.start();

            }
        });

        send_video_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendVideo();

            }
        });
        // encrpytVideo();

    }

    private void sendVideo() {
        Intent intent1 = new Intent(this, SendMediaService.class);
        intent1.putExtra("userChattingWithId", userChattingWithId);
        intent1.putExtra("imageUri", videoUri.toString());
        //intent1.putExtra("encrptedVide", videoUri.toString());
        
        intent1.putExtra("usernameChattingWith", usernameChattingWith);
        intent1.putExtra("userphotoUrl", userphotoUrl);
        intent1.putExtra("currentUsername", currentUsername);
        intent1.putExtra("currentUserPhoto", currentUserPhoto);
        intent1.putExtra("activePhoto", activePhoto);
        intent1.putExtra("currentUserActivePhoto", currentUserActivePhoto);
        intent1.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
        intent1.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
        intent1.putExtra("mKey", mKey);
        intent1.putExtra("type", "video");


        startService(intent1);

        finish();

    }


}
