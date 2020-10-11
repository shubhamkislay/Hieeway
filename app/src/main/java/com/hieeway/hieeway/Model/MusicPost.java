package com.hieeway.hieeway.Model;

import com.spotify.protocol.types.ImageUri;

public class MusicPost {

    private String spotifyId;
    private String spotifySong;
    private String spotifyArtist;
    private ImageUri spotifyCover;
    private String timestamp;

    public MusicPost() {
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
}
