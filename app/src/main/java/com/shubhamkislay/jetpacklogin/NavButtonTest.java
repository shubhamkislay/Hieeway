package com.shubhamkislay.jetpacklogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shubhamkislay.jetpacklogin.Fragments.AddFeelingFragment;
import com.shubhamkislay.jetpacklogin.Fragments.ChatsFragment;
import com.shubhamkislay.jetpacklogin.Fragments.EditBioLayoutFragment;
import com.shubhamkislay.jetpacklogin.Fragments.FriendListFagment;
import com.shubhamkislay.jetpacklogin.Fragments.PeopleFragment;
import com.shubhamkislay.jetpacklogin.Fragments.ProfileFragment;
import com.shubhamkislay.jetpacklogin.Interface.AddFeelingFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.AnimationArrowListener;
import com.shubhamkislay.jetpacklogin.Interface.ChatStampSizeListener;
import com.shubhamkislay.jetpacklogin.Interface.EditBioFragmentListener;
import com.shubhamkislay.jetpacklogin.Interface.ImageSelectionCropListener;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

public class NavButtonTest extends AppCompatActivity implements ChatStampSizeListener, AnimationArrowListener, ImageSelectionCropListener, EditBioFragmentListener, AddFeelingFragmentListener {

    private TextView text_home;
    private ImageView homeBtnPressed, homeBtnUnpressed;

    private TextView text_friends;
    private ImageView friendsBtnPressed, friendsBtnUnpressed;

    private TextView text_search;
    private ImageView searchBtnPressed, searchBtnUnpressed;

    public static final int PERMISSION_PICK_IMAGE = 1000;
    private TextView text_profile;
    private CircleImageView profileBtnPressed, profileBtnUnpressed;

    private FrameLayout frameLayout;
    EditBioLayoutFragment editBioLayoutFragment;

    ChatsFragment chatsFragment;
    PeopleFragment peopleFragment;
    ProfileFragment profileFragment;
    FriendListFagment friendListFagment;
    AddFeelingFragment addFeelingFragment;
    RelativeLayout splash_layout;
    private Bundle bundle;

    ConstraintLayout nav_bar;
    Boolean playFrameAnimation = true;

    ImageView sendArrow, background_screen;
    float buttonSizeAlpha = 1.30f;
    float displayHeight;
    ObjectAnimator animatorY, alphaArrow, animateTextLogo, alphaBackgroundScreen, alphaMasterHead, alphaNavBar, alphaFrameLayout;
    AnimatorSet animatorSet;
    TypeWriter typeWriter;
    TextView master_head;
    private ImageView splash_logo, splash_logo_gradient, send_arrow;
    private int arrowAnimDuration = 600;

    private static final int IMAGE_REQUEST=1;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private Button uploadButton;
    private StorageTask uploadTask;


    RelativeLayout home_button_layout, friends_button_layout, people_button_layout, profile_button_layout;

