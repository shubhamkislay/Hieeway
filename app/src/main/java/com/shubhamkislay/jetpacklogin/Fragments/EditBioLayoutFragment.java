package com.shubhamkislay.jetpacklogin.Fragments;


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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubhamkislay.jetpacklogin.Adapters.EmojiAdapter;
import com.shubhamkislay.jetpacklogin.Adapters.EmojiDisplayAdapter;
import com.shubhamkislay.jetpacklogin.Interface.EditBioFragmentListener;
import com.shubhamkislay.jetpacklogin.Model.EmojiList;
import com.shubhamkislay.jetpacklogin.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ja.burhanrashid52.photoeditor.PhotoEditor;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBioLayoutFragment extends Fragment implements EmojiDisplayAdapter.EmojiAdapterListener {

    TextView edit_text_bio, cancel_edit, confirm_edit, title;
    EditBioFragmentListener editBioFragmentListener;
    String Bio="";
    RecyclerView recycler_emoji;
    List<String> emojiList;
    Button open_emojis;
    RelativeLayout bio_layout;


    public EditBioLayoutFragment() {
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
        final View view = inflater.inflate(R.layout.fragment_edit_bio_layout, container, false);

        edit_text_bio = view.findViewById(R.id.edit_text_bio);
        cancel_edit = view.findViewById(R.id.cancel_edit);
        confirm_edit = view.findViewById(R.id.confirm_edit);

        edit_text_bio.append("\ud83d\ude01");

        recycler_emoji = view.findViewById(R.id.emojiRecyclerView);

        recycler_emoji.setHasFixedSize(true);

        recycler_emoji.setLayoutManager(new GridLayoutManager(getActivity(), 5));

        emojiList = new ArrayList<>();

        open_emojis = view.findViewById(R.id.open_emojis);

        bio_layout = view.findViewById(R.id.bio_layout);

        title = view.findViewById(R.id.title);

        title.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/samsungsharpsans-bold.otf"));







      /*  EmojiDisplayAdapter adapter = new EmojiDisplayAdapter(getContext(), emojiList,this);
        recycler_emoji.setAdapter(adapter);*/
        EmojiList emojiList = new EmojiList();
        // final EmojiDisplayAdapter adapter = new EmojiDisplayAdapter(getContext(), PhotoEditor.getEmojis(getContext()), this);

        final EmojiDisplayAdapter adapter = new EmojiDisplayAdapter(getContext(), emojiList.getEmojiList(), this);


        open_emojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                recycler_emoji.setAdapter(adapter);

                recycler_emoji.setVisibility(View.VISIBLE);
                // bio_layout.setVisibility(View.GONE);
            }
        });

        confirm_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                HashMap<String,Object> hashMap = new HashMap<>();

                String editedBio = edit_text_bio.getText().toString().replaceAll("(?m)^[ \t]*\r?\n", "");



                hashMap.put("bio",editedBio);

                databaseReference.updateChildren(hashMap);

                hideKeyboardFrom(getContext(),view);

                editBioFragmentListener.setEditBioChange(true,Bio);
            }
        });


        cancel_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editBioFragmentListener.setEditBioChange(true,Bio);
            }
        });

        // Bio+=" "+getEmojiByUnicode(0x1F680);

        edit_text_bio.setText(Bio.replaceAll("(?m)^[ \t]*\r?\n", ""));


        return view;
    }

    public void setCurrentBio(EditBioFragmentListener editBioFragmentListener,String Bio)
    {
        this.editBioFragmentListener = editBioFragmentListener;
        this.Bio = Bio;

    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    @Override
    public void onEmojiItemSelected(String emoji) {

        recycler_emoji.setVisibility(View.GONE);
        bio_layout.setVisibility(View.VISIBLE);

        open_emojis.setText(emoji);

    }
}
