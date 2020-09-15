package com.hieeway.hieeway;

import android.graphics.Typeface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.RevealRequestsAdapter;
import com.hieeway.hieeway.Model.ChatMessage;

import java.util.ArrayList;
import java.util.List;

import static com.hieeway.hieeway.MyApplication.notificationIDHashMap;

public class RevealReplyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TextView titleText;
    RevealRequestsAdapter revealRequestsAdapter;
    List<ChatMessage> chatMessageList, newChatList;
    String userIdChattingWith;
    String currentUserPrivateKey;
    String currentUserPublicKeyID;
    String usernameChattingWith;
    String photo;
    String activePhoto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal_reply);

        recyclerView = findViewById(R.id.recycler_view);
        titleText = findViewById(R.id.top_title_txt);

        userIdChattingWith = getIntent().getStringExtra("userIdChattingWith");
        currentUserPrivateKey = getIntent().getStringExtra("currentUserPrivateKey");
        currentUserPublicKeyID = getIntent().getStringExtra("currentUserPublicKeyID");
        usernameChattingWith = getIntent().getStringExtra("username");
        photo = getIntent().getStringExtra("photo");
        activePhoto = getIntent().getStringExtra("activePhoto");

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        chatMessageList = new ArrayList<>();
        newChatList = new ArrayList<>();

        notificationIDHashMap.put(userIdChattingWith + "numberrevealrequest", 0);

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
                notificationIDHashMap.put(userIdChattingWith + "numberrevealrequest", 0);
                if(dataSnapshot.exists())
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        ChatMessage chatMessage = snapshot.getValue(ChatMessage.class);
                        chatMessageList.add(chatMessage);


                    }
                    if(chatMessageList.size()>=newChatList.size()) {
                        revealRequestsAdapter = new RevealRequestsAdapter(chatMessageList, RevealReplyActivity.this, currentUserPrivateKey, currentUserPublicKeyID, userIdChattingWith, usernameChattingWith, photo, activePhoto);
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

    @Override
    protected void onPause() {

        notificationIDHashMap.put(userIdChattingWith + "numberrevealrequest", 0);
        super.onPause();
    }
}
