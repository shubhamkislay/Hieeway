package com.shubhamkislay.jetpacklogin;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Adapters.PeopleAdapter;
import com.shubhamkislay.jetpacklogin.Model.Friend;
import com.shubhamkislay.jetpacklogin.Model.User;

import java.util.ArrayList;
import java.util.List;

public class RequestTrackerActivity extends AppCompatActivity {

    private PeopleAdapter peopleAdapter;
    private RecyclerView userlist_recyclerview;
    private List<User> userList;
    private TextView requests_title;
    ProgressBar progressBar,progressBarTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_tracker);


        userlist_recyclerview = findViewById(R.id.userlist_recyclerview);

        userList = new ArrayList<>();

        requests_title= findViewById(R.id.requests_title);

        progressBar = findViewById(R.id.progress);
        progressBarTwo = findViewById(R.id.progressTwo);

        progressBar.setVisibility(View.VISIBLE);
        progressBarTwo.setVisibility(View.VISIBLE);


        userlist_recyclerview.setHasFixedSize(true);
        userlist_recyclerview.setLayoutManager(new LinearLayoutManager(RequestTrackerActivity.this));

        peopleAdapter = new PeopleAdapter(this,userList,RequestTrackerActivity.this);

        userlist_recyclerview.setAdapter(peopleAdapter);

        try {
            requests_title.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        }catch (Exception e)
        {
            //
        }



        populateWithFriends();


    }

    private void populateWithFriends() {


        userList.clear();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                progressBar.setVisibility(View.GONE);
                progressBarTwo.setVisibility(View.GONE);

                userList.clear();
                if(dataSnapshot.exists())
                {
                    userList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
                        Friend friend = snapshot.getValue(Friend.class);
                        try {
                            if (!friend.getStatus().equals("friends")) {

                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(friend.getFriendId());

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        User user = dataSnapshot.getValue(User.class);
                                        userList.add(user);
                                        progressBar.setVisibility(View.GONE);
                                        progressBarTwo.setVisibility(View.GONE);
                                        peopleAdapter.notifyDataSetChanged();

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                            }
                        }catch (Exception e)
                        {
                            //
                            Toast.makeText(RequestTrackerActivity.this,"Can't fetch friends",Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                            progressBarTwo.setVisibility(View.GONE);
                        }

                    }

                }
                else {
                    Toast.makeText(RequestTrackerActivity.this,"No friends",Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    progressBarTwo.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
