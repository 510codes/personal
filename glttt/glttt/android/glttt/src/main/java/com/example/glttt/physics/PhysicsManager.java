package com.example.glttt.physics;

import android.util.Log;

import com.example.glttt.GamePresenter;

public class PhysicsManager {

    private static final float SWIPE_MASS = 1.0f;

    private float mVel;

    private PhysicsThread mPhysicsThread;

    public PhysicsManager( GamePresenter presenter ) {
        mVel = 0.0f;

        mPhysicsThread = new PhysicsThread(presenter, 60);     // run the physics thread at ~60 fps
        mPhysicsThread.start();
    }

    public void newSwipeMotion( float dTimeInS, long dx, long dy ) {
        if (dTimeInS > 0.0f) {
            float dv = dx - mVel;
            float force = (dv / dTimeInS) * SWIPE_MASS;

            Log.v("PhysicsManager", "new force: " + force);
            mPhysicsThread.addForce(dTimeInS, force);
        }
    }
}
