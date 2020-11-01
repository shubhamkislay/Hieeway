package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.NoRecipientAdapter;
import com.hieeway.hieeway.Adapters.RecipientAdapter;
import com.hieeway.hieeway.Helper.RecipientListHelper;
import com.hieeway.hieeway.Interface.AddRecipientListener;
import com.hieeway.hieeway.Interface.RemoveRecipientListener;
import com.hieeway.hieeway.Model.ChatMessageCompound;
import com.hieeway.hieeway.Model.DatabaseListener;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.MusicPost;
import com.hieeway.hieeway.Model.Post;
import com.hieeway.hieeway.Model.Recipient;
import com.hieeway.hieeway.Model.User;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CreateShotActivity extends AppCompatActivity implements AddRecipientListener, RemoveRecipientListener {

    private TextView no_recipient_txt, recipients_text;
    private Button recipients;
    private RecyclerView recipients_recyclerview, not_recipients_recyclerview;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    final static String HAPPY = "happy";
    final static String BORED = "bored";
    final static String EXCITED = "excited";
    final static String SAD = "sad";
    final static String CONFUSED = "confused";
    final static String ANGRY = "angry";

    public static final String USERNAME = "username";
    private RecipientAdapter recipientAdapter;
    private NoRecipientAdapter noRecipientAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager, staggeredGridLayoutManagerTwo;
    private String userID;
    private SharedPreferences sharedPreferences;
    private List<Recipient> recipientList;
    private List<Recipient> notRecipientList;
    private List<DatabaseListener> databaseListeners;
    private TextView text_msg;
    private EditText message_box;
    private Button sendButton;
    private ImageView send_arrow;
    private String username;
    private String textMessage;
    private Button camera;
    private int totalRecipients;
    private int calRecipients;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shot);

        recipients = findViewById(R.id.recipients);
        no_recipient_txt = findViewById(R.id.no_recipient_txt);
        recipients_text = findViewById(R.id.recipients_text);

        text_msg = findViewById(R.id.text_msg);
        message_box = findViewById(R.id.message_box);
        send_arrow = findViewById(R.id.send_arrow);
        camera = findViewById(R.id.camera);

        databaseListeners = new ArrayList<>();
        recipientList = new ArrayList<>();
        notRecipientList = new ArrayList<>();
        recipients_recyclerview = findViewById(R.id.recipients_recyclerview);
        not_recipients_recyclerview = findViewById(R.id.not_recipients_recyclerview);
        sendButton = findViewById(R.id.send_button);

        recipients.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        recipients_text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_recipient_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");
        username = sharedPreferences.getString(USERNAME, "");

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.darkButtonBackground));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;


        int spanCount; // 3 columns
        int spacing = 0; // 50px
        boolean includeEdge = true;

        if (displayWidth >= 1920)
            spanCount = 4;

        else if (displayWidth >= 1080)
            spanCount = 3;

        else if (displayWidth >= 500)
            spanCount = 2;
        else
            spanCount = 2;

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);
        staggeredGridLayoutManagerTwo = new StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL);


       /* LinearLayoutManager gridLayoutManager = new GridLayoutManager(CreateShotActivity.this, spanCount);


        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);*/

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recipients_recyclerview.setLayoutManager(staggeredGridLayoutManager);
        recipients_recyclerview.setHasFixedSize(true);


        not_recipients_recyclerview.setLayoutManager(staggeredGridLayoutManagerTwo);
        not_recipients_recyclerview.setHasFixedSize(true);


        recipientAdapter = new RecipientAdapter(CreateShotActivity.this, recipientList);
        recipients_recyclerview.setAdapter(recipientAdapter);


        noRecipientAdapter = new NoRecipientAdapter(CreateShotActivity.this, notRecipientList);
        not_recipients_recyclerview.setAdapter(noRecipientAdapter);
        //recipients_recyclerview.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));


        /*RecyclerView.ItemAnimator animator = recipients_recyclerview.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }


        recipients_recyclerview.setItemViewCacheSize(20);
        recipients_recyclerview.setDrawingCacheEnabled(true);
        recipients_recyclerview.setItemAnimator(new DefaultItemAnimator());
        recipients_recyclerview.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/


        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RecipientListHelper.getInstance().setRecipientList(recipientList);

                Intent intent = new Intent(CreateShotActivity.this, CameraActivity.class);
                intent.putExtra("currentUsername", username);
                intent.putExtra("currentUserID", userID);
                intent.putExtra("requestType", "shot");

                startActivity(intent);

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateArrow();
                textMessage = message_box.getText().toString();
            }
        });

        message_box.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                text_msg.setText("" + s.toString());
                if (s.toString().length() > 0) {
                    sendButton.setAlpha(1.0f);
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setAlpha(0.15f);
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                text_msg.setText("" + s.toString());
                if (s.toString().length() > 0) {
                    sendButton.setAlpha(1.0f);
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setAlpha(0.15f);
                    sendButton.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                text_msg.setText("" + s.toString());
                if (s.toString().length() > 0) {
                    sendButton.setAlpha(1.0f);
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setAlpha(0.15f);
                    sendButton.setEnabled(false);
                }
            }
        });


        checkYourRecipients();

    }

    private void checkYourRecipients() {
        totalRecipients = 0;
        calRecipients = 0;
        FirebaseDatabase.getInstance().getReference("Recipient")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Recipient recipient = dataSnapshot.getValue(Recipient.class);
                        fetchFeeling(recipient);
                        totalRecipients += 1;
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        LayoutInflater inflater = getLayoutInflater();
                        View layout = inflater.inflate(R.layout.custom_toast,
                                (ViewGroup) findViewById(R.id.toast_parent));

                        Toast toast = new Toast(CreateShotActivity.this);


                        TextView toast_message = layout.findViewById(R.id.toast_message);
                        toast_message.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

                        double percentage = (double) (calRecipients / totalRecipients) * 100;

                        toast_message.setText("Your shot can reach " + percentage + "% of your friends.\nSwipe down to see your recipients");
                        toast_message.setBackgroundTintList(getResources().getColorStateList(R.color.darkGrey));

                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }, 3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void fetchFeeling(Recipient recipient) {


        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                .child(recipient.getUserid());

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    recipient.setFeelingIcon(user.getFeelingIcon());
                    recipient.setFeeling(user.getFeeling());
                    recipient.setPhoto(user.getPhoto());

                    Long tsLong = System.currentTimeMillis() / 1000;

                    long localUserDiff = tsLong - recipient.getLocaluserstamp();
                    long remoteUserDiff = tsLong - recipient.getOtheruserstamp();


                    long localDiffHours = localUserDiff / (60 * 60 * 24 /** 30*/);
                    long otherDiffHours = remoteUserDiff / (60 * 60 * 24 /** 30*/);

                    Log.i("localDiffHours", "" + localDiffHours);
                    Log.i("otherDiffHours", "" + otherDiffHours);


                    if (localDiffHours < 1 && otherDiffHours < 1) {
                        recipient.setManual(false);
                        if (!recipientList.contains(recipient) && !notRecipientList.contains(recipient)) {
                            calRecipients += 1;
                            recipientList.add(recipient);
                            recipientAdapter.updateList(recipientList);
                        }
                    } else {
                        recipient.setManual(true);
                        if (!recipientList.contains(recipient) && !notRecipientList.contains(recipient)) {
                            notRecipientList.add(recipient);
                            noRecipientAdapter.updateList(notRecipientList);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);
        databaseListeners.add(new DatabaseListener(databaseReference, valueEventListener));


    }

    @Override
    public void addtoRecipientList(Recipient recipient) {

        notRecipientList.remove(recipient);
        noRecipientAdapter.updateList(notRecipientList);

        recipientList.add(recipient);
        recipientAdapter.updateList(recipientList);

    }

    @Override
    public void removeFromRecipientList(Recipient recipient) {
        recipientList.remove(recipient);
        recipientAdapter.updateList(recipientList);


        notRecipientList.add(recipient);
        noRecipientAdapter.updateList(notRecipientList);

    }

    @Override
    protected void onStop() {
        super.onStop();
        int i = 0;
        for (DatabaseListener databaseListener : databaseListeners) {
            try {
                ++i;
                databaseListener.getDatabaseReference().removeEventListener(databaseListener.getValueEventListener());
                //Toast.makeText(this,"Removed Listener: "+i,Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                //
            }
        }
    }

    private void animateArrow() {
        //send_arrow.setAlpha(1.0f);
        /**
         *
         *Chain button and progress view get hidden when send button is pressed
         */


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayHeight = size.y;

        ObjectAnimator animatorY, alphaArrow, animatortextY, alphaText;
        AnimatorSet animatorSet;


        animatorY = ObjectAnimator.ofFloat(send_arrow, "translationY", -displayHeight - (displayHeight) / 3);
        animatortextY = ObjectAnimator.ofFloat(text_msg, "translationY", -displayHeight - (displayHeight) / 3);
        //  animatorY.setDuration(arrowAnimDuration);


        alphaArrow = ObjectAnimator.ofFloat(send_arrow, "alpha", 1.0f, 0.02f);
        alphaText = ObjectAnimator.ofFloat(text_msg, "alpha", 1.0f, 0.02f);
        //  alphaArrow.setDuration(arrowAnimDuration);


        //alphaArrow.setDuration(650);

        sendButton.setAlpha(0.15f);
        sendButton.setEnabled(false);

        int arrowAnimDuration = 600;

        send_arrow.setAlpha(1.0f);


        animatorSet = new AnimatorSet();

        animatorSet.setDuration(arrowAnimDuration);

        animatorSet.playTogether(animatorY, alphaArrow, animatortextY, alphaText);

        animatorSet.start();


        /**
         * calling the below two commented function results in a UI lag
         */
        /*resetMessageBox();
        sendMessage(chatMessage);*/


        animatorSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

                send_arrow.setAlpha(1.0f);


            }

            @Override
            public void onAnimationEnd(Animator animation) {

                /*if(imageLoaded)
                    profile_pic.setBlur(0);*/

                send_arrow.setTranslationY(0f);
                text_msg.setTranslationY(0f);
                text_msg.setAlpha(1.0f);

                resetMessageBox();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        postShot();
                    }
                }, 250);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

                send_arrow.setTranslationY(0f);
                send_arrow.setAlpha(0.0f);

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private void postShot() {

        Timestamp timeStamp = new Timestamp(System.currentTimeMillis());
        Long tsLong = System.currentTimeMillis() / 1000;
        String postKey = FirebaseDatabase.getInstance().getReference("Post")
                .child(userID).push().getKey();


        Post post = new Post();
        post.setUserId(userID);
        post.setPostKey(postKey);
        post.setType("message");
        post.setMediaUrl(textMessage);
        post.setUsername(username);
        post.setPostTime(tsLong);
        post.setMediaKey(postKey);
        post.setTimeStamp(timeStamp.toString());
        //postRef.child(postKey).updateChildren(postHash);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> postHash = new HashMap<>();
        postHash.put("MyPosts/" + userID + "/" + postKey, post);
        databaseReference.updateChildren(postHash);

        HashMap<String, Object> multiplePathUpdate = new HashMap<>();
        /*FirebaseDatabase.getInstance()
                .getReference("FriendList")
                .child(userID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Friend friend = dataSnapshot.getValue(Friend.class);
                                multiplePathUpdate.put("Post/" + friend.getFriendId() + "/" + postKey, post);

                            }
                            databaseReference.updateChildren(multiplePathUpdate);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
        */

        for (Recipient recipient : recipientList) {
            post.setManual(recipient.getManual());
            multiplePathUpdate.put("Post/" + recipient.getUserid() + "/" + postKey, post);
        }
        databaseReference.updateChildren(multiplePathUpdate);

    }

    private void resetMessageBox() {
        message_box.setText("");
    }
}