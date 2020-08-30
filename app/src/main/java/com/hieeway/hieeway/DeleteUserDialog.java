package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hieeway.hieeway.Interface.DeleteUserListener;
import com.hieeway.hieeway.Model.User;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class DeleteUserDialog extends Dialog {


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public Context context;
    boolean continueTransaction = true;
    private DeleteUserListener deleteUserListener;
    private TextView yes_btn, no_btn, live_message_txt, bottom_txt;
    private RelativeLayout progress_bar;
    private RelativeLayout bottom_msg;



    public DeleteUserDialog(@NonNull Context context, DeleteUserListener deleteUserListener) {
        super(context);
        this.deleteUserListener = deleteUserListener;
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.delete_user_dialog);

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

                deleteUser();
                progress_bar.setVisibility(View.VISIBLE);
                bottom_txt.setText(" ");
                bottom_msg.setVisibility(View.GONE);
                yes_btn.setVisibility(View.GONE);
                no_btn.setVisibility(View.GONE);
                live_message_txt.setText("Deleting Account...");
            }
        });
    }

    private void deleteUser() {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String photo = sharedPreferences.getString(PHOTO_URL, "default");
        String userID = sharedPreferences.getString(USER_ID, "default");


        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("dummy", userID);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (continueTransaction) {
                    continueTransaction = false;
                    Toast.makeText(context, "Error delete User. Check your internet connection and try again after some time", Toast.LENGTH_LONG).show();
                    dismiss();
                }

            }
        }, 5000);

        FirebaseDatabase.getInstance().getReference("Dummy")
                .child(userID)
                .updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (continueTransaction) {
                    continueTransaction = false;
                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(userID)
                            .runTransaction(new Transaction.Handler() {
                                @NonNull
                                @Override
                                public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                                    if (currentData == null) {
                                        //  Toast.makeText(context, "Trying to delete", Toast.LENGTH_SHORT).show();
                                        Log.v("Transaction", "currentData null");
                                        return null;
                                    }


                                    try {
                                        User user = currentData.getValue(User.class);
                                        // Toast.makeText(context, "User", Toast.LENGTH_SHORT).show();
                                        Log.v("Transaction", "fetching user");
                                        if (!user.getDeleteUser()) {

                                            Log.v("Transaction", "fetched user data");
                                            //  Toast.makeText(context, "User Data", Toast.LENGTH_SHORT).show();
                                            user.setDeleteUser(true);
                                            currentData.setValue(user);

                                            // Toast.makeText(context, "SetData Data", Toast.LENGTH_SHORT).show();

                                            return Transaction.success(currentData);
                                        }
                                    } catch (Exception e) {

//                            Toast.makeText(context, "Error fetching user Data", Toast.LENGTH_SHORT).show();
                                        Log.v("Transaction", e.toString());

                                    }


                                    return null;


                                }

                                @Override
                                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                                    // Toast.makeText(context, "Trying to delete", Toast.LENGTH_SHORT).show();

                                    if (committed) {
                                        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(photo);
                                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // File deleted successfully
                                                // FirebaseAuth.getInstance().getCurrentUser().delete();
                                                FirebaseAuth.getInstance().signOut();

                                                deleteUserListener.changeActivity();
                                                dismiss();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception exception) {
                                                // Uh-oh, an error occurred!

                                                //FirebaseAuth.getInstance().getCurrentUser().delete();
                                                FirebaseAuth.getInstance().signOut();

                                                deleteUserListener.changeActivity();
                                                dismiss();

                                            }
                                        });
                                    } else {

                                        // Toast.makeText(context, "Error while deleting your account: " + error.getMessage(), Toast.LENGTH_LONG).show();
                                        Log.v("Transaction", "Error while deleting your account: " + error.getMessage());
                                    }
                                }
                            });

                }
            }
        });





/*        FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Emosubscribe")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Likes")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("LiveMessages")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Messages")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Music")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Reports")
                .child(userID)
                .removeValue();

        FirebaseDatabase.getInstance().getReference("Video")
                .child(userID)
                .removeValue();*/




    }
}
