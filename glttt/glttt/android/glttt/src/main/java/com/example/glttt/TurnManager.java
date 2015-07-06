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
                processTurnForPlayer(mRedPlayer);
                processTurnForPlayer(mWhitePlayer);
            }
        }

        private void processTurnForPlayer(IPlayer player) {
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
