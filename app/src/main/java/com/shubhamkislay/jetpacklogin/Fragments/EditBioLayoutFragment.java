package com.shubhamkislay.jetpacklogin.Fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shubhamkislay.jetpacklogin.Interface.EditBioFragmentListener;
import com.shubhamkislay.jetpacklogin.R;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditBioLayoutFragment extends Fragment {

    TextView edit_text_bio, cancel_edit, confirm_edit;
    EditBioFragmentListener editBioFragmentListener;
    String Bio="";


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

        edit_text_bio.setText(Bio.replaceAll("(?m)^[ \t]*\r?\n", ""));


        return view;
    }

    public void setCurrentBio(EditBioFragmentListener editBioFragmentListener,String Bio)
    {
        this.editBioFragmentListener = editBioFragmentListener;
        this.Bio = Bio;

    }

}
