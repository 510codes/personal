package com.example.glttt;

public interface IGameView {
    public float getContentViewLeft();
    public float getContentViewTop();
    public void setCurrentScene( SceneFactory.TYPE type );
    public void setScaleFactor( float scaleFactor );
    public void setRotation( float degrees );
}
