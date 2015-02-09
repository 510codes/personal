package com.example.glttt.pulser;

public class PulseManager {

    private PulseThread mPulseThread;

    public PulseManager( int desiredFps ) {
        mPulseThread = new PulseThread(desiredFps);
        mPulseThread.start();
    }

    public void setPulseReceiver( IPulseReceiver pulseReceiver ) {
        mPulseThread.setPulseReceiver(pulseReceiver);
    }
}
