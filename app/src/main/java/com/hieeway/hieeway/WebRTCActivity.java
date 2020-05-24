package com.hieeway.hieeway;

import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Interface.JavaScriptInterface;
import com.hieeway.hieeway.Model.Signal;

public class WebRTCActivity extends AppCompatActivity {

    WebView webView;
    VideoView videoView;
    String userIdChattingWith;
    String description;
    Button receive_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_rtc);

        webView = findViewById(R.id.webview);

        WebSettings settings = webView.getSettings();

        Intent intent = getIntent();
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");

        receive_btn = findViewById(R.id.receive_btn);




        settings.setJavaScriptEnabled(true);

        /*        camera = Camera.open();


        showCamera = new ShowCamera(MainActivity.this,camera);

        frameLayout.addView(showCamera);*/


        webView.loadUrl("file:///android_asset/index.html");

        webView.addJavascriptInterface(new JavaScriptInterface(this,userIdChattingWith), "Android");

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    request.grant(request.getResources());
                }
            }
        });

        webView.setWebViewClient(new WebViewClient());


        settings.setUseWideViewPort(true);

        settings.setLoadWithOverviewMode(true);

        settings.setBuiltInZoomControls(false);

        // Allow use of Local Storage
        settings.setDomStorageEnabled(true);

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.setDisplayZoomControls(false);
        }
        settings.setMediaPlaybackRequiresUserGesture(false);

       // webView.loadUrl("javascript:recieveSignalOtherUser('"+userIdChattingWith+"')");
        getDescriptionFromOtherUser();



    }

    public void getDescriptionFromOtherUser()
    {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Signal")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    final Signal signal = dataSnapshot.getValue(Signal.class);

                    description = signal.getSdp();

                    receive_btn.setVisibility(View.VISIBLE);

                    receive_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //webView.loadUrl("javascript:readMessage('"+description+"','"+signal.getType()+"')");

                            webView.loadUrl("javascript:readMessage('"+signal.getSdp()+"','"+signal.getType()+"')");


                           // Toast.makeText(WebRTCActivity.this,"type: "+signal.getType(),Toast.LENGTH_SHORT).show();
                            receive_btn.setVisibility(View.GONE);
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
