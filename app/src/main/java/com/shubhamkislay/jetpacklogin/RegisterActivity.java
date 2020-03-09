package com.shubhamkislay.jetpacklogin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import static com.shubhamkislay.jetpacklogin.LoginActivity.PUBLIC_KEY_ID;

public class RegisterActivity extends AppCompatActivity {



    Button register;
    EditText username, email, password;
    String usernameStr, emailStr, passwordStr;
    FirebaseAuth firebaseAuth;
    ProgressDialog pg;
    String device_token;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    String publicKeyText, privateKeyText, encryptedMessage, originalMessage, decryptedMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        firebaseAuth = FirebaseAuth.getInstance();



        register = findViewById(R.id.registerrgtr);

        username = findViewById(R.id.usernamergtr);
        email = findViewById(R.id.emailrgtr);
        password = findViewById(R.id.passwordrgtr);
        pg =new ProgressDialog(RegisterActivity.this);
        pg.setTitle("Registering user");


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                usernameStr = username.getText().toString();
                emailStr = email.getText().toString();
                passwordStr = password.getText().toString();
                pg.show();





                if(TextUtils.isEmpty(usernameStr)||TextUtils.isEmpty(emailStr)||TextUtils.isEmpty(passwordStr))
                {
                    Toast.makeText(RegisterActivity.this, "All fields required!", Toast.LENGTH_SHORT).show();
                    pg.dismiss();
                }
                else
                {
                    firebaseAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful())
                            {



                                FirebaseInstanceId.getInstance().getInstanceId()
                                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                                if (!task.isSuccessful()) {
                                                    ///Log.w(TAG, "getInstanceId failed", task.getException());
                                                    return;
                                                }

                                                generateKeys(task);

                                            }
                                        });



                            }

                            else
                            {
                                Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }
            }
        });


    }

    private void saveKeys(String publicKey,String privateKey,Task<InstanceIdResult> task) {


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseAuth.getCurrentUser().getUid());

        String publicKeyId = databaseReference.push().getKey();

        editor.putString(PRIVATE_KEY,privateKey);
        editor.putString(PUBLIC_KEY,publicKey);
        editor.putString(PUBLIC_KEY_ID,publicKeyId);
        editor.putString(USER_ID,FirebaseAuth.getInstance().getCurrentUser().getUid());

        editor.apply();

        device_token = task.getResult().getToken();


        HashMap<String, Object> registerMap = new HashMap<>();
        registerMap.put("email", emailStr);
        registerMap.put("phonenumber", "default");
        registerMap.put("photo","default");
        registerMap.put("username", usernameStr);
        registerMap.put("userid", firebaseAuth.getCurrentUser().getUid());
        registerMap.put("token",device_token);
        registerMap.put(PUBLIC_KEY,publicKey);
        registerMap.put("publicKeyId",publicKeyId);

        databaseReference.setValue(registerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                pg.dismiss();
                startActivity(new Intent(RegisterActivity.this,NavButtonTest.class));
                finish();

            }
        });

      //  Toast.makeText(RegisterActivity.this,"Keys saved",Toast.LENGTH_SHORT).show();


    }

    private void generateKeys(Task<InstanceIdResult> task)
    {
        KeyPair kp = getKeyPair();
        PublicKey publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String publicKeyBytesBase64 = new String(Base64.encode(publicKeyBytes, Base64.DEFAULT));

        publicKeyText = publicKeyBytesBase64;


        PrivateKey privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String privateKeyBytesBase64 = new String(Base64.encode(privateKeyBytes, Base64.DEFAULT));

        privateKeyText = privateKeyBytesBase64;


        saveKeys(publicKeyText,privateKeyText,task);

    }

    public  KeyPair getKeyPair() {
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






}
