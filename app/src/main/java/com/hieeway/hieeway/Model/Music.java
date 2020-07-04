package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

import com.spotify.protocol.types.ImageUri;

public class Music implements Comparable<Music> {

    private String spotifyId;
    private String spotifySong;
    private String spotifyArtist;
    private ImageUri spotifyCover;
    private String timestamp;
    private String userId;
    private String userPhoto;
    private String username;

    public Music() {
    }

    public String getSpotifyId() {
        return spotifyId;
    }

    public void setSpotifyId(String spotifyId) {
        this.spotifyId = spotifyId;
    }

    public String getSpotifySong() {
        return spotifySong;
    }

    public void setSpotifySong(String spotifySong) {
        this.spotifySong = spotifySong;
    }

    public String getSpotifyArtist() {
        return spotifyArtist;
    }

    public void setSpotifyArtist(String spotifyArtist) {
        this.spotifyArtist = spotifyArtist;
    }

    public ImageUri getSpotifyCover() {
        return spotifyCover;
    }

    public void setSpotifyCover(ImageUri spotifyCover) {
        this.spotifyCover = spotifyCover;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhoto() {
        return userPhoto;
    }

    public void setUserPhoto(String userPhoto) {
        this.userPhoto = userPhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        // return super.equals(obj);
        Music music = (Music) obj;

        if (userId.equals(music.getUserId()))
            return true;
        else
            return false;
    }

    @Override
    public int compareTo(Music o) {


        try {
            return this.getTimestamp().compareTo(o.getTimestamp());
        } catch (Exception e) {
            return 0;
        }

    }
}
