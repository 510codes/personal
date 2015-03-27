package com.example.glttt;

import android.util.Log;

public class TurnManager {

    private final Thread mLoopThread;

    private static class LoopThread extends Thread {
        private final IPlayer mPlayer1;
        private final IPlayer mPlayer2;
        private final GameBoard mGameBoard;
        private final GamePresenter mPresenter;
        private GamePresenter.PEG_SELECT_COLOUR mCurrentColour;

        LoopThread( IPlayer player1, IPlayer player2, GamePresenter.PEG_SELECT_COLOUR startColour,
                    GamePresenter presenter, GameBoard gameBoard ) {
            mPlayer1 = player1;
            mPlayer2 = player2;
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
                    int peg = mPlayer1.getMove();
                    int height = mGameBoard.moveOnPeg(peg, mCurrentColour);
                    if (height != -1) {
                        mPresenter.acceptMove(peg, height);
                        switchColour();
                        playerDone = true;

                        try {
                            Thread.sleep(mPlayer1.getDelayAfterMoveInMillis());
                        } catch (InterruptedException e) {}
                    }
                }

                logScore();

                mPresenter.initiateNextMove(mCurrentColour);
                playerDone = false;
                while (!playerDone) {
                    int peg = mPlayer2.getMove();
                    int height = mGameBoard.moveOnPeg(peg, mCurrentColour);
                    if (height != -1) {
                        mPresenter.acceptMove(peg, height);
                        switchColour();

                        playerDone = true;
                        try {
                            Thread.sleep(mPlayer2.getDelayAfterMoveInMillis());
                        } catch (InterruptedException e) {}
                    }
                }

                logScore();
            }
        }

        private void logScore() {
            int redScore = mGameBoard.getCompleteRows(GamePresenter.PEG_SELECT_COLOUR.RED);
            int whiteScore = mGameBoard.getCompleteRows(GamePresenter.PEG_SELECT_COLOUR.WHITE);
            Log.d("LoopThread", "logScore(), red: " + redScore + ", white: " + whiteScore);
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

    public TurnManager( IPlayer player1, IPlayer player2, GamePresenter.PEG_SELECT_COLOUR startColour, GamePresenter presenter, GameBoard gameBoard ) {
        mLoopThread = new LoopThread( player1, player2, startColour, presenter, gameBoard );
        mLoopThread.start();
    }
}
