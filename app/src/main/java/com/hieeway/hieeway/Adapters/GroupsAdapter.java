package com.hieeway.hieeway.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.hieeway.hieeway.AddGroupDetailsActivity;
import com.hieeway.hieeway.CustomCircularView;
import com.hieeway.hieeway.FriendListFragmentViewModel;
import com.hieeway.hieeway.Interface.FiltersListFragmentListener;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.R;
import com.hieeway.hieeway.Utils.FriendListDiffUtilCallback;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.MyViewHolder> {


    private static final int GROUP_ITEM = 3;
    private static final int CREATE_GROUP_ITEM = 0;
    private static final int START_MUSIC_ITEM = 2;
    private static final int START_VIDEO_ITEM = 1;
    private List<Friend> friendList;
    private List<Friend> refreshedList;
    private Context context;
    private int selectedIndex = 0;


    public GroupsAdapter(List<Friend> friendList, Context context) {
        this.friendList = friendList;

        this.context = context;

        refreshedList = new ArrayList<>();
        Friend friend1 = new Friend();
        Friend friend2 = new Friend();
        Friend friend3 = new Friend();
        refreshedList.add(friend1);
        refreshedList.add(friend2);
        refreshedList.add(friend3);
        refreshedList.addAll(friendList);

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {

        if (position == CREATE_GROUP_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.create_group_item, parent, false);


            return new MyViewHolder(itemView);
        } else if (position == START_VIDEO_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.new_watch_party_item, parent, false);


            return new MyViewHolder(itemView);
        } else if (position == START_MUSIC_ITEM) {
            View itemView = LayoutInflater.from(context).inflate(R.layout.pair_music_item, parent, false);


            return new MyViewHolder(itemView);
        } else {
            View itemView = LayoutInflater.from(context).inflate(R.layout.group_item, parent, false);


            return new MyViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int position) {

        if (myViewHolder.getAdapterPosition() > 2) {
            Friend friend = refreshedList.get(myViewHolder.getAdapterPosition());
            Glide.with(context).load(friend.getPhoto()).addListener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                    myViewHolder.one.setVisibility(View.GONE);
                    myViewHolder.two.setVisibility(View.GONE);

                    return false;
                }
            }).into(myViewHolder.prof_pic);
            myViewHolder.username.setText(friend.getUsername());


        } else {
            if (myViewHolder.getAdapterPosition() == 0) {
                myViewHolder.prof_pic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        context.startActivity(new Intent(context, AddGroupDetailsActivity.class));
                    }
                });

            }
        }


    }

    @Override
    public int getItemCount() {
        return refreshedList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position > 2)
            return GROUP_ITEM;
        else {
            if (position == 0)
                return CREATE_GROUP_ITEM;
            else if (position == 1)
                return START_VIDEO_ITEM;
            else
                return START_MUSIC_ITEM;
        }

    }

    public void updateList(List<Friend> newListChatStamp) {


        FriendListDiffUtilCallback chatStampListDiffUtilCallback = new FriendListDiffUtilCallback(this.friendList, newListChatStamp);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(chatStampListDiffUtilCallback);

        friendList.clear();
        friendList.addAll(newListChatStamp);


        refreshedList.clear();
        Friend friend1 = new Friend();
        Friend friend2 = new Friend();
        Friend friend3 = new Friend();
        refreshedList.add(friend1);
        refreshedList.add(friend2);
        refreshedList.add(friend3);
        refreshedList.addAll(friendList);


        diffResult.dispatchUpdatesTo(this);


    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        CustomCircularView prof_pic;
        TextView username;
        ProgressBar one, two;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            prof_pic = itemView.findViewById(R.id.user_photo);
            username = itemView.findViewById(R.id.username);
            one = itemView.findViewById(R.id.progress);
            two = itemView.findViewById(R.id.progressTwo);
        }
    }
}
