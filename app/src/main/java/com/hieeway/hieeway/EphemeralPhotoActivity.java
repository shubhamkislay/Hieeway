package com.hieeway.hieeway;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hieeway.hieeway.Model.ChatMessage;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.HashMap;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EphemeralPhotoActivity extends AppCompatActivity {


    ImageView dispPhoto;
    ProgressBar progressBar;
    private static final String FOLDER = "Hieeway Test Resources";
    private static final String ENCRYPTED_FILE_PREFIX = "encrpyted";
    private static final String TAG = "PHOTO ACTIVITY";

    String userIdChattingWith, photoUrl, sender;
    List<ChatMessage> photoList;
    String mKey;
    Button nextBtn;
    String currentUserPublicKeyID, publicKeyID, currentUserPrivateKey, mediaKey;
    private File tempFile;
    private Uri decryptedPhotoUri;
    private Uri photoUri;
    private boolean photoReady = false;
    private TextView loadingpercent, percentsign, loading;
    private SecretKeySpec originalKey;

    public static byte[] decodeFile(byte[] fileData, final SecretKey mediaKey) throws Exception {
        byte[] decrypted = null;
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, mediaKey);
        decrypted = cipher.doFinal(fileData);
        return decrypted;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_new__message_);

        setContentView(R.layout.activity_ephermeral_photo);

        progressBar = findViewById(R.id.progress);


        loading = findViewById(R.id.loading);
        loadingpercent = (TextView) findViewById(R.id.loadingpercent);
        percentsign = findViewById(R.id.percentsign);



        dispPhoto =findViewById(R.id.fullscreen_content);
        nextBtn = findViewById(R.id.next_btn);

        Intent intent = getIntent();

        currentUserPublicKeyID = intent.getStringExtra("currentUserPublicKeyID");
        photoUrl = intent.getStringExtra("photoUrl");
        mKey = intent.getStringExtra("mKey");
        sender = intent.getStringExtra("sender");
        publicKeyID = intent.getStringExtra("publicKeyID");
        mediaKey = intent.getStringExtra("mediaKey");
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");
        currentUserPrivateKey = intent.getStringExtra("currentUserPrivateKey");


        /*nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhotos();
            }
        });*/

        //Glide.with(DispImageActivity.this).load(photoUri).into(dispPhoto);

        // loadPhotos();

        File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File encryptedFile = new File(dir, ENCRYPTED_FILE_PREFIX + mKey + ".jpg");

        //File encryptedFile = new File(getFilesDir(), ENCRYPTED_FILE_PREFIX+mKey+".mp4");
