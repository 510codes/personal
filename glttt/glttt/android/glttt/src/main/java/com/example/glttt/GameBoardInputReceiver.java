package com.example.glttt;

import android.util.Log;

import com.example.glttt.pulser.IPulseReceiver;

public class GameBoardInputReceiver implements IPulseReceiver, IGestureListener {
    private static final long NANOS_PER_SECOND = 1000000000;

    private static final float SWIPE_MASS = 1.0f;

    private static final float BOARD_MASS = 2.0f;
    private static final float BOARD_DAMPING_ACCELERATION = 400.0f;
    private static final float BOARD_VELOCITY_MIN = 10.0f;

    private static final float EYE_MASS = 20.0f;
    private static final float EYE_DAMPING_ACCELERATION = 30.0f;
    private static final float EYE_POS_Y_MAX = 15.0f;
    private static final float EYE_POS_Y_MIN = 1.8f;
    private static final float EYE_VELOCITY_MIN = 0.5f;
    private static final float EYE_POS_Z_MIN = 8.0f;
    private static final float EYE_POS_Z_MAX = 20.0f;
    private static final float EYE_LOOKAT_Z_MIN = -5.0f;
    private static final float EYE_LOOKAT_Z_MAX = 5.0f;

    private static final float SCALE_FACTOR_MAX = 5.0f;
    private static final float SCALE_FACTOR_MIN = 1.0f;
    private static final float SCALE_EXPONENT = 1.5f;

    private static final float[] BOARD_NORMAL = {0.0f, 1.0f, 0.0f, 0.0f};
    private static final float[] BOARD_P0 = {0.0f, 0.0f, 0.0f, 0.0f};

    private float mPosInDegrees;
    private float mBoardVelocity;
    private float mEyeVelocity;
    private float mScaleFactor;
    private Scene mScene;
    private ModelObject mTapDownObject;
    private float mEyePosY;

    public GameBoardInputReceiver() {
        mPosInDegrees = 0.0f;
        mBoardVelocity = 0.0f;
        mEyeVelocity = 0.0f;
        mScene = null;
        mTapDownObject = null;
        mScaleFactor = 1.0f;
        mEyePosY = 0.0f;
    }

    @Override
    public void setScene( Scene scene ) {
        mScene = scene;
    }

    @Override
    public synchronized void onPulse( long dtInNanos ) {
        processBoardPulse(dtInNanos);
        processEyePulse(dtInNanos);
    }

