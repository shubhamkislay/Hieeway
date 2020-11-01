package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class OpenMessageShotActivity extends AppCompatActivity {


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_open_message_shot);
        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        Intent intent = getIntent();
        message = findViewById(R.id.message);
        determinateBar = findViewById(R.id.determinateBar);
        blast = findViewById(R.id.blast);
        messageText = intent.getStringExtra("message");
        postKey = intent.getStringExtra("postKey");
        otherUserId = intent.getStringExtra("otherUserId");

        maxValue = messageText.length() * 75 + 1250;
        determinateBar.setMax(maxValue);
        updateProgress();

        message.setText("" + messageText);
        deleteShot(postKey);

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
}