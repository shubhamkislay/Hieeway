package com.shubhamkislay.jetpacklogin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_1_ID;

public class MyMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

      //  if(SettingsFragment.getReceiceNotification()){ //if user wants to receive notification

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +"://" + this.getPackageName() + "/" +R.raw.chin_up);

            /*RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.push_notification_layout);

            remoteViews.setImageViewResource(R.id.push_notif_icon,R.mipmap.ic_bird_black);*/

            Intent intent = new Intent(this,MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent, PendingIntent.FLAG_ONE_SHOT);


            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this,CHANNEL_1_ID);
           // notificationBuilder.setContent(remoteViews);
            notificationBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
            notificationBuilder.setContentText(remoteMessage.getNotification().getBody());

            notificationBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            notificationBuilder.setAutoCancel(true);
            notificationBuilder.setColor(Color.parseColor("#29A8CF"));
            notificationBuilder.setSmallIcon(R.drawable.hieeway_logo_splash);
            notificationBuilder.setContentIntent(pendingIntent);

         /*   remoteViews.setTextViewText(R.id.push_title, "Radyo Türkkuşu");
            remoteViews.setTextViewText(R.id.push_context, remoteMessage.getNotification().getBody());*/
            //notificationBuilder.setLights (ContextCompat.getColor(MainActivity.context, R.color.pushColor), 5000, 5000);
            notificationManager.notify(1,notificationBuilder.build());
    //    }

    }
}
