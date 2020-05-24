package com.hieeway.hieeway.Fragments;


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hieeway.hieeway.Adapters.EmojiDisplayAdapter;
import com.hieeway.hieeway.Interface.AddFeelingFragmentListener;
import com.hieeway.hieeway.Model.EmojiList;
import com.hieeway.hieeway.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFeelingFragment extends Fragment implements EmojiDisplayAdapter.EmojiAdapterListener {


    RelativeLayout emoji_holder;
    EditText edit_feeling;
    TextView emoji_icon, title;
    TextView cancel_edit, confirm_edit;
    RecyclerView setEmojirecyclerView;
    Boolean emojiListOpen = false;
    EmojiList emojiList;
    EmojiDisplayAdapter adapter;
    List<String> emptyList;
    AddFeelingFragmentListener addFeelingFragmentListener;

    public AddFeelingFragment() {
        // Required empty public constructor
    }

    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View view = inflater.inflate(R.layout.fragment_add_feeling, container, false);

        emoji_holder = view.findViewById(R.id.emoji_holder);
        edit_feeling = view.findViewById(R.id.edit_feeling);
        emoji_icon = view.findViewById(R.id.emoji_icon);
        setEmojirecyclerView = view.findViewById(R.id.setEmojirecyclerView);

        setEmojirecyclerView.setHasFixedSize(true);
        emptyList = new ArrayList<>();

        title = view.findViewById(R.id.title);

        cancel_edit = view.findViewById(R.id.cancel_edit);
        confirm_edit = view.findViewById(R.id.confirm_edit);


        confirm_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (emoji_icon.getText().toString().length() < 1 && edit_feeling.getText().toString().length() > 0) {
                    Toast.makeText(getContext(), "Please select a feeling emoji", Toast.LENGTH_SHORT).show();
                } else if (emoji_icon.getText().toString().length() > 0 && edit_feeling.getText().toString().length() < 1) {
                    Toast.makeText(getContext(), "Please write how you feel", Toast.LENGTH_SHORT).show();
                } else if (emoji_icon.getText().toString().length() > 0 && edit_feeling.getText().toString().length() > 1) {


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("feelingIcon", emoji_icon.getText().toString());
                    hashMap.put("feeling", edit_feeling.getText().toString());

                    databaseReference.updateChildren(hashMap);
                    hideKeyboardFrom(getContext(), view);

                    addFeelingFragmentListener.setFeelingChange(true, emoji_icon.getText().toString(), edit_feeling.getText().toString());
                } else {
                    hideKeyboardFrom(getContext(), view);
                    addFeelingFragmentListener.setFeelingChange(true, "default", "");
                }
            }
        });

        cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                hideKeyboardFrom(getContext(), view);

                addFeelingFragmentListener.setFeelingChange(true, "default", "");
            }
        });

        title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));

        setEmojirecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        emojiList = new EmojiList();
        // final EmojiDisplayAdapter adapter = new EmojiDisplayAdapter(getContext(), PhotoEditor.getEmojis(getContext()), this);
        //adapter = new EmojiDisplayAdapter(getContext(), emojiList.getEmojiList(), this);


        emoji_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emojiListOpen) {
                    setAdpaterList(true);
                    emojiListOpen = true;
                } else {
                    setAdpaterList(false);
                    emojiListOpen = false;
                }
            }
        });

        emoji_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!emojiListOpen) {
                    setAdpaterList(true);
                    emojiListOpen = true;
                } else {
                    setAdpaterList(false);
                    emojiListOpen = false;
                }
            }
        });


        return view;
    }

    public void setAddFeelingFragmentListener(AddFeelingFragmentListener addFeelingFragmentListener) {
        this.addFeelingFragmentListener = addFeelingFragmentListener;
    }

    @Override
    public void onEmojiItemSelected(String emoji) {

        emoji_icon.setText(emoji);
        emoji_holder.setVisibility(View.GONE);
        setAdpaterList(false);
        emojiListOpen = false;
    }

    public void setAdpaterList(Boolean openEmojiList) {
        if (openEmojiList) {
            adapter = new EmojiDisplayAdapter(getContext(), emojiList.getEmojiList(), this);
            setEmojirecyclerView.setAdapter(adapter);
        } else {
            adapter = new EmojiDisplayAdapter(getContext(), emptyList, this);
            setEmojirecyclerView.setAdapter(adapter);
        }
    }
}
