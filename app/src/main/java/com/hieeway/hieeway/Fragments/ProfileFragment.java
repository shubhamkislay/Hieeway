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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.hieeway.hieeway.FeelingDialog;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Interface.AddFeelingFragmentListener;
import com.hieeway.hieeway.Interface.ChangePictureListener;
import com.hieeway.hieeway.Interface.EditBioFragmentListener;
import com.hieeway.hieeway.Interface.EditProfileOptionListener;
import com.hieeway.hieeway.Interface.FeelingListener;
import com.hieeway.hieeway.Interface.ImageSelectionCropListener;
import com.hieeway.hieeway.MainActivity;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.MusicBeamService;
import com.hieeway.hieeway.PictureChangeDialog;
import com.hieeway.hieeway.ProfilePhotoActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SendMediaService;
import com.hieeway.hieeway.SettingsActivity;
import com.hieeway.hieeway.SharedViewModel;
import com.hieeway.hieeway.SpotifyActivity;
import com.hieeway.hieeway.ViewProfileActivity;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.client.CallResult;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Artist;
import com.spotify.protocol.types.ImageUri;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static android.content.Context.MODE_PRIVATE;
import static com.hieeway.hieeway.NavButtonTest.USER_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_NAME;
import static com.hieeway.hieeway.NavButtonTest.USER_PHOTO;
import static com.hieeway.hieeway.NavButtonTest.CURR_USER_ID;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.view.WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;

public class ProfileFragment extends Fragment implements FeelingListener, EditProfileOptionListener, ChangePictureListener {

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
    RelativeLayout edit_profile_pic, edit_active_pic;
    RelativeLayout edit_bio;
    Boolean bottomSheetDialogVisible = false;
    TextView bottom_dialog_title, prof_txt, bio_txt_dialog;
    RelativeLayout bottom_sheet_dialog_layout;
    RelativeLayout relay;
    private String profilepic;
    public static final String SPOTIFY_TOKEN = "spotify_token";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MUSIC_BEACON = "musicbeacon";
    public static final String SPOTIFY_CONNECT = "spotifyconnect";
    public static final String VISIBILITY = "visibility";
    Activity activity;
    ChangePictureListener changePictureListener;

    ProgressBar upload_active_progress;
    ImageView track_cover;
    ImageView spotify_cover;

    Point size;
    float displayHeight;
    final String appPackageName = "com.spotify.music";

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

    TextView song_name, artist_name;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean spotifyConnected = false;
    private String songId;
    final String referrer = "adjust_campaign=com.hieeway.hieeway&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
    float displayWidth;
    RelativeLayout connect_spotify;
    ImageView spotify_icon;
    RelativeLayout music_layout, music_loading_layout;
    private TextView music_loading_txt;
    private RelativeLayout connect_spotify_layout;
    private TextView connect_spotify_text;
    private Button edit_settings_option_btn;
    private Boolean visibility = false;
    private Boolean spotifyconnect = false;
    private Boolean musicbeacon = false;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private String activePhoto;

    public ProfileFragment() {

    }

    public ProfileFragment(Activity activity) {
        this.changePictureListener = (ChangePictureListener) activity;
        this.activity = activity;
    }



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

