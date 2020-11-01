package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hieeway.hieeway.Interface.ChangePictureListener;

import java.util.HashMap;

public class PictureChangeDialog extends Dialog {


    Boolean active;
    ChangePictureListener changePictureListener;
    String imageUrl;
    String picType;
    Context context;
    private TextView yes_btn, no_btn, live_message_txt;
    private String userId;
    private String username;

    public PictureChangeDialog(@NonNull Context context, Boolean active, String imageUrl, ChangePictureListener changePictureListener, String picType) {
        super(context);
        this.active = active;
        this.changePictureListener = changePictureListener;
        this.imageUrl = imageUrl;
        this.picType = picType;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.pic_change_dialog);


        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);


        yes_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        live_message_txt.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));

        if (active)
            live_message_txt.setText("Edit your active text picture");
        else
            live_message_txt.setText("Edit your profile picture");


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePictureListener.changePicture(active);
                dismiss();
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePicture(picType);
                changePictureListener.removedPicture(active);

            }
        });


    }

    private void removePicture(String picType) {


        if (picType.equals("photo") || picType.equals("activePhoto")) {

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put(picType, "default");
            FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap);


            // Toast.makeText(context, "ImageUrl: "+imageUrl, Toast.LENGTH_LONG).show();

            try {

                FirebaseStorage storage = FirebaseStorage.getInstance();

                StorageReference storageRef = storage.getReferenceFromUrl(imageUrl);

// Create a reference to the file to delete


// Delete the file
                storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // File deleted successfully
                        // Toast.makeText(context, "Deleted image", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Uh-oh, an error occurred!
                        // Toast.makeText(context, "Could'nt Delete image because " + exception.toString(), Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                //
                //Toast.makeText(context, "imageUrl: "+imageUrl+"\nCould'nt Delete image because " + e.toString(), Toast.LENGTH_LONG).show();
            }

        }


        dismiss();


    }


}
