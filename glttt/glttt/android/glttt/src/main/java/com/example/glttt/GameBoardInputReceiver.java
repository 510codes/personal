package com.example.glttt;

import android.util.Log;

import com.example.glttt.pulser.IPulseReceiver;

public class GameBoardInputReceiver implements IPulseReceiver, IGestureListener {
    private static final long NANOS_PER_SECOND = 1000000000;

    private static final float SWIPE_MASS = 1.0f;

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

    private float mScaleFactor;
    private Scene mScene;
    private ModelObject mTapDownObject;
    private float mEyePosY;
    private PhysicsAttribs mEyePhysicsAttribs;

    public GameBoardInputReceiver() {
        mScene = null;
        mTapDownObject = null;
        mScaleFactor = 1.0f;
        mEyePosY = 0.0f;
        mEyePhysicsAttribs = new PhysicsAttribs(EYE_MASS, EYE_VELOCITY_MIN, 0.0f, EYE_DAMPING_ACCELERATION);
    }

    @Override
    public void setScene( Scene scene ) {
        mScene = scene;
    }

    @Override
    public synchronized void onPulse( long dtInNanos ) {
        processEyePulse(dtInNanos);
        float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
        ModelObject obj = mScene.getObjectByName("board");
        if (obj != null) {
            obj.updatePhysics(dTimeInS);
        }

        obj = mScene.getObjectByName("sphere");
        if (obj != null) {
            obj.updatePhysics(dTimeInS);
        }
    }

    private void processEyePulse( long dtInNanos ) {
        float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
        float vel = mEyePhysicsAttribs.onDeltaTime(dTimeInS);

        if (vel != 0.0f) {
            mEyePosY += vel * dTimeInS;
            if (mEyePosY > EYE_POS_Y_MAX) {
                mEyePosY = EYE_POS_Y_MAX;
                mEyePhysicsAttribs.setVelocity(0.0f);
            }
            if (mEyePosY < EYE_POS_Y_MIN) {
                mEyePosY = EYE_POS_Y_MIN;
                mEyePhysicsAttribs.setVelocity(0.0f);
            }

            updateEye();

            Log.v("GameBoardInputReceiver", "dTimeInS: " + dTimeInS + ", deltaPos: " + (vel * dTimeInS) + ", mEyePosY: " + mEyePosY);
        }
    }

    private void updateEye() {
        float fact = (mEyePosY - EYE_POS_Y_MIN) / (EYE_POS_Y_MAX - EYE_POS_Y_MIN);

        float eyePosZ = EYE_POS_Z_MAX - ((EYE_POS_Z_MAX - EYE_POS_Z_MIN) * fact);
        //eyePosZ -= (mScaleFactor * 2.0f);

        float eyeLookAtZ = EYE_LOOKAT_Z_MAX - ((EYE_LOOKAT_Z_MAX - EYE_LOOKAT_Z_MIN) * fact);
        //eyeLookAtZ -= (mScaleFactor * 2.0f);

        //Log.d("GameBoardInputReceiver", "updateEye(): mEyePosY: " + mEyePosY + ", eyePosZ: " + eyePosZ + ", eyeLookAtZ: " + eyeLookAtZ + ", mScaleFactor: " + mScaleFactor);
        mScene.setEyePos(0.0f, mEyePosY, eyePosZ);
        mScene.setEyeLookAt(0.0f, 0.0f, eyeLookAtZ);
    }

    @Override
    public synchronized void onSwipeGesture( float dTimeInS, long dx, long dy ) {
        if (mTapDownObject == null || !mTapDownObject.getId().equals("sphere")) {
            if (dTimeInS > 0.0f) {
                // dx should be a CHANGE in velocity here, so not sure about this
                PhysicsAttribs boardPhysicsAttribs = mScene.getObjectByName("board").getPhysicsAttribs();
                boardPhysicsAttribs.addForce(dTimeInS, (float) dx, SWIPE_MASS);
                mScene.getObjectByName("board").setPhysicsAttribs(boardPhysicsAttribs);

                // dy should be a CHANGE in velocity here, so not sure about this
                mEyePhysicsAttribs.addForce(dTimeInS, (float)dy, SWIPE_MASS);
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
            if (mTapDownObject.getId().equals("sphere")) {
                int peg = getPegIntersection(mTapDownObject);
                Log.d("GameBoardInputReceiver", "onTapUp: tapped on peg: " + peg);
                if (peg != -1) {
                    addSphereToPeg(mTapDownObject, peg);
                }
            }
        }
        else {
            Log.d("GameBoardInputReceiver", "x: " + x + ", y: " + y + ", tapped on nothing");
        }

        mTapDownObject = null;
    }

    private void addSphereToPeg( ModelObject sphere, int peg ) {
        ModelObject pegObj = mScene.getObjectByName("peg" + peg);
        Transformation pegTransformation = pegObj.getTransformation();
        sphere.setTranslation(pegTransformation.getTranslationX(), 1.25f, pegTransformation.getTranslationZ());
        PhysicsAttribs spherePhysicsAttribs = new PhysicsAttribs(2.0f, 0.0f, 2.0f, 0.0f);
        sphere.setPhysicsAttribs(spherePhysicsAttribs);
        sphere.setPhysicsAction(new SphereDropPhysicsAction(sphere));
    }

    private int getPegIntersection( ModelObject sphere ) {
        float[] sphereOrigin = sphere.getOriginInModelspace();
        int peg = -1;
        float[] vec = new float[4];
        Math3d.vector(vec, sphere.getExtentVertexInModelspace(), sphere.getOriginInModelspace());
        float r = Math3d.vectorlength(vec);

        for (int i=0; i<8; ++i) {
            ModelObject pegObj = mScene.getObjectByName("peg" + i);
            float[] pegOrigin = pegObj.getOriginInModelspace();
            float[] pegUp = pegObj.getUpVectorInModelspace();
            if (Math3d.getSphereIntersection(sphereOrigin, r, pegOrigin, pegUp)) {
                peg = i;
                break;
            }
        }

        return peg;
    }

    @Override
    public synchronized void onScaleGesture( float factor ) {
        mScaleFactor *= Math.pow(factor, SCALE_EXPONENT);

        // Don't let the object get too small or too large.
        mScaleFactor = Math.max(SCALE_FACTOR_MIN, Math.min(mScaleFactor, SCALE_FACTOR_MAX));

        Log.v("GameBoardInputReceiver", "setting zoom factor: " + mScaleFactor);
        mScene.setZoomFactor(mScaleFactor);
        //updateEye();
    }

    @Override
    public synchronized void onPointerMove( int x, int y ) {
        if (mTapDownObject != null && mTapDownObject.getId().equals("sphere")) {
            updateSpherePos(x, y);
        }
    }
}
