package com.hieeway.hieeway.Model;

public class YoutubeSync implements Comparable<YoutubeSync> {

    private String youtubeID;
    private float videoSec;
    private String videoTitle;
    private String timeStamp;

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

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public int compareTo(YoutubeSync o) {


        try {
            return this.getTimeStamp().compareTo(o.getTimeStamp());
        } catch (Exception e) {
            return 0;
        }

    }
}