    int fragmentId=1;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_button_test);

        splash_layout = findViewById(R.id.splash_layout);
        background_screen = findViewById(R.id.background_screen);

        profileBtnPressed = findViewById(R.id.profile_button_pressed);
        profileBtnUnpressed = findViewById(R.id.profile_button_unpressed);

        nav_bar = findViewById(R.id.nav_bar);

        getUserImageIntoNavButton();


        storageReference = FirebaseStorage.getInstance().getReference("uploads");



        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/

   // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        startSplash();
        //initiateNavActivity();
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash_layout.setVisibility(View.GONE);
                initiateNavActivity();
            }
        },2000);*/


    }

    @SuppressLint("ClickableViewAccessibility")
    private void initiateNavActivity() {

        //setContentView(R.layout.activity_nav_button_test);

        frameLayout = findViewById(R.id.container_layout);

        sendArrow = findViewById(R.id.send_arrow);


       // text_home = findViewById(R.id.text_home);
        homeBtnPressed = findViewById(R.id.home_button_pressed);
        homeBtnUnpressed = findViewById(R.id.home_button_unpressed);

        text_friends = findViewById(R.id.text_friends);
        friendsBtnPressed = findViewById(R.id.friends_button_pressed);
        friendsBtnUnpressed = findViewById(R.id.friends_button_unpressed);


        text_search = findViewById(R.id.text_search);
        searchBtnPressed = findViewById(R.id.search_button_pressed);
        searchBtnUnpressed = findViewById(R.id.search_button_unpressed);

        text_profile = findViewById(R.id.text_profile);

        bundle = new Bundle();

/*        profileBtnPressed = findViewById(R.id.profile_button_pressed);
        profileBtnUnpressed = findViewById(R.id.profile_button_unpressed);*/

        home_button_layout = findViewById(R.id.home_button_layout);
        friends_button_layout = findViewById(R.id.relativeLayout);
        people_button_layout = findViewById(R.id.relativeLayout2);
        profile_button_layout = findViewById(R.id.relativeLayout3);


        try {
           // text_home.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
            text_friends.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
            text_search.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
            text_profile.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        }catch (Exception e)
        {
            //
        }


        chatsFragment = new ChatsFragment();
        chatsFragment.setAnimationArrowListener(NavButtonTest.this);


        peopleFragment = new PeopleFragment();
        profileFragment = new ProfileFragment();
        friendListFagment = new FriendListFagment();
        editBioLayoutFragment = new EditBioLayoutFragment();

        addFeelingFragment = new AddFeelingFragment();

        ArrayList<Fragment> fragmentList = new ArrayList<>();


        fragmentList.add(chatsFragment);
        fragmentList.add(friendListFagment);
        fragmentList.add(peopleFragment);
        fragmentList.add(profileFragment);


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container_layout, chatsFragment).commit();

        homeBtnPressed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    home_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    home_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    home_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });

        home_button_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    home_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    home_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    home_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });




        home_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateHomeBtn();
                deactivateFriendsBNtn();
                deactivateSearchBtn();
                deactivateProfileBtn();
                animateBottomNavMenuText(text_home,homeBtnPressed);

            }
        });




        homeBtnPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateHomeBtn();
                deactivateFriendsBNtn();
                deactivateSearchBtn();
                deactivateProfileBtn();
                animateBottomNavMenuText(text_home,homeBtnPressed);
            }
        });

        friends_button_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    friends_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    friends_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    friends_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });
        friendsBtnUnpressed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    friends_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    friends_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    friends_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });




        friends_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateFriendsBtn();
                deactivateHomeBtn();
                deactivateSearchBtn();
                deactivateProfileBtn();
                animateBottomNavMenuText(text_friends,friendsBtnUnpressed);

            }
        });


        friendsBtnUnpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateFriendsBtn();
                deactivateHomeBtn();
                deactivateSearchBtn();
                deactivateProfileBtn();
                animateBottomNavMenuText(text_friends,friendsBtnUnpressed);
            }
        });


        searchBtnUnpressed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    people_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    people_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    people_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });

        people_button_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    people_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    people_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    people_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });


        people_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateSearchBtn();
                deactivateHomeBtn();
                deactivateFriendsBNtn();
                deactivateProfileBtn();
                animateBottomNavMenuText(text_search,searchBtnUnpressed);
            }
        });


        searchBtnUnpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateSearchBtn();
                deactivateHomeBtn();
                deactivateFriendsBNtn();
                deactivateProfileBtn();
                animateBottomNavMenuText(text_search,searchBtnUnpressed);
            }
        });

