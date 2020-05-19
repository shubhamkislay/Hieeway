package com.shubhamkislay.jetpacklogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devlomi.record_view.OnBasketAnimationEnd;
import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.google.android.exoplayer2.Player;
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class AudioRecorderActivity extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final String FOLDER = "Hieeway Test Videos";
    private static final String ENCRYPTED_FILE_PREFIX = "encrpyted";
    private static final String TAG = "AudioRecordTest";
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
    InputStream in = null;
    OutputStream out = null;
    Button decryptedBtn;
    TextView downloadtag, download_percent, download_percent_sign;
    private File tempMp3;
    private String currentUserPublicKeyID, publicKeyID;
    private String currentUserPrivateKey, mediaKey;
    private InputStream inputStream;
    private Cipher decipher;
    private Player.EventListener eventListener;
    private CipherInputStream cis;
    private Uri decryptedVideoUri;
    private SecretKeySpec originalKey;
    private TextView loadingpercent, percentsign;
    private Uri videoUri;
    private boolean videoReady;
    private String videoUrl;

    public static byte[] decodeFile(byte[] fileData, final SecretKey mediaKey) throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, mediaKey);
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_recorder);

        Intent intent = getIntent();

       /* userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        audioUrl = intent.getStringExtra("audiourl");
        sender = intent.getStringExtra("sender");
        mKey = intent.getStringExtra("mKey");*/

        videoUrl = intent.getStringExtra("audiourl");
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        sender = intent.getStringExtra("sender");
        mKey = intent.getStringExtra("mKey");
        currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
        publicKeyID = intent.getStringExtra("publicKeyID");
        currentUserPrivateKey = intent.getStringExtra("currentUserPrivateKey");
        mediaKey = intent.getStringExtra("mediaKey");


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

        download_percent_sign = findViewById(R.id.download_percent_sign);
        download_percent = findViewById(R.id.download_percent_sign);
        downloadtag = findViewById(R.id.downloadtag);

        stop_btn = findViewById(R.id.stop_btn);

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";

        /*if (!audioUrl.equals("default"))
            play_btn.setVisibility(View.VISIBLE);*/

        // ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);

        if (ContextCompat.checkSelfPermission(AudioRecorderActivity.this, android.Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AudioRecorderActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AudioRecorderActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED)
            requestAllPermissions();

        else {

            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File encryptedFile = new File(dir, ENCRYPTED_FILE_PREFIX + mKey + ".mp3");

            if (encryptedFile.exists()) {
            /*videoView.setVisibility(View.VISIBLE);
            player_view.setVisibility(View.GONE);
            String filePath = Environment.getExternalStorageDirectory() + File.separator + FOLDER
                    + File.separator + ENCRYPTED_FILE_PREFIX+mKey+".mp3";

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
                decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".mp3", mKey);
                checkAndPlay();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (!videoReady) {
                            deleteAudioFile(mKey);
                            newDownload(videoUrl);

                        }
                    }
                }, 3000);
            } else {
            /*videoView.setVisibility(View.GONE);
            player_view.setVisibility(View.VISIBLE);*/
            /*File encryptedFile = new File(dir, ENCRYPTED_FILE_PREFIX+mKey+".mp3");
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
            }


        }







/*        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!audioUrl.equals("default")) {

                    *//*if (intialStage)
                        new Player()
                                .execute(audioUrl);

                    else {
                        if (!mediaPlayer.isPlaying())
                            mediaPlayer.start();
                    }*//*
                    try {
                       // playOnlineUrl(audioUrl);
                        loading_audio.setVisibility(View.VISIBLE);
                        //loading_audio.
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else
                    startPlaying();

            }
        });*/

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlaying();
            }
        });

        //player.


    }

    private void checkAndPlay() {
        if (videoReady) {
            startPlaying();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAndPlay();

                }
            }, 1000);
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

    private void startPlaying() {
        player = new MediaPlayer();
        play_btn.setVisibility(View.GONE);
        stop_btn.setVisibility(View.GONE);
        downloadtag.setVisibility(View.GONE);
        try {
            //player.setDataSource(fileName);

            audioPlaying = true;
            loading_audio.setVisibility(View.GONE);
            equilizerAnimation(equi_one);
            equilizerAnimation(equi_three);
            equilizerAnimation(equi_two);
            equilizerAnimation(equi_five);
            equilizerAnimation(equi_four);

            player.setDataSource(AudioRecorderActivity.this, videoUri);
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

    private void requestAllPermissions() {

        Dexter.withActivity(AudioRecorderActivity.this)
                .withPermissions(android.Manifest.permission.RECORD_AUDIO,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (!report.areAllPermissionsGranted()) {
                            Toast.makeText(AudioRecorderActivity.this, "Permission not given, cannot play the audio", Toast.LENGTH_SHORT).show();
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

    private void playVideoFromData(byte[] decodedData, String outputFileName) {
        try {
            // create temp file that will hold byte array
            tempMp3 = File.createTempFile(outputFileName, "mp3", getCacheDir());
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

        //  loading.setText("Downloading video...");
        final AudioRecorderActivity.DownloadTask downloadTask = new AudioRecorderActivity.DownloadTask(AudioRecorderActivity.this);
        downloadTask.execute(url);
    }

    private void deleteAudioFile(String inputFile) {
        if (tempMp3 != null)
            tempMp3.delete();
        try {
            // delete the original file
            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);

            new File(dir, inputFile + ".mp3").delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        try {
            // delete the original file
            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);

            new File(dir, ENCRYPTED_FILE_PREFIX + inputFile + ".mp3").delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        if (sender.equals(userIdChattingWith)) {
            deleteAudioMessage();
            deleteAudioFile(mKey);
        }
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            /*if (deleteUponExiting)
                deleteAudioMessage();*/
        }

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
                String fileN = ENCRYPTED_FILE_PREFIX + mKey + ".mp3";
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
                        //Toast.makeText(AudioRecorderActivity.this,"Progress",Toast.LENGTH_SHORT).show();
                        //progressDialog.setProgress((int) (total * 100 / fileLength))
                    }
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                {
                    deleteAudioFile(mKey);
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

            /*progressDialog = new ProgressDialog(AudioRecorderActivity.this);
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
            /*loading.setText("Downloading ");
            loadingpercent.setText("0");
            loadingpercent.setVisibility(View.VISIBLE);
            percentsign.setVisibility(View.VISIBLE);*/

            downloadtag.setText("Downloading");
            download_percent.setVisibility(View.VISIBLE);
            download_percent.setText("0");
            download_percent_sign.setVisibility(View.VISIBLE);


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            download_percent.setText("" + progress[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            // mWakeLock.release();

/*
            loadingpercent.setVisibility(View.GONE);
            percentsign.setVisibility(View.GONE);*/

            download_percent.setVisibility(View.GONE);

            download_percent_sign.setVisibility(View.GONE);
            downloadtag.setText("Decrypting audio");

            if (result != null) {
                Toast.makeText(context, "Download failed" /*+ result*/, Toast.LENGTH_LONG).show();
                deleteAudioFile(mKey);
                finish();
            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);


                // loading.setText("Video Downloaded");


                // progressDialog.dismiss();


                byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
                originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

                //decryptedBtn.setVisibility(View.VISIBLE);

                /*loading.setText("Decrypting");

                progress_two.setVisibility(View.VISIBLE);
                progress_one.setVisibility(View.VISIBLE);*/

                // decryptFile(FOLDER,ENCRYPTED_FILE_PREFIX+mKey+".mp3",mKey+".mp3",originalKey);

                decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".mp3", mKey);

            }

        }
    }

}
