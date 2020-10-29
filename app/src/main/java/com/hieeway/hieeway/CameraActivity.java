package com.hieeway.hieeway;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class CameraActivity extends AppCompatActivity {

    public static String userChattingWithId;
    public static String usernameChattingWith;
    public static String userphotoUrl;
    public static String currentUserPhoto;
    public static String currentUserActivePhoto;
    public static String currentUsername;
    public static String otherUserPublicKeyID;
    public static String currentUserPublicKeyID;
    public static String otherUserPublicKey;
    public static String currentUserPublicKey;
    public static String activePhoto;
    public static String currentUserID;
    public static String requestType;
    public static String groupID;
    public static String groupName;
    public static String icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        Intent intent = getIntent();
        userStatusOnDiconnect();


        requestType = intent.getStringExtra("requestType");
        if (requestType.equals("shot")) {
            currentUserID = intent.getStringExtra("currentUserID");
            currentUsername = intent.getStringExtra("currentUsername");
        } else if (requestType.equals("message")) {
            userChattingWithId = intent.getStringExtra("userChattingWithId");
            usernameChattingWith = intent.getStringExtra("username");
            userphotoUrl = intent.getStringExtra("userphoto");
            currentUserPhoto = intent.getStringExtra("currentUserPhoto");
            currentUserActivePhoto = intent.getStringExtra("currentUserActivePhoto");
            currentUsername = intent.getStringExtra("currentUsername");
            otherUserPublicKeyID = intent.getStringExtra("otherUserPublicKeyID");
            activePhoto = intent.getStringExtra("activePhoto");
            currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
            otherUserPublicKey = intent.getStringExtra("otherUserPublicKey");
            currentUserPublicKey = intent.getStringExtra("currentUserPublicKey");
        } else if (requestType.equals("group")) {


            groupID = intent.getStringExtra("groupID");
            groupName = intent.getStringExtra("groupName");
            icon = intent.getStringExtra("icon");

            currentUserID = intent.getStringExtra("currentUserID");
            currentUsername = intent.getStringExtra("currentUsername");
            currentUserPhoto = intent.getStringExtra("currentUserPhoto");
        }


        if (null == savedInstanceState) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }

}
