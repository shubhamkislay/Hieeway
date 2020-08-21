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

public class RemoveFriendDialog extends Dialog {


    private TextView yes_btn, no_btn, live_message_txt;
    private String userId;
    private String username;

    public RemoveFriendDialog(@NonNull Context context, String userId, String username) {
        super(context);
        this.userId = userId;
        this.username = username;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.unfriend_dialog);


        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);


        yes_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        live_message_txt.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));

        live_message_txt.setText("Do you want to unfriend " + username + "?");


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeFriend();
                dismiss();
            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    private void removeFriend() {

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);

        DatabaseReference chatUserReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);

        DatabaseReference messageUserReference = FirebaseDatabase.getInstance().getReference("Messages")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userId);

        chatUserReference.removeValue();
        requestSentReference.removeValue();
        messageUserReference.removeValue();


        DatabaseReference requestReceiveReference = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference chatOtherReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        DatabaseReference messageOtherReference = FirebaseDatabase.getInstance().getReference("Messages")
                .child(userId)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        chatOtherReference.removeValue();
        messageOtherReference.removeValue();
        requestReceiveReference.removeValue();

    }
}
