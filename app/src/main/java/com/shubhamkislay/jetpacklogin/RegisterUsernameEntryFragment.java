package com.shubhamkislay.jetpacklogin;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Interface.UsernameListener;
import com.shubhamkislay.jetpacklogin.Model.User;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUsernameEntryFragment extends Fragment {

    EditText usernameTextView;
    String username ="xyz";
    Button enter_btn;
    ImageView profile_pic_background;
    TextView emailTextView, nameTextView;
    String email, name, photourl;
    UsernameListener usernameListener;
    Button intent_change_btn;
    DatabaseReference reference;
    String device_token;
    String public_key;
    String publickeyid;
    Button progressBackgrounnd, username_found;
    ProgressBar progressbar;
    CustomImageView profile_image;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    private static final int RC_SIGN_IN = 1;

    public RegisterUsernameEntryFragment() {
        // Required empty public constructor
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_register_username_entry, container, false);

        profile_pic_background = view.findViewById(R.id.profile_pic_background);

        emailTextView = view.findViewById(R.id.email);

        intent_change_btn = view.findViewById(R.id.intent_change_btn);

       // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        profile_image = view.findViewById(R.id.profile_image);

        progressBackgrounnd = view.findViewById(R.id.progress_back);
        progressbar = view.findViewById(R.id.progressbar);


        enter_btn = view.findViewById(R.id.enter_btn);

        nameTextView = view.findViewById(R.id.name);

        usernameTextView = view.findViewById(R.id.username);

        username_found = view.findViewById(R.id.username_found);


        intent_change_btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    intent_change_btn.animate().scaleX(0.76f).scaleY(0.76f).setDuration(0);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    intent_change_btn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                else
                {
                    intent_change_btn.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                return false;
            }
        });



        try {

            emailTextView.setText(email);
            nameTextView.setText(name);
            Glide.with(getActivity()).load(photourl.replace("s96-c", "s384-c")).into(profile_pic_background);
            Glide.with(getActivity()).load(photourl.replace("s96-c", "s384-c")).into(profile_image);

/*            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            *//*DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());*//*


            editor.putString(USER_ID,FirebaseAuth.getInstance().getCurrentUser().getUid());
            editor.putString(PHOTO_URL,photourl);


            editor.apply();*/

        }
        catch (Exception e)
        {
            //
        }



        setUpListener();


        return view;
    }

    private void setUpListener() {

        intent_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameListener.onUsernameListener(username,photourl, reference,device_token,public_key,publickeyid);
            }
        });

        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBackgrounnd.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.VISIBLE);
                username = usernameTextView.getText().toString();

                if(TextUtils.isEmpty(username)||username.length()<6/*||username.startsWith("[0-9]")*/)
                {
                    usernameTextView.setError("Username should have alteast 6 characters, and should start with alphabets");
                    progressBackgrounnd.setVisibility(View.INVISIBLE);
                    progressbar.setVisibility(View.INVISIBLE);
                }
                else
                {
                    DatabaseReference findUsernameRef = FirebaseDatabase.getInstance().getReference("Users");
/*                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).orderByChild("username")
                            .startAt(username)
                            .endAt(username + "\uf8ff");*/
                          //  .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    findUsernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                int count=0;
                                for(DataSnapshot snapshot:dataSnapshot.getChildren())
                                {
                                    User user = snapshot.getValue(User.class);
                                    if(user.getUsername().equals(username))
                                    {
                                        count+=1;
                                        Toast.makeText(getContext(),"Username unvailable",Toast.LENGTH_SHORT).show();
                                        progressbar.setVisibility(View.INVISIBLE);
                                        progressBackgrounnd.setVisibility(View.INVISIBLE);

                                        break;
                                       // intent_change_btn.setVisibility(View.VISIBLE);

                                    }


                                }
                                if(count==0)
                                {
                                   // Toast.makeText(getContext(),"Username available",Toast.LENGTH_SHORT).show();

                                     intent_change_btn.setVisibility(View.VISIBLE);
                                    progressBackgrounnd.setVisibility(View.VISIBLE);
                                    progressbar.setVisibility(View.INVISIBLE);
                                    username_found.setVisibility(View.VISIBLE);
                                }
                              //  usernameTextView.set

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressBackgrounnd.setVisibility(View.INVISIBLE);
                            progressbar.setVisibility(View.INVISIBLE);

                        }
                    });
                }
            }
        });

        usernameTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                progressBackgrounnd.setVisibility(View.INVISIBLE);
                progressbar.setVisibility(View.INVISIBLE);
                username_found.setVisibility(View.INVISIBLE);
                intent_change_btn.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void setUserData(String email, String name, String photourl, UsernameListener usernameListener,final DatabaseReference reference,final  String device_token,final String public_key,final String publickeyid)
    {
        this.email = email;
        this.name = name;
        this.photourl = photourl;
        this.usernameListener = usernameListener;
        this.reference = reference;
        this.device_token = device_token;
        this.public_key = public_key;
        this.publickeyid = publickeyid;
    }

}