/*        searchBtnPressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateSearchBtn();
                deactivateHomeBtn();
                deactivateFriendsBNtn();
                deactivateProfileBtn();
            }
        });



*/

        profileBtnUnpressed.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    profile_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    profile_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    profile_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });
        profile_button_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    profile_button_layout.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    profile_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    profile_button_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });

        profile_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateProfileBtn();
                deactivateSearchBtn();
                deactivateHomeBtn();
                deactivateFriendsBNtn();
                animateBottomNavMenuText(text_profile,profileBtnPressed);

            }
        });


        profileBtnUnpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                activateProfileBtn();
                deactivateSearchBtn();
                deactivateHomeBtn();
                deactivateFriendsBNtn();
                animateBottomNavMenuText(text_profile,profileBtnPressed);
            }
        });

        profileBtnUnpressed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activateProfileBtn();
                deactivateSearchBtn();
                deactivateHomeBtn();
                deactivateFriendsBNtn();
                animateBottomNavMenuText(text_profile,profileBtnPressed);
            }
        });

       // getUserImageIntoNavButton();
    }


    public void getUserImageIntoNavButton()
    {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                if(!user.getPhoto().equals("default")) {
                    try {
                        Glide.with(NavButtonTest.this).load(user.getPhoto()).into(profileBtnPressed);
                        Glide.with(NavButtonTest.this).load(user.getPhoto()).into(profileBtnUnpressed);
                    } catch (Exception e) {

                    }
                }
                else
                {
                    profileBtnPressed.setImageResource(R.drawable.no_profile);
                    profileBtnUnpressed.setImageResource(R.drawable.no_profile);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public void activateFriendsBtn()
    {
        /*friendsBtnUnpressed.setVisibility(View.GONE);
        friendsBtnPressed.setVisibility(View.VISIBLE);
        text_friends.setVisibility(View.VISIBLE);*/

        friendsBtnUnpressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        friendsBtnUnpressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        //friendsBtnPressed.setColorFilter(null);


        friendsBtnUnpressed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));
       // friendsBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        friendsBtnUnpressed.setAlpha(1.0f);

        friends_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground )));

       // friendsBtnUnpressed.setBackground(getDrawable(R.drawable.ic_friends_active));

        text_friends.setVisibility(View.VISIBLE);

        if(fragmentId>2) {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.container_layout, friendListFagment).commit();
        }

        else {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.container_layout, friendListFagment).commit();
        }

        fragmentId=2;
    }

    public void deactivateFriendsBNtn()
    {
        /*friendsBtnUnpressed.setVisibility(View.VISIBLE);
        friendsBtnPressed.setVisibility(View.GONE);
        text_friends.setVisibility(View.GONE);*/

        friendsBtnUnpressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_inactive_size);
        friendsBtnUnpressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_inactive_size);
        //friendsBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        friendsBtnUnpressed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));


        friendsBtnUnpressed.setAlpha(0.85f);

        friends_button_layout.setBackgroundTintList(null);

      //  friendsBtnUnpressed.setBackground(getDrawable(R.drawable.ic_friends_active));
        text_friends.setVisibility(View.GONE);
    }

    public void activateHomeBtn()
    {
        /*homeBtnUnpressed.setVisibility(View.GONE);
        homeBtnPressed.setVisibility(View.VISIBLE);
        text_home.setVisibility(View.VISIBLE);*/


        homeBtnPressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        homeBtnPressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        homeBtnPressed.setColorFilter(null);

        homeBtnPressed.setAlpha(1.0f);
        homeBtnPressed.setBackgroundTintList(null);
       // homeBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        //home_button_layout.setBackground(getDrawable(R.drawable.active_nav_background));



        home_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground )));

        text_home.setVisibility(View.VISIBLE);




        getSupportFragmentManager().beginTransaction()
                   .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                .replace(R.id.container_layout, chatsFragment).commit();

        fragmentId=1;

    }

    public void deactivateHomeBtn()
    {
        /*homeBtnUnpressed.setVisibility(View.VISIBLE);
        homeBtnPressed.setVisibility(View.GONE);
        text_home.setVisibility(View.GONE);*/

        homeBtnPressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_inactive_size);
        homeBtnPressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_inactive_size);
        //homeBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        homeBtnPressed.setAlpha(0.85f);

        homeBtnPressed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));

        home_button_layout.setBackgroundTintList(null);

        //home_button_layout.setBackground(getDrawable(R.drawable.message_box_drawable));

        text_home.setVisibility(View.GONE);
    }


    public void activateSearchBtn()
    {
        /*searchBtnUnpressed.setVisibility(View.GONE);
        searchBtnPressed.setVisibility(View.VISIBLE);
        text_search.setVisibility(View.VISIBLE);*/


        searchBtnUnpressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        searchBtnUnpressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        searchBtnUnpressed.setColorFilter(null);

        searchBtnUnpressed.setAlpha(1.0f);

        //searchBtnPressed.setBackgroundTintList(null);
        searchBtnUnpressed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryDark)));

       // people_button_layout.setBackground(getDrawable(R.drawable.active_nav_background));

        people_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground )));

        // homeBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        text_search.setVisibility(View.VISIBLE);

