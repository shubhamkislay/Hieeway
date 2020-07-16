package com.hieeway.hieeway.Fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
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
import com.hieeway.hieeway.Interface.GoogleButtonListener;
import com.hieeway.hieeway.LoginActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.RegisterAuthenticateActivity;
import com.hieeway.hieeway.TypeWriter;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

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
    public static final String PHOTO_URL = "photourl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String DEVICE_TOKEN = "devicetoken";
    private static final int RC_SIGN_IN = 1;
    FirebaseAuth firebaseAuth;
    String device_token;
    DatabaseReference databaseReference;
    Button email_login;
    ProgressBar progressBarOne, progressBarTwo;
    // [END declare_auth]
    RelativeLayout relativeLayout;
    String publicKeyText, privateKeyText, encryptedMessage, originalMessage, decryptedMessage, userID;
    LinearLayout google_signin;
    GoogleButtonListener googleButtonListener;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    TypeWriter greet_text;


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

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        mAuth = FirebaseAuth.getInstance();

        google_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //signIn();

                googleButtonListener.onSignInClicked();

            }
        });

        email_login = view.findViewById(R.id.email_login);

        greet_text = view.findViewById(R.id.greet_text);

        email_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), RegisterAuthenticateActivity.class));
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

        greet_text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                greet_text.animateText("Hi");
                greet_text.setCharacterDelay(150);

                greet_text.setTextSize(100);
                greet_text.animate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        greet_text.setText(" ");
                        greet_text.setTextSize(28);
                        greet_text.setCharacterDelay(60);
                        greet_text.animateText("Welcome to the next generation of messaging.");
                        greet_text.animate();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        greet_text.setText(" ");
                        greet_text.setTextSize(24);
                        greet_text.setCharacterDelay(75);
                        greet_text.animateText("Let's set up your account!");
                        greet_text.animate();

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                greet_text.setText(" ");
                                greet_text.setTextSize(24);
                                greet_text.setCharacterDelay(50);
                                greet_text.animateText("Click the google sign in button below to continue");
                                greet_text.animate();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        google_signin.setVisibility(View.VISIBLE);
                                        google_signin.animate().alpha(1.0f).setDuration(500);
                                        google_signin.animate().translationYBy(-50).setDuration(500);
                                    }
                                },2500);


                            }
                        },3000);
                    }
                }, 4000);

                    }
                },2000);

            }
        },1000);



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

 /*   @Override
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
    }*/


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


        saveKeys(publicKeyText,privateKeyText,task,email,name,photourl);

    }

    public void firebaseAuthWithGoogle(final GoogleSignInAccount acct,final  String name, final String surname) {
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

                                            try {
                /*                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
            *//*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());*//*


                                                editor.putString(NAME, name+" "+surname);
                                                editor.putString(EMAIL, acct.getEmail());
                                                editor.putString(USER_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                                editor.putString(PHOTO_URL, acct.getPhotoUrl().toString());


                                                editor.apply();*/
                                            }catch (Exception e)
                                            {

                                            }

                                            /**
                                             * Uncomment the following to enable saved keys when logging back in
                                             */
/*                                            if(privateKeyText!=null&&userID!=null&&userID.equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                            {



                                                // Get new Instance ID token
                                                device_token = task.getResult().getToken();

                                                // Log and toast
                                                // String msg = device_token;

                                                // Toast.makeText(.this, msg, Toast.LENGTH_SHORT).show();
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

                                                *//*startActivity(new Intent(.this,NavButtonTest.class));
                                             *//**//*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*//**//*
                                                finish();*//*

                                                google_signin.setVisibility(View.GONE);
                                                googleButtonListener.onGoogleButtonPressed(acct.getEmail(),name+" "+surname,acct.getPhotoUrl().toString(),databaseReference,device_token);
                                            }
                                            else
                                            {
                                                generateKeys(task,acct.getEmail(),name+" "+surname,acct.getPhotoUrl().toString());
                                            }*/
                                            generateKeys(task, acct.getEmail(), name + " " + surname, acct.getPhotoUrl().toString());


                                        }
                                    });

                            Toast.makeText(getActivity(),"Welcome "+name,Toast.LENGTH_SHORT).show();
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

    private void saveKeys(final String publicKey,final String privateKey,final Task<InstanceIdResult> task,final String email,final String name,final String photourl) {


        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        device_token = task.getResult().getToken();

        final String publicKeyId = databaseReference.push().getKey();

        editor.putString(PRIVATE_KEY,privateKey);
        editor.putString(PUBLIC_KEY,publicKey);
        editor.putString(USER_ID,FirebaseAuth.getInstance().getCurrentUser().getUid());
        editor.putString(PUBLIC_KEY_ID,publicKeyId);
        editor.putString(NAME, name);
        editor.putString(EMAIL, email);
        editor.putString(PHOTO_URL, photourl);
        editor.putString(DEVICE_TOKEN, device_token);

        editor.apply();



        // Log and toast
        // String msg = device_token;

        // Toast.makeText(.this, msg, Toast.LENGTH_SHORT).show();



/*        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("token",device_token);
        hashMap.put(PUBLIC_KEY,publicKey);
        hashMap.put("publicKeyId",publicKeyId);*/

       // databaseReference.updateChildren(hashMap);

        google_signin.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                googleButtonListener.onGoogleButtonPressedKeyAvailable(email,name,photourl,databaseReference,device_token,publicKey,publicKeyId);

            }
        },500);

        /*startActivity(new Intent(.this,NavButtonTest.class));
                                                *//*progressBarOne.setVisibility(View.GONE);
                                                progressBarTwo.setVisibility(View.GONE);*//*
        finish();*/


    }




}
