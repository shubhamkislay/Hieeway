package com.shubhamkislay.jetpacklogin;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;

import java.util.HashMap;
import java.util.List;

public class EphemeralPhotoActivity extends AppCompatActivity {


    ImageView dispPhoto;
    ProgressBar progressBar;

    String userIdChattingWith, photoUrl, sender;
    List<ChatMessage> photoList;
    String mKey;
    Button nextBtn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //setContentView(R.layout.activity_new__message_);

        setContentView(R.layout.activity_ephermeral_photo);

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.VISIBLE);




        dispPhoto =findViewById(R.id.fullscreen_content);
        nextBtn = findViewById(R.id.next_btn);

        Intent intent = getIntent();


            photoUrl = intent.getStringExtra("photoUrl");

            mKey = intent.getStringExtra("mKey");

            sender = intent.getStringExtra("sender");


        userIdChattingWith = intent.getStringExtra("userIdChattingWith");


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPhotos();
            }
        });



        //Glide.with(DispImageActivity.this).load(photoUri).into(dispPhoto);



        loadPhotos();

        //for()




    }

    public void loadPhotos() {






            Glide.with(EphemeralPhotoActivity.this)
                    .load(photoUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            //setContentView(R.layout.activity_disp_image);

                            //dispPhoto.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            dispPhoto.setVisibility(View.VISIBLE);

                          //  if(userIdChattingWith.equals())
                            DatabaseReference deletePhotoMessageSender = FirebaseDatabase.getInstance().getReference("Messages")
                                    .child(userIdChattingWith)
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(mKey);
                            DatabaseReference deletePhotoMessageReceiver = FirebaseDatabase.getInstance().getReference("Messages")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIdChattingWith)
                                    .child(mKey);

                            if(userIdChattingWith.equals(sender)) {
                              //  deletePhotoMessageSender.removeValue();

                                HashMap<String,Object> updateSeen = new HashMap<>();
                                updateSeen.put("seen","seen");
                                updateSeen.put("sentStatus","sent");
                                updateSeen.put("photourl", "played");

                                StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(photoUrl);
                                photoRef.delete();

                                deletePhotoMessageSender.updateChildren(updateSeen);
                                deletePhotoMessageReceiver.removeValue();

                             //   deletePhotoMessageReceiver.getParent().updateChildren(updateSeen);
                            }




                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {


                                 //   if (FirebaseAuth.getInstance().getCurrentUser().getUid().equals(receiver)) {

                                        //dispPhoto.setVisibility(View.GONE);


                                        /*DatabaseReference deletePhotoMessageSender = FirebaseDatabase.getInstance().getReference("Messages")
                                                .child(userIdChattingWith)
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(mKey);
                                        DatabaseReference deletePhotoMessageReceiver = FirebaseDatabase.getInstance().getReference("Messages")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(userIdChattingWith)
                                                .child(mKey);*/


                                    /*String HashMap<String,Object> deletePhotoHash = new HashMap<>();

                                    deletePhotoHash.put("photo","default");*/



                                    /*deletePhotoMessageReceiver.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                            for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                            {


                                                Message photoMessage = snapshot.getValue(Message.class);

                                                if(photoMessage.getmKey().equals(mKey))
                                                {
                                                    Toast.makeText(DispImageActivity.this,"mKey Matches" , Toast.LENGTH_SHORT).show();
                                                    deletePhotoMessageReceiver.removeValue();
                                                    deletePhotoMessageSender.removeValue();

                                                }



                                            }


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
*/


                                        finish();






                                }
                            }, 7000);


                            return false;
                        }
                    }).into(dispPhoto);



    }

}
