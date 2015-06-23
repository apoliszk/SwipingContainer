package com.apolis.android.swipingcontainer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class GradientButton extends View {
    private Drawable mDrawableStart;
    private Drawable mDrawableEnd;

    private float mRatio;

    public void setRatio(float ratio) {
        if (ratio > mMaxIndex && mIndex == 0) {
            ratio = 1 - (ratio - mMaxIndex);
        }
        ratio = 1 - Math.abs(ratio - mIndex);
        if (ratio < 0) {
            ratio = 0;
        }
        if (mRatio != ratio) {
            mRatio = ratio;
            this.invalidate();
        }
    }

    private int mIndex;

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    private int mMaxIndex;

    public void setMaxIndex(int maxIndex) {
        mMaxIndex = maxIndex;
    }

    public GradientButton(Context context) {
        super(context);
        init(null, 0);
    }

    public GradientButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public GradientButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GradientButton, defStyle, 0);

        if (a.hasValue(R.styleable.GradientButton_drawableStart)) {
            mDrawableStart = a.getDrawable(R.styleable.GradientButton_drawableStart);
        }
        if (a.hasValue(R.styleable.GradientButton_drawableEnd)) {
            mDrawableEnd = a.getDrawable(R.styleable.GradientButton_drawableEnd);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int minW = getPaddingLeft() + getPaddingRight();
        if (mDrawableStart != null) {
            minW = Math.max(minW, getPaddingLeft() + getPaddingRight() + mDrawableStart.getIntrinsicWidth());
        }
        int w = resolveSize(minW, widthMeasureSpec);

        int minH = getPaddingBottom() + getPaddingTop();
        if (mDrawableStart != null) {
            minH += mDrawableStart.getIntrinsicHeight();
        }
        int h = resolveSize(minH, heightMeasureSpec);

        setMeasuredDimension(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();

        int contentWidth = getWidth() - paddingLeft - paddingRight;

        if (mDrawableStart != null) {
            int imageX = paddingLeft + (contentWidth - mDrawableStart.getIntrinsicWidth()) / 2;
            mDrawableStart.setAlpha((int) ((1 - mRatio) * 255));
            mDrawableStart.setBounds(imageX, paddingTop, imageX + mDrawableStart.getIntrinsicWidth(), paddingTop + mDrawableStart.getIntrinsicHeight());
            if (mDrawableEnd != null) {
                mDrawableEnd.setAlpha((int) (mRatio * 255));
                mDrawableEnd.setBounds(mDrawableStart.getBounds());
                mDrawableEnd.draw(canvas);
            }
            mDrawableStart.draw(canvas);
        }
    }
}
