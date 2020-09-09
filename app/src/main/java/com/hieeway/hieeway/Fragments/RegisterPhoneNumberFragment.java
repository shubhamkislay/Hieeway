package com.hieeway.hieeway.Fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.hieeway.hieeway.Interface.PhoneNumberListener;
import com.hieeway.hieeway.MainActivity;
import com.hieeway.hieeway.PhoneAuthenticationActivity;
import com.hieeway.hieeway.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterPhoneNumberFragment extends Fragment {

    private final static int FETCH_NUMBER = 3;
    TextView phone_title, notes, feature_title, feautures_list1, feautures_list2;
    Button add_number_btn, skip_btn;
    PhoneNumberListener phoneNumberListener;


    public RegisterPhoneNumberFragment(PhoneNumberListener phoneNumberListener) {
        // Required empty public constructor

        this.phoneNumberListener = phoneNumberListener;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_phone_number, container, false);


        phone_title = view.findViewById(R.id.phone_title);
        notes = view.findViewById(R.id.notes);
        feature_title = view.findViewById(R.id.feature_title);
        feautures_list1 = view.findViewById(R.id.feautures_list1);
        feautures_list2 = view.findViewById(R.id.feautures_list2);

        add_number_btn = view.findViewById(R.id.add_number_btn);
        skip_btn = view.findViewById(R.id.skip_btn);


        phone_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        // notes.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-medium.otf"));
        feature_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-medium.otf"));
        feautures_list1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feautures_list2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        add_number_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getActivity(), PhoneAuthenticationActivity.class), FETCH_NUMBER);
            }
        });

        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();*/

                phoneNumberListener.changeToSpotifyConnectFragment();
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FETCH_NUMBER) {
            if (resultCode == RESULT_OK) {
                /*startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();*/
                phoneNumberListener.changeToSpotifyConnectFragment();
            } else {
                phone_title.setText("Phone number not added");
            }
        }
    }
}
