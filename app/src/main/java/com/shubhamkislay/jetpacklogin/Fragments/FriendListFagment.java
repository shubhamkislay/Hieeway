package com.shubhamkislay.jetpacklogin.Fragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.shubhamkislay.jetpacklogin.Adapters.FriendsAdapter;
import com.shubhamkislay.jetpacklogin.ContactsActivity;
import com.shubhamkislay.jetpacklogin.GridSpacingItemDecoration;
import com.shubhamkislay.jetpacklogin.Model.Friend;
import com.shubhamkislay.jetpacklogin.Model.User;
import com.shubhamkislay.jetpacklogin.NavButtonTest;
import com.shubhamkislay.jetpacklogin.R;
import com.shubhamkislay.jetpacklogin.RequestTrackerActivity;
import com.shubhamkislay.jetpacklogin.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendListFagment extends Fragment {

    public RecyclerView friend_list_recyclerview;
    //private PeopleAdapter peopleAdapter;

    private FriendsAdapter friendsAdapter;
    private List<User> userList;
    ImageView progress_menu_logo, progress_menu_logo_two;
    private TextView appTitle;
    private Button requests_btn, search_chat_btn;
    ProgressBar progressBar,progressBarTwo;
    RelativeLayout search_btn_layout, search_bar_layout;
    private CollapsingToolbarLayout toolbar;
    Button close_search, search_chat_btn_back;
    EditText search_bar;
    TextView search_result_txt;
    private ValueEventListener valueEventListener;
    private Query query;
    Button requests_btn_friends_back;

    Boolean searchBtnActive = true;
    int friendRequestsCounter = 0;
    private boolean enableRefreshButton = false;
    private boolean friendRequests = false;
    private boolean friendAvailable = false;
    Button contactsbntn;
    String phonenumber = "default";
    private SharedViewModel sharedViewModel;


    public FriendListFagment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       // View view = inflater.inflate(R.layout.fragment_friend_list_fagment, container, false);
        View view = inflater.inflate(R.layout.fragment_friend_collapsing_layout, container, false);

    //    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

     //   getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        final Activity activity = getActivity();

        userList = new ArrayList<>();

        contactsbntn = view.findViewById(R.id.contactsbntn);

        friend_list_recyclerview = view.findViewById(R.id.friend_list_recyclerview);

        appTitle = view.findViewById(R.id.app_title);

        toolbar = view.findViewById(R.id.toolbar);

        requests_btn = view.findViewById(R.id.requests_btn_friends);
        requests_btn_friends_back = view.findViewById(R.id.requests_btn_friends_back);

        progress_menu_logo = view.findViewById(R.id.progress_menu_logo);

        progress_menu_logo_two = view.findViewById(R.id.progress_menu_logo_two);

        Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.nav_status_color));


       // search_btn_layout = view.findViewById(R.id.search_bar_layout_friends);


        search_bar = view.findViewById(R.id.search_bar_friends);

        search_result_txt = view.findViewById(R.id.search_result_txt);
        search_result_txt.setVisibility(View.GONE);


        search_chat_btn = view.findViewById(R.id.search_chat_btn_friends);

        //  phonenumber = getArguments().getString("phonenumber");


        search_bar.setImeOptions(EditorInfo.IME_ACTION_DONE);

        search_bar.setRawInputType(InputType.TYPE_CLASS_TEXT);

        sharedViewModel = ViewModelProviders.of(getActivity()).get(SharedViewModel.class);


        sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
            @Override
            public void onChanged(User user) {

                phonenumber = user.getPhonenumber();
            }
        });
        //search_chat_btn_back = view.findViewById(R.id.search_chat_btn_back);

        //close_search = view.findViewById(R.id.close_search);

        /*search_chat_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar_layout.setVisibility(View.VISIBLE);
                appTitle.setVisibility(View.GONE);

                search_bar.setVisibility(View.VISIBLE);
                search_btn_layout.setVisibility(View.GONE);
                requests_btn.setVisibility(View.GONE);

                // search_bar.setFocusable(true);
                search_bar.requestFocus();
                showSoftKeyboard(search_bar);
                // search_bar.setFocusable(true);
                //  search_bar.setSelected(true);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });*/

