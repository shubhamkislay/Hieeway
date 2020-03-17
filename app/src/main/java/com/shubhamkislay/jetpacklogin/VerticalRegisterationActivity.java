package com.shubhamkislay.jetpacklogin;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VerticalRegisterationActivity extends AppCompatActivity {


    FrameLayout frameLayout;
    List<Fragment> fragmentList;
    RegisterEmailEntryFragment registerEmailEntryFragment;
    RegisterAuthenticateActivity registerAuthenticateActivity;
    RegisterUsernameEntryFragment registerUsernameEntryFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_registeration);


        frameLayout = findViewById(R.id.framelayout);



        registerAuthenticateActivity = new RegisterAuthenticateActivity();
        registerEmailEntryFragment = new RegisterEmailEntryFragment();
        registerUsernameEntryFragment = new RegisterUsernameEntryFragment();
        fragmentList = new ArrayList<>();

        fragmentList.add(registerAuthenticateActivity);
        fragmentList.add(registerEmailEntryFragment);
        fragmentList.add(registerUsernameEntryFragment);


        getSupportFragmentManager().beginTransaction()
              //  .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_left_to_right)
                .replace(R.id.container_layout, registerEmailEntryFragment).commit();







    }
}
