package com.shubhamkislay.jetpacklogin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    EditText email, password;
    String emailStr, passwordStr;
    Button loginBtn;
    FirebaseAuth firebaseAuth;
    String device_token;
    DatabaseReference databaseReference;
    ProgressBar progressBarOne, progressBarTwo;
    RelativeLayout relativeLayout;
    String publicKeyText, privateKeyText, encryptedMessage, originalMessage, decryptedMessage, userID;

    Button google_signin;

    private FirebaseAuth mAuth;
    // [END declare_auth]

    private GoogleSignInClient mGoogleSignInClient;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        google_signin = findViewById(R.id.google_signin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mAuth = FirebaseAuth.getInstance();

        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });



        firebaseAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        relativeLayout = findViewById(R.id.login);

        loginBtn = findViewById(R.id.login_btn);
        progressBarOne = findViewById(R.id.progress_one);

        progressBarTwo = findViewById(R.id.progress_two);


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startLoging();
            }
        });


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLoging();
            }
        });

        LoadKeys();

    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account,account.getGivenName(), account.getFamilyName());


            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
               // Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct,final  String name, final String surname) {
       // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            /*Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);*/
                            FirebaseInstanceId.getInstance().getInstanceId()
                                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                            if (!task.isSuccessful()) {
                                                ///Log.w(TAG, "getInstanceId failed", task.getException());

                                                relativeLayout.setEnabled(true);
                                                loginBtn.setVisibility(View.VISIBLE);
                                                progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);
                                                return;
                                            }

                                            if(privateKeyText!=null&&userID!=null&&userID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                            {

                                                // Get new Instance ID token
                                                device_token = task.getResult().getToken();

                                                // Log and toast
                                                // String msg = device_token;

                                                // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                                databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                                                HashMap<String, Object> hashMap = new HashMap<>();

                                                hashMap.put("token",device_token);
                                                hashMap.put("photo",acct.getPhotoUrl().toString());

                                                //acct.getPhotoUrl().

                                                databaseReference.updateChildren(hashMap);

                                                startActivity(new Intent(LoginActivity.this,NavButtonTest.class));
                                                /*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*/
                                                finish();
                                            }
                                            else
                                            {
                                                generateKeys(task);
                                            }


                                        }
                                    });

                            Toast.makeText(LoginActivity.this,/*name+" "+surname+*/acct.getPhotoUrl()+", your login is Successful",Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            /*Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);*/

                            Toast.makeText(LoginActivity.this,"Login failed",Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void startLoging() {
        relativeLayout.setEnabled(false);
        loginBtn.setVisibility(View.GONE);
        progressBarOne.setVisibility(View.VISIBLE);
        progressBarTwo.setVisibility(View.VISIBLE);
        emailStr = email.getText().toString();
        passwordStr = password.getText().toString();

        if(TextUtils.isEmpty(emailStr)||TextUtils.isEmpty(passwordStr))
        {
            Toast.makeText(LoginActivity.this, "All field are required!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            firebaseAuth.signInWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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

                                            relativeLayout.setEnabled(true);
                                            loginBtn.setVisibility(View.VISIBLE);
                                            progressBarOne.setVisibility(View.GONE);
                                            progressBarTwo.setVisibility(View.GONE);
                                            return;
                                        }

                                        if(privateKeyText!=null&&userID!=null&&userID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                        {

                                            // Get new Instance ID token
                                            device_token = task.getResult().getToken();

                                            // Log and toast
                                            // String msg = device_token;

                                            // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                                            HashMap<String, Object> hashMap = new HashMap<>();

                                            hashMap.put("token",device_token);

                                            databaseReference.updateChildren(hashMap);

                                            startActivity(new Intent(LoginActivity.this,NavButtonTest.class));
                                                /*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*/
                                            finish();
                                        }
                                        else
                                        {
                                            generateKeys(task);
                                        }


                                    }
                                });



                    }
                    else
                    {

                        relativeLayout.setEnabled(true);
                        loginBtn.setVisibility(View.VISIBLE);
                        progressBarOne.setVisibility(View.GONE);
                        progressBarTwo.setVisibility(View.GONE);


                        Toast.makeText(LoginActivity.this,  task.getException().toString(), Toast.LENGTH_LONG).show();

                    }

                }
            });
        }
    }

    private void LoadKeys()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        userID = sharedPreferences.getString(USER_ID,null);



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

    private void saveKeys(String publicKey,String privateKey,Task<InstanceIdResult> task) {


        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        String publicKeyId = databaseReference.push().getKey();

        editor.putString(PRIVATE_KEY,privateKey);
        editor.putString(PUBLIC_KEY,publicKey);
        editor.putString(USER_ID,FirebaseAuth.getInstance().getCurrentUser().getUid());
        editor.putString(PUBLIC_KEY_ID,publicKeyId);

        editor.apply();

        device_token = task.getResult().getToken();

        // Log and toast
        // String msg = device_token;

        // Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();



        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("token",device_token);
        hashMap.put(PUBLIC_KEY,publicKey);
        hashMap.put("publicKeyId",publicKeyId);

        databaseReference.updateChildren(hashMap);

        startActivity(new Intent(LoginActivity.this,NavButtonTest.class));
                                                /*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*/
        finish();


    }
}
