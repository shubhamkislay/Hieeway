package com.hieeway.hieeway;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class PhotoViewerUploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer_upload);


        Intent intent = getIntent();

        String picturePath = intent.getStringExtra("picturePath");



        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = displayMetrics.heightPixels;
        final int width = displayMetrics.widthPixels;


        ImageView imageView = findViewById(R.id.photo_imageview);

        ImageButton cancelButton = findViewById(R.id.cancel_button);
        ImageButton sendButton = findViewById(R.id.send_for_upload_button);



        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PhotoViewerUploadActivity.this, "Upload Cancelled", Toast.LENGTH_SHORT).show();
                Intent returnIntent = new Intent();
                // returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_CANCELED,returnIntent);
                finish();
            }
        });


        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                Toast.makeText(PhotoViewerUploadActivity.this, "Uploading...", Toast.LENGTH_SHORT).show();
               // returnIntent.putExtra("result",result);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
            }
        });



       // imageView.setMaxHeight(height);


        imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));


       /* Glide.with(PhotoViewerUploadActivity.this).load(imageUrl)
                .apply(new RequestOptions().override(width, height)).into(imageView);*/



        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

    }
}
