package com.hieeway.hieeway;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.hieeway.hieeway.Model.Pal;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static br.com.instachat.emojilibrary.model.Emoji.TAG;
import static com.hieeway.hieeway.MusicPalService.MUSIC_PAL_SERVICE_RUNNING;
import static com.hieeway.hieeway.MusicPalService.USER_NAME_MUSIC_SYNC;
import static com.hieeway.hieeway.MyApplication.CHANNEL_1_ID;
import static com.hieeway.hieeway.MyApplication.CHANNEL_3_ID;

public class MusicSyncNotification extends Service {

    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    private static String ACTION_STOP_SERVICE = "stop";
    private static String OPEN_SPOTIFY = "spotify";
    private static String OPEN_PROFILE = "profile";
    final String appPackageName = "com.spotify.music";
    Intent stopSelfIntent, openSpotifyIntent, openProfileIntent;
    SpotifyAppRemote mSpotifyAppRemote;
    private String username, userid, photo;
    private PendingIntent pIntentlogin, openSpotify, openProfile;
    private int notificationId;
    private int i = 0;
    private boolean stop = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        notificationId = MyApplication.NotificationID.getID();

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG, "called to cancel service");
            //manager.cancel(NOTIFCATION_ID);


            stop = true;
            stopSelf();


        } else if (OPEN_PROFILE.equals(intent.getAction())) {
            /*Intent openIntent = new Intent(this, VerticalPageActivity.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.putExtra("username", intent.getStringExtra("username"));
            openIntent.putExtra("userid", intent.getStringExtra("userid"));
            openIntent.putExtra("photo", intent.getStringExtra("photo"));
            openIntent.putExtra("live", "live");*/

            //startActivity(openIntent);


            startMusicPalService(intent.getStringExtra("userid"),
                    intent.getStringExtra("username"),
                    intent.getStringExtra("photo"));

            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            stop = true;
            stopSelf();

        } else {


            username = intent.getStringExtra("username");
            userid = intent.getStringExtra("userid");
            photo = intent.getStringExtra("photo");
            stopSelfIntent = new Intent(MusicSyncNotification.this, MusicSyncNotification.class);

            stopSelfIntent.setAction(ACTION_STOP_SERVICE);
            pIntentlogin = PendingIntent.getService(MusicSyncNotification.this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            startRingingNotification();


        }


        return START_NOT_STICKY;
    }

    private void startRingingNotification() {


        // openSpotifyIntent.putExtra("songID", songId);


        openProfileIntent = new Intent(MusicSyncNotification.this, MusicSyncNotification.class);

        openProfileIntent.putExtra("username", username);
        openProfileIntent.putExtra("userid", userid);
        openProfileIntent.putExtra("photo", photo);

        openProfileIntent.setAction(OPEN_PROFILE);
        openProfile = PendingIntent.getService(MusicSyncNotification.this, 0, openProfileIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        // openSpotify = PendingIntent.getService(MusicSyncNotification.this, 0, openSpotifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.live_message_request_notification);

        Bitmap imageBitmap = null;


        try {
            imageBitmap = Glide.with(MusicSyncNotification.this)
                    .asBitmap()
                    .load(photo)
                    .submit(512, 512)
                    .get();

            collapsedView.setImageViewBitmap(R.id.logo, imageBitmap);


        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        if (!MUSIC_PAL_SERVICE_RUNNING) {
            collapsedView.setTextViewText(R.id.notification_message_collapsed, "Music Sync Request");
            collapsedView.setTextViewText(R.id.username, username + " wants to sync music");
        } else {
            collapsedView.setTextViewText(R.id.notification_message_collapsed, "Music Sync Request");
            collapsedView.setTextViewText(R.id.username, username +
                    " wants to sync music\n Tap accept to end with " + USER_NAME_MUSIC_SYNC
                    + " and sync with " + username);
        }


        collapsedView.setOnClickPendingIntent(R.id.ignore_live, pIntentlogin);
        collapsedView.setOnClickPendingIntent(R.id.accept_live, openProfile);


        Notification notification = new NotificationCompat.Builder(MusicSyncNotification.this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                .setCustomContentView(collapsedView)
                .setAutoCancel(true)
                /*.setContentTitle("Live Message Request")
                .setContentText(username+" wants to live chat")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                .addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                .addAction(R.drawable.spotify_white_icon, "Open in Spotify", openSpotify)*/
                .build();

        startForeground(notificationId, notification);

        if (!MUSIC_PAL_SERVICE_RUNNING)
            startRing();

    }

    private void startRing() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        i += 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(200);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stop && i != 25)
                    startRing();
                else
                    stopSelf();
            }
        }, 800);
    }

    private void startMusicPalService(String userid, String username, String photo) {


        FirebaseDatabase.getInstance().getReference("Pal")
                .child(userid)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                        try {
                            Pal pal = mutableData.getValue(Pal.class);
                            if (pal.getConnection().equals("join")) {


                                FirebaseDatabase.getInstance().getReference("Pal")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()) {
                                            HashMap<String, Object> palHash = new HashMap<>();

                                            palHash.put("connection", "join");
                                            palHash.put("songID", "default");
                                            palHash.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            FirebaseDatabase.getInstance().getReference("Pal")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(userid)
                                                    .updateChildren(palHash);

                                            Intent musicPalService = new Intent(MusicSyncNotification.this, MusicPalService.class);

                                            musicPalService.putExtra("otherUserId", userid);
                                            musicPalService.putExtra("username", username);
                                            musicPalService.putExtra("userPhoto", photo);

                                            startService(musicPalService);

                                            Toast.makeText(MusicSyncNotification.this, "Connecting your music with " + username, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            } else {


                                FirebaseDatabase.getInstance().getReference("Pal")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            HashMap<String, Object> palHash = new HashMap<>();

                                            palHash.put("connection", "join");
                                            palHash.put("songID", "default");
                                            palHash.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            FirebaseDatabase.getInstance().getReference("Pal")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(userid)
                                                    .updateChildren(palHash);

                                                            /*HashMap<String, Object> palRequestHash = new HashMap<>();

                                                            palRequestHash.put("connection", "request");
                                                            palRequestHash.put("songID", "default");


                                                            FirebaseDatabase.getInstance().getReference("Pal")
                                                                    .child(userid)
                                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                    .updateChildren(palRequestHash);*/

                                            Intent musicPalService = new Intent(MusicSyncNotification.this, MusicPalService.class);

                                            musicPalService.putExtra("otherUserId", userid);
                                            musicPalService.putExtra("username", username);
                                            musicPalService.putExtra("userPhoto", photo);

                                            startService(musicPalService);

                                            Toast.makeText(MusicSyncNotification.this, "Connecting your music with " + username, Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });


                            }
                        } catch (Exception e) {


                            FirebaseDatabase.getInstance().getReference("Pal")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    HashMap<String, Object> palHash = new HashMap<>();

                                    palHash.put("connection", "join");
                                    palHash.put("songID", "default");
                                    palHash.put("senderId", FirebaseAuth.getInstance().getCurrentUser().getUid());

                                    FirebaseDatabase.getInstance().getReference("Pal")
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(userid)
                                            .updateChildren(palHash);

                                    //HashMap<String, Object> palRequestHash = new HashMap<>();

                                                    /*palRequestHash.put("connection", "request");
                                                    palRequestHash.put("songID", "default");


                                                    FirebaseDatabase.getInstance().getReference("Pal")
                                                            .child(userid)
                                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                            .updateChildren(palRequestHash);*/

                                    Intent musicPalService = new Intent(MusicSyncNotification.this, MusicPalService.class);

                                    musicPalService.putExtra("otherUserId", userid);
                                    musicPalService.putExtra("username", username);
                                    musicPalService.putExtra("userPhoto", photo);

                                    startService(musicPalService);

                                    Toast.makeText(MusicSyncNotification.this, "Connecting your music with " + username, Toast.LENGTH_SHORT).show();


                                }
                            });


                        }

                        return null;
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });


    }

}
