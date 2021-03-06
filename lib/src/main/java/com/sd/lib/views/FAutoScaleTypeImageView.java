package com.sd.lib.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 根据图片宽高比例和控件宽高比例的差值，自动改变{@link ScaleType}
 * <br><br>
 * 差值小于设置的值，按比例拉伸展示（默认{@link ScaleType#CENTER_CROP）；大于等于设置的值，则按比例展示（默认{@link ScaleType#FIT_CENTER））
 */
public class FAutoScaleTypeImageView extends AppCompatImageView
{
    public FAutoScaleTypeImageView(Context context)
    {
        super(context);
    }

    public FAutoScaleTypeImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public FAutoScaleTypeImageView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    private float mWHScaleDelta = 0.2f;
    private ScaleTypeHandler mScaleTypeHandler;

    /**
     * 图片的宽高比例和控件的宽高比例的差值
     *
     * @param delta
     */
    public void setWHScaleDelta(float delta)
    {
        if (mWHScaleDelta != delta)
        {
            mWHScaleDelta = delta;
            changeScaleType(getDrawable());
        }
    }

    /**
     * 设置{@link ScaleTypeHandler}
     *
     * @param handler
     */
    public void setScaleTypeHandler(ScaleTypeHandler handler)
    {
        mScaleTypeHandler = handler;
    }

    private ScaleTypeHandler getScaleTypeHandler()
    {
        if (mScaleTypeHandler == null)
        {
            mScaleTypeHandler = new ScaleTypeHandler()
            {
                @Override
                public ScaleType onStretch()
                {
                    return ScaleType.CENTER_CROP;
                }

                @Override
                public ScaleType onFit()
                {
                    return ScaleType.FIT_CENTER;
                }
            };
        }
        return mScaleTypeHandler;
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable)
    {
        changeScaleType(drawable);
        super.setImageDrawable(drawable);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        if (changed)
            changeScaleType(getDrawable());
    }

    private boolean changeScaleType(Drawable drawable)
    {
        if (drawable == null)
            return false;

        final int drawableWidth = drawable.getIntrinsicWidth();
        final int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth <= 0 || drawableHeight <= 0)
            return false;

        final int width = getWidth();
        if (width <= 0)
            return false;

        final int height = getHeight();
        if (height <= 0)
            return false;

        final float whScale = (float) width / height;
        final float drawableScale = (float) drawableWidth / drawableHeight;

        final float deltaScale = Math.abs(whScale - drawableScale);
        if (deltaScale < mWHScaleDelta)
        {
            final ScaleType scaleTypeTarget = getScaleTypeHandler().onStretch();
            if (scaleTypeTarget != null && scaleTypeTarget != getScaleType())
            {
                setScaleType(scaleTypeTarget);
                return true;
            }
        } else
        {
            final ScaleType scaleTypeTarget = getScaleTypeHandler().onFit();
            if (scaleTypeTarget != null && scaleTypeTarget != getScaleType())
            {
                setScaleType(scaleTypeTarget);
                return true;
            }
        }

        return false;
    }

    public interface ScaleTypeHandler
    {
        /**
         * 宽高比例的差值小于设置的值，此时拉伸展示
         *
         * @return
         */
        ScaleType onStretch();

        /**
         * 宽高比例的差值大于设置的值，此时按比例展示
         *
         * @return
         */
        ScaleType onFit();
    }
}
