package com.hieeway.hieeway.Utils;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyMusicPostRemote {

    public static final SpotifyMusicPostRemote instance = new SpotifyMusicPostRemote();
    private SpotifyAppRemote spotifyAppRemote = null;


    public SpotifyMusicPostRemote() {
    }

    public static SpotifyMusicPostRemote getInstance() {
        return instance;
    }


    public SpotifyAppRemote getSpotifyAppRemote() {
        return spotifyAppRemote;
    }

    public void setSpotifyAppRemote(SpotifyAppRemote spotifyAppRemote) {
        this.spotifyAppRemote = spotifyAppRemote;
    }


}