/*        getSupportFragmentManager().beginTransaction()
                 .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                .replace(R.id.container_layout, peopleFragment).commit();*/



        if(fragmentId>3) {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                    .replace(R.id.container_layout, peopleFragment).commit();
        }

        else {

            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                    .replace(R.id.container_layout, peopleFragment).commit();
        }
        fragmentId=3;
    }

    public void deactivateSearchBtn()
    {
/*        searchBtnUnpressed.setVisibility(View.VISIBLE);
        searchBtnPressed.setVisibility(View.GONE);
        text_search.setVisibility(View.GONE);*/

        searchBtnUnpressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_inactive_size);
        searchBtnUnpressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_inactive_size);
        //homeBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        searchBtnUnpressed.setAlpha(0.85f);

        people_button_layout.setBackgroundTintList(null);

        //people_button_layout.setBackground(getDrawable(R.drawable.message_box_drawable));

        searchBtnUnpressed.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        text_search.setVisibility(View.GONE);

    }

    public void activateProfileBtn()
    {
        profileBtnUnpressed.setVisibility(View.GONE);
        profileBtnPressed.setVisibility(View.VISIBLE);

        profileBtnPressed.setAlpha(1.0f);

        //profile_button_layout.setBackground(getDrawable(R.drawable.active_nav_background));

        text_profile.setVisibility(View.VISIBLE);

        profile_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground )));

