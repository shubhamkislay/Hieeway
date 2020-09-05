package com.hieeway.hieeway;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.VideoItem;
import com.hieeway.hieeway.Model.YoutubeSync;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import static com.hieeway.hieeway.MyApplication.CHANNEL_1_ID;
import static com.hieeway.hieeway.MyApplication.CHANNEL_3_ID;
import static com.hieeway.hieeway.VerticalPageActivityPerf.isWatching;

public class LiveMessageActiveService extends Service {
    private static String ACTION_STOP_SERVICE = "stop";
    private static String OPEN_PROFILE = "profile";
    private static ValueEventListener videoIdListener;
    private static DatabaseReference videoRef;
    private String userIDchattingwith, username;
    private int notificationId;
    private String lastYoutubeId = "default";
    private Boolean watching = false;
    private PendingIntent stopInent, openIntent;
    private Intent sIntent, oIntent;
    private String photo;
    private String youtubeTitle = null;
    private String youtubeID = "default";
    private boolean loadedVideo = false;
    private int i;
    private boolean serviceStarted;
    private boolean stop = false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        notificationId = MyApplication.NotificationID.getID();

        if (OPEN_PROFILE.equals(intent.getAction())) {

            videoRef.removeEventListener(videoIdListener);

            stop = true;
            loadedVideo = false;

            Intent openIntent = new Intent(this, VerticalPageActivityPerf.class);
            openIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            openIntent.putExtra("username", intent.getStringExtra("username"));
            openIntent.putExtra("userid", intent.getStringExtra("userid"));
            openIntent.putExtra("photo", intent.getStringExtra("photo"));

            if (intent.getExtras() != null) {
                for (String key : intent.getExtras().keySet()) {
                    if (key.equals("youtubeID")) {
                        openIntent.putExtra("youtubeID", intent.getStringExtra("youtubeID"));
                    }

                    if (key.equals("youtubeTitle")) {
                        openIntent.putExtra("youtubeTitle", intent.getStringExtra("youtubeTitle"));
                    }

                }
            }


            openIntent.putExtra("live", "live");

            startActivity(openIntent);

            sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
            stopSelf();
        } else if (ACTION_STOP_SERVICE.equals(intent.getAction())) {

            videoRef.removeEventListener(videoIdListener);

            stop = true;
            loadedVideo = false;

            String userid = intent.getStringExtra("userid");

            try {
                FirebaseDatabase.getInstance().getReference("Video")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userid)
                        .removeValue();
            } catch (Exception e) {
                //
            }

