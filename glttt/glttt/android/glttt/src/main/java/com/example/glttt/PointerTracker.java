package com.example.glttt;

import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

public class PointerTracker {

    private static final int INVALID_POINTER_ID = -1;

    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private int mActivePointerId = INVALID_POINTER_ID;

    public boolean onTouchEvent( MotionEvent e, ScaleGestureDetector scaleDetector ) {
        // Let the ScaleGestureDetector inspect all events.
        scaleDetector.onTouchEvent(e);

        final int action = e.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = e.getX();
                final float y = e.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = e.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = e.findPointerIndex(mActivePointerId);
                final float x = e.getX(pointerIndex);
                final float y = e.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!scaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;
                    Log.e("game", "ACTION_MOVE: mPosX: " + mPosX + ", mPosY:" + mPosY);
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
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
                    Log.e("game", "ACTION_POINTER_UP: mLastTouchX: " + mLastTouchX + ", mLastTouchY:" + mLastTouchY);
                }
                break;
            }
        }

        return true;
    }
}