/*        search_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar_layout.setVisibility(View.VISIBLE);
                appTitle.setVisibility(View.GONE);

                search_bar.setVisibility(View.VISIBLE);
                requests_btn.setVisibility(View.GONE);

                // search_bar.setFocusable(true);
                search_bar.requestFocus();
                showSoftKeyboard(search_bar);
                // search_bar.setFocusable(true);
                // search_bar.setSelected(true);

                search_btn_layout.setVisibility(View.GONE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            }
        });*/

        contactsbntn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                intent.putExtra("phonenumber", phonenumber);
                startActivity(intent);

            }
        });

        search_chat_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //search_bar_layout.setVisibility(View.VISIBLE);

                if(searchBtnActive) {
                    searchBtnActive = false;
                    appTitle.setVisibility(View.GONE);

                    search_bar.setVisibility(View.VISIBLE);

                    showSoftKeyboard(search_bar);

                    // search_bar.setFocusable(true);
                    requests_btn.setVisibility(View.GONE);

                    search_chat_btn.setBackground(getActivity().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
                    search_bar.requestFocus();
                    // search_bar.setFocusable(true);
                    // search_bar.setSelected(true);

                    // search_btn_layout.setVisibility(View.GONE);
                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                }
                else
                {
                    searchBtnActive = true;

                    appTitle.setVisibility(View.VISIBLE);
                    requests_btn.setVisibility(View.VISIBLE);

                    hideSoftKeyboard(search_bar);
                    search_bar.setText("");

                    search_bar.setVisibility(View.GONE);

                    search_chat_btn.setBackground(getActivity().getDrawable(R.drawable.ic_search_black_24dp));

                }


            }
        });

/*        search_btn_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar_layout.setVisibility(View.VISIBLE);
                appTitle.setVisibility(View.GONE);

                search_bar.setVisibility(View.VISIBLE);
                requests_btn.setVisibility(View.GONE);

                // search_bar.setFocusable(true);
                search_bar.requestFocus();
                showSoftKeyboard(search_bar);
                // search_bar.setFocusable(true);
                // search_bar.setSelected(true);

                search_btn_layout.setVisibility(View.GONE);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            }
        });*/

/*        close_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar_layout.setVisibility(View.GONE);
                appTitle.setVisibility(View.VISIBLE);

                hideSoftKeyboard(search_bar);
                search_bar.setVisibility(View.GONE);
                requests_btn.setVisibility(View.VISIBLE);

                search_bar.setText("");
                search_btn_layout.setVisibility(View.VISIBLE);
                // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            }
        });*/

        search_bar.addTextChangedListener(new TextWatcher() {
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
                    friendsAdapter.notifyDataSetChanged();
                    search_result_txt.setVisibility(View.VISIBLE);
                    search_result_txt.setText("Add friends from search tab");*/

                    populateWithFriends();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });






        progressBar = view.findViewById(R.id.progress);
        progressBarTwo = view.findViewById(R.id.progressTwo);

        try {
            appTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
            requests_btn.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
            search_result_txt.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        }catch (Exception e)
        {
            //
        }

        requests_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestTrackerActivity.class);

                getActivity().startActivity(intent);
                enableRefreshButton = true;
            }
        });

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;


        int spanCount; // 3 columns
        int spacing = 0; // 50px
        boolean includeEdge = true;

        if(displayWidth>=1920)
            spanCount=4;

        else if(displayWidth>=1080)
            spanCount=3;

        else if(displayWidth>=500)
            spanCount=2;
        else
            spanCount=1;

        search_bar.getLayoutParams().width = (int) displayWidth;

        LinearLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),spanCount);


        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        friend_list_recyclerview.setLayoutManager(gridLayoutManager);
        friend_list_recyclerview.setHasFixedSize(true);
        friend_list_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        friend_list_recyclerview.setItemViewCacheSize(20);
        friend_list_recyclerview.setDrawingCacheEnabled(true);
        friend_list_recyclerview.setItemAnimator(new DefaultItemAnimator());
        friend_list_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);



       // peopleAdapter = new FriendsAdapter(getContext(), userList,activity);
       // friend_list_recyclerview.setAdapter(peopleAdapter);




        //animateMenuImage(progress_menu_logo, progress_menu_logo_two);






