package com.hieeway.hieeway.Fragments;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.CustomImageView;
import com.hieeway.hieeway.Interface.ImageSelectionCropListener;
import com.hieeway.hieeway.Interface.UsernameListener;
import com.hieeway.hieeway.Model.User;
import com.hieeway.hieeway.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterUsernameEntryFragment extends Fragment {

    EditText usernameTextView;
    String username ="xyz";
    Button enter_btn;
    ImageView profile_pic_background;
    TextView emailTextView, nameTextView, edit_pic_text;
    String email, name, photourl;
    UsernameListener usernameListener;
    Button intent_change_btn;
    DatabaseReference reference;
    String device_token;
    String public_key;
    String publickeyid;
    ProgressBar progress_upload;
    RelativeLayout edit_pic_layout;
    public static final int PERMISSION_PICK_IMAGE = 1000;
    ProgressBar progressbar;
    CustomImageView profile_image, profile_image_back;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String PRIVATE_KEY = "privateKey";
    public static final String PUBLIC_KEY = "publicKey";
    public static final String PUBLIC_KEY_ID = "publicKeyID";
    public static final String USER_ID = "userid";
    public static final String PHOTO_URL = "photourl";
    public static final String EMAIL = "email";
    public static final String NAME = "name";
    private static final int RC_SIGN_IN = 1;
    Button progressBackgrounnd, username_found, edit_image;
    ImageSelectionCropListener imageSelectionCropListener;

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

        edit_image = view.findViewById(R.id.edit_image);

        intent_change_btn = view.findViewById(R.id.intent_change_btn);

       // edit_pic_layout = view.findViewById(R.id.edit_pic_layout);

        progress_upload = view.findViewById(R.id.progress_upload);

        profile_image_back = view.findViewById(R.id.profile_image_back);

       // getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        profile_image = view.findViewById(R.id.profile_image);

        progressBackgrounnd = view.findViewById(R.id.progress_back);
        progressbar = view.findViewById(R.id.progressbar);


        enter_btn = view.findViewById(R.id.enter_btn);

        nameTextView = view.findViewById(R.id.name);

        usernameTextView = view.findViewById(R.id.username);

        username_found = view.findViewById(R.id.username_found);

      //  edit_pic_text = view.findViewById(R.id.edit_pic_text);


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

        edit_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startCrop(photourl.replace("s96-c", "s384-c"));




                /*Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");

                startActivityForResult(intent, PERMISSION_PICK_IMAGE);*/

                imageSelectionCropListener.imageSelect();


            }
        });

        /*edit_pic_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelectionCropListener.imageSelect();
            }
        });*/

        /*edit_pic_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageSelectionCropListener.imageSelect();
            }
        });*/

        /*edit_pic_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    edit_pic_layout.animate().scaleX(0.76f).scaleY(0.76f).setDuration(0);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    edit_pic_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                else
                {
                    edit_pic_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                return false;
            }
        });*/

        /*edit_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    edit_pic_layout.animate().scaleX(0.76f).scaleY(0.76f).setDuration(0);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    edit_pic_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                else
                {
                    edit_pic_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                return false;
            }
        });*/

        /*edit_pic_text.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    edit_pic_layout.animate().scaleX(0.76f).scaleY(0.76f).setDuration(0);

                }
                else if(event.getAction() == MotionEvent.ACTION_UP)
                {
                    edit_pic_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                else
                {
                    edit_pic_layout.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300);

                }
                return false;
            }
        });*/



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

    public void setImageSelectionCropListener(ImageSelectionCropListener imageSelectionCropListener)
    {
        this.imageSelectionCropListener = imageSelectionCropListener;
    }

    private void setUpListener() {

        intent_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameListener.onUsernameListener(username,name,photourl, reference,device_token,public_key,publickeyid);
            }
        });

        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressBackgrounnd.setVisibility(View.VISIBLE);
                progressbar.setVisibility(View.VISIBLE);
                enter_btn.setVisibility(View.INVISIBLE);
                username = usernameTextView.getText().toString().toLowerCase().trim();


                String usernameFilter = filterUsername(username);

                Boolean usernameAlphaNumeric = isAlphanumeric2(usernameFilter);


                if(TextUtils.isEmpty(username)||username.length()<5||username.length()>15||!usernameAlphaNumeric/*&&!username.contains("_")*/||username.contains(" ")/*||username.startsWith("[0-9]")*/)
                {

                    usernameTextView.setError("Username should have 5-15 characters.\nIt should contain numbers [0-9] or alphabets[A-Z,a-z], or underscore [ _ ].\nIt can't have spaces in between.");
                    progressBackgrounnd.setVisibility(View.INVISIBLE);
                    progressbar.setVisibility(View.INVISIBLE);
                    enter_btn.setVisibility(View.VISIBLE);
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
                                        Toast.makeText(getContext(),"Username unavailable",Toast.LENGTH_SHORT).show();
                                        progressBackgrounnd.setVisibility(View.INVISIBLE);
                                        progressbar.setVisibility(View.INVISIBLE);
                                        enter_btn.setVisibility(View.VISIBLE);

                                        break;
                                       // intent_change_btn.setVisibility(View.VISIBLE);
                                    }


                                }
                                if(count==0)
                                {
                                   // Toast.makeText(getContext(),"Username available",Toast.LENGTH_SHORT).show();

                                     intent_change_btn.setVisibility(View.VISIBLE);
                                    progressBackgrounnd.setVisibility(View.INVISIBLE);
                                    progressbar.setVisibility(View.INVISIBLE);
                                    username_found.setVisibility(View.VISIBLE);
                                    enter_btn.setVisibility(View.INVISIBLE);

                                }
                              //  usernameTextView.set

                            } else {
                                intent_change_btn.setVisibility(View.VISIBLE);
                                progressBackgrounnd.setVisibility(View.INVISIBLE);
                                progressbar.setVisibility(View.INVISIBLE);
                                username_found.setVisibility(View.VISIBLE);
                                enter_btn.setVisibility(View.INVISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressBackgrounnd.setVisibility(View.INVISIBLE);
                            progressbar.setVisibility(View.INVISIBLE);
                            enter_btn.setVisibility(View.VISIBLE);

                        }
                    });
                }

                /*DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                Query query = rootRef.child("Users").orderByChild("username").equalTo(username);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists())
                        {
                            Toast.makeText(getContext(),"Username unavailable",Toast.LENGTH_SHORT).show();
                            progressBackgrounnd.setVisibility(View.INVISIBLE);
                            progressbar.setVisibility(View.INVISIBLE);
                            enter_btn.setVisibility(View.VISIBLE);

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                            */

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
                enter_btn.setVisibility(View.VISIBLE);
                intent_change_btn.setVisibility(View.INVISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public boolean isAlphanumeric2(String str) {
        for (int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if (c < 0x30 || (c >= 0x3a && c <= 0x40) || (c > 0x5a && c <= 0x60) || c > 0x7a )
                return false;
        }
        return true;
    }

    public String filterUsername(String usernameFilter)
    {
        /*if(username.contains("_"))
        {
            String filteredusername;
            int index = username.indexOf("_");
            filteredusername = charRemoveAt(username,index);

            while(filteredusername.contains("_"))
            {
                filterUsername(filteredusername);
            }
        }*/

        while(usernameFilter.contains("_"))
        {
            int index = usernameFilter.indexOf("_");
            usernameFilter = charRemoveAt(usernameFilter,index);
        }
        return usernameFilter;
    }

    public  String charRemoveAt(String str, int p) {
        return str.substring(0, p) + str.substring(p + 1);
    }

 /*   private void startCrop(Uri myUri) {



        Uri uri = myUri;


        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();


        UCrop uCrop = UCrop.of(uri,Uri.fromFile( new File(getActivity().getCacheDir(),destinationFileName)));

        *//*UCropActivity uCropActivity = new UCropActivity();

        uCropActivity.setTheme(R.style.AppTheme);
*//*






        *//**
         * Use the code below for a square image selection i.e. for profile pictures
         * uCrop.withAspectRatio(1.0f, 1.0f);**//*

        uCrop.start(getActivity());

    }*/



/*    private void handleCropError(Intent data) {

        final Throwable cropError = UCrop.getError(data);

        if(cropError!=null)
        {
            Toast.makeText(getActivity(), ""+cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Unexpected Error", Toast.LENGTH_SHORT).show();
        }


    }

    private void handleCropResult(Intent data) {

        final Uri resultUri = UCrop.getOutput(data);
        if(resultUri != null)
        {
            setImageUri(resultUri);

            Toast.makeText(getActivity(), "Image cropped",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getActivity(), "Cannot retrieve crop image",Toast.LENGTH_SHORT).show();
        }

    }*/

    public void setImageUri(Uri resultUri)
    {

        profile_pic_background.setImageURI(resultUri);
        profile_image.setImageURI(resultUri);
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

    public void setUploadedImage(String photourl)
    {
        this.photourl = photourl;
    }
    public void setProgressVisibility(Boolean visibility)
    {
        if(visibility)
        {
            progress_upload.setVisibility(View.VISIBLE);
            profile_image_back.setVisibility(View.VISIBLE);
            profile_pic_background.setAlpha(0.0f);
            intent_change_btn.setAlpha(0.0f);
            intent_change_btn.setEnabled(false);
        }
        else
        {
            progress_upload.setVisibility(View.INVISIBLE);
            profile_image_back.setVisibility(View.INVISIBLE);
            profile_pic_background.setAlpha(0.8f);
            intent_change_btn.setAlpha(1.0f);
            intent_change_btn.setEnabled(true);
        }

    }

}
