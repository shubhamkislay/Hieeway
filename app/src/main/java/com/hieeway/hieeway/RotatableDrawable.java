package com.hieeway.hieeway;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.RequiresApi;


@RequiresApi(api = Build.VERSION_CODES.M)
public class RotatableDrawable extends DrawableWrapper {

    private float rotation;
    private Rect bounds;
    private ObjectAnimator animator;
    private long defaultAnimationDuration;

    public RotatableDrawable(Resources resources, Drawable drawable) {
        super(vectorToBitmapDrawableIfNeeded(resources, drawable));
        bounds = new Rect();
        defaultAnimationDuration = resources.getInteger(android.R.integer.config_mediumAnimTime);
    }

    /**
     * Workaround for issues related to vector drawables rotation and scaling:
     * https://code.google.com/p/android/issues/detail?id=192413
     * https://code.google.com/p/android/issues/detail?id=208453
     */
    private static Drawable vectorToBitmapDrawableIfNeeded(Resources resources, Drawable drawable) {
        if (drawable instanceof VectorDrawable) {
            Bitmap b = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            drawable.setBounds(0, 0, c.getWidth(), c.getHeight());
            drawable.draw(c);
            drawable = new BitmapDrawable(resources, b);
        }
        return drawable;
    }

    @Override
    public void draw(Canvas canvas) {
        copyBounds(bounds);
        canvas.save();
        canvas.rotate(rotation, bounds.centerX(), bounds.centerY());
        super.draw(canvas);
        canvas.restore();
    }

    public void rotate(float degrees) {
        rotate(degrees, defaultAnimationDuration);
    }

    public void rotate(float degrees, long millis) {
        if (null != animator && animator.isStarted()) {
            animator.end();
        } else if (null == animator) {
            animator = ObjectAnimator.ofFloat(this, "rotation", 0, 0);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        animator.setFloatValues(rotation, degrees);
        animator.setDuration(millis).start();
    }

    public void setRotation(float degrees) {
        this.rotation = degrees % 360;
        invalidateSelf();
    }
}
