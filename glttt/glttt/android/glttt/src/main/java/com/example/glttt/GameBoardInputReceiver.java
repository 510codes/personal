package com.example.glttt;

import android.util.Log;

import com.example.glttt.pulser.IPulseReceiver;

public class GameBoardInputReceiver implements IPulseReceiver, IGestureListener {
    private static final long NANOS_PER_SECOND = 1000000000;
    private static final float SWIPE_MASS = 1.0f;
    private static final float BOARD_MASS = 2.0f;
    private static final float BOARD_DAMPING_ACCELERATION = 400.0f;
    private static final float EYE_MASS = 20.0f;
    private static final float EYE_DAMPING_ACCELERATION = 30.0f;

    private float mPosInDegrees;
    private float mBoardVelocity;
    private float mEyeVelocity;
    private float mScaleFactor;
    private Scene mScene;
    private float[] mEyePos;
    private ModelObject mTapDownObject;

    public GameBoardInputReceiver() {
        mPosInDegrees = 0.0f;
        mBoardVelocity = 0.0f;
        mEyeVelocity = 0.0f;
        mScene = null;
        mEyePos = null;
        mTapDownObject = null;
        mScaleFactor = 1.0f;
    }

    @Override
    public void setScene( Scene scene ) {
        mScene = scene;
        mEyePos = scene.getEyePos();
    }

    @Override
    public synchronized void onPulse( long dtInNanos ) {
        processBoardPulse(dtInNanos);
        processEyePulse(dtInNanos);
    }

    private void processBoardPulse( long dtInNanos ) {
        if (Math.abs(mBoardVelocity) < 10.0f) {
            mBoardVelocity = 0.0f;
        }
        else {
            float dampingForce = BOARD_MASS * BOARD_DAMPING_ACCELERATION;
            float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
            if (mBoardVelocity > 0.0f) {
                dampingForce *= -1.0f;
            }
            addBoardSpinForce(dTimeInS, dampingForce);

            float deltaDegrees = mBoardVelocity * dTimeInS;
            mPosInDegrees += deltaDegrees;

            mScene.setYRotation(mPosInDegrees);

            Log.v("GameBoardInputReceiver", "dTimeInS: " + dTimeInS + ", deltaDegrees: " + deltaDegrees + ", mPosInDegrees: " + mPosInDegrees);
        }
    }

    private void processEyePulse( long dtInNanos ) {
        if (Math.abs(mEyeVelocity) < 0.5f) {
            mEyeVelocity = 0.0f;
        }
        else {
            float dampingForce = EYE_MASS * EYE_DAMPING_ACCELERATION;
            float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
            if (mEyeVelocity > 0.0f) {
                dampingForce *= -1.0f;
            }
            addEyeMoveForce(dTimeInS, dampingForce);

            float deltaPos = mEyeVelocity * dTimeInS;
            mEyePos[1] += deltaPos;
            if (mEyePos[1] > 7.0f) {
                mEyePos[1] = 7.0f;
                mEyeVelocity = 0.0f;
            }
            if (mEyePos[1] < 1.8f) {
                mEyePos[1] = 1.8f;
                mEyeVelocity = 0.0f;
            }

            mScene.setEyePos(mEyePos);

            Log.v("GameBoardInputReceiver", "dTimeInS: " + dTimeInS + ", deltaPos: " + deltaPos + ", mEyePos[1]: " + mEyePos[1]);
        }
    }

    @Override
    public synchronized void newSwipeGesture( float dTimeInS, long dx, long dy ) {
        if (dTimeInS > 0.0f) {
            float dvBoard = (float)dx;       // dx should be a CHANGE in velocity here, so not sure about this
            float forceBoard = (dvBoard / dTimeInS) * SWIPE_MASS;

            Log.v("GameBoardInputReceiver", "new board force: " + forceBoard);
            addBoardSpinForce(dTimeInS, forceBoard);

            float dvEye = (float)dy;        // dy should be a CHANGE in velocity here, so not sure about this
            float forceEye = (dvEye / dTimeInS) * SWIPE_MASS;

            Log.v("GameBoardInputReceiver", "new eye force: " + forceEye);
            addEyeMoveForce(dTimeInS, forceEye);
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

    private synchronized void addBoardSpinForce( float dTimeInS, float force ) {
        float acc = force / BOARD_MASS;
        float dv = acc * dTimeInS;
        mBoardVelocity += dv;
        Log.v("GameBoardInputReceiver", "new board force: dTimeInS: " + dTimeInS + ", force: " + force + ", mBoardVelocity is now: " + mBoardVelocity);
    }

    private synchronized void addEyeMoveForce( float dTimeInS, float force ) {
        float acc = force / EYE_MASS;
        float dv = acc * dTimeInS;
        mEyeVelocity += dv;
        Log.v("GameBoardInputReceiver", "new eye force: dTimeInS: " + dTimeInS + ", force: " + force + ", mEyeVelocity is now: " + mEyeVelocity);
    }
}