            stopSelf();

        } else {


            userIDchattingwith = intent.getStringExtra("userid");
            username = intent.getStringExtra("username");
            lastYoutubeId = intent.getStringExtra("youtubeID");
            photo = intent.getStringExtra("photo");

            if (intent.getExtras() != null) {
                for (String key : intent.getExtras().keySet()) {
                    if (key.equals("youtubeTitle")) {
                        youtubeTitle = intent.getStringExtra("youtubeTitle");

                    }

                    if (key.equals("loadedVideo")) {
                        loadedVideo = true;
                    }

                }
            }


            String notifMessage = username + " started live message with you";
            if (lastYoutubeId != null && lastYoutubeId.equals("started"))
                notifMessage = "You started Live message with " + username;

            oIntent = new Intent(LiveMessageActiveService.this, LiveMessageActiveService.class);
            oIntent.putExtra("username", username);
            oIntent.putExtra("userid", userIDchattingwith);
            oIntent.putExtra("photo", photo);
            oIntent.putExtra("youtubeID", youtubeID);

            if (youtubeTitle != null)
                oIntent.putExtra("youtubeTitle", youtubeTitle);
            oIntent.setAction(OPEN_PROFILE);


            sIntent = new Intent(LiveMessageActiveService.this, LiveMessageActiveService.class);
            sIntent.putExtra("userid", userIDchattingwith);
            sIntent.setAction(ACTION_STOP_SERVICE);


            openIntent = PendingIntent.getService(LiveMessageActiveService.this, 0, oIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            stopInent = PendingIntent.getService(LiveMessageActiveService.this, 0, sIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            Notification notification = new NotificationCompat.Builder(LiveMessageActiveService.this, CHANNEL_3_ID)
                    .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                    .setAutoCancel(true)
                    .setContentTitle(notifMessage)
                    .setContentText("Starting chatting and watch videos together")
                    .addAction(R.drawable.ic_cancel_white_24dp, "Leave", stopInent)
                    .addAction(R.drawable.ic_cancel_white_24dp, "Join", openIntent)
                    .setPriority(NotificationCompat.PRIORITY_MIN)
                    .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                    .build();

            startForeground(notificationId, notification);

            fetchYoutubeVideo();
            checkForStatus();


            if (loadedVideo)
                startRing();

        }


        return START_NOT_STICKY;
    }

    private void fetchYoutubeVideo() {

        videoIdListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    YoutubeSync videoItem = dataSnapshot.getValue(YoutubeSync.class);


                    youtubeID = videoItem.getYoutubeID();
                    youtubeTitle = videoItem.getVideoTitle();


                    if (isWatching != null)
                        watching = isWatching;
                    else
                        watching = false;

                    if (!lastYoutubeId.equals(videoItem.getYoutubeID())) {


                        TaskCompletionSource<Bitmap> bitmapTaskCompletionSource = new TaskCompletionSource<>();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {

                                try {
                                    Bitmap bitmap = Glide.with(LiveMessageActiveService.this)
                                            .asBitmap()
                                            .load("https://img.youtube.com/vi/" + videoItem.getYoutubeID() + "/0.jpg")
                                            .submit(512, 512)
                                            .get();

                                    bitmapTaskCompletionSource.setResult(bitmap);
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/


                            }
                        }).start();

                        Task<Bitmap> bitmapTask = bitmapTaskCompletionSource.getTask();

                        bitmapTask.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
                            @Override
                            public void onComplete(@NonNull Task<Bitmap> task) {

                                if (task.isSuccessful()) {

                                    int priority = NotificationCompat.PRIORITY_MAX;
                                    String channelID = CHANNEL_1_ID;
                                    if (watching) {
                                        priority = NotificationCompat.PRIORITY_MIN;
                                        channelID = CHANNEL_3_ID;
                                    }


                                    RemoteViews collapsedView = new RemoteViews(getPackageName(), R.layout.youtube_video_notification_collapsed);
                                    RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.youtube_video_notification_expanded);

                                    collapsedView.setTextViewText(R.id.notification_message_collapsed, "You are live with " + username);
                                    collapsedView.setTextViewText(R.id.artist_name, videoItem.getVideoTitle());
                                    collapsedView.setImageViewBitmap(R.id.logo, task.getResult());


                                    expandedView.setTextViewText(R.id.notification_message_collapsed, "You are live with " + username);
                                    expandedView.setTextViewText(R.id.artist_name, videoItem.getVideoTitle());
                                    expandedView.setImageViewBitmap(R.id.logo, task.getResult());
                                    expandedView.setOnClickPendingIntent(R.id.logo, openIntent);

                                    expandedView.setOnClickPendingIntent(R.id.open_spotify, openIntent);
                                    expandedView.setOnClickPendingIntent(R.id.stop_beacon, stopInent);
                                    Notification notification = null;


                                    notification = new NotificationCompat.Builder(LiveMessageActiveService.this, channelID)
                                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                            //.setAutoCancel(true)
                                            .setCustomContentView(collapsedView)
                                            .setCustomBigContentView(expandedView)
                                            /*.setContentTitle("You are live with " + username)
                                            .setContentText(videoItem.getVideoTitle())
                                            .setContentInfo(videoItem.getVideoTitle())
                                            .setLargeIcon(task.getResult())
                                            .addAction(R.drawable.ic_cancel_white_24dp, "Close", stopInent)
                                            .addAction(R.drawable.youtube_btn, "Join Live", openIntent)*/
                                            .setPriority(priority)
                                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                            .build();


                                    if (!watching)
                                        startForeground(notificationId, notification);

                                    lastYoutubeId = videoItem.getYoutubeID();
                                }

                            }
                        });
                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        videoRef = FirebaseDatabase.getInstance().getReference("Video")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIDchattingwith);
        videoRef.addValueEventListener(videoIdListener);
        videoRef.onDisconnect().removeValue();
    }

    private void checkForStatus() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (watching)
                    stopSelf();
                else
                    checkForStatus();
            }
        }, 300);
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
                    loadedVideo = false;
                    //stopSelf();
                }
            }
        }, 400);
    }


}
