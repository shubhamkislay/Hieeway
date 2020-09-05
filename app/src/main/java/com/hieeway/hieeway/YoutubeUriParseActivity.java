package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class YoutubeUriParseActivity extends AppCompatActivity {

    TextView youtube_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_uri_parse);

        youtube_url = findViewById(R.id.youtube_url);

        Bundle extras = getIntent().getExtras();
        String youtube_Url = extras.getString(Intent.EXTRA_TEXT);


        youtube_url.setText(youtube_Url);


    }
}
