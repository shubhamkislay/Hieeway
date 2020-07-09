package com.hieeway.hieeway;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MyApplication extends Application {
    public static final String CHANNEL_1_ID = "messages";
    public static final String CHANNEL_2_ID = "feelings";
    public static final String CHANNEL_3_ID = "contacts";
    public static Boolean CONTACT_SERVICE_RUNNUNG = false;
    public static HashMap<String, Object> notificationIDHashMap = new HashMap<>();
    public static Palette.Swatch darkMutedSwatch;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannels();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

            databaseReference.keepSynced(true);
           // checkPresence();
        }


    }

    private void createNotificationChannels() {

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "messages",
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setVibrationPattern(new long[]{300, 300, 300});


            NotificationChannel channel3 = new NotificationChannel(
                    CHANNEL_3_ID,
                    "contacts",
                    NotificationManager.IMPORTANCE_LOW);
            // channel1.setVibrationPattern(new long[]{300, 300, 300});

            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                channel1.setSound(defaultSoundUri, att);
            }

            channel1.setDescription("direct messages");


            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "feelings",
                    NotificationManager.IMPORTANCE_HIGH);
            if (defaultSoundUri != null) {
                AudioAttributes att = new AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build();
                channel2.setSound(defaultSoundUri, att);


            }

            channel2.setDescription("feeling changes");
            channel2.setVibrationPattern(new long[]{300, 300, 300});

            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);
            manager.createNotificationChannel(channel3);

        }
    }

    public void checkPresence()
    {
        final DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    // Log.d(TAG, "connected");
                    // Toast.makeText(getActivity(),"You are connected!",Toast.LENGTH_SHORT).show();

                    // updateUserPresence(false);
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child("online");

                    databaseReference.setValue(true);
                    databaseReference.onDisconnect().setValue(false);


                    //  updateUserPresence(true);




                } else {
                    // Log.d(TAG, "not connected");
                    // Toast.makeText(getActivity(),"You are disconnected!",Toast.LENGTH_SHORT).show();


                    // updateUserPresence(false);







                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log.w(TAG, "Listener was cancelled");
            }
        });
    }

    public static class NotificationID {
        private final static AtomicInteger c = new AtomicInteger(0);

        public static int getID() {
            return c.incrementAndGet();
        }
    }



}