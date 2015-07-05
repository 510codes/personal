package com.example.glttt;

public class TurnManager {

    private final Thread mLoopThread;

    private static class LoopThread extends Thread {
        private final IPlayer mRedPlayer;
        private final IPlayer mWhitePlayer;
        private final GameBoard mGameBoard;
        private final GamePresenter mPresenter;
        private GamePresenter.PEG_SELECT_COLOUR mCurrentColour;

        LoopThread( IPlayer redPlayer, IPlayer whitePlayer, GamePresenter.PEG_SELECT_COLOUR startColour,
                    GamePresenter presenter, GameBoard gameBoard ) {
            mRedPlayer = redPlayer;
            mWhitePlayer = whitePlayer;
            mGameBoard = gameBoard;
            mPresenter = presenter;
            mCurrentColour = startColour;
        }

        @Override
        public void run() {
            while (true) {
                mPresenter.initiateNextMove(mCurrentColour);
                boolean playerDone = false;
                while (!playerDone) {
                    int peg = mRedPlayer.getMove();
                    int height = mGameBoard.moveOnPeg(peg, mCurrentColour);
                    if (height != -1) {
                        mPresenter.acceptMove(peg, height);
                        switchColour();
                        playerDone = true;

                        try {
                            Thread.sleep(mRedPlayer.getDelayAfterMoveInMillis());
                        } catch (InterruptedException e) {}
                    }
                }

                mPresenter.initiateNextMove(mCurrentColour);
                playerDone = false;
                while (!playerDone) {
                    int peg = mWhitePlayer.getMove();
                    int height = mGameBoard.moveOnPeg(peg, mCurrentColour);
                    if (height != -1) {
                        mPresenter.acceptMove(peg, height);
                        switchColour();

                        playerDone = true;
                        try {
                            Thread.sleep(mWhitePlayer.getDelayAfterMoveInMillis());
                        } catch (InterruptedException e) {}
                    }
                }
            }
        }

        private void switchColour() {
            if (mCurrentColour == GamePresenter.PEG_SELECT_COLOUR.RED) {
                mCurrentColour = GamePresenter.PEG_SELECT_COLOUR.WHITE;
            }
            else {
                mCurrentColour = GamePresenter.PEG_SELECT_COLOUR.RED;
            }
        }
    }

    public TurnManager( IPlayer redPlayer, IPlayer whitePlayer, GamePresenter.PEG_SELECT_COLOUR startColour, GamePresenter presenter, GameBoard gameBoard ) {
        mLoopThread = new LoopThread( redPlayer, whitePlayer, startColour, presenter, gameBoard );
        mLoopThread.start();
    }
}
