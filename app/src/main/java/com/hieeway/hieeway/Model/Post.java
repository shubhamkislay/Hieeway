package com.hieeway.hieeway.Model;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Comparator;
import java.util.List;

public class Post implements Comparable<Post> {

    public String userId;
    public String postKey;
    public String type;
    public String mediaUrl;
    public String mediaKey;
    public String username;
    public String timeStamp;
    public String seenBy;
    public boolean manual;
    private long postTime;
    private boolean visibility = false;

    public String getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
    }


    public Post() {
    }

    public boolean isManual() {
        return manual;
    }

    public void setManual(boolean manual) {
        this.manual = manual;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getType() {
        return type;
    }

    public long getPostTime() {
        return postTime;
    }

    public void setPostTime(long postTime) {
        this.postTime = postTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaKey() {
        return mediaKey;
    }

    public void setMediaKey(String mediaKey) {
        this.mediaKey = mediaKey;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isVisibility() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    @Override
    public int compareTo(Post o) {
        try {
            return this.getTimeStamp().compareTo(o.getTimeStamp());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Post o = (Post) obj;

        if (o.getType().equals("music"))
            return this.getType().equals(o.getType());

        else
            return this.getType().equals(o.getType())
                    && this.getUsername().equals(o.getUsername())
                    && this.getMediaUrl().equals(o.getMediaUrl())
                    && this.getTimeStamp().equals(o.getTimeStamp());
    }
}
