package com.shubhamkislay.jetpacklogin;


import android.content.Context;

import android.util.AttributeSet;


import android.os.Handler;


public class TypeWriter extends androidx.appcompat.widget.AppCompatTextView {

    private CharSequence mText;
    private int index;
    private long delay = 150;




    public TypeWriter(Context context) {
        super(context);
    }

    public TypeWriter(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    private Handler mHandler = new Handler();

    private Runnable characterAdder = new Runnable() {
        @Override
        public void run() {


            setText(mText.subSequence(0, index++));
            if (index <= mText.length())
                mHandler.postDelayed(characterAdder, delay);

        }
    };

    public void animateText(CharSequence txt)
    {
        mText = txt;
        index = 0;
        setText(" ");
        mHandler.removeCallbacks(characterAdder);

        mHandler.postDelayed(characterAdder,delay);
    }

    public void setCharacterDelay(long m)
    {
        delay = m;
    }

}