/*        profileBtnPressed.getLayoutParams().width = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        profileBtnPressed.getLayoutParams().height = (int) getResources().getDimension(R.dimen.nav_button_active_size);
        profileBtnPressed.setColorFilter(null);

        profileBtnPressed.setBackgroundTintList(null);
        // homeBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        text_profile.setVisibility(View.VISIBLE);*/



        profileFragment.setImageSelectionCropListener(NavButtonTest.this);
        profileFragment.setEditBioFragmentListener(NavButtonTest.this);
        profileFragment.setAddFeelingFragmentListener(NavButtonTest.this);

        getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                .replace(R.id.container_layout, profileFragment).commit();
        fragmentId=4;
    }

    @Override
    public void imageSelect() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, PERMISSION_PICK_IMAGE);
    }

    private void startCrop(Uri myUri) {



        Uri uri = myUri;


        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();


        UCrop uCrop = UCrop.of(uri,Uri.fromFile( new File(getCacheDir(),destinationFileName)));

        UCrop.Options up = new UCrop.Options();
        up.setLogoColor(getResources().getColor(R.color.colorBlack));
        up.setStatusBarColor(getResources().getColor(R.color.colorBlack));
        up.setToolbarColor(getResources().getColor(R.color.colorBlack));
        up.setLogoColor(getResources().getColor(R.color.colorPrimaryDark));
        up.setActiveWidgetColor(getResources().getColor(R.color.colorPrimaryDark));
        up.setCompressionQuality(50);
        /*up.setRootViewBackgroundColor(getResources().getColor(R.color.colorBlack));
        up.setToolbarWidgetColor(getResources().getColor(R.color.colorBlack));
        up.setDimmedLayerColor(getResources().getColor(R.color.colorPrimaryDark));*/

        uCrop.withAspectRatio(9,18);
        uCrop.withOptions(up);

        /*UCropActivity uCropActivity = new UCropActivity();

        uCropActivity.setTheme(R.style.AppTheme);*/






        /**
         * Use the code below for a square image selection i.e. for profile pictures
         * uCrop.withAspectRatio(1.0f, 1.0f);**/

        uCrop.start(NavButtonTest.this);

    }

    private void handleCropError(Intent data) {

        final Throwable cropError = UCrop.getError(data);

        if(cropError!=null)
        {
            Toast.makeText(this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }


    }

    private void handleCropResult(Intent data) {

        final Uri resultUri = UCrop.getOutput(data);
        if(resultUri != null)
        {
            //registerUsernameEntryFragment.setImageUri(resultUri);
            //Toast.makeText(this, "Image cropped",Toast.LENGTH_SHORT).show();

            if(uploadTask!=null&&uploadTask.isInProgress())
            {
                Toast.makeText(NavButtonTest.this,"Uploading..." , Toast.LENGTH_SHORT).show();
            }
            else
            {

                uploadImage(resultUri);
            }

        }
        else
        {
            Toast.makeText(this, "Cannot retrieve crop image",Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadImage(Uri imageUri)
    {

        /*final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this );

        progressDialog.setMessage("Uploading photo");
        progressDialog.show();*/
        profileFragment.setProgressVisibility(true);

        //registerUsernameEntryFragment.setProgressVisibility(true);

        if(imageUri!=null)
        {

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task task) throws Exception {

                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }





                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {


                    if(task.isSuccessful())
                    {

                        Uri downloadUri = task.getResult();

                        String mUri = downloadUri.toString();


                        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("photo",mUri );
                        databaseReference.updateChildren(map);
                        //progressDialog.dismiss();


                       Glide.with(NavButtonTest.this).load(mUri).into(profileBtnPressed);
                       Glide.with(NavButtonTest.this).load(mUri).into(profileBtnUnpressed);



                        // progressDialog.dismiss();
                        profileFragment.setProgressVisibility(false);

                        /*registerUsernameEntryFragment.setProgressVisibility(false);
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(PHOTO_URL, mUri);
                        editor.apply();

                        registerUsernameEntryFragment.setUploadedImage(mUri);*/

                    }
                    else
                    {
                        Toast.makeText(NavButtonTest.this,"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                       // registerUsernameEntryFragment.setProgressVisibility(false);
                        profileFragment.setProgressVisibility(false);
                    }

                }
            });


        }
        else
        {
            Toast.makeText(NavButtonTest.this,"No Image selected",Toast.LENGTH_SHORT ).show();
            profileFragment.setProgressVisibility(false);
        }


    }

    private String getExtension(Uri uri)
    {


        ContentResolver contentResolver = NavButtonTest.this.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {


            if (requestCode == PERMISSION_PICK_IMAGE) {
                //  Bitmap bitmap = BitmapUtils.getBitmapFromGallery(getContext(), data.getData(), 800, 800);


                // image_selected_uri = data.getData();

                startCrop(data.getData());


                /*//clear bitmap memory

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();


                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);

                //   img_preview.setImageBitmap(originalBitmap);
                photoEditorView.getSource().setImageBitmap(originalBitmap);*/

                //bitmap.recycle();

                //Render selected img thumbnail

                /*filtersListFragment.displayThumbnail(originalBitmap);*/

                /*filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);*/
            } else if (requestCode == UCrop.REQUEST_CROP) {
                //  Toast.makeText(this, "Image cropped",Toast.LENGTH_SHORT).show();
                handleCropResult(data);

            } else if (requestCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }


        }
    }

    public void deactivateProfileBtn()
    {
        profileBtnUnpressed.setVisibility(View.VISIBLE);
        profileBtnPressed.setVisibility(View.GONE);
        profileBtnUnpressed.setAlpha(0.85f);
       // profile_button_layout.setBackground(getDrawable(R.drawable.message_box_drawable));
       // profileBtnUnpressed

        profile_button_layout.setBackgroundTintList(null);

        text_profile.setVisibility(View.GONE);
    }


    public void animateBottomNavMenuText(TextView navText, ImageView imageView)
    {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.text_bounce_anim);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        //navText.setAnimation(hyperspaceJump);



        Animation hyperspaceJumpImg = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.text_bounce_anim);

        hyperspaceJumpImg.setRepeatMode(Animation.INFINITE);

        imageView.setAnimation(hyperspaceJump);
    }

    /*public void animateArrow() {
        // sendArrow.setAlpha(1.0f);

        try {
            if (animatorSet.isRunning()) {
                animatorSet.cancel();
            }
        }catch (Exception e)
        {

        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        animatorY = ObjectAnimator.ofFloat(sendArrow,"translationY",-displayHeight - (displayHeight) / 4);
        animatorY.setDuration(500);
        // animatorY.start();

        alphaArrow = ObjectAnimator.ofFloat(sendArrow,"alpha",1.0f,0.0f);
        alphaArrow.setDuration(500);

        animatorSet = new AnimatorSet();
        animatorSet.playTogether(animatorY,alphaArrow);

        animatorSet.start();

        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                sendArrow.setAlpha(1.0f);

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                sendArrow.setTranslationY(0f);
                sendArrow.setAlpha(0.0f);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

                sendArrow.setTranslationY(0f);
                sendArrow.setAlpha(0.0f);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }*/

    private void startSplash() {

        splash_logo = findViewById(R.id.splash_logo);

        splash_logo_gradient = findViewById(R.id.splash_logo_gradient);

        send_arrow = findViewById(R.id.send_arrow_splash);

        typeWriter = findViewById(R.id.logo_txt);

        master_head = findViewById(R.id.master_head);

        text_home = findViewById(R.id.text_home);

        text_home.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));






        // overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);


        final Animation hyperspaceJump = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        splash_logo.setAnimation(hyperspaceJump);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        typeWriter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        master_head.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));

        typeWriter.setCharacterDelay(75);
        typeWriter.animateText("Hieeway");


       // animateArrow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initiateNavActivity();
            }
        },1250);





    }

    public void animateArrow() {


        animatorY = ObjectAnimator.ofFloat(send_arrow,"translationY",-displayHeight -(displayHeight)/3);
        animateTextLogo = ObjectAnimator.ofFloat(typeWriter,"translationY",-displayHeight -(displayHeight)/3);


        alphaArrow = ObjectAnimator.ofFloat(send_arrow,"alpha",1.0f,0.0f);

        alphaBackgroundScreen = ObjectAnimator.ofFloat(background_screen,"alpha",1.0f,0.0f);

        alphaNavBar = ObjectAnimator.ofFloat(nav_bar,"alpha",0.0f,1.0f);

        alphaFrameLayout = ObjectAnimator.ofFloat(frameLayout,"alpha",0.0f,1.0f);

        //  alphaMasterHead = ObjectAnimator.ofFloat(master_head,"alpha",1.0f,0.0f);

        animatorSet = new AnimatorSet();

        animatorSet.setDuration(arrowAnimDuration);

        AnimatorSet visibleAnimate =  new AnimatorSet();

        visibleAnimate.setDuration(arrowAnimDuration*3);



/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/

                send_arrow.setAlpha(1.0f);

                final Animation accel = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.accelerate_image);
                final Animation accelFaster = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.accelerate_image_faster);


                accel.setRepeatMode(Animation.INFINITE);
                accelFaster.setRepeatMode(Animation.INFINITE);


                // splash_logo_gradient.setAnimation(accel);
                // typeWriter.setAnimation(accelFaster);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        typeWriter.animate().translationYBy(-displayHeight/2).setDuration(arrowAnimDuration/2);






                    }
                },arrowAnimDuration/2);


                typeWriter.animate().alpha(0.0f).setDuration(arrowAnimDuration);
                // splash_logo_gradient.animate().alpha(0.0f).setDuration(750);

                master_head.setVisibility(View.GONE);

                animatorSet.playTogether(animatorY,alphaArrow/*, animateTextLogo*/,alphaBackgroundScreen/*,alphaMasterHead*//*,alphaNavBar, alphaFrameLayout*/);
                animatorSet.start();

                if(playFrameAnimation) {

                visibleAnimate.playTogether(alphaNavBar, alphaFrameLayout);
                visibleAnimate.start();

                    playFrameAnimation = false;

                }

