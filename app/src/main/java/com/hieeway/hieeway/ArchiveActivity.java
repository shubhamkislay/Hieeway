package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hieeway.hieeway.Fragments.PinnedMessagesFragment;
import com.hieeway.hieeway.Fragments.SendMessageFragment;
import com.hieeway.hieeway.Model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ArchiveActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private List<ChatMessage> messageList;
    private ArchiveActivityViewModel archiveActivityViewModel;
    public  String userIdChattingWith;
    private ArchiveActivityViewModelFactory archiveActivityViewModelFactory;
    FrameLayout frameLayout;
    Button back_button;
    SendMessageFragment sendMessageFragment;
    PinnedMessagesFragment pinnedMessagesFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sent_messages:
                  //  mTextMessage.setText(R.string.title_home);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, sendMessageFragment).commit();
                    return true;
                case R.id.pinned_messages:
                 //   mTextMessage.setText(R.string.title_notifications);
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, pinnedMessagesFragment).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);



        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);






        Intent intent = getIntent();
        userIdChattingWith = intent.getStringExtra("userIdChattingWith");


        back_button = findViewById(R.id.back_button);


        frameLayout = findViewById(R.id.container_layout);


        Bundle bundle = new Bundle();
        bundle.putString("userIdChattingWith", userIdChattingWith);

        sendMessageFragment = new SendMessageFragment();

        sendMessageFragment.setArguments(bundle);

        pinnedMessagesFragment = new PinnedMessagesFragment();
        pinnedMessagesFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_layout, sendMessageFragment).commit();




        messageList = new ArrayList<>();


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });





        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);





        archiveActivityViewModelFactory = new ArchiveActivityViewModelFactory(userIdChattingWith);
        archiveActivityViewModel = ViewModelProviders.of(this, archiveActivityViewModelFactory).get(ArchiveActivityViewModel.class);
        archiveActivityViewModel.getChatList().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {

                messageList = chatMessageList;

                filterList(messageList);

            }
        });








    }

    private void filterList(List<ChatMessage> messageList) {

        //

    }

}
