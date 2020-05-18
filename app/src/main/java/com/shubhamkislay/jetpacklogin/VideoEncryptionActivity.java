package com.shubhamkislay.jetpacklogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
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
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class VideoEncryptionActivity extends AppCompatActivity {

    private static final int RECORD_VIDEO = 1;
    ImageButton videoRecord;
    private static final String FOLDER = "Hieeway Test Videos";
    Uri videoURI = null, encryptedVideoUri = null, decryptedVideoUri = null;
    InputStream inputStream;
    FileInputStream originalfileInputStream, encryptedfileInputStream;
    FileOutputStream fileOutputStream, enryptOutputStream, deryptOutputStream;
    VideoView video_view;
    MediaPlayer mediaPlayer;
    TextView originalVideoTextFile, encryptedlVideoTextFile, decryptedlVideoTextFile;
    PlayerView player_view;
    SecretKey skey;
    Button encryptVideo, decryptVideo;
    int read;
    InputStream in = null;
    OutputStream out = null;
    private SimpleExoPlayer player;
    private CipherInputStream cis;
    private String mKey;


    public static byte[] decodeFile(byte[] fileData, final SecretKey mediaKey)
            throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, mediaKey);
        decrypted = cipher.doFinal(fileData);
        return decrypted;
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

    private void checkEncryptedVideo() {
        if (encryptedVideoUri == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkEncryptedVideo();


                }
            }, 1000);
        } else {
            encryptedlVideoTextFile.setText(encryptedVideoUri.toString());
            Toast.makeText(VideoEncryptionActivity.this, "Try to play the encryted video now", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkDecryptedVideo() {
        if (decryptedVideoUri == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkDecryptedVideo();


                }
            }, 1000);
        } else {
            decryptedlVideoTextFile.setText(decryptedVideoUri.toString());
            Toast.makeText(VideoEncryptionActivity.this, "Decrypted", Toast.LENGTH_SHORT).show();
        }

    }

    private void playVideo(Uri videoPlayURI) {
        player_view.setVisibility(View.VISIBLE);

        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "jetpacklogin"));

        ExtractorMediaSource mediaSource = new ExtractorMediaSource.Factory(defaultDataSourceFactory)
                .createMediaSource(videoPlayURI);

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
                if (playbackState == Player.STATE_ENDED)
                    player_view.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

                if (error.type == ExoPlaybackException.TYPE_SOURCE) {
                    Toast.makeText(VideoEncryptionActivity.this, "Source error", Toast.LENGTH_SHORT).show();
                    player_view.setVisibility(View.GONE);
                } else if (error.type == ExoPlaybackException.TYPE_RENDERER) {
                    Toast.makeText(VideoEncryptionActivity.this, "Rending error", Toast.LENGTH_SHORT).show();
                    player_view.setVisibility(View.GONE);
                } else if (error.type == ExoPlaybackException.TYPE_UNEXPECTED) {
                    Toast.makeText(VideoEncryptionActivity.this, "Uexpected error", Toast.LENGTH_SHORT).show();
                    player_view.setVisibility(View.GONE);
                }
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


        /*video_view.setVisibility(View.VISIBLE);
        video_view.setVideoURI(videoPlayURI);
        video_view.requestFocus();
        video_view.start();

       // try {

            video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    *//*mediaPlayer = mp;
                    mediaPlayer.start();*//*

                }
            });

            video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    *//*mediaPlayer = mp;
                   // mediaPlayer.stop();
                   // mediaPlayer.reset();
                   // video_view.
                    mediaPlayer.release();
                    mediaPlayer = null;*//*
                   // video_view.setVisibility(View.GONE);

                }
            });
        *//*} catch (Exception e) {
            Toast.makeText(VideoEncryptionActivity.this, "Cannot play video because: "+e.toString(), Toast.LENGTH_SHORT).show();
            video_view.setVisibility(View.GONE);
        }*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == RECORD_VIDEO) {

                videoURI = data.getData();
                saveOriginalFile(videoURI);
            }
        }
    }

    public void encryptVideoMethod() {

        checkEncryptedVideo();
        encryptedlVideoTextFile.setText("Your video is being encrypted");
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Hieeway Test Videos");
            if (!root.exists()) {
                root.mkdirs();
            }
            final String filename = "copyxyz" + ".mp4";
            final String originalFilename = "xyz" + ".mp4";

            final File outfile = new File(root, filename);

            final File inputFile = new File(root, originalFilename);
            originalfileInputStream = new FileInputStream(inputFile);

            enryptOutputStream = new FileOutputStream(outfile);

            Cipher encipher = Cipher.getInstance("AES");
            // Cipher decipher = Cipher.getInstance("AES");
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
            skey = kgen.generateKey();
            //Lgo
            encipher.init(Cipher.ENCRYPT_MODE, skey);
            cis = new CipherInputStream(originalfileInputStream, encipher);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] d = new byte[1024];
                        /*while ((read = cis.read(d)) != -1) {
                            enryptOutputStream.write(d, 0, (char) read);
                            enryptOutputStream.flush();
                        }*/

                        while (true) {

                            if (!((read = originalfileInputStream.read(d)) != -1)) break;


                            Log.v("Copy video", "" + read);
                            enryptOutputStream.write(d, 0, (char) read);
                            enryptOutputStream.flush();
                        }
                        enryptOutputStream.close();

                        String filePath = Environment.getExternalStorageDirectory() + File.separator + "Hieeway Test Videos"
                                + File.separator + filename;

                        encryptedVideoUri = Uri.parse(filePath);

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(VideoEncryptionActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (FileNotFoundException e) {
            Toast.makeText(VideoEncryptionActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            Toast.makeText(VideoEncryptionActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText(VideoEncryptionActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            Toast.makeText(VideoEncryptionActivity.this, "Error: " + e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }


    private void saveOriginalFile(Uri videoIntentURI) {
        checkVideoURI();

        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Hieeway Test Videos");
            if (!root.exists()) {
                root.mkdirs();
            }

            videoURI = videoIntentURI;
            inputStream = getContentResolver().openInputStream(videoURI);

            final String filename = "xyz" + ".mp4";
            final File outfile = new File(root, filename);
            fileOutputStream = new FileOutputStream(outfile);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        byte[] d = new byte[1024 * 10];

                        while (true) {


                            if (!((read = inputStream.read(d)) != -1)) break;

                            fileOutputStream.write(d, 0, (char) read);
                            fileOutputStream.flush();

                        }
                        fileOutputStream.close();

                        String filePath = Environment.getExternalStorageDirectory() + File.separator + "Hieeway Test Videos"
                                + File.separator + filename;

                        videoURI = Uri.parse(filePath);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private void playFromFile(String originalFileName) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + "Hieeway Test Videos"
                + File.separator + originalFileName;

        videoURI = Uri.parse(filePath);

        //playTheURI
    }

    private void encryptFile(final String folder, final String inputFileName, final String outputFileName) {


        checkEncryptedVideo();
        encryptedlVideoTextFile.setText("encrypting video");

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

                    in = new FileInputStream(inpufile);
                    out = new FileOutputStream(oufile);

                    Cipher encipher = Cipher.getInstance("AES");
                    // Cipher decipher = Cipher.getInstance("AES");
                    KeyGenerator kgen = KeyGenerator.getInstance("AES");
                    //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                    skey = kgen.generateKey();

                    encipher.init(Cipher.ENCRYPT_MODE, skey);
                    cis = new CipherInputStream(in, encipher);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = cis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;

                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                        + File.separator + outputFileName;

                encryptedVideoUri = Uri.parse(filePath);

            }
        }).start();


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_encryption);


        videoRecord = findViewById(R.id.videoRecord);
        originalVideoTextFile = findViewById(R.id.originalVideoTextFile);
        encryptedlVideoTextFile = findViewById(R.id.encryptedlVideoTextFile);
        video_view = findViewById(R.id.video_view);


        player_view = findViewById(R.id.player_view);


        player = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector());

        player_view.setPlayer(player);

        decryptedlVideoTextFile = findViewById(R.id.decryptedlVideoTextFile);

        encryptVideo = findViewById(R.id.encryptVideo);
        decryptVideo = findViewById(R.id.decryptVideo);


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
        decryptedlVideoTextFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (decryptedVideoUri != null)
                    playVideo(decryptedVideoUri);
            }
        });


        videoRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startVideoRecording();
            }
        });

        encryptVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoURI != null) {
                    //  encryptVideoMethod();

                    encryptFile(FOLDER, "xyz.mp4", "encryptedxyz.mp4");
                }
            }
        });


        decryptVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (encryptedVideoUri != null) {
                    // decryptFile();
                    //  decryptFile(FOLDER, "encryptedxyz.mp4", "decryptedxyz.mp4", skey);

                    decodeFile(skey, "encryptedxyz.mp4");
                }
            }
        });
    }

    public void decryptFile(final String folder, final String inputFileName, final String outputFileName, final SecretKey mediaKey) {
        checkDecryptedVideo();
        decryptedlVideoTextFile.setText("decrypting video");

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {


                    //create output directory if it doesn't exist
                    File dir = new File(Environment.getExternalStorageDirectory(), folder);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File cache = new File(Environment.getExternalStorageDirectory(), folder);
                    if (!cache.exists()) {
                        cache.mkdirs();
                    }

                    File inpufile = new File(dir, inputFileName);
                    File oufile = new File(dir, outputFileName);

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
                    while ((read = cis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;

                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                        + File.separator + outputFileName;

                decryptedVideoUri = Uri.parse(filePath);

            }
        }).start();
    }

    public byte[] readFile(final String encryptedFileName) {
        byte[] contents = null;

        File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

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

    void decodeFile(SecretKey mediaKey, String inputFile) {

        checkDecryptedVideo();
        try {
            byte[] decodedData = decodeFile(readFile(inputFile), mediaKey);
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
            playVideoFromData(decodedData);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playVideoFromData(byte[] decodedData) {
        try {
            // create temp file that will hold byte array
            File tempMp3 = File.createTempFile("decrpytedxyz", "mp4", getCacheDir());
            tempMp3.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempMp3);
            fos.write(decodedData);
            fos.close();

            decryptedVideoUri = Uri.parse(tempMp3.getPath());

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
        final DownloadTask downloadTask = new DownloadTask(VideoEncryptionActivity.this);
        downloadTask.execute(url);
    }

    /**
     * Download file from URL
     */
    public class DownloadTask extends AsyncTask<String, Integer, String> {
        private Context context;
        private PowerManager.WakeLock mWakeLock;

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
                String fileN = mKey + ".mp4";
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
                    if (fileLength > 0)
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
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
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            Toast.makeText(context, "Downloading Started", Toast.LENGTH_SHORT).show();


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // bnp.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();

            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();

        }
    }

}
