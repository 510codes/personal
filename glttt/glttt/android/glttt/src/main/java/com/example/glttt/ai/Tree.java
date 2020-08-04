package com.example.glttt.ai;

import com.example.glttt.GamePresenter;

public class Tree {
    private TreeNode mRootNode;
    private volatile int mMaxCompleteLevel;
    private GamePresenter.PEG_SELECT_COLOUR mRootColour;

    public Tree( GamePresenter.PEG_SELECT_COLOUR rootColour ) {
        this(new TreeNode(), rootColour);
    }

    public Tree( TreeNode rootNode, GamePresenter.PEG_SELECT_COLOUR rootColour) {
        mRootNode = rootNode;
        mRootColour = rootColour;
        mMaxCompleteLevel = 1;
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

    public boolean addNode() {
        TreeNode incompleteNode = mRootNode.findIncomplete(mMaxCompleteLevel, 1, mRootColour);
        boolean retVal;

        if (incompleteNode == null) {
            retVal = false;
            mMaxCompleteLevel++;
        }
        else {
            int mc = incompleteNode.missingChild();
            if (mc < 0) {
                throw new RuntimeException("mc is out of range: " + mc);
            }

            TreeNode newNode = new TreeNode(incompleteNode, mc);
            newNode.makeMove(mc, whosTurn(mRootColour, newNode.level()));

            retVal = true;
        }

        return retVal;
    }

    public void makeMove( int peg ) {
        GamePresenter.PEG_SELECT_COLOUR newRootColour = GamePresenter.PEG_SELECT_COLOUR.RED;
        if (mRootColour == GamePresenter.PEG_SELECT_COLOUR.RED) {
            newRootColour = GamePresenter.PEG_SELECT_COLOUR.WHITE;
        }

        TreeNode newRoot = mRootNode.getChild(peg);
        newRoot.detach();
        mRootNode.prune();
        mRootNode = newRoot;
        mRootColour = newRootColour;
        mMaxCompleteLevel--;
    }

    public int getBestMove() {
        GamePresenter.PEG_SELECT_COLOUR oppCol = GamePresenter.PEG_SELECT_COLOUR.RED;
        if (mRootColour == GamePresenter.PEG_SELECT_COLOUR.RED) {
            oppCol = GamePresenter.PEG_SELECT_COLOUR.WHITE;
        }

        int bestMove = mRootNode.getBestMove(mRootColour, 1.0, oppCol, 0.9);

        return bestMove;
    }

    public String toString() {
        return mRootNode.getBoardDisplay();
    }

    public int getMaxCompleteLevel() {
        return mMaxCompleteLevel;
    }

    public int size() {
        return mRootNode.size();
    }
}
