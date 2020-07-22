package com.hieeway.hieeway.Fragments;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.ChatMessageAdapter;
import com.hieeway.hieeway.ChatsFragmentViewModel;
import com.hieeway.hieeway.EphemeralMessageViewModel;
import com.hieeway.hieeway.GridSpacingItemDecoration;
import com.hieeway.hieeway.Interface.AnimationArrowListener;
import com.hieeway.hieeway.Interface.ChatStampSizeListener;
import com.hieeway.hieeway.Interface.DeleteOptionsListener;
import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.Music;
import com.hieeway.hieeway.MusicFeedActivity;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SharedViewModel;
import com.hieeway.hieeway.UserPicViewModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ChatsFragment extends Fragment implements DeleteOptionsListener{

    private SharedViewModel sharedViewModel;
    private ChatsFragmentViewModel chatsFragmentViewModel;
    public static final String CHECKED_TIMESTAMP = "checked_timestamp";
    private UserPicViewModel userPicViewModel;
    private TextView email, logo_title;
    private RecyclerView chats_recyclerview;
    private EphemeralMessageViewModel ephemeralMessageViewModel;
    private Activity activity;
    private ProgressBar progressBar, progressTwo;
    private Button button, close_search, search_chat_btn, search_chat_btn_back;
    private RelativeLayout search_btn_layout, search_bar_layout;
    private TextView noTextBack, appTitle;
    private EditText search_bar;
    private ImageView progress_menu_logo;
    private String original;


    private Boolean searchBtnActive = true;
    public static final String SHARED_PREFS = "sharedPrefs";
    private List<Music> userList;
    private int sentListSize;

    private Context context;

    private Thread searchUserThread;
    public Dialog d;
    private RelativeLayout bottom_delete_options;
    private RecyclerView.ViewHolder viewHolder;
    private BottomSheetBehavior bottomSheetBehavior;
    private int position;
    private ChatStamp chatStamp;
    private List<ChatStamp> mChatStamps;
    private TextView deleteForMe, deleteForAll, optionsTag, deleteForMeDesc, deleteForAllDesc;
    private DeleteOptionsListener deleteOptionsListener;
    private ChatStampSizeListener chatStampSizeListener;


    private List<String> usersIdList = new ArrayList<>();
    private ChatMessageAdapter chatMessageAdapter;
    private List<ChatStamp> chatStampsList;


    private int chatStampSize =0;
    private List<ChatStamp> chatStampsListtwo;
    private RelativeLayout delete_for_all_option_layout, delete_for_me_option_layout;
    private AnimationArrowListener animationArrowListener;
    private List<ChatStamp> resetList;
    private Boolean searchedList = false;
    private ImageButton spotify_status, spotify_status_back;
    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_chat_collapse_layout, container, false);

        // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        spotify_status_back = view.findViewById(R.id.spotify_status_back);


        email = view.findViewById(R.id.email);
        logo_title = view.findViewById(R.id.logo_title);

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
                    window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.nav_status_color_accent));
                } catch (Exception e) {
                    //
                }

            }
        }, 0);


        userList = new ArrayList<>();


        //  username = view.findViewById(R.id.username);
        chats_recyclerview = view.findViewById(R.id.chats_recyclerview);
        chatStampsList = new ArrayList<>();
        resetList = new ArrayList<>();

        chatStampsListtwo = new ArrayList<>();
        spotify_status = view.findViewById(R.id.spotify_status);


        // search_btn_layout = view.findViewById(R.id.search_btn_layout);
        search_bar_layout = view.findViewById(R.id.search_bar_layout);
        progress_menu_logo = view.findViewById(R.id.progress_menu_logo);

        search_bar = view.findViewById(R.id.search_bar);

        bottom_delete_options = view.findViewById(R.id.bottom_delete_options);


        search_bar.setImeOptions(EditorInfo.IME_ACTION_DONE);

        search_bar.setRawInputType(InputType.TYPE_CLASS_TEXT);


        deleteForMe = view.findViewById(R.id.delete_for_me_option);

        deleteForAll = view.findViewById(R.id.delete_for_all_option);

        optionsTag = view.findViewById(R.id.options_txt);

        deleteForMeDesc = view.findViewById(R.id.delete_for_me_des);
        deleteForAllDesc = view.findViewById(R.id.delete_for_all_des);

        delete_for_all_option_layout = view.findViewById(R.id.delete_for_all_option_layout);

        delete_for_me_option_layout = view.findViewById(R.id.delete_for_me_option_layout);

        bottomSheetBehavior = BottomSheetBehavior.from(bottom_delete_options);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }

            @Override
            public void onSlide(View bottomSheet, float slideOffset) {
            }
        });


        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                // if(s.length()>0)
                //  searchChat(s.toString());

                if (s.toString().length() > 0)
                    searchUsername(s.toString());


                else {
                    /**
                     * Uncomment
                     */
                    if (before > 0)
                        resetChatList();
                }


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        search_chat_btn = view.findViewById(R.id.search_chat_btn);


        close_search = view.findViewById(R.id.close_search);


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

                    search_chat_btn.setBackground(getActivity().getDrawable(R.drawable.ic_keyboard_arrow_left_white_24dp));
                    search_bar.requestFocus();


                    // search_bar.setFocusable(true);
                    // search_bar.setSelected(true);
                    // search_btn_layout.setVisibility(View.GONE);

                    getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

                } else {
                    searchBtnActive = true;

                    appTitle.setVisibility(View.VISIBLE);

                    hideSoftKeyboard(search_bar);
                    search_bar.setText("");

                    /*chatMessageAdapter = new ChatMessageAdapter(getContext(), resetList, activity*//*,ChatsFragment.this*//*);
                    chats_recyclerview.setAdapter(chatMessageAdapter);*/
                    //chatMessageAdapter.notifyDataSetChanged();

                    search_bar.setVisibility(View.GONE);

                    search_chat_btn.setBackground(getActivity().getDrawable(R.drawable.ic_search_black_24dp));

                }


            }
        });


        spotify_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //spotify_status_back.setVisibility(View.GONE);
                startActivity(new Intent(getActivity(), MusicFeedActivity.class));
            }
        });


        activity = getActivity();
        noTextBack = view.findViewById(R.id.no_items_text);

        progressBar = view.findViewById(R.id.progress);
        progressTwo = view.findViewById(R.id.progressTwo);

        appTitle = view.findViewById(R.id.app_title);

        try {
            appTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
            logo_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
            noTextBack.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        }catch (NullPointerException e) {
            //
        }


        button = view.findViewById(R.id.refresh);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;
        float displayHeight = size.y;

        // Toast.makeText(getContext(),"Height: "+displayHeight,Toast.LENGTH_LONG).show();


        int spanCount; // 3 columns
        int spacing = 0; // 50px
        boolean includeEdge = true;

        if(displayWidth>=1920)
            spanCount = 2;

        else if(displayWidth>=1080)
            spanCount = 2;

        else if(displayWidth>=500)
            spanCount=2;
        else
            spanCount=1;


        search_bar.getLayoutParams().width = (int) displayWidth;


        // staggeredGridLayoutManager = new StaggeredGridLayoutManager(/*spanCount*/2,LinearLayoutManager.VERTICAL);
        LinearLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),spanCount);


        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        chats_recyclerview.setLayoutManager(gridLayoutManager);
        chats_recyclerview.setHasFixedSize(true);
        chats_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        RecyclerView.ItemAnimator animator = chats_recyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }


        chats_recyclerview.setItemViewCacheSize(20);
        chats_recyclerview.setDrawingCacheEnabled(true);
        chats_recyclerview.setItemAnimator(new DefaultItemAnimator());
        chats_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatMessageAdapter.notifyDataSetChanged();
            }
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {

                    chatsFragmentViewModel = ViewModelProviders.of(getActivity()).get(ChatsFragmentViewModel.class);
                    chatsFragmentViewModel.getAllUser().observe(getViewLifecycleOwner(), new Observer<List<ChatStamp>>() {
                        @Override
                        public void onChanged(@Nullable final List<ChatStamp> chatStamps) {

                            initialChatstampPopulateThread(chatStamps);

                        }
                    });
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Handler Error: " + e.toString(), Toast.LENGTH_SHORT).show();

                }
            }
        },350);


        return view;
    }

    private void initialChatstampPopulateThread(List<ChatStamp> chatStamps) {


        TaskCompletionSource<List<ChatStamp>> initialListtaskSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {

                Collections.sort(chatStamps, Collections.<ChatStamp>reverseOrder());

                initialListtaskSource.setResult(chatStamps);


            }
        }).start();


        Task<List<ChatStamp>> initialListtask = initialListtaskSource.getTask();

        initialListtask.addOnCompleteListener(new OnCompleteListener<List<ChatStamp>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatStamp>> task) {

                if (task.isSuccessful()) {
                    resetList = task.getResult();

                    chatStampsList = task.getResult();
                    chatStampsListtwo = task.getResult();
                    if (chatStampsList.size() <= 0) {
                        noTextBack.setVisibility(View.VISIBLE);
                    } else
                        noTextBack.setVisibility(View.GONE);

                    progress_menu_logo.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    progressTwo.setVisibility(View.GONE);


                    if (chatStampSize <= chatStampsList.size()) {
                        chatMessageAdapter = new ChatMessageAdapter(getContext(), chatStampsList, activity/*,ChatsFragment.this*/);
                        chatMessageAdapter.setHasStableIds(true);
                        chats_recyclerview.setAdapter(chatMessageAdapter);
                        chatMessageAdapter.notifyDataSetChanged();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                    animationArrowListener.playArrowAnimation();

                                } catch (Exception e) {

                                    Toast.makeText(getContext(), "Thread Error: " + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, 500);


                        //chatStampSize = chatStamps.size();
                    } else {
                        chatStampSize = chatStampsList.size();
                    }


                } else {

                }

            }
        });

    }

    private void resetChatList() {


                FirebaseDatabase.getInstance().getReference("ChatList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                resetChatListThread(dataSnapshot);
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


    }

    private void resetChatListThread(DataSnapshot dataSnapshot) {

        TaskCompletionSource<List<ChatStamp>> reseltThreadListSource = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()) {
                    chatStampsList.clear();
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {*/

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        ChatStamp chatStamp = dataSnapshot1.getValue(ChatStamp.class);
                        //if(!user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))

                        try {
                            if (!chatStamp.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                                chatStampsList.add(chatStamp);

                        } catch (Exception e) {

                        }


                        //chatStampsList.add(chatStamp);

                    }

                    /*peopleAdapter.setList(userlist);*/
                    // if(username.length()>0)
                    Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());


                    reseltThreadListSource.setResult(chatStampsList);

                } else {
                    reseltThreadListSource.setException(new NullPointerException());
                }
            }
        }).start();


        Task<List<ChatStamp>> listTask = reseltThreadListSource.getTask();

        listTask.addOnCompleteListener(new OnCompleteListener<List<ChatStamp>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatStamp>> task) {
                if (task.isSuccessful()) {
                    chatMessageAdapter = new ChatMessageAdapter(getContext(), task.getResult(), activity/*,ChatsFragment.this*/);
                    chats_recyclerview.setAdapter(chatMessageAdapter);
                    chatMessageAdapter.notifyDataSetChanged();
                } else {
                    //Toast.makeText(getContext(), "Result Error", Toast.LENGTH_SHORT).show();
                    chatMessageAdapter = new ChatMessageAdapter(getContext(), chatStampsList, activity/*,ChatsFragment.this*/);
                    chats_recyclerview.setAdapter(chatMessageAdapter);
                    chatMessageAdapter.notifyDataSetChanged();
                }
            }
        });


    }



    private void populateMusicList() {


        FirebaseDatabase.getInstance().getReference("FriendList")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                TaskCompletionSource<List<Music>> musicTaskCompletionSource = new TaskCompletionSource<>();


                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        {
                                            userList.clear();
                                            if (dataSnapshot.exists()) {
                                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                    Friend friend = snapshot.getValue(Friend.class);
                                                    if (friend.getStatus().equals("friends")) {

                                                        FirebaseDatabase.getInstance().getReference("Music")
                                                                .child(friend.getFriendId())
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(@NonNull DataSnapshot musicSnapshot) {
                                                                        if (musicSnapshot.exists()) {

                                                                            try {
                                                                                Music music = musicSnapshot.getValue(Music.class);


                                                                                if (!userList.contains(music)) {
                                                                                    userList.add(music);
                                                                                } else {
                                                                                    userList.remove(music);
                                                                                    userList.add(music);
                                                                                }


                                                                                if (searchedList) {
                                                                                    if (sentListSize < userList.size()) {
                                                                                        try {

                                                                                            Collections.sort(userList, Collections.<Music>reverseOrder());

                                                                                            musicTaskCompletionSource.setResult(userList);


                                                                                        } catch (Exception e) {
                                                                                            //
                                                                                        }
                                                                                    }
                                                                                    if (userList.size() < 1) {
                                                                                        spotify_status_back.setVisibility(View.GONE);
                                                                                    }

                                                                                }


                                                                            } catch (Exception e) {
                                                                                //
                                                                            }


                                                                        }


                                                                    }

                                                                    @Override
                                                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                    }
                                                                });

                                    /*FirebaseDatabase.getInstance().getReference("Music")
                                            .child(friend.getFriendId())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot musicSnapshot) {
                                                    if (musicSnapshot.exists()) {
                                                        Music music = musicSnapshot.getValue(Music.class);


                                                        if (!userList.contains(music)) {
                                                            userList.add(music);
                                                        } else {
                                                            userList.remove(music);
                                                            userList.add(music);
                                                        }


                                                        if (searchedList && sentListSize < userList.size()) {
                                                            try {

                                                                Collections.sort(userList, Collections.<Music>reverseOrder());

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {

                                                                    }
                                                                }, 500);

                                                            } catch (Exception e) {
                                                                //
                                                            }

                                                        }

                                                    }


                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });*/


                                                    }
                                                }

                                                searchedList = true;
                                                sentListSize = userList.size();


                                            }
                                        }
                                    }
                                }).start();


                                Task<List<Music>> musicListtask = musicTaskCompletionSource.getTask();


                                musicListtask.addOnCompleteListener(new OnCompleteListener<List<Music>>() {
                                    @Override
                                    public void onComplete(@NonNull Task<List<Music>> task) {
                                        if (task.isSuccessful()) {
                                            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);


                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    String checkedTimeStamp = sharedPreferences.getString(CHECKED_TIMESTAMP, "");

                                                    // Toast.makeText(getContext(),"Saved time: "+checkedTimeStamp+"\nNew Time: "+userList.get(0).getTimestamp(),Toast.LENGTH_SHORT).show();


                                                    if (checkedTimeStamp.compareTo(task.getResult().get(0).getTimestamp()) != 0) {
                                                        spotify_status_back.setVisibility(View.VISIBLE);
                                                    } else {
                                                        spotify_status_back.setVisibility(View.GONE);
                                                    }

                                                }
                                            }, 500);


                                        }

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


    }



    public void setChatStampSizeFragment(int chatStampSize)
    {
        this.chatStampSize = chatStampSize;
    }


    @Override
    public void onDeleteForAll(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder) {

        //updateList(position,mChatStampsListSize,viewHolder);
        int pos = viewHolder.getAdapterPosition();

        ChatStamp chatStamp = mChatStamps.get(pos);
        mChatStamps.remove(chatStamp);

        chatMessageAdapter.notifyItemRemoved(pos);

    }

    @Override
    public void onDeleteForMe(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder) {

        //updateList(position,mChatStampsListSize,viewHolder);
        int pos = viewHolder.getAdapterPosition();

        ChatStamp chatStamp = mChatStamps.get(pos);
        mChatStamps.remove(chatStamp);

        chatMessageAdapter.notifyItemRemoved(pos);
    }



    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }


    public void setAnimationArrowListener(AnimationArrowListener animationArrowListener)
    {
        this.animationArrowListener = animationArrowListener;
    }

    private void searchUsername(String username) {

                Query query;
                if (username.length() > 0) {
                    query = FirebaseDatabase.getInstance().getReference("ChatList")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("username")
                            .startAt(username)
                            .endAt(username + "\uf8ff");
                } else {
                    query = FirebaseDatabase.getInstance().getReference("ChatList")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                }

                query.addListenerForSingleValueEvent(

                        new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                                searchUsernameThread(dataSnapshot);

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


    }


    private void searchUsernameThread(DataSnapshot dataSnapshot) {

        TaskCompletionSource<List<ChatStamp>> resultList = new TaskCompletionSource<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (dataSnapshot.exists()) {
                    chatStampsList.clear();
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {*/

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                        ChatStamp chatStamp = dataSnapshot1.getValue(ChatStamp.class);

                        try {
                            // if (!chatStamp.getId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))
                            chatStampsList.add(chatStamp);

                        } catch (Exception e) {

                        }


                        //chatStampsList.add(chatStamp);

                    }

                    /*peopleAdapter.setList(userlist);*/
                    // if(username.length()>0)
                    Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());
                    resultList.setResult(chatStampsList);


                }
            }
        }).start();


        Task<List<ChatStamp>> listTask = resultList.getTask();
        listTask.addOnCompleteListener(new OnCompleteListener<List<ChatStamp>>() {
            @Override
            public void onComplete(@NonNull Task<List<ChatStamp>> task) {

                if (task.isSuccessful()) {
                    chatMessageAdapter.notifyDataSetChanged();
                }

            }
        });


    }

    @Override
    public void onResume() {
        super.onResume();
        /**
         * Uncomment
         */
        populateMusicList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        view = null;
    }


}
