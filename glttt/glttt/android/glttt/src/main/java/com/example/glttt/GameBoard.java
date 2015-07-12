package com.example.glttt;

public class GameBoard {

    private final GamePresenter.PEG_SELECT_COLOUR[][] mBoardState;
    private int mTurnCount;

    public GameBoard() {
        mTurnCount = 0;
        mBoardState = new GamePresenter.PEG_SELECT_COLOUR[8][3];
        for (int i=0; i<8; ++i) {
            for (int j=0; j<3; ++j) {
                mBoardState[i][j] = GamePresenter.PEG_SELECT_COLOUR.NONE;
            }
        }
    }

    public GameBoard( GameBoard board ) {
        mTurnCount = 0;
        mBoardState = new GamePresenter.PEG_SELECT_COLOUR[8][3];
        for (int i=0; i<8; ++i) {
            for (int j=0; j<3; ++j) {
                mBoardState[i][j] = board.getPegSpotColour(i, j);
            }
        }
    }

    private int findRowsVertical( int peg, GamePresenter.PEG_SELECT_COLOUR colour )
    {
        if (mBoardState[peg][0] == colour && mBoardState[peg][1] == colour && mBoardState[peg][2] == colour)
            return 1;
        else
            return 0;
    }

    private int findRows(int peg1, int peg2, int peg3, GamePresenter.PEG_SELECT_COLOUR colour)
    {
        int n;
        int x;

        n=0;

        if (colour != GamePresenter.PEG_SELECT_COLOUR.NONE)
        {
            for (x=0; x<3; x++)
                if (mBoardState[peg1][x] == colour && mBoardState[peg2][x] == colour && mBoardState[peg3][x] == colour)
                    n++;

            if (mBoardState[peg2][1]==colour)
            {
                if (mBoardState[peg1][2]==colour && mBoardState[peg3][0]==colour)
                    n++;
                if (mBoardState[peg3][2]==colour && mBoardState[peg1][0]==colour)
                    n++;
            }
        }

        return n;
    }

    public boolean isPegFull( int peg ) {
        for (int i=0; i<3; ++i) {
            System.out.println("GameBoard.isPegFull(" + peg + "): mBoardState[" + peg + "][" + i + "]: " + mBoardState[peg][i]);
            if (mBoardState[peg][i] == GamePresenter.PEG_SELECT_COLOUR.NONE) {
                System.out.println("GameBoard.isPegFull(" + peg + "): returning false");
                return false;
            }
        }

        System.out.println("GameBoard.isPegFull(" + peg + "): returning true");
        return true;
    }

    public int getCompleteRows( GamePresenter.PEG_SELECT_COLOUR colour )
    {
        int n;
        int x;

        n = findRows(5, 6, 7, colour);
        n += findRows(5, 3, 1, colour);
        n += findRows(6, 4, 2, colour);
        n += findRows(0, 1, 2, colour);
        n += findRows(0, 3, 6, colour);
        n += findRows(1, 4, 7, colour);

        for (x=0; x<8; x++)
            n+=findRowsVertical(x, colour);

        return n;
    }

    public int moveOnPeg( int peg, GamePresenter.PEG_SELECT_COLOUR colour) {
        for (int i=0; i<3; ++i) {
            if (mBoardState[peg][i] == GamePresenter.PEG_SELECT_COLOUR.NONE) {
                mBoardState[peg][i] = colour;
                mTurnCount++;
                return i;
            }
        }

        return -1;
    }

    public GamePresenter.PEG_SELECT_COLOUR getPegSpotColour( int peg, int height ) {
        return mBoardState[peg][height];
    }

    public boolean isGameDone() {
        return mTurnCount == 24;
    }

    public int rating( GamePresenter.PEG_SELECT_COLOUR col, GamePresenter.PEG_SELECT_COLOUR oppcol ) {
        return getCompleteRows(col) - getCompleteRows(oppcol);
    }
}
