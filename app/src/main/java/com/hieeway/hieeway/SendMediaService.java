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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hieeway.hieeway.Model.ChatStamp;

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

            imageUri = Uri.parse(intent.getStringExtra("imageUri"));
            userChattingWithId = intent.getStringExtra("userChattingWithId");
            usernameChattingWith = intent.getStringExtra("usernameChattingWith");
            userphotoUrl = intent.getStringExtra("userphotoUrl");
            currentUsername = intent.getStringExtra("currentUsername");
            currentUserPhoto = intent.getStringExtra("currentUserPhoto");
            otherUserPublicKeyID = intent.getStringExtra("otherUserPublicKeyID");
            currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
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
        }


        return START_NOT_STICKY;
    }

    private void encrpytVideo() {

        try {
            //  AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
            // FileInputStream fis = videoAsset.createInputStream();
            // FileInputStream fis = new FileInputStream(getContentResolver().openFileDescriptor(videoUri, "r").getFileDescriptor());

            // FileInputStream fis = new FileInputStream(String.valueOf(getContentResolver().acquireContentProviderClient(videoUri)));
            inputStream = getContentResolver().openInputStream(imageUri);

            File root = new File(Environment.getExternalStorageDirectory(), "Hieeway Videos");
            if (!root.exists()) {
                root.mkdirs();
            }
            final File outfile = new File(root, mKey + ".mp4");
            fos = new FileOutputStream(outfile);


            final Cipher cipher = Cipher.getInstance("AES");

            // encrypt the plain text using the public key
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecretKey skey = kgen.generateKey();

            cipher.init(Cipher.ENCRYPT_MODE, skey);


            final String mediaKey = Base64.encodeToString(skey.getEncoded(), Base64.DEFAULT);


            /**
             * changing key in string form back to SecretKey object
             *
             * String seckey = publicKey;
             * byte[] encodedKey     = Base64.decode(secKey, Base64.DEFAULT);
             * SecretKey originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
             *
             * */


            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        CipherInputStream cis = new CipherInputStream(inputStream, cipher);

                        byte[] d = new byte[10 * 1024 * 1024];

                        while (true) {


                            if (!((read = inputStream.read(d)) != -1)) break;


                            fos.write(d, 0, (char) read);
                            fos.flush();


                        }
                        fos.close();
                        encryptedUri = Uri.fromFile(outfile);
                        /*String senderMediaKey = encryptRSAToString(mediaKey,CameraActivity.currentUserPublicKey);
                        String receiverMediaKey = encryptRSAToString(mediaKey,CameraActivity.otherUserPublicKey);*/

                        String senderMediaKey = encryptRSAToString(mediaKey, CameraActivity.currentUserPublicKey);
                        String receiverMediaKey = encryptRSAToString(mediaKey, CameraActivity.otherUserPublicKey);

                        uploadVideo(imageUri, senderMediaKey, receiverMediaKey);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();


        } catch (FileNotFoundException e) {
            //Toast.makeText(VideoUploadActivity.this,"Error message: "+e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            //Toast.makeText(VideoUploadActivity.this,"Error message: "+e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            //Toast.makeText(VideoUploadActivity.this,"Error message: "+e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            // Toast.makeText(VideoUploadActivity.this,"Error message: "+e.toString(),Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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

                    timeStampHashReceiver.put("timeStamp", ts);
                    timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    timeStampHashReceiver.put("username", currentUserName);
                    timeStampHashReceiver.put("photo", currentUserPhoto);
                    timeStampHashReceiver.put("seen", "notseen");
                    timeStampHashReceiver.put("chatPending", true);
                    timeStampHashReceiver.put("gemCount", 2);
                    senderChatCreateRef.updateChildren(timeStampHash);
                    receiverChatCreateRef.updateChildren(timeStampHashReceiver);

                } else {
                    timeStampHash.put("timeStamp", ts);
                    timeStampHash.put("id", userChattingWithId);
                    timeStampHash.put("username", usernameUserChattingWith);
                    timeStampHash.put("photo", userChattingWith_photo);
                    timeStampHash.put("seen", "notseen");
                    timeStampHash.put("chatPending", false);

                    timeStampHashReceiver.put("timeStamp", ts);
                    timeStampHashReceiver.put("id", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    timeStampHashReceiver.put("username", currentUserName);
                    timeStampHashReceiver.put("photo", currentUserPhoto);
                    timeStampHashReceiver.put("seen", "notseen");
                    timeStampHashReceiver.put("chatPending", true);

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
