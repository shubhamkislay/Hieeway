package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class GroupChatActivity extends AppCompatActivity {

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
    private boolean isDisablerecord_button = false;
    private Button send_button;
    private String groupID, groupNameTxt, iconUrl;
    private CircleImageView icon;
    private TextView groupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        recordView = (RecordView) findViewById(R.id.record_view);
        recordButton = (RecordButton) findViewById(R.id.record_button);
        camera_background = findViewById(R.id.camera_background);
        message_box = findViewById(R.id.message_box);
        send_button = findViewById(R.id.send_button);
        disablerecord_button = (ImageView) findViewById(R.id.disablerecord_button);

        equlizer = findViewById(R.id.equlizer);
        equi_one = findViewById(R.id.equi_one);
        equi_two = findViewById(R.id.equi_two);
        equi_three = findViewById(R.id.equi_three);
        equi_four = findViewById(R.id.equi_four);
        equi_five = findViewById(R.id.equi_five);


        icon = findViewById(R.id.group_icon);
        groupName = findViewById(R.id.group_name);


        Intent intent = getIntent();
        groupID = intent.getStringExtra("groupID");
        groupNameTxt = intent.getStringExtra("groupName");
        iconUrl = intent.getStringExtra("icon");


        Glide.with(this).load(iconUrl).into(icon);
        groupName.setText(groupNameTxt);


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
        if (ContextCompat.checkSelfPermission(GroupChatActivity.this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            disablerecord_button.setVisibility(View.VISIBLE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.GONE);
            //recordButton.setEnabled(false);
        } else {
            disablerecord_button.setVisibility(View.GONE);
            isDisablerecord_button = true;
            recordButton.setVisibility(View.VISIBLE);
        }

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

    }

    private void sendMessage() {
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
}