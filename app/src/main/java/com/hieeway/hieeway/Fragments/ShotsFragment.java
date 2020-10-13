package com.hieeway.hieeway.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.GroupsAdapter;
import com.hieeway.hieeway.Adapters.PostAdapter;
import com.hieeway.hieeway.ChatParentActivity;
import com.hieeway.hieeway.FriendListFragmentViewModel;
import com.hieeway.hieeway.GridSpacingItemDecoration;
import com.hieeway.hieeway.GroupChatActivity;
import com.hieeway.hieeway.Interface.AnimationArrowListener;
import com.hieeway.hieeway.Interface.ChatFragmentOpenListener;
import com.hieeway.hieeway.Interface.SeeAllGroupItemsListener;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.FriendListValues;
import com.hieeway.hieeway.Model.Groups;
import com.hieeway.hieeway.Model.MyGroup;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;


public class ShotsFragment extends Fragment implements SeeAllGroupItemsListener {

    private RecyclerView feeds_recyclerview, groups_recyclerview;
    private TextView feed_title, shots_title, groups_title;
    private Activity parentActivity;
    public static final String SHARED_PREFS = "sharedPrefs";
    private FriendListFragmentViewModel friendListFragmentViewModel;
    private GroupsAdapter groupsAdapter;
    private ImageButton chats;
    public static final String USER_ID = "userid";
    private List<MyGroup> userList;
    private ChatFragmentOpenListener chatFragmentOpenListener;
    private List<MyGroup> myGroupList;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private List<Post> postList;
    private PostAdapter postAdapter;
    private AnimationArrowListener animationArrowListener;
    private String userID;
    private boolean alreadyScrolled = false;
    private SharedPreferences sharedPreferences;
    private Button see_all_btn;
    private Boolean lastPost = false;
    private View view = null;

    public ShotsFragment() {

    }

    public ShotsFragment(Activity activity) {
        this.animationArrowListener = (AnimationArrowListener) activity;
        parentActivity = activity;
        this.chatFragmentOpenListener = (ChatFragmentOpenListener) activity;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null)
            return view;

