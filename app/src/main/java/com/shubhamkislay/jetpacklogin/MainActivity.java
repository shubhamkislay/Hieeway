package com.shubhamkislay.jetpacklogin;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.shubhamkislay.jetpacklogin.Interface.GoogleButtonListener;

import java.util.ArrayList;
import java.util.List;

import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_1_ID;
import static com.shubhamkislay.jetpacklogin.MyApplication.CHANNEL_2_ID;

public class MainActivity extends AppCompatActivity implements GoogleButtonListener {


    Button loginBtn, registerBtn, get_started, get_started_back;
    FirebaseAuth firebaseAuth;

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    RelativeLayout splash_layout;

    ImageView sendArrow, background_screen;
    float buttonSizeAlpha = 1.30f;
    float displayHeight;
    ObjectAnimator animatorY, alphaArrow, animateTextLogo, alphaBackgroundScreen, alphagetStartedBtn, alphagetStartedBtnBack, alphaFrameLayout;
    AnimatorSet animatorSet;
    TypeWriter typeWriter;
    TextView master_head;
    FrameLayout frameLayout;
    List<Fragment> fragmentList;
    RegisterEmailEntryFragment registerEmailEntryFragment;
    RegisterAuthenticateActivity registerAuthenticateActivity;
    RegisterUsernameEntryFragment registerUsernameEntryFragment;
    Button change_frag_btn;
    int fragment_number=0;
    private ImageView splash_logo, splash_logo_gradient, send_arrow;
    private int arrowAnimDuration = 600;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*        notificationManager = NotificationManagerCompat.from(this);
        editTextTitle = findViewById(R.id.edit_text_title);

        editTextMessage = findViewById(R.id.edit_text_message);*/

        frameLayout = findViewById(R.id.framelayout);

        registerAuthenticateActivity = new RegisterAuthenticateActivity();
        registerEmailEntryFragment = new RegisterEmailEntryFragment();
        registerUsernameEntryFragment = new RegisterUsernameEntryFragment();
        fragmentList = new ArrayList<>();

        fragmentList.add(registerAuthenticateActivity);
        fragmentList.add(registerEmailEntryFragment);
        fragmentList.add(registerUsernameEntryFragment);


        firebaseAuth = FirebaseAuth.getInstance();

        get_started = findViewById(R.id.get_started);

        get_started_back = findViewById(R.id.get_started_back);


