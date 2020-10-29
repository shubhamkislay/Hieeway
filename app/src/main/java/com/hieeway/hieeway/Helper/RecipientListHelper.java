package com.hieeway.hieeway.Helper;

import android.graphics.Bitmap;

import com.hieeway.hieeway.Model.Recipient;

import java.util.List;

public class RecipientListHelper {

    private List<Recipient> recipientList = null;
    public static final RecipientListHelper instance = new RecipientListHelper();


    public RecipientListHelper() {
    }

    public static RecipientListHelper getInstance() {
        return instance;
    }


    public List<Recipient> getRecipientList() {
        return recipientList;
    }

    public void setRecipientList(List<Recipient> recipientList) {
        this.recipientList = recipientList;
    }


}

