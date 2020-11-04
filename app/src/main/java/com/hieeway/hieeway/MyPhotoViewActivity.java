package com.hieeway.hieeway;

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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.SeenByAdapter;
import com.hieeway.hieeway.Model.SeenByUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyPhotoViewActivity extends AppCompatActivity {

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
    private MotionLayout motion_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_photo_view);

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
        motion_layout = findViewById(R.id.motion_layout);

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

        motion_layout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {

                if (i == R.layout.activity_my_photo_view_end) {
                    paused = true;
                } else
                    paused = false;

            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });

        addSeenByList();

    }

    private void addSeenByList() {


        RecyclerView seen_recyclerview;
        List<SeenByUser> stringList;
        SharedPreferences sharedPreferences;
        String currentUser;

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        currentUser = sharedPreferences.getString("userid", "");
        stringList = new ArrayList<>();
        seen_recyclerview = findViewById(R.id.seen_recyclerview);
        TextView seen_by_txt = findViewById(R.id.seen_by_txt);

        seen_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        seen_recyclerview.setHasFixedSize(true);

        FirebaseDatabase.getInstance().getReference("SeenBy")
                .child(currentUser)
                .child(postKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            stringList.clear();
                            int count = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                SeenByUser userName = snapshot.getValue(SeenByUser.class);
                                stringList.add(userName);
                                ++count;
                            }

                            seen_by_txt.setText("Seen by " + count);
                            SeenByAdapter seenByAdapter = new SeenByAdapter(MyPhotoViewActivity.this, stringList);
                            seen_recyclerview.setAdapter(seenByAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

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