package com.shubhamkislay.jetpacklogin.Interface;

import android.content.Context;

import androidx.annotation.NonNull;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class JavaScriptInterface {

    Context context;
    String userIdChattingWith;
    String description;


    public JavaScriptInterface(Context context,String userIdChattingWith)
    {
        this.context = context;
        this.userIdChattingWith = userIdChattingWith;
       // getDescriptionFromOtherUser();
    }


    @JavascriptInterface
    public void signalOtherUser(String sdp, String type) {
        Toast.makeText(context, type+"sent!", Toast.LENGTH_SHORT).show();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Signal")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        HashMap<String,Object> signalDescp = new HashMap<>();

        signalDescp.put("sdp",sdp);
        signalDescp.put("type",type);


        databaseReference.updateChildren(signalDescp);
    }


    @JavascriptInterface
    public void closeConnection() {
        // Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Signal")
                .child(userIdChattingWith)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.removeValue();
    }



    @JavascriptInterface
    public String getRemoteDescription() {
        return description;
    }


    @JavascriptInterface
    public void showToast(String msg) {
        Toast.makeText(context,  msg, Toast.LENGTH_SHORT).show();
    }



}
