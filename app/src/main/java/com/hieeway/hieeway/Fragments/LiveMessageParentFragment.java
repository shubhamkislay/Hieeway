package com.hieeway.hieeway.Fragments;


import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hieeway.hieeway.Interface.LiveMessageEventListener;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivityPerf;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveMessageParentFragment extends Fragment {

    LiveMessageEventListener liveMessageEventListener;
    Activity parentActivity;
    String userIdChattingWith;
    String youtubeID;
    String youtubeTitle;
    String live;
    private FrameLayout framelayout;
    private LiveMessageFragmentPerf liveMessageFragmentPerf;
    private String usernameChattingWith;
    private String photo;
    private Bundle bundle;
    private String userChattingWithId;
    private VerticalPageActivityPerf activity;
    private FragmentManager menuFragmentManager;
    private Boolean userVisited = false;


    public LiveMessageParentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_live_message_parent, container, false);

        framelayout = view.findViewById(R.id.framelayout);


        usernameChattingWith = getArguments().getString("usernameChattingWith");
        userChattingWithId = getArguments().getString("userIdChattingWith");
        // userIdChattingWith = getArguments().getString("userIdChattingWith");
        photo = getArguments().getString("photo");

        /*bundle = new Bundle();
        bundle.putString("usernameChattingWith", usernameChattingWith);
        bundle.putString("photo",photo);
        bundle.putString("userIdChattingWith",userChattingWithId);*/


        return view;
    }

    public void setParentActivity(VerticalPageActivityPerf activity) {
        this.activity = activity;
        menuFragmentManager = activity.getSupportFragmentManager();

    }

    public void setLiveMessageEventListener(LiveMessageEventListener liveMessageEventListener, Activity parentActivity,
                                            String photo, String usernameChattingWith, String userIdChattingWith,
                                            String youtubeID, String youtubeTitle, String live) throws NullPointerException {
        this.liveMessageEventListener = liveMessageEventListener;
        this.parentActivity = parentActivity;
        this.photo = photo;
        this.usernameChattingWith = usernameChattingWith;
        this.userIdChattingWith = userIdChattingWith;
        this.youtubeID = youtubeID;
        this.youtubeTitle = youtubeTitle;
        this.live = live;
    }

    public void closeBottomSheet() {

        liveMessageFragmentPerf.closeBottomSheet();

    }

    public void showLiveMessageDialog(Activity activity, String live) {
        this.live = live;
        try {
            liveMessageFragmentPerf.showLiveMessageDialog(activity, live);
        } catch (Exception e) {

        }
    }

    public void destoryLiveFragment() {
        try {
            liveMessageFragmentPerf.destoryLiveFragment();
        } catch (Exception e) {

        }
    }

    public void setBottomSheetBehavior(MotionEvent event) {
        try {
            liveMessageFragmentPerf.setBottomSheetBehavior(event);
        } catch (Exception e) {

        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            userVisited = true;
            bundle = new Bundle();
            bundle.putString("usernameChattingWith", usernameChattingWith);
            bundle.putString("photo", photo);
            bundle.putString("userIdChattingWith", userIdChattingWith);


            liveMessageFragmentPerf = new LiveMessageFragmentPerf();
            liveMessageFragmentPerf.setArguments(bundle);
            liveMessageFragmentPerf.setYoutubeBottomFragmentStateListener(activity);

            liveMessageFragmentPerf
                    .setLiveMessageEventListener(
                            liveMessageEventListener,
                            parentActivity,
                            photo,
                            usernameChattingWith,
                            userIdChattingWith,
                            youtubeID,
                            youtubeTitle);
            menuFragmentManager.beginTransaction()
                    .replace(R.id.framelayout, liveMessageFragmentPerf).commit();

            liveMessageFragmentPerf.showLiveMessageDialog(parentActivity, live);


        } else {
            try {
                liveMessageFragmentPerf.destoryLiveFragment();
            } catch (Exception e) {

            }
            liveMessageFragmentPerf = null;
            if (userVisited)
                live = "no";
        }
    }
}
