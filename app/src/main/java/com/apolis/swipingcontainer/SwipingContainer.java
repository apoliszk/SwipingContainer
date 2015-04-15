package com.apolis.swipingcontainer;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

public class SwipingContainer extends FrameLayout {
    private static final String TAG = "SwipingContainer";

    private GestureDetector mGestureDetector;

    private float mHScrollPos = .0f;

    public SwipingContainer(Context context) {
        super(context);
        init(context);
    }

    public SwipingContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mGestureDetector = new GestureDetector(context, new SwipingContainerGestureListener());
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
            return true;
        }
        return mGestureDetector.onTouchEvent(event);
    }

    public void setHScrollPos(float HScrollPos) {
        this.mHScrollPos = HScrollPos;
        updateChildrenState();
    }

    private void updateChildrenState() {
        int w = this.getWidth();
        int curVisibleIndex;
        if (mHScrollPos < 0) {
            curVisibleIndex = 0;
        } else {
            curVisibleIndex = (int) (mHScrollPos / w);
        }
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

    private void adjustChildren() {
        int w = this.getWidth();
        int childCount = this.getChildCount();
        if (mHScrollPos < 0) {
            mHScrollPos = 0;
        } else if (mHScrollPos > w * (childCount - 1)) {
            mHScrollPos = w * (childCount - 1);
        } else {
            int curChild = (int) (mHScrollPos / w);
            if (mHScrollPos - w * curChild > w / 2) {
                mHScrollPos = w * (curChild + 1);
            } else {
                mHScrollPos = w * curChild;
            }
        }
        updateChildrenState();
    }

    class SwipingContainerGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            super.onDown(e);
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
            Log.d(TAG, "velocityX = " + velocityX);
            return true;
        }
    }
}
