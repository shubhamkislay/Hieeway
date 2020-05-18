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
