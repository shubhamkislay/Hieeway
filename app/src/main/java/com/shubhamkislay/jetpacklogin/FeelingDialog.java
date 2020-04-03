package com.shubhamkislay.jetpacklogin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shubhamkislay.jetpacklogin.Interface.FeelingListener;


public class FeelingDialog extends Dialog {

    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    FeelingListener feelingListener;
    TextView happy_emoji;
    TextView bored_emoji;
    TextView sad_emoji;
    TextView excited_emoji;
    TextView angry_emoji;
    TextView confused_emoji;


    public FeelingDialog( Context context, FeelingListener feelingListener) {
        super(context);
        this.feelingListener = feelingListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fellings_emoticon_dailog);

        happy_emoji = findViewById(R.id.happy_emoji);
        bored_emoji = findViewById(R.id.bored_emoji);
        sad_emoji = findViewById(R.id.sad_emoji);
        excited_emoji = findViewById(R.id.excited_emoji);
        angry_emoji = findViewById(R.id.angry_emoji);
        confused_emoji = findViewById(R.id.confused_emoji);



        happy_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingListener.changeFeeling(HAPPY);
                dismiss();
            }
        });

        bored_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingListener.changeFeeling(BORED);
                dismiss();
            }
        });

        sad_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingListener.changeFeeling(SAD);
                dismiss();
            }
        });

        confused_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingListener.changeFeeling(CONFUSED);
                dismiss();
            }
        });

        angry_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingListener.changeFeeling(ANGRY);
                dismiss();
            }
        });

        excited_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingListener.changeFeeling(EXCITED);
                dismiss();
            }
        });
    }
}
