package com.hieeway.hieeway;


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
import android.view.MotionEvent;
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
import com.hieeway.hieeway.Adapters.VPagerAdapter;
import com.hieeway.hieeway.Fragments.EphemeralMessagingFragment;
import com.hieeway.hieeway.Fragments.LiveMessageFragment;
import com.hieeway.hieeway.Fragments.SendMessageFragment;
import com.hieeway.hieeway.Interface.LiveMessageEventListener;
import com.hieeway.hieeway.Interface.MessageHighlightListener;
import com.hieeway.hieeway.Interface.YoutubeBottomFragmentStateListener;
import com.hieeway.hieeway.Model.ChatMessage;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.hieeway.hieeway.MyApplication.notificationIDHashMap;


public class VerticalPageActivity extends AppCompatActivity implements MessageHighlightListener, LiveMessageEventListener, YoutubeBottomFragmentStateListener {


    private VerticalViewPager verticalViewPager;
    private fr.castorflex.android.verticalviewpager.VerticalViewPager viewPager;
    private FlingableViewPager flingableViewPager;
    public SendMessageFragment  sendMessageFragment;
    private PagerAdapter pagerAdapter;
    private String userIdChattingWith;
    public EphemeralMessagingFragment ephemeralMessagingFragment;

    public Boolean observingSendFragment = false;

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
    private boolean enabled;
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
    Boolean openSendMessageFragment = false;
    String publicKeyText, privateKeyText, otherUserPublicKey, otherUserPublicKeyID, publicKeyId;
    String feededMessageID = "default";
    private boolean bottomPageUp = false;
    public static String userIDCHATTINGWITH = "";
    public static String userNameChattingWith = "";
    public static String USERPHOTO = "";
    public static String USER_PRIVATE_KEY = "";
    public static String OTHER_USER_PUBLIC_KEY = "";
    public static String CURRENT_USERNAME = "";
    public static String CURRENT_USERPHOTO = "";

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
        userIDCHATTINGWITH = userIdChattingWith;
        userNameChattingWith = usernameChattingWith;
        final String photo = intent.getStringExtra("photo");
        USERPHOTO = photo;
        live = intent.getStringExtra("live");

        notificationIDHashMap.put(userIdChattingWith + "numbersent", 0);
        notificationIDHashMap.put(userIdChattingWith + "numberreply", 0);


