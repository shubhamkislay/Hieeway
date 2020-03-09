package com.shubhamkislay.jetpacklogin;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.shubhamkislay.jetpacklogin.Helper.BitmapHelper;

public class PhotoTreatmentActivity extends AppCompatActivity {


    private Bitmap originalBitmap;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_treatment);


        originalBitmap = BitmapHelper.getInstance().getBitmap();
        imageView = findViewById(R.id.image_treat);

        imageView.setImageBitmap(originalBitmap);

    //    uploadImage(originalBitmap);

    }

    private void uploadImage(Bitmap originalBitmap) {



    }


    @Override
    public void onBackPressed() {
        finish();
    }
}
