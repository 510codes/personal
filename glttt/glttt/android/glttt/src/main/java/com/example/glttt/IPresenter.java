package com.example.glttt;

import com.example.glttt.shader.IShader;

public interface IPresenter {

    public void pegSelected( int peg );
    public GamePresenter.PEG_SELECT_COLOUR getCurrentTurnColour();
    public Scene getCurrentScene();
    public void setZoomFactor( float zf );
    public void setYRotation( float yr );
    public void drawScene( IShader shader );
    public void onViewportChanged( int[] viewport );
    public void setGameStateListener( IGameStateListener listener );
}