/*        File originalFile = new File(dir, mKey+".mp4");

        if(originalFile.exists())
        {
            String filePath = Environment.getExternalStorageDirectory() + File.separator + FOLDER
                    + File.separator + mKey+".mp4";
            decryptedVideoUri = Uri.parse(filePath);
            checkAndPlay();
        }*/

        if (encryptedFile.exists()) {
            /*videoView.setVisibility(View.VISIBLE);
            player_view.setVisibility(View.GONE);
            String filePath = Environment.getExternalStorageDirectory() + File.separator + FOLDER
                    + File.separator + ENCRYPTED_FILE_PREFIX+mKey+".mp4";

            decryptedVideoUri = Uri.parse(filePath);
            //videoReady = true;
            progress_layout.setVisibility(View.GONE);
            load_layout.setVisibility(View.GONE);
            loading.setVisibility(View.GONE);

            videoView.setVideoURI(decryptedVideoUri);
            videoView.start();
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    finish();
                }
            });*/

            String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);

            byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
            originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
            decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".jpg", mKey);
            checkAndPlay();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if (!photoReady) {
                        deletePhotoFile(mKey);
                        newDownload(photoUrl);

                    }
                }
            }, 3000);
        } else {
            /*videoView.setVisibility(View.GONE);
            player_view.setVisibility(View.VISIBLE);*/
            /*File encryptedFile = new File(dir, ENCRYPTED_FILE_PREFIX+mKey+".mp4");
            if(encryptedFile.exists())
            {

                String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);


                loading.setText("File loaded");


                progress_two.setVisibility(View.INVISIBLE);
                progress_one.setVisibility(View.INVISIBLE);


                byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
                originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

                decryptedBtn.setVisibility(View.VISIBLE);
            }
            else {*/

            checkAndPlay();
            newDownload(photoUrl);
            //}
        }

        //for()

    }

    public void loadPhotos() {






            Glide.with(EphemeralPhotoActivity.this)
                    .load(photoUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //setContentView(R.layout.activity_disp_image);

                            //dispPhoto.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            dispPhoto.setVisibility(View.VISIBLE);

                          //  if(userIdChattingWith.equals())
                            DatabaseReference deletePhotoMessageSender = FirebaseDatabase.getInstance().getReference("Messages")
                                    .child(userIdChattingWith)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mKey);
                            DatabaseReference deletePhotoMessageReceiver = FirebaseDatabase.getInstance().getReference("Messages")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIdChattingWith)
                                    .child(mKey);

                            if(userIdChattingWith.equals(sender)) {
                              //  deletePhotoMessageSender.removeValue();

                                HashMap<String,Object> updateSeen = new HashMap<>();
                                updateSeen.put("seen","seen");
                                updateSeen.put("sentStatus","sent");
                                updateSeen.put("photourl", "played");

                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
                                photoRef.delete();

                                deletePhotoMessageSender.updateChildren(updateSeen);
                                deletePhotoMessageReceiver.removeValue();

                             //   deletePhotoMessageReceiver.getParent().updateChildren(updateSeen);
                            }




                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                 //   if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(receiver)) {

                                        //dispPhoto.setVisibility(View.GONE);


                                        /*DatabaseReference deletePhotoMessageSender = FirebaseDatabase.getInstance().getReference("Messages")
                                                .child(userIdChattingWith)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(mKey);
                                        DatabaseReference deletePhotoMessageReceiver = FirebaseDatabase.getInstance().getReference("Messages")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(userIdChattingWith)
                                                .child(mKey);*/


                                    /*String HashMap<String,Object> deletePhotoHash = new HashMap<>();

                                    deletePhotoHash.put("photo","default");*/



                                    /*deletePhotoMessageReceiver.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                            {


                                                Message photoMessage = snapshot.getValue(Message.class);

                                                if(photoMessage.getmKey().equals(mKey))
                                                {
                                                    Toast.makeText(DispImageActivity.this,"mKey Matches" , Toast.LENGTH_SHORT).show();
                                                    deletePhotoMessageReceiver.removeValue();
                                                    deletePhotoMessageSender.removeValue();

                                                }



                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
*/


                                        finish();






                                }
                            }, 7000);


                            return false;
                        }
                    }).into(dispPhoto);



    }

    private void checkAndPlay() {
        if (photoReady) {

            dispPhoto.setVisibility(View.VISIBLE);

            loading.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            dispPhoto.setImageURI(decryptedPhotoUri);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    deletePhotoMessage();
                    finish();
                }
            }, 7000);

        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkAndPlay();

                }
            }, 1000);
        }

    }

    public byte[] readFile(final String encryptedFileName) {
        byte[] contents = null;

        File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // File file = new File(getCacheDir(), encryptedFileName);

        File file = new File(dir, encryptedFileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }

    void decodeFile(SecretKey mediaKey, String inputFile, String outputFileName) {

        //checkDecryptedPhoto();
        try {

            byte[] decodedData = decodeFile(readFile(inputFile), mediaKey);
            // String str = new String(decodedData);
            //System.out.println("DECODED FILE CONTENTS : " + str);
            playPhotoFromData(decodedData, outputFileName);
            // }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String decryptRSAToString(String encryptedBase64, String privateKey) {

        String decryptedString = "";
        try {
            KeyFactory keyFac = KeyFactory.getInstance("RSA");
            KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decode(privateKey.trim().getBytes(), Base64.DEFAULT));
            Key key = keyFac.generatePrivate(keySpec);

            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
            // encrypt the plain text using the public key
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] encryptedBytes = Base64.decode(encryptedBase64, Base64.DEFAULT);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptedString = new String(decryptedBytes);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return decryptedString;
    }

    private void playPhotoFromData(byte[] decodedData, String outputFileName) {
        try {
            // create temp file that will hold byte array
            tempFile = File.createTempFile(outputFileName, "jpg", getCacheDir());
            tempFile.deleteOnExit();
            FileOutputStream fos = new FileOutputStream(tempFile);
            fos.write(decodedData);
            fos.close();

            decryptedPhotoUri = Uri.parse(tempFile.getPath());

            photoUri = Uri.fromFile(tempFile);
            photoReady = true;

            // Tried reusing instance of media player
            // but that resulted in system crashes...
            /*MediaPlayer mediaPlayer = new MediaPlayer();
            FileInputStream fis = new FileInputStream(tempMp3);
            mediaPlayer.setDataSource(fis.getFD());
            mediaPlayer.prepare();
            mediaPlayer.start();*/
        } catch (IOException ex) {
            ex.printStackTrace();

        }

    }

    public void newDownload(String url) {

        //loading.setText("Downloading Photo...");
        final EphemeralPhotoActivity.DownloadTask downloadTask = new EphemeralPhotoActivity.DownloadTask(EphemeralPhotoActivity.this);
        downloadTask.execute(url);
    }

    private void deletePhotoFile(String inputFile) {
        if (tempFile != null)
            tempFile.delete();
        try {
            // delete the original file
            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);

            new File(dir, inputFile + ".jpg").delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        try {
            // delete the original file
            File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);

            new File(dir, ENCRYPTED_FILE_PREFIX + inputFile + ".jpg").delete();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
    }

    private void deletePhotoMessage() {
        DatabaseReference deletePhotoMessageSender = FirebaseDatabase.getInstance().getReference("Messages")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(mKey);
        DatabaseReference deletePhotoMessageReceiver = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith)
                .child(mKey);

        if (userIdChattingWith.equals(sender)) {
            //  deletePhotoMessageSender.removeValue();

            HashMap<String, Object> updateSeen = new HashMap<>();
            updateSeen.put("seen", "seen");
            updateSeen.put("sentStatus", "sent");
            updateSeen.put("photourl", "played");

            StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
            photoRef.delete();

            deletePhotoMessageSender.updateChildren(updateSeen);
            deletePhotoMessageReceiver.removeValue();

            //   deletePhotoMessageReceiver.getParent().updateChildren(updateSeen);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (photoReady)
            deletePhotoMessage();
    }

    public class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;


        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                java.net.URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                int fileLength = connection.getContentLength();

                input = connection.getInputStream();
                String fileN = ENCRYPTED_FILE_PREFIX + mKey + ".jpg";
                File dir = new File(Environment.getExternalStorageDirectory(), FOLDER);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File filename = new File(dir, fileN);
                output = new FileOutputStream(filename);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    if (fileLength > 0) {
                        publishProgress((int) (total * 100 / fileLength));
                        //Toast.makeText(EphemeralPhotoActivity.this,"Progress",Toast.LENGTH_SHORT).show();
                        //progressDialog.setProgress((int) (total * 100 / fileLength))
                    }
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                {
                    deletePhotoFile(mKey);
                    return e.toString();

                }
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           /* PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();*/

            /*progressDialog = new ProgressDialog(EphemeralPhotoActivity.this);
            progressDialog.setProgress(0);
            progressDialog.setTitle("Fetching Photo");
            progressDialog.setMax(100);


            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();*/




            /*progress_two.setIndeterminate(false);
            progress_one.setIndeterminate(false);

            progress_one.setVisibility(View.VISIBLE);
            progress_two.setVisibility(View.VISIBLE);

            progress_one.setMax(100);
            progress_one.setProgress(20);*/


            // Toast.makeText(context, "Downloading Started", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.VISIBLE);
            loading.setText("Downloading ");
            loadingpercent.setText("0");
            loadingpercent.setVisibility(View.VISIBLE);
            percentsign.setVisibility(View.VISIBLE);


        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // bnp.setProgress(progress[0]);
           /* loading.setText("Dowloading " + progress[0]+"%");
            Toast.makeText(EphemeralPhotoActivity.this,"Progress"+progress[0],Toast.LENGTH_SHORT).show();*/


            loadingpercent.setText(progress[0] + "");

            //  progressDialog.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(String result) {
            // mWakeLock.release();


            loadingpercent.setVisibility(View.GONE);
            percentsign.setVisibility(View.GONE);


            if (result != null) {
                Toast.makeText(context, "Download failed" /*+ result*/, Toast.LENGTH_LONG).show();
                deletePhotoFile(mKey);
                finish();
            } else {
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
                String sKey = decryptRSAToString(mediaKey, currentUserPrivateKey);

                //progressBar.setVisibility(View.INVISIBLE);

                loading.setText("Photo Downloaded");


                // progressDialog.dismiss();


                byte[] encodedKey = Base64.decode(sKey, Base64.DEFAULT);
                originalKey = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");

                //decryptedBtn.setVisibility(View.VISIBLE);

                loading.setText("Decrypting");

               /* progress_two.setVisibility(View.VISIBLE);
                progress_one.setVisibility(View.VISIBLE);*/

                // decryptFile(FOLDER,ENCRYPTED_FILE_PREFIX+mKey+".jpg",mKey+".jpg",originalKey);

                decodeFile(originalKey, ENCRYPTED_FILE_PREFIX + mKey + ".jpg", mKey);

            }

        }
    }
}
