package com.shubhamkislay.jetpacklogin.Interface;

import com.google.firebase.database.DatabaseReference;

public interface UsernameListener {


    void onUsernameListener(String username,String name, String photourl, DatabaseReference reference, String device_token, String public_key, String publickeyid);
}
