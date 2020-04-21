package com.shubhamkislay.jetpacklogin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_1_ID;

public class MyMessagingService extends FirebaseMessagingService {


    private String TAG = "Log";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);

        //  if(SettingsFragment.getReceiceNotification()){ //if user wants to receive notification

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (remoteMessage.getData().get("type").equals("message"))
                sendMessageNotification(remoteMessage);
            else //if(remoteMessage.getData().get("type").equals("feeling"))
                sendFeelingNotification(remoteMessage);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // scheduleJob();
            } else {
                // Handle message within 10 seconds
                // handleNow();
            }

        } else {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.chin_up);

            /*RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.push_notification_layout);

            remoteViews.setImageViewResource(R.id.push_notif_icon,R.mipmap.ic_bird_black);*/

            Intent intent = new Intent(this, SwipeButtonActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID);
            // notificationBuilder.setContent(remoteViews);
            notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());

            notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setColor(Color.parseColor("#29A8CF"));
            // notificationBuilder.setSmallIcon(R.drawable.hieeway_logo_splash);
            notificationBuilder.setSmallIcon(R.mipmap.ic_hieeway_logo);
            notificationBuilder.setContentIntent(pendingIntent);

         /*   remoteViews.setTextViewText(R.id.push_title, "Radyo Türkkuşu");
            remoteViews.setTextViewText(R.id.push_context, remoteMessage.getNotification().getBody());*/
            //notificationBuilder.setLights (ContextCompat.getColor(MainActivity.context, R.color.pushColor), 5000, 5000);
            notificationManager.notify(1, notificationBuilder.build());
            //    }
        }

    }

    private void sendMessageNotification(RemoteMessage remoteMessage) {



        /*final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");*/

        Intent intent = new Intent(this, VerticalPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("userid", remoteMessage.getData().get("userId"));
        intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
        intent.putExtra("live", remoteMessage.getData().get("live"));
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, CHANNEL_1_ID)
                        .setSmallIcon(R.mipmap.ic_hieeway_logo)
                        .setContentTitle(remoteMessage.getData().get("label"))
                        .setContentText(remoteMessage.getData().get("content"))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void sendFeelingNotification(RemoteMessage remoteMessage) {



        /*final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");*/


        if (remoteMessage.getData().get("default").equals("no")) {


            Intent intent = new Intent(this, VerticalPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("username", remoteMessage.getData().get("username"));
            intent.putExtra("userid", remoteMessage.getData().get("userId"));
            intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
            intent.putExtra("live", remoteMessage.getData().get("live"));
            intent.putExtra("default", "no");
            intent.putExtra("feeling", remoteMessage.getData().get("feeling"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);


            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, CHANNEL_1_ID)
                            .setSmallIcon(R.mipmap.ic_hieeway_logo)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                        "Channel human readable title",
                        NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
        } else {


            Intent intent = new Intent(this, VerticalPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("username", remoteMessage.getData().get("username"));
            intent.putExtra("userid", remoteMessage.getData().get("userId"));
            intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
            intent.putExtra("live", remoteMessage.getData().get("live"));
            intent.putExtra("default", "yes");
            intent.putExtra("feeling", remoteMessage.getData().get("feeling"));
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            if (remoteMessage.getData().get("feeling").equals("happy")) {

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                String mipMap = remoteMessage.getData().get("icon");
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.emoticon_feeling_happy)
                                .setContentTitle(remoteMessage.getData().get("label"))
                                .setContentText(remoteMessage.getData().get("content"))
                                .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setGroup("feelingcustom")
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
            } else {
                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                String mipMap = remoteMessage.getData().get("icon");
                NotificationCompat.Builder notificationBuilder =
                        new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                .setSmallIcon(R.mipmap.ic_hieeway_logo)
                                .setContentTitle(remoteMessage.getData().get("label"))
                                .setContentText(remoteMessage.getData().get("content"))
                                // .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setGroup("feelingdefault")
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);

                /*NotificationCompat.Builder summaryNotificationBuilder =
                        new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                .setSmallIcon(R.mipmap.ic_hieeway_logo)
                                .setStyle(new NotificationCompat.InboxStyle()
                                .addLine(remoteMessage.getData().get("label") + " " + remoteMessage.getData().get("content"))
                                .setBigContentTitle("2 new messages"))
                                // .setAutoCancel(true)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setGroup("feelingdefault")
                                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);*/

                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
                /* notificationManager.notify(0 *//* ID of notification *//*, notificationBuilder.build());
                notificationManager.notify(0 *//* ID of notification *//*, summaryNotificationBuilder.build());*/
            }
        }

    }
}
