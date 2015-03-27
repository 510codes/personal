package com.example.glttt;

public class HumanPlayer implements IPlayer {

    private final GamePresenter mPresenter;

    public HumanPlayer( GamePresenter presenter ) {
        mPresenter = presenter;
    }

    @Override
    public int getMove() {
        return mPresenter.getNextHumanMove();
    }

    @Override
    public int getDelayAfterMoveInMillis() {
        return 2000;
    }
}
