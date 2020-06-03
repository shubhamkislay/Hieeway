package com.hieeway.hieeway.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.palette.graphics.Palette;

import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hieeway.hieeway.FeelingDialog;
import com.hieeway.hieeway.Interface.AddFeelingFragmentListener;
import com.hieeway.hieeway.Interface.EditBioFragmentListener;
import com.hieeway.hieeway.Interface.EditProfileOptionListener;
import com.hieeway.hieeway.Interface.FeelingListener;
import com.hieeway.hieeway.Interface.ImageSelectionCropListener;
import com.hieeway.hieeway.MainActivity;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.ProfilePhotoActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SharedViewModel;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

public class ProfileFragment extends Fragment implements FeelingListener, EditProfileOptionListener {

    private static final int RC_HINT = 3;
    private SharedViewModel sharedViewModel;

    ImageView profile_pic_background, center_dp;
    Button logoutBtn, uploadActivityButton;
    final static String HAPPY = "happy";
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

    Point size;
    float displayHeight;

    static Bitmap bitmap;
    //adding theme based on profile photo colors
    static private Palette.Swatch vibrantSwatch;
    static private Palette.Swatch mutedSwatch;
    static private Palette.Swatch lightVibrantSwatch;
    static private Palette.Swatch darkVibrantSwatch;
    static private Palette.Swatch dominantSwatch;
    static private Palette.Swatch lightMutedSwatch;
    static private Palette.Swatch darkMutedSwatch;
    static private Palette.Swatch currentSwatch = null;
    RelativeLayout top_fade, bottom_fade, overlay_fade;
    Window window;
    String userPhoto = "default";
    PhoneAuthProvider.ForceResendingToken forceResendingToken;



/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }*/

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static boolean isValid(String s) {
        // The given argument to compile() method
        // is regular expression. With the help of
        // regular expression we can validate mobile
        // number.
        // 1) Begins with 0 or 91
        // 2) Then contains 7 or 8 or 9.
        // 3) Then contains 9 digits
        Pattern p = Pattern.compile("(0/91)?[7-9][0-9]{9}");


        // Pattern class contains matcher() method
        // to find matching between given number
        // and regular expression
        Matcher m = p.matcher(s);
        return (m.find() && m.group().equals(s));
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Display display = getActivity().getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);








