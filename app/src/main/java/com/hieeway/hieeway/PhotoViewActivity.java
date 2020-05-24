package com.hieeway.hieeway;

import android.content.Intent;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class PhotoViewActivity extends AppCompatActivity {


    ImageView imageView;
    RelativeLayout relativeLayout;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);



        imageView = findViewById(R.id.photoCaptured);
        relativeLayout = findViewById(R.id.relative_layout);
        button = findViewById(R.id.screenshot_btn);

        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra("BitmapImage");

        imageView.setImageBitmap(bitmap);


        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Bitmap b = ScreenShotUtils.getScreenShotRootView(relativeLayout);


                imageView.setImageBitmap(b);
                relativeLayout.setBackgroundColor(Color.parseColor("#999999"));

            }
        });
*/










    }
}
