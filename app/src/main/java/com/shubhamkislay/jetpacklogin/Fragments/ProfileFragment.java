package com.shubhamkislay.jetpacklogin.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubhamkislay.jetpacklogin.EditProfileDialog;
import com.shubhamkislay.jetpacklogin.FeelingDialog;
import com.shubhamkislay.jetpacklogin.Interface.AddFeelingFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.EditBioFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.EditProfileOptionListener;
import com.shubhamkislay.jetpacklogin.Interface.FeelingListener;
import com.shubhamkislay.jetpacklogin.Interface.ImageSelectionCropListener;
import com.shubhamkislay.jetpacklogin.MainActivity;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.shubhamkislay.jetpacklogin.ProfilePhotoActivity;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.SharedViewModel;

import java.util.HashMap;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

public class ProfileFragment extends Fragment implements FeelingListener, EditProfileOptionListener {

    private SharedViewModel sharedViewModel;
    final static String HAPPY = "happy";
    ImageView profile_pic_background, center_dp;
    Button logoutBtn, uploadActivityButton;
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    TextView username, name, feeling_icon, feeling_txt, bio_txt, emoji_icon;
    FeelingDialog feelingDialog;
    ImageSelectionCropListener imageSelectionCropListener;
    AddFeelingFragmentListener addFeelingFragmentListener;
    String feelingNow = null;
    EditText change_nio_edittext;
    ProgressBar upload_progress;
    RelativeLayout relativeLayout;
    String bio = "";
    Boolean continue_blinking = false;
    Boolean isBlinking = false;
    RelativeLayout emoji_holder_layout;
    EditBioFragmentListener editBioFragmentListener;
    RelativeLayout ring_blinker_layout;
    Boolean blinking = false;
    Button edit_profile_option_btn;
    BottomSheetBehavior bottomSheetBehavior;
    RelativeLayout edit_profile_pic;
    RelativeLayout edit_bio;
    Boolean bottomSheetDialogVisible = false;
    TextView bottom_dialog_title, prof_txt, bio_txt_dialog;
    RelativeLayout bottom_sheet_dialog_layout;
    RelativeLayout relay;
    private String profilepic;


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

        edit_profile_pic = view.findViewById(R.id.edit_profile_pic);
        edit_bio = view.findViewById(R.id.edit_bio);

        bottom_dialog_title = view.findViewById(R.id.title);
        prof_txt = view.findViewById(R.id.profile_txt);
        bio_txt_dialog = view.findViewById(R.id.bio_txtview);

        username = view.findViewById(R.id.username);
        profile_pic_background = view.findViewById(R.id.profile_pic_background);
        center_dp = view.findViewById(R.id.center_dp);
        name = view.findViewById(R.id.name);
        logoutBtn = view.findViewById(R.id.logout_btn);
        uploadActivityButton = view.findViewById(R.id.change_activity);
        feeling_icon = view.findViewById(R.id.feeling_icon);
        bottom_sheet_dialog_layout = view.findViewById(R.id.bottom_sheet_dialog_layout);

