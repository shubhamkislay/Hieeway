package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Model.Friend;

import java.util.HashMap;

public class DeletePostDialog extends Dialog {


    private TextView yes_btn, no_btn, live_message_txt;
    private String postKey;
    private String userId;
    HashMap<String, Object> postHash;
    private DatabaseReference databaseReference;

    public DeletePostDialog(@NonNull Context context, String postKey, String userId) {

        super(context);
        this.postKey = postKey;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.delete_post_dialog);


        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);


        yes_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        live_message_txt.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removePost();
                //dismiss();
                hide();

            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dismiss();
                hide();
            }
        });


    }

    private void removePost() {


        //dismiss();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        postHash = new HashMap<>();
        postHash.put("MyPosts/" + userId + "/" + postKey, null);
        postHash.put("MyMusicPosts/" + userId + "/" + postKey, null);

        FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Friend friend = snapshot.getValue(Friend.class);
                                postHash.put("Post/" + friend.getFriendId() + "/" + postKey, null);
                                postHash.put("MusicPost/" + friend.getFriendId() + "/" + postKey, null);
                            }


                        }

                        databaseReference.updateChildren(postHash);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }
}
