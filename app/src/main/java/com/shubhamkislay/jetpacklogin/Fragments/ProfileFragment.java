package com.shubhamkislay.jetpacklogin.Fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubhamkislay.jetpacklogin.Adapters.ChatMessageAdapter;
import com.shubhamkislay.jetpacklogin.Adapters.PeopleAdapter;
import com.shubhamkislay.jetpacklogin.DeleteOptionsDialog;
import com.shubhamkislay.jetpacklogin.FeelingDialog;
import com.shubhamkislay.jetpacklogin.ImageUpload;
import com.shubhamkislay.jetpacklogin.Interface.FeelingListener;
import com.shubhamkislay.jetpacklogin.MainActivity;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.SharedViewModel;

import java.util.HashMap;

public class ProfileFragment extends Fragment implements FeelingListener {

    private SharedViewModel sharedViewModel;
    final static String HAPPY = "happy";
    ImageView profile_pic_background, center_dp;
    Button logoutBtn, uploadActivityButton;
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    TextView username, name, feeling_icon, feeling_txt;
    FeelingDialog feelingDialog;

/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);


        username = view.findViewById(R.id.username);
        profile_pic_background = view.findViewById(R.id.profile_pic_background);
        center_dp = view.findViewById(R.id.center_dp);
        name = view.findViewById(R.id.name);
        logoutBtn = view.findViewById(R.id.logout_btn);
        uploadActivityButton = view.findViewById(R.id.change_activity);
        feeling_icon = view.findViewById(R.id.feeling_icon);

        feeling_txt = view.findViewById(R.id.feeling_txt);

        uploadActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ImageUpload.class));
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token", "default");
                hashMap.put("online",false);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        FirebaseAuth.getInstance().signOut();

                        try{
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        }catch (Exception e)
                        {
                            Toast.makeText(getContext(),"Logout done! Close app and restart",Toast.LENGTH_SHORT).show();
                        }

                    }
                });


            }
        });

        feeling_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingDialog = new FeelingDialog(getContext(),ProfileFragment.this);
                feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                feelingDialog.show();
            }
        });


        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {


                username.setText(user.getUsername());
                name.setText(user.getName());
              //  feeling_txt.setText(user.getFeeling());
                switch (user.getFeeling())
                {
                    case HAPPY:
                        feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_happy));
                        feeling_txt.setText(HAPPY);
                        break;
                    case SAD:
                        feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_sad));
                        feeling_txt.setText(SAD);
                        break;
                    case BORED:
                        feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_bored));
                        feeling_txt.setText(BORED);
                        break;
                    case ANGRY:
                        feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_angry));
                        feeling_txt.setText(ANGRY);
                        break;
                    case EXCITED:
                        feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_excited));
                        feeling_txt.setText(EXCITED);
                        break;
                    case CONFUSED:
                        feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_confused));
                        feeling_txt.setText(CONFUSED);
                        break;
                }

                try{
                    Glide.with(getContext()).load(user.getPhoto().replace("s96-c", "s384-c")).into(profile_pic_background);
                    Glide.with(getContext()).load(user.getPhoto().replace("s96-c", "s384-c")).into(center_dp);
                }
                catch (Exception e)
                {

                }

            }
        });


    }

    @Override
    public void changeFeeling(String feeling) {
        DatabaseReference feelingReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        HashMap<String,Object> feelingHash =  new HashMap<>();
        switch (feeling)
        {
            case HAPPY: feelingHash.put("feeling",HAPPY);
                 feelingReference.updateChildren(feelingHash);
                 feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_happy));
                 feeling_txt.setText(HAPPY);
                 break;
            case SAD: feelingHash.put("feeling",SAD);
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_sad));
                feeling_txt.setText(SAD);
                break;
            case BORED: feelingHash.put("feeling",BORED);
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_bored));
                feeling_txt.setText(BORED);
                break;
            case ANGRY: feelingHash.put("feeling",ANGRY);
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_angry));
                feeling_txt.setText(ANGRY);
                break;
            case EXCITED: feelingHash.put("feeling",EXCITED);
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_excited));
                feeling_txt.setText(EXCITED);
                break;
            case CONFUSED: feelingHash.put("feeling",CONFUSED);
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_confused));
                feeling_txt.setText(CONFUSED);
                break;
        }
    }
}
