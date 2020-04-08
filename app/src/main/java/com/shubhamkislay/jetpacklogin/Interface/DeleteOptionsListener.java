package com.shubhamkislay.jetpacklogin.Interface;

import androidx.recyclerview.widget.RecyclerView;

public interface DeleteOptionsListener {


    void onDeleteForAll(int position, int mChatStampsListSize, RecyclerView.ViewHolder viewHolder);
    void onDeleteForMe(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder);
}
