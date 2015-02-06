package com.example.glttt;

public interface IGameView {
    public float getContentViewLeft();
    public float getContentViewTop();
    public ModelObject getClickedModelObject( int x, int y );
    public void setCurrentScene( SceneFactory.TYPE type);
}
