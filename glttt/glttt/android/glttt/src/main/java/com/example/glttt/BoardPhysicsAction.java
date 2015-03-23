package com.example.glttt;

import android.util.Log;

public class BoardPhysicsAction implements IPhysicsAction {
    private float mPosInDegrees;
    private final Scene mScene;

    public BoardPhysicsAction( Scene scene ) {
        mPosInDegrees = 0.0f;
        mScene = scene;
    }

    @Override
    public synchronized void onVelocityChange( float dTimeInS, float vel ) {
        float deltaDegrees = vel * dTimeInS;
        mPosInDegrees += deltaDegrees;

        mScene.setYRotation(mPosInDegrees);

        Log.v("BoardPhysicsAction", "dTimeInS: " + dTimeInS + ", deltaDegrees: " + deltaDegrees + ", mPosInDegrees: " + mPosInDegrees);
    }
}
