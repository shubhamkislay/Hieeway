package com.shubhamkislay.jetpacklogin;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ScrollerCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ScrollerCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;


public class FlingableViewPager extends ViewPager
{
    private ScrollerCompat  mScroller;
    private VelocityTracker mVelocityTracker;
    private int             mMinimumFlingVelocity;
    private int             mMaximumFlingVelocity;
    private int             mLastFlingX;


    public FlingableViewPager(Context context)
    {
        this(context, null);
    }

    public FlingableViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mScroller = ScrollerCompat.create(getContext(), new DecelerateInterpolator());
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev)
    {
        boolean result = super.onTouchEvent(ev);

        PagerAdapter adapter = getAdapter();
        if(adapter == null || adapter.getCount() == 0){
            // Nothing to present or scroll; nothing to touch.
            return false;
        }

        final int action = ev.getAction();
        switch(action & MotionEventCompat.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
                if(mVelocityTracker == null){
                    mVelocityTracker = VelocityTracker.obtain();
                }
                else{
                    mVelocityTracker.clear();
                }
                mVelocityTracker.addMovement(ev);
                break;

            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(ev);
                break;

            case MotionEvent.ACTION_UP:
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(0, mMaximumFlingVelocity);
                int initialVelocityX = (int)velocityTracker.getXVelocity();
                if((Math.abs(initialVelocityX) > mMinimumFlingVelocity)){
                    startFling(initialVelocityX);
                    result = true;
                }
                break;

            case MotionEvent.ACTION_CANCEL:
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }

        return result;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        removeCallbacks(mFlingRunnable);
        super.onDetachedFromWindow();
    }

    public void startFling(int initialVelocityX)
    {
        if(initialVelocityX == 0){
            return;
        }
        removeCallbacks(mFlingRunnable);
        if(beginFakeDrag()){
            mScroller.abortAnimation();
            mLastFlingX = getScrollX();
            //fakeDragBy(0);
            mScroller.fling(mLastFlingX, 0, initialVelocityX, 0, 0, Integer.MAX_VALUE, 0, 0);
            ViewCompat.postOnAnimation(this, mFlingRunnable);
        }
    }

    private Runnable mFlingRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            boolean finished = mScroller.computeScrollOffset();
            int newX = mScroller.getCurrX();
            fakeDragBy(newX - mLastFlingX);
            if(finished){
                mLastFlingX = newX;
                ViewCompat.postOnAnimation(FlingableViewPager.this, this);
            }
            else{
                endFakeDrag();
            }
        }
    };
}