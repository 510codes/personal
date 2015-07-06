package com.example.glttt;

import android.util.Log;

public class ComputerPlayer implements IPlayer {

    private final GameBoard mGameBoard;
    private final String mName;

    public ComputerPlayer( GameBoard gameBoard, String name ) {
        mGameBoard = gameBoard;
        mName = name;
    }

    @Override
    public int getMove(long currentTimeInNanos) {
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

    @Override
    public String getName() {
        return mName;
    }
}
