package com.example.glttt;

public class HumanPlayer implements IPlayer {

    private final GamePresenter mPresenter;
    private final String mName;

    public HumanPlayer( GamePresenter presenter, String name ) {
        mPresenter = presenter;
        mName = name;
    }

    @Override
    public int getMove(long currentTimeInNanos) {
        return mPresenter.getNextHumanMove(currentTimeInNanos);
    }

    @Override
    public int getDelayAfterMoveInMillis() {
        return 2000;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void opponentMove(int peg) {
        // human player is not concerned with opponent move....
    }
}
