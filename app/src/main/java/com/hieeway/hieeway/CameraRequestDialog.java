package com.hieeway.hieeway;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hieeway.hieeway.Interface.CameraPermissionListener;

public class CameraRequestDialog extends Dialog {


    private TextView yes_btn, no_btn, live_message_txt;
    private String userId;
    private String username;
    private CameraPermissionListener cameraPermissionListener;

    public CameraRequestDialog(@NonNull Context context, CameraPermissionListener cameraPermissionListener) {
        super(context);
        this.cameraPermissionListener = cameraPermissionListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.camera_request_dialog);


        yes_btn = findViewById(R.id.yes_btn);

        live_message_txt = findViewById(R.id.live_message_txt);


        yes_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        //live_message_txt.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //removeFriend();
                cameraPermissionListener.startPermission();
                dismiss();
            }
        });


    }


}
