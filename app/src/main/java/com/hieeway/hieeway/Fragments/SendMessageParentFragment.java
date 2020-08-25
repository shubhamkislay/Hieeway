package com.hieeway.hieeway.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivityPerf;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageParentFragment extends Fragment {

    public SendMessageFragment sendMessageFragment;
    public VerticalPageActivityPerf activity;
    String usernameChattingWith;
    String photo;
    Bundle bundleSendMessage;
    private FrameLayout frameLayout;
    private String userIdChattingWith, currentUserPrivateKey, currentUserPublicKeyID;
    private String messageID = "default";
    private FragmentManager menuFragmentManager;

    public SendMessageParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_message_parent, container, false);
        frameLayout = view.findViewById(R.id.framelayout);

        userIdChattingWith = getArguments().getString("userIdChattingWith");
        currentUserPrivateKey = getArguments().getString("currentUserPrivateKey");
        currentUserPublicKeyID = getArguments().getString("currentUserPublicKeyID");
        messageID = getArguments().getString("messageID");
        usernameChattingWith = getArguments().getString("usernameChattingWith");
        // userIdChattingWith = getArguments().getString("userIdChattingWith");
        photo = getArguments().getString("photo");


        bundleSendMessage = new Bundle();
        bundleSendMessage.putString("userIdChattingWith", userIdChattingWith);
        bundleSendMessage.putString("currentUserPrivateKey", currentUserPrivateKey);
        bundleSendMessage.putString("currentUserPublicKeyID", currentUserPublicKeyID);
        bundleSendMessage.putString("messageID", messageID);
        bundleSendMessage.putString("usernameChattingWith", usernameChattingWith);
        bundleSendMessage.putString("userIdChattingWith", userIdChattingWith);
        bundleSendMessage.putString("photo", photo);


        return view;
    }

    public void setParentActivity(VerticalPageActivityPerf activity) {
        this.activity = activity;
        menuFragmentManager = activity.getSupportFragmentManager();

    }


    public void removeListeners() {
        try {
            sendMessageFragment.removeListeners();
        } catch (Exception e) {

        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            sendMessageFragment = new SendMessageFragment();
            sendMessageFragment.setMessageHighlightListener(activity);
            sendMessageFragment.setArguments(bundleSendMessage);

            menuFragmentManager.beginTransaction()
                    .replace(R.id.framelayout, sendMessageFragment).commit();
        } else {
            removeListeners();
        }

    }
}
