package com.shubhamkislay.jetpacklogin;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class FeelingDialog extends Dialog {


    public FeelingDialog( Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.reveal_message_dialog);
    }
}
