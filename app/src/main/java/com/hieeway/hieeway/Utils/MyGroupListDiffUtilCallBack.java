package com.hieeway.hieeway.Utils;


import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Friend;
import com.hieeway.hieeway.Model.MyGroup;

import java.util.List;

public class MyGroupListDiffUtilCallBack extends DiffUtil.Callback {


    private List<MyGroup> mOldList;
    private List<MyGroup> mNewList;


    public MyGroupListDiffUtilCallBack(List<MyGroup> oldList, List<MyGroup> newList) {
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
        return mNewList.get(newItemPosition).getGroupID().equals(mOldList.get(oldItemPosition).getGroupID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        try {
            return mNewList.get(newItemPosition).getTimeStamp().equals(mOldList.get(oldItemPosition).getTimeStamp())
                    && mNewList.get(newItemPosition).getGroupName().equals(mOldList.get(oldItemPosition).getGroupName())
                    && mNewList.get(newItemPosition).getIcon().equals(mOldList.get(oldItemPosition).getIcon());
        } catch (Exception e) {
            return mNewList.get(newItemPosition).getGroupName().equals(mOldList.get(oldItemPosition).getGroupName())
                    && mNewList.get(newItemPosition).getIcon().equals(mOldList.get(oldItemPosition).getIcon());
        }
    }
}
