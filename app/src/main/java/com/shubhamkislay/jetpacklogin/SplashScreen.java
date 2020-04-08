package com.shubhamkislay.jetpacklogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    private ImageView splash_logo, splash_logo_gradient, send_arrow;
    float displayHeight;
    ObjectAnimator animatorY, alphaArrow, animateTextLogo;
    AnimatorSet animatorSet;
    private int arrowAnimDuration = 600;
    TypeWriter typeWriter;
    TextView master_head;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      //  setContentView(R.layout.activity_splash_screen);


       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        Intent intent = new Intent(SplashScreen.this,MainActivity.class);
        startActivity(intent);
        finish();

      //  startSplash();

    }

    private void startSplash() {

        splash_logo = findViewById(R.id.splash_logo);

        splash_logo_gradient = findViewById(R.id.splash_logo_gradient);

        send_arrow = findViewById(R.id.send_arrow);

        typeWriter = findViewById(R.id.logo_txt);

        master_head = findViewById(R.id.master_head);


        // overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);


        final Animation hyperspaceJump = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        splash_logo.setAnimation(hyperspaceJump);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        typeWriter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        master_head.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));

        typeWriter.setCharacterDelay(75);
        typeWriter.animateText("Hieeway");








        animatorY = ObjectAnimator.ofFloat(send_arrow,"translationY",-displayHeight -(displayHeight)/3);
        animateTextLogo = ObjectAnimator.ofFloat(typeWriter,"translationY",-displayHeight -(displayHeight)/3);


        alphaArrow = ObjectAnimator.ofFloat(send_arrow,"alpha",1.0f,0.0f);
        animatorSet = new AnimatorSet();

        animatorSet.setDuration(arrowAnimDuration);



        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                send_arrow.setAlpha(1.0f);

                final Animation accel = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.accelerate_image);
                final Animation accelFaster = AnimationUtils.loadAnimation(SplashScreen.this, R.anim.accelerate_image_faster);

                accel.setRepeatMode(Animation.INFINITE);
                accelFaster.setRepeatMode(Animation.INFINITE);


                // splash_logo_gradient.setAnimation(accel);
                // typeWriter.setAnimation(accelFaster);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        typeWriter.animate().translationYBy(-displayHeight/2).setDuration(arrowAnimDuration/2);


                    }
                },arrowAnimDuration/2);


                typeWriter.animate().alpha(0.0f).setDuration(arrowAnimDuration);
                // splash_logo_gradient.animate().alpha(0.0f).setDuration(750);

                animatorSet.playTogether(animatorY,alphaArrow/*, animateTextLogo*/);
                animatorSet.start();

            }
        },1250);





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },1850);

    }


}
