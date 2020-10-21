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
import android.view.Display;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.RecipientAdapter;
import com.hieeway.hieeway.Model.Recipient;

import java.util.ArrayList;
import java.util.List;

public class CreateShotActivity extends AppCompatActivity {

    private TextView recipients;
    private RecyclerView recipients_recyclerview;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String USER_ID = "userid";
    private RecipientAdapter recipientAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    private String userID;
    private SharedPreferences sharedPreferences;
    private List<Recipient> recipientList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_shot);

        recipients = findViewById(R.id.recipients);

        recipientList = new ArrayList<>();
        recipients_recyclerview = findViewById(R.id.recipients_recyclerview);

        recipients.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

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


        LinearLayoutManager gridLayoutManager = new GridLayoutManager(CreateShotActivity.this, spanCount);


        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recipients_recyclerview.setLayoutManager(staggeredGridLayoutManager);
        recipients_recyclerview.setHasFixedSize(true);
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

                        recipient.setFeelingIcon("default");
                        recipient.setFeeling("happy");

                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);

                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);

                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);

                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);

                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);

                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);
                        recipientList.add(recipient);


                    }

                    recipientAdapter = new RecipientAdapter(CreateShotActivity.this, recipientList);
                    recipients_recyclerview.setAdapter(recipientAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}