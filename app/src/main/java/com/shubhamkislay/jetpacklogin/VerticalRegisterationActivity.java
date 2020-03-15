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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vertical_registeration);


        frameLayout = findViewById(R.id.framelayout);
        fragmentList = new ArrayList<>();




    }
}
