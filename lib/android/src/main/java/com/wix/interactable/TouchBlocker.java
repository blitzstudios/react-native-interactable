package com.wix.interactable;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by zachik on 28/03/2017.
 */

public class TouchBlocker extends ViewGroup {
    public final static String TAG = "TouchBlocker";

    private boolean blockAllTouch = false;
    private boolean blockVerticalInteraction = false;
    private boolean blockHorizontalInteraction = false;

    public void setBlockAllTouch(boolean blockAllTouch) {
        this.blockAllTouch = blockAllTouch;
    }

    public void setBlockVerticalInteraction(boolean blockVerticalInteraction) {
        this.blockVerticalInteraction = blockVerticalInteraction;
    }

    public void setBlockHorizontalInteraction(boolean blockHorizontalInteraction) {
        this.blockHorizontalInteraction = blockHorizontalInteraction;
    }

    public TouchBlocker(Context context) {
        super(context);
        setTag(TAG);
    }

    public TouchBlocker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setTag(TAG);
    }

    public TouchBlocker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTag(TAG);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }

    private float xDistance, yDistance, lastX, lastY;

    int lastEvent = -1;
    boolean isLastEventIntercepted = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (blockAllTouch) {
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (blockVerticalInteraction || blockHorizontalInteraction) {
            switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                lastX = curX;
                lastY = curY;

                if(isLastEventIntercepted && lastEvent == MotionEvent.ACTION_MOVE){
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return super.onInterceptTouchEvent(ev);
                }

                float larger = 0, smaller = 0;

                if (blockVerticalInteraction) {
                    larger = yDistance;
                    smaller = xDistance;
                } else if (blockHorizontalInteraction) {
                    larger = xDistance;
                    smaller = yDistance;
                }

                if (larger > smaller) {
                    isLastEventIntercepted = true;
                    lastEvent = MotionEvent.ACTION_MOVE;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return super.onInterceptTouchEvent(ev);
                }
                break;
            }

            lastEvent=ev.getAction();
            isLastEventIntercepted=false;
        }
        
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    public boolean isAtTop() {
        return getChildAt(0).getScrollY() == 0;
    }
}
