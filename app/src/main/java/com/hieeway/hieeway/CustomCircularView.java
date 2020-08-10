package com.hieeway.hieeway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class CustomCircularView extends androidx.appcompat.widget.AppCompatImageView {

    public static float radius = 24.0f;

    public CustomCircularView(Context context) {
        super(context);
    }

    public CustomCircularView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomCircularView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        Path clipPath = new Path();
        clipPath.addArc(0, 0, this.getWidth(), this.getHeight(), 0, 360);


        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }


}
