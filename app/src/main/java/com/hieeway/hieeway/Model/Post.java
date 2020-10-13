package com.hieeway.hieeway.Model;

import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.Comparator;

public class Post implements Comparable<Post> {

    public String userId;
    public String postKey;
    public String type;
    public String mediaUrl;
    public String mediaKey;
    public String username;

    public Post() {
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


    @Override
    public int compareTo(Post o) {
        try {

            /*int i = this.getPostKey().compareTo(o.getPostKey());
            if (i != 0) return i;
            i = this.getType().compareTo(o.getType());
            if (i != 0) return i;
            return 0;*/

            return this.getUserId().compareTo(o.getUserId());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        Post o = (Post) obj;
        return this.getUserId().equals(o.getUserId());
    }
}
