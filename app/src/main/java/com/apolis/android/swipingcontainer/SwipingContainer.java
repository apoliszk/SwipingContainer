package com.apolis.android.swipingcontainer;

import android.animation.Animator;
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
    private static final int animateDuration = 400;

    private float mHScrollPos;

    private GestureDetector mGestureDetector;
    private ValueAnimator mAnimator;

    private int mVisibleIndex;
    private VisibleIndexChangeListener mVisibleIndexChangeListener;

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
        mVisibleIndex = 0;

        mGestureDetector = new GestureDetector(context, new SwipingContainerGestureListener());

        mAnimator = ObjectAnimator.ofFloat();
        mAnimator.setInterpolator(sDecelerate);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float curValue = (float) animation.getAnimatedValue();
                setHScrollPos(curValue);
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                adjustChildren();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateChildrenState();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        if (!result
                && (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            adjustChildren();
            result = true;
        }
        return result;
    }

    private void setHScrollPos(float HScrollPos) {
        this.mHScrollPos = HScrollPos;
        if (mVisibleIndexChangeListener != null) {
            mVisibleIndexChangeListener.onVisibleIndexChanging(caculateVisibleIndexInFloat());
        }
        updateChildrenState();
    }

    private void updateChildrenState() {
        int w = this.getWidth();
        int curVisibleIndex = (int) caculateVisibleIndexInFloat();
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
                child.setX(0);
                child.setVisibility(GONE);
            }
        }
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
            setVisibleIndex((int) (dst / w));
        } else {
            startAdjustAnimation(mHScrollPos, dst);
        }
    }

    private void startAdjustAnimation(float src, float dst) {
        stopAdjustAnimation();

        int w = this.getWidth();
        int duration = (int) (Math.abs(src - dst) * animateDuration / w);
        if (duration < 100) {
            duration = 100;
        }
        mAnimator.setFloatValues(src, dst);
        mAnimator.setDuration(duration).start();
    }

    private void stopAdjustAnimation() {
        if (mAnimator.isRunning()) {
            mAnimator.cancel();
        }
    }

    private float caculateVisibleIndexInFloat() {
        int w = this.getWidth();
        float index;
        if (mHScrollPos < 0) {
            index = 0;
        } else {
            index = mHScrollPos / w;
        }
        if (index > this.getChildCount() - 1) {
            index = this.getChildCount() - 1;
        }
        index = ((int) (index * 10)) / 10.0f;
        return index;
    }

    private void setVisibleIndex(int visibleIndex) {
        if (mVisibleIndex != visibleIndex) {
            mVisibleIndex = visibleIndex;
            if (mVisibleIndexChangeListener != null) {
                mVisibleIndexChangeListener.onVisibleIndexChange(mVisibleIndex);
            }
        }
    }

    public boolean swipeToIndex(int index, boolean animate) {
        boolean indexValid = index >= 0 && index < this.getChildCount();
        if (indexValid) {
            int w = this.getWidth();
            if (animate) {
                startAdjustAnimation(mHScrollPos, index * w);
            } else {
                setHScrollPos(index * w);
                setVisibleIndex(index);
            }
        }
        return indexValid;
    }

    public VisibleIndexChangeListener getVisibleIndexChangeListener() {
        return mVisibleIndexChangeListener;
    }

    public void setVisibleIndexChangeListener(VisibleIndexChangeListener visibleIndexChangeListener) {
        mVisibleIndexChangeListener = visibleIndexChangeListener;
    }

    public int getVisibleIndex() {
        return mVisibleIndex;
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
                factor = 1 + .5f * Math.abs(mHScrollPos);
            } else if (mHScrollPos > w * (childCount - 1) && distanceX > 0) {
                factor = 1 + .5f * Math.abs(mHScrollPos - w * (childCount - 1));
            }
            setHScrollPos(mHScrollPos + distanceX / factor);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            super.onFling(e1, e2, velocityX, velocityY);

            int curVisibleIndex = (int) caculateVisibleIndexInFloat();
            int targetVisibleIndex;
            if (velocityX > 0) {
                targetVisibleIndex = curVisibleIndex;
            } else {
                targetVisibleIndex = curVisibleIndex + 1;
            }
            if (!swipeToIndex(targetVisibleIndex, true)) {
                adjustChildren();
            }
            return true;
        }
    }

    public interface VisibleIndexChangeListener {
        void onVisibleIndexChanging(float index);

        void onVisibleIndexChange(int index);
    }
}
