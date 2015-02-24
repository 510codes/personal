package com.example.glttt;

public interface IGestureListener {
    public void onSwipeGesture( float dTimeInS, long dx, long dy );
    public void onTapDown( int x, int y );
    public void onTapUp( int x, int y );
    public void onPointerMove( int x, int y );
    public void onScaleGesture( float factor );
}