/*            }
        },1250);*/





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Intent intent = new Intent(NavButtonTest.this,MainActivity.class);
                startActivity(intent);
                finish();*/
                splash_layout.setVisibility(View.GONE);
                //initiateNavActivity();
            }
        },arrowAnimDuration);
    }


    @Override
    protected void onPause()
    {
        playFrameAnimation = false;

        try {

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("online");

            databaseReference.setValue(false);
        }catch (Exception e)
        {

        }
        super.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("online");

            databaseReference.setValue(true);
        }catch (Exception e)
        {

        }


    }

    @Override
    public void setChatStampSizeActivity(int chatstampSize) {
        chatsFragment.setChatStampSizeFragment(chatstampSize);
    }

    @Override
    public void playArrowAnimation() {

        animateArrow();
    }


    @Override
    public void setEditBioChange(Boolean returnFromEditBio,String currentBio) {
        if(returnFromEditBio)
        {

            profileFragment.setImageSelectionCropListener(NavButtonTest.this);
            profileFragment.setEditBioFragmentListener(NavButtonTest.this);
            profileFragment.setAddFeelingFragmentListener(NavButtonTest.this);


            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                    .replace(R.id.container_layout, profileFragment).commit();
        }
        else
        {
            editBioLayoutFragment = new EditBioLayoutFragment();
            editBioLayoutFragment.setCurrentBio(NavButtonTest.this,currentBio);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                    .replace(R.id.container_layout, editBioLayoutFragment).commit();
        }

    }

    @Override
    public void setFeelingChange(Boolean returnFromAddFeeling, String feelingIcon, String feelingText) {

        if (returnFromAddFeeling) {

            profileFragment.setImageSelectionCropListener(NavButtonTest.this);
            profileFragment.setEditBioFragmentListener(NavButtonTest.this);
            profileFragment.setAddFeelingFragmentListener(NavButtonTest.this);


            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                    .replace(R.id.container_layout, profileFragment).commit();
        } else {
            addFeelingFragment = new AddFeelingFragment();

            addFeelingFragment.setAddFeelingFragmentListener(NavButtonTest.this);
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                    .replace(R.id.container_layout, addFeelingFragment).commit();
        }

    }
}
