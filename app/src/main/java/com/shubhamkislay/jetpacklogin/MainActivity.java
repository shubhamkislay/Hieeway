package com.shubhamkislay.jetpacklogin;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_1_ID;
import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity {


    Button loginBtn, registerBtn;
    FirebaseAuth firebaseAuth;

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        notificationManager = NotificationManagerCompat.from(this);
        editTextTitle = findViewById(R.id.edit_text_title);

        editTextMessage = findViewById(R.id.edit_text_message);*/


        firebaseAuth = FirebaseAuth.getInstance();

        loginBtn = findViewById(R.id.login);
        registerBtn = findViewById(R.id.register);









        if(firebaseAuth.getCurrentUser()!=null)
        {
            //startActivity(new Intent(MainActivity.this,AlphaActivity.class));
            startActivity(new Intent(MainActivity.this,NavButtonTest.class));
            finish();
        }
        else
        {

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();

                }
            });

            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                    finish();
                }
            });


        }

    }

/*

    public void sendOnChannel1(View v) {

        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Intent activityIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);


        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);

        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_head)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                */
/*.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.big_text))
                        .setBigContentTitle("Bigggg")
                        .setSummaryText("Summary"))*//*

                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigLargeIcon(null)
                        .bigPicture(largeIcon))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
               // .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
               // .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();


        notificationManager.notify(1, notification);

    }

    public void sendOnChannel2(View v) {

        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.hiewaylogomessageback);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(title)
                .setContentText(message)
                */
/*.setStyle(new NotificationCompat.InboxStyle()
                        .addLine("This is line 1")
                        .addLine("This is line 2")
                        .addLine("This is line 3")
                        .addLine("This is line 4")
                        .addLine("This is line 5")
                        .addLine("This is line 6")
                        .addLine("This is line 7")
                        .addLine("This is line 8")
                        .setBigContentTitle("Bigggg")
                        .setSummaryText("Summary")
                )*//*

                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();


        notificationManager.notify(2, notification);

    }
*/

}