        Display display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        privateKeyText = sharedPreferences.getString(PRIVATE_KEY, null);
        publicKeyText = sharedPreferences.getString(PUBLIC_KEY, null);
        publicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID, null);

        USER_PRIVATE_KEY = privateKeyText;


        if (intent.getExtras() != null) {

            for (String key : getIntent().getExtras().keySet()) {
                if (key.equals("feeling")) {
                    feelingDefault = intent.getStringExtra("default");
                    if (feelingDefault.equals("no")) {
                        feelingDefaultEmoji.setVisibility(View.GONE);
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
                                feelingCustomEmoji.setVisibility(View.GONE);
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
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
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
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
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
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
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
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
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
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
                                feelingDefaultEmoji.setBackground(getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
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
                } else if (key.equals("revealmessage")) {
                    if (intent.getStringExtra("revealmessage").equals("no")) {
                        Intent revealActivityIntent = new Intent(VerticalPageActivity.this, RevealReplyActivity.class);

                        revealActivityIntent.putExtra("userIdChattingWith", userIdChattingWith);
                        revealActivityIntent.putExtra("currentUserPrivateKey", privateKeyText);
                        revealActivityIntent.putExtra("currentUserPublicKeyID", publicKeyId);
                        revealActivityIntent.putExtra("photo", photo);
                        revealActivityIntent.putExtra("username", usernameChattingWith);



                        startActivity(revealActivityIntent);
                    } else if (intent.getStringExtra("revealmessage").equals("yes")) {
                        openSendMessageFragment = true;
                        feededMessageID = intent.getStringExtra("messageID");
                    }
                }

            }
        }








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
                        OTHER_USER_PUBLIC_KEY = otherUserPublicKey;
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
        bundleSendMessage.putString("messageID", feededMessageID);
        bundleSendMessage.putString("usernameChattingWith", usernameChattingWith);
        bundleSendMessage.putString("userIdChattingWith", userIdChattingWith);
        bundleSendMessage.putString("photo", photo);

        // set Fragmentclass Arguments
        ephemeralMessagingFragment = new EphemeralMessagingFragment();
        ephemeralMessagingFragment.setArguments(bundle);


        sendMessageFragment = new SendMessageFragment();

        sendMessageFragment.setMessageHighlightListener(this);
        sendMessageFragment.setArguments(bundleSendMessage);

        liveMessageFragment = new LiveMessageFragment();
        liveMessageFragment.setArguments(bundle);
        liveMessageFragment.setYoutubeBottomFragmentStateListener(this);




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
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (bottomPageUp)
                    return true;
                else
                    return false;
            }
        });
        /**
        * When we add live fragment as well, then
         * viewPager.setCurrentItem(fragmentList.size()-2);
         * else
         * viewPager.setCurrentItem(fragmentList.size()-1);
        *
         *
         * The following line of code is the focused entity
        * */
        if (live.equals("no")) {
            if (openSendMessageFragment)
                viewPager.setCurrentItem(fragmentList.size() - 3);
            else
                viewPager.setCurrentItem(fragmentList.size() - 2);
        } else {

            //Toast.makeText(VerticalPageActivity.this,"Swipe up to join live messaging",Toast.LENGTH_SHORT).show();
            if (live.equals("live")) {

                liveMessageFragment.setLiveMessageEventListener(VerticalPageActivity.this);


                liveMessageFragment.showLiveMessageDialog(VerticalPageActivity.this);
                sendMessageFragment.removeListeners();

                pageSelected = 2;

                viewPager.setCurrentItem(fragmentList.size() - 1);

            }
        }


       // observeLiveChatList();
        viewPager.setOffscreenPageLimit(2);


        // viewPager.setOverScrollMode(View.OVER_SCROLL_ALWAYS);

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


                    //liveMessageFragment.initialiseLiveragment(VerticalPageActivity.this);

                    liveMessageFragment.setLiveMessageEventListener(VerticalPageActivity.this);


                    liveMessageFragment.showLiveMessageDialog(VerticalPageActivity.this);
                    sendMessageFragment.removeListeners();
                  //  Toast.makeText(VerticalPageActivity.this,"Page no."+i,Toast.LENGTH_SHORT).show();

                    pageSelected = 2;


                }
                else if(i==1)
                {
                    //if(observingSendFragment)
                        //sendMessageFragment.removeObserveLiveChatList();
                    if (pageSelected == 2) {

                        liveMessageFragment.destoryLiveFragment();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("present", false);
                        FirebaseDatabase.getInstance().getReference("ChatList")
                                .child(userIDCHATTINGWITH)
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .updateChildren(hashMap);
                    }

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


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                liveMessageFragment.setBottomSheetBehavior(event);
                // chatsFragment.setBottomSheetBehavior(event);
            } catch (Exception e) {
                //
            }
        }

        return super.dispatchTouchEvent(event);
    }


    @Override
    public void onBackPressed() {
        if (!bottomPageUp) {
            super.onBackPressed();
            finish();
        } else {
            try {
                liveMessageFragment.closeBottomSheet();
            } catch (Exception e) {

            }
        }
    }

    @Override
    protected void onPause() {

        notificationIDHashMap.put(userIdChattingWith + "numbersent", 0);
        notificationIDHashMap.put(userIdChattingWith + "numberreply", 0);

        userNameChattingWith = "";

        if (pageSelected == 2) {

            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("present", false);
            FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userIDCHATTINGWITH)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap);
            try {
                //liveMessageFragment.destoryLiveFragment();
            } catch (Exception e) {
                //
            }
        }
        try {
            sendMessageFragment.removeListeners();
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("online");

            databaseReference.setValue(false);
            //databaseReference.onDisconnect().setValue(false);

        } catch (Exception e) {

        }
        super.onPause();


    }


    @Override
    protected void onResume() {
        super.onResume();

        if (pageSelected == 2) {
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("present", true);
            FirebaseDatabase.getInstance().getReference("ChatList")
                    .child(userIDCHATTINGWITH)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .updateChildren(hashMap);
        }
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

    private void LoadKeys() {
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

    @Override
    public void setDrag(Boolean state) {


        bottomPageUp = state;

            /*if (bottomPageUp)
                viewPager.beginFakeDrag();
            else {
                if (viewPager.isFakeDragging())
                    viewPager.endFakeDrag();
            }*/

    }


}