// clear FLAG_TRANSLUCENT_STATUS flag:
        try {
            window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
        } catch (Exception e) {
            //
        }

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
        top_fade = view.findViewById(R.id.top_fade);
        bottom_fade = view.findViewById(R.id.bottom_fade);
        overlay_fade = view.findViewById(R.id.overlay_fade);

        feeling_txt = view.findViewById(R.id.feeling_txt);
        change_nio_edittext = view.findViewById(R.id.change_nio_edittext);
        bio_txt = view.findViewById(R.id.bio_txt);
        edit_profile_option_btn = view.findViewById(R.id.edit_profile_option_btn);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);

        getActivity().getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);

        upload_progress = view.findViewById(R.id.upload_progress);
        relay = view.findViewById(R.id.relay);



        emoji_holder_layout = view.findViewById(R.id.emoji_holder_layout);

        ring_blinker_layout = view.findViewById(R.id.ring_blinker_layout);

        emoji_icon = view.findViewById(R.id.emoji_icon);

        center_dp.getLayoutParams().height = (int) displayHeight / 4;
        center_dp.getLayoutParams().width = (int) displayHeight / 8;

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

                imageSelectionCropListener.imageSelect();
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("token", "default");
                hashMap.put("online", false);

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                databaseReference.updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        FirebaseAuth.getInstance().signOut();

                        try {
                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Logout done! Close app and restart", Toast.LENGTH_SHORT).show();
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
        } catch (Exception e) {
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

        emoji_holder_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feelingDialog = new FeelingDialog(getContext(), ProfileFragment.this, feelingNow, addFeelingFragmentListener);
                feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                feelingDialog.show();
            }
        });

        emoji_holder_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    emoji_holder_layout.animate().scaleX(0.75f).scaleY(0.75f).setDuration(300);
                    emoji_holder_layout.setAlpha(0.75f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    emoji_holder_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    emoji_holder_layout.setAlpha(1.0f);
                } else {
                    emoji_holder_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    emoji_holder_layout.setAlpha(1.0f);
                }


                return false;
            }
        });


        emoji_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    emoji_holder_layout.animate().scaleX(0.75f).scaleY(0.75f).setDuration(300);
                    emoji_holder_layout.setAlpha(0.75f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    emoji_holder_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    emoji_holder_layout.setAlpha(1.0f);
                } else {
                    emoji_holder_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    emoji_holder_layout.setAlpha(1.0f);
                }


                return false;
            }
        });


        feeling_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    emoji_holder_layout.animate().scaleX(0.75f).scaleY(0.75f).setDuration(0);
                    emoji_holder_layout.setAlpha(0.75f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    emoji_holder_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(30);
                    emoji_holder_layout.setAlpha(1.0f);
                } else {
                    emoji_holder_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    emoji_holder_layout.setAlpha(1.0f);
                }


                return false;
            }
        });

        try {
            bitmap = Glide.with(getActivity())
                    .asBitmap()
                    .load(userPhoto)
                    .submit(5, 5)
                    .get();
            //setPaletteColor();
        } catch (Exception e) {
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


                } catch (Exception e) {
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
                        feeling_icon.setVisibility(View.GONE);
                        feeling_txt.setText(user.getFeeling());
                        emoji_icon.setVisibility(View.GONE);

                        feelingNow = user.getFeeling();
                        switch (user.getFeeling()) {
                            case HAPPY:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                                feeling_txt.setText(HAPPY);
                                break;
                            case SAD:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                                feeling_txt.setText(SAD);
                                break;
                            case BORED:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                                feeling_txt.setText(BORED);
                                break;
                            case ANGRY:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                                feeling_txt.setText(ANGRY);
                                break;
                            case "excited":
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                                feeling_txt.setText(EXCITED);
                                break;
                            case CONFUSED:
                                feeling_icon.setVisibility(View.VISIBLE);
                                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                                feeling_txt.setText(CONFUSED);
                                break;
                        }
                    }

                } catch (Exception e) {
                    //
                }

                try {
                    profilepic = user.getPhoto().replace("s96-c", "s384-c");
                    userPhoto = profilepic;
                    Glide.with(getContext()).load(user.getPhoto().replace("s96-c", "s384-c")).into(profile_pic_background);
                    Glide.with(getContext()).load(user.getPhoto().replace("s96-c", "s384-c")).into(center_dp);

                    bitmap = ((BitmapDrawable) profile_pic_background.getDrawable()).getBitmap();


                    // setPaletteColor();
                } catch (Exception e) {

                }

            }
        });

        //setPaletteColor();
        setColor();

    }

    public void setImageSelectionCropListener(ImageSelectionCropListener imageSelectionCropListener) {
        this.imageSelectionCropListener = imageSelectionCropListener;
    }

    public void setAddFeelingFragmentListener(AddFeelingFragmentListener addFeelingFragmentListener) {
        this.addFeelingFragmentListener = addFeelingFragmentListener;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        //setPaletteColor();
    }

    public void setEditBioFragmentListener(EditBioFragmentListener editBioFragmentListener) {
        this.editBioFragmentListener = editBioFragmentListener;
    }

    private void setColor() {
        top_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
        bottom_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
        overlay_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));

        try {
            window = getActivity().getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(getResources().getColor(R.color.profile_color_theme));
        } catch (Exception ef) {
            //
        }
    }

    private void setPaletteColor() {
        try {
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {

                    vibrantSwatch = palette.getVibrantSwatch();
                    darkVibrantSwatch = palette.getDarkVibrantSwatch();
                    lightVibrantSwatch = palette.getLightVibrantSwatch();
                    mutedSwatch = palette.getMutedSwatch();
                    darkMutedSwatch = palette.getDarkMutedSwatch();
                    lightMutedSwatch = palette.getLightMutedSwatch();
                    dominantSwatch = palette.getDominantSwatch();


                    if (vibrantSwatch != null)
                        currentSwatch = vibrantSwatch;
                    else if (darkVibrantSwatch != null)
                        currentSwatch = darkVibrantSwatch;
                    else if (lightVibrantSwatch != null)
                        currentSwatch = lightVibrantSwatch;
                    else if (mutedSwatch != null)
                        currentSwatch = mutedSwatch;
                    else if (darkMutedSwatch != null)
                        currentSwatch = darkMutedSwatch;
                    else if (lightMutedSwatch != null)
                        currentSwatch = lightMutedSwatch;
                    else if (dominantSwatch != null)
                        currentSwatch = dominantSwatch;


                    if (currentSwatch != null) {
                        //relativeLayout.setBackgroundColor(currentSwatch.getRgb());

                        if (darkMutedSwatch != null) {

                            currentSwatch = darkMutedSwatch;
                            try {
                                top_fade.setBackgroundTintList(ColorStateList.valueOf(currentSwatch.getRgb()));
                                bottom_fade.setBackgroundTintList(ColorStateList.valueOf(currentSwatch.getRgb()));
                                overlay_fade.setBackgroundTintList(ColorStateList.valueOf(currentSwatch.getRgb()));
                            } catch (Exception e) {
                                //
                            }



                    /*top_fade.setBackgroundColor(currentSwatch.getRgb());
                    bottom_fade.setBackgroundColor(currentSwatch.getRgb());
                    overlay_fade.setBackgroundColor(currentSwatch.getRgb());*/
                        /*username.setTextColor(currentSwatch.getTitleTextColor());
                        feeling_txt.setTextColor(currentSwatch.getTitleTextColor());
                        bio_txt.setTextColor(currentSwatch.getTitleTextColor());
                        name.setTextColor(currentSwatch.getTitleTextColor());*/

                            try {
                                window = getActivity().getWindow();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                                window.setStatusBarColor(currentSwatch.getRgb());
                            } catch (Exception e) {
                                //
                            }
                        }


                        // ...
                    } else {
                        //Toast.makeText(getActivity(), "Null color", Toast.LENGTH_SHORT).show();
                        top_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
                        bottom_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
                        overlay_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));

                        try {
                            window = getActivity().getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                            window.setStatusBarColor(getResources().getColor(R.color.profile_color_theme));
                        } catch (Exception e) {
                            //
                        }

                    }


                }
            });
        } catch (Exception e) {
            top_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
            bottom_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
            overlay_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));

            try {
                window = getActivity().getWindow();
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                window.setStatusBarColor(getResources().getColor(R.color.profile_color_theme));
            } catch (Exception ef) {
                //
            }
        }
    }

    @Override
    public void changeFeeling(String feeling) {
        DatabaseReference feelingReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        animateEmoji();
        HashMap<String, Object> feelingHash = new HashMap<>();
        feeling_icon.setVisibility(View.VISIBLE);
        emoji_icon.setVisibility(View.GONE);
        switch (feeling) {
            case HAPPY:
                feelingHash.put("feeling", HAPPY);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(HAPPY);
                break;
            case SAD:
                feelingHash.put("feeling", SAD);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(SAD);
                break;
            case BORED:
                feelingHash.put("feeling", BORED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(BORED);
                break;
            case ANGRY:
                feelingHash.put("feeling", ANGRY);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(ANGRY);
                break;
            case "excited":
                feelingHash.put("feeling", "excited");
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(EXCITED);
                break;
            case CONFUSED:
                feelingHash.put("feeling", CONFUSED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(getActivity().getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(CONFUSED);
                break;
        }
    }

    private void setAddedFeeling(String feelingIcon, String feelingText) {
        if (!feelingIcon.equals("default")) {

        }
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

    public void animateEmoji() {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        emoji_holder_layout.setAnimation(hyperspaceJump);
        //feeling_txt.setAnimation(hyperspaceJump);

        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator animator = ObjectAnimator.ofFloat(feeling_txt, "alpha", 0.0f, 1.0f);
        //ObjectAnimator animatorfeeling_icon = ObjectAnimator.ofFloat(feeling_txt,"alpha",0.0f,1.0f);

        animatorSet.setDuration(2000);
        animatorSet.playTogether(animator/*,animatorfeeling_icon*/);
        animatorSet.start();


    }

    public void setProgressVisibility(Boolean visibility) {
        if (visibility) {

            center_dp.setAlpha(0.5f);
            upload_progress.setVisibility(View.VISIBLE);
            profile_pic_background.setAlpha(0.0f);
        } else {
            center_dp.setAlpha(1.0f);
            upload_progress.setVisibility(View.GONE);
            profile_pic_background.setAlpha(1.0f);
        }

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

    private void stripUnderlines(TextView textView) {
        Spannable s = new SpannableString(textView.getText());
        URLSpan[] spans = s.getSpans(0, s.length(), URLSpan.class);
        for (URLSpan span : spans) {
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
