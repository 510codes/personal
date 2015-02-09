package com.example.glttt;

import android.util.Log;

public class GameBoardInputReceiver implements IPulseReceiver, IGestureListener {
    private static final long NANOS_PER_SECOND = 1000000000;
    private static final float BOARD_MASS = 2.0f;
    private static final float SWIPE_MASS = 1.0f;
    private static final float DAMPING_ACCELERATION = 400.0f;

    private float mPosInDegrees;
    private float mBoardVelocity;
    private float mScaleFactor;
    private Scene mScene;
    private ModelObject mTapDownObject;

    public GameBoardInputReceiver() {
        mPosInDegrees = 0.0f;
        mBoardVelocity = 0.0f;
        mScene = null;
        mTapDownObject = null;
        mScaleFactor = 1.0f;
    }

    @Override
    public void setScene( Scene scene ) {
        mScene = scene;
    }

    @Override
    public synchronized void onPulse( long dtInNanos ) {
        if (Math.abs(mBoardVelocity) < 10.0f) {
            mBoardVelocity = 0.0f;
        }
        else {
            float dampingForce = BOARD_MASS * DAMPING_ACCELERATION;
            float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
            if (mBoardVelocity > 0.0f) {
                dampingForce *= -1.0f;
            }
            addForce(dTimeInS, dampingForce);

            float deltaDegrees = mBoardVelocity * dTimeInS;
            mPosInDegrees += deltaDegrees;

            mScene.setYRotation(mPosInDegrees);

            Log.v("GameBoardInputReceiver", "dTimeInS: " + dTimeInS + ", deltaDegrees: " + deltaDegrees + ", mPosInDegrees: " + mPosInDegrees);
        }
    }

    @Override
    public synchronized void newSwipeGesture( float dTimeInS, long dx, long dy ) {
        if (dTimeInS > 0.0f) {
            float dv = (float)dx;       // dx should be a CHANGE in velocity here, so not sure about this
            float force = (dv / dTimeInS) * SWIPE_MASS;

            Log.v("GameBoardInputReceiver", "new force: " + force);
            addForce(dTimeInS, force);
        }
    }

    @Override
    public void tapDown( int x, int y ) {
        mTapDownObject = mScene.getClickedModelObject(x, y);
    }

    @Override
    public void tapUp( int x, int y ) {
        ModelObject tapUpObject = mScene.getClickedModelObject(x, y);
        if (mTapDownObject == tapUpObject && mTapDownObject != null) {
            Log.v("GameBoardInputReceiver", "x: " + x + ", y: " + y + ", tapped on: " + mTapDownObject);
        }
        else {
            Log.v("GameBoardInputReceiver", "x: " + x + ", y: " + y + ", tapped on nothing");
        }

        mTapDownObject = null;
    }

    @Override
    public synchronized void newScaleGesture( float factor ) {
        mScaleFactor *= Math.pow(factor, 1.5);

        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

        mScene.setZoomFactor(mScaleFactor);
    }

    private void addForce( float dTimeInS, float force ) {
        float acc = force / BOARD_MASS;
        float dv = acc * dTimeInS;
        mBoardVelocity += dv;
        Log.v("GameBoardInputReceiver", "new force: dTimeInS: " + dTimeInS + ", force: " + force + ", mBoardVelocity is now: " + mBoardVelocity);
    }
}
