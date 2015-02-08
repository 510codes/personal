package com.example.glttt.physics;

import android.util.Log;

import com.example.glttt.GamePresenter;

public class PhysicsManager {

    private float mPos;
    private float mVel;

    private PhysicsThread mPhysicsThread;

    public PhysicsManager( GamePresenter presenter ) {
        mPos = 0.0f;
        mVel = 0.0f;

        mPhysicsThread = new PhysicsThread(presenter, 60);     // run the physics thread at ~60 fps
        mPhysicsThread.start();
    }

    public void newSwipeMotion( long dTime, long dx, long dy ) {
        Log.v("PhysicsManager", "newSwipeMotion(): dTime: " + dTime + ", dx: " + dx + ", mVel: " + mVel);
        mPos = mPos + (mVel * dTime);
        float acc = (dx - mVel) / dTime;
        mVel = mVel + (acc * dTime);
        int degrees = (int)mPos % 360;
        Log.v("PhysicsManager", "newSwipeMotion(): mPos: " + mPos + ", mVel: " + mVel + ", acc: " + acc + ", degrees: " + degrees);
    }
}
