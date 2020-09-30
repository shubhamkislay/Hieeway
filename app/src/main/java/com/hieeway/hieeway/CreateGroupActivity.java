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
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.CreateGroupAdapter;
import com.hieeway.hieeway.Interface.FriendsSelectedListener;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateGroupActivity extends AppCompatActivity implements FriendsSelectedListener {

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    public static final String USERNAME = "username";
    public static final String PHOTO_URL = "photourl";
    public String searchingUsername;
    TextView username, result_text;
    RecyclerView friend_list_recyclerview;
    Activity activity;
    CreateGroupAdapter createGroupAdapter;
    EditText searchPeople;
    //ImageView //progress_menu_logo;
    ProgressBar progressBar, progressBarTwo; //progress_menu_logo_two;
    TextView logo_title;
    private List<Friend> userList;
    private ValueEventListener valueEventListener;
    private Query query;
    private List<User> userlist;
    private boolean friendAvailable = false;
    private String userID;
    private ImageView video_thumbnail;
    private TextView youtube_url;
    private RelativeLayout youtube_video_view;
    private String youtube_Url;
    private int friendsSelected = 0;
    private Button create_group_btn;
    private String groupName, groupID, icon;
    private List<Friend> groupMembers;
    private String currentUsername, currentPhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);


        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        groupID = intent.getStringExtra("groupID");
        icon = intent.getStringExtra("icon");


        userID = sharedPreferences.getString(USER_ID, "");
        currentUsername = sharedPreferences.getString(USERNAME, "");
        currentPhoto = sharedPreferences.getString(PHOTO_URL, "default");
        //try {


        userStatusOnDiconnect();
        username = findViewById(R.id.username);
        friend_list_recyclerview = findViewById(R.id.friend_list_recyclerview);
        userlist = new ArrayList<>();

        activity = this;

        userList = new ArrayList<>();

        progressBar = findViewById(R.id.progress);
        progressBarTwo = findViewById(R.id.progressTwo);
        create_group_btn = findViewById(R.id.create_group_btn);

        searchPeople = findViewById(R.id.search_people);

        searchPeople.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchPeople.setRawInputType(InputType.TYPE_CLASS_TEXT);
        logo_title = findViewById(R.id.layout_title);
        video_thumbnail = findViewById(R.id.video_thumbnail);
        youtube_url = findViewById(R.id.youtube_url);


        youtube_video_view = findViewById(R.id.youtube_video_view);


               /* Bundle extras = getIntent().getExtras();
                youtube_Url = extras.getString(Intent.EXTRA_TEXT);

                youtube_url.setText(youtube_Url);*/

        //getVideoIdfromUrl(youtube_Url);


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
                    videoPalAdapter.notifyDataSetChanged();
                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("Add friends from search tab");*/

                    populateWithFriends();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        create_group_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupMembers.size() > 1)
                    createGroup();
                else
                    Toast.makeText(CreateGroupActivity.this, "A group should be of atleast 3 people including you", Toast.LENGTH_SHORT).show();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateWithFriends();
                createGroupAdapter = new CreateGroupAdapter(CreateGroupActivity.this, userList, activity, youtube_Url);
                friend_list_recyclerview.setAdapter(createGroupAdapter);

            }
        }, 400);

        /*} catch (Exception e) {
            Toast.makeText(CreateGroupActivity.this, "Exception: "+e.toString(), Toast.LENGTH_SHORT).show();
            //startActivity(new Intent(CreateGroupActivity.this, MainActivity.class));
            finish();
        }*/
    }

    private void createGroup() {
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference("Groups")
                .child(groupID);

        HashMap<String, Object> groupHash = new HashMap<>();
        groupHash.put("icon", icon);
        groupHash.put("groupID", groupID);
        groupHash.put("groupName", groupName);

        groupRef.updateChildren(groupHash);


        DatabaseReference myGroup = FirebaseDatabase.getInstance().getReference("MyGroup")
                .child(userID)
                .child(groupID);

        myGroup.updateChildren(groupHash);


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("GroupMembers")
                .child(groupID);

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("id", userID);
        userMap.put("username", currentUsername);
        userMap.put("photo", currentPhoto);


        userRef.child(userRef.push().getKey())
                .updateChildren(userMap);

        int friends = 1;
        for (Friend friend : groupMembers) {
            DatabaseReference memRef = FirebaseDatabase.getInstance().getReference("GroupMembers")
                    .child(groupID);

            HashMap<String, Object> hashMap = new HashMap<>();

            hashMap.put("id", friend.getFriendId());
            hashMap.put("username", friend.getUsername());
            hashMap.put("photo", friend.getFriendId());
            hashMap.put("activePhoto", friend.getActivePhoto());
            hashMap.put("live", "no");

            memRef.child(memRef.push().getKey())
                    .updateChildren(hashMap);

            DatabaseReference mGroup = FirebaseDatabase.getInstance().getReference("MyGroup")
                    .child(friend.getFriendId())
                    .child(groupID);

            HashMap<String, Object> ghashMap = new HashMap<>();
            ghashMap.put("icon", icon);
            ghashMap.put("groupID", groupID);
            ghashMap.put("groupName", groupName);

            mGroup.updateChildren(ghashMap);

        }

        //Toast.makeText(CreateGroupActivity.this, "Group Created", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CreateGroupActivity.this, GroupChatActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("groupID", groupID);
        intent.putExtra("groupName", groupName);
        intent.putExtra("icon", icon);
        startActivity(intent);
    }

    private void userStatusOnDiconnect() {

        HashMap<String, Object> setOfflineHash = new HashMap<>();

        setOfflineHash.put("online", false);

        FirebaseDatabase.getInstance().getReference("Users")
                .child(userID)
                .onDisconnect()
                .updateChildren(setOfflineHash);
    }

    private void searchFriends(final String username) {


        if (username.length() > 0) {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(userID).orderByChild("username")
                    .startAt(username)
                    .endAt(username + "\uf8ff");
        } else {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(userID);
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


                                userList.add(friend);

                                // videoPalAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            //
                            result_text.setVisibility(View.VISIBLE);
                            result_text.setText("Can't fetch friends");
                            // Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                        }

                    }

                    createGroupAdapter.notifyDataSetChanged();

                    /*peopleAdapter.setList(userlist);*/
                    // if(username.length()>0)
                    //Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());

                    //  videoPalAdapter.notifyDataSetChanged();


                } else {
                    userList.clear();
                    createGroupAdapter.notifyDataSetChanged();
                    result_text.setVisibility(View.VISIBLE);
                    result_text.setText("No results found for '" + username + "'");
                    // Toast.makeText(getContext(),"Couldn't find user: "+username,Toast.LENGTH_SHORT).show();
                }

                // videoPalAdapter.notifyDataSetChanged();


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
                .child(userID);

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

                                userList.add(friend);
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
                            result_text.setText("Can't fetch friends\nSearch and add friends from the search tab");

                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            //progress_menu_logo.setVisibility(View.INVISIBLE);
                            //progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        }

                    }
                    try {
                        createGroupAdapter.notifyDataSetChanged();
                    } catch (Exception e) {

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

    @Override
    public void setFriendCount(List<Friend> groupMembers) {
        this.groupMembers = groupMembers;
    }
}