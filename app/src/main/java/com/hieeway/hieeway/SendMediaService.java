package com.hieeway.hieeway;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hieeway.hieeway.Helper.RecipientListHelper;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.GroupMember;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.Model.Recipient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static br.com.instachat.emojilibrary.model.Emoji.TAG;
import static com.hieeway.hieeway.MyApplication.CHANNEL_3_ID;

public class SendMediaService extends Service {

    private static String ACTION_STOP_SERVICE = "yes";
    public String mKey;
    DatabaseReference databaseReference, receiverReference;
    String userChattingWithId, usernameChattingWith, userphotoUrl, currentUsername, currentUserPhoto, currentUserPublicKeyID, otherUserPublicKeyID, type;
    Uri imageUri, encryptedUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    Intent stopSelfIntent;
    private PendingIntent pIntentlogin;
    Timestamp timestamp;
    private InputStream inputStream;
    private FileOutputStream fos;
    private int read;
    private static final String FOLDER = "Hieeway Test Resources";
    private static final String ENCRYPTED_FILE_PREFIX = "encrpyted";
    private FileOutputStream fileOutputStream;
    private FileInputStream in;
    private FileOutputStream out;
    private CipherInputStream cis;
    private String mediaKey;
    private String filename;
    private String currentUserPublicKey;
    private String otherUserPublicKey;
    private String activePhoto;
    private String currentUserActivePhoto;
    private String requestType;
    private String currentUserID;
    private List<Recipient> recipientList;
    String groupID;
    String groupName;
    String icon;
    private long tsLong;

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

            timestamp = new Timestamp(System.currentTimeMillis());
            requestType = intent.getStringExtra("requestType");