        splash_layout = findViewById(R.id.splash_layout);
        background_screen = findViewById(R.id.background_screen);



        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateArrow();
            }
        });

        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (fragment_number)
                {
                    case 0: fragment_number =1;
                        animateArrow();
                        get_started.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerEmailEntryFragment).commit();
                        registerEmailEntryFragment.setGoogleButtonListener(MainActivity.this);
                        break;

                    case 1: fragment_number =2;
                        animateArrow();
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerUsernameEntryFragment).commit();
                        break;

                    case 2: fragment_number =3;
                        animateArrow();
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerAuthenticateActivity).commit();
                        break;

                    case 3: fragment_number =1;
                       // animateArrow();
                        get_started.setVisibility(View.GONE);
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                                .replace(R.id.framelayout, registerEmailEntryFragment).commit();
                        break;



                }
            }
        });

        get_started.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    get_started.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                    get_started_back.animate().scaleX(buttonSizeAlpha).scaleY(buttonSizeAlpha).setDuration(0);
                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    get_started.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    get_started_back.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                else
                {
                    get_started.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    get_started_back.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                }
                return false;
            }
        });












        if(firebaseAuth.getCurrentUser()!=null)
        {
            //startActivity(new Intent(MainActivity.this,AlphaActivity.class));
            startActivity(new Intent(MainActivity.this,NavButtonTest.class));
            finish();
        }
        else
        {

            /*loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();

                }
            });

            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,RegisterActivity.class));
                    finish();
                }
            });*/
            startSplash();


        }

    }

    private void startSplash() {

        splash_logo = findViewById(R.id.splash_logo);

        splash_logo_gradient = findViewById(R.id.splash_logo_gradient);

        send_arrow = findViewById(R.id.send_arrow_splash);

        typeWriter = findViewById(R.id.logo_txt);

        master_head = findViewById(R.id.master_head);








        // overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);


        final Animation hyperspaceJump = AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_bounce);

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
                //initiateNavActivity();
               // animateArrow();
                get_started.setVisibility(View.VISIBLE);
                //get_started_back.setVisibility(View.VISIBLE);

                animateBottomNavMenuText(get_started,get_started_back);
            }
        },1250);





    }

    public void animateArrow() {


        animatorY = ObjectAnimator.ofFloat(send_arrow,"translationY",-displayHeight -(displayHeight)/3);
        animateTextLogo = ObjectAnimator.ofFloat(typeWriter,"translationY",-displayHeight -(displayHeight)/3);

        alphagetStartedBtn = ObjectAnimator.ofFloat(get_started,"alpha",1.0f,0.0f);
        alphagetStartedBtnBack = ObjectAnimator.ofFloat(get_started_back,"alpha",1.0f,0.0f);


        alphaArrow = ObjectAnimator.ofFloat(send_arrow,"alpha",1.0f,0.0f);

        alphaBackgroundScreen = ObjectAnimator.ofFloat(background_screen,"alpha",1.0f,0.0f);

      //  alphaNavBar = ObjectAnimator.ofFloat(nav_bar,"alpha",0.0f,1.0f);

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

        final Animation accel = AnimationUtils.loadAnimation(MainActivity.this, R.anim.accelerate_image);
        final Animation accelFaster = AnimationUtils.loadAnimation(MainActivity.this, R.anim.accelerate_image_faster);


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

        if(fragment_number==1)
            animatorSet.playTogether(animatorY,alphaArrow,alphaFrameLayout/*, animateTextLogo*/,alphaBackgroundScreen/*,alphagetStartedBtnBack,alphagetStartedBtn*/);
        else
            animatorSet.playTogether(animatorY,alphaArrow/*, animateTextLogo*/,alphaBackgroundScreen/*,alphagetStartedBtnBack,alphagetStartedBtn*/);

        animatorSet.start();


        animateBottomNavMenuText(get_started,get_started_back);
        get_started_back.setText("Change Fragment");
        get_started.setText("Change Fragment");





            /*visibleAnimate.playTogether(alphaNavBar, alphaFrameLayout);
            visibleAnimate.start();*/



/*            }
        },1250);*/





        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*Intent intent = new Intent(NavButtonTest.this,MainActivity.class);
                startActivity(intent);
                finish();*/
                splash_layout.setVisibility(View.GONE);
                send_arrow.setTranslationY(0);
                //initiateNavActivity();
            }
        },arrowAnimDuration);
    }
    public void animateBottomNavMenuText(Button navText, Button imageView)
    {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_bounce_anim);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        navText.setAnimation(hyperspaceJump);



        Animation hyperspaceJumpImg = AnimationUtils.loadAnimation(MainActivity.this, R.anim.text_bounce_anim);

        hyperspaceJumpImg.setRepeatMode(Animation.INFINITE);

        imageView.setAnimation(hyperspaceJump);
    }

    @Override
    public void onGoogleButtonPressed(String email, String name, String photourl) {

        fragment_number =2;
        animateArrow();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                .replace(R.id.framelayout, registerUsernameEntryFragment).commit();
        get_started.setVisibility(View.VISIBLE);

        registerUsernameEntryFragment.setUserData(email,name,photourl);

    }

/*

    public void sendOnChannel1(View v) {

        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Intent activityIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);


        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", message);

        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0,
                broadcastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.profile_pic);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_head)
                .setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                */
/*.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.big_text))
                        .setBigContentTitle("Bigggg")
                        .setSummaryText("Summary"))*//*

                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigLargeIcon(null)
                        .bigPicture(largeIcon))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
               // .setColor(getResources().getColor(R.color.colorPrimary))
                .setContentIntent(contentIntent)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
               // .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .build();


        notificationManager.notify(1, notification);

    }

    public void sendOnChannel2(View v) {

        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.hiewaylogomessageback);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_chat)
                .setContentTitle(title)
                .setContentText(message)
                */
/*.setStyle(new NotificationCompat.InboxStyle()
                        .addLine("This is line 1")
                        .addLine("This is line 2")
                        .addLine("This is line 3")
                        .addLine("This is line 4")
                        .addLine("This is line 5")
                        .addLine("This is line 6")
                        .addLine("This is line 7")
                        .addLine("This is line 8")
                        .setBigContentTitle("Bigggg")
                        .setSummaryText("Summary")
                )*//*

                .setStyle(new android.support.v4.media.app.NotificationCompat.MediaStyle())
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .build();


        notificationManager.notify(2, notification);

    }
*/

}
