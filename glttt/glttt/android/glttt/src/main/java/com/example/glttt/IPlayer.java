package com.example.glttt;

public interface IPlayer {
    public int getMove( GamePresenter.PEG_SELECT_COLOUR colour );
    public int getDelayAfterMoveInMillis();
}
