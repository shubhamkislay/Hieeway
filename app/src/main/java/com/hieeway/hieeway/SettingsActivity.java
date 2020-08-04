package com.hieeway.hieeway;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

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
    private RelativeLayout phone_btn;

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        if (!sharedPreferences.getString(PHONE, "default").equals("default")) {

            change_phone_title.setText("" + sharedPreferences.getString(PHONE, "default") + "\nChange your phone number");
            visible_switch.setAlpha(1.0f);
            if (visibility)
                visible_switch.setChecked(true);
        } else {
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
                    editor.putBoolean(SPOTIFY_CONNECT, isChecked);
                    editor.apply();
                } else {

                    music_beacon_switch.setAlpha(0.15f);
                    music_beacon_switch.setChecked(false);
                    editor.putBoolean(MUSIC_BEACON, isChecked);
                    editor.putBoolean(SPOTIFY_CONNECT, isChecked);
                    editor.apply();

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
}
