package com.hieeway.hieeway.Fragments;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.app.Dialog;
import android.content.Context;
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
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SharedViewModel;
import com.hieeway.hieeway.UserPicViewModel;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.utils.FadeViewHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ChatsFragment extends Fragment implements DeleteOptionsListener{

    private SharedViewModel sharedViewModel;
    private ChatsFragmentViewModel chatsFragmentViewModel;
    private UserPicViewModel userPicViewModel;
    TextView email, logo_title;
    RecyclerView chats_recyclerview;
    private EphemeralMessageViewModel ephemeralMessageViewModel;
    Activity activity;
    ProgressBar progressBar, progressTwo;
    Button button, close_search, search_chat_btn, search_chat_btn_back;
    RelativeLayout search_btn_layout, search_bar_layout;
    TextView noTextBack, appTitle;
    EditText search_bar;
    ImageView progress_menu_logo;

    Boolean searchBtnActive = true;

    public Context context;

    Thread searchUserThread;
    public Dialog d;
    RelativeLayout bottom_delete_options;
    RecyclerView.ViewHolder viewHolder;
    BottomSheetBehavior bottomSheetBehavior;
    private int position;
    private ChatStamp chatStamp;
    private List<ChatStamp> mChatStamps;
    private TextView deleteForMe, deleteForAll, optionsTag, deleteForMeDesc, deleteForAllDesc;
    private DeleteOptionsListener deleteOptionsListener;
    private ChatStampSizeListener chatStampSizeListener;


    List<String> usersIdList = new ArrayList<>();
    ChatMessageAdapter chatMessageAdapter;
    private List<ChatStamp> chatStampsList;

    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private int chatStampSize =0;
    private List<ChatStamp> chatStampsListtwo;
    private RelativeLayout delete_for_all_option_layout, delete_for_me_option_layout;
    AnimationArrowListener animationArrowListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_chat_collapse_layout, container, false);

       // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);




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








      //  username = view.findViewById(R.id.username);
        chats_recyclerview = view.findViewById(R.id.chats_recyclerview);
        chatStampsList = new ArrayList<>();

        chatStampsListtwo = new ArrayList<>();



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

                new SearchUserAsyncTask().execute(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });

        search_chat_btn = view.findViewById(R.id.search_chat_btn);
        search_chat_btn_back = view.findViewById(R.id.search_chat_btn_back);

        close_search = view.findViewById(R.id.close_search);

/*        search_chat_btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar_layout.setVisibility(View.VISIBLE);
                appTitle.setVisibility(View.GONE);

                search_bar.setVisibility(View.VISIBLE);
                search_btn_layout.setVisibility(View.GONE);

                showSoftKeyboard(search_bar);

               // search_bar.setFocusable(true);
                search_bar.requestFocus();
               // search_bar.setFocusable(true);
              //  search_bar.setSelected(true);
                getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
        });*/


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

                }
                else
                {
                    searchBtnActive = true;

                    appTitle.setVisibility(View.VISIBLE);

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

                showSoftKeyboard(search_bar);

               // search_bar.setFocusable(true);
                search_bar.requestFocus();
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

                search_bar.setText("");
                search_btn_layout.setVisibility(View.VISIBLE);
               // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

            }
        });*/

        activity = getActivity();
        noTextBack = view.findViewById(R.id.no_items_text);

        progressBar = view.findViewById(R.id.progress);
        progressTwo = view.findViewById(R.id.progressTwo);

        appTitle = view.findViewById(R.id.app_title);

        try {
            appTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
            logo_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
            noTextBack.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));
        }catch (NullPointerException e)
        {
            //
        }





        button = view.findViewById(R.id.refresh);

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


        // staggeredGridLayoutManager = new StaggeredGridLayoutManager(/*spanCount*/2,LinearLayoutManager.VERTICAL);
        LinearLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),spanCount);


        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        chats_recyclerview.setLayoutManager(gridLayoutManager);
        chats_recyclerview.setHasFixedSize(true);
        chats_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

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


