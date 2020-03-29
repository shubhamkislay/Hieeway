package com.shubhamkislay.jetpacklogin;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Fragments.ChatsFragment;
import com.shubhamkislay.jetpacklogin.Fragments.FriendListFagment;
import com.shubhamkislay.jetpacklogin.Fragments.PeopleFragment;
import com.shubhamkislay.jetpacklogin.Fragments.ProfileFragment;
import com.shubhamkislay.jetpacklogin.Interface.AnimationArrowListener;
import com.shubhamkislay.jetpacklogin.Interface.ChatStampSizeListener;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavButtonTest extends AppCompatActivity implements ChatStampSizeListener, AnimationArrowListener {

    private TextView text_home;
    private ImageView homeBtnPressed, homeBtnUnpressed;

    private TextView text_friends;
    private ImageView friendsBtnPressed, friendsBtnUnpressed;

    private TextView text_search;
    private ImageView searchBtnPressed, searchBtnUnpressed;

    private TextView text_profile;
    private CircleImageView profileBtnPressed, profileBtnUnpressed;

    private FrameLayout frameLayout;

    ChatsFragment chatsFragment;
    PeopleFragment peopleFragment;
    ProfileFragment profileFragment;
    FriendListFagment friendListFagment;
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



        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                .replace(R.id.container_layout, profileFragment).commit();
        fragmentId=4;
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
}