        else {
            view = inflater.inflate(R.layout.fragment_shots, container, false);


            feeds_recyclerview = view.findViewById(R.id.feeds_recyclerview);
            feed_title = view.findViewById(R.id.feed_title);
            groups_title = view.findViewById(R.id.groups_title);
            shots_title = view.findViewById(R.id.shots_title);
            groups_recyclerview = view.findViewById(R.id.groups_recyclerview);
            chats = view.findViewById(R.id.chats);
            see_all_btn = view.findViewById(R.id.see_all_btn);

            try {
                sharedPreferences = parentActivity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

                userID = sharedPreferences.getString(USER_ID, "");
            } catch (NullPointerException e) {
                userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }


            userList = new ArrayList<>();
            postList = new ArrayList<>();
            userList.clear();
            MyGroup friend1 = new MyGroup("abc", "ijk", "xyz");
            MyGroup friend2 = new MyGroup("xyz", "abc", "ijk");
            MyGroup friend3 = new MyGroup("ijk", "xyz", "abc");
            userList.add(friend1);
            userList.add(friend2);
            userList.add(friend3);

            groupsAdapter = new GroupsAdapter(userList, getActivity(), this);


            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            float displayWidth = size.x;
            float displayHeight = size.y;

            try {
                // text_home.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
                feed_title.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
                groups_title.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
                shots_title.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
                //text_feed.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), "fonts/samsungsharpsans-bold.otf"));

            } catch (Exception e) {
                //
            }

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        Window window = Objects.requireNonNull(getActivity()).getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
                        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
                        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.darkButtonBackground));
                    } catch (Exception e) {
                        //
                    }

                }
            }, 0);

            // Toast.makeText(getContext(),"Height: "+displayHeight,Toast.LENGTH_LONG).show();


            int spanCount; // 3 columns
            int spacing = 0; // 50px
            boolean includeEdge = true;

            if (displayWidth >= 1920)
                spanCount = 2;

            else if (displayWidth >= 1080)
                spanCount = 2;

            else if (displayWidth >= 500)
                spanCount = 2;
            else
                spanCount = 1;


            chats.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // startActivity(new Intent(getActivity(), ChatParentActivity.class));
                    chatFragmentOpenListener.setChatFragmentStatus(true);
                }
            });


            staggeredGridLayoutManager = new StaggeredGridLayoutManager(/*spanCount*/2, LinearLayoutManager.VERTICAL);


            LinearLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);


            gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

            //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            feeds_recyclerview.setLayoutManager(staggeredGridLayoutManager);
            feeds_recyclerview.setHasFixedSize(true);
            feeds_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

            RecyclerView.ItemAnimator animator = feeds_recyclerview.getItemAnimator();
            if (animator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            }


            feeds_recyclerview.setItemViewCacheSize(20);
            feeds_recyclerview.setDrawingCacheEnabled(true);
            feeds_recyclerview.setItemAnimator(new DefaultItemAnimator());
            feeds_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


            postAdapter = new PostAdapter(postList, parentActivity, userID);

            feeds_recyclerview.setAdapter(postAdapter);


            LinearLayoutManager horizontalLayoutManager = new LinearLayoutManager(getContext());
            horizontalLayoutManager.setOrientation(RecyclerView.HORIZONTAL);
            horizontalLayoutManager.setStackFromEnd(false);


            groups_recyclerview.setLayoutManager(horizontalLayoutManager);
            groups_recyclerview.setHasFixedSize(true);


            RecyclerView.ItemAnimator groups_recyclerviewAnimator = feeds_recyclerview.getItemAnimator();
            if (groups_recyclerviewAnimator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) groups_recyclerviewAnimator).setSupportsChangeAnimations(false);
            }


            groups_recyclerview.setItemViewCacheSize(20);
            groups_recyclerview.setDrawingCacheEnabled(true);
            groups_recyclerview.setItemAnimator(new DefaultItemAnimator());
            groups_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            groups_recyclerview.setAdapter(groupsAdapter);
            groups_recyclerview.scrollToPosition(0);


            chats.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        chats.animate().scaleX(1.5f).scaleY(1.5f).setDuration(0);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        chats.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    } else {
                        chats.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);
                    }
                    return false;
                }
            });

            see_all_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getActivity(), "Opening groups", Toast.LENGTH_SHORT).show();
                }
            });


        }
        return view;
    }

    private void playArrowAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    animationArrowListener.playArrowAnimation();

                } catch (Exception e) {

                    //Toast.makeText(getContext(), "Thread Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, 1000);
    }

    private void populateGroups() {
        myGroupList = new ArrayList<>();
        myGroupList.clear();

        MyGroup friend1 = new MyGroup("abc", "ijk", "xyz");
        MyGroup friend2 = new MyGroup("xyz", "abc", "ijk");
        MyGroup friend3 = new MyGroup("ijk", "xyz", "abc");
        myGroupList.add(friend1);
        myGroupList.add(friend2);
        myGroupList.add(friend3);

        DatabaseReference myGroupRefs = FirebaseDatabase.getInstance().getReference("MyGroup")
                .child(userID);
        myGroupRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MyGroup myGroup = dataSnapshot.getValue(MyGroup.class);
                        myGroupList.add(myGroup);
                    }

                    //userList = myGroupList;
                    groupsAdapter.updateList(myGroupList);
                    if (!alreadyScrolled) {
                        groups_recyclerview.scrollToPosition(0);
                        alreadyScrolled = true;
                    }


                }

                playArrowAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                /*friendListFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(FriendListFragmentViewModel.class);
                friendListFragmentViewModel.getAllUser().observe(getViewLifecycleOwner(), new Observer<FriendListValues>() {
                    @Override
                    public void onChanged(@Nullable final FriendListValues friendListValues) {
                        //userList = friendListValues.getFriendList();
                        groupsAdapter.updateList(userList);
                        groups_recyclerview.scrollToPosition(0);
                        playArrowAnimation();

                    }
                });*/


                populateGroups();
                lookForPosts();
            }
        }, 350);
    }

    private void lookForPosts() {


        postList.clear();


        DatabaseReference friendsRef = FirebaseDatabase.getInstance().getReference("FriendList")
                .child(userID);

        friendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Friend friend = dataSnapshot.getValue(Friend.class);
                        if (friend.getStatus().equals("friends"))
                            populatePosts(friend.getFriendId());
                    }

                    /*new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            *//*postAdapter = new PostAdapter(postList,parentActivity,userID);
                            feeds_recyclerview.setAdapter(postAdapter);*//*
                            //postAdapter.updateList(postList);
                            Toast.makeText(parentActivity,"Size: "+postList.size(),Toast.LENGTH_SHORT).show();
                        }
                    },5000);*/


                    lastPost = true;
                } else {
                    Toast.makeText(parentActivity, "FriendList doesnot exists", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void populatePosts(String friendId) {


        DatabaseReference postRefs = FirebaseDatabase.getInstance().getReference("Post")
                .child(friendId);

        postRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    List<Post> postToRemoveList = new ArrayList<>();
                    for (Post postToRemove : postList) {
                        if (postToRemove.getUserId().equals(friendId))
                            postToRemoveList.add(postToRemove);
                    }

                    postList.removeAll(postToRemoveList);

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (!postList.contains(post))
                            postList.add(post);

                        //Toast.makeText(parentActivity,"Post added",Toast.LENGTH_SHORT).show();

                    }

                    //if(lastPost)
                    postAdapter.updateList(postList);
                    //Toast.makeText(parentActivity,"Post Size: "+postList.size(),Toast.LENGTH_SHORT).show();
                    /*postAdapter = new PostAdapter(postList,parentActivity,userID);
                    feeds_recyclerview.setAdapter(postAdapter);*/

                } else {

                    List<Post> postToRemoveList = new ArrayList<>();
                    for (Post postToRemove : postList) {
                        if (postToRemove.getUserId().equals(friendId))
                            postToRemoveList.add(postToRemove);
                    }

                    postList.removeAll(postToRemoveList);
                    postAdapter.updateList(postList);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void seeAllBtnVisibility(int visibility) {
        see_all_btn.setVisibility(visibility);
    }
}