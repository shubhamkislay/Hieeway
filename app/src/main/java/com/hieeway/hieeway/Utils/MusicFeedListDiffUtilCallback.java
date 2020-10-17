package com.hieeway.hieeway.Utils;

import androidx.recyclerview.widget.DiffUtil;

import com.hieeway.hieeway.Model.ChatStamp;
import com.hieeway.hieeway.Model.MusicPost;

import java.util.List;

public class MusicFeedListDiffUtilCallback extends DiffUtil.Callback {


    private List<MusicPost> mOldList;
    private List<MusicPost> mNewList;


    public MusicFeedListDiffUtilCallback(List<MusicPost> oldList, List<MusicPost> newList) {
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
                mNewList.get(newItemPosition).getSpotifyArtist().equals(mOldList.get(oldItemPosition).getSpotifyArtist())
                        && mNewList.get(newItemPosition).getSpotifyCover().equals(mOldList.get(oldItemPosition).getSpotifyCover())
                        && mNewList.get(newItemPosition).getSpotifySong().equals(mOldList.get(oldItemPosition).getSpotifySong())
                        && mNewList.get(newItemPosition).getSpotifyId().equals(mOldList.get(oldItemPosition).getSpotifyId());
    }
}
