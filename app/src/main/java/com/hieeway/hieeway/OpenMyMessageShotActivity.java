package com.hieeway.hieeway;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class OpenMyMessageShotActivity extends AppCompatActivity {


    TextView message;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    private SharedPreferences sharedPreferences;
    private String userID;
    ProgressBar determinateBar;
    ImageView blast;
    private String messageText;
    String postKey;
    private int maxValue;
    private boolean paused = false;
    private String currentUsername;
    private String otherUserId;
    private MotionLayout motion_layout_parent;
    private DatabaseReference seenByReference;
    private ValueEventListener seenByValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_my_message_shot);
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        Intent intent = getIntent();
        message = findViewById(R.id.message);
        determinateBar = findViewById(R.id.determinateBar);
        blast = findViewById(R.id.blast);
        motion_layout_parent = findViewById(R.id.motion_layout_parent);

        messageText = intent.getStringExtra("message");
        postKey = intent.getStringExtra("postKey");
        otherUserId = intent.getStringExtra("otherUserId");


        maxValue = messageText.length() * 75 + 1250;
        determinateBar.setMax(maxValue);
        updateProgress();

        message.setText("" + messageText);
        //deleteShot(postKey);

        message.setOnTouchListener(new View.OnTouchListener() {
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


        motion_layout_parent.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int i, int i1) {

            }

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int i, int i1, float v) {

            }

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int i) {

                if (i == R.layout.activity_open_my_message_shot_end) {
                    paused = true;
                } else {
                    paused = false;
                }


            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int i, boolean b, float v) {

            }
        });


        addSeenByList();


    }

    private void deleteShot(String postKey) {

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
        seenByReference = FirebaseDatabase.getInstance().getReference("SeenBy")
                .child(currentUser)
                .child(postKey);

        seenByValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    stringList.clear();
                    int count = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        SeenByUser userName = snapshot.getValue(SeenByUser.class);
                        stringList.add(userName);
                        count++;
                    }
                    seen_by_txt.setText("Seen by " + count);
                    SeenByAdapter seenByAdapter = new SeenByAdapter(OpenMyMessageShotActivity.this, stringList);
                    seen_recyclerview.setAdapter(seenByAdapter);
                } else {
                    seen_by_txt.setText("Seen by " + 0);
                    SeenByAdapter seenByAdapter = new SeenByAdapter(OpenMyMessageShotActivity.this, stringList);
                    seen_recyclerview.setAdapter(seenByAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        seenByReference.addValueEventListener(seenByValueEventListener);

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


    @Override
    protected void onStop() {
        super.onStop();
        try {
            seenByReference.removeEventListener(seenByValueEventListener);
        } catch (Exception e) {
            //
        }
    }
}