/*        DatabaseReference friendsRefresh = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        friendsRefresh.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists())
                {
                    if(enableRefreshButton) {
                        Toast.makeText(getContext(), "Refresh list", Toast.LENGTH_SHORT).show();
                        enableRefreshButton = false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateWithFriends();
                friendsAdapter = new FriendsAdapter(getContext(), userList,activity);
                friend_list_recyclerview.setAdapter(friendsAdapter);

            }
        }, 400);

        return view;
    }

    public void getUserPhonenumber() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);

                phonenumber = user.getPhonenumber();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void searchFriends(final String username) {


        if(username.length()>0) {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("username")
                    .startAt(username)
                    .endAt(username + "\uf8ff");
        }
        else
        {
            query = FirebaseDatabase.getInstance().getReference("FriendList")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // userList.clear();


                if(dataSnapshot.exists())
                {
                    userList.clear();
                    search_result_txt.setVisibility(View.GONE);

                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
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
                                            friendsAdapter.notifyDataSetChanged();
                                        }catch (Exception e)
                                        {
                                            //
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


                                // friendsAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception e)
                        {
                            //
                            search_result_txt.setVisibility(View.VISIBLE);
                            search_result_txt.setText("Can't fetch friends");
                            // Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                        }

                    }

                    /*peopleAdapter.setList(userlist);*/
                    // if(username.length()>0)
                    //Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());

                    //  friendsAdapter.notifyDataSetChanged();



                }
                else {
                    userList.clear();
                    friendsAdapter.notifyDataSetChanged();
                    search_result_txt.setVisibility(View.VISIBLE);
                    search_result_txt.setText("No results found for '"+username+"'");
                    // Toast.makeText(getContext(),"Couldn't find user: "+username,Toast.LENGTH_SHORT).show();
                }

                // friendsAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };


        query.addValueEventListener(valueEventListener);




    }

    private void populateWithFriends() {


        search_result_txt.setVisibility(View.GONE);
        friendRequestsCounter = 0;
        userList.clear();
        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //userList.clear();
                if(dataSnapshot.exists())
                {
                    search_result_txt.setVisibility(View.GONE);
                    userList.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren())
                    {
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
                                                    userList.add(user);

                                                }
                                            }).start();
                                            /*User user = dataSnapshot.getValue(User.class);
                                            userList.add(user);*/

                                            friendsAdapter.notifyDataSetChanged();


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });

                                progressBar.setVisibility(View.INVISIBLE);
                                progress_menu_logo.setVisibility(View.INVISIBLE);
                                progress_menu_logo_two.setVisibility(View.INVISIBLE);
                                progressBarTwo.setVisibility(View.INVISIBLE);

                                friendAvailable = true;

                            } else if (friend.getStatus().equals("got")) {

                                friendRequests = true;
                                friendRequestsCounter += 1;
                            }

                        }catch (Exception e)
                        {
                            //
                          //  Toast.makeText(getContext(),"Can't fetch friends",Toast.LENGTH_SHORT).show();

                            search_result_txt.setVisibility(View.VISIBLE);
                            search_result_txt.setText("Can't fetch friends");

                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            progress_menu_logo.setVisibility(View.INVISIBLE);
                            progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        }

                    }
                    if (!friendAvailable) {
                        if (friendRequests) {
                            search_result_txt.setVisibility(View.VISIBLE);
                            requests_btn_friends_back.setVisibility(View.VISIBLE);
                            if (friendRequestsCounter > 1)
                                search_result_txt.setText("You have got " + friendRequestsCounter + " friend requests");
                            else
                                search_result_txt.setText("You have got a friend request");
                            progressBar.setVisibility(View.INVISIBLE);
                            progress_menu_logo.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        } else {
                            search_result_txt.setVisibility(View.VISIBLE);
                            search_result_txt.setText("No friends");
                            progressBar.setVisibility(View.INVISIBLE);
                            progress_menu_logo.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        if (friendRequests) {
                            requests_btn_friends_back.setVisibility(View.VISIBLE);
                            // friendRequestsCounter
                            progressBar.setVisibility(View.INVISIBLE);
                            progress_menu_logo.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            progress_menu_logo_two.setVisibility(View.INVISIBLE);
                        }
                    }


                }
                else {
                  //  Toast.makeText(getContext(),"No friends",Toast.LENGTH_SHORT).show();

                    search_result_txt.setVisibility(View.VISIBLE);
                    search_result_txt.setText("No friends");
                    progressBar.setVisibility(View.INVISIBLE);
                    progress_menu_logo.setVisibility(View.INVISIBLE);
                    progressBarTwo.setVisibility(View.INVISIBLE);
                    progress_menu_logo_two.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        populateWithFriends();
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }
    public void animateMenuImage(ImageView imageView, ImageView imageViewTwo)
    {



        Animation hyperspaceJumpImg = AnimationUtils.loadAnimation(getContext(), R.anim.enter_right_to_left);

        hyperspaceJumpImg.setRepeatMode(Animation.INFINITE);

        imageView.setAnimation(hyperspaceJumpImg);

        Animation hyperspaceJumpImgTwo = AnimationUtils.loadAnimation(getContext(), R.anim.enter_left_to_right);

        hyperspaceJumpImgTwo.setRepeatMode(Animation.INFINITE);

        imageViewTwo.setAnimation(hyperspaceJumpImgTwo);


    }

    @Override
    public void onPause() {
        try{
            query.removeEventListener(valueEventListener);
        }
        catch (Exception e)
        {

        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        //getUserPhonenumber();
        if (enableRefreshButton) {
            friendRequests = false;
            friendAvailable = false;
            requests_btn_friends_back.setVisibility(View.GONE);
            search_result_txt.setVisibility(View.GONE);
            populateWithFriends();
            enableRefreshButton = false;
        }

    }
}
