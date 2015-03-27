package com.example.glttt;

public class TurnManager {

    private final Thread mLoopThread;

    private static class LoopThread extends Thread {
        private boolean mDone;
        private final IPlayer mPlayer1;
        private final IPlayer mPlayer2;
        private final GamePresenter.PEG_SELECT_COLOUR[][] mBoardState;
        private int mMoveCount;
        private GamePresenter.PEG_SELECT_COLOUR mCurrentColour;

        LoopThread( IPlayer player1, IPlayer player2, GamePresenter.PEG_SELECT_COLOUR startColour,
                    GamePresenter.PEG_SELECT_COLOUR[][] boardState ) {
            mDone = false;
            mPlayer1 = player1;
            mPlayer2 = player2;
            mBoardState = boardState;
            mMoveCount = 0;
            mCurrentColour = startColour;
        }

        @Override
        public void run() {
            while (!mDone) {
                int peg = mPlayer1.getMove(mCurrentColour);
                if (!addPeg(peg, mCurrentColour)) {
                    mDone = true;
                }

                mMoveCount++;
                switchColour();

                try {
                    Thread.sleep(mPlayer1.getDelayAfterMoveInMillis());
                }
                catch (InterruptedException e) {}

                peg = mPlayer2.getMove(mCurrentColour);
                if (!addPeg(peg, mCurrentColour)) {
                    mDone = true;
                }

                mMoveCount++;
                switchColour();

                try {
                    Thread.sleep(mPlayer2.getDelayAfterMoveInMillis());
                }
                catch (InterruptedException e) {}
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

        private boolean addPeg( int peg, GamePresenter.PEG_SELECT_COLOUR colour ) {
            for (int i=0; i<3; ++i) {
                if (mBoardState[peg][i] == GamePresenter.PEG_SELECT_COLOUR.NONE) {
                    mBoardState[peg][i] = colour;
                    return true;
                }
            }

            return false;
        }
    }

    public TurnManager( IPlayer player1, IPlayer player2, GamePresenter.PEG_SELECT_COLOUR startColour, GamePresenter.PEG_SELECT_COLOUR[][] boardState ) {
        mLoopThread = new LoopThread( player1, player2, startColour, boardState );
        mLoopThread.start();
    }
}