    @Override
    public void onResume() {
        super.onResume();

        sharedPreferences = activity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        editor = sharedPreferences.edit();

        visibility = sharedPreferences.getBoolean(VISIBILITY, false);
        musicbeacon = sharedPreferences.getBoolean(MUSIC_BEACON, false);
        spotifyconnect = sharedPreferences.getBoolean(SPOTIFY_CONNECT, false);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (sharedPreferences.getBoolean(SPOTIFY_CONNECT, false)) {

                    ConnectionParams connectionParams =
                            new ConnectionParams.Builder(CLIENT_ID)
                                    .setRedirectUri(REDIRECT_URI)
                                    .setPreferredThumbnailImageSize(1500)
                                    .setPreferredImageSize(1500)
                                    .showAuthView(true)
                                    .build();

                    SpotifyAppRemote spotifyAppRemote = SpotifyRemoteHelper.getInstance().getSpotifyAppRemote();
                    if (spotifyAppRemote == null) {
                /*try {
                    SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                } catch (Exception e) {
                    //
                }*/

                        SpotifyAppRemote.CONNECTOR.connect(getActivity(), connectionParams,
                                new Connector.ConnectionListener() {

                                    @Override
                                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                                        mSpotifyAppRemote = spotifyAppRemote;


                                        SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(mSpotifyAppRemote);

                                        connect_spotify.setVisibility(View.GONE);
                                        connect_spotify_layout.setVisibility(View.GONE);
                                        spotify_icon.setVisibility(View.VISIBLE);
                                        music_layout.setVisibility(View.VISIBLE);
                                        music_loading_layout.setVisibility(View.GONE);
                                        // Toast.makeText(getActivity(),"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                                        // Now you can start interacting with App Remote
                                        spotifyConnected = true;
                                        listedToSpotifySong();
                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                        Log.e("MainActivity", throwable.getMessage(), throwable);
                                        connect_spotify.setVisibility(View.VISIBLE);
                                        connect_spotify_layout.setVisibility(View.VISIBLE);
                                        spotify_icon.setVisibility(View.GONE);
                                        music_loading_layout.setVisibility(View.GONE);

                                        song_name.setVisibility(View.GONE);
                                        artist_name.setVisibility(View.GONE);
                                        spotify_cover.setVisibility(View.GONE);

                                        music_layout.setVisibility(View.VISIBLE);
                                        //song_name.setText("Connect to spotify");

                                        spotifyConnected = false;
                                        // Toast.makeText(getActivity(), "Cannot connect to spotify automtically :(" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        // Something went wrong when attempting to connect! Handle errors here
                                    }
                                });
                    } else {
                        mSpotifyAppRemote = spotifyAppRemote;
                        connect_spotify.setVisibility(View.GONE);
                        connect_spotify_layout.setVisibility(View.GONE);
                        spotify_icon.setVisibility(View.VISIBLE);
                        music_layout.setVisibility(View.VISIBLE);
                        music_loading_layout.setVisibility(View.GONE);
                        // Toast.makeText(getActivity(),"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                        // Now you can start interacting with App Remote
                        spotifyConnected = true;
                        listedToSpotifySong();

                    }


                } else {
                    try {
                        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                    } catch (Exception e) {
                        //
                    }

                    connect_spotify.setVisibility(View.VISIBLE);
                    connect_spotify_layout.setVisibility(View.VISIBLE);
                    spotify_icon.setVisibility(View.GONE);
                    music_loading_layout.setVisibility(View.GONE);

                    song_name.setVisibility(View.GONE);
                    artist_name.setVisibility(View.GONE);
                    spotify_cover.setVisibility(View.GONE);


                    music_layout.setVisibility(View.VISIBLE);

                    //song_name.setText("Connect to spotify");

                    spotifyConnected = false;
                }

        /*Intent intent1 = new Intent(getActivity(), MusicBeamService.class);
        activity.startService(intent1);*/
            }
        }, 500);






    }



    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        displayHeight = size.y;
        displayWidth = size.x;

        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        music_layout = view.findViewById(R.id.music_layout);


        upload_active_progress = view.findViewById(R.id.upload_active_progress);







