package com.shubhamkislay.jetpacklogin.Interface;

import com.google.firebase.database.DatabaseReference;

public interface GoogleButtonListener {


    void onSignInClicked();

    void onGoogleButtonPressed(String email, String name, String photourl, DatabaseReference reference,String device_token);

    void onGoogleButtonPressedKeyAvailable(String email, String name, String photourl, DatabaseReference reference, String device_token,String public_key, String publickeyid);
}
