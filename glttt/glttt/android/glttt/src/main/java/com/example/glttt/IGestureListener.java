package com.example.glttt;

public interface IGestureListener {
    public void newSwipeGesture( float dTimeInS, long dx, long dy );
    public void tapDown( int x, int y );
    public void tapUp( int x, int y );
    public void newScaleGesture( float factor );
}
