package com.hieeway.hieeway;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.palette.graphics.Palette;

import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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
import com.hieeway.hieeway.Fragments.AddFeelingFragment;
import com.hieeway.hieeway.Fragments.ChatsFragment;
import com.hieeway.hieeway.Fragments.EditBioLayoutFragment;
import com.hieeway.hieeway.Fragments.FriendListFagment;
import com.hieeway.hieeway.Fragments.PeopleFragment;
import com.hieeway.hieeway.Fragments.ProfileFragment;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Interface.AddFeelingFragmentListener;
import com.hieeway.hieeway.Interface.AnimationArrowListener;
import com.hieeway.hieeway.Interface.ChatStampSizeListener;
import com.hieeway.hieeway.Interface.EditBioFragmentListener;
import com.hieeway.hieeway.Interface.ImageSelectionCropListener;
import com.hieeway.hieeway.Model.User;

import static com.hieeway.hieeway.MyApplication.darkMutedSwatch;

import com.hieeway.hieeway.Utils.MutedVideoView;
import com.spotify.android.appremote.api.SpotifyAppRemote;
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

    private static final int PERMISSION_PICK_IMAGE = 1000;
    private TextView text_profile;
    private CircleImageView profileBtnPressed, profileBtnUnpressed;

    private FrameLayout frameLayout;
    public static String USER_NAME;
    private EditBioLayoutFragment editBioLayoutFragment;
    private ChatsFragment chatsFragment;
    private PeopleFragment peopleFragment;
    private ProfileFragment profileFragment;
    private FriendListFagment friendListFagment;
    public MediaPlayer mediaPlayer;
    private Bundle bundle;
    private AddFeelingFragment addFeelingFragment;
    private ConstraintLayout nav_bar;
    private Boolean playFrameAnimation = true;
    private ImageView sendArrow, background_screen;
    private float buttonSizeAlpha = 1.30f;
    private float displayHeight;
    private ObjectAnimator animatorY, alphaArrow, animateTextLogo, alphaBackgroundScreen, alphaMasterHead, alphaNavBar, alphaFrameLayout;
    private AnimatorSet animatorSet;
    private TypeWriter typeWriter;
    private ImageView splash_logo, splash_logo_gradient, send_arrow;
    private int arrowAnimDuration = 600;
    private TextView master_head;

    private static final int IMAGE_REQUEST=1;
    private RelativeLayout splash_layout, video_splash_layout;
    private DatabaseReference databaseReference;
    private Button uploadButton;
    private StorageTask uploadTask;
    private StorageReference storageReference;
    private RelativeLayout progressBar;
    private RelativeLayout home_button_layout, friends_button_layout, people_button_layout, profile_button_layout;
    private RelativeLayout nav_parent_layout, app_logo;
    private Bitmap bitmap;
    private String phonenumber = "default";
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MUSIC_BEACON = "musicbeacon";
    public static final String SPOTIFY_CONNECT = "spotifyconnect";
    public static final String VISIBILITY = "visibility";
    private int fragmentId = 1;
    private MutedVideoView view, videoViewalt;
    private boolean videoPlayBack = true;
    private boolean continueAppLogoAnim = true;
    private int stopPosition;
    private Window window;
    public static String USER_PHOTO;
    public static String USER_ID;
    private FragmentManager menuFragmentManager;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_button_test);

        splash_layout = findViewById(R.id.splash_layout);
        video_splash_layout = findViewById(R.id.video_splash_layout);
        background_screen = findViewById(R.id.background_screen);

        profileBtnPressed = findViewById(R.id.profile_button_pressed);
        profileBtnUnpressed = findViewById(R.id.profile_button_unpressed);

        master_head = findViewById(R.id.master_head);
        app_logo = findViewById(R.id.app_logo);

        nav_bar = findViewById(R.id.nav_bar);

        profileFragment = new ProfileFragment();

        progressBar = findViewById(R.id.progress);

        menuFragmentManager = getSupportFragmentManager();


        getUserImageIntoNavButton();
        nav_parent_layout = findViewById(R.id.nav_parent_layout);

        master_head.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        userStatusOnDiconnect();

        /**
         *When you set the background as a gradiant animation list drawable, then you can uncomment the following code
         * to get animation
         */

        /*AnimationDrawable animationDrawable = (AnimationDrawable) nav_parent_layout.getBackground();

        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();*/


        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlack));



        /*getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);*/

   // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        view = findViewById(R.id.videoView);

        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.hiee_splash);
        /*Uri videoAlt = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.high_image_splash_alt);*/

        // String path = "android.resource://" + getPackageName() + "/" + R.raw.hieeway_splashscreen;
        view.setVideoURI(video);


        view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                mediaPlayer = mp;
                mediaPlayer.setVolume(0, 0);
                mediaPlayer.start();


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        // progressBar.setAlpha(0.25f);

                        startSplash();

                        mediaPlayer.pause();
                        stopPosition = view.getCurrentPosition();
                    }
                }, 1500);

            }
        });


        view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {

                // app_logo.animate().alpha(1.0f).setDuration(500);
                // appLogoAnimation(false);
                view.setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                // startSplash();
                animateArrow();


                /*if(videoPlayBack)
                view.start();*/

            }
        });


        //initiateNavActivity();
