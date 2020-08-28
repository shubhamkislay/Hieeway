package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.hieeway.hieeway.Helper.SpotifyRemoteHelper;
import com.hieeway.hieeway.Model.Pal;
import com.hieeway.hieeway.Model.User;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String DEVICE_TOKEN = "devicetoken";
    public static final String MUSIC_BEACON = "musicbeacon";
    public static final String SPOTIFY_CONNECT = "spotifyconnect";
    public static final String VISIBILITY = "visibility";
    private static final String PHONE = "phone";
    private TextView settings_title;
    private TextView music_beacon_title;
    private TextView spotify_title;
    private TextView visibility_status_title;
    private TextView change_phone_title;
    private TextView suggestion_title;
    private TextView delete_account_title;
    private TextView policy_title;
    private TextView master_head;
    private TextView logout_title;
    private Boolean visibility = false;
    private Boolean spotifyconnect = false;
    private Boolean musicbeacon = false;
    private Switch music_beacon_switch;
    private Switch spotify_switch;
    private Switch visible_switch;
    private String phonenumber;
    private String storePublicKeyId;
    private RelativeLayout phone_btn;
    private RelativeLayout logout_lay;
    private RelativeLayout privacy_Policy;


    @Override
    protected void onResume() {
        super.onResume();
        userStatusOnDiconnect();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (!sharedPreferences.getString(PHONE, "default").equals("default")) {

            change_phone_title.setText("" + sharedPreferences.getString(PHONE, "default"));
            visible_switch.setAlpha(1.0f);
            if (visibility)
                visible_switch.setChecked(true);
        } else {
            change_phone_title.setText("phone number");
            visible_switch.setChecked(false);
            visible_switch.setAlpha(0.15f);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_list);


        settings_title = findViewById(R.id.settings_title);
        music_beacon_title = findViewById(R.id.music_beacon_title);
        spotify_title = findViewById(R.id.spotify_title);
        visibility_status_title = findViewById(R.id.visibility_status_title);
        change_phone_title = findViewById(R.id.change_phone_title);
        suggestion_title = findViewById(R.id.suggestion_title);
        delete_account_title = findViewById(R.id.delete_account_title);
        policy_title = findViewById(R.id.policy_title);
        master_head = findViewById(R.id.master_head);
        logout_title = findViewById(R.id.logout_title);
        music_beacon_switch = findViewById(R.id.music_beacon_switch);
        spotify_switch = findViewById(R.id.spotify_switch);
        visible_switch = findViewById(R.id.visible_switch);
        phone_btn = findViewById(R.id.phone_btn);
        logout_lay = findViewById(R.id.logout_lay);
        privacy_Policy = findViewById(R.id.privacy_Policy);


        settings_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        music_beacon_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        spotify_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        visibility_status_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        change_phone_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        suggestion_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        delete_account_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        policy_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        master_head.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        logout_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        visibility = sharedPreferences.getBoolean(VISIBILITY, false);
        musicbeacon = sharedPreferences.getBoolean(MUSIC_BEACON, false);
        spotifyconnect = sharedPreferences.getBoolean(SPOTIFY_CONNECT, false);
        phonenumber = sharedPreferences.getString(PHONE, "default");


        if (musicbeacon)
            music_beacon_switch.setChecked(true);

        if (spotifyconnect)
            spotify_switch.setChecked(true);
        else {
            music_beacon_switch.setAlpha(0.15f);
        }


        phone_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, PhoneAuthenticationActivity.class));
            }
        });


        logout_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                storePublicKeyId = sharedPreferences.getString(PUBLIC_KEY_ID, "default");


                FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .runTransaction(new Transaction.Handler() {
                            @NonNull
                            @Override
                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {

                                User user = mutableData.getValue(User.class);


                                if (user.getPublicKeyId().equals(storePublicKeyId)) {
                                    user.setPublicKeyId("default");
                                    user.setToken("default");
                                    user.setPublicKey("default");
                                    mutableData.setValue(user);
                                    return Transaction.success(mutableData);
                                }


                                return null;
                            }

                            @Override
                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                FirebaseAuth.getInstance().signOut();

                                SharedPreferences preferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.clear();
                                editor.apply();
                                // finish();

                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);

                            }
                        });




                //finish();


            }
        });

        privacy_Policy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://hieeway.blogspot.com/";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        visible_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!phonenumber.equals("default")) {
                    visibility = isChecked;
                    editor.putBoolean(VISIBILITY, isChecked);
                    editor.apply();


                    HashMap<String, Object> vHash = new HashMap<>();
                    vHash.put("cloud", isChecked);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .updateChildren(vHash);
                } else {
                    visible_switch.setChecked(false);
                    Toast.makeText(SettingsActivity.this, "Add your phone number to activate the feature", Toast.LENGTH_SHORT).show();
                }


            }
        });

        spotify_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                spotifyconnect = isChecked;

                if (isChecked) {

                    music_beacon_switch.setAlpha(1.0f);
                    music_beacon_switch.setChecked(true);
                    editor.putBoolean(MUSIC_BEACON, isChecked);
                    editor.putBoolean(SPOTIFY_CONNECT, isChecked);
                    editor.apply();
                } else {

                    music_beacon_switch.setAlpha(0.15f);
                    music_beacon_switch.setChecked(false);
                    editor.putBoolean(MUSIC_BEACON, isChecked);
                    editor.putBoolean(SPOTIFY_CONNECT, isChecked);
                    editor.apply();

                    try {
                        SpotifyAppRemote.CONNECTOR.disconnect(SpotifyRemoteHelper.getInstance().getSpotifyAppRemote());
                    } catch (Exception e) {

                    }
                    SpotifyRemoteHelper.getInstance().setSpotifyAppRemote(null);

                    FirebaseDatabase.getInstance().getReference("Music")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .removeValue();
                }

            }
        });

        music_beacon_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                musicbeacon = isChecked;

                if (isChecked) {
                    if (!spotifyconnect) {
                        music_beacon_switch.setChecked(false);
                        Toast.makeText(SettingsActivity.this, "Turn on spotify connection", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent1 = new Intent(SettingsActivity.this, MusicBeamService.class);
                        startService(intent1);
                        editor.putBoolean(MUSIC_BEACON, isChecked);
                        editor.apply();
                    }
                } else {
                    editor.putBoolean(MUSIC_BEACON, isChecked);
                    editor.apply();
                }

            }
        });


    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }
}
