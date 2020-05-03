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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        Intent intent = getIntent();

        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        audioUrl = intent.getStringExtra("audiourl");

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
                    // startPlaying();
                }
            });
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
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
        MediaPlayer mediaPlayer = new MediaPlayer();
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
                equilizerAnimation();
                mp.start();
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

    private void equilizerAnimation() {

        equlizer.setVisibility(View.VISIBLE);
        play_btn.setVisibility(View.GONE);
        stop_btn.setVisibility(View.GONE);

        Random r = new Random();
        float min = 0.65f;
        float max = 1.75f;

        float equiranfive = min + r.nextFloat() * (max - min);
        float equiranone = min + r.nextFloat() * (max - min);
        float equirantwo = min + r.nextFloat() * (max - min);
        float equirathree = min + r.nextFloat() * (max - min);
        float equirafour = min + r.nextFloat() * (max - min);

        Random ri = new Random();

        final int minTime = 150;
        final int maxTime = 300;

        equiranoneTime = ri.nextInt((maxTime - minTime) + 1) + minTime;
        equirantwoTime = ri.nextInt((maxTime - minTime) + 1) + minTime;
        equiranthreeTime = ri.nextInt((maxTime - minTime) + 1) + minTime;
        equiranfourTime = ri.nextInt((maxTime - minTime) + 1) + minTime;
        equiranfiveTime = ri.nextInt((maxTime - minTime) + 1) + minTime;

        /*if(equiranoneTime<0)
            equiranoneTime = equiranoneTime * -1;

        if(equirantwoTime<0)
            equirantwoTime = equirantwoTime * -1;
        if(equiranthreeTime<0)
            equiranthreeTime = equiranthreeTime * -1;
        if(equiranfourTime<0)
            equiranfourTime = equiranfourTime * -1;
        if(equiranfiveTime<0)
            equiranfiveTime = equiranfiveTime * -1;*/


        equi_one.animate().scaleY(equiranone).setDuration(equiranoneTime);
        equi_three.animate().scaleY(equirathree).setDuration(equiranthreeTime);
        equi_two.animate().scaleY(equirantwo).setDuration(equirantwoTime);
        equi_five.animate().scaleY(equiranfive).setDuration(equiranfiveTime);
        equi_four.animate().scaleY(equirafour).setDuration(equiranfourTime);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                equi_one.animate().scaleY(1.0f).setDuration(equiranoneTime);
                equi_three.animate().scaleY(1.0f).setDuration(equiranthreeTime);
                equi_two.animate().scaleY(1.0f).setDuration(equirantwoTime);
                equi_five.animate().scaleY(1.0f).setDuration(equiranfiveTime);
                equi_four.animate().scaleY(1.0f).setDuration(equiranfourTime);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (audioPlaying)
                            equilizerAnimation();
                        else
                            equlizer.setVisibility(View.GONE);

                    }
                }, maxTime);
            }
        }, maxTime);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    class Player extends AsyncTask<String, Void, Boolean> {
        private ProgressDialog progress;

        public Player() {
            progress = new ProgressDialog(AudioRecorderActivity.this);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            // TODO Auto-generated method stub
            Boolean prepared;
            try {

                mediaPlayer.setDataSource(params[0]);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        intialStage = true;
                        //  playPause=false;
                        //btn.setBackgroundResource(R.drawable.button_play);
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                    }
                });
                mediaPlayer.prepare();
                prepared = true;
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                Log.d("IllegarArgument", e.getMessage());
                prepared = false;
                e.printStackTrace();
            } catch (SecurityException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IllegalStateException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                prepared = false;
                e.printStackTrace();
            }
            return prepared;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progress.isShowing()) {
                progress.cancel();
            }
            Log.d("Prepared", "//" + result);
            mediaPlayer.start();

            intialStage = false;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            this.progress.setMessage("Buffering...");
            this.progress.show();

        }
    }
}