            if (requestType.equals("message")) {

                imageUri = Uri.parse(intent.getStringExtra("imageUri"));
                userChattingWithId = intent.getStringExtra("userChattingWithId");
                usernameChattingWith = intent.getStringExtra("usernameChattingWith");
                userphotoUrl = intent.getStringExtra("userphotoUrl");
                currentUsername = intent.getStringExtra("currentUsername");
                currentUserPhoto = intent.getStringExtra("currentUserPhoto");
                otherUserPublicKeyID = intent.getStringExtra("otherUserPublicKeyID");
                currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
                activePhoto = intent.getStringExtra("activePhoto");
                currentUserActivePhoto = intent.getStringExtra("currentUserActivePhoto");
                mKey = intent.getStringExtra("mKey");
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
                            .setSmallIcon(R.drawable.ic_image_24dp)
                            .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                            // .setContentIntent(pendingIntent)
                            .setProgress(0, 0, true)
                            .build();


                    startForeground(1, notification);

                    // uploadImage();
                    saveOriginalFile(imageUri);
                } else if (type.equals("video")) {
                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                            .setContentTitle("Sending video")
                            .setSmallIcon(R.drawable.ic_videocam_black_24dp)
                            .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                            // .setContentIntent(pendingIntent)
                            .setProgress(0, 0, true)
                            .build();


                    startForeground(1, notification);

                    // encrpytVideo();
                    saveOriginalFile(imageUri);
                } else if (type.equals("audio")) {
                    Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                            .setContentTitle("Sending audio")
                            .setSmallIcon(R.drawable.ic_mic_black_24dp)
                            .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                            // .setContentIntent(pendingIntent)
                            .setProgress(0, 0, true)
                            .build();

                    otherUserPublicKey = intent.getStringExtra("otherUserPublicKey");
                    currentUserPublicKey = intent.getStringExtra("currentUserPublicKey");


                    startForeground(1, notification);

                    // encrpytVideo();
                    saveOriginalFile(imageUri);
                }

            } else if (requestType.equals("shot")) {

                recipientList = RecipientListHelper.getInstance().getRecipientList();
                imageUri = Uri.parse(intent.getStringExtra("imageUri"));

                currentUsername = intent.getStringExtra("currentUsername");
                currentUserID = intent.getStringExtra("currentUserID");
                mKey = intent.getStringExtra("mKey");
                type = intent.getStringExtra("type");

                databaseReference = FirebaseDatabase.getInstance().getReference("Post")
                        .child(currentUserID);

                String postKey = FirebaseDatabase.getInstance().getReference("Post")
                        .child(currentUserID).push().getKey();


                storageReference = FirebaseStorage.getInstance().getReference("uploads");


                stopSelfIntent = new Intent(this, SendMediaService.class);

                stopSelfIntent.setAction(ACTION_STOP_SERVICE);
                stopSelfIntent.putExtra("messageKey", mKey);
                stopSelfIntent.putExtra("userChattingWithId", "xyz");

                //This is optional if you have more than one buttons and want to differentiate between two


                pIntentlogin = PendingIntent.getService(this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                        .setContentTitle("Posting your shot")
                        .setSmallIcon(R.drawable.nav_hieeway_send_btn)
                        .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                        // .setContentIntent(pendingIntent)
                        .setProgress(0, 0, true)
                        .build();


                startForeground(1, notification);

                uploadShot(imageUri, postKey, type);

            } else if (requestType.equals("group")) {

                tsLong = System.currentTimeMillis() / 1000;

                recipientList = RecipientListHelper.getInstance().getRecipientList();
                imageUri = Uri.parse(intent.getStringExtra("imageUri"));

                currentUsername = intent.getStringExtra("currentUsername");
                currentUserID = intent.getStringExtra("currentUserID");
                currentUserPhoto = intent.getStringExtra("currentUserPhoto");
                mKey = intent.getStringExtra("mKey");
                type = intent.getStringExtra("type");
                groupID = intent.getStringExtra("groupID");
                groupName = intent.getStringExtra("groupName");
                icon = intent.getStringExtra("icon");

                databaseReference = FirebaseDatabase.getInstance().getReference("GroupMessage")
                        .child(groupID);

                String postKey = FirebaseDatabase.getInstance().getReference("GroupMessage")
                        .child(groupID).push().getKey();


                storageReference = FirebaseStorage.getInstance().getReference("uploads");


                stopSelfIntent = new Intent(this, SendMediaService.class);

                stopSelfIntent.setAction(ACTION_STOP_SERVICE);
                stopSelfIntent.putExtra("messageKey", mKey);
                stopSelfIntent.putExtra("userChattingWithId", "xyz");

                //This is optional if you have more than one buttons and want to differentiate between two

                String contentTitle;
                if (type.equals("video"))
                    contentTitle = "Uploading Video";
                else {
                    contentTitle = "Uploading Photo";


                    DatabaseReference groupMessageSendRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                            .child(groupID);


                    HashMap<String, Object> groupMessageHash = new HashMap<>();
                    groupMessageHash.put("messageText", "");
                    groupMessageHash.put("senderId", currentUserID);
                    groupMessageHash.put("messageId", postKey);
                    groupMessageHash.put("timeStamp", timestamp.toString());
                    groupMessageHash.put("photo", currentUserPhoto);
                    groupMessageHash.put("sentStatus", "sending");
                    groupMessageHash.put("username", currentUsername);
                    groupMessageHash.put("messageTime", tsLong);
                    groupMessageHash.put("mediaID", imageUri.toString());
                    groupMessageHash.put("type", type);


                    //groupMessageHash.put("mediaID", "none");


                    groupMessageSendRef.child(postKey).updateChildren(groupMessageHash);


                }

                pIntentlogin = PendingIntent.getService(this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_3_ID)
                        .setContentTitle(contentTitle)
                        .setSmallIcon(R.drawable.nav_hieeway_send_btn)
                        .addAction(R.drawable.ic_cancel_white_24dp, "Close", pIntentlogin)
                        // .setContentIntent(pendingIntent)
                        .setProgress(0, 0, true)
                        .build();


                startForeground(1, notification);

                uploadGroupMessage(imageUri, postKey, type);

            }

        }


        return START_NOT_STICKY;
    }



    private void saveOriginalFile(Uri mediaIntentURI) {
        //checkVideoURI();

        try {
            File root = new File(Environment.getExternalStorageDirectory(), FOLDER);
            if (!root.exists()) {
                root.mkdirs();
            }


            inputStream = getContentResolver().openInputStream(mediaIntentURI);

            if (type.equals("video"))
                filename = mKey + ".mp4";

            if (type.equals("audio"))
                filename = mKey + ".mp3";

            if (type.equals("photo"))
                filename = mKey + ".jpg";

            final File outfile = new File(root, filename);
            if (!outfile.exists())
                outfile.createNewFile();
            fileOutputStream = new FileOutputStream(outfile);


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        byte[] d = new byte[1024 * 10];

                        while (true) {


                            if (!((read = inputStream.read(d)) != -1)) break;

                            fileOutputStream.write(d, 0, (char) read);
                            fileOutputStream.flush();

                        }
                        fileOutputStream.close();

                        /*String filePath = Environment.getExternalStorageDirectory() + File.separator + "Hieeway Test Videos"
                                + File.separator + filename;

                       // videoURI = Uri.parse(filePath);*/

                        if (type.equals("video"))
                            encryptVideoFile(FOLDER, filename, ENCRYPTED_FILE_PREFIX + filename);

                        else if (type.equals("audio"))
                            encryptAudioFile(FOLDER, filename, ENCRYPTED_FILE_PREFIX + filename);

                        else if (type.equals("photo"))
                            encryptPhotoFile(FOLDER, filename, ENCRYPTED_FILE_PREFIX + filename);



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void encryptVideoFile(final String folder, final String inputFileName, final String outputFileName) {


        /*checkEncryptedVideo();
        encryptedlVideoTextFile.setText("encrypting video");*/

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //File oufile = new File(getFilesDir(), outputFileName);

                    //create output directory if it doesn't exist
                    File dir = new File(Environment.getExternalStorageDirectory(), folder);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File inpufile = new File(dir, inputFileName);
                    if (!inpufile.exists())
                        inpufile.createNewFile();
                    File oufile = new File(dir, outputFileName);
                    if (!oufile.exists())
                        oufile.createNewFile();


                    in = new FileInputStream(inpufile);
                    out = new FileOutputStream(oufile);

                    Cipher encipher = Cipher.getInstance("AES");
                    // Cipher decipher = Cipher.getInstance("AES");
                    KeyGenerator kgen = KeyGenerator.getInstance("AES");
                    //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                    SecretKey skey = kgen.generateKey();
                    mediaKey = Base64.encodeToString(skey.getEncoded(), Base64.DEFAULT);

                    encipher.init(Cipher.ENCRYPT_MODE, skey);
                    cis = new CipherInputStream(in, encipher);

                    //File file = new File(dir, encryptedFileName);
                    int size = (int) inpufile.length();
                    byte[] buffer = new byte[size];

                    // byte[] buffer = new byte[1024];
                    int read;
                    while ((read = cis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;
                    encryptedUri = Uri.fromFile(oufile);

                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                        + File.separator + outputFileName;


                String senderMediaKey = encryptRSAToString(mediaKey, CameraActivity.currentUserPublicKey);
                String receiverMediaKey = encryptRSAToString(mediaKey, CameraActivity.otherUserPublicKey);

                if (type.equals("video"))
                    uploadVideo(encryptedUri, senderMediaKey, receiverMediaKey);
                else if (type.equals("audio"))
                    uploadAudio(encryptedUri, senderMediaKey, receiverMediaKey);


            }
        }).start();


    }

    private void encryptAudioFile(final String folder, final String inputFileName, final String outputFileName) {


        /*checkEncryptedVideo();
        encryptedlVideoTextFile.setText("encrypting video");*/

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //File oufile = new File(getFilesDir(), outputFileName);

                    //create output directory if it doesn't exist
                    File dir = new File(Environment.getExternalStorageDirectory(), folder);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File inpufile = new File(dir, inputFileName);
                    if (!inpufile.exists())
                        inpufile.createNewFile();
                    File oufile = new File(dir, outputFileName);
                    if (!oufile.exists())
                        oufile.createNewFile();


                    in = new FileInputStream(inpufile);
                    out = new FileOutputStream(oufile);

                    Cipher encipher = Cipher.getInstance("AES");
                    // Cipher decipher = Cipher.getInstance("AES");
                    KeyGenerator kgen = KeyGenerator.getInstance("AES");
                    //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                    SecretKey skey = kgen.generateKey();
                    mediaKey = Base64.encodeToString(skey.getEncoded(), Base64.DEFAULT);

                    encipher.init(Cipher.ENCRYPT_MODE, skey);
                    cis = new CipherInputStream(in, encipher);

                    //File file = new File(dir, encryptedFileName);
                    int size = (int) inpufile.length();
                    byte[] buffer = new byte[size];

                    // byte[] buffer = new byte[1024];
                    int read;
                    while ((read = cis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;
                    encryptedUri = Uri.fromFile(oufile);

                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                        + File.separator + outputFileName;


                String senderMediaKey = encryptRSAToString(mediaKey, currentUserPublicKey);
                String receiverMediaKey = encryptRSAToString(mediaKey, otherUserPublicKey);

                /*if (type.equals("video"))
                    uploadVideo(encryptedUri, senderMediaKey, receiverMediaKey);
                else if (type.equals("audio"))
                    uploadAudio(encryptedUri, senderMediaKey, receiverMediaKey);*/

                uploadAudio(encryptedUri, senderMediaKey, receiverMediaKey);


            }
        }).start();


    }

    private void encryptPhotoFile(final String folder, final String inputFileName, final String outputFileName) {


        /*checkEncryptedVideo();
        encryptedlVideoTextFile.setText("encrypting video");*/

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    //File oufile = new File(getFilesDir(), outputFileName);

                    //create output directory if it doesn't exist
                    File dir = new File(Environment.getExternalStorageDirectory(), folder);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    File inpufile = new File(dir, inputFileName);
                    if (!inpufile.exists())
                        inpufile.createNewFile();
                    File oufile = new File(dir, outputFileName);
                    if (!oufile.exists())
                        oufile.createNewFile();


                    in = new FileInputStream(inpufile);
                    out = new FileOutputStream(oufile);

                    Cipher encipher = Cipher.getInstance("AES");
                    // Cipher decipher = Cipher.getInstance("AES");
                    KeyGenerator kgen = KeyGenerator.getInstance("AES");
                    //byte key[] = {0x00,0x32,0x22,0x11,0x00,0x00,0x00,0x00,0x00,0x23,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
                    SecretKey skey = kgen.generateKey();
                    mediaKey = Base64.encodeToString(skey.getEncoded(), Base64.DEFAULT);

                    encipher.init(Cipher.ENCRYPT_MODE, skey);
                    cis = new CipherInputStream(in, encipher);

                    //File file = new File(dir, encryptedFileName);
                    int size = (int) inpufile.length();
                    byte[] buffer = new byte[size];

                    // byte[] buffer = new byte[1024];
                    int read;
                    while ((read = cis.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    in = null;

                    // write the output file (You have now copied the file)
                    out.flush();
                    out.close();
                    out = null;
                    encryptedUri = Uri.fromFile(oufile);

                } catch (FileNotFoundException fnfe1) {
                    Log.e("tag", fnfe1.getMessage());
                } catch (Exception e) {
                    Log.e("tag", e.getMessage());
                }
                String filePath = Environment.getExternalStorageDirectory() + File.separator + folder
                        + File.separator + outputFileName;


                String senderMediaKey = encryptRSAToString(mediaKey, CameraActivity.currentUserPublicKey);
                String receiverMediaKey = encryptRSAToString(mediaKey, CameraActivity.otherUserPublicKey);

                /*if (type.equals("video"))
                    uploadVideo(encryptedUri, senderMediaKey, receiverMediaKey);
                else if (type.equals("audio"))
                    uploadAudio(encryptedUri, senderMediaKey, receiverMediaKey);*/

                uploadPhoto(encryptedUri, senderMediaKey, receiverMediaKey);


            }
        }).start();


    }

    private void uploadVideo(Uri encryptedUri, final String senderMediaKey, final String receiverMediaKey) {
        Log.v("SendMediaService", "Uploading...");
        imageUri = encryptedUri;
        if (imageUri != null) {


            final StorageReference fileReference = storageReference.child(mKey + ".mp4");

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
                        sendMessageHash.put("timeStamp", timestamp.toString());
                        sendMessageHash.put("showGotReplyMsg", false);
                        sendMessageHash.put("mediaKey", senderMediaKey);
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
                        receiveMessageHash.put("timeStamp", timestamp.toString());
                        receiveMessageHash.put("showGotReplyMsg", false);
                        receiveMessageHash.put("mediaKey", receiverMediaKey);
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

    private void uploadAudio(Uri encryptedUri, final String senderMediaKey, final String receiverMediaKey) {
        Log.v("SendMediaService", "Uploading...");
        imageUri = encryptedUri;
        if (imageUri != null) {


            final StorageReference fileReference = storageReference.child(mKey + ".mp3");

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
                        sendMessageHash.put("audiourl", mUri);
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
                        sendMessageHash.put("timeStamp", timestamp.toString());
                        sendMessageHash.put("showGotReplyMsg", false);
                        sendMessageHash.put("mediaKey", senderMediaKey);
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
                        receiveMessageHash.put("audiourl", mUri);
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
                        receiveMessageHash.put("timeStamp", timestamp.toString());
                        receiveMessageHash.put("showGotReplyMsg", false);
                        receiveMessageHash.put("mediaKey", receiverMediaKey);
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

    private void uploadPhoto(Uri encryptedUri, final String senderMediaKey, final String receiverMediaKey) {
        Log.v("SendMediaService", "Uploading...");
        imageUri = encryptedUri;
        if (imageUri != null) {


            final StorageReference fileReference = storageReference.child(mKey + ".jpg");

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
                        sendMessageHash.put("timeStamp", timestamp.toString());
                        sendMessageHash.put("showGotReplyMsg", false);
                        sendMessageHash.put("mediaKey", senderMediaKey);
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
                        receiveMessageHash.put("timeStamp", timestamp.toString());
                        receiveMessageHash.put("showGotReplyMsg", false);
                        receiveMessageHash.put("mediaKey", receiverMediaKey);
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

    private void uploadShot(Uri mediaUri, String postKey, String type) {
        Log.v("SendMediaService", "Uploading...");
        imageUri = mediaUri;
        if (imageUri != null) {


            String extension = "default";
            if (type.equals("photo"))
                extension = ".jpg";
            else if (type.equals("video"))
                extension = ".mp4";

            final StorageReference fileReference = storageReference.child(postKey + extension);

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

                        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
                        Long tsLong = System.currentTimeMillis() / 1000;

                        Post post = new Post();
                        post.setUserId(currentUserID);
                        post.setPostKey(postKey);
                        post.setType(type);
                        post.setMediaUrl(mUri);
                        post.setUsername(currentUsername);
                        post.setPostTime(tsLong);
                        post.setMediaKey(postKey);
                        post.setTimeStamp(timeStamp.toString());


                        stopSelfIntent.putExtra("messageKey", postKey);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

                        HashMap<String, Object> postHash = new HashMap<>();
                        postHash.put("MyPosts/" + currentUserID + "/" + postKey, post);
                        databaseReference.updateChildren(postHash);

                        HashMap<String, Object> multiplePathUpdate = new HashMap<>();
        /*FirebaseDatabase.getInstance()
                .getReference("FriendList")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Friend friend = dataSnapshot.getValue(Friend.class);
                                multiplePathUpdate.put("Post/" + friend.getFriendId() + "/" + postKey, post);

                            }
                            databaseReference.updateChildren(multiplePathUpdate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        */

                        for (Recipient recipient : recipientList) {
                            post.setManual(recipient.getManual());
                            multiplePathUpdate.put("Post/" + recipient.getUserid() + "/" + postKey, post);
                        }
                        databaseReference.updateChildren(multiplePathUpdate);
                        stopSelf();


                    } else {
                        stopSelf();
                        //Toast.makeText(getBaseContext(),"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        //  progressDialog.dismiss();
                    }

                }
            });


        } else {

            stopSelf();
            // Toast.makeText(PhotoEditToolsActivity.this,"No Image selected",Toast.LENGTH_SHORT ).show();
        }
    }

    private void uploadGroupMessage(Uri mediaUri, String postKey, String type) {
        Log.v("SendMediaService", "Uploading...");
        imageUri = mediaUri;
        if (imageUri != null) {


            String extension = "default";
            if (type.equals("photo"))
                extension = ".jpg";
            else if (type.equals("video"))
                extension = ".mp4";

            final StorageReference fileReference = storageReference.child(postKey + extension);

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

                        DatabaseReference groupMessageSendRef = FirebaseDatabase.getInstance().getReference("GroupMessage")
                                .child(groupID);


                        HashMap<String, Object> groupMessageHash = new HashMap<>();
                        groupMessageHash.put("messageText", "");
                        groupMessageHash.put("senderId", currentUserID);
                        groupMessageHash.put("messageId", postKey);
                        groupMessageHash.put("timeStamp", timestamp.toString());
                        groupMessageHash.put("photo", currentUserPhoto);
                        groupMessageHash.put("sentStatus", "sent");
                        groupMessageHash.put("username", currentUsername);
                        groupMessageHash.put("messageTime", tsLong);
                        groupMessageHash.put("mediaID", mUri);
                        groupMessageHash.put("type", type);


                        //groupMessageHash.put("mediaID", "none");


                        groupMessageSendRef.child(postKey).updateChildren(groupMessageHash);

                        HashMap<String, Object> updateGroupTimeHash = new HashMap<>();
                        FirebaseDatabase.getInstance().getReference("GroupMembers")
                                .child(groupID)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                GroupMember groupMember = dataSnapshot.getValue(GroupMember.class);

                                                HashMap<String, Object> hashMap = new HashMap<>();
                                                hashMap.put("sender", currentUserID);
                                                hashMap.put("type", "text");
                                                hashMap.put("key", postKey);
                                                hashMap.put("timeStamp", timestamp.toString());
                                                //updateGroupTimeHash.put("MyGroup/" + groupMember.getId() + "/"+groupID+"/timeStamp/",timestamp.toString());
                                                FirebaseDatabase.getInstance().getReference("MyGroup")
                                                        .child(groupMember.getId())
                                                        .child(groupID)
                                                        .updateChildren(hashMap);


                                            }

                                            //FirebaseDatabase.getInstance().getReference().setValue(updateGroupTimeHash);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

                        stopSelf();


                    } else {
                        stopSelf();
                        //Toast.makeText(getBaseContext(),"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        //  progressDialog.dismiss();
                    }

                }
            });


        } else {

            stopSelf();
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

    public String encryptRSAToString(String clearText, String publicKey) {
        String encryptedBase64 = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new X509EncodedKeySpec(Base64.decode(publicKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePublic(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] encryptedBytes = cipher.doFinal(clearText.getBytes("UTF-8"));
            encryptedBase64 = new String(Base64.encode(encryptedBytes, Base64.DEFAULT));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return encryptedBase64.replaceAll("(\\r|\\n)", "");
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
                        mKey = databaseReference.push().getKey();


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
                        sendMessageHash.put("timeStamp", timestamp.toString());
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
                        receiveMessageHash.put("timeStamp", timestamp.toString());
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

        final DatabaseReference senderChatCreateRef, receiverChatCreateRef;

        senderChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(userChattingWithId);

        receiverChatCreateRef = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userChattingWithId).child(FirebaseAuth.getInstance().getCurrentUser().getUid());


        senderChatCreateRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                ChatStamp chatStamp = mutableData.getValue(ChatStamp.class);

                HashMap<String, Object> timeStampHash = new HashMap<>();


                HashMap<String, Object> timeStampHashReceiver = new HashMap<>();

                if (chatStamp == null) {

                    timeStampHash.put("timeStamp", ts);
                    timeStampHash.put("id", userChattingWithId);
                    timeStampHash.put("username", usernameUserChattingWith);
                    timeStampHash.put("photo", userChattingWith_photo);
                    timeStampHash.put("seen", "notseen");
                    timeStampHash.put("chatPending", false);
                    timeStampHash.put("gemCount", 2);
                    timeStampHash.put("activePhoto", activePhoto);

                    timeStampHashReceiver.put("timeStamp", ts);
                    timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    timeStampHashReceiver.put("username", currentUserName);
                    timeStampHashReceiver.put("photo", currentUserPhoto);
                    timeStampHashReceiver.put("seen", "notseen");
                    timeStampHashReceiver.put("chatPending", true);
                    timeStampHashReceiver.put("gemCount", 2);
                    timeStampHash.put("activePhoto", currentUserActivePhoto);
                    senderChatCreateRef.updateChildren(timeStampHash);
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                } else {
                    timeStampHash.put("timeStamp", ts);
                    timeStampHash.put("id", userChattingWithId);
                    timeStampHash.put("username", usernameUserChattingWith);
                    timeStampHash.put("photo", userChattingWith_photo);
                    timeStampHash.put("seen", "notseen");
                    timeStampHash.put("chatPending", false);
                    timeStampHash.put("activePhoto", activePhoto);

                    timeStampHashReceiver.put("timeStamp", ts);
                    timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    timeStampHashReceiver.put("username", currentUserName);
                    timeStampHashReceiver.put("photo", currentUserPhoto);
                    timeStampHashReceiver.put("seen", "notseen");
                    timeStampHashReceiver.put("chatPending", true);
                    timeStampHash.put("activePhoto", currentUserActivePhoto);

                    senderChatCreateRef.updateChildren(timeStampHash);
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                }


                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

            }
        });

        stopSelf();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
