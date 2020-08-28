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

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static br.com.instachat.emojilibrary.model.Emoji.TAG;
import static com.hieeway.hieeway.MyApplication.CHANNEL_1_ID;
import static com.hieeway.hieeway.MyApplication.CHANNEL_3_ID;
import static com.hieeway.hieeway.VerticalPageActivityPerf.isWatching;

public class LiveMessageNotificationService extends Service {

    private static String ACTION_STOP_SERVICE = "stop";
    private static String OPEN_SPOTIFY = "spotify";
    private static String OPEN_PROFILE = "profile";
    Intent stopSelfIntent, openSpotifyIntent, openProfileIntent;
    private String username, userid, photo;
    private PendingIntent pIntentlogin, openSpotify, openProfile;
    private int notificationId;
    private int i = 0;
    private boolean stop = false;
    private boolean serviceStarted = false;

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
            Intent startLiveActiveServiceIntent = new Intent(LiveMessageNotificationService.this, LiveMessageActiveService.class);
            startLiveActiveServiceIntent.putExtra("username", intent.getStringExtra("username"));
            startLiveActiveServiceIntent.putExtra("userid", intent.getStringExtra("userid"));
            startLiveActiveServiceIntent.putExtra("youtubeID", "default");
            startLiveActiveServiceIntent.putExtra("photo", intent.getStringExtra("photo"));

            startService(startLiveActiveServiceIntent);

            serviceStarted = true;
            stopSelf();


        } else if (OPEN_PROFILE.equals(intent.getAction())) {
            Intent openIntent = new Intent(this, VerticalPageActivityPerf.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.putExtra("username", intent.getStringExtra("username"));
            openIntent.putExtra("userid", intent.getStringExtra("userid"));
            openIntent.putExtra("photo", intent.getStringExtra("photo"));
            openIntent.putExtra("live", "live");

            startActivity(openIntent);
            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            stop = true;
            stopSelf();
        } else {


            username = intent.getStringExtra("username");
            userid = intent.getStringExtra("userid");
            photo = intent.getStringExtra("photo");
            stopSelfIntent = new Intent(LiveMessageNotificationService.this, LiveMessageNotificationService.class);
            stopSelfIntent.putExtra("username", username);
            stopSelfIntent.putExtra("userid", userid);
            stopSelfIntent.putExtra("photo", photo);

            stopSelfIntent.setAction(ACTION_STOP_SERVICE);
            pIntentlogin = PendingIntent.getService(LiveMessageNotificationService.this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            startRingingNotification();


        }


        return START_NOT_STICKY;
    }

    private void startRingingNotification() {


        // openSpotifyIntent.putExtra("songID", songId);


        openProfileIntent = new Intent(LiveMessageNotificationService.this, LiveMessageNotificationService.class);

        openProfileIntent.putExtra("username", username);
        openProfileIntent.putExtra("userid", userid);
        openProfileIntent.putExtra("photo", photo);

        openProfileIntent.setAction(OPEN_PROFILE);
        openProfile = PendingIntent.getService(LiveMessageNotificationService.this, 0, openProfileIntent, PendingIntent.FLAG_CANCEL_CURRENT);


        // openSpotify = PendingIntent.getService(LiveMessageNotificationService.this, 0, openSpotifyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.live_message_request_notification);

        Bitmap imageBitmap = null;


        try {
            imageBitmap = Glide.with(LiveMessageNotificationService.this)
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


        collapsedView.setTextViewText(R.id.notification_message_collapsed, "Live Message Request");
        collapsedView.setTextViewText(R.id.username, username + " wants to live chat");


        collapsedView.setOnClickPendingIntent(R.id.ignore_live, pIntentlogin);
        collapsedView.setOnClickPendingIntent(R.id.accept_live, openProfile);


        Notification notification = new NotificationCompat.Builder(LiveMessageNotificationService.this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                .setCustomContentView(collapsedView)
                .setCustomBigContentView(collapsedView)
                .setAutoCancel(true)
                /*.setContentTitle("Live Message Request")
                .setContentText(username+" wants to live chat")
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                .addAction(R.drawable.ic_cancel_white_24dp, "Stop Music Beacon", pIntentlogin)
                .addAction(R.drawable.spotify_white_icon, "Open in Spotify", openSpotify)*/
                .build();

        if (isWatching == null || !isWatching) {
            startForeground(notificationId, notification);
            startRing();
        }



    }

    private void startRing() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
        i += 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(50);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!stop && i != 25)
                    startRing();
                else {

                    if (!serviceStarted) {
                        Intent startLiveActiveServiceIntent = new Intent(LiveMessageNotificationService.this, LiveMessageActiveService.class);
                        startLiveActiveServiceIntent.putExtra("username", username);
                        startLiveActiveServiceIntent.putExtra("userid", userid);
                        startLiveActiveServiceIntent.putExtra("youtubeID", "default");
                        startLiveActiveServiceIntent.putExtra("photo", photo);

                        startService(startLiveActiveServiceIntent);
                    }
                    stopSelf();

                }
            }
        }, 400);
    }


}
