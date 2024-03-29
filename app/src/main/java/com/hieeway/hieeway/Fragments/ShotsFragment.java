package com.hieeway.hieeway.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import com.bumptech.glide.Glide;
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
import com.hieeway.hieeway.ChatsFragmentViewModel;
import com.hieeway.hieeway.FeelingDialog;
import com.hieeway.hieeway.FriendListFragmentViewModel;
import com.hieeway.hieeway.GridSpacingItemDecoration;
import com.hieeway.hieeway.GroupChatActivity;
import com.hieeway.hieeway.Interface.AddFeelingFragmentListener;
import com.hieeway.hieeway.Interface.AnimationArrowListener;
import com.hieeway.hieeway.Interface.ChatFragmentOpenListener;
import com.hieeway.hieeway.Interface.FeelingListener;
import com.hieeway.hieeway.Interface.SeeAllGroupItemsListener;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.FriendListValues;
import com.hieeway.hieeway.Model.Groups;
import com.hieeway.hieeway.Model.MyGroup;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.Model.PostSeen;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.NavButtonTest;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SharedViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;
import static com.hieeway.hieeway.NavButtonTest.CURR_USER_ID;
import static com.hieeway.hieeway.NavButtonTest.USER_NAME;
import static com.hieeway.hieeway.NavButtonTest.USER_PHOTO;


public class ShotsFragment extends Fragment implements SeeAllGroupItemsListener, FeelingListener {

    private RecyclerView feeds_recyclerview, groups_recyclerview;
    private TextView feed_title, shots_title, groups_title;
    public static final String FEELING = "feeling";
    public static final String SHARED_PREFS = "sharedPrefs";
    private FriendListFragmentViewModel friendListFragmentViewModel;
    private GroupsAdapter groupsAdapter;
    private ImageButton chats;
    public static final String USER_ID = "userid";
    private List<MyGroup> userList;
    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";
    RelativeLayout emoji_holder_layout;
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
    TextView feeling_icon, emoji_icon, new_shot_btn;
    FeelingDialog feelingDialog;
    String feelingNow = null;
    AddFeelingFragmentListener addFeelingFragmentListener;
    private NavButtonTest parentActivity;
    private SharedViewModel sharedViewModel;
    private DatabaseReference myGroupRefs;
    private ValueEventListener groupValueEventListener;
    private DatabaseReference postRefs;
    private ValueEventListener postsValueEventListener;
    private TextView chat_pending;
    private RelativeLayout chats_back;
    private DatabaseReference chatPendingReference;
    private ValueEventListener chatPendingListener;
    public List<ChatStamp> chatStampList;


    private ChatsFragmentViewModel chatsFragmentViewModel;


    public ShotsFragment() {

    }

    public ShotsFragment(Activity activity) {
        this.animationArrowListener = (AnimationArrowListener) activity;
        parentActivity = (NavButtonTest) activity;
        this.chatFragmentOpenListener = (ChatFragmentOpenListener) activity;
        this.addFeelingFragmentListener = (AddFeelingFragmentListener) activity;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null)
            return view;

       // else {
            view = inflater.inflate(R.layout.fragment_shots, container, false);


            feeds_recyclerview = view.findViewById(R.id.feeds_recyclerview);
        feed_title = view.findViewById(R.id.feed_title);
        groups_title = view.findViewById(R.id.groups_title);
        shots_title = view.findViewById(R.id.shots_title);
        groups_recyclerview = view.findViewById(R.id.groups_recyclerview);
        chats = view.findViewById(R.id.chats);
        see_all_btn = view.findViewById(R.id.see_all_btn);
        feeling_icon = view.findViewById(R.id.feeling_icon);
        emoji_icon = view.findViewById(R.id.emoji_icon);
        emoji_holder_layout = view.findViewById(R.id.emoji_holder_layout);
        new_shot_btn = view.findViewById(R.id.new_shot_btn);
        chat_pending = view.findViewById(R.id.chat_pending);
        chats_back = view.findViewById(R.id.chats_back);

