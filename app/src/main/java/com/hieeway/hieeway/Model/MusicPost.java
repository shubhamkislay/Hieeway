package com.hieeway.hieeway.Model;

import androidx.annotation.Nullable;

import com.spotify.protocol.types.ImageUri;

import java.util.List;

public class MusicPost implements Comparable<MusicPost> {

    private String spotifyId;
    private String spotifySong;
    private String spotifyArtist;
    private ImageUri spotifyCover;
    private String timestamp;
    private String postKey;
    private String seenBy;

    public MusicPost() {
    }

    public String getSeenBy() {
        return seenBy;
    }

    public void setSeenBy(String seenBy) {
        this.seenBy = seenBy;
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

    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    @Override
    public int compareTo(MusicPost o) {

        try {
            return this.getTimestamp().compareTo(o.getTimestamp());
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        MusicPost musicPost = (MusicPost) obj;
        return this.getPostKey().equals(musicPost.getPostKey());
    }
}
