package com.shubhamkislay.jetpacklogin;

import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shubhamkislay.jetpacklogin.Adapters.RevealRequestsAdapter;
import com.shubhamkislay.jetpacklogin.Model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class RevealReplyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView titleText;
    RevealRequestsAdapter revealRequestsAdapter;
    List<ChatMessage> chatMessageList, newChatList;
    String userIdChattingWith;
    String currentUserPrivateKey;
    String currentUserPublicKeyID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_reply);

        recyclerView = findViewById(R.id.recycler_view);
        titleText = findViewById(R.id.top_title_txt);

        userIdChattingWith = getIntent().getStringExtra("userIdChattingWith");
        currentUserPrivateKey = getIntent().getStringExtra("currentUserPrivateKey");
        currentUserPublicKeyID = getIntent().getStringExtra("currentUserPublicKeyID");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        chatMessageList = new ArrayList<>();
        newChatList = new ArrayList<>();

        titleText.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/samsungsharpsans-bold.otf"));

        populateList();

    }

    private void populateList() {

        DatabaseReference requestMessageRef = FirebaseDatabase.getInstance().getReference("MessageRequests")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userIdChattingWith);

        requestMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatMessageList.clear();
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                        chatMessageList.add(chatMessage);


                    }
                    if(chatMessageList.size()>=newChatList.size()) {
                        revealRequestsAdapter = new RevealRequestsAdapter(chatMessageList, RevealReplyActivity.this, currentUserPrivateKey, currentUserPublicKeyID, userIdChattingWith);
                        recyclerView.setAdapter(revealRequestsAdapter);
                        newChatList = chatMessageList;
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
