package com.shubhamkislay.jetpacklogin;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.core.widget.ScrollerCompat;
import android.util.AttributeSet;

import android.view.MotionEvent;

import android.view.View;


public  class VerticalViewPager extends ViewPager {


    public static final float MIN_SCALE = 1.0f;
    public VerticalViewPager(@NonNull Context context) {
        super(context);
        init();
    }

    public VerticalViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init()
    {
        setPageTransformer(true,new VerticalPage());

        setOverScrollMode(OVER_SCROLL_NEVER);

        setNestedScrollingEnabled(false);
        setOffscreenPageLimit(1);

    }


    private MotionEvent getIntercambioXY(MotionEvent ev){


        float width = getWidth();
        float height = getHeight();

        float newX = (ev.getY()/height) * width;
        float newY = (ev.getX()/width) * height;


        ev.setLocation(newX,newY);
        return ev;

    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean intercepted = super.onInterceptTouchEvent(getIntercambioXY(ev));

        getIntercambioXY(ev);


        return intercepted;

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {


        final boolean toHandle = super.onTouchEvent(getIntercambioXY(event));

        getIntercambioXY(event);
        return toHandle;



    }

    private class VerticalPage implements ViewPager.PageTransformer{


        @Override
        public void transformPage(@NonNull View view, float position) {



            if(position < -1)
            {
                view.setAlpha(0);
            }else if(position <= 1)
            {
                view.setAlpha(1);
                view.setTranslationX(view.getWidth() * -position);
                view.setTranslationY(view.getHeight() * position);



                view.setScaleX(1);
                view.setScaleY(1);

            }/*else if(position<=1)
            {
                view.setAlpha(1-position);
                view.setTranslationX(view.getWidth() * -position);
                view.setTranslationY(0);
                float scaleFactor = MIN_SCALE + (MIN_SCALE-1.0f)*(1-Math.abs(position));

                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            }*/else //if(position > 1)
            {
                 view.setAlpha(0);
            }
        }

    }












}
