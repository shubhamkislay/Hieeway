package com.shubhamkislay.jetpacklogin;

import android.app.ProgressDialog;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.shubhamkislay.jetpacklogin.Model.User;


public class HomeActivity extends AppCompatActivity {
    TextView username, emailAddress;
    FirebaseUserNameViewModel viewModel;
    ProgressDialog pg;
    Button logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //hook layout and activity
        username = findViewById(R.id.username);
        emailAddress = findViewById(R.id.emailaddress);
        logout = findViewById(R.id.logout);
        pg = new ProgressDialog(HomeActivity.this);
        pg.setTitle("Loading user info");
        pg.show();


        viewModel =  ViewModelProviders.of(this).get(FirebaseUserNameViewModel.class);
        viewModel.getUsername().observe(this, new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if(user!=null) {
                    username.setText(user.getUsername());
                    emailAddress.setText(user.getEmail());
                    pg.dismiss();
                }
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(HomeActivity.this,MainActivity.class));
                finish();
            }
        });

    }

}
