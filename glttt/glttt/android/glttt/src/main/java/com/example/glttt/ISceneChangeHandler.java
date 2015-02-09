package com.example.glttt;

public interface ISceneChangeHandler {
    public void onViewportChanged( int width, int height );
    public void setScene( Scene scene );
}
