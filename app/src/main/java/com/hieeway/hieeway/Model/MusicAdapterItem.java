package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

public class MusicAdapterItem {

    private Music music;
    private String username;
    private String userPhoto;

    public MusicAdapterItem() {
    }

    public Music getMusic() {
        return music;
    }

    public void setMusic(Music music) {
        this.music = music;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // return super.equals(obj);
        MusicAdapterItem musicAdapterItem = (MusicAdapterItem) obj;

        if (music.getSpotifyId().equals(musicAdapterItem.getMusic().getSpotifyId()))
            return true;
        else
            return false;
    }
}
