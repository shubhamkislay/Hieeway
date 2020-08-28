package com.hieeway.hieeway.Fragments;


import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hieeway.hieeway.Interface.CloseLiveMessagingLoading;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivityPerf;


/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageParentFragment extends Fragment implements CloseLiveMessagingLoading {

    public SendMessageFragment sendMessageFragment;
    public VerticalPageActivityPerf activity;
    String usernameChattingWith;
    String photo;
    Bundle bundleSendMessage;
    private FrameLayout frameLayout;
    private String userIdChattingWith, currentUserPrivateKey, currentUserPublicKeyID;
    private String messageID = "default";
    private FragmentManager menuFragmentManager;
    private RelativeLayout checking_layout;
    private TextView checking_txt;

    public SendMessageParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_message_parent, container, false);
        frameLayout = view.findViewById(R.id.framelayout);


        checking_layout = view.findViewById(R.id.checking_layout);
        checking_txt = view.findViewById(R.id.checking_txt);

        checking_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        return view;
    }

    public void setParentActivity(VerticalPageActivityPerf activity) {
        this.activity = activity;
        menuFragmentManager = activity.getSupportFragmentManager();

    }

    public void setUserId(String userIdChattingWith) {
        this.userIdChattingWith = userIdChattingWith;
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
            //bundleSendMessage.putString("userIdChattingWith", userIdChattingWith);
            bundleSendMessage.putString("photo", photo);

            sendMessageFragment = new SendMessageFragment();
            sendMessageFragment.setMessageHighlightListener(activity);
            sendMessageFragment.setCloseLiveMessagingLoadingListener(this);
            sendMessageFragment.setArguments(bundleSendMessage);

            menuFragmentManager.beginTransaction()
                    .replace(R.id.framelayout, sendMessageFragment).commit();

        } else {
            removeListeners();
        }

    }

    @Override
    public void turnOffLoadingScreen() {
        try {
            checking_layout.setVisibility(View.GONE);
        } catch (Exception e) {

        }
    }
}
