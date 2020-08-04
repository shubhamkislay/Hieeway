package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.FirebaseDatabase;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.hieeway.hieeway.Model.Iso2Phone;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class PhoneAuthenticationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_HINT = 3;
    EditText enter_details;
    TextView authenticationPageTitle;
    TextView questionText;
    TextView displayDetails;
    RelativeLayout titleIndicatorLayout;
    LinearLayout choice_layout;
    Button agreeBtn, disagreeBtn;
    RelativeLayout number_indicator_box;
    Boolean checkingPhoneNumber = true;
    Boolean checkingPhoneNumberFromPhone = true;
    String phonenumber = "default";
    public static final String SHARED_PREFS = "sharedPrefs";
    private static final String PHONE = "phone";
    Boolean verifyOTP = false;
    GoogleApiClient mCredentialsApiClient;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    String verificationID;
    PhoneAuthProvider.ForceResendingToken forceResendingToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authentication);


        titleIndicatorLayout = findViewById(R.id.relativeLayout5);
        authenticationPageTitle = findViewById(R.id.authentication_title);
        number_indicator_box = findViewById(R.id.number_indicator_box);
        questionText = findViewById(R.id.question_txt);
        displayDetails = findViewById(R.id.phone_txt);
        enter_details = findViewById(R.id.enter_details);
        choice_layout = findViewById(R.id.choice_layout);
        agreeBtn = findViewById(R.id.agree_btn);
        disagreeBtn = findViewById(R.id.disagree_btn);

        authenticationPageTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        questionText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        displayDetails.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        enter_details.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        agreeBtn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        disagreeBtn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                updatePhoneNumber();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {

            }

            @Override
            public void onCodeSent(String firebaseVerificationID, PhoneAuthProvider.ForceResendingToken firebaseForceResendingToken) {
                //  super.onCodeSent(s, forceResendingToken);
                verificationID = firebaseVerificationID;
                forceResendingToken = firebaseForceResendingToken;
            }
        };


        agreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkingPhoneNumber) {
                    updatePhoneNumber();
                } else if (verifyOTP) {

                    authenticationPageTitle.setVisibility(View.VISIBLE);
                    titleIndicatorLayout.setVisibility(View.VISIBLE);
                    authenticationPageTitle.setText("Validating OTP");
                    number_indicator_box.setVisibility(View.GONE);
                    checkOTP();


                } else {

                    phonenumber = enter_details.getText().toString();
                    phonenumber = phonenumber.replace(" ", "");
                    phonenumber = phonenumber.replace("-", "");
                    phonenumber = phonenumber.replace("(", "");
                    phonenumber = phonenumber.replace(")", "");

                    if (!String.valueOf(phonenumber.charAt(0)).equals("+"))
                        enter_details.setError("Please add country code as the prefix. For eg. +91 for India");

                    else {
                        Toast.makeText(PhoneAuthenticationActivity.this, "Sending OTP", Toast.LENGTH_SHORT).show();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(phonenumber, 60, TimeUnit.SECONDS, PhoneAuthenticationActivity.this, mCallbacks);
                        enter_details.setText("");
                        agreeBtn.setText("Verify OTP");
                        questionText.setText("Enter the OTP received on " + phonenumber);
                        verifyOTP = true;
                    }
                }
            }
        });
        disagreeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkingPhoneNumberFromPhone) {

                    titleIndicatorLayout.setVisibility(View.VISIBLE);
                    authenticationPageTitle.setText("Checking phone number from google account now");
                    number_indicator_box.setVisibility(View.GONE);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                getCreadenticalApiClient();
                            } catch (Exception e) {

                            }
                        }
                    }, 1000);

                } else {

                    try {
                        // getCreadenticalApiClient();
                        initialiseOTPUI();
                    } catch (Exception e) {

                    }
                }
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                checkPhoneNumberFromDevice();
            }
        }, 1000);


    }

    private void updatePhoneNumber() {
        HashMap<String, Object> phoneHash = new HashMap<>();
        phoneHash.put("phonenumber", phonenumber);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .updateChildren(phoneHash);

        Toast.makeText(PhoneAuthenticationActivity.this, "Phone number verifed :)", Toast.LENGTH_SHORT);

        Intent data = new Intent();

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor = sharedPreferences.edit();
        editor.putString(PHONE, phonenumber);
        editor.apply();

//---set the data to pass back---
        data.setData(Uri.parse(phonenumber));
        setResult(RESULT_OK, data);
        finish();

    }

    private void checkOTP() {
        enter_details.getText().toString();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, enter_details.getText().toString());


        FirebaseAuth.getInstance().getCurrentUser().linkWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            updatePhoneNumber();
                        } else {
                            Toast.makeText(PhoneAuthenticationActivity.this, "Verification failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void initialiseOTPUI() {
        checkingPhoneNumber = false;
        titleIndicatorLayout.setVisibility(View.GONE);
        authenticationPageTitle.setVisibility(View.GONE);
        displayDetails.setVisibility(View.GONE);
        number_indicator_box.setVisibility(View.VISIBLE);
        enter_details.setVisibility(View.VISIBLE);
        questionText.setText("Enter your phone number with country code");
        enter_details.setText(getCountryIso());
        enter_details.setHint("type here");
        agreeBtn.setText("Send OTP");
        disagreeBtn.setVisibility(View.GONE);
    }


    private void checkPhoneNumberFromDevice() {


        if (//ContextCompat.checkSelfPermission(PhoneAuthenticationActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED
            // && ContextCompat.checkSelfPermission(PhoneAuthenticationActivity.this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED
            // &&
                ContextCompat.checkSelfPermission(PhoneAuthenticationActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            requestAllPermissions();
        } else {

            TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            String mPhoneNumber = tMgr.getLine1Number();

            if (mPhoneNumber != null) {
                int trimeSize = mPhoneNumber.length() - 10;
                if (trimeSize < 0)
                    trimeSize = 0;


                // Toast.makeText(getActivity(), "Phone number fetching: "+mPhoneNumber, Toast.LENGTH_SHORT).show();

                if (mPhoneNumber.length() > 6/*&& isValid(mPhoneNumber)*/) {

                    mPhoneNumber = tMgr.getLine1Number().substring(trimeSize);
                    phonenumber = mPhoneNumber;

                    phonenumber = phonenumber.replace(" ", "");
                    phonenumber = phonenumber.replace("-", "");
                    phonenumber = phonenumber.replace("(", "");
                    phonenumber = phonenumber.replace(")", "");

                    if (!String.valueOf(phonenumber.charAt(0)).equals("+"))
                        phonenumber = getCountryIso() + phonenumber;

                    displayDetails.setText(phonenumber);
                    /*HashMap<String, Object> phoneHash = new HashMap<>();
                    phoneHash.put("phonenumber", mPhoneNumber);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(phoneHash);*/

                    number_indicator_box.setVisibility(View.VISIBLE);
                    titleIndicatorLayout.setVisibility(View.GONE);

                } else {
                    //Toast.makeText(getActivity(), "Phone number not available", Toast.LENGTH_SHORT).show();
                    authenticationPageTitle.setText("Phone number not available, checking google account now");
                    try {
                        getCreadenticalApiClient();
                    } catch (Exception e) {

                    }
                    //Toast.makeText(PhoneAuthenticationActivity.this, "Starting phone verification", Toast.LENGTH_SHORT).show();
                    // verifyPhoneNumber();
                }
            } else {
                authenticationPageTitle.setText("Phone number not available, checking google account now");
                try {
                    getCreadenticalApiClient();
                } catch (Exception e) {

                }
                //Toast.makeText(PhoneAuthenticationActivity.this, "Starting phone verification", Toast.LENGTH_SHORT).show();
                //verifyPhoneNumber();
            }
        }
    }

    private void requestAllPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Dexter.withActivity(PhoneAuthenticationActivity.this)
                    .withPermissions(//Manifest.permission.READ_SMS,
                            // Manifest.permission.READ_PHONE_NUMBERS,
                            Manifest.permission.READ_PHONE_STATE)
                    .withListener(new MultiplePermissionsListener() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {

                            if (report.areAllPermissionsGranted()) {
                                // checkPhoneNumberFromDevice();
                                try {
                                    TelephonyManager tMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                                    @SuppressLint("MissingPermission") String mPhoneNumber = tMgr.getLine1Number();

                                    if (mPhoneNumber != null) {
                                        int trimeSize = mPhoneNumber.length() - 10;
                                        if (trimeSize < 0)
                                            trimeSize = 0;


                                        // Toast.makeText(getActivity(), "Phone number fetching: "+mPhoneNumber, Toast.LENGTH_SHORT).show();

                                        if (mPhoneNumber.length() > 6/*&& isValid(mPhoneNumber)*/) {

                                            mPhoneNumber = tMgr.getLine1Number().substring(trimeSize);
                                            phonenumber = mPhoneNumber;

                                            phonenumber = phonenumber.replace(" ", "");
                                            phonenumber = phonenumber.replace("-", "");
                                            phonenumber = phonenumber.replace("(", "");
                                            phonenumber = phonenumber.replace(")", "");

                                            if (!String.valueOf(phonenumber.charAt(0)).equals("+"))
                                                phonenumber = getCountryIso() + phonenumber;

                                            displayDetails.setText(phonenumber);
                    /*HashMap<String, Object> phoneHash = new HashMap<>();
                    phoneHash.put("phonenumber", mPhoneNumber);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(phoneHash);*/

                                            number_indicator_box.setVisibility(View.VISIBLE);
                                            titleIndicatorLayout.setVisibility(View.GONE);

                                        } else {
                                            //Toast.makeText(getActivity(), "Phone number not available", Toast.LENGTH_SHORT).show();
                                            authenticationPageTitle.setText("Phone number not available, checking google account now");
                                            try {
                                                getCreadenticalApiClient();
                                            } catch (Exception e) {

                                            }
                                            //Toast.makeText(PhoneAuthenticationActivity.this, "Starting phone verification", Toast.LENGTH_SHORT).show();
                                            // verifyPhoneNumber();
                                        }
                                    } else {
                                        authenticationPageTitle.setText("Phone number not available, checking google account now");
                                        try {
                                            getCreadenticalApiClient();
                                        } catch (Exception e) {

                                        }
                                        //Toast.makeText(PhoneAuthenticationActivity.this, "Starting phone verification", Toast.LENGTH_SHORT).show();
                                        //verifyPhoneNumber();
                                    }
                                } catch (Exception e) {

                                }


                            } else {


                                Toast.makeText(PhoneAuthenticationActivity.this, "Permission not given!", Toast.LENGTH_SHORT).show();

                                finish();


                            }

                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                            token.continuePermissionRequest();

                            // Toast.makeText(getActivity(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                    }).check();
        }
    }

    private String getCountryIso() {

        String iso = null;

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getSimCountryIso() != null)

            iso = telephonyManager.getSimCountryIso().toString();

        return Iso2Phone.getPhone(iso);


    }

    private void getCreadenticalApiClient() throws Exception {

        mCredentialsApiClient = new GoogleApiClient.Builder(PhoneAuthenticationActivity.this)
                .addConnectionCallbacks(this)
                .enableAutoManage(PhoneAuthenticationActivity.this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        showHint();
    }

    private void showHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0, new Bundle());
        } catch (IntentSender.SendIntentException e) {
            // Log.e("Login", "Could not start hint picker Intent", e);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                titleIndicatorLayout.setVisibility(View.GONE);
                number_indicator_box.setVisibility(View.VISIBLE);
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);
                //etMobile.setText(cred.getId().substring(3));

                phonenumber = cred.getId().substring(3);
                HashMap<String, Object> phoneHash = new HashMap<>();
                phoneHash.put("phonenumber", cred.getId().substring(3));

                phonenumber = phonenumber.replace(" ", "");
                phonenumber = phonenumber.replace("-", "");
                phonenumber = phonenumber.replace("(", "");
                phonenumber = phonenumber.replace(")", "");

                if (!String.valueOf(phonenumber.charAt(0)).equals("+"))
                    phonenumber = getCountryIso() + phonenumber;

                checkingPhoneNumberFromPhone = false;

                displayDetails.setText(phonenumber);

            } else {
                initialiseOTPUI();
            }
        }
    }
}
