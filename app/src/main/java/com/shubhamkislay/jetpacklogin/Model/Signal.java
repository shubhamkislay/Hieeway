package com.shubhamkislay.jetpacklogin.Model;

public class Signal {
    String sdp;
    String type;

    public Signal(String sdp, String type) {
        this.sdp = sdp;
        this.type = type;
    }

    public Signal() {
    }

    public String getSdp() {
        return sdp;
    }

    public void setSdp(String sdp) {
        this.sdp = sdp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
