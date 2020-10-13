package com.hieeway.hieeway.Fragments;

import android.app.Activity;

import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.PeopleAdapter;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.PeopleFragmentViewModel;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class PeopleFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private PeopleFragmentViewModel peopleFragmentViewModel;
    TextView username, result_text;
    RecyclerView peopleRecyclerView;
    Activity activity;
    PeopleAdapter peopleAdapter;
    EditText searchPeople;
    ImageView progress_menu_logo;
    public String searchingUsername;
    ProgressBar progressBar, progressBarTwo;
    private List<User> userlist;
    TextView logo_title;
    private View view;

    public PeopleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view != null)
            return view;

        else {

            view = inflater.inflate(R.layout.fragment_people, container, false);
            username = view.findViewById(R.id.username);
            peopleRecyclerView = view.findViewById(R.id.peoplerecycler);
            userlist = new ArrayList<>();

            activity = getActivity();

            progressBar = view.findViewById(R.id.progress);
            progressBarTwo = view.findViewById(R.id.progressTwo);

            searchPeople = view.findViewById(R.id.search_people);

            searchPeople.setImeOptions(EditorInfo.IME_ACTION_DONE);

            searchPeople.setRawInputType(InputType.TYPE_CLASS_TEXT);
            logo_title = view.findViewById(R.id.layout_title);


            Window window = getActivity().getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
            window.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.background_theme));

            result_text = view.findViewById(R.id.result_text);
            result_text.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));

            progress_menu_logo = view.findViewById(R.id.progress_menu_logo);

            logo_title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));


            peopleRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            peopleRecyclerView.setHasFixedSize(true);

            peopleAdapter = new PeopleAdapter(getContext(), userlist, activity);
            peopleRecyclerView.setAdapter(peopleAdapter);

/*        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                try {
                    peopleFragmentViewModel = ViewModelProviders.of(getActivity()).get(PeopleFragmentViewModel.class);
                    peopleFragmentViewModel.getAllUser().observe(getViewLifecycleOwner(), new Observer<List<User>>() {
                        @Override
                        public void onChanged(@Nullable List<User> users) {
                            //peopleAdapter.setList(users);

                            progressBar.setVisibility(View.INVISIBLE);
                            progressBarTwo.setVisibility(View.INVISIBLE);
                            result_text.setVisibility(View.VISIBLE);

                        }
                    });
                }catch (Exception e)
                {

                }
            }
        },250);*/
            showSoftKeyboard(searchPeople);

            //searchPeople.requestFocus();

            searchPeople.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
/*                userlist.clear();
                peopleAdapter.setList(userlist);
                peopleAdapter.notifyDataSetChanged();

                if(s.length()>0)
                    searchUsers(s.toString().toLowerCase());*/
                    userlist.clear();
                    peopleAdapter.setList(userlist);

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

/*                userlist.clear();
                peopleAdapter.setList(userlist);
                peopleAdapter.notifyDataSetChanged();*/

                    if (s.length() > 0) {
                        searchUsers(s.toString().toLowerCase());

                        searchingUsername = s.toString();
                    }
                }


                @Override
                public void afterTextChanged(Editable s) {
/*                userlist.clear();
                peopleAdapter.setList(userlist);
                peopleAdapter.notifyDataSetChanged();

                if(s.length()>0)
                    searchUsers(s.toString().toLowerCase());*/

                    userlist.clear();
                    peopleAdapter.setList(userlist);


                }
            });


            //listenToSearchInputs();


            animateMenuImage(progress_menu_logo);


            return view;
        }

    }

    private void listenToSearchInputs() {




    }

    private void searchUsers(String username) {

        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("username")
                .startAt(username)
                .endAt(username+"\uf8ff");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if(dataSnapshot.exists())
                {
                    userlist.clear();

                    for(DataSnapshot dataSnapshot1: dataSnapshot.getChildren())
                    {

                        User user = dataSnapshot1.getValue(User.class);
                        if (!user.getUserid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            try {
                                if (!user.getCloud())
                                    userlist.add(user);
                            } catch (Exception e) {
                                userlist.add(user);
                            }
                        }

                    }

                    /*peopleAdapter.setList(userlist);*/
                    peopleAdapter.notifyDataSetChanged();
                    if(userlist.size()>0)
                    {
                        result_text.setVisibility(View.GONE);
                        progress_menu_logo.setVisibility(View.GONE);
                    }
                    else
                    {

                        result_text.setVisibility(View.VISIBLE);
                        progress_menu_logo.setVisibility(View.VISIBLE);
                        result_text.setText("No results found for '"+searchingUsername+"'");
                    }


                }
                else
                {
                    result_text.setVisibility(View.VISIBLE);
                    progress_menu_logo.setVisibility(View.VISIBLE);
                    result_text.setText("No results found for '"+searchingUsername+"'");
                    /*userlist.clear();
                    peopleAdapter.setList(userlist);
                    peopleAdapter.notifyDataSetChanged();*/
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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



        Animation hyperspaceJumpImg = AnimationUtils.loadAnimation(getContext(), R.anim.enter_right_to_left);

        hyperspaceJumpImg.setRepeatMode(Animation.INFINITE);

        imageView.setAnimation(hyperspaceJumpImg);
    }


    @Override
    public void onPause() {
        //searchPeople.setText("");
        super.onPause();
    }

}
