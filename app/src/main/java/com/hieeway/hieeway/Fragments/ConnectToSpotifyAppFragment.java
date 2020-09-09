package com.hieeway.hieeway.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Interface.ConnectToSpotifyListener;
import com.hieeway.hieeway.Interface.PhoneNumberListener;
import com.hieeway.hieeway.MainActivity;
import com.hieeway.hieeway.R;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ConnectToSpotifyAppFragment extends Fragment {
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String MUSIC_BEACON = "musicbeacon";
    public static final String SPOTIFY_CONNECT = "spotifyconnect";
    public static final String VISIBILITY = "visibility";
    private static final int REQUEST_CODE = 1337;
    private static final String REDIRECT_URI = "http://10.0.2.2:8888/callback";
    private static final String CLIENT_ID = "79c53faf8b67451b9adf996d40285521";
    final String appPackageName = "com.spotify.music";
    final String referrer = "adjust_campaign=com.hieeway.hieeway&adjust_tracker=ndjczk&utm_source=adjust_preinstall";
    TextView phone_title, notes, feature_title, feautures_list1, feautures_list2;
    Button add_number_btn, skip_btn;
    Activity activity;
    Boolean visibility;
    Boolean musicbeacon;
    Boolean spotifyconnect;
    RelativeLayout connect_spotify_layout;
    private SpotifyAppRemote mSpotifyAppRemote;
    private boolean spotifyConnected = false;
    private String songId;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private ConnectToSpotifyListener connectToSpotifyListener;


    public ConnectToSpotifyAppFragment(Activity activity, ConnectToSpotifyListener connectToSpotifyListener) {
        // Required empty public constructor
        this.activity = activity;
        this.connectToSpotifyListener = connectToSpotifyListener;
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
                    connect_spotify_layout.setVisibility(View.VISIBLE);
                    add_number_btn.setVisibility(View.GONE);

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


                                        // Toast.makeText(getActivity(),"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                                        // Now you can start interacting with App Remote
                                        spotifyConnected = true;


                                        /*startActivity(new Intent(activity, MainActivity.class));
                                        activity.finish();*/

                                        connectToSpotifyListener.initilizeAccount();


                                    }

                                    @Override
                                    public void onFailure(Throwable throwable) {
                                        Log.e("MainActivity", throwable.getMessage(), throwable);

                                        //song_name.setText("Connect to spotify");

                                        connect_spotify_layout.setVisibility(View.GONE);
                                        add_number_btn.setVisibility(View.VISIBLE);
                                        spotifyConnected = false;
                                        // Toast.makeText(getActivity(), "Cannot connect to spotify automtically :(" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                        // Something went wrong when attempting to connect! Handle errors here
                                    }
                                });
                    } else {
                        mSpotifyAppRemote = spotifyAppRemote;

                        // Toast.makeText(getActivity(),"Connected to spotify automtically :D",Toast.LENGTH_SHORT).show();

                        // Now you can start interacting with App Remote
                        spotifyConnected = true;


                    }


                } else {
                    try {
                        SpotifyAppRemote.CONNECTOR.disconnect(mSpotifyAppRemote);
                    } catch (Exception e) {
                        //
                    }


                    //song_name.setText("Connect to spotify");

                    spotifyConnected = false;
                }

        /*Intent intent1 = new Intent(getActivity(), MusicBeamService.class);
        activity.startService(intent1);*/
            }
        }, 500);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_spotify_connect, container, false);
        phone_title = view.findViewById(R.id.phone_title);
        notes = view.findViewById(R.id.notes);
        feature_title = view.findViewById(R.id.feature_title);
        feautures_list1 = view.findViewById(R.id.feautures_list1);
        feautures_list2 = view.findViewById(R.id.feautures_list2);
        connect_spotify_layout = view.findViewById(R.id.connect_spotify_layout);

        add_number_btn = view.findViewById(R.id.add_number_btn);
        skip_btn = view.findViewById(R.id.skip_btn);


        phone_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        // notes.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-medium.otf"));
        feature_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-medium.otf"));
        feautures_list1.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        feautures_list2.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


        add_number_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                PackageManager pm = null;
                try {
                    pm = getActivity().getPackageManager();

                    boolean isSpotifyInstalled;


                    try {
                        pm.getPackageInfo("com.spotify.music", 0);
                        isSpotifyInstalled = true;
                        // Toast.makeText(getActivity(), "Log in to Spotify", Toast.LENGTH_SHORT).show();
                        AuthenticationRequest.Builder builder =
                                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

                        builder.setScopes(new String[]{"streaming"});
                        AuthenticationRequest request = builder.build();

                        editor.putBoolean(MUSIC_BEACON, true);
                        editor.putBoolean(SPOTIFY_CONNECT, true);
                        editor.apply();

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
                    // Toast.makeText(getActivity(), "Cannot fetch package manager", Toast.LENGTH_SHORT).show();
                }
            }
        });

        skip_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectToSpotifyListener.initilizeAccount();
            }
        });


        return view;
    }

}
