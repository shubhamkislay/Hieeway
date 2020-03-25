package com.shubhamkislay.jetpacklogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUsernameEntryFragment extends Fragment {

    EditText username;
    Button enter_btn;
    ImageView profile_pic_background;
    TextView emailTextView, nameTextView;
    String email, name, photourl;

    public RegisterUsernameEntryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register_username_entry, container, false);

        profile_pic_background = view.findViewById(R.id.profile_pic_background);

        emailTextView = view.findViewById(R.id.email);


        enter_btn = view.findViewById(R.id.enter_btn);

        nameTextView = view.findViewById(R.id.name);

        username = view.findViewById(R.id.username);


        try {

            emailTextView.setText(email);
            nameTextView.setText(name);
            Glide.with(getActivity()).load(photourl.replace("s96-c", "s384-c")).into(profile_pic_background);
        }
        catch (Exception e)
        {
            //
        }


        return view;
    }

    public void setUserData(String email, String name, String photourl)
    {
        this.email = email;
        this.name = name;
        this.photourl = photourl;

    }

}
