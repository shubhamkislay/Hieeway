package com.shubhamkislay.jetpacklogin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.EventListener;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class VideoPlayActivity extends AppCompatActivity /*, MediaPlayer.OnBufferingUpdateListener*/ {

    VideoView videoView;
    SurfaceView surfaceView;
    RelativeLayout progress_layout, load_layout;
    private Uri videoUri;
    private static final String FOLDER = "Hieeway Test Videos";
    private static final String ENCRYPTED_FILE_PREFIX = "encrpyted";
    private static final String TAG = "VIDEO PLAY ACTIVITY";
    public static boolean videoReady = false;
    private String userIdChattingWith, sender, mKey, videoUrl;
    int read;
    private URL url;
    public Boolean stopDecrypting = false;
    TextView loading;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder vidHolder;
    private SurfaceView vidSurface;
    private PlayerView player_view;
    private SimpleExoPlayer player;
    ProgressBar progress_one, progress_two;
    private String currentUserPublicKeyID, publicKeyID;
    private String currentUserPrivateKey, mediaKey;
    private InputStream inputStream;
    private Cipher decipher;
    InputStream in = null;
    OutputStream out = null;
    Button decryptedBtn;
    private Player.EventListener eventListener;
    private CipherInputStream cis;
    private Uri decryptedVideoUri;
    private SecretKeySpec originalKey;
    private ProgressDialog progressDialog;
    private File tempMp3;
    private TextView loadingpercent, percentsign;



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


        progress_one = findViewById(R.id.progress_one);
        progress_two = findViewById(R.id.progress_two);

        decryptedBtn = findViewById(R.id.decryptedBtn);

        /*try {
            url = new URL(videoUrl);

        } catch (MalformedURLException e) {
            Toast.makeText(VideoPlayActivity.this, "Error: " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }*/

        loading = findViewById(R.id.loading);
        loadingpercent = (TextView) findViewById(R.id.loadingpercent);
        percentsign = findViewById(R.id.percentsign);


        videoUri = Uri.parse(videoUrl);

        player_view = findViewById(R.id.player_view);


        /*player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());




        player_view.setPlayer(player);*/

        decryptedBtn = findViewById(R.id.decryptedBtn);


        File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File encryptedFile = new File(dir, ENCRYPTED_FILE_PREFIX + mKey + ".mp4");

        //File encryptedFile = new File(getFilesDir(), ENCRYPTED_FILE_PREFIX+mKey+".mp4");
/*        File originalFile = new File(dir, mKey+".mp4");

        if(originalFile.exists())
        {
            String filePath = Environment.getExternalStorageDirectory() + File.separator + FOLDER
                    + File.separator + mKey+".mp4";
            decryptedVideoUri = Uri.parse(filePath);
            checkAndPlay();
        }*/

        if (encryptedFile.exists()) {
            /*videoView.setVisibility(View.VISIBLE);
            player_view.setVisibility(View.GONE);
            String filePath = Environment.getExternalStorageDirectory() + File.separator + FOLDER
                    + File.separator + ENCRYPTED_FILE_PREFIX+mKey+".mp4";

            decryptedVideoUri = Uri.parse(filePath);
            //videoReady = true;
            progress_layout.setVisibility(View.GONE);
            load_layout.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);

            videoView.setVideoURI(decryptedVideoUri);
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });*/

            String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);

            byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
            originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".mp4", mKey);
            checkAndPlay();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!videoReady) {
                        deleteVideoFile(mKey);
                        newDownload(videoUrl);

                    }
                }
            }, 3000);
        } else {
            /*videoView.setVisibility(View.GONE);
            player_view.setVisibility(View.VISIBLE);*/
            /*File encryptedFile = new File(dir, ENCRYPTED_FILE_PREFIX+mKey+".mp4");
            if(encryptedFile.exists())
            {

                String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);


                loading.setText("File loaded");


                progress_two.setVisibility(View.INVISIBLE);
                progress_one.setVisibility(View.INVISIBLE);


                byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
                originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

                decryptedBtn.setVisibility(View.VISIBLE);
            }
            else {*/

            checkAndPlay();
            newDownload(videoUrl);
            //}
        }


        decryptedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decryptedBtn.setVisibility(View.GONE);

                loading.setText("Decrypting");

                progress_two.setVisibility(View.VISIBLE);
                progress_one.setVisibility(View.VISIBLE);

                // decryptFile(FOLDER,ENCRYPTED_FILE_PREFIX+mKey+".mp4",mKey+".mp4",originalKey);

                decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".mp4", mKey);
            }
        });


        /*DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "jetpacklogin"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(videoUri);

        player.prepare(mediaSource);
        player.setPlayWhenReady(true);*/



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



    private void checkAndPlay() {
        if (videoReady) {


            progress_layout.setVisibility(View.GONE);
            load_layout.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);

            videoView.setVisibility(View.VISIBLE);
            videoView.setVideoURI(decryptedVideoUri);
            videoView.start();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });



            /*DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "jetpacklogin"));

            ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                    .createMediaSource(decryptedVideoUri);

            player.prepare(mediaSource);
            player.setPlayWhenReady(true);
            eventListener = new Player.EventListener() {
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

                        player.release();
                        player.removeListener(eventListener);
                        player = null;
                        if (sender.equals(userIdChattingWith)) {
                            deleteVideoMessage();
                            deleteVideoFile(mKey);
                        }
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

                    switch (error.type) {
                        case ExoPlaybackException.TYPE_SOURCE:
                            Log.e(TAG, "TYPE_SOURCE: " + error.getSourceException().getMessage());
                            break;

                        case ExoPlaybackException.TYPE_RENDERER:
                            Log.e(TAG, "TYPE_RENDERER: " + error.getRendererException().getMessage());
                            break;

                        case ExoPlaybackException.TYPE_UNEXPECTED:
                            Log.e(TAG, "TYPE_UNEXPECTED: " + error.getUnexpectedException().getMessage());
                            break;
                    }

                *//*if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    Toast.makeText(VideoPlayActivity.this, "Source error", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                    Toast.makeText(VideoPlayActivity.this, "Rending error", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                    Toast.makeText(VideoPlayActivity.this, "Uexpected error", Toast.LENGTH_SHORT).show();
                    finish();
                }*//*

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
            };


            player.addListener(eventListener);*/
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAndPlay();

                }
            }, 1000);
        }

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

        //deleteVideoFile(mKey);

        finish();
    }

    public void decryptFile(final String folder, final String inputFileName, final String outputFileName, final SecretKey mediaKey) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {


                    //create output directory if it doesn't exist
                    File dir = new File(Environment.getExternalStorageDirectory(), folder);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    File inpufile = new File(dir, inputFileName);

                    File oufile = new File(dir, outputFileName);

                    if (oufile.exists()) {
                        String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                                + File.separator + outputFileName;

                        decryptedVideoUri = Uri.parse(filePath);
                        videoReady = true;
                    } else {
                        oufile.createNewFile();
                        if (!inpufile.exists())
                            inpufile.createNewFile();


                        in = new FileInputStream(inpufile);
                        out = new FileOutputStream(oufile);

                        Cipher decipher = Cipher.getInstance("AES");
                        // Cipher decipher = Cipher.getInstance("AES");
                        KeyGenerator kgen = KeyGenerator.getInstance("AES");
                        //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                        // skey = kgen.generateKey();

                        decipher.init(Cipher.DECRYPT_MODE, mediaKey);
                        cis = new CipherInputStream(in, decipher);

                        byte[] buffer = new byte[1024];
                        int read;
                        while ((read = cis.read(buffer)) != -1 && !stopDecrypting) {
                            out.write(buffer, 0, read);
                        }
                        if (stopDecrypting)
                            deleteVideoFile(mKey);
                        in.close();

                        in = null;

                        // write the output file (You have now copied the file)
                        out.flush();
                        out.close();
                        out = null;
                    }

                    String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                            + File.separator + outputFileName;

                    decryptedVideoUri = Uri.parse(filePath);
                    videoReady = true;


                } catch (FileNotFoundException fnfe1) {
                    deleteVideoFile(mKey);
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    deleteVideoFile(mKey);
                    Log.e("tag", e.getMessage());
                }


            }
        }).start();
    }

    public static byte[] decodeFile(byte[] fileData, final SecretKey mediaKey) throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, mediaKey);
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    public byte[] readFile(final String encryptedFileName) {
        byte[] contents = null;

        File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // File file = new File(getCacheDir(), encryptedFileName);

        File file = new File(dir, encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    void decodeFile(SecretKey mediaKey, String inputFile, String outputFileName) {

        //checkDecryptedVideo();
        try {

            byte[] decodedData = decodeFile(readFile(inputFile), mediaKey);
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
            playVideoFromData(decodedData, outputFileName);
            // }

        } catch (Exception e) {
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

    private void playVideoFromData(byte[] decodedData, String outputFileName) {
        try {
            // create temp file that will hold byte array
            tempMp3 = File.createTempFile(outputFileName, "mp4", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(decodedData);
            fos.close();

            decryptedVideoUri = Uri.parse(tempMp3.getPath());

            videoUri = Uri.fromFile(tempMp3);
            videoReady = true;

            // Tried reusing instance of media player
            // but that resulted in system crashes...
            /*MediaPlayer mediaPlayer = new MediaPlayer();
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (IOException ex) {
            ex.printStackTrace();

        }

    }

    public void newDownload(String url) {

        loading.setText("Downloading video...");
        final DownloadTask downloadTask = new DownloadTask(VideoPlayActivity.this);
        downloadTask.execute(url);
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;


        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                String fileN = ENCRYPTED_FILE_PREFIX + mKey + ".mp4";
                File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File filename = new File(dir, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                        //Toast.makeText(VideoPlayActivity.this,"Progress",Toast.LENGTH_SHORT).show();
                        //progressDialog.setProgress((int) (total * 100 / fileLength))
                    }
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                {
                    deleteVideoFile(mKey);
                    return e.toString();

                }
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();*/

            /*progressDialog = new ProgressDialog(VideoPlayActivity.this);
            progressDialog.setProgress(0);
            progressDialog.setTitle("Fetching Video");
            progressDialog.setMax(100);


            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();*/




            /*progress_two.setIndeterminate(false);
            progress_one.setIndeterminate(false);

            progress_one.setVisibility(View.VISIBLE);
            progress_two.setVisibility(View.VISIBLE);

            progress_one.setMax(100);
            progress_one.setProgress(20);*/


            // Toast.makeText(context, "Downloading Started", Toast.LENGTH_SHORT).show();
            loading.setText("Downloading ");
            loadingpercent.setText("0");
            loadingpercent.setVisibility(View.VISIBLE);
            percentsign.setVisibility(View.VISIBLE);


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // bnp.setProgress(progress[0]);
           /* loading.setText("Dowloading " + progress[0]+"%");
            Toast.makeText(VideoPlayActivity.this,"Progress"+progress[0],Toast.LENGTH_SHORT).show();*/


            loadingpercent.setText(progress[0] + "");

            //  progressDialog.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            // mWakeLock.release();


            loadingpercent.setVisibility(View.GONE);
            percentsign.setVisibility(View.GONE);


            if (result != null) {
                Toast.makeText(context, "Download failed" /*+ result*/, Toast.LENGTH_LONG).show();
                deleteVideoFile(mKey);
                finish();
            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);


                loading.setText("Video Downloaded");


                // progressDialog.dismiss();


                byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
                originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

                //decryptedBtn.setVisibility(View.VISIBLE);

                loading.setText("Decrypting");

                progress_two.setVisibility(View.VISIBLE);
                progress_one.setVisibility(View.VISIBLE);

                // decryptFile(FOLDER,ENCRYPTED_FILE_PREFIX+mKey+".mp4",mKey+".mp4",originalKey);

                decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".mp4", mKey);

            }

        }
    }

    private void deleteVideoFile(String inputFile) {
        if (tempMp3 != null)
            tempMp3.delete();
        try {
            // delete the original file
            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);

            new File(dir, inputFile + ".mp4").delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        try {
            // delete the original file
            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);

            new File(dir, ENCRYPTED_FILE_PREFIX + inputFile + ".mp4").delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();


        stopDecrypting = true;
        if (sender.equals(userIdChattingWith)) {
            deleteVideoMessage();
            deleteVideoFile(mKey);
        }
        //videoView.

        if (player != null) {
            player.removeListener(eventListener);

            player.release();
            player = null;
        }


    }


}
