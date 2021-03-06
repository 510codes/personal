package com.example.glttt.ai;

import android.util.Log;

import com.example.glttt.GameBoard;
import com.example.glttt.GamePresenter;

public class TreeNode {
    private TreeNode mParent;
    private GameBoard mGameBoard;
    private TreeNode[] mChild;
    private String mName;

    private static void printStackTrace(StackTraceElement[] ste) {
        for (StackTraceElement s : ste) {
            String str = s.toString();
            if (str.startsWith("com.example")) {
                System.out.println(str);
            }
        }
    }

    public TreeNode() {
        this(new GameBoard());
    }

    public TreeNode( GameBoard board, String name ) {
        mGameBoard = new GameBoard(board);
        mChild = new TreeNode[8];
        mName = name;
    }

    public TreeNode( String name ) {
        this(new GameBoard(), name);
    }

    public TreeNode( GameBoard board ) {
        this(board, null);
    }

    public TreeNode( TreeNode parent, int childIndex ) {
        this(parent, childIndex, null);
    }

    public TreeNode( TreeNode parent, int childIndex, String name ) {
        this(parent.mGameBoard, name);
        if (parent.mChild[childIndex] != null) {
            throw new RuntimeException("child at index " + childIndex + " already exists");
        }
        mParent = parent;
        mParent.mChild[childIndex] = this;
    }

    TreeNode getParent() {
        return mParent;
    }

    TreeNode getChild( int index ) {
        return mChild[index];
    }

    public void detach() {
        if (mParent != null) {
            for (int i=0; i<8; ++i) {
                if (mParent.mChild[i] == this) {
                    mParent.mChild[i] = null;
                }
            }
            mParent = null;
        }
    }

    public void prune() {
        for (int i=0; i<8; ++i) {
            if (mChild[i] != null) {
                mChild[i].mParent = null;
                mChild[i] = null;
            }
        }
    }

    public int level() {
        if (mParent == null) {
            return 0;
        }

        return 1 + mParent.level();
    }

    public int size() {
        int count = 1;

        for (int i=0; i<8; ++i) {
            if (mChild[i] != null) {
                count += mChild[i].size();
            }
        }

        return count;
    }

    public int missingChild() {
        int mc = 0;

        while (mc < 8 && (mChild[mc] != null || mGameBoard.isPegFull(mc))) {
            mc++;
        }

        int retVal =  (mc < 8 ? mc : -1);

        return retVal;
    }

    public TreeNode findIncomplete( int desiredLevel, int currentLevel, GamePresenter.PEG_SELECT_COLOUR rootMoveColour ) {
        TreeNode retNode = null;

        if (desiredLevel == currentLevel) {
            int mc = missingChild();
            if (mc >= 0) {
                retNode = this;
            }
        }
        else {
            int i = 0;
            TreeNode fn2 = null;
            while (i < 8 && fn2 == null) {
                if (!mGameBoard.isPegFull(i)) {
                    if (mChild[i] == null) {
                        mChild[i] = new TreeNode(this, i);
                        mChild[i].mGameBoard.moveOnPeg(i, whosTurn(rootMoveColour, mChild[i].level()));
                    }

                    fn2 = mChild[i].findIncomplete( desiredLevel, currentLevel + 1, rootMoveColour );
                    retNode = fn2;
                }

                i++;
            }
        }

        return retNode;
    }

    private static GamePresenter.PEG_SELECT_COLOUR whosTurn( GamePresenter.PEG_SELECT_COLOUR rootMoveColour, int level ) {
        if (level % 2 == 1) {
            return rootMoveColour;
        }
        else if (rootMoveColour == GamePresenter.PEG_SELECT_COLOUR.RED) {
            return GamePresenter.PEG_SELECT_COLOUR.WHITE;
        }
        else {
            return GamePresenter.PEG_SELECT_COLOUR.RED;
        }
    }

    public String toString() {
        if (mName == null) {
            return super.toString();
        }

        return mName;
    }

