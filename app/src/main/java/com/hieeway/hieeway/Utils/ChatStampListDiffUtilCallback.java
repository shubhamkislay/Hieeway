package com.hieeway.hieeway.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.ChatStamp;

import java.util.List;

public class ChatStampListDiffUtilCallback extends DiffUtil.Callback {


    private List<ChatStamp> mOldList;
    private List<ChatStamp> mNewList;


    public ChatStampListDiffUtilCallback(List<ChatStamp> oldList, List<ChatStamp> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }


    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).getId().equals(mOldList.get(oldItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return //mNewList.get(newItemPosition).getTimeStamp().equals(mOldList.get(oldItemPosition).getTimeStamp())&&
                mNewList.get(newItemPosition).getChatPending().equals(mOldList.get(oldItemPosition).getChatPending())
                && mNewList.get(newItemPosition).getPhoto().equals(mOldList.get(oldItemPosition).getPhoto());
    }
}
