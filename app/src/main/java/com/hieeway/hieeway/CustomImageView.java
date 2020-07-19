package com.hieeway.hieeway;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class CustomImageView extends androidx.appcompat.widget.AppCompatImageView {

    public static float radius = 24.0f;

    public CustomImageView(Context context) {
        super(context);
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float radius = 24.0f;
        Path clipPath = new Path();
        RectF rect = new RectF(0,0, this.getWidth(), this.getHeight());

      //  RectF rect = new RectF(0, 0, 0, 0);

        clipPath.addRoundRect(rect, radius, radius, Path.Direction.CW);



        canvas.clipPath(clipPath);
        super.onDraw(canvas);
    }


}
