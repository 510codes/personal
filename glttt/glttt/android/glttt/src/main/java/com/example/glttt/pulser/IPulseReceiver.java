package com.example.glttt.pulser;

import com.example.glttt.Scene;

public interface IPulseReceiver {
    public void setScene( Scene scene );
    public void onPulse( long dtInNanos );
}
