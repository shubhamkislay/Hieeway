package com.hieeway.hieeway.Model;

import com.spotify.protocol.types.ImageUri;

public class MusicMessage {

    private String spotifyId;
    private String spotifySong;
    private String spotifyArtist;
    private String spotifyCover;

    public MusicMessage() {
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

    public String getSpotifyCover() {
        return spotifyCover;
    }

    public void setSpotifyCover(String spotifyCover) {
        this.spotifyCover = spotifyCover;
    }
}
