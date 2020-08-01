package com.hieeway.hieeway;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.net.Uri;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.core.app.NotificationManagerCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media.MediaSessionManager;

import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
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
import com.hieeway.hieeway.Fragments.RegisterEmailEntryFragment;
import com.hieeway.hieeway.Fragments.RegisterPhoneNumberFragment;
import com.hieeway.hieeway.Fragments.RegisterUsernameEntryFragment;
import com.hieeway.hieeway.Interface.GoogleButtonListener;
import com.hieeway.hieeway.Interface.ImageSelectionCropListener;
import com.hieeway.hieeway.Interface.UsernameListener;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.Utils.MutedVideoView;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity implements GoogleButtonListener, UsernameListener, ImageSelectionCropListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int RC_HINT = 3;

    //Last commit stated as Profile Fragment is a major migration commit from appcompat to androidX

    Button loginBtn, registerBtn, get_started, get_started_back;
    FirebaseAuth firebaseAuth;

    private NotificationManagerCompat notificationManager;
    private EditText editTextTitle;
    private EditText editTextMessage;
    RelativeLayout splash_layout;

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String DEVICE_TOKEN = "devicetoken";
    public final static String HAPPY = "happy";
    private static final int RC_SIGN_IN = 1;
    public static final int PERMISSION_PICK_IMAGE = 1000;
    private static final int IMAGE_REQUEST=1;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    private Button uploadButton;
    private StorageTask uploadTask;
    private MediaPlayer mMediaPlayer;
    private MediaSessionManager mManager;
    private MediaSession mSession;
    private MediaController mController;


    GoogleApiClient mCredentialsApiClient;


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
    RegisterPhoneNumberFragment registerPhoneNumberFragment;
    private GoogleSignInClient mGoogleSignInClient;
    Button change_frag_btn;
    int fragment_number=0;
    private ImageView splash_logo, splash_logo_gradient, send_arrow;
    private int arrowAnimDuration = 600;
    String email,  name,  photourl, username;
    MutedVideoView video_view;
    private MediaPlayer mediaPlayer;

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

        registerPhoneNumberFragment = new RegisterPhoneNumberFragment();

        fragmentList = new ArrayList<>();

        //fragmentList.add(registerAuthenticateActivity);
        fragmentList.add(registerEmailEntryFragment);
        fragmentList.add(registerUsernameEntryFragment);
        fragmentList.add(registerPhoneNumberFragment);


        firebaseAuth = FirebaseAuth.getInstance();

        get_started = findViewById(R.id.get_started);

        get_started_back = findViewById(R.id.get_started_back);

        video_view = findViewById(R.id.video_view);

        splash_layout = findViewById(R.id.splash_layout);
        background_screen = findViewById(R.id.background_screen);

        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.this, gso);


        get_started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                get_started.setVisibility(View.GONE);
                switch (fragment_number)
                {
                    case 0: fragment_number =1;

                        try {
                            if (mediaPlayer != null)

                                mediaPlayer.start();
                        } catch (Exception e) {

                        }
                        get_started.setVisibility(View.GONE);
                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                animateArrow();
                                // get_started.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                                .replace(R.id.framelayout, registerEmailEntryFragment).commit();

                                        registerEmailEntryFragment.setGoogleButtonListener(MainActivity.this);
                                    }
                                }, arrowAnimDuration);

                            }
                        });


                        break;

                    case 1: fragment_number =2;
                        try {
                            if (mediaPlayer != null)

                                mediaPlayer.start();
                        } catch (Exception e) {

                        }

                        //animateArrow();
                        //  get_started.setVisibility(View.GONE);


                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                animateArrow();
                                //  get_started.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        registerUsernameEntryFragment.setImageSelectionCropListener(MainActivity.this);
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                                .replace(R.id.framelayout, registerUsernameEntryFragment).commit();
                                    }
                                }, arrowAnimDuration);

                            }
                        });

                        //registerUsernameEntryFragment.setImageSelectionCropListener(MainActivity.this);

                        break;

                    case 2: fragment_number =3;
                        // animateArrow();

                        try {
                            if (mediaPlayer != null)

                                mediaPlayer.start();
                        } catch (Exception e) {

                        }


                        //animateArrow();
                        //  get_started.setVisibility(View.GONE);


                        /*video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                animateArrow();
                                //  get_started.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                                .replace(R.id.framelayout, registerAuthenticateActivity).commit();

                                    }
                                }, arrowAnimDuration);

                            }
                        });*/
                        break;

                    case 3:
                        fragment_number = 4;
                       // animateArrow();
                        try {
                            if (mediaPlayer != null)

                                mediaPlayer.start();
                        } catch (Exception e) {

                        }




                        /*video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                animateArrow();
                                //  get_started.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                                .replace(R.id.framelayout, registerAuthenticateActivity).commit();

                                    }
                                }, arrowAnimDuration);

                            }
                        });*/

                        break;

                    case 4:
                        fragment_number = 1;
                        // animateArrow();
                        // get_started.setVisibility(View.GONE);
                        try {
                            if (mediaPlayer != null)

                                mediaPlayer.start();
                        } catch (Exception e) {

                        }





                        video_view.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                animateArrow();
                                //  get_started.setVisibility(View.GONE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getSupportFragmentManager().beginTransaction()
                                                .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                                                .replace(R.id.framelayout, registerEmailEntryFragment).commit();

                                    }
                                }, arrowAnimDuration);

                            }
                        });
                        break;



                }
            }
        });

        /*get_started.setOnTouchListener(new View.OnTouchListener() {
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


*/










        if(firebaseAuth.getCurrentUser()!=null)
        {

            DatabaseReference databaseReference =  FirebaseDatabase.getInstance().getReference("Users")
                    .child(firebaseAuth.getCurrentUser().getUid());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists())
                    {
                        startActivity(new Intent(MainActivity.this,NavButtonTest.class));
                        finish();
                    }
                    else
                    {
                        splash_layout.setVisibility(View.VISIBLE);

                        Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                                + R.raw.hiee_splash);
        /*Uri videoAlt = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.high_image_splash_alt);*/

                        // String path = "android.resource://" + getPackageName() + "/" + R.raw.hieeway_splashscreen;
                        video_view.setVideoURI(video);

                        video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {

                                mediaPlayer = mp;
                                mediaPlayer.setVolume(0, 0);
                                mediaPlayer.start();


                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        // progressBar.setAlpha(0.25f);

                                        // startSplash();

                                        get_started.setText("Continue with profile setup");
                                        // animateArrow();
                                        //   fragment_number = 0;
                                        startSplash();
                                        mediaPlayer.pause();
                                        //stopPosition = view.getCurrentPosition();
                                    }
                                }, 1500);

                            }
                        });
                        String public_key,publickeyid,device_token;

                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());


                        email = sharedPreferences.getString(EMAIL,null);
                        name = sharedPreferences.getString(NAME,null);
                        photourl = sharedPreferences.getString(PHOTO_URL,null);
                        public_key = sharedPreferences.getString(PUBLIC_KEY,null);
                        publickeyid = sharedPreferences.getString(PUBLIC_KEY_ID,null);
                        device_token = sharedPreferences.getString(DEVICE_TOKEN,null);


                        fragment_number =1;

                        registerUsernameEntryFragment.setImageSelectionCropListener(MainActivity.this);

                        registerUsernameEntryFragment.setUserData(email,name,photourl,MainActivity.this,databaseReference, device_token,public_key,publickeyid);

                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerUsernameEntryFragment).commit();
                        // get_started.setVisibility(View.VISIBLE);


                        /*firebaseAuth.signOut();
                        startSplash();*/
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }
        else
        {

            /*loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,.class));
                    finish();

                }
            });

            registerBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this,.class));
                    finish();
                }
            });*/

            Uri video = Uri.parse("android.resource://" + getPackageName() + "/"
                    + R.raw.hiee_splash);
        /*Uri videoAlt = Uri.parse("android.resource://" + getPackageName() + "/"
                + R.raw.high_image_splash_alt);*/

            // String path = "android.resource://" + getPackageName() + "/" + R.raw.hieeway_splashscreen;
            video_view.setVideoURI(video);

            video_view.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {

                    mediaPlayer = mp;
                    mediaPlayer.setVolume(0, 0);
                    mediaPlayer.start();


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            // progressBar.setAlpha(0.25f);

                            // startSplash();

                            //  get_started.setText("Continue with profile setup");
                            // animateArrow();
                            if (fragment_number == 0)
                                get_started.setVisibility(View.VISIBLE);
                            startSplash();
                            mediaPlayer.pause();
                            //stopPosition = view.getCurrentPosition();
                        }
                    }, 1800);

                    splash_layout.setVisibility(View.VISIBLE);
                    startSplash();


                }
            });
        }


    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
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

        uCrop.start(MainActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {


            if(requestCode==PERMISSION_PICK_IMAGE) {
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
            }



            else if(requestCode == UCrop.REQUEST_CROP) {
              //  Toast.makeText(this, "Image cropped",Toast.LENGTH_SHORT).show();
                handleCropResult(data);

            }

            else if(requestCode == UCrop.RESULT_ERROR) {
                handleCropError(data);
            }

            else if (requestCode == RC_SIGN_IN) {

               // Toast.makeText(this, "Google sign in",Toast.LENGTH_SHORT).show();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    registerEmailEntryFragment.firebaseAuthWithGoogle(account,account.getGivenName(), account.getFamilyName());


                } catch (ApiException e) {
                    // Google Sign In failed, update UI appropriately
                    // Log.w(TAG, "Google sign in failed", e);
                    // ...
                }
            }

        } else if (requestCode == RC_HINT) {
            if (resultCode == RESULT_OK) {
                Credential cred = data.getParcelableExtra(Credential.EXTRA_KEY);

            }
        }
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
            registerUsernameEntryFragment.setImageUri(resultUri);
            //Toast.makeText(this, "Image cropped",Toast.LENGTH_SHORT).show();

            if(uploadTask!=null&&uploadTask.isInProgress())
            {
                Toast.makeText(MainActivity.this,"Uploading..." , Toast.LENGTH_SHORT).show();
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

        //typeWriter.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        master_head.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-medium.otf"));

        /*typeWriter.setCharacterDelay(75);
        typeWriter.animateText("Hieeway");*/

       // animateArrow();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //initiateNavActivity();
               // animateArrow();
                //if (fragment_number == 0 || fragment_number == 1)
                //get_started.setVisibility(View.VISIBLE);
                //get_started_back.setVisibility(View.VISIBLE);

                // animateBottomNavMenuText(get_started,get_started_back);
            }
        },1250);





    }

    public void animateArrow() {


        animatorY = ObjectAnimator.ofFloat(send_arrow,"translationY",-displayHeight -(displayHeight)/3);
        animateTextLogo = ObjectAnimator.ofFloat(typeWriter,"translationY",-displayHeight -(displayHeight)/3);

        /*alphagetStartedBtn = ObjectAnimator.ofFloat(get_started,"alpha",1.0f,0.0f);
        alphagetStartedBtnBack = ObjectAnimator.ofFloat(get_started_back,"alpha",1.0f,0.0f);*/


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


        // animateBottomNavMenuText(get_started,get_started_back);
/*        get_started_back.setText("Change Fragment");
        get_started.setText("Change Fragment");*/





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
        Animation hyperspaceJump = AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        navText.setAnimation(hyperspaceJump);



        Animation hyperspaceJumpImg = AnimationUtils.loadAnimation(MainActivity.this, R.anim.image_bounce);

        hyperspaceJumpImg.setRepeatMode(Animation.INFINITE);

        imageView.setAnimation(hyperspaceJump);
    }



    @Override
    public void onUsernameListener(String username,String name, String photourl,final DatabaseReference reference,final  String device_token,final String public_key,final String publickeyid) {

        this.username = username;
        this.photourl = photourl;
        this.name = name;


        HashMap<String, Object> registerMap = new HashMap<>();
        registerMap.put("email", email);
        registerMap.put("name", name);
        registerMap.put("phonenumber", "default");
        registerMap.put("photo",photourl);
        registerMap.put("username", username);
        registerMap.put("userid", firebaseAuth.getCurrentUser().getUid());
        registerMap.put("token",device_token);
        registerMap.put("feeling",HAPPY);
        registerMap.put("feelingIcon", "default");
        registerMap.put(PUBLIC_KEY,public_key);
        registerMap.put("publicKeyId",publickeyid);

        reference.setValue(registerMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                //pg.dismiss();
                /*startActivity(new Intent(MainActivity.this,NavButtonTest.class));
                finish();*/

                animateArrow();
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                        .replace(R.id.framelayout, registerPhoneNumberFragment).commit();

            }
        });


        /*startActivity(new Intent(MainActivity.this,NavButtonTest.class));
        finish();*/

    }

    @Override
    public void onSignInClicked() {
        signIn();
    }

    @Override
    public void onGoogleButtonPressed(String email, String name, String photourl, DatabaseReference reference, String device_token) {

        //databaseReference.updateChildren(hashMap);
        startActivity(new Intent(MainActivity.this,NavButtonTest.class));
        finish();

    }

    @Override
    public void onGoogleButtonPressedKeyAvailable(final String email,final  String name,final  String photourl,final DatabaseReference reference,final  String device_token,final String public_key,final String publickeyid) {

        this.email = email;
        this.name = name;
        this.photourl = photourl;


        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {

                    User user = dataSnapshot.getValue(User.class);


                    HashMap<String, Object> hashMap = new HashMap<>();

                    hashMap.put("token", device_token);
                    hashMap.put(PUBLIC_KEY, public_key);
                    hashMap.put("publicKeyId", publickeyid);

                    databaseReference.updateChildren(hashMap);


                    if (user.getPhonenumber().equals("default")) {
                        animateArrow();
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerPhoneNumberFragment).commit();
                    } else {

                        startActivity(new Intent(MainActivity.this, NavButtonTest.class));
                        finish();
                    }
                }
                else
                {
                    fragment_number =2;
                   // animateArrow();

                    // get_started.setVisibility(View.VISIBLE);
                    registerUsernameEntryFragment.setImageSelectionCropListener(MainActivity.this);
                    registerUsernameEntryFragment.setUserData(email,name,photourl,MainActivity.this,reference, device_token,public_key,publickeyid);

                     animateArrow();
                            getSupportFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                    .replace(R.id.framelayout, registerUsernameEntryFragment).commit();



                    }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

