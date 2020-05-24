package com.hieeway.hieeway;

import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.gauravk.bubblenavigation.BubbleNavigationConstraintView;
import com.gauravk.bubblenavigation.BubbleToggleView;
import com.gauravk.bubblenavigation.listener.BubbleNavigationChangeListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jgabrielfreitas.core.BlurImageView;
import com.hieeway.hieeway.Fragments.ChatsFragment;
import com.hieeway.hieeway.Fragments.FriendListFagment;
import com.hieeway.hieeway.Fragments.PeopleFragment;
import com.hieeway.hieeway.Fragments.ProfileFragment;
import com.hieeway.hieeway.Interface.ChatStampSizeListener;

import java.util.ArrayList;

public class AlphaActivity extends AppCompatActivity implements ChatStampSizeListener {
   // private TextView mTextMessage;
    FrameLayout frameLayout;
   // RelativeLayout searchRelativeLayout;
    ChatsFragment chatsFragment;
    //TextView appTitleTextView;
    PeopleFragment peopleFragment;
    ProfileFragment profileFragment;
    FriendListFagment friendListFagment;
    BubbleToggleView bubbleToggleHomeView, bubbleToggleProfileView, bubbleToggleSearchView, bubbleToggleFriendsView;
    BlurImageView blurImageView;
    int fragmentId=1;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:


                   // mTextMessage.setText ("Chats");

                    if(fragmentId!=1) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.container_layout, chatsFragment).commit();
                    }

                    fragmentId=1;
                    return true;
                case R.id.navigation_dashboard:


                  //  mTextMessage.setText("People");

                    if(fragmentId==1) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                                .replace(R.id.container_layout, peopleFragment).commit();
                    }

                    if(fragmentId==3) {
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.container_layout, peopleFragment).commit();
                    }
                    fragmentId=2;
                    return true;
                case R.id.navigation_notifications:


                   // mTextMessage.setText("Profile");


                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                                .replace(R.id.container_layout, profileFragment).commit();
                    fragmentId=3;

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_alpha_bubble_nav);

        setContentView(R.layout.bubble_bottom_nav);

        ArrayList<Fragment> fragmentList = new ArrayList<>();


        fragmentList.add(chatsFragment);
        fragmentList.add(friendListFagment);
        fragmentList.add(peopleFragment);
        fragmentList.add(profileFragment);

       // appTitleTextView = findViewById(R.id.app_title);





        fragmentId=1;
        frameLayout = findViewById(R.id.container_layout);
        chatsFragment = new ChatsFragment();
        peopleFragment = new PeopleFragment();
        profileFragment = new ProfileFragment();
        friendListFagment = new FriendListFagment();
        blurImageView = findViewById(R.id.blurr_profile_pic);
        //searchRelativeLayout = findViewById(R.id.app_title_layout);


        bubbleToggleHomeView = findViewById(R.id.l_item_home);
        bubbleToggleFriendsView = findViewById(R.id.l_item_friend);
        bubbleToggleSearchView = findViewById(R.id.l_item_search);
        bubbleToggleProfileView = findViewById(R.id.l_item_profile);



        final BubbleNavigationConstraintView bubbleNavigationLinearView = findViewById(R.id.bottom_navigation_view_linear);


        bubbleNavigationLinearView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/ProximaNova-Semibold.ttf"));

        //appTitleTextView.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));





        bubbleNavigationLinearView.setNavigationChangeListener(new BubbleNavigationChangeListener() {
            @Override
            public void onNavigationChanged(View view, int position) {

                switch (position) {
                    case 0:

                       // mTextMessage.setText ("Chatbox");
/*
                        if(fragmentId!=1) {
                            getSupportFragmentManager().beginTransaction()
                                 //   .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.container_layout, chatsFragment).commit();
                        }*/
                        getSupportFragmentManager().beginTransaction()
                                //   .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.container_layout, chatsFragment).commit();

                        fragmentId=1;
                        bubbleNavigationLinearView.setCurrentActiveItem(R.id.l_item_home);
                        //searchRelativeLayout.setVisibility(View.VISIBLE);
                        break;

                    case 1:


                       // mTextMessage.setText("Friends");

/*                        if(fragmentId==1) {
                            getSupportFragmentManager().beginTransaction()
                                    // .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                                    .replace(R.id.container_layout, friendListFagment).commit();
                        }

                        if(fragmentId==3) {
                            getSupportFragmentManager().beginTransaction()
                                    //  .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.container_layout, friendListFagment).commit();
                        }
                        fragmentId=2;*/
                        fragmentId=2;
                        getSupportFragmentManager().beginTransaction()
                                //  .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.container_layout, friendListFagment).commit();
                        bubbleNavigationLinearView.setCurrentActiveItem(R.id.l_item_friend);
                       // searchRelativeLayout.setVisibility(View.GONE);

                        break;

                    case 2:


                      //  mTextMessage.setText("Search");

/*                        if(fragmentId==1) {
                            getSupportFragmentManager().beginTransaction()
                                   // .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                                    .replace(R.id.container_layout, peopleFragment).commit();
                        }

                        if(fragmentId==3) {
                            getSupportFragmentManager().beginTransaction()
                                  //  .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                    .replace(R.id.container_layout, peopleFragment).commit();
                        }*/

                        getSupportFragmentManager().beginTransaction()
                                //  .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                                .replace(R.id.container_layout, peopleFragment).commit();
                        fragmentId=3;
                        bubbleNavigationLinearView.setCurrentActiveItem(R.id.l_item_search);
                      //  searchRelativeLayout.setVisibility(View.GONE);

                        break;

                    case 3:


                     //   mTextMessage.setText("Profile");


                        getSupportFragmentManager().beginTransaction()
                               // .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                                .replace(R.id.container_layout, profileFragment).commit();
                        fragmentId=4;
                        bubbleNavigationLinearView.setCurrentActiveItem(R.id.l_item_profile);
                      //  searchRelativeLayout.setVisibility(View.GONE);

                        break;


                }



            }
        });




/*        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists())
                {

                    User user = dataSnapshot.getValue(User.class);
                    Glide.with(AlphaActivity.this).load(user.getPhoto()).into(blurImageView);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/






        blurImageView.setBlur(0);

        getSupportFragmentManager().beginTransaction()
        .replace(R.id.container_layout, chatsFragment).commit();


        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);


        /*Bundle bundle = new Bundle();
        String myMessage = "Stackoverflow is cool!";
        bundle.putString("message", myMessage );
        FragmentClass fragInfo = new FragmentClass();
        fragInfo.setArguments(bundle);*/
        //setting the font style of message text


        Typeface custom_font = Typeface.createFromAsset(getAssets(),  "fonts/samsungsharpsans-bold.otf");

        SpannableString content = new SpannableString("Chats");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);


        BottomNavigationView navView = findViewById(R.id.nav_view);
       // mTextMessage = findViewById(R.id.message);mTextMessage.setText("Chats");


      //  navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }



    @Override
    protected void onPause() {

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
    protected void onResume() {
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
}
