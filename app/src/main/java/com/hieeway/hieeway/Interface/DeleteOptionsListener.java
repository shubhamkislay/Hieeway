package com.hieeway.hieeway.Interface;

import androidx.recyclerview.widget.RecyclerView;

public interface DeleteOptionsListener {


    void onDeleteForAll(int position, int mChatStampsListSize, RecyclerView.ViewHolder viewHolder);
    void onDeleteForMe(int position, int mChatStampsListSize,RecyclerView.ViewHolder viewHolder);
    /*void setDeleteOptionsDialog(Context context, ChatStamp chatStamp, int position,
                                List<ChatStamp> mChatStamps, Activity activity, RecyclerView.ViewHolder viewHolder);*/
}
