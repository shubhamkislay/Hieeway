package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.hieeway.hieeway.Interface.LiveMessageRequestListener;

public class LiveMessageRequestDialog extends Dialog {


    private TextView yes_btn, no_btn, live_message_txt;
    private LiveMessageRequestListener liveMessageRequestListener;
    private String live;
    private LinearLayout linear_options;

    public LiveMessageRequestDialog(@NonNull Context context, LiveMessageRequestListener liveMessageRequestListener, String live) throws NullPointerException {
        super(context);
        this.liveMessageRequestListener = liveMessageRequestListener;
        this.live = live;





    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_live_message_request);
        try {
            getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            //
        }

        linear_options = findViewById(R.id.linear_options);
        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);


        if (live.equals("live")) {

            linear_options.setVisibility(View.GONE);

            live_message_txt.setText("Starting live messaging");

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


        yes_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                liveMessageRequestListener.startLiveMessaging();
                dismiss();
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
