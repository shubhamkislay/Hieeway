package com.shubhamkislay.jetpacklogin.Interface;

import android.app.Activity;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.shubhamkislay.jetpacklogin.Model.ChatStamp;

import java.util.List;

public interface DeleteOptionsListener {


    void onDeleteForAll(int position, int mChatStampsListSize, RecyclerView.ViewHolder viewHolder);
    void onDeleteForMe(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder);
    /*void setDeleteOptionsDialog(Context context, ChatStamp chatStamp, int position,
                                List<ChatStamp> mChatStamps, Activity activity, RecyclerView.ViewHolder viewHolder);*/
}
