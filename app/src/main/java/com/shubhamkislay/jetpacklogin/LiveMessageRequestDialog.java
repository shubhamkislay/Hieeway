package com.shubhamkislay.jetpacklogin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shubhamkislay.jetpacklogin.Interface.LiveMessageRequestListener;

public class LiveMessageRequestDialog extends Dialog {


    TextView yes_btn, no_btn;
    LiveMessageRequestListener liveMessageRequestListener;

    public LiveMessageRequestDialog(@NonNull Context context, LiveMessageRequestListener liveMessageRequestListener) throws NullPointerException {
        super(context);
        this.liveMessageRequestListener = liveMessageRequestListener;

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

        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);


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
