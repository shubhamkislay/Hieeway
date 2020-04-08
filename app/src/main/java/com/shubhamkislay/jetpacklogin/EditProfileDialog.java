package com.shubhamkislay.jetpacklogin;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.shubhamkislay.jetpacklogin.Interface.EditProfileOptionListener;

public class EditProfileDialog extends Dialog {

    RelativeLayout edit_profile_pic;
    RelativeLayout edit_bio;
    TextView title, prof_txt, bio_txt;
    EditProfileOptionListener editProfileOptionListener;
    Context context;

    public EditProfileDialog(@NonNull Context context, EditProfileOptionListener editProfileOptionListener) {

        super(context);
        this.context = context;
        this.editProfileOptionListener = editProfileOptionListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_edit_profile);


        edit_profile_pic = findViewById(R.id.edit_profile_pic);
        edit_bio = findViewById(R.id.edit_bio);

        title = findViewById(R.id.title);
        prof_txt = findViewById(R.id.profile_txt);
        bio_txt = findViewById(R.id.bio_txt);

        title.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        prof_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bio_txt.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        edit_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileOptionListener.editProfileOption("bio");
                dismiss();
            }
        });

        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileOptionListener.editProfileOption("pic");
                dismiss();
            }
        });

    }
}
