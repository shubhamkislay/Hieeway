package com.shubhamkislay.jetpacklogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class AudioRecorderActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static String fileName = null;
    String userIdChattingWith;
    StorageReference storageReference;
    StorageTask uploadTask;
    ProgressDialog progressDialog;
    Button record_btn;
    Button play_btn;
    Button stop_btn;
    String audioUrl;
    ProgressBar loading_audio;
    RelativeLayout equlizer;
    RelativeLayout equi_one;
    RelativeLayout equi_two;
    RelativeLayout equi_three;
    RelativeLayout equi_four;
    RelativeLayout equi_five;
    Boolean audioPlaying = false;
    int equiranoneTime;
    int equirantwoTime;
    int equiranthreeTime;
    int equiranfourTime;
    int equiranfiveTime;
    private MediaRecorder recorder = null;
    private MediaPlayer player = null;
    private MediaPlayer mediaPlayer = null;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.MEDIA_CONTENT_CONTROL};
    private boolean intialStage = true;
    String sender;
    Boolean deleteMessage = true;
    String mKey;
    Boolean deleteUponExiting = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        Intent intent = getIntent();

        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        audioUrl = intent.getStringExtra("audiourl");
        sender = intent.getStringExtra("sender");
        mKey = intent.getStringExtra("mKey");


        if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(sender))
            deleteMessage = false;



        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        loading_audio = findViewById(R.id.loading_audio);
        equlizer = findViewById(R.id.equlizer);
        equi_one = findViewById(R.id.equi_one);
        equi_two = findViewById(R.id.equi_two);
        equi_three = findViewById(R.id.equi_three);
        equi_four = findViewById(R.id.equi_four);
        equi_five = findViewById(R.id.equi_five);


        record_btn = findViewById(R.id.record_btn);
        play_btn = findViewById(R.id.play_btn);

        stop_btn = findViewById(R.id.stop_btn);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        if (!audioUrl.equals("default"))
            play_btn.setVisibility(View.VISIBLE);

        // ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        if (ContextCompat.checkSelfPermission(AudioRecorderActivity.this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AudioRecorderActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AudioRecorderActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
            requestAllPermissions();


        record_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN)
                    startRecording();

                else if (event.getAction() == MotionEvent.ACTION_UP)
                    stopRecording();

                return false;
            }
        });

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!audioUrl.equals("default")) {

                    /*if (intialStage)
                        new Player()
                                .execute(audioUrl);

                    else {
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }*/
                    try {
                        playOnlineUrl(audioUrl);
                        loading_audio.setVisibility(View.VISIBLE);
                        //loading_audio.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    startPlaying();

            }
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });

        //player.

        RecordView recordView = (RecordView) findViewById(R.id.record_view);
        RecordButton recordButton = (RecordButton) findViewById(R.id.record_button);

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
        recordView.setCounterTimeColor(Color.parseColor("#ff0000"));


        //recordButton.setListenForRecord(false);

        //ListenForRecord must be false ,otherwise onClick will not be called
        recordButton.setOnRecordClickListener(new OnRecordClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(AudioRecorderActivity.this, "RECORD BUTTON CLICKED", Toast.LENGTH_SHORT).show();
                //  Log.d("RecordButton","RECORD BUTTON CLICKED");
            }
        });

        recordView.setOnBasketAnimationEndListener(new OnBasketAnimationEnd() {
            @Override
            public void onAnimationEnd() {
                // Log.d("RecordView", "Basket Animation Finished");
            }
        });


        recordView.setSlideToCancelText("Slide to cancel");

        //IMPORTANT
        recordButton.setRecordView(recordView);


        recordView.setOnRecordListener(new OnRecordListener() {
            @Override
            public void onStart() {
                //Start Recording..
                startRecording();
                Log.d("RecordView", "onStart");
            }

            @Override
            public void onCancel() {
                //On Swipe To Cancel
                recorder.reset();
                recorder.release();

                //recorder.
                Log.d("RecordView", "onCancel");

            }

            @Override
            public void onFinish(long recordTime) {
                //Stop Recording..
                /*String time = getHumanTimeText(recordTime);
                Log.d("RecordView", "onFinish");


                Log.d("RecordTime", time);*/

                stopRecording();
            }

            @Override
            public void onLessThanSecond() {
                //When the record time is less than One Second
                Log.d("RecordView", "onLessThanSecond");

                recorder.reset();
                recorder.release();
            }
        });


    }


    private void startRecording() {
        recorder = new MediaRecorder();
        play_btn.setVisibility(View.GONE);
        Toast.makeText(AudioRecorderActivity.this, "Recording started", Toast.LENGTH_SHORT).show();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            //Log.e(LOG_TAG, "prepare() failed");
        }

        recorder.start();
    }

    private void stopRecording() {

        Toast.makeText(AudioRecorderActivity.this, "Recording stopped", Toast.LENGTH_SHORT).show();
        recorder.stop();
        recorder.release();
        recorder = null;

        uploadAudio();

        play_btn.setVisibility(View.VISIBLE);
    }

    private void uploadAudio() {

        progressDialog = new ProgressDialog(AudioRecorderActivity.this);
        progressDialog.setTitle("Uploading audio...");
        progressDialog.show();

        Uri uri = Uri.fromFile(new File(fileName));


        storageReference = FirebaseStorage.getInstance().getReference().child(System.currentTimeMillis() + "." + getExtension(uri));


        uploadTask = storageReference.putFile(uri);

        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task task) throws Exception {

                if (!task.isSuccessful()) {
                    throw task.getException();
                }


                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {

                    progressDialog.dismiss();
                    Uri downloadUri = task.getResult();

                    String mUri = downloadUri.toString();


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid())
                            .child(userIdChattingWith);

                    DatabaseReference receiverReference = FirebaseDatabase.getInstance().getReference("Messages")
                            .child(userIdChattingWith)
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid());

                    String mKey = databaseReference.push().getKey();


                    final HashMap<String, Object> sendMessageHash = new HashMap<>();
                    sendMessageHash.put("senderId", FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid());
                    sendMessageHash.put("receiverId", userIdChattingWith);
                    sendMessageHash.put("messageId", mKey);
                    sendMessageHash.put("messageText", "");
                    sendMessageHash.put("sentStatus", "sending");
                    sendMessageHash.put("seen", "notseen");
                    sendMessageHash.put("photourl", "none");
                    sendMessageHash.put("audiourl", mUri);
                    sendMessageHash.put("videourl", "none");
                    sendMessageHash.put("gotReplyID", "none");
                    sendMessageHash.put("replyTag", false);
                    sendMessageHash.put("replyID", "none");
                    sendMessageHash.put("senderReplyMessage", "none");
                    sendMessageHash.put("ifMessageTwo", false);
                    sendMessageHash.put("messageTextTwo", "");
                    sendMessageHash.put("ifMessageThree", false);
                    sendMessageHash.put("messageTextThree", "");
                    sendMessageHash.put("showReplyMsg", false);
                    sendMessageHash.put("replyMsg", " ");
                    sendMessageHash.put("showGotReplyMsg", false);
                    sendMessageHash.put("gotReplyMsg", " ");
                    databaseReference.child(mKey).updateChildren(sendMessageHash);
                    receiverReference.child(mKey).updateChildren(sendMessageHash);

                    // createChatListItem(usernameChattingWith,userphotoUrl,currentUsername,currentUserPhoto);
                }
            }
        });


    }

    private String getExtension(Uri uri) {


        ContentResolver contentResolver = AudioRecorderActivity.this.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void startPlaying() {
        player = new MediaPlayer();
        play_btn.setVisibility(View.GONE);
        stop_btn.setVisibility(View.VISIBLE);
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();

            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    deleteAudioMessage();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void deleteAudioMessage() {


        if (!sender.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {

            DatabaseReference receiverReference = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userIdChattingWith);

            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(audioUrl);
            photoRef.delete();


            receiverReference.child(mKey).removeValue();

            DatabaseReference senderReference = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(userIdChattingWith)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(mKey);

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("seen", "seen");
            hashMap.put("audiourl", "played");
            senderReference.updateChildren(hashMap);

            finish();
        } else
            finish();

    }

    private void stopPlaying() {
        stop_btn.setVisibility(View.GONE);
        play_btn.setVisibility(View.VISIBLE);
        player.release();
        player = null;
    }

    private void requestAllPermissions() {

        Dexter.withActivity(AudioRecorderActivity.this)
                .withPermissions(android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!report.areAllPermissionsGranted()) {
                            Toast.makeText(AudioRecorderActivity.this, "Permission not given!", Toast.LENGTH_SHORT).show();
                            finish();
                        }


                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                        token.continuePermissionRequest();

                        // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void playOnlineUrl(String audioUrl) throws IOException {
        String url = audioUrl;// your URL here
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(url);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // You can show progress dialog here untill it prepared to play
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Now dismis progress dialog, Media palyer will start playing
                audioPlaying = true;
                loading_audio.setVisibility(View.GONE);
                //equilizerAnimation();
                equilizerAnimation(equi_one);
                equilizerAnimation(equi_three);
                equilizerAnimation(equi_two);
                equilizerAnimation(equi_five);
                equilizerAnimation(equi_four);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        deleteUponExiting = true;
                    }
                }, 1000);


                mp.start();


                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (deleteMessage)
                            deleteAudioMessage();
                            // startPlaying();
                        else
                            finish();
                    }
                });
            }
        });

        mediaPlayer.prepareAsync();

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                // dissmiss progress bar here. It will come here when
                // MediaPlayer
                // is not able to play file. You can show error message to user


                return false;
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Toast.makeText(AudioRecorderActivity.this, "Finished playing audio", Toast.LENGTH_SHORT).show();
                //loading_audio.setVisibility(View.GONE);
                audioPlaying = false;

                mp.stop();
                mp.release();
                finish();
            }
        });
    }


    private void equilizerAnimation(final RelativeLayout equi) {

        equlizer.setAlpha(1.0f);
        equlizer.setVisibility(View.VISIBLE);
        play_btn.setVisibility(View.GONE);
        stop_btn.setVisibility(View.GONE);

        Random r = new Random();
        float min = 0.65f;
        float max = 1.3f;

        float equir = min + r.nextFloat() * (max - min);


        Random ri = new Random();

        final int minTime = 150;
        final int maxTime = 300;

        final int equitime = ri.nextInt((maxTime - minTime) + 1) + minTime;

        equi.animate().scaleY(equir).setDuration(equitime);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                equi.animate().scaleY(1.0f).setDuration(equitime);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (audioPlaying)
                            equilizerAnimation(equi);
                        else
                            equlizer.setVisibility(View.GONE);

                    }
                }, equitime);
            }
        }, equitime);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            if (deleteUponExiting)
                deleteAudioMessage();
        }

    }


}
