package com.apolis.android.swipingcontainer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
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

    private boolean mCyclic = false;

    public SwipingContainer(Context context) {
        super(context);
        init(context);
    }

    public SwipingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mHScrollPos = 0;
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
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                adjustChildren();
            }
        });
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        updateChildrenState();
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        if (!result
                && (event.getAction() == MotionEvent.ACTION_UP
                || event.getAction() == MotionEvent.ACTION_CANCEL)) {
            adjustChildren();
            result = true;
        }
        return result;
    }

    private void setHScrollPos(float hScrollPos) {
        if (isCyclic()) {
            int w = this.getWidth();
            int childCount = this.getChildCount();
            int totalW = w * childCount;
            hScrollPos = (hScrollPos + totalW) % totalW;
        }
        this.mHScrollPos = hScrollPos;
        if (mVisibleIndexChangeListener != null) {
            mVisibleIndexChangeListener.onVisibleIndexChanging(caculateVisibleIndexInFloat());
        }
        updateChildrenState();
    }

    private void updateChildrenState() {
        int w = this.getWidth();
        int childCount = this.getChildCount();
        int curVisibleIndex = (int) caculateVisibleIndexInFloat();
        float curVisibleChildX = w * curVisibleIndex - mHScrollPos;
        int rightIndex = -1;
        int leftIndex = -1;
        if (isCyclic()) {
            rightIndex = (curVisibleIndex + 1) % childCount;
            leftIndex = (curVisibleIndex - 1 + childCount) % childCount;
        } else {
            if (curVisibleIndex > 0) {
                leftIndex = curVisibleIndex - 1;
            }
            if (curVisibleIndex < childCount - 1) {
                rightIndex = curVisibleIndex + 1;
            }
        }
        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            if (i == curVisibleIndex) {
                child.setX(curVisibleChildX);
                child.setVisibility(VISIBLE);
            } else if (i == rightIndex
                    && curVisibleChildX < 0) {
                child.setX(curVisibleChildX + w);
                child.setVisibility(VISIBLE);
            } else if (i == leftIndex
                    && curVisibleChildX > 0) {
                child.setX(curVisibleChildX - w);
                child.setVisibility(VISIBLE);
            } else {
                child.setX(0);
                child.setVisibility(GONE);
            }
        }
    }

    private void adjustChildren() {
        int w = this.getWidth();
        int curChild = (int) (mHScrollPos / w);
        float dst;
        if (mHScrollPos - w * curChild > w / 2) {
            dst = w * (curChild + 1);
        } else {
            dst = w * curChild;
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
        if (isCyclic()) {
            index = mHScrollPos / w;
            index = ((int) (index * 10)) / 10.0f;
        } else {
            int childCount = this.getChildCount();
            if (mHScrollPos < 0) {
                index = 0;
            } else if (mHScrollPos > w * (childCount - 1)) {
                index = childCount - 1;
            } else {
                index = mHScrollPos / w;
                index = ((int) (index * 10)) / 10.0f;
            }
        }
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

    public static final int DIRECTION_RIGHT = 1;
    public static final int DIRECTION_LEFT = 2;
    public static final int DIRECTION_NEAREST = 3;

    public boolean swipeToIndex(int index, boolean animate) {
        return swipeToIndex(index, animate, DIRECTION_NEAREST);
    }

    public boolean swipeToIndex(int index, boolean animate, int direction) {
        boolean indexValid;
        int w = this.getWidth();
        float dstPos = w * index;
        if (isCyclic()) {
            indexValid = true;
            if (animate) {
                if (mHScrollPos < dstPos) {
                    if (direction == DIRECTION_RIGHT) {
                        startAdjustAnimation(mHScrollPos, dstPos);
                    } else if (direction == DIRECTION_LEFT) {
                        startAdjustAnimation(mHScrollPos + w * this.getChildCount(), dstPos);
                    } else if (direction == DIRECTION_NEAREST) {
                        if (Math.abs(dstPos - mHScrollPos) <= Math.abs(mHScrollPos + w * this.getChildCount() - dstPos)) {
                            startAdjustAnimation(mHScrollPos, dstPos);
                        } else {
                            startAdjustAnimation(mHScrollPos + w * this.getChildCount(), dstPos);
                        }
                    }
                } else {
                    if (direction == DIRECTION_RIGHT) {
                        startAdjustAnimation(mHScrollPos, dstPos + w * this.getChildCount());
                    } else if (direction == DIRECTION_LEFT) {
                        startAdjustAnimation(mHScrollPos, dstPos);
                    } else if (direction == DIRECTION_NEAREST) {
                        if (Math.abs(dstPos + w * this.getChildCount() - mHScrollPos) <= Math.abs(mHScrollPos - dstPos)) {
                            startAdjustAnimation(mHScrollPos, dstPos + w * this.getChildCount());
                        } else {
                            startAdjustAnimation(mHScrollPos, dstPos);
                        }
                    }
                }
            } else {
                setHScrollPos(dstPos);
                setVisibleIndex(index);
            }
        } else {
            indexValid = index >= 0 && index < this.getChildCount();
            if (indexValid) {
                if (animate) {
                    startAdjustAnimation(mHScrollPos, dstPos);
                } else {
                    setHScrollPos(dstPos);
                    setVisibleIndex(index);
                }
            }
        }
        return indexValid;
    }

    public boolean isCyclic() {
        return mCyclic;
    }

    public void setCyclic(boolean cyclic) {
        mCyclic = cyclic;
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
            if (!isCyclic()) {
                int w = SwipingContainer.this.getWidth();
                int childCount = SwipingContainer.this.getChildCount();
                if (mHScrollPos < 0 && distanceX < 0) {
                    factor = 1 + .5f * Math.abs(mHScrollPos);
                } else if (mHScrollPos > w * (childCount - 1) && distanceX > 0) {
                    factor = 1 + .5f * Math.abs(mHScrollPos - w * (childCount - 1));
                }
            }
            setHScrollPos(mHScrollPos + distanceX / factor);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            super.onFling(e1, e2, velocityX, velocityY);
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                int curVisibleIndex = (int) caculateVisibleIndexInFloat();
                int targetVisibleIndex;
                int direction = DIRECTION_RIGHT;
                if (velocityX > 0) {
                    direction = DIRECTION_LEFT;
                    targetVisibleIndex = curVisibleIndex;
                } else {
                    targetVisibleIndex = curVisibleIndex + 1;
                }
                if (!swipeToIndex(targetVisibleIndex, true, direction)) {
                    adjustChildren();
                }
                return true;
            } else {
                return false;
            }
        }
    }

    public interface VisibleIndexChangeListener {
        void onVisibleIndexChanging(float index);

        void onVisibleIndexChange(int index);
    }
}
