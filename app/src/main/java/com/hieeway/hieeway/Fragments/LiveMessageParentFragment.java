package com.hieeway.hieeway.Fragments;


import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hieeway.hieeway.Interface.CloseLiveMessagingLoading;
import com.hieeway.hieeway.Interface.LiveMessageEventListener;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.VerticalPageActivityPerf;


/**
 * A simple {@link Fragment} subclass.
 */
public class LiveMessageParentFragment extends Fragment implements CloseLiveMessagingLoading {

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
    private RelativeLayout checking_layout;
    private TextView checking_txt;
    private String loadVideo;


    public LiveMessageParentFragment(LiveMessageEventListener liveMessageEventListener, Activity parentActivity,
                                     String photo, String usernameChattingWith, String userIdChattingWith,
                                     String youtubeID, String youtubeTitle, String live, String loadVideo) {
        // Required empty public constructor

        this.liveMessageEventListener = liveMessageEventListener;
        this.parentActivity = parentActivity;
        this.photo = photo;
        this.usernameChattingWith = usernameChattingWith;
        this.userIdChattingWith = userIdChattingWith;
        this.youtubeID = youtubeID;
        this.youtubeTitle = youtubeTitle;
        this.live = live;
        this.loadVideo = loadVideo;
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


        /*checking_layout = view.findViewById(R.id.checking_layout);
        checking_txt = view.findViewById(R.id.checking_txt);

        checking_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));*/

        bundle = new Bundle();
        bundle.putString("usernameChattingWith", usernameChattingWith);
        bundle.putString("photo",photo);
        bundle.putString("userIdChattingWith", userChattingWithId);


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
            //  liveMessageFragmentPerf.showLiveMessageDialog((VerticalPageActivityPerf)activity, live, this);
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

    /**
     * Uncomment below code to show dialog after loading this fragment
     */

/*
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            userVisited = true;
            bundle = new Bundle();
            bundle.putString("usernameChattingWith", usernameChattingWith);
            bundle.putString("photo", photo);
            bundle.putString("userIdChattingWith", userIdChattingWith);


            liveMessageFragmentPerf = new LiveMessageFragmentPerf(liveMessageEventListener,
                    parentActivity,
                    photo,
                    usernameChattingWith,
                    userIdChattingWith,
                    youtubeID,
                    youtubeTitle,
                    loadVideo);
            liveMessageFragmentPerf.setArguments(bundle);
            liveMessageFragmentPerf.setYoutubeBottomFragmentStateListener(activity);

            */
/*liveMessageFragmentPerf
                    .setLiveMessageEventListener(
                            liveMessageEventListener,
                            parentActivity,
                            photo,
                            usernameChattingWith,
                            userIdChattingWith,
                            youtubeID,
                            youtubeTitle);*//*



            menuFragmentManager.beginTransaction()
                    .replace(R.id.framelayout, liveMessageFragmentPerf).commit();
            liveMessageFragmentPerf.showLiveMessageDialog(parentActivity, live, this);

        }
        else {
            try {
                liveMessageFragmentPerf.destoryLiveFragment();
            } catch (Exception e) {

            }
            liveMessageFragmentPerf = null;
            if (userVisited)
                live = "no";
        }
    }
*/
    @Override
    public void turnOffLoadingScreen() {
        try {
            //checking_layout.setVisibility(View.GONE);
            //liveMessageFragmentPerf.turnOffRequestDialogVisibility();
        } catch (Exception e) {
            //
        }
    }

    public void startLiveMessaging() {
        try {
            //checking_layout.setVisibility(View.GONE);
            //liveMessageFragmentPerf.turnOffRequestDialogVisibility();
        } catch (Exception e) {
            //
        }
        bundle = new Bundle();
        bundle.putString("usernameChattingWith", usernameChattingWith);
        bundle.putString("photo", photo);
        bundle.putString("userIdChattingWith", userIdChattingWith);


        liveMessageFragmentPerf = new LiveMessageFragmentPerf(liveMessageEventListener,
                parentActivity,
                photo,
                usernameChattingWith,
                userIdChattingWith,
                youtubeID,
                youtubeTitle,
                loadVideo);
        liveMessageFragmentPerf.setArguments(bundle);
        liveMessageFragmentPerf.setYoutubeBottomFragmentStateListener(activity);

            /*liveMessageFragmentPerf
                    .setLiveMessageEventListener(
                            liveMessageEventListener,
                            parentActivity,
                            photo,
                            usernameChattingWith,
                            userIdChattingWith,
                            youtubeID,
                            youtubeTitle);*/


        menuFragmentManager.beginTransaction()
                .replace(R.id.framelayout, liveMessageFragmentPerf).commit();
        /*liveMessageFragmentPerf.showLiveMessageDialog((VerticalPageActivityPerf) getActivity(), live, this)*/
        ;
    }
}
