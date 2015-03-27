package com.example.glttt;

import android.util.Log;

public class ComputerPlayer implements IPlayer {

    private final GameBoard mGameBoard;

    public ComputerPlayer( GameBoard gameBoard ) {
        mGameBoard = gameBoard;
    }

    @Override
    public int getMove() {
        for (int i=0; i<8; ++i) {
            for (int j=0; j<3; ++j) {
                if (mGameBoard.getPegSpotColour(i, j) == GamePresenter.PEG_SELECT_COLOUR.NONE) {
                    Log.d("ComputerPlayer", "getMove(): chose peg: " + i);
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    public int getDelayAfterMoveInMillis() {
        return 500;
    }
}
