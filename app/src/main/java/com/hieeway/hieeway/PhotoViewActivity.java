package com.hieeway.hieeway;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

public class PhotoViewActivity extends AppCompatActivity {

    ProgressBar determinateBar;
    ImageView blast;
    String imageUri;
    CustomImageView photo;
    int maxValue = 7000;
    boolean paused = false;
    ProgressBar loading_video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_view);

        Intent intent = getIntent();

        imageUri = intent.getStringExtra("path");

        determinateBar = findViewById(R.id.determinateBar);
        blast = findViewById(R.id.blast);
        photo = findViewById(R.id.photo);
        loading_video = findViewById(R.id.loading_video);

        Glide.with(this)
                .load(imageUri)
                .addListener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        loading_video.setVisibility(View.GONE);
                        determinateBar.setMax(maxValue);
                        updateProgress();

                        return false;
                    }
                }).into(photo);


        photo.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        paused = true;
                        //Toast.makeText(ExoPlayerActivity.this,"Down",Toast.LENGTH_SHORT).show();
                    }
                        /*else if (event.getAction() == MotionEvent.ACTION_UP)
                            player.play();*/

                    else if (event.getAction() == MotionEvent.ACTION_UP) {
                        paused = false;
                        //Toast.makeText(ExoPlayerActivity.this,"UP",Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {
                    //
                }
                return true;
            }
        });


    }

    private void updateProgress() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    try {

                        if (!paused) {
                            if (maxValue <= 0) {
                                blast.animate().scaleX(500.0f).scaleY(500.0f).setDuration(750);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 750);
                            } else {
                                maxValue = maxValue - 100;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    determinateBar.setProgress((int) maxValue, true);
                                } else
                                    determinateBar.setProgress((int) maxValue);

                                updateProgress();
                            }
                        } else
                            updateProgress();
                    } catch (Exception e) {
                        //
                    }
                }
            }, 100);
        } catch (Exception e) {
            //
        }
    }


    /*@Override
    public void onBackPressed() {

        blast.animate().scaleX(500.0f).scaleY(500.0f).setDuration(350);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 350);
    }*/
}