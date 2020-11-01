package com.hieeway.hieeway;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hieeway.hieeway.Adapters.FriendsAdapter;
import com.hieeway.hieeway.Helper.GroupMemberListHelper;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.GroupMember;

import java.util.ArrayList;
import java.util.List;

public class GroupInfoActivity extends AppCompatActivity {

    private String groupName;
    private String groupID;
    private String groupIcon;
    private TextView group_name;
    private ImageView group_icon;
    private RecyclerView members_recyclerView;
    private List<Friend> friendList;
    private FriendsAdapter friendsAdapter;
    private Button back_btn, add_members_btn;
    private static final int UPDATED_RESULT = 10;
    Button edit_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);


        friendList = new ArrayList<>();

        Intent intent = getIntent();
        groupName = intent.getStringExtra("groupName");
        groupID = intent.getStringExtra("groupID");
        groupIcon = intent.getStringExtra("groupIcon");

        group_name = findViewById(R.id.group_name);
        group_icon = findViewById(R.id.group_icon);
        members_recyclerView = findViewById(R.id.members_recyclerView);
        back_btn = findViewById(R.id.back_btn);
        add_members_btn = findViewById(R.id.add_members_btn);
        edit_group = findViewById(R.id.edit_group);

        group_name.setText(groupName);

        group_name.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));
        add_members_btn.setTypeface(Typeface.createFromAsset(this.getAssets(), "fonts/samsungsharpsans-bold.otf"));

        if (!groupIcon.equals("default"))
            Glide.with(this).load(groupIcon).into(group_icon);
        else
            Glide.with(this).load(getResources().getDrawable(R.drawable.groups_image)).into(group_icon);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        add_members_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupMemberListHelper.getInstance().setGroupMemberList(friendList);

                Intent intent = new Intent(GroupInfoActivity.this, CreateGroupActivity.class);
                intent.putExtra("icon", groupIcon);
                intent.putExtra("groupID", groupID);
                intent.putExtra("groupName", groupName);
                intent.putExtra("requestType", "add");
                startActivity(intent);
            }
        });

        edit_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                GroupMemberListHelper.getInstance().setGroupMemberList(friendList);

                Intent intent = new Intent(GroupInfoActivity.this, ChangeGroupDetailsActivity.class);
                intent.putExtra("icon", groupIcon);
                intent.putExtra("groupID", groupID);
                intent.putExtra("groupName", groupName);

                startActivityForResult(intent, UPDATED_RESULT);
            }
        });


    }


    private void getMemberList() {

        friendList.clear();

        FirebaseDatabase.getInstance().getReference("GroupMembers")
                .child(groupID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                                GroupMember groupMember = snapshot.getValue(GroupMember.class);

                                friendList.add(
                                        new Friend(groupMember.getId(),
                                                "friends",
                                                groupMember.getUsername(),
                                                groupMember.getPhoto(),
                                                "default"));
                            }


                            friendsAdapter = new FriendsAdapter(GroupInfoActivity.this, friendList, GroupInfoActivity.this, false);
                            //chatMessageAdapter.setHasStableIds(true);
                            members_recyclerView.setAdapter(friendsAdapter);

                            edit_group.setVisibility(View.VISIBLE);
                            add_members_btn.setVisibility(View.VISIBLE);


                        } else {

                            friendsAdapter = new FriendsAdapter(GroupInfoActivity.this, friendList, GroupInfoActivity.this, false);
                            //chatMessageAdapter.setHasStableIds(true);
                            members_recyclerView.setAdapter(friendsAdapter);

                            edit_group.setVisibility(View.VISIBLE);
                            add_members_btn.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATED_RESULT && resultCode == RESULT_OK) {

            if (data != null) {

                for (String key : data.getExtras().keySet()) {

                    if (key.equals("groupIcon")) {
                        groupIcon = data.getStringExtra("groupIcon");
                        if (!groupIcon.equals("default"))
                            Glide.with(this).load(groupIcon).into(group_icon);
                        else
                            Glide.with(this).load(getResources().getDrawable(R.drawable.groups_image)).into(group_icon);
                    }

                    if (key.equals("groupName")) {
                        groupName = data.getStringExtra("groupName");
                        group_name.setText(groupName);
                    }
                }
            }

        }


    }

    private void initialteRecyclerView() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        float displayWidth = size.x;


        int spanCount; // 3 columns
        int spacing = 0; // 50px
        boolean includeEdge = true;

        if (displayWidth >= 1920)
            spanCount = 2;

        else if (displayWidth >= 1080)
            spanCount = 2;

        else if (displayWidth >= 500)
            spanCount = 2;
        else
            spanCount = 1;


        LinearLayoutManager gridLayoutManager = new GridLayoutManager(this, spanCount);

        gridLayoutManager.setOrientation(RecyclerView.VERTICAL);

        //gridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        members_recyclerView.setLayoutManager(gridLayoutManager);
        members_recyclerView.setHasFixedSize(true);
        members_recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        members_recyclerView.setItemViewCacheSize(20);
        members_recyclerView.setDrawingCacheEnabled(true);
        members_recyclerView.setItemAnimator(new DefaultItemAnimator());
        members_recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    @Override
    protected void onStart() {
        super.onStart();

        initialteRecyclerView();
        getMemberList();
    }

    @Override
    public void onBackPressed() {

        Intent resultIntent = new Intent();
        resultIntent.putExtra("groupName", groupName);
        resultIntent.putExtra("groupIcon", groupIcon);

        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}