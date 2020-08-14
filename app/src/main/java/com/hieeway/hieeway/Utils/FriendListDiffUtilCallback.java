package com.hieeway.hieeway.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;

import java.util.List;

public class FriendListDiffUtilCallback extends DiffUtil.Callback {


    private List<Friend> mOldList;
    private List<Friend> mNewList;


    public FriendListDiffUtilCallback(List<Friend> oldList, List<Friend> newList) {
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
        return mNewList.get(newItemPosition).getFriendId().equals(mOldList.get(oldItemPosition).getFriendId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).getStatus().equals(mOldList.get(oldItemPosition).getStatus())
                && mNewList.get(newItemPosition).getUsername().equals(mOldList.get(oldItemPosition).getUsername())
                && mNewList.get(newItemPosition).getPhoto().equals(mOldList.get(oldItemPosition).getPhoto());
    }
}
