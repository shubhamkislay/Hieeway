package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.FrameLayout;

import com.hieeway.hieeway.Fragments.ChatsFragment;
import com.hieeway.hieeway.Interface.AnimationArrowListener;
import com.hieeway.hieeway.Interface.ChatStampSizeListener;

public class ChatParentActivity extends AppCompatActivity implements ChatStampSizeListener, AnimationArrowListener {

    private ChatsFragment chatsFragment;
    private FrameLayout parent_layout;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_parent);


        parent_layout = findViewById(R.id.parent_layout);

        fragmentManager = getSupportFragmentManager();

//        chatsFragment = new ChatsFragment();
        chatsFragment.setAnimationArrowListener(ChatParentActivity.this);


        fragmentManager.beginTransaction()
                .replace(R.id.parent_layout, chatsFragment).commit();


    }

    @Override
    public void setChatStampSizeActivity(int chatstampSize) {
        chatsFragment.setChatStampSizeFragment(chatstampSize);
    }

    @Override
    public void playArrowAnimation() {
        //
    }
}