/*        ((NavButtonTest)getActivity()).animateArrow();
        progressBar.setVisibility(View.GONE);
        progressTwo.setVisibility(View.GONE);*/

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                try {

                chatsFragmentViewModel = ViewModelProviders.of(getActivity()).get(ChatsFragmentViewModel.class);
                chatsFragmentViewModel.getAllUser().observe(getViewLifecycleOwner(), new Observer<List<ChatStamp>>() {
                    @Override
                    public void onChanged(@Nullable final List<ChatStamp> chatStamps) {
                        Collections.sort(chatStamps, Collections.<ChatStamp>reverseOrder());

                        chatStampsList = chatStamps;
                        chatStampsListtwo = chatStamps;
                        if (chatStampsList.size() <= 0) {
                            noTextBack.setVisibility(View.VISIBLE);
                        } else
                            noTextBack.setVisibility(View.GONE);

                        progress_menu_logo.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        progressTwo.setVisibility(View.GONE);


                        if(chatStampSize<=chatStampsList.size()) {
                            chatMessageAdapter = new ChatMessageAdapter(getContext(), chatStampsList, activity/*,ChatsFragment.this*/);
                            chats_recyclerview.setAdapter(chatMessageAdapter);
                            chatMessageAdapter.notifyDataSetChanged();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        animationArrowListener.playArrowAnimation();
                                    } catch (Exception e) {

                                    }
                                }
                            }, 500);


                            //chatStampSize = chatStamps.size();
                        }
                        else
                        {
                            chatStampSize = chatStampsList.size();
                        }


                    }
                });
            }catch (Exception e)
                {
                    //
                }

            }
        },350);


        //animateMenuImage(progress_menu_logo);


        return view;
    }

    public void setBottomSheetBehavior(MotionEvent event) {
        try {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {

                Rect outRect = new Rect();
                bottom_delete_options.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    //relay.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            //
        }
    }

/*    public void setDeleteOptionsDialog(Context context, ChatStamp chatStamp, int position,
                               List<ChatStamp> mChatStamps, Activity activity, RecyclerView.ViewHolder viewHolder) {

        this.context=context;
        this.chatStamp = chatStamp;
        this.position = position;
        this.mChatStamps = mChatStamps;
        this.activity = activity;
        this.viewHolder = viewHolder;
        chatStampSizeListener = (NavButtonTest) activity;
    }*/


    private void searchChat(final String username) {

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {

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

                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                            if (dataSnapshot.exists()) {
                                chatStampsList.clear();

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    ChatStamp chatStamp = dataSnapshot1.getValue(ChatStamp.class);
                                    //if(!user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))

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

                                chatMessageAdapter.notifyDataSetChanged();


                            }


                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }).start();
        }catch (Exception e)
        {
            //
        }






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

/*    @Override
    public void setDeleteOptionsDialog(Context context, ChatStamp chatStamp, int position, List<ChatStamp> mChatStamps, Activity activity, RecyclerView.ViewHolder viewHolder){


        this.context=context;
        this.chatStamp = chatStamp;
        this.position = position;
        this.mChatStamps = mChatStamps;
        this.activity = activity;
        this.viewHolder = viewHolder;
        chatStampSizeListener = (NavButtonTest) activity;

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

    }*/

    private void updateList(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder)
    {

        int pos = viewHolder.getAdapterPosition();
        ChatStamp chatStamp = chatStampsList.get(position);

        chatStampsList.remove(chatStamp);



       // chatStampSize = mChatStampsListSize;

       // chatMessageAdapter.notifyItemRemoved(pos);

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

    public void animateMenuImage(ImageView imageView)
    {



        Animation hyperspaceJumpImg = AnimationUtils.loadAnimation(getContext(), R.anim.exit_bottom_to_top);

        hyperspaceJumpImg.setRepeatMode(Animation.INFINITE);

        imageView.setAnimation(hyperspaceJumpImg);


    }

    public void setAnimationArrowListener(AnimationArrowListener animationArrowListener)
    {
        this.animationArrowListener = animationArrowListener;
    }

    private  class SearchUserAsyncTask extends AsyncTask<String,Void,List<ChatStamp>>{

        @Override
        protected List<ChatStamp> doInBackground(String... strings) {

            String username = strings[0];

           // List<ChatStamp> stampList = new ArrayList<>();

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

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {


                    if (dataSnapshot.exists()) {
                        chatStampsList.clear();
                        /*new Thread(new Runnable() {
                            @Override
                            public void run() {*/

                                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {

                                    ChatStamp chatStamp = dataSnapshot1.getValue(ChatStamp.class);
                                    //if(!user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid()))

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

                                try {
                                    chatMessageAdapter.notifyDataSetChanged();
                                }catch (Exception e)
                                {
                                    //
                                }

                            }
             //           }).start();





                 //   }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            return chatStampsList;
        }

        @Override
        protected void onPostExecute(List<ChatStamp> chatStampsList) {


           // super.onPostExecute(chatStampsList);
            Collections.sort(chatStampsList, Collections.<ChatStamp>reverseOrder());

            try {
                chatMessageAdapter.notifyDataSetChanged();
            }catch (Exception e)
            {

            }

        }
    }


}
