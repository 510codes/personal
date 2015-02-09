package com.example.glttt;

public interface IPulseReceiver {
    public void setScene( Scene scene );
    public void onPulse( long dtInNanos );
}
