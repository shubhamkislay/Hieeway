package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.ncorti.slidetoact.SlideToActView;

public class SlideButtonTestActivity extends AppCompatActivity {

    SlideToActView slideToActView;
    ProgressBar complete_icon;
    Boolean videoLive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_button_test);

        slideToActView = findViewById(R.id.slideToActView);

        complete_icon = findViewById(R.id.complete_icon);


        //slideToActView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        try {
            slideToActView.setTypeFace(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf").getStyle());


            slideToActView.setTextAppearance(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf").getStyle());
        } catch (Exception e) {
            //
        }


        slideToActView.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideView) {

                complete_icon.setVisibility(View.VISIBLE);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        complete_icon.setVisibility(View.GONE);

                        if (!videoLive) {
                            slideToActView.setText("Stop Live Expression");

                            slideToActView.resetSlider();
                            slideToActView.setReversed(true);
                            videoLive = true;
                        } else {
                            slideToActView.setText("Start Live Expression");
                            slideToActView.resetSlider();
                            slideToActView.setReversed(false);

                            videoLive = false;
                        }

                    }
                }, 3000);


            }
        });
    }
}