        feeling_txt = view.findViewById(R.id.feeling_txt);
        change_nio_edittext = view.findViewById(R.id.change_nio_edittext);
        bio_txt = view.findViewById(R.id.bio_txt);
        edit_profile_option_btn = view.findViewById(R.id.edit_profile_option_btn);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);

        getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);

        upload_progress = view.findViewById(R.id.upload_progress);
        relay = view.findViewById(R.id.relay);

        relativeLayout = view.findViewById(R.id.edit_text_back);

        emoji_holder_layout = view.findViewById(R.id.emoji_holder_layout);

        ring_blinker_layout = view.findViewById(R.id.ring_blinker_layout);

        emoji_icon = view.findViewById(R.id.emoji_icon);

        name.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feeling_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        username.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-medium.otf"));
        //change_nio_edittext.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bio_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bio_txt_dialog.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        prof_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bottom_dialog_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));

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

        center_dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bottomSheetDialogVisible) {
                    Intent intent = new Intent(getActivity(), ProfilePhotoActivity.class);
                    intent.putExtra("profilepic", profilepic);

                    getActivity().startActivity(intent);
                }
            }
        });

        feeling_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bottomSheetDialogVisible) {

                    feelingDialog = new FeelingDialog(getContext(), ProfileFragment.this, feelingNow, addFeelingFragmentListener);
                    feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    feelingDialog.show();
                }
            }
        });

        emoji_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bottomSheetDialogVisible) {
                    feelingDialog = new FeelingDialog(getContext(), ProfileFragment.this, feelingNow, addFeelingFragmentListener);
                    feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    feelingDialog.show();
                }
            }
        });



        bio_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bottomSheetDialogVisible) {
                /*

                change_nio_edittext.setText(bio_txt.getText().toString());
                bio_txt.setVisibility(View.GONE);
                change_nio_edittext.setVisibility(View.VISIBLE);
                change_nio_edittext.setEnabled(true);*/
                    if (bio_txt.getText().toString().length() < 1)
                        editBioFragmentListener.setEditBioChange(false, bio_txt.getText().toString());
                }
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

        edit_profile_option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bottomSheetDialogVisible) {
                /*EditProfileDialog editProfileDialog = new EditProfileDialog(getContext(), ProfileFragment.this);
                editProfileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                editProfileDialog.show();*/
                    relay.setVisibility(View.VISIBLE);
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    bottomSheetDialogVisible = true;
                }
            }
        });

        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relay.setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                imageSelectionCropListener.imageSelect();
                bottomSheetDialogVisible = false;
            }
        });

        edit_bio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relay.setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                editBioFragmentListener.setEditBioChange(false, bio_txt.getText().toString());
                bottomSheetDialogVisible = false;
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });

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


        if (!blinking) {
            blinking = true;
            startBlinking();
        }
        return view;
    }

    private void startBlinking() {
        ring_blinker_layout.animate().alpha(0.0f).setDuration(950);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                ring_blinker_layout.animate().alpha(1.0f).setDuration(950);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ring_blinker_layout.animate().alpha(0.0f).setDuration(950);
                        startBlinking();
                    }
                }, 1000);
            }
        }, 1000);
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
                feelingNow = user.getFeeling();

                try {
                    if (!user.getFeelingIcon().equals("default")) {
                        feelingNow = user.getFeeling();
                        feeling_icon.setVisibility(View.GONE);
                        emoji_icon.setText(user.getFeelingIcon());
                        emoji_icon.setVisibility(View.VISIBLE);
                        feeling_txt.setText(user.getFeeling());

                    } else {
                            /*feeling_icon.setVisibility(View.GONE);
                            feeling_txt.setText(user.getFeeling());*/
                        emoji_icon.setVisibility(View.GONE);

                        feelingNow = user.getFeeling();
                        switch (user.getFeeling()) {
                            case HAPPY:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_happy));
                                feeling_txt.setText(HAPPY);
                                break;
                            case SAD:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_sad));
                                feeling_txt.setText(SAD);
                                break;
                            case BORED:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_bored));
                                feeling_txt.setText(BORED);
                                break;
                            case ANGRY:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_angry));
                                feeling_txt.setText(ANGRY);
                                break;
                            case EXCITED:
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_excited));
                                feeling_txt.setText(EXCITED);
                                break;
                            case CONFUSED:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_confused));
                                feeling_txt.setText(CONFUSED);
                                break;
                        }
                    }

                }catch (Exception e)
                {
                    //
                }

                try{
                    profilepic = user.getPhoto().replace("s96-c", "s384-c");
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

    public void setAddFeelingFragmentListener(AddFeelingFragmentListener addFeelingFragmentListener) {
        this.addFeelingFragmentListener = addFeelingFragmentListener;
    }

    @Override
    public void changeFeeling(String feeling) {
        DatabaseReference feelingReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        animateEmoji();
        HashMap<String,Object> feelingHash =  new HashMap<>();
        feeling_icon.setVisibility(View.VISIBLE);
        emoji_icon.setVisibility(View.GONE);
        switch (feeling)
        {
            case HAPPY: feelingHash.put("feeling",HAPPY);
                feelingHash.put("feelingIcon", "default");
                 feelingReference.updateChildren(feelingHash);
                 feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_happy));
                 feeling_txt.setText(HAPPY);
                 break;
            case SAD: feelingHash.put("feeling",SAD);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_sad));
                feeling_txt.setText(SAD);
                break;
            case BORED: feelingHash.put("feeling",BORED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_bored));
                feeling_txt.setText(BORED);
                break;
            case ANGRY: feelingHash.put("feeling",ANGRY);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_angry));
                feeling_txt.setText(ANGRY);
                break;
            case EXCITED: feelingHash.put("feeling",EXCITED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.emoticon_feeling_excited));
                feeling_txt.setText(EXCITED);
                break;
            case CONFUSED: feelingHash.put("feeling",CONFUSED);
                feelingHash.put("feelingIcon", "default");
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

    private void setAddedFeeling(String feelingIcon, String feelingText) {
        if (!feelingIcon.equals("default")) {

        }
    }

    public void animateEmoji()
    {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        emoji_holder_layout.setAnimation(hyperspaceJump);
        //feeling_txt.setAnimation(hyperspaceJump);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(feeling_txt,"alpha",0.0f,1.0f);
        //ObjectAnimator animatorfeeling_icon = ObjectAnimator.ofFloat(feeling_txt,"alpha",0.0f,1.0f);

        animatorSet.setDuration(2000);
        animatorSet.playTogether(animator/*,animatorfeeling_icon*/);
        animatorSet.start();


    }


    public void setBottomSheetBehavior(MotionEvent event) {
        try {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottom_sheet_dialog_layout.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    relay.setVisibility(View.GONE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            bottomSheetDialogVisible = false;
                        }
                    }, 500);
                }
            }
        } catch (Exception e) {
            //
        }
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

    @Override
    public void editProfileOption(String option) {
        switch (option) {
            case "bio":
                editBioFragmentListener.setEditBioChange(false, bio_txt.getText().toString());
                break;

            case "pic":
                imageSelectionCropListener.imageSelect();
                break;
        }
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
