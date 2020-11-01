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

public class LeaveGroupDialog extends Dialog {


    private TextView yes_btn, no_btn, live_message_txt;
    private String groupId;
    private String userId;

    public LeaveGroupDialog(@NonNull Context context, String groupId, String userId) {
        super(context);
        this.groupId = groupId;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.leave_group_dialog);


        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);


        yes_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_btn.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        live_message_txt.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeGroup();
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

    private void removeGroup() {

        DatabaseReference requestSentReference = FirebaseDatabase.getInstance().getReference("MyGroup")
                .child(userId)
                .child(groupId);

        DatabaseReference chatUserReference = FirebaseDatabase.getInstance().getReference("GroupMembers")
                .child(groupId)
                .child(userId);


        chatUserReference.removeValue();
        requestSentReference.removeValue();

        dismiss();
    }
}
