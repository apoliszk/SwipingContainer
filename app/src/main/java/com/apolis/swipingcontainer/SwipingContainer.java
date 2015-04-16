package com.apolis.swipingcontainer;

import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

public class SwipingContainer extends FrameLayout {
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();

    private GestureDetector mGestureDetector;
    private float mHScrollPos;
    private ValueAnimator mAnimator;
    private ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;

    public SwipingContainer(Context context) {
        super(context);
        init(context);
    }

    public SwipingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mHScrollPos = .0f;

        mGestureDetector = new GestureDetector(context, new SwipingContainerGestureListener());
        mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                setHScrollPos(curValue);
            }
        };
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateChildrenState();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            adjustChildren();
        }
        return mGestureDetector.onTouchEvent(event);
    }

    public void setHScrollPos(float HScrollPos) {
        this.mHScrollPos = HScrollPos;
        updateChildrenState();
    }

    private void updateChildrenState() {
        int w = this.getWidth();
        int curVisibleIndex = getVisibleIndex();
        for (int i = 0, len = this.getChildCount(); i < len; i++) {
            View child = this.getChildAt(i);
            if (i == curVisibleIndex) {
                child.setX(w * curVisibleIndex - mHScrollPos);
                child.setVisibility(VISIBLE);
            } else if (i == curVisibleIndex + 1
                    && mHScrollPos > 0
                    && Math.abs(mHScrollPos - w * curVisibleIndex) > 1) {
                child.setX(w * (curVisibleIndex + 1) - mHScrollPos);
                child.setVisibility(VISIBLE);
            } else {
                child.setVisibility(GONE);
            }
        }
    }

    public int getVisibleIndex() {
        int w = this.getWidth();
        int index;
        if (mHScrollPos < 0) {
            index = 0;
        } else {
            index = (int) (mHScrollPos / w);
        }
        return index;
    }

    private void adjustChildren() {
        int w = this.getWidth();
        int childCount = this.getChildCount();

        float dst;
        if (mHScrollPos < 0) {
            dst = 0;
        } else if (mHScrollPos > w * (childCount - 1)) {
            dst = w * (childCount - 1);
        } else {
            int curChild = (int) (mHScrollPos / w);
            if (mHScrollPos - w * curChild > w / 2) {
                dst = w * (curChild + 1);
            } else {
                dst = w * curChild;
            }
        }

        if (Math.abs(dst - mHScrollPos) < 5) {
            setHScrollPos(dst);
        } else {
            startAdjustAnimation(mHScrollPos, dst);
        }
    }

    private void startAdjustAnimation(float src, float dst) {
        stopAdjustAnimation();
        mAnimator = ObjectAnimator.ofFloat(src, dst).setDuration(500);
        mAnimator.setInterpolator(sDecelerate);
        mAnimator.addUpdateListener(mAnimatorUpdateListener);
        mAnimator.start();
    }

    private void stopAdjustAnimation() {
        if (mAnimator != null) {
            mAnimator.cancel();
            mAnimator.removeAllUpdateListeners();
        }
        mAnimator = null;
    }

    class SwipingContainerGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            super.onDown(e);
            stopAdjustAnimation();
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            super.onScroll(e1, e2, distanceX, distanceY);
            float factor = 1;
            int w = SwipingContainer.this.getWidth();
            int childCount = SwipingContainer.this.getChildCount();
            if (mHScrollPos < 0 && distanceX < 0) {
                factor = 1 + Math.abs(mHScrollPos);
            } else if (mHScrollPos > w * (childCount - 1) && distanceX > 0) {
                factor = 1 + Math.abs(mHScrollPos - w * (childCount - 1));
            }
            setHScrollPos(mHScrollPos + distanceX / factor);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            super.onFling(e1, e2, velocityX, velocityY);

            int w = SwipingContainer.this.getWidth();
            int curVisibleIndex = getVisibleIndex();

            int targetVisibleIndex;
            if (velocityX > 0) {
                targetVisibleIndex = curVisibleIndex;
            } else {
                targetVisibleIndex = curVisibleIndex + 1;
            }

            if (targetVisibleIndex >= 0
                    && targetVisibleIndex < SwipingContainer.this.getChildCount()) {
                startAdjustAnimation(mHScrollPos, targetVisibleIndex * w);
            }
            return true;
        }
    }
}
