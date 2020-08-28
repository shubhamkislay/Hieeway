package com.hieeway.hieeway.Helper;

import android.graphics.Bitmap;

import com.spotify.android.appremote.api.SpotifyAppRemote;

public class SpotifyMusicPalRemoteHelper {

    public static final SpotifyMusicPalRemoteHelper instance = new SpotifyMusicPalRemoteHelper();
    private SpotifyAppRemote spotifyAppRemote = null;


    public SpotifyMusicPalRemoteHelper() {
    }

    public static SpotifyMusicPalRemoteHelper getInstance() {
        return instance;
    }


    public SpotifyAppRemote getSpotifyAppRemote() {
        return spotifyAppRemote;
    }

    public void setSpotifyAppRemote(SpotifyAppRemote spotifyAppRemote) {
        this.spotifyAppRemote = spotifyAppRemote;
    }


}