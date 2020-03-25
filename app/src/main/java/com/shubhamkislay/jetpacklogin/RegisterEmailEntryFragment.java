package com.shubhamkislay.jetpacklogin;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.shubhamkislay.jetpacklogin.Interface.GoogleButtonListener;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterEmailEntryFragment extends Fragment {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth firebaseAuth;
    String device_token;
    DatabaseReference databaseReference;
    ProgressBar progressBarOne, progressBarTwo;
    // [END declare_auth]
    RelativeLayout relativeLayout;
    String publicKeyText, privateKeyText, encryptedMessage, originalMessage, decryptedMessage, userID;
    LinearLayout google_signin;
    GoogleButtonListener googleButtonListener;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    public RegisterEmailEntryFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_email_entry, container, false);

        google_signin = view.findViewById(R.id.google_signin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        mAuth = FirebaseAuth.getInstance();

        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signIn();

            }
        });

        google_signin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    google_signin.animate().scaleX(0.76f).scaleY(0.76f).setDuration(0);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    google_signin.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                else
                {
                    google_signin.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                return false;
            }
        });


        return view;
    }

    public void setGoogleButtonListener(GoogleButtonListener googleButtonListener)
    {
        this.googleButtonListener = googleButtonListener;
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


    private void LoadKeys()
    {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        userID = sharedPreferences.getString(USER_ID,null);



    }

    private void generateKeys(Task<InstanceIdResult> task, String email, String name, String photourl)
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


        saveKeys(publicKeyText,privateKeyText,task,  email,  name,  photourl);

    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct,final  String name, final String surname) {
        // Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user.
                        /*Log.w(TAG, "signInWithCredential:failure", task.getException());
                           // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                            updateUI(null);*/
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

                                                /*relativeLayout.setEnabled(true);
                                                loginBtn.setVisibility(View.VISIBLE);
                                                progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*/
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

                                                try {
                                                    hashMap.put("token", device_token);
                                                    hashMap.put("photo", acct.getPhotoUrl().toString());
                                                }
                                                catch (NullPointerException ne)
                                                {
                                                    hashMap = new HashMap<>();
                                                    hashMap.put("token", device_token);
                                                }

                                                //acct.getPhotoUrl().

                                                databaseReference.updateChildren(hashMap);

                                                /*startActivity(new Intent(LoginActivity.this,NavButtonTest.class));
                                                *//*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*//*
                                                finish();*/

                                                google_signin.setVisibility(View.GONE);
                                                googleButtonListener.onGoogleButtonPressed(acct.getEmail(),name+" "+surname,acct.getPhotoUrl().toString());
                                            }
                                            else
                                            {
                                                generateKeys(task,acct.getEmail(),name+" "+surname,acct.getPhotoUrl().toString());
                                            }


                                        }
                                    });

                            Toast.makeText(getActivity(),name+" "+surname+", your login is Successful",Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getContext(), "Login failed", Toast.LENGTH_SHORT).show();

                        // ...
                    }
                });
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

    private void saveKeys(String publicKey,String privateKey,Task<InstanceIdResult> task, String email, String name, String photourl) {


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
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

        google_signin.setVisibility(View.GONE);
        googleButtonListener.onGoogleButtonPressed(email,name,photourl);

        /*startActivity(new Intent(LoginActivity.this,NavButtonTest.class));
                                                *//*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*//*
        finish();*/


    }




}
