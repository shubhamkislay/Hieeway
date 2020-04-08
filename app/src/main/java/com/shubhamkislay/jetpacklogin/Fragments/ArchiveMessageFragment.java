package com.shubhamkislay.jetpacklogin.Fragments;


import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.shubhamkislay.jetpacklogin.ArchiveActivityViewModel;
import com.shubhamkislay.jetpacklogin.ArchiveActivityViewModelFactory;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArchiveMessageFragment extends Fragment {

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
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, sendMessageFragment).commit();
                    return true;
                case R.id.pinned_messages:
                    //   mTextMessage.setText(R.string.title_notifications);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container_layout, pinnedMessagesFragment).commit();
                    return true;
            }
            return false;
        }
    };



    public ArchiveMessageFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_page1, container, false);

       sendMessageFragment = new SendMessageFragment();


        PinnedMessagesFragment pinnedMessagesFragment = new PinnedMessagesFragment();

        /*getParentFragment().getActivity().getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/


        //final String usernameChattingWith = getArguments().getString("usernameChattingWith");
        userIdChattingWith = getArguments().getString("userIdChattingWith");

        back_button = view.findViewById(R.id.back_button);


        frameLayout = view.findViewById(R.id.container_layout);

        //sendMessageFragment.setParentFragmentData(userIdChattingWith);
        pinnedMessagesFragment.setParentFragmentData(userIdChattingWith);

        final Bundle bundle = new Bundle();
        bundle.putString("userIdChattingWith",userIdChattingWith);

        sendMessageFragment.setArguments(bundle);

        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_layout, sendMessageFragment).commit();




        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(sendMessageFragment);
        fragmentList.add(pinnedMessagesFragment);

        messageList = new ArrayList<>();


        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });





        BottomNavigationView navView = view.findViewById(R.id.nav_view);
        mTextMessage = view.findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);





        archiveActivityViewModelFactory = new ArchiveActivityViewModelFactory(userIdChattingWith);
        archiveActivityViewModel = ViewModelProviders.of(this, archiveActivityViewModelFactory).get(ArchiveActivityViewModel.class);
        archiveActivityViewModel.getChatList().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {

                messageList.clear();

                messageList = chatMessageList;

                sendMessageFragment.notifyAdapter();

                //filterList(messageList);

            }
        });




        return view;
    }



}
