package com.shubhamkislay.jetpacklogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

public class VideoEncryptionActivity extends AppCompatActivity {

    private static final int RECORD_VIDEO = 1;
    ImageButton videoRecord;
    Uri videoURI = null, encryptedVideoUri = null;
    FileInputStream fileInputStream;
    InputStream inputStream;
    FileOutputStream fileOutputStream;
    TextView originalVideoTextFile, encryptedlVideoTextFile;
    VideoView video_view;
    MediaPlayer mediaPlayer;
    int read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_encryption);


        videoRecord = findViewById(R.id.videoRecord);
        originalVideoTextFile = findViewById(R.id.originalVideoTextFile);
        encryptedlVideoTextFile = findViewById(R.id.encryptedlVideoTextFile);
        video_view = findViewById(R.id.video_view);


        originalVideoTextFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoURI != null)
                    playVideo(videoURI);
            }
        });
        encryptedlVideoTextFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (encryptedVideoUri != null)
                    playVideo(encryptedVideoUri);
            }
        });


        videoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startVideoRecording();
            }
        });
    }

    private void startVideoRecording() {


        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        takeVideoIntent.putExtra(android.provider.MediaStore.EXTRA_VIDEO_QUALITY, 5);
        takeVideoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 7);
        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takeVideoIntent, RECORD_VIDEO);
        }


    }


    private void checkVideoURI() {
        if (videoURI == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkVideoURI();
                }
            }, 1000);
        } else {
            originalVideoTextFile.setText(videoURI.toString());
            Toast.makeText(VideoEncryptionActivity.this, "You can play the video now", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo(Uri videoPlayURI) {
        video_view.setVisibility(View.VISIBLE);
        video_view.setVideoURI(videoPlayURI);

        try {

            video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer = mp;
                    mediaPlayer.start();

                }
            });

            video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer = mp;
                    mediaPlayer.release();
                    mediaPlayer = null;
                    video_view.setVisibility(View.GONE);

                }
            });
        } catch (Exception e) {
            Toast.makeText(VideoEncryptionActivity.this, "Cannot play video", Toast.LENGTH_SHORT).show();
            video_view.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RECORD_VIDEO) {
                checkVideoURI();

                try {
                    File root = new File(Environment.getExternalStorageDirectory(), "Hieeway Test Videos");
                    if (!root.exists()) {
                        root.mkdirs();
                    }

                    videoURI = data.getData();
                    inputStream = getContentResolver().openInputStream(videoURI);

                    final File outfile = new File(root, SystemClock.currentThreadTimeMillis() + ".mp4");
                    fileOutputStream = new FileOutputStream(outfile);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {

                                byte[] d = new byte[10 * 1024 * 1024];

                                while (true) {


                                    if (!((read = inputStream.read(d)) != -1)) break;

                                    fileOutputStream.write(d, 0, (char) read);
                                    fileOutputStream.flush();

                                }
                                fileOutputStream.close();
                                videoURI = Uri.fromFile(outfile);

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }
    }
}