        try {
            sharedPreferences = parentActivity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

            userID = sharedPreferences.getString(USER_ID, "");
            feelingNow = sharedPreferences.getString(FEELING, "happy");
        } catch (NullPointerException e) {
            userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            feelingNow = "happy";
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

            groupsAdapter = new GroupsAdapter(userList, getActivity(), this, userID);


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
                new_shot_btn.setTypeface(Typeface.createFromAsset(parentActivity.getAssets(), "fonts/samsungsharpsans-bold.otf"));
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
                        window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.nav_darktheme_btn_active));
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
        //staggeredGridLayoutManager.setReverseLayout(true);

            /*LinearLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), spanCount);


            gridLayoutManager.setOrientation(RecyclerView.VERTICAL);*/

            //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
            feeds_recyclerview.setLayoutManager(staggeredGridLayoutManager);
            feeds_recyclerview.setHasFixedSize(true);

            feeds_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        /**changed code here

         /*RecyclerView.ItemAnimator animator = feeds_recyclerview.getItemAnimator();
         if (animator instanceof SimpleItemAnimator) {
         ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
         }*/

        /**

         /*feeds_recyclerview.setItemViewCacheSize(20);
         feeds_recyclerview.setDrawingCacheEnabled(true);
         feeds_recyclerview.setItemAnimator(new DefaultItemAnimator());
         feeds_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/


        Post post = new Post();
        post.setMediaKey("xyz");
        post.setMediaUrl("xyz");
        post.setPostKey("xyz");
        post.setTimeStamp("xyz");
        post.setType("create");
        post.setUsername("xyz");
        post.setUserId(userID);

            postList.add(post);

            postAdapter = new PostAdapter(postList, parentActivity, userID);
        //postAdapter.setHasStableIds(true);
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

            new_shot_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), "Creating new shot", Toast.LENGTH_SHORT).show();
                }
            });

            see_all_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getActivity(), "Opening groups", Toast.LENGTH_SHORT).show();
                }
            });

            feeling_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    feelingDialog = new FeelingDialog(getContext(), ShotsFragment.this, feelingNow, addFeelingFragmentListener, "shots");
                    feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    feelingDialog.show();

                }
            });
            emoji_holder_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    feelingDialog = new FeelingDialog(getContext(), ShotsFragment.this, feelingNow, addFeelingFragmentListener, "shots");
                    feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    feelingDialog.show();

                }
            });

            emoji_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    feelingDialog = new FeelingDialog(getContext(), ShotsFragment.this, feelingNow, addFeelingFragmentListener, "shots");
                    feelingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    feelingDialog.show();

                }
            });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    chatsFragmentViewModel = ViewModelProviders.of(Objects.requireNonNull(getActivity())).get(ChatsFragmentViewModel.class);
                    chatsFragmentViewModel.getAllUser().observe(getViewLifecycleOwner(), new Observer<List<ChatStamp>>() {
                        @Override
                        public void onChanged(@Nullable final List<ChatStamp> chatStamps) {

                            int pendingChats = 0;

                            for (ChatStamp chatStamp : chatStamps) {
                                if (chatStamp.getChatPending())
                                    ++pendingChats;
                            }


                        }
                    });
                } catch (Exception e) {
                    //Toast.makeText(getContext(), "Handler Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        }, 500);


        // }
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {
                    sharedViewModel = ViewModelProviders.of(parentActivity).get(SharedViewModel.class);

                    sharedViewModel.getUser().observe(getViewLifecycleOwner(), new Observer<User>() {
                        @Override
                        public void onChanged(@Nullable User user) {


                            //  feeling_txt.setText(user.getFeeling());
                            feelingNow = user.getFeeling();
                            SharedPreferences sharedPreferences = parentActivity.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();

                            editor.putString(FEELING, feelingNow);
                            editor.apply();

                            try {
                                if (!user.getFeelingIcon().equals("default")) {
                                    feelingNow = user.getFeeling();
                                    feeling_icon.setVisibility(View.GONE);
                                    emoji_icon.setText(user.getFeelingIcon());
                                    emoji_icon.setVisibility(View.VISIBLE);


                                } else {
                                    feeling_icon.setVisibility(View.GONE);
                                    emoji_icon.setVisibility(View.GONE);

                                    feelingNow = user.getFeeling();
                                    switch (user.getFeeling()) {
                                        case HAPPY:
                                            feeling_icon.setVisibility(View.VISIBLE);
                                            feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                                            break;
                                        case SAD:
                                            feeling_icon.setVisibility(View.VISIBLE);
                                            feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                                            break;
                                        case BORED:
                                            feeling_icon.setVisibility(View.VISIBLE);
                                            feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                                            break;
                                        case ANGRY:
                                            feeling_icon.setVisibility(View.VISIBLE);
                                            feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                                            break;
                                        case "excited":
                                            feeling_icon.setVisibility(View.VISIBLE);
                                            feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                                            break;
                                        case CONFUSED:
                                            feeling_icon.setVisibility(View.VISIBLE);
                                            feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                                            break;
                                    }
                                }

                            } catch (Exception e) {
                                //
                            }


                        }
                    });
                } catch (Exception e) {
                    //
                }


            }
        }, 350);
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

        myGroupRefs = FirebaseDatabase.getInstance().getReference("MyGroup")
                .child(userID);


       /* myGroupRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myGroupList.clear();
                MyGroup friend1 = new MyGroup("abc", "ijk", "xyz");
                MyGroup friend2 = new MyGroup("xyz", "abc", "ijk");
                MyGroup friend3 = new MyGroup("ijk", "xyz", "abc");
                myGroupList.add(friend1);
                myGroupList.add(friend2);
                myGroupList.add(friend3);

                if (snapshot.exists()) {
                    List<MyGroup> sortedGroupList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MyGroup myGroup = dataSnapshot.getValue(MyGroup.class);
                        sortedGroupList.add(myGroup);
                    }

                    Collections.sort(sortedGroupList, Collections.<MyGroup>reverseOrder());
                    //userList = myGroupList;
                    myGroupList.addAll(sortedGroupList);
                    groupsAdapter.updateList(myGroupList);
                    if (!alreadyScrolled) {
                        groups_recyclerview.scrollToPosition(0);
                        alreadyScrolled = true;
                    }


                } else {


                    groupsAdapter.updateList(myGroupList);
                }

                playArrowAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

*/
        groupValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                myGroupList.clear();
                MyGroup friend1 = new MyGroup("abc", "ijk", "xyz");
                MyGroup friend2 = new MyGroup("xyz", "abc", "ijk");
                MyGroup friend3 = new MyGroup("ijk", "xyz", "abc");
                myGroupList.add(friend1);
                myGroupList.add(friend2);
                myGroupList.add(friend3);

                if (snapshot.exists()) {
                    List<MyGroup> sortedGroupList = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        MyGroup myGroup = dataSnapshot.getValue(MyGroup.class);
                        sortedGroupList.add(myGroup);
                    }

                    Collections.sort(sortedGroupList, Collections.<MyGroup>reverseOrder());
                    //userList = myGroupList;
                    myGroupList.addAll(sortedGroupList);
                    groupsAdapter.updateList(myGroupList);
                    if (!alreadyScrolled) {
                        groups_recyclerview.scrollToPosition(0);
                        alreadyScrolled = true;
                    }


                } else {


                    groupsAdapter.updateList(myGroupList);
                }

                playArrowAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        myGroupRefs.addValueEventListener(groupValueEventListener);
    }

    @Override
    public void onResume() {
        super.onResume();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                populateGroups();

            }
        }, 350);

        populatePosts(userID);

        chatPendingReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userID);

        chatStampList = new ArrayList<>();
        chatPendingListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatStampList.clear();
                int chatsPending = 0;
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                        ChatStamp chatStamp = snapshot.getValue(ChatStamp.class);

                        if (chatStamp.getChatPending())
                            ++chatsPending;


                    }

                    //Toast.makeText(parentActivity,"Chats Pending: "+chatsPending,Toast.LENGTH_SHORT).show();

                    if (chatsPending > 0) {

                        chats_back.setVisibility(View.VISIBLE);
                        chat_pending.setVisibility(View.VISIBLE);


                        if (chatsPending <= 10)
                            chat_pending.setText("" + chatsPending);
                        else
                            chat_pending.setText("" + chatsPending);

                    } else {
                        chats_back.setVisibility(View.INVISIBLE);
                        chat_pending.setVisibility(View.INVISIBLE);
                    }


                } else {
                    chat_pending.setVisibility(View.INVISIBLE);
                    chats_back.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        chatPendingReference.addValueEventListener(chatPendingListener);

    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            myGroupRefs.removeEventListener(groupValueEventListener);
        } catch (Exception e) {

        }

        try {
            postRefs.removeEventListener(postsValueEventListener);
        } catch (Exception e) {
            //
        }

        try {
            chatPendingReference.removeEventListener(chatPendingListener);
        } catch (Exception e) {

        }

    }



    private void populatePosts(String friendId) {


        postRefs = FirebaseDatabase.getInstance().getReference("Post")
                .child(friendId);


        postsValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<Post> initialList = new ArrayList<>();
                //initialList.clear();
                List<Post> refreshPostList = new ArrayList<>();

                Post postDummy = new Post();
                postDummy.setMediaKey(postRefs.push().getKey());
                postDummy.setMediaUrl("xyz");
                postDummy.setPostKey(postRefs.push().getKey());
                postDummy.setTimeStamp("xyz");
                postDummy.setType("create");
                postDummy.setUsername("xyz");
                postDummy.setUserId("xyz");

                refreshPostList.add(postDummy);

                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Post post = dataSnapshot.getValue(Post.class);

                        Long tsLong = System.currentTimeMillis() / 1000;
                        long localUserDiff = tsLong - post.getPostTime();

                        long localDiffHours = localUserDiff / (60 * 60 * 24);

                        if (localDiffHours < 1) {
                            if (!initialList.contains(post))
                                initialList.add(post);

                            else if (post.getType().equals("music"))
                                initialList.get(initialList.indexOf(post)).setTimeStamp(post.getTimeStamp());
                        } else {
                            postRefs.child(post.getPostKey()).removeValue();
                        }


                        //Toast.makeText(parentActivity,"Post added",Toast.LENGTH_SHORT).show();

                    }

                    //if(lastPost)
                    Collections.sort(initialList, Collections.<Post>reverseOrder());
                    refreshPostList.addAll(initialList);

                    //postAdapter = new PostAdapter(refreshPostList,parentActivity,userID);
                    //feeds_recyclerview.setAdapter(postAdapter);
                    //Toast.makeText(parentActivity,"List updated "+refreshPostList.size(),Toast.LENGTH_SHORT).show();
                    postAdapter.updateList(refreshPostList);
                    //feeds_recyclerview.scrollToPosition(0);
                    //Toast.makeText(parentActivity,"Post Size: "+postList.size(),Toast.LENGTH_SHORT).show();
                    /*postAdapter = new PostAdapter(postList,parentActivity,userID);
                    feeds_recyclerview.setAdapter(postAdapter);*/

                } else {
                    postAdapter.updateList(refreshPostList);

                    /*postAdapter = new PostAdapter(refreshPostList,parentActivity,userID);
                    feeds_recyclerview.setAdapter(postAdapter);*/
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        postRefs.addValueEventListener(postsValueEventListener);


    }

    @Override
    public void seeAllBtnVisibility(int visibility) {
        see_all_btn.setVisibility(visibility);
    }

    @Override
    public void changeFeeling(String feeling) {
        DatabaseReference feelingReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(userID);

        animateEmoji();

        HashMap<String, Object> feelingHash = new HashMap<>();
        feeling_icon.setVisibility(View.VISIBLE);
        emoji_icon.setVisibility(View.GONE);
        switch (feeling) {
            case HAPPY:
                feelingHash.put("feeling", HAPPY);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_happy));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(parentActivity.getResources().getColor(R.color.colorPrimaryDark)));

                break;
            case SAD:
                feelingHash.put("feeling", SAD);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_sad));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(parentActivity.getResources().getColor(R.color.colorPrimaryDark)));

                break;
            case BORED:
                feelingHash.put("feeling", BORED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_bored));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(parentActivity.getResources().getColor(R.color.colorPrimaryDark)));

                break;
            case ANGRY:
                feelingHash.put("feeling", ANGRY);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_angry));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(parentActivity.getResources().getColor(R.color.colorPrimaryDark)));

                break;
            case "excited":
                feelingHash.put("feeling", "excited");
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_excited));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(parentActivity.getResources().getColor(R.color.colorPrimaryDark)));

                break;
            case CONFUSED:
                feelingHash.put("feeling", CONFUSED);
                feelingHash.put("feelingIcon", "default");
                feelingReference.updateChildren(feelingHash);
                feeling_icon.setBackground(parentActivity.getResources().getDrawable(R.drawable.ic_emoticon_feeling_confused));
                feeling_icon.setBackgroundTintList(ColorStateList.valueOf(parentActivity.getResources().getColor(R.color.colorPrimaryDark)));

                break;
        }

    }

    public void animateEmoji() {
        Animation hyperspaceJump = AnimationUtils.loadAnimation(getContext(), R.anim.image_bounce);

        hyperspaceJump.setRepeatMode(Animation.INFINITE);

        emoji_holder_layout.setAnimation(hyperspaceJump);
        //feeling_txt.setAnimation(hyperspaceJump);

    }
}