// clear FLAG_TRANSLUCENT_STATUS flag:
        try {
            window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorBlack));
        } catch (Exception e) {
            //
        }

        edit_profile_pic = view.findViewById(R.id.edit_profile_pic);
        edit_active_pic = view.findViewById(R.id.edit_active_pic);
        edit_bio = view.findViewById(R.id.edit_bio);
        edit_settings_option_btn = view.findViewById(R.id.edit_settings_option_btn);

        bottom_dialog_title = view.findViewById(R.id.title);
        prof_txt = view.findViewById(R.id.profile_txt);
        bio_txt_dialog = view.findViewById(R.id.bio_txtview);

        username = view.findViewById(R.id.username);
        profile_pic_background = view.findViewById(R.id.profile_pic_background);
        center_dp = view.findViewById(R.id.center_dp);
        spotify_cover = view.findViewById(R.id.spotify_cover);
        artist_name = view.findViewById(R.id.artist_name);
        song_name = view.findViewById(R.id.song_name);
        music_loading_layout = view.findViewById(R.id.music_loading_layout);
        connect_spotify_layout = view.findViewById(R.id.connect_spotify_layout);
        connect_spotify_text = view.findViewById(R.id.connect_spotify_text);


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
        connect_spotify = view.findViewById(R.id.connect_spotify);
        spotify_icon = view.findViewById(R.id.spotify_icon);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_dialog_layout);

        activity.getWindow().setSoftInputMode(SOFT_INPUT_ADJUST_PAN);


        upload_progress = view.findViewById(R.id.upload_progress);
        relay = view.findViewById(R.id.relay);

        music_loading_txt = view.findViewById(R.id.music_loading_txt);





        emoji_holder_layout = view.findViewById(R.id.emoji_holder_layout);

        ring_blinker_layout = view.findViewById(R.id.ring_blinker_layout);

        emoji_icon = view.findViewById(R.id.emoji_icon);

        center_dp.getLayoutParams().height = (int) displayHeight / 4;
        center_dp.getLayoutParams().width = (int) displayHeight / 8;


        connect_spotify_text.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        music_loading_txt.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        name.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feeling_txt.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        username.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-medium.otf"));
        //change_nio_edittext.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bio_txt.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        //bio_txt_dialog.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        //prof_txt.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        bottom_dialog_title.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        song_name.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        artist_name.setTypeface(Typeface.createFromAsset(activity.getAssets(), "fonts/samsungsharpsans-medium.otf"));

        uploadActivityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imageSelectionCropListener.imageSelect(false);
            }
        });
        song_name.setSelected(true);
        artist_name.setSelected(true);


        edit_settings_option_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.startActivity(new Intent(getActivity(), SettingsActivity.class));
            }
        });


        // spotify_cover.getLayoutParams().height = (int) displayWidth;

        connect_spotify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                editor.putBoolean(MUSIC_BEACON, true);
                editor.putBoolean(SPOTIFY_CONNECT, true);
                editor.apply();


                PackageManager pm = null;
                try {
                    pm = activity.getPackageManager();

                    boolean isSpotifyInstalled;


                    try {
                        pm.getPackageInfo("com.spotify.music", 0);
                        isSpotifyInstalled = true;
                        // Toast.makeText(getActivity(), "Log in to Spotify", Toast.LENGTH_SHORT).show();
                        AuthenticationRequest.Builder builder =
                                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                        builder.setScopes(new String[]{"streaming"});
                        AuthenticationRequest request = builder.build();

                        AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
                    } catch (PackageManager.NameNotFoundException e) {
                        isSpotifyInstalled = false;

                        Toast.makeText(getActivity(), "Install spotify app to continue", Toast.LENGTH_LONG).show();

                        try {
                            Uri uri = Uri.parse("market://details")
                                    .buildUpon()
                                    .appendQueryParameter("id", appPackageName)
                                    .appendQueryParameter("referrer", referrer)
                                    .build();
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        } catch (android.content.ActivityNotFoundException ignored) {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details")
                                    .buildUpon()
                                    .appendQueryParameter("id", appPackageName)
                                    .appendQueryParameter("referrer", referrer)
                                    .build();
                            startActivity(new Intent(Intent.ACTION_VIEW, uri));
                        }

                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "Cannot fetch package manager", Toast.LENGTH_SHORT).show();
                }
            }
        });

        spotify_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    // mSpotifyAppRemote.getPlayerApi().play(songId);
                    /*Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                    if (launchIntent != null) {
                        startActivity(launchIntent);//null pointer check in case package name was not found
                    }*/

                    if (songId != null) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("" + songId));
                        intent.putExtra(Intent.EXTRA_REFERRER,
                                Uri.parse("android-app://" + getContext().getPackageName()));
                        getContext().startActivity(intent);
                    } else {
                        Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                    }
                } catch (Exception e) {
                    //
                }
            }
        });


        spotify_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (songId != null) {

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("" + songId));
                        intent.putExtra(Intent.EXTRA_REFERRER,
                                Uri.parse("android-app://" + getContext().getPackageName()));
                        getContext().startActivity(intent);
                    } else {
                        Intent launchIntent = activity.getPackageManager().getLaunchIntentForPackage("com.spotify.music");
                        if (launchIntent != null) {
                            startActivity(launchIntent);//null pointer check in case package name was not found
                        }
                    }
                } catch (Exception e) {

                }
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
                            activity.finish();
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
                    /*Intent intent = new Intent(getActivity(), ProfilePhotoActivity.class);
                    intent.putExtra("profilepic", profilepic);

                    activity.startActivity(intent);*/

                    PictureChangeDialog pictureChangeDialog = new PictureChangeDialog(getActivity(), false, profilepic, ProfileFragment.this, "photo");
                    pictureChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    pictureChangeDialog.setCancelable(true);
                    pictureChangeDialog.show();

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
                bio_txt.setTextColor(activity.getResources().getColor(R.color.darkGrey));
            } else {
                bio_txt.setText(bio);
                stripUnderlines(bio_txt);
                bio_txt.setTextColor(activity.getResources().getColor(R.color.colorWhite));
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
                PictureChangeDialog pictureChangeDialog = new PictureChangeDialog(getActivity(), false, profilepic, ProfileFragment.this, "photo");
                pictureChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pictureChangeDialog.setCancelable(true);

                pictureChangeDialog.show();
                //imageSelectionCropListener.imageSelect(false);
                bottomSheetDialogVisible = false;
            }
        });

        edit_active_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relay.setVisibility(View.GONE);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                //imageSelectionCropListener.imageSelect(true);
                PictureChangeDialog pictureChangeDialog = new PictureChangeDialog(getActivity(), true, activePhoto, ProfileFragment.this, "activePhoto");
                pictureChangeDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                pictureChangeDialog.setCancelable(true);
                pictureChangeDialog.show();
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

        TaskCompletionSource<Bitmap> bitmapTaskCompletionSource = new TaskCompletionSource<>();


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Bitmap bitmap = Glide.with(getActivity())
                            .asBitmap()
                            .load(userPhoto)
                            .submit(5, 5)
                            .get();
                    //setPaletteColor();
                    bitmapTaskCompletionSource.setResult(bitmap);

                } catch (Exception e) {
                    //
                }

            }
        }).start();


        Task<Bitmap> bitmapTask = bitmapTaskCompletionSource.getTask();
        bitmapTask.addOnCompleteListener(new OnCompleteListener<Bitmap>() {
            @Override
            public void onComplete(@NonNull Task<Bitmap> task) {
                if (task.isSuccessful())
                    bitmap = task.getResult();


            }
        });


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

    public void onFragmentActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response

                    editor.putString(SPOTIFY_TOKEN, response.getAccessToken());
                    editor.apply();
                    connect_spotify.setVisibility(View.GONE);
                    connect_spotify_layout.setVisibility(View.GONE);

                    spotify_icon.setVisibility(View.VISIBLE);
                    music_layout.setVisibility(View.VISIBLE);
                    music_loading_layout.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Connected to spotify :D", Toast.LENGTH_SHORT).show();
                    spotifyConnected = true;
                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response

                    connect_spotify.setVisibility(View.VISIBLE);
                    connect_spotify_layout.setVisibility(View.VISIBLE);
                    music_layout.setVisibility(View.VISIBLE);
                    spotify_icon.setVisibility(View.GONE);
                    spotifyConnected = false;
                    music_loading_layout.setVisibility(View.GONE);
                    //song_name.setText("Connect to spotify");
                    Toast.makeText(getActivity(), "Cannot connect to spotify :(" + response.getError(), Toast.LENGTH_SHORT).show();
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    private Animation outToLeftAnimation() {
        Animation outtoLeft = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, -1.0f, Animation.RELATIVE_TO_PARENT, 0.0f, Animation.RELATIVE_TO_PARENT, 0.0f);
        outtoLeft.setDuration(500);
        outtoLeft.setInterpolator(new AccelerateInterpolator());
        return outtoLeft;
    }

    private void listedToSpotifySong() {
        //    mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");


        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(new Subscription.EventCallback<PlayerState>() {
                    @Override
                    public void onEvent(PlayerState playerState) {


                        if (playerState != null) {
                            final Track track = playerState.track;
                            songId = track.uri;

                            //song_name.setAnimation(outToLeftAnimation());


                            if (track.artist.name.length() > 1) {

                                String artistNames = null;

                                List<Artist> artists = track.artists;
                                for (Artist artist : artists) {
                                    if (artistNames != null)
                                        artistNames = artistNames + artist.name + ", ";
                                    else
                                        artistNames = artist.name + ", ";
                                }
                                artistNames = artistNames.substring(0, artistNames.length() - 2);


                                /*HashMap<String, Object> songHash = new HashMap<>();
                                songHash.put("spotifyId", songId);
                                songHash.put("spotifySong", track.name);
                                songHash.put("spotifyArtist", artistNames);
                                songHash.put("spotifyCover", track.imageUri);*/

                                //song_name.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));

                                /*FirebaseDatabase.getInstance()
                                        .getReference("Music")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .updateChildren(songHash);*/

                                Log.d("Spotify Activity", track.name + " by " + track.artist.name);
                                // Toast.makeText(SpotifyActivity.this,"You are playing "+track.name,Toast.LENGTH_SHORT).show();
                                //  Toast.makeText(getActivity(),"Track is "+track.name,Toast.LENGTH_SHORT).show();
                                spotify_icon.setVisibility(View.VISIBLE);
                                spotify_cover.setVisibility(View.INVISIBLE);
                                song_name.setText(track.name);
                                artist_name.setText(artistNames);


                                mSpotifyAppRemote.getImagesApi().getImage(track.imageUri)
                                        .setResultCallback(new CallResult.ResultCallback<Bitmap>() {
                                            @Override
                                            public void onResult(Bitmap bitmap) {

                                                spotify_icon.setVisibility(View.INVISIBLE);
                                                spotify_cover.setVisibility(View.VISIBLE);
                                                spotify_cover.setImageBitmap(bitmap);


                                                //updateMusicStatus(track.name, track.artist.name, track.imageUri);
                                            }
                                        });


                                //Glide.with(SpotifyActivity.this).load(track.imageUri).into(track_cover);
                            } else {
                                // Toast.makeText(getActivity(),"Track is null",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // Toast.makeText(getActivity(),"PlayerState is null",Toast.LENGTH_SHORT).show();
                        }

                    }


                });


    }

    private void updateMusicStatus(String track_name, String postArtist, ImageUri track_imageUri) {

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        FirebaseDatabase.getInstance().getReference("Music")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                        String musicKey = FirebaseDatabase.getInstance().getReference("Music")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).push().getKey();

                        Music music = mutableData.getValue(Music.class);

                        try {
                            if (music.getSpotifyId() == null || !music.getSpotifyId().equals(songId)) {
                                HashMap<String, Object> songHash = new HashMap<>();
                                songHash.put("spotifyId", songId);
                                songHash.put("spotifySong", track_name);
                                songHash.put("spotifyArtist", postArtist);
                                songHash.put("spotifyCover", track_imageUri);
                                songHash.put("username", USER_NAME);
                                songHash.put("userId", USER_ID);
                                songHash.put("userPhoto", USER_PHOTO);
                                songHash.put("musicKey", musicKey);
                                songHash.put("timestamp", timestamp.toString());

                                //song_name.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));

                                FirebaseDatabase.getInstance().getReference("Music")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                FirebaseDatabase.getInstance().getReference("Likes")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .removeValue();

                                                FirebaseDatabase.getInstance()
                                                        .getReference("Music")
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .updateChildren(songHash);

                                            }
                                        });

                            }


                        } catch (NullPointerException e) {

                            HashMap<String, Object> songHash = new HashMap<>();
                            songHash.put("spotifyId", songId);
                            songHash.put("spotifySong", track_name);
                            songHash.put("spotifyArtist", postArtist);
                            songHash.put("spotifyCover", track_imageUri);
                            songHash.put("username", USER_NAME);
                            songHash.put("userId", USER_ID);
                            songHash.put("userPhoto", USER_PHOTO);
                            songHash.put("musicKey", musicKey);
                            songHash.put("timestamp", timestamp.toString());

                            //song_name.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));

                            FirebaseDatabase.getInstance().getReference("Music")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            FirebaseDatabase.getInstance()
                                                    .getReference("Music")
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .updateChildren(songHash);

                                        }
                                    });

                        }

                        return null;
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                    }
                });
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

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);

                sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(@Nullable User user) {


                        USER_PHOTO = user.getPhoto();

                        activePhoto = user.getActivePhoto();


                        CURR_USER_ID = user.getUserid();
                        USER_NAME = user.getUsername();
                        username.setText(user.getUsername());
                        name.setText(user.getName());


                        try {
                            bio = user.getBio();
                            // change_nio_edittext.setText(user.getBio());
                            bio_txt.setText(user.getBio());
                            stripUnderlines(bio_txt);
                            bio_txt.setTextColor(activity.getResources().getColor(R.color.colorWhite));


                        } catch (Exception e) {
                            //
                            bio_txt.setText("Tell something about yourself");
                            bio_txt.setTextColor(activity.getResources().getColor(R.color.darkGrey));
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
                                        feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                                        feeling_txt.setText(HAPPY);
                                        break;
                                    case SAD:
                                        feeling_icon.setVisibility(View.VISIBLE);
                                        feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                                        feeling_txt.setText(SAD);
                                        break;
                                    case BORED:
                                        feeling_icon.setVisibility(View.VISIBLE);
                                        feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                                        feeling_txt.setText(BORED);
                                        break;
                                    case ANGRY:
                                        feeling_icon.setVisibility(View.VISIBLE);
                                        feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                                        feeling_txt.setText(ANGRY);
                                        break;
                                    case "excited":
                                        feeling_icon.setVisibility(View.VISIBLE);
                                        feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                                        feeling_txt.setText(EXCITED);
                                        break;
                                    case CONFUSED:
                                        feeling_icon.setVisibility(View.VISIBLE);
                                        feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                                        feeling_txt.setText(CONFUSED);
                                        break;
                                }
                            }

                        } catch (Exception e) {
                            //
                        }

                        try {

                            USER_PHOTO = user.getPhoto();

                            profilepic = user.getPhoto().replace("s96-c", "s384-c");
                            userPhoto = profilepic;
                            if (!activePhoto.equals("default"))
                                try {
                                    Glide.with(getContext()).load(user.getActivePhoto().replace("s96-c", "s384-c")).into(profile_pic_background);
                                } catch (Exception e) {

                                }
                            if (!userPhoto.equals("default"))
                                try {
                                    Glide.with(getContext()).load(user.getPhoto().replace("s96-c", "s384-c")).into(center_dp);
                                } catch (Exception e) {
                                    Glide.with(getContext()).load(activity.getDrawable(R.drawable.bottom_sheet_background_drawable)).into(center_dp);
                                }
                            else {
                                try {
                                    Glide.with(getContext()).load(activity.getDrawable(R.drawable.bottom_sheet_background_drawable)).into(center_dp);
                                } catch (Exception e) {

                                }
                            }

                            try {
                                bitmap = ((BitmapDrawable) profile_pic_background.getDrawable()).getBitmap();
                            } catch (Exception e) {

                            }


                            // setPaletteColor();
                        } catch (Exception e) {

                        }

                    }
                });


            }
        }, 350);


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
        /*top_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
        bottom_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));
        overlay_fade.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.profile_color_theme)));*/

        try {
            window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            // window.setStatusBarColor(getResources().getColor(R.color.profile_color_theme));
            window.setStatusBarColor(getResources().getColor(R.color.colorBlack));
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
                                window = activity.getWindow();
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
                            window = activity.getWindow();
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
                window = activity.getWindow();
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
                feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(HAPPY);
                break;
            case SAD:
                feelingHash.put("feeling", SAD);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(SAD);
                break;
            case BORED:
                feelingHash.put("feeling", BORED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(BORED);
                break;
            case ANGRY:
                feelingHash.put("feeling", ANGRY);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(ANGRY);
                break;
            case "excited":
                feelingHash.put("feeling", "excited");
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(getContext().getResources().getColor(R.color.colorPrimaryDark)));
                feeling_txt.setText(EXCITED);
                break;
            case CONFUSED:
                feelingHash.put("feeling", CONFUSED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(activity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
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


    public void setActiveProgressVisibility(int visibility) {
        upload_active_progress.setVisibility(visibility);
    }

    @Override
    public void editProfileOption(String option) {
        switch (option) {
            case "bio":
                editBioFragmentListener.setEditBioChange(false, bio_txt.getText().toString());
                break;

            case "pic":
                imageSelectionCropListener.imageSelect(false);
                break;

            case "active":
                imageSelectionCropListener.imageSelect(true);
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

    @Override
    public void changePicture(Boolean active) {

        imageSelectionCropListener.imageSelect(active);
    }

    @Override
    public void removedPicture(Boolean active) {

        if (active) {
            profile_pic_background.setImageDrawable(activity.getDrawable(R.color.colorBlack));
        } else {
            changePictureListener.removedPicture(active);
            center_dp.setImageDrawable(activity.getDrawable(R.drawable.no_profile));
        }
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

    @Override
    public void onPause() {
        super.onPause();
        /*try {
            SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
        } catch (Exception e) {
            //
        }*/
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }
}