/*        fragment_number =2;
        animateArrow();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                .replace(R.id.framelayout, registerUsernameEntryFragment).commit();
        // get_started.setVisibility(View.VISIBLE);

        registerUsernameEntryFragment.setUserData(email,name,photourl,MainActivity.this);*/



    }

    @Override
    public void imageSelect() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");

        startActivityForResult(intent, PERMISSION_PICK_IMAGE);

    }



    private String getExtension(Uri uri)
    {


        ContentResolver contentResolver = MainActivity.this.getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));

    }

    private void uploadImage(Uri imageUri)
    {

        /*final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this );

        progressDialog.setMessage("Uploading photo");
        progressDialog.show();*/

        registerUsernameEntryFragment.setProgressVisibility(true);

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


                        /*databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getUid());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("photo",mUri );
                        databaseReference.updateChildren(map);
                        progressDialog.dismiss();*/



                       // progressDialog.dismiss();

                        registerUsernameEntryFragment.setProgressVisibility(false);
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString(PHOTO_URL, mUri);
                        editor.apply();

                        registerUsernameEntryFragment.setUploadedImage(mUri);




                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"Uploading failed" ,Toast.LENGTH_SHORT).show();
                        //progressDialog.dismiss();
                        registerUsernameEntryFragment.setProgressVisibility(false);
                    }

                }
            });


        }
        else
        {
            Toast.makeText(MainActivity.this,"No Image selected",Toast.LENGTH_SHORT ).show();
        }


    }

    public void selectPhoneNumber(View view) {

        getCreadenticalApiClient();
    }

    private void getCreadenticalApiClient() {
        mCredentialsApiClient = new GoogleApiClient.Builder(getBaseContext())
                .addConnectionCallbacks(this)
                .enableAutoManage(MainActivity.this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        showHint();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void showHint() {
        HintRequest hintRequest = new HintRequest.Builder()
                .setHintPickerConfig(new CredentialPickerConfig.Builder()
                        .setShowCancelButton(true)
                        .build())
                .setPhoneNumberIdentifierSupported(true)
                .build();

        PendingIntent intent =
                Auth.CredentialsApi.getHintPickerIntent(mCredentialsApiClient, hintRequest);
        try {
            startIntentSenderForResult(intent.getIntentSender(), RC_HINT, null, 0, 0, 0, new Bundle());
        } catch (IntentSender.SendIntentException e) {
            // Log.e("Login", "Could not start hint picker Intent", e);
        }
    }




/*

    public void sendOnChannel1(View v) {

        String title = editTextTitle.getText().toString();
        String message = editTextMessage.getText().toString();

        Intent activityIntent = new Intent(this, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, activityIntent, 0);


        Intent broadcastIntent = new Intent(this, .class);
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
