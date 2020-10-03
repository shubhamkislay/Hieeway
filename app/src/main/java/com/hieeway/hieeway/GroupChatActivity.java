package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.GroupMessageAdapter;
import com.hieeway.hieeway.Interface.ScrollRecyclerViewListener;
import com.hieeway.hieeway.Model.GroupMessage;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity implements ScrollRecyclerViewListener {

    private RelativeLayout bin;
    private ImageView disablerecord_button;
    private MediaRecorder recorder = null;
    private RelativeLayout equlizer;
    private RelativeLayout equi_one;
    private RelativeLayout equi_two;
    private RelativeLayout equi_three;
    private RelativeLayout equi_four;
    private RelativeLayout equi_five;
    private RecordView recordView;
    private RecordButton recordButton;
    private Button camera_background;
    private EditText message_box;
    private Button camera;
    public static final String SHARED_PREFS = "sharedPrefs";
    private boolean isDisablerecord_button = false;
    private Button send_button;
    private String groupID, groupNameTxt, iconUrl;
    private CircleImageView icon;
    private TextView groupName;
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String USERNAME = "username";
    private RecyclerView message_recycler_View;
    private ValueEventListener valueEventListener;
    private List<GroupMessage> groupMessageList;
    private DatabaseReference groupMessageRef;
    private GroupMessageAdapter groupMessageAdapter;
    private String userID;
    private String userPhoto;
    private String currentUsername;
    private boolean updated = false;
    private Boolean scrollRecyclerView = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        camera_background = findViewById(R.id.camera_background);
        message_box = findViewById(R.id.message_box);
        send_button = findViewById(R.id.send_button);
        disablerecord_button = (ImageView) findViewById(R.id.disablerecord_button);
        message_recycler_View = (RecyclerView) findViewById(R.id.message_recycler_View);

        equlizer = findViewById(R.id.equlizer);
        equi_one = findViewById(R.id.equi_one);
        equi_two = findViewById(R.id.equi_two);
        equi_three = findViewById(R.id.equi_three);
        equi_four = findViewById(R.id.equi_four);
        equi_five = findViewById(R.id.equi_five);
        groupMessageList = new ArrayList<>();


        icon = findViewById(R.id.group_icon);
        groupName = findViewById(R.id.group_name);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        userPhoto = sharedPreferences.getString(PHOTO_URL, "default");
        currentUsername = sharedPreferences.getString(PHOTO_URL, "default");


        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupNameTxt = intent.getStringExtra("groupName");
        iconUrl = intent.getStringExtra("icon");

        groupMessageRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                .child(groupID);


        Glide.with(this).load(iconUrl).into(icon);
        groupName.setText(groupNameTxt);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);

        message_recycler_View.setLayoutManager(linearLayoutManager);
        message_recycler_View.scrollToPosition(groupMessageList.size() - 1);

        groupMessageList.clear();
        groupMessageAdapter = new GroupMessageAdapter(this, groupMessageList, userID, this);

        message_recycler_View.setAdapter(groupMessageAdapter);


        try {
            groupName.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        } catch (Exception e) {
            //
        }


        recordView.setSmallMicColor(Color.parseColor("#ffffff"));

        // recordView.setSlideToCancelText("TEXT");

        //disable Sounds
        recordView.setSoundEnabled(false);

        //prevent recording under one Second (it's false by default)
        recordView.setLessThanSecondAllowed(false);

        //set Custom sounds onRecord
        //you can pass 0 if you don't want to play sound in certain state
        recordView.setCustomSounds(R.raw.quiet_knock, R.raw.shootem, R.raw.record_error);

        //change slide To Cancel Text Color
        recordView.setSlideToCancelTextColor(Color.parseColor("#ffffff"));
        //change slide To Cancel Arrow Color
        recordView.setSlideToCancelArrowColor(Color.parseColor("#ffffff"));
        //change Counter Time (Chronometer) color
        recordView.setCounterTimeColor(getResources().getColor(R.color.colorPrimaryDark));

        camera = findViewById(R.id.camera);

        //recordButton.setListenForRecord(false);
        /*if (ContextCompat.checkSelfPermission(GroupChatActivity.this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            disablerecord_button.setVisibility(View.VISIBLE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.GONE);
            //recordButton.setEnabled(false);
        } else {
            disablerecord_button.setVisibility(View.GONE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.VISIBLE);
        }*/

        disablerecord_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(GroupChatActivity.this, Manifest.permission.RECORD_AUDIO)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestAudioPermisson();
                    Toast.makeText(GroupChatActivity.this, "Requesting Audio Permission", Toast.LENGTH_SHORT).show();
                }
            }
        });

        send_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        send_button.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    // viewHolder.user_photo.setAlpha(0.4f);

                    send_button.animate().scaleX(0.95f).scaleY(0.95f).setDuration(0);
                    send_button.animate().scaleX(0.85f).scaleY(0.85f).setDuration(0);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {

                    send_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);


                    //         viewHolder.user_photo.animate().alpha(1.0f).setDuration(50);

                    send_button.animate().scaleX(0.85f).scaleY(0.85f).setDuration(0);


                } else {
                    // viewHolder.user_photo.animate().setDuration(50).alpha(1.0f);

                    send_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);

                    send_button.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50);
                }

                return false;
            }

        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if (ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED ||
                        ContextCompat.checkSelfPermission(parentActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED)
                    requestAllPermissions();


                else
                {
                    Intent intent = new Intent(parentActivity, CameraActivity.class);

                    // ephemeralMessageViewModel.createChatListItem(usernameChattingWith, photo, currentUsername, currentUserPhoto);
                    if (currentUsername != null && currentUserPhoto != null && currentUserActivePhoto != null) {

                        intent.putExtra("userChattingWithId", userIdChattingWith);
                        intent.putExtra("userphoto", photo);
                        intent.putExtra("activePhoto", activePhoto);
                        intent.putExtra("username", usernameChattingWith);
                        intent.putExtra("currentUsername", currentUsername);
                        intent.putExtra("currentUserPhoto", currentUserPhoto);
                        intent.putExtra("currentUserActivePhoto", currentUserActivePhoto);
                        intent.putExtra("otherUserPublicKeyID", otherUserPublicKeyID);
                        intent.putExtra("currentUserPublicKeyID", currentUserPublicKeyID);
                        intent.putExtra("otherUserPublicKey", otherUserPublicKey);
                        intent.putExtra("currentUserPublicKey", currentUserPublicKey);



                        // intent.putExtra("userChattingWithId", currentUserPhoto);


                        startActivity(intent);
                    } else
                        Toast.makeText(parentActivity, "Getting user details", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                //requestAudioPermisson();
                // Toast.makeText(AudioRecorderActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                //  Log.d("RecordButton","RECORD BUTTON CLICKED");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                // Log.d("RecordView", "Basket Animation Finished");
                camera.setVisibility(View.VISIBLE);
                camera_background.setVisibility(View.VISIBLE);
                message_box.setVisibility(View.VISIBLE);

                //message_box_behind.setVisibility(View.VISIBLE);
            }
        });


        recordView.setSlideToCancelText("Slide to cancel");

        //IMPORTANT
        recordButton.setRecordView(recordView);


        populateMessages();

        message_recycler_View.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    message_recycler_View.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                message_recycler_View.smoothScrollToPosition(groupMessageList.size() - 1);
                            } catch (Exception e) {

                            }
                        }
                    }, 100);
                }
            }
        });

    }

    private void populateMessages() {


        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    int messages = 0;
                    groupMessageList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        GroupMessage groupMessage = dataSnapshot.getValue(GroupMessage.class);

                        //Toast.makeText(GroupChatActivity.this,"Added: " +groupMessage.getMessageText(),Toast.LENGTH_SHORT).show();
                        groupMessageList.add(groupMessage);
                        messages += 1;
                    }


                   // Toast.makeText(GroupChatActivity.this, "Total Messages: " + groupMessageList.size() + "\n Counted: " + messages, Toast.LENGTH_SHORT).show();
                    groupMessageAdapter.updateList(groupMessageList);
                    if (scrollRecyclerView)
                        message_recycler_View.scrollToPosition(groupMessageList.size() - 1);
                   /* groupMessageAdapter = new GroupMessageAdapter(GroupChatActivity.this,groupMessageList,userID);
                    message_recycler_View.setAdapter(groupMessageAdapter);
                    message_recycler_View.scrollToPosition(groupMessageList.size() - 1);*/

                   /*if(updated)
                    groupMessageAdapter.updateList(groupMessageList);
                   else
                   {
                       groupMessageAdapter = new GroupMessageAdapter(GroupChatActivity.this,groupMessageList,userID);
                       message_recycler_View.setAdapter(groupMessageAdapter);
                       updated = true;
                   }*/

                    //message_recycler_View.scrollToPosition(0);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        groupMessageRef.addValueEventListener(valueEventListener);


    }

    private void sendMessage() {


        if (message_box.getText().toString().length() > 0) {
            String message = message_box.getText().toString();
            DatabaseReference groupMessageSendRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                    .child(groupID);


            Timestamp timestamp = new Timestamp(System.currentTimeMillis());

            String messageId = groupMessageSendRef.push().getKey();


            HashMap<String, Object> groupMessageHash = new HashMap<>();
            groupMessageHash.put("messageText", message);
            groupMessageHash.put("senderId", userID);
            groupMessageHash.put("messageId", messageId);
            groupMessageHash.put("timeStamp", timestamp.toString());
            groupMessageHash.put("photo", userPhoto);
            groupMessageHash.put("sentStatus", "sending");
            groupMessageHash.put("username", currentUsername);

            groupMessageSendRef.child(messageId).updateChildren(groupMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    HashMap<String, Object> groupMessageUpdateHash = new HashMap<>();
                    groupMessageUpdateHash.put("sentStatus", "sent");
                    groupMessageSendRef.child(messageId).updateChildren(groupMessageUpdateHash);

                }
            });

            message_box.setText("");
        }

    }

    private void requestAudioPermisson() {



        /*Dexter.withActivity(parentActivity)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        disablerecord_button.setVisibility(View.GONE);
                        recordButton.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();*/

        Dexter.withActivity(GroupChatActivity.this)
                .withPermissions(Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            disablerecord_button.setVisibility(View.GONE);
                            recordButton.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(GroupChatActivity.this, "Please give audio record permission", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(parentActivity, "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            groupMessageRef.removeEventListener(valueEventListener);
        } catch (Exception e) {
            //
        }
    }

    @Override
    public void scrollViewToLastItem(Boolean scrollRecyclerView) {
        this.scrollRecyclerView = scrollRecyclerView;
    }
}