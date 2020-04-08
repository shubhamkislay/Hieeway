package com.shubhamkislay.jetpacklogin;


import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Adapters.VPagerAdapter;
import com.shubhamkislay.jetpacklogin.Fragments.ArchiveMessageFragment;
import com.shubhamkislay.jetpacklogin.Fragments.EphemeralMessagingFragment;
import com.shubhamkislay.jetpacklogin.Fragments.LiveMessageFragment;
import com.shubhamkislay.jetpacklogin.Fragments.SendMessageFragment;
import com.shubhamkislay.jetpacklogin.Interface.LiveMessageEventListener;
import com.shubhamkislay.jetpacklogin.Interface.MessageHighlightListener;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.List;


public class VerticalPageActivity extends AppCompatActivity implements MessageHighlightListener , LiveMessageEventListener {


    private VerticalViewPager verticalViewPager;
    private fr.castorflex.android.verticalviewpager.VerticalViewPager viewPager;
    private FlingableViewPager flingableViewPager;
    public SendMessageFragment  sendMessageFragment;
    private PagerAdapter pagerAdapter;
    private String userIdChattingWith;
    public EphemeralMessagingFragment ephemeralMessagingFragment;
    public ArchiveMessageFragment archiveMessageFragment;
    public Boolean observingSendFragment = false;
    public ArchiveActivityViewModel archiveActivityViewModel;
    private ArchiveActivityViewModelFactory archiveActivityViewModelFactory;
    private List<ChatMessage> messageList, sendMessagelist;
    private Bundle bundle;
    private Bundle bundleSendMessage;
    private int pageSelected = 1;
    private int chatStampSize = 0;
    LiveMessageFragment liveMessageFragment;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    String publicKeyText, privateKeyText, otherUserPublicKey, otherUserPublicKeyID, publicKeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_page);



        Intent intent = getIntent();

        messageList = new ArrayList<>();
        sendMessagelist = new ArrayList<>();

        final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");



        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);
        publicKeyText = sharedPreferences.getString(PUBLIC_KEY,null);
        publicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID,null);



        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userIdChattingWith);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    User user = dataSnapshot.getValue(User.class);
                    try {
                        otherUserPublicKey = user.getPublicKey();
                        otherUserPublicKeyID = user.getPublicKeyId();
                    }
                    catch (Exception e)
                    {
                        //
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        bundle = new Bundle();
        bundle.putString("usernameChattingWith", usernameChattingWith);
        bundle.putString("userIdChattingWith",userIdChattingWith);
        bundle.putString("photo",photo);
        bundle.putString("currentUserPublicKey",publicKeyText);
        bundle.putString("currentUserPrivateKey",privateKeyText);
        bundle.putString("otherUserPublicKeyID",otherUserPublicKeyID);
        bundle.putString("currentUserPublicKeyID",publicKeyId);
        bundle.putString("otherUserPublicKey",otherUserPublicKey);



        bundleSendMessage = new Bundle();
        bundleSendMessage.putString("userIdChattingWith",userIdChattingWith);
        bundleSendMessage.putString("currentUserPrivateKey",privateKeyText);
        bundleSendMessage.putString("currentUserPublicKeyID",publicKeyId);

        // set Fragmentclass Arguments
        ephemeralMessagingFragment = new EphemeralMessagingFragment();
        ephemeralMessagingFragment.setArguments(bundle);

        /*archiveMessageFragment = new ArchiveMessageFragment();
        archiveMessageFragment.setArguments(bundle);*/

        sendMessageFragment = new SendMessageFragment();

        sendMessageFragment.setMessageHighlightListener(this);
        sendMessageFragment.setArguments(bundleSendMessage);

        liveMessageFragment = new LiveMessageFragment();
        liveMessageFragment.setArguments(bundle);




        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(sendMessageFragment);
        fragmentList.add(ephemeralMessagingFragment);

        /**
         * Uncomment the following snippet to add live fragment as well
         */
        fragmentList.add(liveMessageFragment);


        verticalViewPager = findViewById(R.id.pager);
        flingableViewPager = findViewById(R.id.flingpager);

        viewPager = findViewById(R.id.verticalpager);

        FragmentManager fragmentManager = getSupportFragmentManager();

        pagerAdapter = new VPagerAdapter(fragmentManager,fragmentList);



        viewPager.setAdapter(pagerAdapter);

        /**
        * When we add live fragment as well, then
         * viewPager.setCurrentItem(fragmentList.size()-2);
         * else
         * viewPager.setCurrentItem(fragmentList.size()-1);
        *
         *
         * The following line of code is the focused entity
        * */
        viewPager.setCurrentItem(fragmentList.size()-2);

       // observeLiveChatList();
        viewPager.setOffscreenPageLimit(2);



        viewPager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {


                /**
                 * Uncomment the following snippet to add live fragment also.
                 */
                if(i==2)
                {


                    liveMessageFragment.setLiveMessageEventListener(VerticalPageActivity.this);
                    liveMessageFragment.createLiveMessageDbInstance();
                    sendMessageFragment.removeListeners();
                  //  Toast.makeText(VerticalPageActivity.this,"Page no."+i,Toast.LENGTH_SHORT).show();

                    pageSelected = 2;

                }
                else if(i==1)
                {
                    //if(observingSendFragment)
                        //sendMessageFragment.removeObserveLiveChatList();
                    sendMessageFragment.removeListeners();

                   // Toast.makeText(VerticalPageActivity.this,"Page no."+i,Toast.LENGTH_SHORT).show();
                    pageSelected = 1;

                }

                else if(i==0)
                {
                   // sendMessageFragment.observeLiveChatList(userIdChattingWith);
/*                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });*/

                    /*new Thread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    }).start();*/

                    try {
                        sendMessageFragment.searchChats(userIdChattingWith);
                    }catch (Exception e)
                    {
                        //
                    }


                   // observingSendFragment = true;

                    //Toast.makeText(VerticalPageActivity.this,"Page no."+i,Toast.LENGTH_SHORT).show();

                    pageSelected = 0;


                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

/*        if(viewPager.getCurrentItem()==2)
        {
            liveMessageFragment.createLiveMessageDbInstance();
                    if(observingSendFragment)
            sendMessageFragment.removeObserveLiveChatList();
        }
        else if(viewPager.getCurrentItem()==1)
        {
            sendMessageFragment.observeLiveChatList(userIdChattingWith);
            observingSendFragment = true;
        }
        else
        {
            if(observingSendFragment)
                sendMessageFragment.removeObserveLiveChatList();
        }*/



    }




 /*   private void observeLiveChatList() {
        archiveActivityViewModelFactory = new ArchiveActivityViewModelFactory(userIdChattingWith);
        archiveActivityViewModel = ViewModelProviders.of(this, archiveActivityViewModelFactory).get(ArchiveActivityViewModel.class);
        archiveActivityViewModel.getChatList().observe(this, new Observer<List<ChatMessage>>() {
            @Override
            public void onChanged(@Nullable List<ChatMessage> chatMessageList) {
                archiveMessageFragment.setChatList(chatMessageList);
            }
        });
    }*/


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onPause() {
        try {
            sendMessageFragment.removeListeners();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("online");

            databaseReference.setValue(false);
            //databaseReference.onDisconnect().setValue(false);

        }catch (Exception e)
        {

        }
        super.onPause();

    }

    @Override
    protected void onResume() {
        try {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("online");

            databaseReference.setValue(true);

            if(pageSelected == 0)
            {
                sendMessageFragment.searchChats(userIdChattingWith);
            }
        }catch (Exception e) {
            //
        }
        super.onResume();
    }

    @Override
    public void onMessageHighlightOptionListener(Boolean highlight) {

        try {

            ephemeralMessagingFragment.setMessageHighlight(highlight);
        }catch (Exception e)
        {
            //
        }

    }

    private void LoadKeys()
    {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY,null);


        publicKeyText = sharedPreferences.getString(PUBLIC_KEY,null);


    }

    @Override
    public void changeFragment() {
        viewPager.setCurrentItem(1);
        liveMessageFragment = null;
        liveMessageFragment = new LiveMessageFragment();
        liveMessageFragment.setArguments(bundle);
    }
}
