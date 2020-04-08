package com.shubhamkislay.jetpacklogin;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Point;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class AutoTextSizeChangeActivity extends AppCompatActivity {

    SeekBar seekBar ;
    Button send_btn;
    TextView textView;
    ImageView sendArrow, receiveArrow;
    ObjectAnimator animatorY, alphaArrow;
    AnimatorSet animatorSet;
    float displayHeight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_text_size_change);


        sendArrow = findViewById(R.id.send_arrow);
        receiveArrow = findViewById(R.id.receive_arrow);
        send_btn = findViewById(R.id.send_btn);



        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateArrow();
            }
        });








    }

    private void animateArrow() {
       // sendArrow.setAlpha(1.0f);

        try {
            if (animatorSet.isRunning()) {
                animatorSet.cancel();
            }
        }catch (Exception e)
        {

        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        animatorY = ObjectAnimator.ofFloat(sendArrow,"translationY",-displayHeight - (displayHeight) / 4);
        animatorY.setDuration(550);
       // animatorY.start();

        alphaArrow = ObjectAnimator.ofFloat(sendArrow,"alpha",1.0f,0.0f);
        alphaArrow.setDuration(550);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,alphaArrow);

        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                sendArrow.setAlpha(1.0f);

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                sendArrow.setTranslationY(0f);
                sendArrow.setAlpha(0.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

                sendArrow.setTranslationY(0f);
                sendArrow.setAlpha(0.0f);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }


}
