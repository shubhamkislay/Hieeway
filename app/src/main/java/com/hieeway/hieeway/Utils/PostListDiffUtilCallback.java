package com.hieeway.hieeway.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.Post;

import java.util.List;

public class PostListDiffUtilCallback extends DiffUtil.Callback {


    private List<Post> mOldList;
    private List<Post> mNewList;


    public PostListDiffUtilCallback(List<Post> oldList, List<Post> newList) {
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
        return mNewList.get(newItemPosition).getPostKey().equals(mOldList.get(oldItemPosition).getPostKey());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return //mNewList.get(newItemPosition).getTimeStamp().equals(mOldList.get(oldItemPosition).getTimeStamp())&&
                mNewList.get(newItemPosition).getType().equals(mOldList.get(oldItemPosition).getType())
                        && mNewList.get(newItemPosition).getMediaKey().equals(mOldList.get(oldItemPosition).getMediaKey())
                        && mNewList.get(newItemPosition).getUserId().equals(mOldList.get(oldItemPosition).getUserId())
                        && mNewList.get(newItemPosition).getMediaUrl().equals(mOldList.get(oldItemPosition).getMediaUrl())
                        && mNewList.get(newItemPosition).getUsername().equals(mOldList.get(oldItemPosition).getUsername());
    }
}
