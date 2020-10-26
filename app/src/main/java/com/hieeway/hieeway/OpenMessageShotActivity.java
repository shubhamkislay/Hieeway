package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class OpenMessageShotActivity extends AppCompatActivity {


    TextView message;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    private SharedPreferences sharedPreferences;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_message_shot);

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        Intent intent = getIntent();
        message = findViewById(R.id.message);
        message.setText("" + intent.getStringExtra("message"));
        deleteShot(intent.getStringExtra("postKey"));
    }

    private void deleteShot(String postKey) {
        FirebaseDatabase.getInstance().getReference("Post")
                .child(userID)
                .child(postKey).removeValue();
    }
}