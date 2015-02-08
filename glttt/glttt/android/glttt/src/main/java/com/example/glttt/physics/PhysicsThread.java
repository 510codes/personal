package com.example.glttt.physics;

import android.util.Log;

import com.example.glttt.GamePresenter;

class PhysicsThread extends Thread {

    private static final long NANOS_PER_MS = 1000000;

    private FpsManager mFpsManager;
    private GamePresenter mPresenter;

    public PhysicsThread(GamePresenter presenter, int desiredPhysicsFps) {
        mPresenter = presenter;
        mFpsManager = new FpsManager(desiredPhysicsFps);
    }

    public void run() {
        boolean exitThread = false;
        long lastInterval = -1;
        int curFrame = 0;

        try {
            Log.d("PhysicsThread", "waiting for view to become ready....");
            mPresenter.waitForViewReady();
            Log.d("PhysicsThread", "view is ready, starting loop");

            while (true) {
                curFrame++;
                long sleepTimeMs = mFpsManager.getNextWaitTimeMs(System.nanoTime());
                //Log.e("PhysicsThread", curFrame + " will sleep for " + sleepTimeMs + "ms");
                Thread.sleep(sleepTimeMs);
                long currentInterval = mFpsManager.getCurrentInterval();
                if (currentInterval != lastInterval) {
                    Log.v("PhysicsThread", "interval " + currentInterval);
                    lastInterval = currentInterval;
                    curFrame = 0;
                }

                doWork();
            }
        }

        catch (InterruptedException e) {
            Log.e("PhysicsThread", "caught interruptedexception, thread exiting");
        }
    }

    private void doWork() {
        float degrees = ((mFpsManager.getCurrentTimeNanos() / (NANOS_PER_MS * 20))) % 360;
        mPresenter.setSceneRotation(degrees);
    }
}