    private void processBoardPulse( long dtInNanos ) {
        if (Math.abs(mBoardVelocity) < BOARD_VELOCITY_MIN) {
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
        if (Math.abs(mEyeVelocity) < EYE_VELOCITY_MIN) {
            mEyeVelocity = 0.0f;
        }
        else {
            float dampingForce = EYE_MASS * EYE_DAMPING_ACCELERATION;
            float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
            if (mEyeVelocity > 0.0f) {
                dampingForce *= -1.0f;
            }
            addEyeMoveForce(dTimeInS, dampingForce);

            mEyePosY += mEyeVelocity * dTimeInS;
            if (mEyePosY > EYE_POS_Y_MAX) {
                mEyePosY = EYE_POS_Y_MAX;
                mEyeVelocity = 0.0f;
            }
            if (mEyePosY < EYE_POS_Y_MIN) {
                mEyePosY = EYE_POS_Y_MIN;
                mEyeVelocity = 0.0f;
            }

            float fact = (mEyePosY - EYE_POS_Y_MIN) / (EYE_POS_Y_MAX - EYE_POS_Y_MIN);
            float eyePosZ = EYE_POS_Z_MAX - ((EYE_POS_Z_MAX - EYE_POS_Z_MIN) * fact);
            float eyeLookAtZ = EYE_LOOKAT_Z_MAX - ((EYE_LOOKAT_Z_MAX - EYE_LOOKAT_Z_MIN) * fact);

            mScene.setEyePos(0.0f, mEyePosY, eyePosZ);
            mScene.setEyeLookAt(0.0f, 0.0f, eyeLookAtZ);

            Log.v("GameBoardInputReceiver", "dTimeInS: " + dTimeInS + ", deltaPos: " + (mEyeVelocity * dTimeInS) + ", mEyePosY: " + mEyePosY);
        }
    }

    @Override
    public synchronized void onSwipeGesture( float dTimeInS, long dx, long dy ) {
        if (mTapDownObject == null || !mTapDownObject.getId().equals("sphere")) {
            if (dTimeInS > 0.0f) {
                float dvBoard = (float) dx;       // dx should be a CHANGE in velocity here, so not sure about this
                float forceBoard = (dvBoard / dTimeInS) * SWIPE_MASS;
                addBoardSpinForce(dTimeInS, forceBoard);

                float dvEye = (float) dy;        // dy should be a CHANGE in velocity here, so not sure about this
                float forceEye = (dvEye / dTimeInS) * SWIPE_MASS;
                addEyeMoveForce(dTimeInS, forceEye);
            }
        }
    }

    @Override
    public void onTapDown( int x, int y ) {
        mTapDownObject = mScene.getClickedModelObject(x, y);
        if (mTapDownObject != null) {
            float[] pos = new float[4];
            float[] dir = new float[4];
            if (mScene.getClickPosition(mTapDownObject, x, y, pos, dir)) {
                ModelObject touchsphere3 = mScene.getObjectByName("touchsphere3");
                Transformation transformation = mTapDownObject.getTransformation();
                // TODO: not entirely sure about the use of translation here
                // is it correct to simply translate the object using the point
                // returned from getClickPosition()?
                transformation.translate(pos[0], pos[1], pos[2]);
                touchsphere3.setTransformation(transformation);

                if (mTapDownObject.getId().equals("sphere")) {
                    updateSpherePos(x, y);
                }
            }
        }
    }

    private void updateSpherePos(int xpos, int ypos) {
        float[] pos = new float[4];
        float[] dir = new float[4];
        ModelObject board = mScene.getObjectByName("board");
        if (mScene.getPlaneIntersection(board, xpos, ypos, BOARD_P0, BOARD_NORMAL, pos, dir)) {
            float[] vecSurface = new float[4];
            vecSurface[0] = dir[0];
            vecSurface[1] = 0.0f;
            vecSurface[2] = dir[2];
            vecSurface[3] = 0.0f;
            Math3d.normalize(vecSurface);

            float dotprod = Math3d.dotProduct(dir, vecSurface);
            float theta = (float) Math.acos(dotprod);
            float h = 1.25f / (float) Math.sin(theta);

            float x = pos[0] + (dir[0] * h);
            float y = pos[1] + (dir[1] * h);
            float z = pos[2] + (dir[2] * h);
            mTapDownObject.setTranslation(x, y, z);
        }
    }

    @Override
    public synchronized void onTapUp( int x, int y ) {
        ModelObject tapUpObject = mScene.getClickedModelObject(x, y);
        if (mTapDownObject == tapUpObject && mTapDownObject != null) {
            Log.d("GameBoardInputReceiver", "x: " + x + ", y: " + y + ", tapped on: " + mTapDownObject);
        }
        else {
            Log.d("GameBoardInputReceiver", "x: " + x + ", y: " + y + ", tapped on nothing");
        }

        mTapDownObject = null;
    }

    @Override
    public synchronized void onScaleGesture( float factor ) {
        mScaleFactor *= Math.pow(factor, SCALE_EXPONENT);

        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(SCALE_FACTOR_MIN, Math.min(mScaleFactor, SCALE_FACTOR_MAX));

        Log.v("GameBoardInputReceiver", "setting zoom factor: " + mScaleFactor);
        mScene.setZoomFactor(mScaleFactor);
    }

    @Override
    public synchronized void onPointerMove( int x, int y ) {
        if (mTapDownObject != null && mTapDownObject.getId().equals("sphere")) {
            updateSpherePos(x, y);
        }
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
