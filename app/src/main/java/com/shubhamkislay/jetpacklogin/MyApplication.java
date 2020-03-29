package com.shubhamkislay.jetpacklogin;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MyApplication extends Application {
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";

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
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "channel 1",
                    NotificationManager.IMPORTANCE_HIGH);

            channel1.setDescription("This is channel 1");

            NotificationChannel channel2 = new NotificationChannel(
                    CHANNEL_2_ID,
                    "channel 2",
                    NotificationManager.IMPORTANCE_LOW);

            channel2.setDescription("This is channel 2");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
            manager.createNotificationChannel(channel2);

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



}