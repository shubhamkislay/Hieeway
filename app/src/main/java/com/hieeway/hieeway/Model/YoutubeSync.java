package com.hieeway.hieeway.Model;

public class YoutubeSync {

    private String youtubeID;
    private float videoSec;
    private String videoTitle;

    public YoutubeSync() {
    }

    public String getYoutubeID() {
        return youtubeID;
    }

    public void setYoutubeID(String youtubeID) {
        this.youtubeID = youtubeID;
    }

    public float getVideoSec() {
        return videoSec;
    }

    public void setVideoSec(float videoSec) {
        this.videoSec = videoSec;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }
}
