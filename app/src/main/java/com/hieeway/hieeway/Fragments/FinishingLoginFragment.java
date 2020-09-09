package com.hieeway.hieeway.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hieeway.hieeway.MainActivity;
import com.hieeway.hieeway.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FinishingLoginFragment extends Fragment {


    TextView loading_txt;
    Activity activity;

    public FinishingLoginFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_finishing_login, container, false);

        loading_txt = view.findViewById(R.id.loading_txt);


        loading_txt.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(activity, MainActivity.class));
                activity.finish();
            }
        }, 2000);


        return view;
    }

}