/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splash_layout.setVisibility(View.GONE);
                initiateNavActivity();
            }
        },2000);*/


    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }

    public void appLogoAnimation(Boolean reverse) {
        float alpha = 1.0f;
        if (reverse)
            alpha = 0.0f;
        app_logo.animate().alpha(alpha).setDuration(500);
        /*new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                app_logo.animate().alpha(0.0f).setDuration(500);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        if(continueAppLogoAnim)
                            appLogoAnimation();


                    }
                },500);
            }
        },500);*/
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


        bundle = new Bundle();
        bundle.putString("phonenumber", phonenumber);



        friendListFagment = new FriendListFagment();
        friendListFagment.setArguments(bundle);

        editBioLayoutFragment = new EditBioLayoutFragment();

        addFeelingFragment = new AddFeelingFragment();

        menuFragmentManager.beginTransaction()
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


        /**
         * Uncomment
         */


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

/**
 * Uncomment
 */



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

        /**
         * Uncomment
         */


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

        /**
         * Uncomment
         */


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

                loadImageThread(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadImageThread(DataSnapshot dataSnapshot) {

        final TaskCompletionSource<Bitmap> source = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {


                if (dataSnapshot.exists()) {

                    User user = dataSnapshot.getValue(User.class);

                    phonenumber = user.getPhonenumber();

                    USER_ID = user.getUserid();
                    USER_NAME = user.getUsername();
                    USER_PHOTO = user.getPhoto();

                    if (!user.getPhoto().equals("default")) {
                        try {
                            //  Glide.with(NavButtonTest.this).load(user.getPhoto()).into(profileBtnPressed);
                            //Glide.with(NavButtonTest.this).load(user.getPhoto()).into(profileBtnUnpressed);

                            bitmap = Glide.with(NavButtonTest.this)
                                    .asBitmap()
                                    .load(user.getPhoto())
                                    .submit(100, 100)
                                    .get();

                            // profileFragment.setBitmap(bitmap);

                            source.setResult(bitmap);


                        } catch (Exception e) {

                            source.setException(e);

                        }
                    } else {
                        //profileBtnPressed.setImageResource(R.drawable.no_profile);
                        //profileBtnUnpressed.setImageResource(R.drawable.no_profile);
                        source.setException(new NullPointerException());

                    }


                } else {
                    source.setException(new NullPointerException());
                }

            }
        }).start();


        Task<Bitmap> task = source.getTask();
        task.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
            @Override
            public void onComplete(@NonNull Task<Bitmap> task) {
                if (task.isSuccessful()) {
                    Glide.with(NavButtonTest.this).load(task.getResult()).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                           /* final Matrix matrix = profileBtnPressed.getImageMatrix();
                            matrix.postScale(1, 1);

                            profileBtnPressed.setImageMatrix(matrix);*/


                            return false;
                        }
                    }).into(profileBtnPressed);
                    Glide.with(NavButtonTest.this).load(task.getResult()).addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {


                            /*final Matrix matrix = profileBtnUnpressed.getImageMatrix();
                            matrix.postScale(1, 1);
                            profileBtnUnpressed.setImageMatrix(matrix);*/

                            return false;
                        }
                    }).into(profileBtnUnpressed);
                    profileFragment.setBitmap(task.getResult());
                } else {
                    // Toast.makeText(NavButtonTest.this, "Image not uploaded", Toast.LENGTH_SHORT).show();
                    profileBtnPressed.setImageResource(R.drawable.no_profile);
                    profileBtnUnpressed.setImageResource(R.drawable.no_profile);
                }
            }


        });


    }

    private void setPaletteColor() {
        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(@Nullable Palette palette) {

                /*vibrantSwatch = palette.getVibrantSwatch();
                darkVibrantSwatch = palette.getDarkVibrantSwatch();
                lightVibrantSwatch = palette.getLightVibrantSwatch();
                mutedSwatch = palette.getMutedSwatch();*/
                darkMutedSwatch = palette.getDarkMutedSwatch();
                /*lightMutedSwatch = palette.getLightMutedSwatch();
                dominantSwatch = palette.getDominantSwatch();*/

                // Palette.Swatch currentSwatch = null;
                /*
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
                    currentSwatch = dominantSwatch;*/


                /*if (currentSwatch != null) {
                    //relativeLayout.setBackgroundColor(currentSwatch.getRgb());

                    if(darkMutedSwatch!=null) {

                        currentSwatch = darkMutedSwatch;
                        *//*top_fade.setBackgroundTintList(ColorStateList.valueOf(currentSwatch.getRgb()));
                        bottom_fade.setBackgroundTintList(ColorStateList.valueOf(currentSwatch.getRgb()));
                        overlay_fade.setBackgroundTintList(ColorStateList.valueOf(currentSwatch.getRgb()));*//*



                 *//*top_fade.setBackgroundColor(currentSwatch.getRgb());
                    bottom_fade.setBackgroundColor(currentSwatch.getRgb());
                    overlay_fade.setBackgroundColor(currentSwatch.getRgb());*//*
                 *//*username.setTextColor(currentSwatch.getTitleTextColor());
                        feeling_txt.setTextColor(currentSwatch.getTitleTextColor());
                        bio_txt.setTextColor(currentSwatch.getTitleTextColor());
                        name.setTextColor(currentSwatch.getTitleTextColor());*//*

                        try {
                            *//*window = getActivity().getWindow();
                            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                            window.setStatusBarColor(currentSwatch.getRgb());*//*
                        } catch (Exception e) {
                            //
                        }
                    }


                    // ...
                } else {
                    //Toast.makeText(getActivity(), "Null color", Toast.LENGTH_SHORT).show();
                }*/


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

        friends_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground_accent)));

       // friendsBtnUnpressed.setBackground(getDrawable(R.drawable.ic_friends_active));

        text_friends.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (fragmentId > 2) {

                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.container_layout, friendListFagment)
                            .remove(chatsFragment)
                            .remove(peopleFragment)
                            .remove(profileFragment).commit();
                } else {

                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                            .replace(R.id.container_layout, friendListFagment)
                            .remove(chatsFragment)
                            .remove(peopleFragment)
                            .remove(profileFragment).commit();
                }

                fragmentId = 2;

            }
        }, 500);


    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            try {
                profileFragment.setBottomSheetBehavior(event);
                // chatsFragment.setBottomSheetBehavior(event);
            } catch (Exception e) {
                //
            }
        }

        return super.dispatchTouchEvent(event);
    }

    public Palette createPaletteSync(Bitmap bitmap) {
        Palette p = Palette.from(bitmap).generate();
        return p;
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


        home_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground_accent)));

        text_home.setVisibility(View.VISIBLE);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                menuFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                        .replace(R.id.container_layout, chatsFragment)
                        .remove(friendListFagment)
                        .remove(peopleFragment)
                        .remove(profileFragment).commit();

                fragmentId = 1;

            }
        }, 500);



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

        people_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground_accent)));

        // homeBtnPressed.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        text_search.setVisibility(View.VISIBLE);

