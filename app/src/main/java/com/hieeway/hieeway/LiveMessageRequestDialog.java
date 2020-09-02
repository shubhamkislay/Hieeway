package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.hieeway.hieeway.Interface.LiveMessageRequestListener;

public class LiveMessageRequestDialog extends Dialog {


    private TextView yes_btn, no_btn, live_message_txt;
    private LiveMessageRequestListener liveMessageRequestListener;
    private String live;
    private LinearLayout linear_options;
    private RelativeLayout progress_bar;
    private Context context;

    public LiveMessageRequestDialog(@NonNull Context context, LiveMessageRequestListener liveMessageRequestListener, String live) throws NullPointerException {
        super(context);

        this.liveMessageRequestListener = liveMessageRequestListener;
        this.live = live;
        this.context = context;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.dialog_live_message_request);


        linear_options = findViewById(R.id.linear_options);
        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);
        progress_bar = findViewById(R.id.progress_bar);


        if (live.equals("live")) {

            linear_options.setVisibility(View.GONE);

            live_message_txt.setText("Starting live messaging...");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (live.equals("live")) {
                        liveMessageRequestListener.startLiveMessaging();
                        dismiss();
                    }
                }
            }, 1000);
        } else {
            linear_options.setVisibility(View.VISIBLE);
        }


        yes_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                live_message_txt.setText("Starting live messaging...");
                linear_options.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        liveMessageRequestListener.startLiveMessaging();
                        dismiss();
                    }
                }, 0);

            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveMessageRequestListener.stopLiveMessaging();
                dismiss();
            }
        });


    }


}
