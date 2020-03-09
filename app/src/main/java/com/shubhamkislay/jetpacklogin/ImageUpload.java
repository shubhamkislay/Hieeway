package com.shubhamkislay.jetpacklogin;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Toast;

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

public class ImageUpload extends AppCompatActivity {

    private Button uploadButton;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST=1;
    private Uri imageUri;
    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);



        uploadButton = findViewById(R.id.upload);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid());




        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                openImage();

            }
        });

    }







    private void openImage()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getExtension(Uri uri)
    {


        ContentResolver contentResolver = ImageUpload.this.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage()
    {

        final ProgressDialog progressDialog = new ProgressDialog(ImageUpload.this );

        progressDialog.setMessage("Uploading");
        progressDialog.show();
        if(imageUri!=null)
        {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }




                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if(task.isSuccessful())
                    {

                        Uri downloadUri = task.getResult();

                        String mUri = downloadUri.toString();


                        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("photo",mUri );
                        databaseReference.updateChildren(map);
                        progressDialog.dismiss();


                    }
                    else
                    {
                        Toast.makeText(ImageUpload.this,"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            });


        }
        else
        {
            Toast.makeText(ImageUpload.this,"No Image selected",Toast.LENGTH_SHORT ).show();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if(requestCode==IMAGE_REQUEST&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null)
        {
            imageUri = data.getData();
            if(uploadTask!=null&&uploadTask.isInProgress())
            {
                Toast.makeText(ImageUpload.this,"Uploadin..." , Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploadImage();
            }
        }




    }

    //uploading image to firebase ends here!




}
