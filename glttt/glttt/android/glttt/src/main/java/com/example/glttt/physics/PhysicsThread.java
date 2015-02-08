package com.example.glttt.physics;

import android.util.Log;

import com.example.glttt.GamePresenter;

class PhysicsThread extends Thread {

    private static final long NANOS_PER_SECOND = 1000000000;
    private static final float BOARD_MASS = 2.0f;
    private static final float DAMPING_ACCELERATION = 400.0f;

    private FpsManager mFpsManager;
    private GamePresenter mPresenter;

    private float mPosInDegrees;
    private float mVelocity;

    public PhysicsThread(GamePresenter presenter, int desiredPhysicsFps) {
        mPresenter = presenter;
        mFpsManager = new FpsManager(desiredPhysicsFps);

        mPosInDegrees = 0.0f;
        mVelocity = 0.0f;
    }

    public void run() {
        long lastInterval = -1;
        int curFrame = 0;

        try {
            Log.d("PhysicsThread", "waiting for view to become ready....");
            mPresenter.waitForViewReady();
            Log.d("PhysicsThread", "view is ready, starting loop");

            long currentTimeInNanos = System.nanoTime();
            long lastTimeInNanos = currentTimeInNanos;
            while (true) {
                curFrame++;
                long sleepTimeMs = mFpsManager.getNextWaitTimeMs(currentTimeInNanos);
                //Log.e("PhysicsThread", curFrame + " will sleep for " + sleepTimeMs + "ms");
                Thread.sleep(sleepTimeMs);
                long currentInterval = mFpsManager.getCurrentInterval();
                if (currentInterval != lastInterval) {
                    Log.v("PhysicsThread", "interval " + currentInterval);
                    lastInterval = currentInterval;
                    curFrame = 0;
                }

                doWork( currentTimeInNanos - lastTimeInNanos );

                lastTimeInNanos = currentTimeInNanos;
                currentTimeInNanos = System.nanoTime();
            }
        }

        catch (InterruptedException e) {
            Log.e("PhysicsThread", "caught interruptedexception, thread exiting");
        }
    }

    private void doWork( long dtInNanos ) {
        if (Math.abs(mVelocity) < 10.0f) {
            mVelocity = 0.0f;
        }
        else {
            float dampingForce = BOARD_MASS * DAMPING_ACCELERATION;
            float dTimeInS = (float)dtInNanos / (float)NANOS_PER_SECOND;
            if (mVelocity > 0.0f) {
                dampingForce *= -1.0f;
            }
            addForce(dTimeInS, dampingForce);

            float deltaDegrees = mVelocity * dTimeInS;
            mPosInDegrees += deltaDegrees;

            mPresenter.setSceneRotation(mPosInDegrees);

            Log.v("PhysicsThread", "dTimeInS: " + dTimeInS + ", deltaDegrees: " + deltaDegrees + ", mPosInDegrees: " + mPosInDegrees);
        }
    }

    public void addForce( float dTimeInS, float force ) {
        float acc = force / BOARD_MASS;
        float dv = acc * dTimeInS;
        mVelocity += dv;
        Log.v("PhysicsThread", "new force: dTimeInS: " + dTimeInS + ", force: " + force + ", velocity is now: " + mVelocity);
    }
}
