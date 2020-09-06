package com.hieeway.hieeway;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.hieeway.hieeway.Model.User;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import static com.hieeway.hieeway.MainActivity.DEVICE_TOKEN;
import static com.hieeway.hieeway.MainActivity.EMAIL;
import static com.hieeway.hieeway.MainActivity.NAME;
import static com.hieeway.hieeway.MainActivity.PHOTO_URL;
import static com.hieeway.hieeway.MainActivity.PRIVATE_KEY;
import static com.hieeway.hieeway.MainActivity.PUBLIC_KEY;
import static com.hieeway.hieeway.MainActivity.PUBLIC_KEY_ID;
import static com.hieeway.hieeway.MainActivity.SHARED_PREFS;
import static com.hieeway.hieeway.MainActivity.USER_ID;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterAuthenticateActivity extends AppCompatActivity {

    Button register_account, login_account;
    EditText username, email, password;
    public static final String PHONE = "phone";
    private static final String USERNAME = "username";
    public static final String ACTIVE_PHOTO = "activePhoto";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_register_authenticate);


        register_account = findViewById(R.id.register_account);
        login_account = findViewById(R.id.login_account);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);


        register_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().length() > 5 && email.getText().toString().length() > 4
                        && username.getText().toString().length() > 3) {
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        /*FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if(task.isSuccessful())
                                                        {

                                                            generateKeys(task,email.getText().toString(),username.getText().toString(),"default");

                                                        }
                                                    }
                                                });*/


                                        HashMap<String, Object> registerHash = new HashMap<>();
                                        registerHash.put("email", email.getText().toString());
                                        registerHash.put("name", username.getText().toString());
                                        registerHash.put("username", username.getText().toString());
                                        registerHash.put("token", "xyz");
                                        registerHash.put("online", true);
                                        registerHash.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        registerHash.put("publicKey", "xyz");
                                        registerHash.put("publicKeyId", "xyz");
                                        registerHash.put("feeling", "happy");
                                        registerHash.put("bio", "great life");
                                        registerHash.put("photo", "default");
                                        registerHash.put("activePhoto", "default");
                                        registerHash.put("phonenumber", "default");
                                        registerHash.put("feelingIcon", "default");
                                        registerHash.put("deleteUser", false);
                                        registerHash.put("synced", false);

                                        FirebaseDatabase.getInstance().getReference("Users")
                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .updateChildren(registerHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseAuth.getInstance().signOut();
                                                Toast.makeText(RegisterAuthenticateActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                            }
                                        });


                                    }

                                }
                            });
                } else {
                    Toast.makeText(RegisterAuthenticateActivity.this, "Put data properly", Toast.LENGTH_SHORT).show();
                }
            }
        });

        login_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (password.getText().toString().length() > 5 && email.getText().toString().length() > 4
                        && username.getText().toString().length() > 3) {
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {

                                        FirebaseInstanceId.getInstance().getInstanceId()
                                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                        if (task.isSuccessful()) {

                                                            generateKeys(task, email.getText().toString(), username.getText().toString(), "default");

                                                        }
                                                    }
                                                });

                                    }
                                }
                            });
                } else {
                    Toast.makeText(RegisterAuthenticateActivity.this, "Put data properly", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void generateKeys(Task<InstanceIdResult> task, String email, String username, String photourl) {
        KeyPair kp = getKeyPair();
        PublicKey publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

        String publicKeyText = publicKeyBytesBase64;


        PrivateKey privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));

        String privateKeyText = privateKeyBytesBase64;


        saveKeys(publicKeyText, privateKeyText, task, email, username, photourl);

    }


    private void saveKeys(final String publicKey, final String privateKey, final Task<InstanceIdResult> task, final String email, final String username, final String photourl) {


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String device_token = task.getResult().getToken();

        final String publicKeyId = databaseReference.push().getKey();

        editor.putString(PRIVATE_KEY, privateKey);
        editor.putString(PUBLIC_KEY, publicKey);
        editor.putString(USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
        editor.putString(PUBLIC_KEY_ID, publicKeyId);
        editor.putString(USERNAME, username);
        editor.putString(NAME, username);
        editor.putString(EMAIL, email);
        editor.putString(PHOTO_URL, photourl);
        editor.putString(ACTIVE_PHOTO, photourl);
        editor.putString(DEVICE_TOKEN, device_token);

        editor.apply();


        loginNow(device_token, publicKey, publicKeyId);


    }


    public KeyPair getKeyPair() {
        KeyPair kp = null;
        try {
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            kp = kpg.generateKeyPair();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return kp;
    }

    public void loginNow(final String device_token, final String public_key, final String publickeyid) {


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    User user = dataSnapshot.getValue(User.class);


                    HashMap<String, Object> hashMap = new HashMap<>();

                    try {
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PHOTO_URL, user.getPhoto());
                        editor.putString(ACTIVE_PHOTO, user.getActivePhoto());

                        try {
                            editor.putString(PHONE, user.getPhonenumber());
                        } catch (Exception e) {
                            editor.putString(PHONE, "default");
                        }
                        editor.apply();

                        hashMap.put("token", device_token);
                        hashMap.put(PUBLIC_KEY, public_key);
                        hashMap.put("publicKeyId", publickeyid);
                    } catch (Exception e) {
                        //
                    }

                    databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            startActivity(new Intent(RegisterAuthenticateActivity.this, NavButtonTest.class));
                            finish();
                        }
                    });


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
