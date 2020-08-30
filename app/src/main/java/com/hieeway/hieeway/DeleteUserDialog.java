package com.hieeway.hieeway;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

import static android.content.Context.MODE_PRIVATE;

public class DeleteUserDialog extends Dialog {


    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public Context context;
    private TextView yes_btn, no_btn, live_message_txt;
    private DeleteUserListener deleteUserListener;

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
            }
        });
    }

    private void deleteUser() {

        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        String photo = sharedPreferences.getString(PHOTO_URL, "default");
        String userID = sharedPreferences.getString(USER_ID, "default");


        FirebaseDatabase.getInstance().getReference("Users")
                .child(userID)
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                        try {
                            User user = currentData.getValue(User.class);
                            if (!user.getDeleteUser()) {
                                user.setDeleteUser(true);
                                currentData.setValue(user);

                            }
                        } catch (Exception e) {

                        }


                        return Transaction.success(currentData);

                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                        if (committed) {
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(photo);
                            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    // File deleted successfully
                                    FirebaseAuth.getInstance().getCurrentUser().delete();

                                    deleteUserListener.changeActivity();
                                    dismiss();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Uh-oh, an error occurred!

                                    FirebaseAuth.getInstance().getCurrentUser().delete();

                                    deleteUserListener.changeActivity();
                                    dismiss();

                                }
                            });
                        } else {
                            Toast.makeText(context, "Error while deleting your account: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
