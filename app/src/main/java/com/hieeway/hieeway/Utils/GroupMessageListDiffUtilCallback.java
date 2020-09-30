package com.hieeway.hieeway.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.GroupMessage;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.ChatStamp;

import java.util.List;

public class GroupMessageListDiffUtilCallback extends DiffUtil.Callback {


    private List<GroupMessage> mOldList;
    private List<GroupMessage> mNewList;


    public GroupMessageListDiffUtilCallback(List<GroupMessage> oldList, List<GroupMessage> newList) {
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
        return mNewList.get(newItemPosition).getMessageId().equals(mOldList.get(oldItemPosition).getMessageId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).getTimeStamp().equals(mOldList.get(oldItemPosition).getTimeStamp()) &&
                mNewList.get(newItemPosition).getMessageText().equals(mOldList.get(oldItemPosition).getMessageText())
                && mNewList.get(newItemPosition).getPhoto().equals(mOldList.get(oldItemPosition).getPhoto());
    }
}
