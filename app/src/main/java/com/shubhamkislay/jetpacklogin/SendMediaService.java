package com.shubhamkislay.jetpacklogin;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static br.com.instachat.emojilibrary.model.Emoji.TAG;
import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_3_ID;

public class SendMediaService extends Service {

    private static String ACTION_STOP_SERVICE = "yes";
    public String mKey;
    DatabaseReference databaseReference, receiverReference;
    String userChattingWithId, usernameChattingWith, userphotoUrl, currentUsername, currentUserPhoto, currentUserPublicKeyID, otherUserPublicKeyID, type;
    Uri imageUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    Intent stopSelfIntent;
    private PendingIntent pIntentlogin;

    @Override
    public void onCreate() {
        super.onCreate();


    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (ACTION_STOP_SERVICE.equals(intent.getAction())) {
            Log.d(TAG, "called to cancel service");
            //manager.cancel(NOTIFCATION_ID);

            // stopSelf();

            stopUploading(intent.getStringExtra("messageKey"), intent.getStringExtra("userChattingWithId"));

        } else {

            imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            userChattingWithId = intent.getStringExtra("userChattingWithId");
            usernameChattingWith = intent.getStringExtra("usernameChattingWith");
            userphotoUrl = intent.getStringExtra("userphotoUrl");
            currentUsername = intent.getStringExtra("currentUsername");
            currentUserPhoto = intent.getStringExtra("currentUserPhoto");
            otherUserPublicKeyID = intent.getStringExtra("otherUserPublicKeyID");
            currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
            type = intent.getStringExtra("type");

            databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid())
                    .child(userChattingWithId);

            receiverReference = FirebaseDatabase.getInstance().getReference("Messages")
                    .child(userChattingWithId)
                    .child(FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getUid());

            mKey = databaseReference.push().getKey();


            storageReference = FirebaseStorage.getInstance().getReference("uploads");


            Log.v("CurrentUserId", "" + currentUserPublicKeyID);
            Log.v("OtherUserId", "" + otherUserPublicKeyID);


            Log.v("SendMediaService", "Started");

            stopSelfIntent = new Intent(this, SendMediaService.class);

            stopSelfIntent.setAction(ACTION_STOP_SERVICE);
            stopSelfIntent.putExtra("messageKey", mKey);
            stopSelfIntent.putExtra("userChattingWithId", userChattingWithId);

            //This is optional if you have more than one buttons and want to differentiate between two


            pIntentlogin = PendingIntent.getService(this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);


            // getContactList();
            if (type.equals("photo")) {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                        .setContentTitle("Sending photo")
                        .setSmallIcon(R.drawable.ic_photos)
                        .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                        // .setContentIntent(pendingIntent)
                        .setProgress(0, 0, true)
                        .build();


                startForeground(1, notification);

                uploadImage();
            } else {
                Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                        .setContentTitle("Sending video")
                        .setSmallIcon(R.drawable.ic_videocam_black_24dp)
                        .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                        // .setContentIntent(pendingIntent)
                        .setProgress(0, 0, true)
                        .build();


                startForeground(1, notification);

                uploadVideo();
            }
        }


