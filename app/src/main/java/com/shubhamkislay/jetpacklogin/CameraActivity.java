package com.shubhamkislay.jetpacklogin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class CameraActivity extends AppCompatActivity {

    public static String userChattingWithId;
    public static String usernameChattingWith;
    public static String userphotoUrl;
    public static String currentUserPhoto;
    public static String currentUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent  = getIntent();

        userChattingWithId = intent.getStringExtra("userChattingWithId");
        usernameChattingWith = intent.getStringExtra("username");
        userphotoUrl = intent.getStringExtra("userphoto");
        currentUserPhoto = intent.getStringExtra("currentUserPhoto");
        currentUsername = intent.getStringExtra("currentUsername");



        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

}
