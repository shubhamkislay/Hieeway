package com.shubhamkislay.jetpacklogin;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.shubhamkislay.jetpacklogin.Fragments.RegisterEmailEntryFragment;
import com.shubhamkislay.jetpacklogin.Fragments.RegisterUsernameEntryFragment;

import java.util.ArrayList;
import java.util.List;

public class VerticalRegisterationActivity extends AppCompatActivity {


    FrameLayout frameLayout;
    List<Fragment> fragmentList;
    RegisterEmailEntryFragment registerEmailEntryFragment;
    RegisterAuthenticateActivity registerAuthenticateActivity;
    RegisterUsernameEntryFragment registerUsernameEntryFragment;
    Button change_frag_btn;
    int fragment_number=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_registeration);


        frameLayout = findViewById(R.id.framelayout);

        change_frag_btn = findViewById(R.id.change_frag_btn);



        registerAuthenticateActivity = new RegisterAuthenticateActivity();
        registerEmailEntryFragment = new RegisterEmailEntryFragment();
        registerUsernameEntryFragment = new RegisterUsernameEntryFragment();
        fragmentList = new ArrayList<>();

        fragmentList.add(registerAuthenticateActivity);
        fragmentList.add(registerEmailEntryFragment);
        fragmentList.add(registerUsernameEntryFragment);


        getSupportFragmentManager().beginTransaction()
              //  .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_left_to_right)
                .replace(R.id.framelayout, registerEmailEntryFragment).commit();

        change_frag_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (fragment_number)
                {
                    case 1: fragment_number =2;
                        getSupportFragmentManager().beginTransaction()
                                  .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerUsernameEntryFragment).commit();
                        break;

                    case 2: fragment_number =3;
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_bottom_to_top, R.anim.exit_bottom_to_top)
                                .replace(R.id.framelayout, registerAuthenticateActivity).commit();
                        break;

                    case 3: fragment_number =1;
                        getSupportFragmentManager().beginTransaction()
                                .setCustomAnimations(R.anim.enter_top_to_bottom, R.anim.exit_top_to_bottom)
                                .replace(R.id.framelayout, registerEmailEntryFragment).commit();
                        break;



                }
            }
        });





    }
}
