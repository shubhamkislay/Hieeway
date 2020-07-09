package com.hieeway.hieeway;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.palette.graphics.Palette;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


import static com.hieeway.hieeway.MyApplication.CHANNEL_1_ID;
import static com.hieeway.hieeway.MyApplication.notificationIDHashMap;

public class MyMessagingService extends FirebaseMessagingService {


    private String TAG = "Log";
    private static int messageNotificationCount = 1;
    Bitmap bitmap = null;
    int notificationBackGroundColor = Color.RED;
    int notificationTitleColor = Color.RED;
    boolean setColorized = false;
    RemoteViews collapsedView;


    Context context;
    private Palette.Swatch vibrantSwatch;
    private Palette.Swatch mutedSwatch;
    private Palette.Swatch lightVibrantSwatch;
    private Palette.Swatch darkVibrantSwatch;
    private Palette.Swatch dominantSwatch;
    private Palette.Swatch lightMutedSwatch;
    private Palette.Swatch darkMutedSwatch;
    private int swatchNumber;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // super.onMessageReceived(remoteMessage);


        //  if(SettingsFragment.getReceiceNotification()){ //if user wants to receive notification

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (remoteMessage.getData().get("type").equals("message")
                    && remoteMessage.getData().get("live").equals("no"))
                sendMessageNotification(remoteMessage);
            else if (remoteMessage.getData().get("type").equals("message")
                    && remoteMessage.getData().get("live").equals("yes"))
                sendLiveNotification(remoteMessage);
            else if (remoteMessage.getData().get("type").equals("request"))
                sendFriendRequestNotification(remoteMessage);
            else if (remoteMessage.getData().get("type").equals("requestaccepted"))
                sendFriendRequestAcceptedNotification(remoteMessage);
            else if (remoteMessage.getData().get("type").equals("revealrequest"))
                sendMessageRevealRequestedNotification(remoteMessage);
            else if (remoteMessage.getData().get("type").equals("revealrequestaccepted"))
                sendMessageRevealRequestAcceptedNotification(remoteMessage);
            else //if(remoteMessage.getData().get("type").equals("feeling"))
                sendFeelingNotification(remoteMessage);

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
                // scheduleJob();requestaccepted revealrequest
            } else {
                // Handle message within 10 seconds
                // handleNow();
            }

        } else {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + this.getPackageName() + "/" + R.raw.chin_up);

            /*RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.push_notification_layout);

            remoteViews.setImageViewResource(R.id.push_notif_icon,R.mipmap.ic_bird_black);*/

            // Intent intent = new Intent(this, .class);
            // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_1_ID);
            // notificationBuilder.setContent(remoteViews);
            notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());

            notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setColor(Color.parseColor("#29A8CF"));
            // notificationBuilder.setSmallIcon(R.drawable.hieeway_logo_splash);
            notificationBuilder.setSmallIcon(R.mipmap.ic_hieeway_logo);
            //notificationBuilder.setContentIntent(pendingIntent);


         /*   remoteViews.setTextViewText(R.id.push_title, "Radyo Türkkuşu");
            remoteViews.setTextViewText(R.id.push_context, remoteMessage.getNotification().getBody());*/
            //notificationBuilder.setLights (ContextCompat.getColor(MainActivity.context, R.color.pushColor), 5000, 5000);
            notificationManager.notify(1, notificationBuilder.build());
            //    }
        }

    }

    private void sendMessageRevealRequestAcceptedNotification(RemoteMessage remoteMessage) {
        context = this;
        Intent intent;

        int id = MyApplication.NotificationID.getID();
        String userValueIntentExtra;

        userValueIntentExtra = remoteMessage.getData().get("userId") + "revealrequestaccepted";

        if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "revealrequestaccepted")) {
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "revealrequestaccepted", id);
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberrevealrequestaccepted", 1);

            messageNotificationCount = 1;
        } else {
            int newCount = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "numberrevealrequestaccepted");
            id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "revealrequestaccepted");
            messageNotificationCount = newCount + 1;
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberrevealrequestaccepted", messageNotificationCount);
        }

        intent = new Intent(this, VerticalPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("userid", remoteMessage.getData().get("userId"));
        intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
        intent.putExtra("live", "no");
        intent.putExtra("revealmessage", "yes");
        intent.putExtra("userValueIntentExtra", userValueIntentExtra);
        intent.putExtra("messageID", remoteMessage.getData().get("messageId"));

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_message_notification);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_message_notification);


        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/

            collapsedView.setImageViewBitmap(R.id.logo, bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        NotificationChannel notificationChannel;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nm", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }


            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);


            Notification summaryNotificationBuilder;


            summaryNotificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(messageNotificationCount + " messages revealed by " + remoteMessage.getData().get("username"))
                            //.setCustomContentView(collapsedView)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            .setGroup(remoteMessage.getData().get("userId"))
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent).build();


            // Since android Oreo notification channel is needed.
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/

            //int id =NotificationID.getID();
            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
            //  ++messageNotificationCount;
            if (messageNotificationCount > 1) {
                notificationManager.notify(id/* ID of notification */, summaryNotificationBuilder);
            }


        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            //.setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);


            Notification summaryNotificationBuilder;


            summaryNotificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(messageNotificationCount + " messages revealed by " + remoteMessage.getData().get("username"))
                            //.setCustomContentView(collapsedView)
                            //.setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            .setGroup(remoteMessage.getData().get("userId"))
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent).build();


            // Since android Oreo notification channel is needed.
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/

            //int id =NotificationID.getID();
            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
            //  ++messageNotificationCount;
            if (messageNotificationCount > 1) {
                notificationManager.notify(id/* ID of notification */, summaryNotificationBuilder);
            }


        }
    }

    private void sendMessageRevealRequestedNotification(RemoteMessage remoteMessage) {
        context = this;
        Intent intent;

        int id = MyApplication.NotificationID.getID();
        String userValueIntentExtra;

        userValueIntentExtra = remoteMessage.getData().get("userId") + "revealrequest";

        if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "revealrequest")) {
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "revealrequest", id);
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberrevealrequest", 1);

            messageNotificationCount = 1;
        } else {
            int newCount = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "numberrevealrequest");
            id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "revealrequest");
            messageNotificationCount = newCount + 1;
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberrevealrequest", messageNotificationCount);
        }

        intent = new Intent(this, VerticalPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("userid", remoteMessage.getData().get("userId"));
        intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
        intent.putExtra("live", remoteMessage.getData().get("live"));
        intent.putExtra("revealmessage", "no");
        intent.putExtra("userValueIntentExtra", userValueIntentExtra);

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_message_notification);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_message_notification);


        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/

            collapsedView.setImageViewBitmap(R.id.logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        NotificationChannel notificationChannel;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nm", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }


            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);


            Notification summaryNotificationBuilder;


            summaryNotificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(messageNotificationCount + " message reveal requests from " + remoteMessage.getData().get("username"))
                            //.setCustomContentView(collapsedView)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            .setGroup(remoteMessage.getData().get("userId"))
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent).build();


            // Since android Oreo notification channel is needed.
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/

            //int id =NotificationID.getID();
            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
            //  ++messageNotificationCount;
            if (messageNotificationCount > 1) {
                notificationManager.notify(id/* ID of notification */, summaryNotificationBuilder);
            }


        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            //.setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);


            Notification summaryNotificationBuilder;


            summaryNotificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(messageNotificationCount + " message reveal requests from " + remoteMessage.getData().get("username"))
                            //.setCustomContentView(collapsedView)
                            //.setLargeIcon(bitmap)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .setBigContentTitle(remoteMessage.getData().get("label"))
                                    .bigText(remoteMessage.getData().get("content")))
                            .setGroup(remoteMessage.getData().get("userId"))
                            .setGroupSummary(true)
                            .setContentIntent(pendingIntent).build();


            // Since android Oreo notification channel is needed.
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/

            //int id =NotificationID.getID();
            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
            //  ++messageNotificationCount;
            if (messageNotificationCount > 1) {
                notificationManager.notify(id/* ID of notification */, summaryNotificationBuilder);
            }


        }

    }

    private void sendFriendRequestAcceptedNotification(RemoteMessage remoteMessage) {
        context = this;
        Intent intent;

        int id = MyApplication.NotificationID.getID();
        String userValueIntentExtra;


        userValueIntentExtra = remoteMessage.getData().get("userId") + "accept";

        if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "accept")) {
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "accept", id);
        } else {
            id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "accept");
        }

        intent = new Intent(this, VerticalPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("userid", remoteMessage.getData().get("userId"));
        intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
        intent.putExtra("live", remoteMessage.getData().get("live"));
        intent.putExtra("userValueIntentExtra", userValueIntentExtra);
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        NotificationChannel notificationChannel;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nm", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }


            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);

            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);

            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
        }


    }

    private void sendFriendRequestNotification(RemoteMessage remoteMessage) {
        context = this;
        Intent intent;

        int id = MyApplication.NotificationID.getID();
        String userValueIntentExtra;


        userValueIntentExtra = remoteMessage.getData().get("userId") + "request";

        if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "request")) {
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "request", id);
        } else {
            id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "request");
        }

        intent = new Intent(this, RequestTrackerActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        /*intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("userid", remoteMessage.getData().get("userId"));
        intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
        intent.putExtra("live", remoteMessage.getData().get("live"));
        intent.putExtra("userValueIntentExtra", userValueIntentExtra);*/
        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        NotificationChannel notificationChannel;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nm", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }


            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);

            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText(remoteMessage.getData().get("content"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);

            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
        }


    }

    private void sendLiveNotification(RemoteMessage remoteMessage) {
        context = this;

        Intent intent;
        intent = new Intent(this, VerticalPageActivity.class);



        collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_message_notification);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_message_notification);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/

            collapsedView.setImageViewBitmap(R.id.logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;


        NotificationChannel notificationChannel;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nr", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }

            int id = MyApplication.NotificationID.getID();


            if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "live"))
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "live", id);
            else
                id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "live");

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("username", remoteMessage.getData().get("username"));
            intent.putExtra("userid", remoteMessage.getData().get("userId"));
            intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
            intent.putExtra("live", remoteMessage.getData().get("live"));
            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notificationManager.createNotificationChannel(notificationChannel);


            notificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText("Tap to accept")
                            //.setCont
                            .setLargeIcon(bitmap)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            //.setAutoCancel(true)
                            /*.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                            .setSound(defaultSoundUri)*/
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            //.setOngoing(true)
                            //.setDefaults(Notification.FLAG_INSISTENT|Notification.DEFAULT_VIBRATE)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);





            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
        } else {

            int id = MyApplication.NotificationID.getID();


            if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "live"))
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "live", id);
            else
                id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "live");

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("username", remoteMessage.getData().get("username"));
            intent.putExtra("userid", remoteMessage.getData().get("userId"));
            intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
            intent.putExtra("live", remoteMessage.getData().get("live"));
            PendingIntent pendingIntent;
            pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            notificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle(remoteMessage.getData().get("label"))
                            .setContentText("Tap to accept")
                            //.setCont
                            .setLargeIcon(bitmap)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            //.setAutoCancel(true)
                            .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            .setPriority(NotificationCompat.PRIORITY_MAX)
                            .setOngoing(true)
                            //.setDefaults(Notification.FLAG_INSISTENT|Notification.DEFAULT_VIBRATE)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);
            ;





            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
        }


    }

    private void sendMessageNotification(final RemoteMessage remoteMessage) {



        /*final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");*/

        // mediaSessionCompat = new MediaSessionCompat(getBaseContext(),"mediaSession");

        context = this;
        Intent intent;

        int id = MyApplication.NotificationID.getID();
        String userValueIntentExtra;

        if (remoteMessage.getData().get("reply").equals("no")) {
            userValueIntentExtra = remoteMessage.getData().get("userId") + "numbersent";
            if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "sent")) {
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "sent", id);
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numbersent", 1);

                messageNotificationCount = 1;
            } else {
                int newCount = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "numbersent");
                id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "sent");
                messageNotificationCount = newCount + 1;
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numbersent", messageNotificationCount);
            }

        } else {
            userValueIntentExtra = remoteMessage.getData().get("userId") + "numberreply";
            if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "reply")) {
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "reply", id);
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberreply", 1);
                messageNotificationCount = 1;
            } else {
                int newCount = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "numberreply");
                id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "reply");
                messageNotificationCount = newCount + 1;
                notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberreply", messageNotificationCount);
            }
        }

        intent = new Intent(this, VerticalPageActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("username", remoteMessage.getData().get("username"));
        intent.putExtra("userid", remoteMessage.getData().get("userId"));
        intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
        intent.putExtra("live", remoteMessage.getData().get("live"));

        intent.putExtra("userValueIntentExtra", userValueIntentExtra);

        PendingIntent pendingIntent;
        pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        collapsedView = new RemoteViews(getPackageName(), R.layout.collapsed_message_notification);
        RemoteViews expandedView = new RemoteViews(getPackageName(), R.layout.expanded_message_notification);


        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

            /*bitmap = BitmapFactory.decodeResource(getResources(),
                    R.drawable.profile_pic);*/

            collapsedView.setImageViewBitmap(R.id.logo, bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }





/*        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {

                Palette.Swatch currentSwatch = null;

                vibrantSwatch = palette.getVibrantSwatch();
                darkVibrantSwatch = palette.getDarkVibrantSwatch();
                lightVibrantSwatch = palette.getLightVibrantSwatch();
                mutedSwatch = palette.getMutedSwatch();
                darkMutedSwatch = palette.getDarkMutedSwatch();
                lightMutedSwatch = palette.getLightMutedSwatch();
                dominantSwatch = palette.getDominantSwatch();

               // Toast.makeText(getBaseContext(),"Swatches Size: "+palette.getSwatches().size(),Toast.LENGTH_SHORT).show();
                if(vibrantSwatch!=null)
                    currentSwatch = vibrantSwatch;
                else if(darkVibrantSwatch!=null)
                    currentSwatch = darkVibrantSwatch;
                else if(lightVibrantSwatch!=null)
                    currentSwatch = lightVibrantSwatch;
                else if(mutedSwatch!=null )
                    currentSwatch = mutedSwatch;
                else if(darkMutedSwatch!=null)
                    currentSwatch = darkMutedSwatch;
                else if(lightMutedSwatch!=null)
                    currentSwatch = lightMutedSwatch;
                else if(dominantSwatch!=null)
                    currentSwatch = dominantSwatch;

                if(currentSwatch != null){
                    int titleColor = currentSwatch.getRgb();
                    notificationTitleColor = titleColor;
                    notificationBackGroundColor = currentSwatch.getRgb();
                    setColorized = true;
                    // ...
                }
                else
                {
                    notificationBackGroundColor = Color.YELLOW;
                    setColorized = true;

                }

                collapsedView.setTextViewText(R.id.notification_message_collapsed,remoteMessage.getData().get("label"));
                collapsedView.setInt(R.id.background_fade, "setBackgroundTint",
                        notificationTitleColor);
              //  collapsedView.setT


            }
        });*/


        Date parsedDate = Calendar.getInstance().getTime();


        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

        // S is the millisecond
        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy' 'HH:mm:ss:S");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");


        // sendMessageViewHolder.timestamp.setText("" + simpleDateFormat.format(timestamp));
        collapsedView.setTextViewText(R.id.timestamp_textview, simpleDateFormat.format(timestamp));

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;

        NotificationChannel notificationChannel;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nm", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }


            notificationManager.createNotificationChannel(notificationChannel);

            notificationBuilder =
                    new NotificationCompat.Builder(context, notificationChannel.getId())
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle("You got a message")
                            .setContentText(remoteMessage.getData().get("label"))
                            //.setCont
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);


            Notification summaryNotificationBuilder;

            if (remoteMessage.getData().get("reply").equals("no")) {


                summaryNotificationBuilder =
                        new NotificationCompat.Builder(context, notificationChannel.getId())
                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                .setContentTitle("You have got messages")
                                .setContentText(messageNotificationCount + " messages from " + remoteMessage.getData().get("username"))
                                //.setCustomContentView(collapsedView)
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                .setLargeIcon(bitmap)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                .setGroup(remoteMessage.getData().get("userId"))
                                .setGroupSummary(true)
                                .setContentIntent(pendingIntent).build();
            } else {


                summaryNotificationBuilder =
                        new NotificationCompat.Builder(context, notificationChannel.getId())
                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                //.setCustomContentView(collapsedView)
                                .setContentTitle("You have got messages")
                                .setContentText(messageNotificationCount + " replies from " + remoteMessage.getData().get("username"))
                                .setLargeIcon(bitmap)
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                .setGroup(remoteMessage.getData().get("userId"))
                                .setGroupSummary(true)
                                .setContentIntent(pendingIntent).build();
            }


            // Since android Oreo notification channel is needed.
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/

            //int id =NotificationID.getID();
            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
            //  ++messageNotificationCount;
            if (messageNotificationCount > 1) {
                if (remoteMessage.getData().get("reply").equals("no"))
                    collapsedView.setTextViewText(R.id.notification_message_collapsed, remoteMessage.getData().get("username") + " has sent you " + (messageNotificationCount) + " messages");
                else
                    collapsedView.setTextViewText(R.id.notification_message_collapsed, remoteMessage.getData().get("username") + " replied to " + (messageNotificationCount) + " of your messages");

                notificationManager.notify(id/* ID of notification */, summaryNotificationBuilder);
            }


        } else {
            notificationBuilder =
                    new NotificationCompat.Builder(context, CHANNEL_1_ID)
                            .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                            //.setCustomContentView(collapsedView)
                            .setContentTitle("You got a message")
                            .setContentText(remoteMessage.getData().get("label"))
                            //.setCont
                            .setLargeIcon(bitmap)
                            //.addAction(R.drawable.ic_action_chat_bubble,"Open",null)
                            .setColor(getResources().getColor(R.color.colorPrimaryDark))
                            .setColorized(true)
                            .setAutoCancel(true)
                            .setSound(defaultSoundUri)
                            .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                            //.setPriority(NotificationCompat.PRIORITY_MAX)
                            /*.setStyle(new NotificationCompat.BigTextStyle()
                            .setBigContentTitle(remoteMessage.getData().get("label")))*/

                            //  .setShowActionsInCompactView(1))
                            //.setMediaSession(MediaSessionCompat.Token))
                            //.setGroup(remoteMessage.getData().get("userId"))
                            .setContentIntent(pendingIntent);


            if (remoteMessage.getData().get("reply").equals("no")) {
                if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "sent")) {
                    notificationIDHashMap.put(remoteMessage.getData().get("userId") + "sent", id);
                    notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numbersent", 1);
                    messageNotificationCount = 1;
                } else {
                    int newCount = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "numbersent");
                    id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "sent");
                    messageNotificationCount = newCount + 1;
                    notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numbersent", messageNotificationCount);
                }

            } else {
                if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "reply")) {
                    notificationIDHashMap.put(remoteMessage.getData().get("userId") + "reply", id);
                    notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberreply", 1);
                    messageNotificationCount = 1;
                } else {
                    int newCount = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "numberreply");
                    id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "reply");
                    messageNotificationCount = newCount + 1;
                    notificationIDHashMap.put(remoteMessage.getData().get("userId") + "numberreply", messageNotificationCount);
                }
            }


            Notification summaryNotificationBuilder;

            if (remoteMessage.getData().get("reply").equals("no")) {


                summaryNotificationBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                .setContentTitle("You have got messages")
                                .setContentText(messageNotificationCount + " messages from " + remoteMessage.getData().get("username"))
                                //.setCustomContentView(collapsedView)
                                .setLargeIcon(bitmap)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                .setGroup(remoteMessage.getData().get("userId"))
                                .setGroupSummary(true)
                                .setContentIntent(pendingIntent).build();
            } else {


                summaryNotificationBuilder =
                        new NotificationCompat.Builder(context, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                //.setCustomContentView(collapsedView)
                                .setContentTitle("You have got messages")
                                .setContentText(messageNotificationCount + " replies from " + remoteMessage.getData().get("username"))
                                .setLargeIcon(bitmap)
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                .setGroup(remoteMessage.getData().get("userId"))
                                .setGroupSummary(true)
                                .setContentIntent(pendingIntent).build();
            }


            // Since android Oreo notification channel is needed.
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }*/

            //int id =NotificationID.getID();
            notificationManager.notify(id/* ID of notification */, notificationBuilder.build());
            //  ++messageNotificationCount;
            if (messageNotificationCount > 1) {
                if (remoteMessage.getData().get("reply").equals("no"))
                    collapsedView.setTextViewText(R.id.notification_message_collapsed, remoteMessage.getData().get("username") + " has sent you " + (messageNotificationCount) + " messages");
                else
                    collapsedView.setTextViewText(R.id.notification_message_collapsed, remoteMessage.getData().get("username") + " replied to " + (messageNotificationCount) + " of your messages");

                notificationManager.notify(id/* ID of notification */, summaryNotificationBuilder);
            }


        }

    }

    private void sendFeelingNotification(RemoteMessage remoteMessage) {



        /*final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");*/

        Bitmap bitmap = null;

        try {
            bitmap = Glide.with(this)
                    .asBitmap()
                    .load(remoteMessage.getData().get("userPhoto").replace("s96-c", "s384-c"))
                    .submit(512, 512)
                    .get();

        } catch (Exception e) {
            e.printStackTrace();
        }


        int id = MyApplication.NotificationID.getID();

        if (!notificationIDHashMap.containsKey(remoteMessage.getData().get("userId") + "feeling")) {
            notificationIDHashMap.put(remoteMessage.getData().get("userId") + "feeling", id);
        } else {
            id = (int) notificationIDHashMap.get(remoteMessage.getData().get("userId") + "feeling");
        }

        NotificationCompat.Builder notificationBuilder;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationChannel notificationChannel;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel =
                    new NotificationChannel("ch_nm", "CHART", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                notificationChannel.setSound(defaultSoundUri, att);
            }


            notificationManager.createNotificationChannel(notificationChannel);
            if (remoteMessage.getData().get("default").equals("no")) {


                Intent intent = new Intent(this, VerticalPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("username", remoteMessage.getData().get("username"));
                intent.putExtra("userid", remoteMessage.getData().get("userId"));
                intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
                intent.putExtra("live", remoteMessage.getData().get("live"));
                intent.putExtra("default", "no");
                intent.putExtra("feeling", remoteMessage.getData().get("feeling"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                        PendingIntent.FLAG_ONE_SHOT);


                // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


                notificationBuilder =
                        new NotificationCompat.Builder(this, notificationChannel.getId())
                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                .setContentTitle(remoteMessage.getData().get("label"))
                                .setContentText(remoteMessage.getData().get("content"))
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                // .setLargeIcon(bitmap)
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .setBigContentTitle(remoteMessage.getData().get("label"))
                                        .bigText(remoteMessage.getData().get("content")))
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);


                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(id /* ID of notification */, notificationBuilder.build());


            } else {


                Intent intent = new Intent(this, VerticalPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("username", remoteMessage.getData().get("username"));
                intent.putExtra("userid", remoteMessage.getData().get("userId"));
                intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
                intent.putExtra("live", remoteMessage.getData().get("live"));
                intent.putExtra("default", "yes");
                intent.putExtra("feeling", remoteMessage.getData().get("feeling"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                if (remoteMessage.getData().get("feeling").equals("happy")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_happy)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("bored")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_bored)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("sad")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_sad)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("angry")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_angry)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("excited")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_excited)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("confused")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_confused)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else {

                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, notificationChannel.getId())
                                    .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setLargeIcon(bitmap)
                                    // .setAutoCancel(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    .setPriority(NotificationCompat.PRIORITY_MAX)

                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());

                }
            }
        } else {
            if (remoteMessage.getData().get("default").equals("no")) {


                Intent intent = new Intent(this, VerticalPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("username", remoteMessage.getData().get("username"));
                intent.putExtra("userid", remoteMessage.getData().get("userId"));
                intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
                intent.putExtra("live", remoteMessage.getData().get("live"));
                intent.putExtra("default", "no");
                intent.putExtra("feeling", remoteMessage.getData().get("feeling"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


                notificationBuilder =
                        new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                .setSmallIcon(R.drawable.ic_stat_hieeway_arrow_title_bar)
                                .setContentTitle(remoteMessage.getData().get("label"))
                                .setContentText(remoteMessage.getData().get("content"))
                                .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                .setColorized(true)
                                .setLargeIcon(bitmap)
                                //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .setBigContentTitle(remoteMessage.getData().get("label"))
                                        .bigText(remoteMessage.getData().get("content")))
                                .setAutoCancel(true)
                                .setSound(defaultSoundUri)
                                .setContentIntent(pendingIntent);


                // Since android Oreo notification channel is needed.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_HIGH);
                    notificationManager.createNotificationChannel(channel);
                }

                notificationManager.notify(id /* ID of notification */, notificationBuilder.build());


            } else {


                Intent intent = new Intent(this, VerticalPageActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("username", remoteMessage.getData().get("username"));
                intent.putExtra("userid", remoteMessage.getData().get("userId"));
                intent.putExtra("photo", remoteMessage.getData().get("userPhoto"));
                intent.putExtra("live", remoteMessage.getData().get("live"));
                intent.putExtra("default", "yes");
                intent.putExtra("feeling", remoteMessage.getData().get("feeling"));
                PendingIntent pendingIntent = PendingIntent.getActivity(this, id /* Request code */, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);


                if (remoteMessage.getData().get("feeling").equals("happy")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_happy)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("bored")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_bored)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("sad")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_sad)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("angry")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_angry)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("excited")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_excited)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else if (remoteMessage.getData().get("feeling").equals("confused")) {

                    // Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.drawable.ic_emoticon_feeling_confused)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setStyle(new androidx.media.app.NotificationCompat.MediaStyle())
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    //.setLargeIcon(bitmap)
                                    .setAutoCancel(true)
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());
                } else {

                    String mipMap = remoteMessage.getData().get("icon");
                    notificationBuilder =
                            new NotificationCompat.Builder(this, CHANNEL_1_ID)
                                    .setSmallIcon(R.mipmap.ic_hieeway_logo)
                                    .setContentTitle(remoteMessage.getData().get("label"))
                                    .setContentText(remoteMessage.getData().get("content"))
                                    .setColor(getResources().getColor(R.color.colorPrimaryDark))
                                    .setColorized(true)
                                    //.setLargeIcon(bitmap)
                                    // .setAutoCancel(true)
                                    .setStyle(new NotificationCompat.BigTextStyle()
                                            .setBigContentTitle(remoteMessage.getData().get("label"))
                                            .bigText(remoteMessage.getData().get("content")))
                                    .setPriority(NotificationCompat.PRIORITY_MAX)

                                    .setSound(defaultSoundUri)
                                    .setContentIntent(pendingIntent);


                    // Since android Oreo notification channel is needed.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_1_ID,
                                "Channel human readable title",
                                NotificationManager.IMPORTANCE_DEFAULT);
                        notificationManager.createNotificationChannel(channel);
                    }

                    notificationManager.notify(id /* ID of notification */, notificationBuilder.build());

                }
            }
        }





    }


    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
    }

    // Generate palette asynchronously and use it on a different
// thread using onGenerated()
    public void createPaletteAsync(Bitmap bitmap) {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            public void onGenerated(Palette p) {
                // Use generated instance
            }
        });
    }
}
