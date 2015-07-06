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
            boolean done = false;

            while (!done) {
                processTurnForPlayer(mPlayer1);
                done = processTurnForPlayer(mPlayer2);
            }
        }

        private boolean processTurnForPlayer(IPlayer player) {
            mPresenter.initiateNextMove(mCurrentColour);
            boolean playerDone = false;
            while (!playerDone) {
                int peg = player.getMove(System.nanoTime());
                int scoreBefore = mGameBoard.getCompleteRows(mCurrentColour);
                int height = mGameBoard.moveOnPeg(peg, mCurrentColour);
                if (height != -1) {
                    int deltaScore = mGameBoard.getCompleteRows(mCurrentColour) - scoreBefore;
                    mPresenter.acceptMove(peg, height, deltaScore, player, System.nanoTime());
                    switchColour();
                    playerDone = true;

                    try {
                        Thread.sleep(player.getDelayAfterMoveInMillis());
                    } catch (InterruptedException e) {}
                }
            }

            return mGameBoard.isGameDone();
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
