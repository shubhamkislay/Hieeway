package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Interface.DeleteUserListener;
import com.hieeway.hieeway.Interface.EditProfileOptionListener;
import com.hieeway.hieeway.Interface.RechargeGemsListener;
import com.hieeway.hieeway.Model.ChatMessage;

import java.util.HashMap;

public class RecchargeGemsDialog extends Dialog {
    public Context context;
    boolean continueTransaction = true;
    String userIdChattingWith;
    private DeleteUserListener deleteUserListener;
    private TextView yes_btn, no_btn, live_message_txt, bottom_txt;
    private RelativeLayout progress_bar;
    private RelativeLayout bottom_msg;
    private RechargeGemsListener rechargeGemsListener;

    public RecchargeGemsDialog(@NonNull Context context, RechargeGemsListener rechargeGemsListener, String userIdChattingWith) {
        super(context);
        this.rechargeGemsListener = rechargeGemsListener;
        this.context = context;
        this.userIdChattingWith = userIdChattingWith;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.recharge_gems_dialog);

        yes_btn = findViewById(R.id.yes_btn);
        no_btn = findViewById(R.id.no_btn);
        live_message_txt = findViewById(R.id.live_message_txt);
        progress_bar = findViewById(R.id.progress_bar);
        bottom_msg = findViewById(R.id.bottom_msg);
        bottom_txt = findViewById(R.id.bottom_txt);


        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rechargeReadGems();
                progress_bar.setVisibility(View.VISIBLE);
                bottom_txt.setText(" ");
                bottom_msg.setVisibility(View.GONE);
                yes_btn.setVisibility(View.GONE);
                no_btn.setVisibility(View.GONE);
                live_message_txt.setText("Recharging reading gems...");
            }
        });


        yes_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        no_btn.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/samsungsharpsans-bold.otf"));

    }


    private void rechargeReadGems() {

        TaskCompletionSource<Boolean> booleanTaskCompletionSource = new TaskCompletionSource<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Messages")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(userIdChattingWith);


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                                try {

                                    if (chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                        databaseReference.child(chatMessage.getMessageId()).removeValue();

                                    }
                                } catch (Exception e) {
                                    //
                                }


                            }


                            booleanTaskCompletionSource.setResult(true);


                        } else {
                            booleanTaskCompletionSource.setResult(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();


        Task<Boolean> booleanTask = booleanTaskCompletionSource.getTask();


        booleanTask.addOnCompleteListener(new OnCompleteListener<Boolean>() {
            @Override
            public void onComplete(@NonNull Task<Boolean> task) {

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if (task.isSuccessful()) {


                            /*DatabaseReference updateGemsRef = FirebaseDatabase.getInstance().getReference("ChatList")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(userIdChattingWith);

                            HashMap<String,Object> updateHash = new HashMap<>();

                            updateHash.put("gemCount",2);
                            updateGemsRef.updateChildren(updateHash);*/

                            rechargeGemsListener.rechargeGems();

                            dismiss();


                        }

                    }
                }, 1000);


            }
        });


    }

}
