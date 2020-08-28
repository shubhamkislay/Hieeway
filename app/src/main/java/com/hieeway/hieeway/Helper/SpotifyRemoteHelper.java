package com.hieeway.hieeway.Helper;

import android.graphics.Bitmap;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyRemoteHelper {

    public static final SpotifyRemoteHelper instance = new SpotifyRemoteHelper();
    private SpotifyAppRemote spotifyAppRemote = null;


    public SpotifyRemoteHelper() {
    }

    public static SpotifyRemoteHelper getInstance() {
        return instance;
    }


    public SpotifyAppRemote getSpotifyAppRemote() {
        return spotifyAppRemote;
    }

    public void setSpotifyAppRemote(SpotifyAppRemote spotifyAppRemote) {
        this.spotifyAppRemote = spotifyAppRemote;
    }


}