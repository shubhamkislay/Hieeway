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

import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.NoRecipientAdapter;
import com.hieeway.hieeway.Adapters.RecipientAdapter;
import com.hieeway.hieeway.Interface.AddRecipientListener;
import com.hieeway.hieeway.Interface.RemoveRecipientListener;
import com.hieeway.hieeway.Model.DatabaseListener;
import com.hieeway.hieeway.Model.Recipient;
import com.hieeway.hieeway.Model.User;

import java.util.ArrayList;
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
    private RecipientAdapter recipientAdapter;
    private NoRecipientAdapter noRecipientAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager, staggeredGridLayoutManagerTwo;
    private String userID;
    private SharedPreferences sharedPreferences;
    private List<Recipient> recipientList;
    private List<Recipient> notRecipientList;
    private List<DatabaseListener> databaseListeners;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shot);

        recipients = findViewById(R.id.recipients);
        no_recipient_txt = findViewById(R.id.no_recipient_txt);
        recipients_text = findViewById(R.id.recipients_text);

        databaseListeners = new ArrayList<>();
        recipientList = new ArrayList<>();
        notRecipientList = new ArrayList<>();
        recipients_recyclerview = findViewById(R.id.recipients_recyclerview);
        not_recipients_recyclerview = findViewById(R.id.not_recipients_recyclerview);

        recipients.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        recipients_text.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));
        no_recipient_txt.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        sharedPreferences = this.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);

        userID = sharedPreferences.getString(USER_ID, "");

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


        checkYourRecipients();

    }

    private void checkYourRecipients() {
        FirebaseDatabase.getInstance().getReference("Recipient")
                .child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Recipient recipient = dataSnapshot.getValue(Recipient.class);
                        fetchFeeling(recipient);
                    }
                }
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


                    long localDiffHours = localUserDiff / (60 * 60);
                    long otherDiffHours = remoteUserDiff / (60 * 60);

                    Log.i("localDiffHours", "" + localDiffHours);
                    Log.i("otherDiffHours", "" + otherDiffHours);


                    if (localDiffHours < 1 && otherDiffHours < 1) {
                        recipient.setManual(false);
                        if (!recipientList.contains(recipient) && !notRecipientList.contains(recipient)) {
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
}