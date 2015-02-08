package com.example.glttt.physics;

import android.util.Log;

class FpsManager {
    private static final long NANOS_PER_SECOND = 1000000000;
    private static final long NANOS_PER_MS = 1000000;

    private long mSimulationStartTimeNanos;
    private long mCurrentTimeNanos;
    private final long mIntervalLengthNanos;
    private final long mStepLengthNanos;
    private final long mMinSleepTime;

    FpsManager( int requestedFps ) {
        mCurrentTimeNanos = -1;
        mIntervalLengthNanos = NANOS_PER_SECOND;
        mSimulationStartTimeNanos = -1;
        mStepLengthNanos = mIntervalLengthNanos / (long)requestedFps;
        mMinSleepTime = mStepLengthNanos / 10;
    }

    public long getNextWaitTimeMs( long currentTimeNanos ) {
        if (mSimulationStartTimeNanos == -1) {
            mSimulationStartTimeNanos = currentTimeNanos;
        }
        mCurrentTimeNanos = currentTimeNanos;

        return (getNextStepTime() - mCurrentTimeNanos) / NANOS_PER_MS;
    }

    public long getCurrentInterval() {
        long elapsedTimeNanos = (mCurrentTimeNanos - mSimulationStartTimeNanos);
        long currentInterval = elapsedTimeNanos / mIntervalLengthNanos;
        while ((currentInterval * mIntervalLengthNanos) - mCurrentTimeNanos < mMinSleepTime) {
            currentInterval++;
        }

        return currentInterval - (mSimulationStartTimeNanos / mIntervalLengthNanos);
    }

    private long getNextStepTime() {
        long elapsedTime = mCurrentTimeNanos - mSimulationStartTimeNanos;
        long nextStepIndex = elapsedTime / mStepLengthNanos;
        long timeToNextStepNanos;
        long nextStepTime;
        do {
            nextStepIndex++;
            nextStepTime = mSimulationStartTimeNanos + (nextStepIndex * mStepLengthNanos);
            timeToNextStepNanos = nextStepTime - mCurrentTimeNanos;
            //Log.e("FpsManager", "nextStepIndex: " + nextStepIndex + ", mStepLengthNanos: " + mStepLengthNanos + ", timeToNextStepNanos: " + timeToNextStepNanos + ", nextStepTime: " + nextStepTime);
        }
        while (timeToNextStepNanos < mMinSleepTime);

        return nextStepTime;
    }

    public long getCurrentTimeNanos() {
        return mCurrentTimeNanos;
    }
}
