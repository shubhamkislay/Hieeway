package com.shubhamkislay.jetpacklogin.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.shubhamkislay.jetpacklogin.Interface.EditBioFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.FeelingListener;
import com.shubhamkislay.jetpacklogin.Interface.ImageSelectionCropListener;
import com.shubhamkislay.jetpacklogin.MainActivity;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.shubhamkislay.jetpacklogin.NavButtonTest;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.SharedViewModel;

import java.util.HashMap;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

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
    TextView username, name, feeling_icon, feeling_txt, bio_txt;
    FeelingDialog feelingDialog;
    ImageSelectionCropListener imageSelectionCropListener;
    String feelingNow = null;
    EditText change_nio_edittext;
    ProgressBar upload_progress;
    RelativeLayout relativeLayout;
    String bio = "";
    Boolean continue_blinking = false;
    Boolean isBlinking = false;
    EditBioFragmentListener editBioFragmentListener;

/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

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
        change_nio_edittext = view.findViewById(R.id.change_nio_edittext);
        bio_txt = view.findViewById(R.id.bio_txt);

        getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);

        upload_progress = view.findViewById(R.id.upload_progress);

        relativeLayout = view.findViewById(R.id.edit_text_back);

        name.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feeling_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        username.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-medium.otf"));
        //change_nio_edittext.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bio_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));

        uploadActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivity(new Intent(getActivity(), ImageUpload.class));
                imageSelectionCropListener.imageSelect();
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
                feelingDialog = new FeelingDialog(getContext(),ProfileFragment.this,feelingNow);
                feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                feelingDialog.show();
            }
        });


        bio_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {/*
                change_nio_edittext.setText(bio_txt.getText().toString());
                bio_txt.setVisibility(View.GONE);
                change_nio_edittext.setVisibility(View.VISIBLE);
                change_nio_edittext.setEnabled(true);*/

                editBioFragmentListener.setEditBioChange(false,bio_txt.getText().toString());
            }
        });
        try {
            if (bio.length() < 1) {
                bio_txt.setText("Tell something about yourself");
                bio_txt.setTextColor(getActivity().getResources().getColor(R.color.darkGrey));
            } else {
                bio_txt.setText(bio);
                stripUnderlines(bio_txt);
                bio_txt.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));
            }
        }catch (Exception e){
            //
        }

/*
        change_nio_edittext.setImeOptions(EditorInfo.IME_ACTION_DONE);

        change_nio_edittext.setRawInputType(InputType.TYPE_CLASS_TEXT);

        change_nio_edittext.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    //do here your stuff f
                   // change_nio_edittext.setText(change_nio_edittext.getText().toString());

                    hideKeyboardFrom(getContext(),view);
                    change_nio_edittext.clearFocus();

                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put("bio",change_nio_edittext.getText().toString());

                   // if(change_nio_edittext.getText().toString().length()>0)
                        databaseReference.updateChildren(hashMap);
                    bio = change_nio_edittext.getText().toString();

                    Toast.makeText(getContext(),"Bio updated!",Toast.LENGTH_SHORT).show();

                    return true;
                }
                return false;
            }
        });
*/


/*
        change_nio_edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
*/
/*                if(!s.toString().equals(bio))
                {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                else
                    relativeLayout.setVisibility(View.GONE);*//*


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!s.toString().equals(bio))
                {
                    relativeLayout.setVisibility(View.VISIBLE);
                    continue_blinking = true;
                    if(!isBlinking)
                        blinkEditTextBackground();
                    isBlinking = true;
                }
                else {
                    relativeLayout.setVisibility(View.GONE);
                    continue_blinking = false;
                    isBlinking = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
*/
/*                if(!s.toString().equals(bio))
                {
                    relativeLayout.setVisibility(View.VISIBLE);
                }
                else
                    relativeLayout.setVisibility(View.GONE);*//*


            }
        });
*/





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



                try {
                    bio = user.getBio();
                   // change_nio_edittext.setText(user.getBio());
                    bio_txt.setText(user.getBio());
                    stripUnderlines(bio_txt);
                    bio_txt.setTextColor(getActivity().getResources().getColor(R.color.colorWhite));


                }catch (Exception e)
                {
                    //
                    bio_txt.setText("Tell something about yourself");
                    bio_txt.setTextColor(getActivity().getResources().getColor(R.color.darkGrey));
                }

              //  feeling_txt.setText(user.getFeeling());

                try {
                    feelingNow = user.getFeeling();
                    switch (user.getFeeling()) {
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
                }catch (Exception e)
                {
                    //
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

    public void setImageSelectionCropListener(ImageSelectionCropListener imageSelectionCropListener)
    {
        this.imageSelectionCropListener = imageSelectionCropListener;
    }

    public  void setEditBioFragmentListener(EditBioFragmentListener editBioFragmentListener)
    {
        this.editBioFragmentListener = editBioFragmentListener;
    }

    @Override
    public void changeFeeling(String feeling) {
        DatabaseReference feelingReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        animateEmoji();
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

    private void blinkEditTextBackground() {



        relativeLayout.animate().alpha(1.0f).setDuration(250);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                relativeLayout.animate().alpha(0.0f).setDuration(250);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(continue_blinking)
                            blinkEditTextBackground();

                    }
                }, 300);
            }

        },300);




    }

    public void animateEmoji()
    {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        feeling_icon.setAnimation(hyperspaceJump);
        //feeling_txt.setAnimation(hyperspaceJump);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(feeling_txt,"alpha",0.0f,1.0f);
        //ObjectAnimator animatorfeeling_icon = ObjectAnimator.ofFloat(feeling_txt,"alpha",0.0f,1.0f);

        animatorSet.setDuration(2000);
        animatorSet.playTogether(animator/*,animatorfeeling_icon*/);
        animatorSet.start();






    }

    public void setProgressVisibility(Boolean visibility)
    {
        if(visibility)
        {

            center_dp.setAlpha(0.5f);
            upload_progress.setVisibility(View.VISIBLE);
            profile_pic_background.setAlpha(0.0f);
        }
        else
        {
            center_dp.setAlpha(1.0f);
            upload_progress.setVisibility(View.GONE);
            profile_pic_background.setAlpha(1.0f);
        }

    }

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span: spans) {
            int start = s.getSpanStart(span);
            int end = s.getSpanEnd(span);
            s.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            s.setSpan(span, start, end, 0);
        }
        textView.setText(s);
    }

    private class URLSpanNoUnderline extends URLSpan {
        public URLSpanNoUnderline(String url) {
            super(url);
        }
        @Override public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setUnderlineText(false);
        }
    }
}