/*        menuFragmentManager.beginTransaction()
                 .setCustomAnimations(R.anim.enter_right_to_left,R.anim.exit_right_to_left)
                .replace(R.id.container_layout, peopleFragment).commit();*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(fragmentId>3) {

                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
                            .replace(R.id.container_layout, peopleFragment).commit();
                } else {

                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                            .replace(R.id.container_layout, peopleFragment).commit();
                }
                fragmentId=3;

            }
        }, 500);



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

        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nav_bar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
            }
        }, 100);*/


        profileBtnPressed.setAlpha(1.0f);

        //profile_button_layout.setBackground(getDrawable(R.drawable.active_nav_background));

        text_profile.setVisibility(View.VISIBLE);

        profile_button_layout.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.darkButtonBackground_accent)));

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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                menuFragmentManager.beginTransaction()
                        .setCustomAnimations(R.anim.enter_right_to_left, R.anim.exit_right_to_left)
                        .replace(R.id.container_layout, profileFragment).commit();
                fragmentId = 4;

            }
        }, 500);

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
            // Toast.makeText(this, ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            //Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
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
                        try {
                            bitmap = Glide.with(NavButtonTest.this)
                                    .asBitmap()
                                    .load(mUri)
                                    .submit(50, 50)
                                    .get();

                            profileFragment.setBitmap(bitmap);

                        } catch (Exception e) {
                            //
                        }




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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                nav_bar.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.nav_btn_background_accent_color)));
            }
        }, 100);

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



        text_home = findViewById(R.id.text_home);

        text_home.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));






        // overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right);


        /*final Animation hyperspaceJump = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        splash_logo.setAnimation(hyperspaceJump);
*/
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        displayHeight = size.y;

        /*typeWriter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        master_head.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));

        typeWriter.setCharacterDelay(75);
        typeWriter.animateText("Hieeway");*/

        initiateNavActivity();

        //animateArrow();
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


               // animateArrow();
            }
        },1250);*/





    }

    public void animateArrow() {


        appLogoAnimation(true);
        window.setStatusBarColor(ContextCompat.getColor(NavButtonTest.this, R.color.nav_status_color_accent));
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

        visibleAnimate.setDuration(arrowAnimDuration * 5);



/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {*/

                send_arrow.setAlpha(1.0f);

                final Animation accel = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.accelerate_image);
                final Animation accelFaster = AnimationUtils.loadAnimation(NavButtonTest.this, R.anim.accelerate_image_faster);

        video_splash_layout.animate().alpha(0.0f).setDuration(300);

                accel.setRepeatMode(Animation.INFINITE);
                accelFaster.setRepeatMode(Animation.INFINITE);


                // splash_logo_gradient.setAnimation(accel);
                // typeWriter.setAnimation(accelFaster);


                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        typeWriter.animate().translationYBy(-displayHeight / 1.5f).setDuration(arrowAnimDuration / 2);
                        continueAppLogoAnim = false;


                        /*new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                initiateNavActivity();
                            }
                        },arrowAnimDuration);*/





                    }
                }, arrowAnimDuration / 4);


                typeWriter.animate().alpha(0.0f).setDuration(arrowAnimDuration);
                // splash_logo_gradient.animate().alpha(0.0f).setDuration(750);

                master_head.setVisibility(View.GONE);

                animatorSet.playTogether(animatorY,alphaArrow/*, animateTextLogo*/,alphaBackgroundScreen/*,alphaMasterHead*//*,alphaNavBar, alphaFrameLayout*/);
                animatorSet.start();
        //typeWriter.animate().translationYBy(-displayHeight/2).setDuration(arrowAnimDuration/2);

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
                videoPlayBack = false;
                // view.stopPlayback();
                splash_layout.setVisibility(View.GONE);
                send_arrow.setVisibility(View.GONE);
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
        } catch (Exception e) {
            //
        }

        /**
         * Music Beacon start
         */
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        try {
            if (sharedPreferences.getBoolean(MUSIC_BEACON, false)
                    && sharedPreferences.getBoolean(SPOTIFY_CONNECT, false)) {


                SpotifyAppRemote spotifyAppRemote = SpotifyRemoteHelper.getInstance().getSpotifyAppRemote();
                //if(spotifyAppRemote==null)
                /**
                 * Uncomment this to start Music Beam Service
                 */
                /*Intent intent = new Intent(NavButtonTest.this, MusicBeamService.class);
                intent.putExtra("forcestart", true);
                startService(intent);*/
            }
        } catch (Exception e) {
            //
        }


    }

    @Override
    public void onBackPressed() {
        if (fragmentId == 1)
            super.onBackPressed();
        else {
            activateHomeBtn();
            deactivateFriendsBNtn();
            deactivateSearchBtn();
            deactivateProfileBtn();
            animateBottomNavMenuText(text_home, homeBtnPressed);
        }
    }

    @Override
    public void setChatStampSizeActivity(int chatstampSize) {
        chatsFragment.setChatStampSizeFragment(chatstampSize);
    }

    @Override
    public void playArrowAnimation() {

        // animateArrow();
        //view.seekTo(stopPosition);
        progressBar.setAlpha(0.0f);

        try {
            if (mediaPlayer != null)
                mediaPlayer.start();
        } catch (Exception e) {

        }
    }


    @Override
    public void setEditBioChange(Boolean returnFromEditBio,String currentBio) {
        if(returnFromEditBio)
        {

            profileFragment.setImageSelectionCropListener(NavButtonTest.this);
            profileFragment.setEditBioFragmentListener(NavButtonTest.this);
            profileFragment.setAddFeelingFragmentListener(NavButtonTest.this);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                            .replace(R.id.container_layout, profileFragment).commit();
                }
            }, 500);

        }
        else
        {
            editBioLayoutFragment = new EditBioLayoutFragment();
            editBioLayoutFragment.setCurrentBio(NavButtonTest.this,currentBio);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                            .replace(R.id.container_layout, editBioLayoutFragment).commit();
                }
            }, 500);

        }

    }

    @Override
    public void setFeelingChange(Boolean returnFromAddFeeling, String feelingIcon, String feelingText) {

        if (returnFromAddFeeling) {

            profileFragment.setImageSelectionCropListener(NavButtonTest.this);
            profileFragment.setEditBioFragmentListener(NavButtonTest.this);
            profileFragment.setAddFeelingFragmentListener(NavButtonTest.this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                            .replace(R.id.container_layout, profileFragment).commit();
                }
            }, 500);


        } else {
            addFeelingFragment = new AddFeelingFragment();

            addFeelingFragment.setAddFeelingFragmentListener(NavButtonTest.this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    menuFragmentManager.beginTransaction()
                            .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                            .replace(R.id.container_layout, addFeelingFragment).commit();

                }
            }, 500);

        }

    }
}
