package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.FriendsAdapter;
import com.hieeway.hieeway.Adapters.MusicPalAdapter;
import com.hieeway.hieeway.Adapters.PeopleAdapter;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MusicPalActivity extends AppCompatActivity {

    public String searchingUsername;
    TextView username, result_text;
    RecyclerView friend_list_recyclerview;
    Activity activity;
    MusicPalAdapter musicPalAdapter;
    EditText searchPeople;
    //ImageView //progress_menu_logo;
    ProgressBar progressBar, progressBarTwo; //progress_menu_logo_two;
    TextView logo_title;
    private List<User> userList;
    private ValueEventListener valueEventListener;
    private Query query;
    private List<User> userlist;
    private boolean friendAvailable = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_pal);

        userStatusOnDiconnect();
        username = findViewById(R.id.username);
        friend_list_recyclerview = findViewById(R.id.friend_list_recyclerview);
        userlist = new ArrayList<>();

        activity = this;

        userList = new ArrayList<>();

        progressBar = findViewById(R.id.progress);
        progressBarTwo = findViewById(R.id.progressTwo);

        searchPeople = findViewById(R.id.search_people);

        searchPeople.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchPeople.setRawInputType(InputType.TYPE_CLASS_TEXT);
        logo_title = findViewById(R.id.layout_title);


        Window window = this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.nav_status_color));

        result_text = findViewById(R.id.search_result_txt);
        result_text.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        //progress_menu_logo = findViewById(R.id.//progress_menu_logo);


        logo_title.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;


        int spanCount; // 3 columns
        int spacing = 0; // 50px
        boolean includeEdge = true;

        if (displayWidth >= 1920)
            spanCount = 4;

        else if (displayWidth >= 1080)
            spanCount = 3;

        else if (displayWidth >= 500)
            spanCount = 2;
        else
            spanCount = 1;


        LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);


        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        friend_list_recyclerview.setLayoutManager(gridLayoutManager);
        friend_list_recyclerview.setHasFixedSize(true);
        friend_list_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        friend_list_recyclerview.setItemViewCacheSize(20);
        friend_list_recyclerview.setDrawingCacheEnabled(true);
        friend_list_recyclerview.setItemAnimator(new DefaultItemAnimator());
        friend_list_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        searchPeople.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


                //userList.clear();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 0)
                    searchFriends(s.toString().toLowerCase());

                else {
                    /*userList.clear();
                    musicPalAdapter.notifyDataSetChanged();
                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("Add friends from search tab");*/

                    populateWithFriends();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateWithFriends();
                musicPalAdapter = new MusicPalAdapter(MusicPalActivity.this, userList, activity);
                friend_list_recyclerview.setAdapter(musicPalAdapter);

            }
        }, 400);

    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }

    private void searchFriends(final String username) {


        if (username.length() > 0) {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("username")
                    .startAt(username)
                    .endAt(username + "\uf8ff");
        } else {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // userList.clear();


                if (dataSnapshot.exists()) {
                    userList.clear();
                    result_text.setVisibility(View.GONE);

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Friend friend = snapshot.getValue(Friend.class);
                        try {
                            if (friend.getStatus().equals("friends")) {

                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(friend.getFriendId());

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {

                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        // userList.clear();
                                        User user = dataSnapshot.getValue(User.class);
                                        userList.add(user);
                                        try {
                                            musicPalAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {
                                            //
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                // musicPalAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            //
                            result_text.setVisibility(View.VISIBLE);
                            result_text.setText("Can't fetch friends");
                            // Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                        }

                    }

                    /*peopleAdapter.setList(userlist);*/
                    // if(username.length()>0)
                    //Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());

                    //  musicPalAdapter.notifyDataSetChanged();


                } else {
                    userList.clear();
                    musicPalAdapter.notifyDataSetChanged();
                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("No results found for '" + username + "'");
                    // Toast.makeText(getContext(),"Couldn't find user: "+username,Toast.LENGTH_SHORT).show();
                }

                // musicPalAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addValueEventListener(valueEventListener);


    }

    private void populateWithFriends() {


        result_text.setVisibility(View.GONE);

        userList.clear();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //userList.clear();
                if (dataSnapshot.exists()) {
                    result_text.setVisibility(View.GONE);
                    userList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Friend friend = snapshot.getValue(Friend.class);
                        try {
                            if (friend.getStatus().equals("friends")) {

                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                        .child(friend.getFriendId());

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {

                                                User user = dataSnapshot.getValue(User.class);
                                                try {
                                                    if (!userList.contains(user))
                                                        userList.add(user);
                                                } catch (Exception e) {
                                                    //
                                                }

                                            }
                                        }).start();
                                            /*User user = dataSnapshot.getValue(User.class);
                                            userList.add(user);*/

                                        try {
                                            musicPalAdapter.notifyDataSetChanged();
                                        } catch (Exception e) {

                                        }


                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                progressBar.setVisibility(View.INVISIBLE);
                                //progress_menu_logo.setVisibility(View.INVISIBLE);
                                //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                                progressBarTwo.setVisibility(View.INVISIBLE);

                                friendAvailable = true;

                            }

                        } catch (Exception e) {
                            //
                            //  Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                            result_text.setVisibility(View.VISIBLE);
                            result_text.setText("Can't fetch friends");

                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            //progress_menu_logo.setVisibility(View.INVISIBLE);
                            //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        }

                    }
                    if (!friendAvailable) {

                        result_text.setVisibility(View.VISIBLE);
                        result_text.setText("No friends");
                        progressBar.setVisibility(View.INVISIBLE);
                        //progress_menu_logo.setVisibility(View.INVISIBLE);
                        progressBarTwo.setVisibility(View.INVISIBLE);
                        //progress_menu_logo_two.setVisibility(View.INVISIBLE);

                    }


                } else {
                    //  Toast.makeText(getContext(),"No friends",Toast.LENGTH_SHORT).show();

                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("No friends");
                    progressBar.setVisibility(View.INVISIBLE);
                    //progress_menu_logo.setVisibility(View.INVISIBLE);
                    progressBarTwo.setVisibility(View.INVISIBLE);
                    //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}
