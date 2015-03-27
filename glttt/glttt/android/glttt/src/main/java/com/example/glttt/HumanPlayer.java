package com.example.glttt;

public class HumanPlayer implements IPlayer {

    private final GamePresenter mPresenter;

    public HumanPlayer( GamePresenter presenter ) {
        mPresenter = presenter;
    }

    @Override
    public int getMove( GamePresenter.PEG_SELECT_COLOUR colour ) {
        return mPresenter.getNextHumanMove( colour );
    }

    @Override
    public int getDelayAfterMoveInMillis() {
        return 2000;
    }
}
