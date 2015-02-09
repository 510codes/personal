package com.example.glttt.pulser;

import android.util.Log;

class PulseThread extends Thread {
    private FpsManager mFpsManager;
    private IPulseReceiver mPulseReceiver;

    public PulseThread(int desiredFps) {
        mFpsManager = new FpsManager(desiredFps);
        mPulseReceiver = null;
    }

    public void run() {
        long lastInterval = -1;
        int curFrame = 0;

        try {
            long currentTimeInNanos = System.nanoTime();
            long lastTimeInNanos = currentTimeInNanos;
            while (true) {
                curFrame++;
                long sleepTimeMs = mFpsManager.getNextWaitTimeMs(currentTimeInNanos);
                //Log.e("PhysicsThread", curFrame + " will sleep for " + sleepTimeMs + "ms");
                Thread.sleep(sleepTimeMs);
                long currentInterval = mFpsManager.getCurrentInterval();
                if (currentInterval != lastInterval) {
                    Log.v("PulseThread", "interval " + currentInterval);
                    lastInterval = currentInterval;
                    curFrame = 0;
                }

                doWork( currentTimeInNanos - lastTimeInNanos );

                lastTimeInNanos = currentTimeInNanos;
                currentTimeInNanos = System.nanoTime();
            }
        }

        catch (InterruptedException e) {
            Log.e("PulseThread", "caught interruptedexception, thread exiting");
        }
    }

    private void doWork( long dtInNanos ) throws InterruptedException {
        getPulseReceiver().onPulse( dtInNanos );
    }

    public synchronized void setPulseReceiver( IPulseReceiver pulseReceiver ) {
        mPulseReceiver = pulseReceiver;
        notify();
    }

    private synchronized IPulseReceiver getPulseReceiver() throws InterruptedException {
        while (mPulseReceiver == null) {
            Log.d("PulseThread", "waiting for view to become ready....");
            wait();
            if (mPulseReceiver != null) {
                Log.d("PulseThread", "view is ready, starting loop");
            }
        }

        return mPulseReceiver;
    }
}
