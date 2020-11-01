package com.hieeway.hieeway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PhotoViewActivity extends AppCompatActivity {

    ProgressBar determinateBar;
    ImageView blast;
    String imageUri;
    CustomImageView photo;
    int maxValue = 7000;
    boolean paused = false;
    ProgressBar loading_video;
    String requestType;
    String postKey;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USERNAME = "username";
    public static final String USER_ID = "userid";
    private SharedPreferences sharedPreferences;
    private String userID;
    private String currentUsername;
    private String otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

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


        Intent intent = getIntent();

        imageUri = intent.getStringExtra("path");
        requestType = intent.getStringExtra("requestType");
        postKey = intent.getStringExtra("postKey");
        otherUserId = intent.getStringExtra("otherUserId");


        determinateBar = findViewById(R.id.determinateBar);
        blast = findViewById(R.id.blast);
        photo = findViewById(R.id.photo);
        loading_video = findViewById(R.id.loading_video);

        if (!requestType.equals("post"))
            blast.setVisibility(View.INVISIBLE);

        Glide.with(this)
                .load(imageUri)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        loading_video.setVisibility(View.GONE);
                        determinateBar.setMax(maxValue);
                        updateProgress();

                        if (requestType.equals("shot")) {

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
                        }

                        return false;
                    }
                }).into(photo);


        photo.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        paused = true;
                        //Toast.makeText(ExoPlayerActivity.this,"Down",Toast.LENGTH_SHORT).show();
                    }
                        /*else if (event.getAction() == MotionEvent.ACTION_UP)
                            player.play();*/

                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        paused = false;
                        //Toast.makeText(ExoPlayerActivity.this,"UP",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    //
                }
                return true;
            }
        });


    }

    private void updateProgress() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {

                        if (!paused) {
                            if (maxValue <= 0) {
                                blast.animate().scaleX(500.0f).scaleY(500.0f).setDuration(750);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 750);
                            } else {
                                maxValue = maxValue - 100;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    determinateBar.setProgress((int) maxValue, true);
                                } else
                                    determinateBar.setProgress((int) maxValue);

                                updateProgress();
                            }
                        } else
                            updateProgress();
                    } catch (Exception e) {
                        //
                    }
                }
            }, 100);
        } catch (Exception e) {
            //
        }
    }


    /*@Override
    public void onBackPressed() {

        blast.animate().scaleX(500.0f).scaleY(500.0f).setDuration(350);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 350);
    }*/
}