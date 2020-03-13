package com.shubhamkislay.jetpacklogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {

    private ImageView splash_logo, splash_logo_gradient, send_arrow;
    float displayHeight;
    ObjectAnimator animatorY, alphaArrow;
    AnimatorSet animatorSet;
    private int arrowAnimDuration = 600;
    TypeWriter typeWriter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);


        splash_logo = findViewById(R.id.splash_logo);

        splash_logo_gradient = findViewById(R.id.splash_logo_gradient);

        send_arrow = findViewById(R.id.send_arrow);

        typeWriter = findViewById(R.id.logo_txt);


       // overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);


        final Animation hyperspaceJump = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        splash_logo.setAnimation(hyperspaceJump);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        typeWriter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        typeWriter.setCharacterDelay(75);
        typeWriter.animateText("Hieeway");








        animatorY = ObjectAnimator.ofFloat(send_arrow,"translationY",-displayHeight -(displayHeight)/3);


        alphaArrow = ObjectAnimator.ofFloat(send_arrow,"alpha",1.0f,0.02f);
        animatorSet = new AnimatorSet();

        animatorSet.setDuration(arrowAnimDuration);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                send_arrow.setAlpha(1.0f);

                final Animation accel = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.accelerate_image);
                final Animation accelFaster = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.accelerate_image_faster);

                accel.setRepeatMode(Animation.INFINITE);

               // splash_logo_gradient.setAnimation(accel);
                typeWriter.setAnimation(accelFaster);


                typeWriter.animate().alpha(0.0f).setDuration(arrowAnimDuration);
               // splash_logo_gradient.animate().alpha(0.0f).setDuration(750);

                animatorSet.playTogether(animatorY,alphaArrow);
                animatorSet.start();

            }
        },1000);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }
}
