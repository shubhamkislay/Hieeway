package com.shubhamkislay.jetpacklogin;


import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import static com.shubhamkislay.jetpacklogin.MyApplication.notificationIDHashMap;


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
    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    String userFeeling;
    String feelingDefault;
    LiveMessageFragment liveMessageFragment;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    RelativeLayout feeling_splash_layout;
    ImageView feelingDefaultEmoji;
    TextView feelingCustomEmoji;
    String live;
    Point size;
    float displayHeight;
    int emojiYTransitionDuration = 1250;
    int emojiAplhaDuration = 500;
    String publicKeyText, privateKeyText, otherUserPublicKey, otherUserPublicKeyID, publicKeyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_page);



        Intent intent = getIntent();

        notificationIDHashMap.put(intent.getStringExtra("userValueIntentExtra"), 0);

        messageList = new ArrayList<>();
        sendMessagelist = new ArrayList<>();
        feeling_splash_layout = findViewById(R.id.feeling_splash_layout);
        feelingDefaultEmoji = findViewById(R.id.feelingDefaultEmoji);
        feelingCustomEmoji = findViewById(R.id.feelingCustomEmoji);


        final String usernameChattingWith = intent.getStringExtra("username");
        userIdChattingWith = intent.getStringExtra("userid");
        final String photo = intent.getStringExtra("photo");
        live = intent.getStringExtra("live");

        notificationIDHashMap.put(userIdChattingWith + "numbersent", 0);
        notificationIDHashMap.put(userIdChattingWith + "numberreply", 0);

        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;


        if (intent.getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("feeling")) {
                    feelingDefault = intent.getStringExtra("default");
                    if (feelingDefault.equals("no")) {
                        feelingCustomEmoji.setText(intent.getStringExtra("feeling"));
                        feelingCustomEmoji.setVisibility(View.VISIBLE);
                        feeling_splash_layout.setVisibility(View.VISIBLE);
                        feelingCustomEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                        feelingCustomEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                feelingCustomEmoji.animate().alpha(0.0f).setDuration(emojiAplhaDuration);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingCustomEmoji.setVisibility(View.GONE);
                                        feeling_splash_layout.setVisibility(View.GONE);
                                    }
                                }, 1000);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                    }
                                }, emojiAplhaDuration);

                            }
                        }, 2000);

                    } else {

                        String feeling = intent.getStringExtra("feeling");
                        switch (feeling) {
                            case HAPPY:
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_happy));
                                feelingDefaultEmoji.setVisibility(View.VISIBLE);
                                feelingDefaultEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                                feelingDefaultEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);
                                feeling_splash_layout.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingDefaultEmoji.animate().alpha(0.0f).setDuration(emojiAplhaDuration);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feelingDefaultEmoji.setVisibility(View.GONE);
                                                feeling_splash_layout.setVisibility(View.GONE);
                                            }
                                        }, 1000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                            }
                                        }, emojiAplhaDuration);
                                    }
                                }, 2000);
                                break;

                            case ANGRY:
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_angry));
                                feelingDefaultEmoji.setVisibility(View.VISIBLE);
                                feeling_splash_layout.setVisibility(View.VISIBLE);
                                feelingDefaultEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                                feelingDefaultEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingDefaultEmoji.animate().alpha(0.0f).setDuration(emojiAplhaDuration);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feelingDefaultEmoji.setVisibility(View.GONE);
                                                feeling_splash_layout.setVisibility(View.GONE);
                                            }
                                        }, 1000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                            }
                                        }, emojiAplhaDuration);
                                    }
                                }, 2000);
                                break;

                            case SAD:
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_sad));
                                feelingDefaultEmoji.setVisibility(View.VISIBLE);
                                feeling_splash_layout.setVisibility(View.VISIBLE);
                                feelingDefaultEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                                feelingDefaultEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingDefaultEmoji.animate().alpha(0.0f).setDuration(emojiAplhaDuration);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feelingDefaultEmoji.setVisibility(View.GONE);
                                                feeling_splash_layout.setVisibility(View.GONE);
                                            }
                                        }, 1000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                            }
                                        }, emojiAplhaDuration);
                                    }
                                }, 2000);
                                break;

                            case BORED:
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_bored));
                                feelingDefaultEmoji.setVisibility(View.VISIBLE);
                                feeling_splash_layout.setVisibility(View.VISIBLE);
                                feelingDefaultEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                                feelingDefaultEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingDefaultEmoji.animate().alpha(0.0f).setDuration(emojiAplhaDuration);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feelingDefaultEmoji.setVisibility(View.GONE);
                                                feeling_splash_layout.setVisibility(View.GONE);
                                            }
                                        }, 1000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                            }
                                        }, emojiAplhaDuration);
                                    }
                                }, 2000);
                                break;

                            case CONFUSED:
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_confused));
                                feelingDefaultEmoji.setVisibility(View.VISIBLE);
                                feeling_splash_layout.setVisibility(View.VISIBLE);
                                feelingDefaultEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                                feelingDefaultEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingDefaultEmoji.animate().alpha(0.0f).setDuration(emojiAplhaDuration);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feelingDefaultEmoji.setVisibility(View.GONE);
                                                feeling_splash_layout.setVisibility(View.GONE);
                                            }
                                        }, 1000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                            }
                                        }, emojiAplhaDuration);
                                    }
                                }, 2000);
                                break;

                            case EXCITED:
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.emoticon_feeling_excited));
                                feelingDefaultEmoji.setVisibility(View.VISIBLE);
                                feeling_splash_layout.setVisibility(View.VISIBLE);
                                feelingDefaultEmoji.animate().translationY(-displayHeight / 2).setDuration(emojiYTransitionDuration);
                                feelingDefaultEmoji.animate().alpha(1.0f).setDuration(emojiYTransitionDuration);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        feelingDefaultEmoji.animate().alpha(0.0f).setDuration(1000);
                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feelingDefaultEmoji.setVisibility(View.GONE);
                                                feeling_splash_layout.setVisibility(View.GONE);
                                            }
                                        }, 1000);

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                feeling_splash_layout.animate().alpha(0.0f).setDuration(800);
                                            }
                                        }, emojiAplhaDuration);
                                    }
                                }, 2000);
                                break;

                        }
                    }
                }
                if (key.equals("userValueIntentExtra")) {
                    //notificationIDHashMap.put(intent.getStringExtra("userValueIntentExtra"),0);
                    // Toast.makeText(VerticalPageActivity.this,"Updated Notification",Toast.LENGTH_SHORT).show();
                }
            }
        }




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
        if (live.equals("no"))
            viewPager.setCurrentItem(fragmentList.size()-2);

        else
            viewPager.setCurrentItem(fragmentList.size() - 1);


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
        notificationIDHashMap.put(userIdChattingWith + "numbersent", 0);
        notificationIDHashMap.put(userIdChattingWith + "numberreply", 0);
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
