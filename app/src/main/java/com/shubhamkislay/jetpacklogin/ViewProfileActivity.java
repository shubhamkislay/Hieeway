package com.shubhamkislay.jetpacklogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shubhamkislay.jetpacklogin.Model.Friend;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.HashMap;

public class ViewProfileActivity extends AppCompatActivity {


    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    ImageView profile_pic_background, center_dp;
    TextView username, name, feeling_icon, feeling_txt, bio_txt, emoji_icon;
    String feelingEmoji;
    RelativeLayout emoji_holder_layout;
    RelativeLayout ring_blinker_layout;
    String friendStatus;
    String usernameText;
    String photourl;
    String currentUsername;
    Button friend_btn_cancel;
    String feeling_text_label;

    String userId;
    Button friend_btn, start_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);

        username = findViewById(R.id.username);
        profile_pic_background = findViewById(R.id.profile_pic_background);
        center_dp = findViewById(R.id.center_dp);
        name = findViewById(R.id.name);
        feeling_icon = findViewById(R.id.feeling_icon);
        bio_txt = findViewById(R.id.bio_txt);
        emoji_holder_layout = findViewById(R.id.emoji_holder_layout);
        ring_blinker_layout = findViewById(R.id.ring_blinker_layout);
        emoji_icon = findViewById(R.id.emoji_icon);
        feeling_txt = findViewById(R.id.feeling_txt);

        friend_btn = findViewById(R.id.friend_btn);
        friend_btn_cancel = findViewById(R.id.friend_btn_cancel);
        start_chat = findViewById(R.id.start_chat);


        name.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feeling_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        username.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));
        bio_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        friend_btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        friend_btn_cancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        start_chat.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));


        Intent intent = getIntent();

        username.setText(intent.getStringExtra("username"));
        name.setText(intent.getStringExtra("name"));

        photourl = intent.getStringExtra("photourl");
        userId = intent.getStringExtra("userId");
        currentUsername = intent.getStringExtra("currentUsername");

        usernameText = intent.getStringExtra("username");


        Glide.with(this).load(photourl.replace("s96-c", "s384-c")).into(center_dp);
        Glide.with(this).load(photourl.replace("s96-c", "s384-c")).into(profile_pic_background);


        checkUserData(intent);


        friend_btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelRequest();
            }
        });

        start_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewProfileActivity.this, VerticalPageActivity.class);
                intent.putExtra("username", usernameText);
                intent.putExtra("userid", userId);
                intent.putExtra("photo", photourl);
                intent.putExtra("live", "no");
                //   intent.putExtra("otherUserPublicKey",chatStamp.getPublicKey());

                startActivity(intent);
                finish();
            }
        });


    }

    private void checkUserData(Intent intent) {
        // try {

        feeling_txt.setText(intent.getStringExtra("feeling_txt"));
        bio_txt.setText(intent.getStringExtra("bio_txt"));
        friendStatus = intent.getStringExtra("friendStatus");
        feelingEmoji = intent.getStringExtra("feelingEmoji");
        feeling_text_label = intent.getStringExtra("feeling_txt");
        try {
            if (feeling_text_label.length() < 1)
                feeling_txt.setText(HAPPY);
        } catch (Exception e) {
            feeling_text_label = "happy";
            feeling_txt.setText(HAPPY);
        }
/*        }catch (Exception e)
        {
            //
            //   Toast.makeText(ViewProfileActivity.this,"Data unavailable",Toast.LENGTH_SHORT).show();
            //friendStatus = "friends";
            loadNullValue(userId,FirebaseAuth.getInstance().getCurrentUser().getUid());
            return;
        }
        try {*/
        if (friendStatus.equals("friends")
                || friendStatus.equals("notFriend")
                || friendStatus.equals("got")
                || friendStatus.equals("requested"))
            setUserData();
        // friendStatus.length();


    }

    private void loadNullValue(final String userId, String uid) {

/*        DatabaseReference findFriendShipReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(uid)
                .child(userId);

        findFriendShipReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    Friend friend = dataSnapshot.getValue(Friend.class);
                    try {
                        friendStatus = friend.getStatus();
                    }catch (NullPointerException e)
                    {
                        friendStatus = "notFriend";
                    }
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        DatabaseReference findUserDataReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userId);

        findUserDataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    try {
                        feeling_txt.setText(user.getFeeling());
                    } catch (Exception e) {
                        feeling_txt.setText(HAPPY);
                    }

                    bio_txt.setText(user.getBio());

                    try {
                        feelingEmoji = user.getFeelingIcon();
                    } catch (Exception e) {
                        feelingEmoji = "default";
                    }

                    try {
                        feeling_text_label = user.getFeeling();
                    } catch (Exception e) {
                        feeling_text_label = HAPPY;
                    }

                    setUserData();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void setUserData() {
        // try {
        if (friendStatus.equals("friends")) {
            feeling_txt.setVisibility(View.VISIBLE);
            emoji_holder_layout.setVisibility(View.VISIBLE);
            friend_btn.setText("Remove Friend");

            startBlinking();


            try {
                if (feelingEmoji.equals("default")) {
                    emoji_icon.setVisibility(View.INVISIBLE);
                    feeling_icon.setVisibility(View.VISIBLE);
                    setFeelingEmoji(feeling_text_label);
                } else {
                    emoji_icon.setVisibility(View.VISIBLE);
                    feeling_icon.setVisibility(View.INVISIBLE);
                    emoji_icon.setText(feelingEmoji);

                }
            } catch (Exception e) {
                loadNullValue(userId, FirebaseAuth.getInstance().getCurrentUser().getUid());
                feelingEmoji = "default";
                emoji_icon.setVisibility(View.INVISIBLE);
                feeling_icon.setVisibility(View.VISIBLE);
                setFeelingEmoji(HAPPY);

            }
        } else {
            if (friendStatus.equals("requested")) {
                friend_btn.setText("Cancel Request");
            } else if (friendStatus.equals("got")) {
                friend_btn.setText("Accept");
                friend_btn_cancel.setVisibility(View.VISIBLE);
            } else if (friendStatus.equals("notFriend")) {
                friend_btn.setText("Add Friend");
                friend_btn.setTextColor(getResources().getColor(R.color.colorBlack));
                friend_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));
            }
        }

        friend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (friendStatus) {
                    case "requested":
                        cancelRequest();
                        break;

                    case "got":
                        acceptRequest();
                        friend_btn_cancel.setVisibility(View.GONE);
                        start_chat.setVisibility(View.VISIBLE);
                        break;

                    case "friends":
                        removeFriend();
                        break;

                    case "notFriend":
                        addFriend();
                        break;
                }

            }
        });/*
        }catch (Exception e)
        {
            Toast.makeText(ViewProfileActivity.this,"Data is not present",Toast.LENGTH_SHORT).show();
            loadNullValue(userId,FirebaseAuth.getInstance().getCurrentUser().getUid());
            }*/


    }

    private void addFriend() {

        friend_btn.setText("Cancel Request");
        friend_btn.setTextColor(getResources().getColor(R.color.colorWhite));
        friend_btn.setBackgroundTintList(null);

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("friendId", userId);
        hashMap.put("status", "requested");
        hashMap.put("username", usernameText);

        requestSentReference.updateChildren(hashMap);


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> receiveHashMap = new HashMap<>();


        receiveHashMap.put("friendId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        receiveHashMap.put("status", "got");
        receiveHashMap.put("username", currentUsername);

        requestReceiveReference.updateChildren(receiveHashMap);

        friendStatus = "requested";
    }

    private void removeFriend() {
        feeling_txt.setVisibility(View.INVISIBLE);
        emoji_holder_layout.setVisibility(View.INVISIBLE);
        start_chat.setVisibility(View.GONE);

        FirebaseMessaging.getInstance().unsubscribeFromTopic(usernameText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(ViewProfileActivity.this, "unsubscribed to " + usernameText, Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancelRequest();


    }

    private void acceptRequest() {

        feeling_txt.setVisibility(View.VISIBLE);
        emoji_holder_layout.setVisibility(View.VISIBLE);
        friend_btn.setText("Remove Friend");
        friend_btn.setTextColor(getResources().getColor(R.color.colorWhite));
        friend_btn.setBackgroundTintList(null);


        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("friendId", userId);
        hashMap.put("status", "friends");
        hashMap.put("username", usernameText);

        requestSentReference.updateChildren(hashMap);

        FirebaseMessaging.getInstance().subscribeToTopic(usernameText).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ViewProfileActivity.this, "Friend request accepted", Toast.LENGTH_SHORT).show();
                }
            }
        });


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String, Object> receiveHashMap = new HashMap<>();


        receiveHashMap.put("friendId", FirebaseAuth.getInstance().getCurrentUser().getUid());
        receiveHashMap.put("status", "friends");
        receiveHashMap.put("username", currentUsername);

        requestReceiveReference.updateChildren(receiveHashMap);

        friendStatus = "friends";


        try {
            if (feelingEmoji.equals("default")) {
                emoji_icon.setVisibility(View.INVISIBLE);
                feeling_icon.setVisibility(View.VISIBLE);
                setFeelingEmoji(feeling_text_label);
            } else {
                emoji_icon.setVisibility(View.VISIBLE);
                feeling_icon.setVisibility(View.INVISIBLE);
                emoji_icon.setText(feelingEmoji);
            }
        } catch (Exception e) {
            emoji_icon.setVisibility(View.INVISIBLE);
            feeling_icon.setVisibility(View.VISIBLE);
            setFeelingEmoji(HAPPY);
        }
        startBlinking();
    }

    private void cancelRequest() {

        friend_btn.setText("Add Friend");
        friend_btn.setTextColor(getResources().getColor(R.color.colorBlack));
        friend_btn.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorWhite)));

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);


        requestSentReference.removeValue();


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        requestReceiveReference.removeValue();

        friendStatus = "notFriend";
    }

    private void setFeelingEmoji(String feeling) {

        switch (feeling) {
            case HAPPY:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_happy));
                feeling_txt.setText(HAPPY);
                break;
            case SAD:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_sad));
                feeling_txt.setText(SAD);
                break;
            case BORED:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_bored));
                feeling_txt.setText(BORED);
                break;
            case ANGRY:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_angry));
                feeling_txt.setText(ANGRY);
                break;
            case EXCITED:
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_excited));
                feeling_txt.setText(EXCITED);
                break;
            case CONFUSED:
                feeling_icon.setVisibility(View.VISIBLE);
                feeling_icon.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_confused));
                feeling_txt.setText(CONFUSED);
                break;
        }
    }

    private void startBlinking() {
/*        ring_blinker_layout.animate().alpha(0.0f).setDuration(950);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ring_blinker_layout.animate().alpha(1.0f).setDuration(950);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ring_blinker_layout.animate().alpha(0.0f).setDuration(950);
                        startBlinking();
                    }
                }, 1000);
            }
        }, 1000);*/
    }

}