        return START_NOT_STICKY;
    }

    private void uploadVideo() {
        Log.v("SendMediaService", "Uploading...");
        if (imageUri != null) {


            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        stopSelf();
                        throw task.getException();

                    }


                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if (task.isSuccessful()) {

                        Log.v("SendMediaService", "Upload");

                        Uri downloadUri = task.getResult();

                        String mUri = downloadUri.toString();


                        stopSelfIntent.putExtra("messageKey", mKey);


                        final HashMap<String, Object> sendMessageHash = new HashMap<>();
                        sendMessageHash.put("senderId", FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid());
                        sendMessageHash.put("receiverId", userChattingWithId);
                        sendMessageHash.put("messageId", mKey);
                        sendMessageHash.put("messageText", "");
                        sendMessageHash.put("sentStatus", "sending");
                        sendMessageHash.put("seen", "notseen");
                        sendMessageHash.put("photourl", "none");
                        sendMessageHash.put("audiourl", "none");
                        sendMessageHash.put("publicKeyID", currentUserPublicKeyID);
                        sendMessageHash.put("videourl", mUri);
                        sendMessageHash.put("gotReplyID", "none");
                        sendMessageHash.put("replyTag", false);
                        sendMessageHash.put("replyID", "none");
                        sendMessageHash.put("senderReplyMessage", "none");
                        sendMessageHash.put("ifMessageTwo", false);
                        sendMessageHash.put("messageTextTwo", "");
                        sendMessageHash.put("ifMessageThree", false);
                        sendMessageHash.put("messageTextThree", "");
                        sendMessageHash.put("showReplyMsg", false);
                        sendMessageHash.put("replyMsg", " ");
                        sendMessageHash.put("showGotReplyMsg", false);
                        sendMessageHash.put("gotReplyMsg", " ");

                        final HashMap<String, Object> receiveMessageHash = new HashMap<>();
                        receiveMessageHash.put("senderId", FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid());
                        receiveMessageHash.put("receiverId", userChattingWithId);
                        receiveMessageHash.put("messageId", mKey);
                        receiveMessageHash.put("messageText", "");
                        receiveMessageHash.put("sentStatus", "sending");
                        receiveMessageHash.put("seen", "notseen");
                        receiveMessageHash.put("photourl", "none");
                        receiveMessageHash.put("audiourl", "none");
                        receiveMessageHash.put("publicKeyID", otherUserPublicKeyID);
                        receiveMessageHash.put("videourl", mUri);
                        receiveMessageHash.put("gotReplyID", "none");
                        receiveMessageHash.put("replyTag", false);
                        receiveMessageHash.put("replyID", "none");
                        receiveMessageHash.put("senderReplyMessage", "none");
                        receiveMessageHash.put("ifMessageTwo", false);
                        receiveMessageHash.put("messageTextTwo", "");
                        receiveMessageHash.put("ifMessageThree", false);
                        receiveMessageHash.put("messageTextThree", "");
                        receiveMessageHash.put("showReplyMsg", false);
                        receiveMessageHash.put("replyMsg", " ");
                        receiveMessageHash.put("showGotReplyMsg", false);
                        receiveMessageHash.put("gotReplyMsg", " ");

                        databaseReference.child(mKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sentStatus", "sent");
                                hashMap.put("messageId", mKey);

                                databaseReference.child(mKey).updateChildren(hashMap);
                            }
                        });
                        receiverReference.child(mKey).updateChildren(receiveMessageHash);

                        Log.v("SendMediaService", "Sent");


                        // createChatListItem(usernameChattingWith,userphotoUrl,currentUsername,currentUserPhoto);
                        // progressDialog.dismiss();

                        createChatListItem(usernameChattingWith, userphotoUrl, currentUsername, currentUserPhoto);


                    } else {
                        stopSelf();
                        //Toast.makeText(getBaseContext(),"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        //  progressDialog.dismiss();
                    }

                }
            });


        } else {
            // Toast.makeText(PhotoEditToolsActivity.this,"No Image selected",Toast.LENGTH_SHORT ).show();
        }
    }

    private void stopUploading(String messageKey, String userChattingWithId) {
        //  try{

        // try {
        //  uploadTask.cancel();


        /*FirebaseDatabase.getInstance().getReference("Messages")
                    .child(userChattingWithId)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(messageKey).removeValue();

            FirebaseDatabase.getInstance().getReference("Messages")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userChattingWithId)
                    .child(messageKey).removeValue();*/


        stopSelf();


    }

    private String getExtension(Uri uri) {

        ContentResolver contentResolver = getBaseContext().getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage() {

        /*final ProgressDialog progressDialog = new ProgressDialog(PhotoEditToolsActivity.this );

        progressDialog.setMessage("Uploading");

        progressDialog.show();*/

        Log.v("SendMediaService", "Uploading...");
        if (imageUri != null) {


            final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if (!task.isSuccessful()) {
                        stopSelf();
                        throw task.getException();

                    }


                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if (task.isSuccessful()) {

                        Log.v("SendMediaService", "Upload");

                        Uri downloadUri = task.getResult();

                        String mUri = downloadUri.toString();


                        stopSelfIntent.putExtra("messageKey", mKey);


                        final HashMap<String, Object> sendMessageHash = new HashMap<>();
                        sendMessageHash.put("senderId", FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid());
                        sendMessageHash.put("receiverId", userChattingWithId);
                        sendMessageHash.put("messageId", mKey);
                        sendMessageHash.put("messageText", "");
                        sendMessageHash.put("sentStatus", "sending");
                        sendMessageHash.put("seen", "notseen");
                        sendMessageHash.put("photourl", mUri);
                        sendMessageHash.put("audiourl", "none");
                        sendMessageHash.put("publicKeyID", currentUserPublicKeyID);
                        sendMessageHash.put("videourl", "none");
                        sendMessageHash.put("gotReplyID", "none");
                        sendMessageHash.put("replyTag", false);
                        sendMessageHash.put("replyID", "none");
                        sendMessageHash.put("senderReplyMessage", "none");
                        sendMessageHash.put("ifMessageTwo", false);
                        sendMessageHash.put("messageTextTwo", "");
                        sendMessageHash.put("ifMessageThree", false);
                        sendMessageHash.put("messageTextThree", "");
                        sendMessageHash.put("showReplyMsg", false);
                        sendMessageHash.put("replyMsg", " ");
                        sendMessageHash.put("showGotReplyMsg", false);
                        sendMessageHash.put("gotReplyMsg", " ");

                        final HashMap<String, Object> receiveMessageHash = new HashMap<>();
                        receiveMessageHash.put("senderId", FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getUid());
                        receiveMessageHash.put("receiverId", userChattingWithId);
                        receiveMessageHash.put("messageId", mKey);
                        receiveMessageHash.put("messageText", "");
                        receiveMessageHash.put("sentStatus", "sending");
                        receiveMessageHash.put("seen", "notseen");
                        receiveMessageHash.put("photourl", mUri);
                        receiveMessageHash.put("audiourl", "none");
                        receiveMessageHash.put("publicKeyID", otherUserPublicKeyID);
                        receiveMessageHash.put("videourl", "none");
                        receiveMessageHash.put("gotReplyID", "none");
                        receiveMessageHash.put("replyTag", false);
                        receiveMessageHash.put("replyID", "none");
                        receiveMessageHash.put("senderReplyMessage", "none");
                        receiveMessageHash.put("ifMessageTwo", false);
                        receiveMessageHash.put("messageTextTwo", "");
                        receiveMessageHash.put("ifMessageThree", false);
                        receiveMessageHash.put("messageTextThree", "");
                        receiveMessageHash.put("showReplyMsg", false);
                        receiveMessageHash.put("replyMsg", " ");
                        receiveMessageHash.put("showGotReplyMsg", false);
                        receiveMessageHash.put("gotReplyMsg", " ");

                        databaseReference.child(mKey).updateChildren(sendMessageHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                HashMap<String, Object> hashMap = new HashMap<>();
                                hashMap.put("sentStatus", "sent");
                                hashMap.put("messageId", mKey);

                                databaseReference.child(mKey).updateChildren(hashMap);
                            }
                        });
                        receiverReference.child(mKey).updateChildren(receiveMessageHash);

                        Log.v("SendMediaService", "Sent");


                        // createChatListItem(usernameChattingWith,userphotoUrl,currentUsername,currentUserPhoto);
                        // progressDialog.dismiss();

                        createChatListItem(usernameChattingWith, userphotoUrl, currentUsername, currentUserPhoto);


                    } else {
                        stopSelf();
                        //Toast.makeText(getBaseContext(),"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        //  progressDialog.dismiss();
                    }

                }
            });


        } else {
            // Toast.makeText(PhotoEditToolsActivity.this,"No Image selected",Toast.LENGTH_SHORT ).show();
        }


    }

    public void createChatListItem(final String usernameUserChattingWith, final String userChattingWith_photo, final String currentUserName, final String currentUserPhoto) {

        Long tsLong = System.currentTimeMillis() / 1000;
        final String ts = tsLong.toString();

        DatabaseReference senderChatCreateRef, receiverChatCreateRef;

        senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userChattingWithId);

        receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userChattingWithId).child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        final HashMap<String, Object> timeStampHash = new HashMap<>();
        timeStampHash.put("timeStamp", ts);
        timeStampHash.put("id", userChattingWithId);
        timeStampHash.put("username", usernameUserChattingWith);
        timeStampHash.put("photo", userChattingWith_photo);
        timeStampHash.put("seen", "notseen");
        timeStampHash.put("chatPending", false);
        senderChatCreateRef.updateChildren(timeStampHash);

        HashMap<String, Object> timeStampHashReceiver = new HashMap<>();

        timeStampHashReceiver.put("timeStamp", ts);
        timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
        timeStampHashReceiver.put("username", currentUserName);
        timeStampHashReceiver.put("photo", currentUserPhoto);
        timeStampHashReceiver.put("seen", "notseen");
        timeStampHashReceiver.put("chatPending", true);
        receiverChatCreateRef.updateChildren(timeStampHashReceiver);

        stopSelf();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
