package com.example.glttt.pulser;

import com.example.glttt.GamePresenter;
import com.example.glttt.IPulseReceiver;

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
