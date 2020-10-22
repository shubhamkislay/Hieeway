package com.hieeway.hieeway.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.MusicPost;
import com.hieeway.hieeway.Model.Recipient;

import java.util.List;

public class RecipientListDiffUtilCallback extends DiffUtil.Callback {


    private List<Recipient> mOldList;
    private List<Recipient> mNewList;


    public RecipientListDiffUtilCallback(List<Recipient> oldList, List<Recipient> newList) {
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
        return mNewList.get(newItemPosition).getUserid().equals(mOldList.get(oldItemPosition).getUserid());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return //mNewList.get(newItemPosition).getTimeStamp().equals(mOldList.get(oldItemPosition).getTimeStamp())&&
                mNewList.get(newItemPosition).getUsername().equals(mOldList.get(oldItemPosition).getUsername())
                        && mNewList.get(newItemPosition).getPhoto().equals(mOldList.get(oldItemPosition).getPhoto())
                        && mNewList.get(newItemPosition).getFeeling().equals(mOldList.get(oldItemPosition).getFeeling())
                        && mNewList.get(newItemPosition).getFeelingIcon().equals(mOldList.get(oldItemPosition).getFeelingIcon());
    }
}
