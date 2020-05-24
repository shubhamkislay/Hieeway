package com.hieeway.hieeway;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LiveMessagingActivity extends AppCompatActivity {

    public String userIdChattingWith;
    public String usernameChattingWith;
    public String photo;
    public EditText messageBox;
    public TextView username, senderTextView, receiverTextView;
    public LiveMessagingViewModel liveMessagingViewModel;
    public Boolean truncateString = false;
    public Handler handler;
    public Runnable runnable;
    public Button backBtn;
    public Boolean typing=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_messaging);


        messageBox = findViewById(R.id.message_box);

        username = findViewById(R.id.username);

        backBtn = findViewById(R.id.back_button);


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeCallbacks(runnable);
                finish();
            }
        });

        receiverTextView = findViewById(R.id.receiver_message);
        senderTextView = findViewById(R.id.sender_message);
        handler = new Handler();

        runnable = new Runnable() {
            @Override
            public void run() {

                if(!typing) {

                    liveMessagingViewModel.updateLiveMessage("");
                    senderTextView.setText("");
                    messageBox.setText("");
                }
                else
                {
                    handler.removeCallbacks(runnable);
                }

            }
        };


        Intent intent = getIntent();

        usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        photo = intent.getStringExtra("photo");


        username.setText(usernameChattingWith);



        LiveMessagingViewModelFactory liveMessagingViewModelFactory = new LiveMessagingViewModelFactory(userIdChattingWith);
        liveMessagingViewModel = ViewModelProviders.of(this, liveMessagingViewModelFactory).get(LiveMessagingViewModel.class);
        liveMessagingViewModel.getLiveMessage().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable final String s) {


                if(s.length()<1) {
                    receiverTextView.animate().alpha(0.0f).setDuration(500);


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            receiverTextView.setText(s);

                        }
                    }, 500);

                }

                else
                {

                    receiverTextView.setText(s);
                    receiverTextView.animate().alpha(1.0f).setDuration(500);




                }



            }
        });



        messageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                handler.removeCallbacks(runnable);
                typing = true;

                senderTextView.setAlpha(1.0f);
                senderTextView.setText(s);

                liveMessagingViewModel.updateLiveMessage(s.toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        typing = false;
                        handler.postDelayed(runnable,1000);

                    }
                },1000);







            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });




    }
}
