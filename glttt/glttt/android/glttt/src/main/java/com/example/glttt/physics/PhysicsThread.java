package com.example.glttt.physics;

import android.util.Log;

class PhysicsThread extends Thread {

    private FpsManager mFpsManager;

    public PhysicsThread(int desiredPhysicsFps) {
        mFpsManager = new FpsManager(desiredPhysicsFps);
    }

    public void run() {
        boolean exitThread = false;
        long lastInterval = -1;
        int curFrame = 0;

        while (!exitThread) {
            try {
                curFrame++;
                long sleepTimeMs = mFpsManager.getNextWaitTimeMs(System.nanoTime());
                //Log.e("PhysicsThread", curFrame + " will sleep for " + sleepTimeMs + "ms");
                Thread.sleep(sleepTimeMs);
                long currentInterval = mFpsManager.getCurrentInterval();
                if (currentInterval != lastInterval) {
                    Log.e("PhysicsThread", "interval " + currentInterval);
                    lastInterval = currentInterval;
                    curFrame = 0;
                }

                doWork();
            }
            catch (InterruptedException e) {
                exitThread = true;
                Log.e("PhysicsThread", "caught interruptedexception, thread exiting");
            }
        }
    }

    private void doWork() {
        // do some physics processing, etc.
    }
}