    public TreeNode makeMove( int peg, GamePresenter.PEG_SELECT_COLOUR colour ) {
        int i = mGameBoard.moveOnPeg(peg, colour);
        if (i >= 0) {
            return mChild[i];
        }

        throw new RuntimeException("got invalid result from moveOnPeg(): " + i);
    }

    public double rating( GamePresenter.PEG_SELECT_COLOUR col, double lineScoreWeight, GamePresenter.PEG_SELECT_COLOUR oppcol, double oppLineScoreWeight ) {
        double dom_child_rating = 0.0;
        boolean hasChild = false;
        int nodeLevel = level();

        double rate = (double)mGameBoard.rating(col, oppcol);
        rate *= lineScoreWeight;

        double oppRate = (double)mGameBoard.rating(oppcol, col);
        oppRate *= oppLineScoreWeight;

        double nodeRating = rate - oppRate;

        if (nodeLevel % 2 == 1) {
            for (int i=0; i<8; ++i) {
                if (mChild[i] != null) {
                    double r = mChild[i].rating(col, lineScoreWeight, oppcol, oppLineScoreWeight);
                    if (!hasChild) {
                        hasChild = true;
                        dom_child_rating = r;
                    }
                    else if (r < dom_child_rating) {
                        dom_child_rating = r;
                    }
                }
            }
        }
        else {
            for (int i=0; i<8; ++i) {
                if (mChild[i] != null) {
                    double r = mChild[i].rating(col, lineScoreWeight, oppcol, oppLineScoreWeight);
                    if (!hasChild) {
                        hasChild = true;
                        dom_child_rating = r;
                    }
                    else if (r > dom_child_rating) {
                        dom_child_rating = r;
                    }
                }
            }
        }

        if (hasChild) {
            nodeRating += dom_child_rating;
        }

        return nodeRating;
    }

    public int getBestMove( GamePresenter.PEG_SELECT_COLOUR col, double lineScoreWeight, GamePresenter.PEG_SELECT_COLOUR oppcol, double oppLineScoreWeight ) {
        boolean first = true;
        double bestRating = 0.0;
        int bestChild = -1;

        for (int i=0; i<8; ++i) {
            if (mChild[i] != null) {
                double r = mChild[i].rating(col, lineScoreWeight, oppcol, oppLineScoreWeight);
                if (first) {
                    bestChild = i;
                    bestRating = r;
                    first = false;
                }
                else if (mChild[i].rating(col, lineScoreWeight, oppcol, oppLineScoreWeight) > bestRating) {
                    bestChild = i;
                    bestRating = r;
                }
            }
        }

        //Log.d("TreeNode", "getBestMove() - returning best move: " + bestChild + ", rating: " + bestRating);
        System.out.println("getBestMove() - returning best move: " + bestChild + ", rating: " + bestRating);
        return bestChild;
    }

    public boolean isPegFull( int peg ) {
        return mGameBoard.isPegFull(peg);
    }

    private static String getColorChar(GamePresenter.PEG_SELECT_COLOUR c) {
        if (c == GamePresenter.PEG_SELECT_COLOUR.NONE) {
            return " ";
        }
        return (c == GamePresenter.PEG_SELECT_COLOUR.RED ? "R" : "W");
    }

    public String getBoardDisplay() {
        String s = "";

        for (int height=2; height >=0; --height) {
            for (int peg = 0; peg < 3; ++peg) {
                s += "[" + getColorChar(mGameBoard.getPegSpotColour(peg, height)) + "]   ";
            }
            s += "\n";
        }

        s += "\n";

        for (int height=2; height >=0; --height) {
            for (int peg = 3; peg < 5; ++peg) {
                s += "   [" + getColorChar(mGameBoard.getPegSpotColour(peg, height)) + "]";
            }
            s += "\n";
        }

        s += "\n";

        for (int height=2; height >=0; --height) {
            for (int peg = 5; peg < 8; ++peg) {
                s += "[" + getColorChar(mGameBoard.getPegSpotColour(peg, height)) + "]   ";
            }
            s += "\n";
        }

        return s;
    }
}
