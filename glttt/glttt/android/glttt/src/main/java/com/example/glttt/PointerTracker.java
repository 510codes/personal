package com.example.glttt;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class PointerTracker {

    private static final int INVALID_POINTER_ID = -1;

    private GamePresenter mPresenter;

    private float mLastTouchX;
    private float mLastTouchY;
    private long mLastEventTimeInMs;
    private int mActivePointerId = INVALID_POINTER_ID;

    public PointerTracker( GamePresenter presenter ) {
        mPresenter = presenter;
    }

    public boolean onTouchEvent( MotionEvent e, ScaleGestureDetector scaleDetector ) {
        // Let the ScaleGestureDetector inspect all events.
        scaleDetector.onTouchEvent(e);

        final int action = e.getAction();
        final long currentEventTimeInMs = e.getEventTime();
        final long dTimeInMs_ = currentEventTimeInMs - mLastEventTimeInMs;
        final float dTimeInS = (float)dTimeInMs_ / 1000.0f;

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                mLastEventTimeInMs = e.getEventTime();
                final float x = e.getX();
                final float y = e.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = e.getPointerId(0);
                Log.v("PointerTracker", "ACTION_DOWN: mActivePointerReview: " + mActivePointerId);
                mPresenter.tapDown((int)x, (int)y);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = e.findPointerIndex(mActivePointerId);
                final float x = e.getX(pointerIndex);
                final float y = e.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!scaleDetector.isInProgress()) {
                    final long dx = (long)x - (long)mLastTouchX;
                    final long dy = (long)y - (long)mLastTouchY;

                    Log.v("PointerTracker", "ACTION_MOVE: dx: " + dx + ", dy: " + dy);
                    mPresenter.newSwipeMotion(dTimeInS, dx, dy);
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mPresenter.tapUp((int)e.getX(), (int)e.getY());
                mActivePointerId = INVALID_POINTER_ID;
                Log.v("PointerTracker", "ACTION_UP");
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = e.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = e.getX(newPointerIndex);
                    mLastTouchY = e.getY(newPointerIndex);
                    mActivePointerId = e.getPointerId(newPointerIndex);
                    Log.v("PointerTracker", "ACTION_POINTER_UP: mLastTouchX: " + mLastTouchX + ", mLastTouchY:" + mLastTouchY);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN: {
                Log.v("PointerTracker", "ACTION_POINTER_DOWN: mLastTouchX: " + mLastTouchX + ", mLastTouchY:" + mLastTouchY);
            }
        }

        mLastEventTimeInMs = currentEventTimeInMs;

        return true;
    }
}
