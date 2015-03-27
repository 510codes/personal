package com.example.glttt;

public class ComputerPlayer implements IPlayer {

    private final GamePresenter mPresenter;

    public ComputerPlayer( GamePresenter presenter ) {
        mPresenter = presenter;
    }

    @Override
    public int getMove( GamePresenter.PEG_SELECT_COLOUR colour ) {
        return mPresenter.getNextComputerMove(colour);
    }

    @Override
    public int getDelayAfterMoveInMillis() {
        return 500;
    }
}
