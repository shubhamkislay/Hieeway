package com.shubhamkislay.jetpacklogin.Model;

import android.widget.RelativeLayout;
import android.widget.TextView;

public class CheckPendingMessageAsyncModel {

     private String userChattingWith;
     private RelativeLayout relativeLayout;
     private RelativeLayout countMsgLayout;
     private TextView countMessageText;

    public CheckPendingMessageAsyncModel(String userChattingWith, RelativeLayout relativeLayout, RelativeLayout countMsgLayout, TextView countMessageText) {
        this.userChattingWith = userChattingWith;
        this.relativeLayout = relativeLayout;
        this.countMsgLayout = countMsgLayout;
        this.countMessageText = countMessageText;
    }

    public CheckPendingMessageAsyncModel() {
    }

    public String getUserChattingWith() {
        return userChattingWith;
    }

    public void setUserChattingWith(String userChattingWith) {
        this.userChattingWith = userChattingWith;
    }

    public RelativeLayout getRelativeLayout() {
        return relativeLayout;
    }

    public void setRelativeLayout(RelativeLayout relativeLayout) {
        this.relativeLayout = relativeLayout;
    }

    public RelativeLayout getCountMsgLayout() {
        return countMsgLayout;
    }

    public void setCountMsgLayout(RelativeLayout countMsgLayout) {
        this.countMsgLayout = countMsgLayout;
    }

    public TextView getCountMessageText() {
        return countMessageText;
    }

    public void setCountMessageText(TextView countMessageText) {
        this.countMessageText = countMessageText;
    }
}
