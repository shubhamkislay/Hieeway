package com.shubhamkislay.jetpacklogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.PlaybackPreparer;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class VideoPlayActivity extends AppCompatActivity /*, MediaPlayer.OnBufferingUpdateListener*/ {

    VideoView videoView;
    SurfaceView surfaceView;
    RelativeLayout progress_layout, load_layout;
    private Uri videoUri;
    public static boolean videoReady = false;
    private String userIdChattingWith, sender, mKey, videoUrl;
    int read;
    private URL url;
    TextView loading;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    private PlayerView player_view;
    private SimpleExoPlayer player;
    private String currentUserPublicKeyID, publicKeyID;
    private String currentUserPrivateKey, mediaKey;
    private InputStream inputStream;
    private Cipher decipher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);

        videoView = findViewById(R.id.video_view);

        Intent intent = getIntent();

        progress_layout = findViewById(R.id.progress_layout);
        load_layout = findViewById(R.id.load_layout);


        videoUrl = intent.getStringExtra("videoUrl");
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        sender = intent.getStringExtra("sender");
        mKey = intent.getStringExtra("mKey");
        currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
        publicKeyID = intent.getStringExtra("publicKeyID");
        currentUserPrivateKey = intent.getStringExtra("currentUserPrivateKey");
        mediaKey = intent.getStringExtra("mediaKey");


        try {
            url = new URL(videoUrl);

        } catch (MalformedURLException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        loading = findViewById(R.id.loading);


        videoUri = Uri.parse(videoUrl);

        player_view = findViewById(R.id.player_view);


        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

        player_view.setPlayer(player);

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "jetpacklogin"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(videoUri);

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);


        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    progress_layout.setVisibility(View.GONE);
                    load_layout.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                }

                if (playbackState == Player.STATE_BUFFERING) {
                    progress_layout.setVisibility(View.VISIBLE);
                    load_layout.setVisibility(View.VISIBLE);
                    loading.setVisibility(View.VISIBLE);
                }

                if (playbackState == Player.STATE_ENDED) {
                    if (sender.equals(userIdChattingWith))
                        deleteVideoMessage();
                    else
                        finish();
                }


            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });
/*        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {

                mp.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
                    @Override
                    public void onBufferingUpdate(MediaPlayer mp, int percent) {
                        loading.setText("Loading... "+percent);
                    }
                });



                return false;
            }
        });*/

        /*vidSurface = findViewById(R.id.surfaceView);
        vidHolder = vidSurface.getHolder();
        vidHolder.addCallback(this);*/

        // try {
        //    mediaPlayer = new MediaPlayer();






        /*}
        catch(Exception e){
            e.printStackTrace();
        }*/

        /*videoView.setVideoURI(videoUri);


        //   onBufferingUpdate();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                progress_layout.setVisibility(View.GONE);
                loading.setVisibility(View.GONE);
                videoView.start();
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (sender.equals(userIdChattingWith))
                    deleteVideoMessage();
                else
                    finish();
            }
        });*/

        // decryptVideo();
        //  checkAndPlay();

    }

    private void checkAndPlay() {
        if (videoReady) {
            DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "jetpacklogin"));

            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(videoUri);

            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAndPlay();

                }
            }, 1000);
        }

    }


    private void decryptVideo() {
        try {


            File root = new File(Environment.getExternalStorageDirectory(), "Hieeway Videos");
            if (!root.exists()) {
                root.mkdirs();
            }


            final File originalFile = new File(root, "original" + mKey + ".mp4");

            if (!originalFile.exists()) {
                originalFile.createNewFile();

                //  inputStream = getContentResolver().openInputStream();

                inputStream = new BufferedInputStream(url.openStream(), 8192);

                final FileOutputStream fileOutputStream = new FileOutputStream(originalFile);

                decipher = Cipher.getInstance("AES");


                String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);

                byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
                SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");


                decipher.init(Cipher.DECRYPT_MODE, originalKey);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {


                            CipherInputStream cis = new CipherInputStream(inputStream, decipher);

                            byte[] d = new byte[10 * 1024 * 1024];

                            while (true) {


                                if (!((read = inputStream.read(d)) != -1)) break;


                                fileOutputStream.write(d, 0, (char) read);
                                fileOutputStream.flush();


                            }
                            fileOutputStream.close();
                            videoUri = Uri.fromFile(originalFile);

                            videoReady = true;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


            } else {
                //FileInputStream encfis = new FileInputStream(inputFile);
                videoUri = Uri.fromFile(originalFile);
                videoReady = true;

            }
            // fos = new FileOutputStream(outfile);


        } catch (FileNotFoundException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }


    private void deleteVideoMessage() {
        FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(sender)
                .child(mKey)
                .removeValue();


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("seen", "seen");
        hashMap.put("videourl", "played");
        FirebaseDatabase.getInstance().getReference("Messages")
                .child(sender)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mKey)
                .updateChildren(hashMap);
        StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoUrl);
        photoRef.delete();

        finish();
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.release();
            player = null;
            finish();
        }
    }
}
