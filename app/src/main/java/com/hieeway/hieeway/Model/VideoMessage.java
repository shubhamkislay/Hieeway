package com.hieeway.hieeway.Model;

public class VideoMessage {

    private String youtubeId;
    private String youtubeTitle;
    private String youtubeUrl;

    public VideoMessage() {
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getYoutubeTitle() {
        return youtubeTitle;
    }

    public void setYoutubeTitle(String youtubeTitle) {
        this.youtubeTitle = youtubeTitle;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public void setYoutubeUrl(String videoHash) {
        this.youtubeUrl = videoHash;
    }
}
