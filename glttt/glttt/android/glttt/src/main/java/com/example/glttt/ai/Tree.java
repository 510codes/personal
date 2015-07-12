package com.example.glttt.ai;

import com.example.glttt.GamePresenter;

public class Tree {
    private TreeNode mRootNode;
    private int mMaxCompleteLevel;
    private GamePresenter.PEG_SELECT_COLOUR mRootColour;

    public Tree( GamePresenter.PEG_SELECT_COLOUR rootColour ) {
        this(new TreeNode(), rootColour);
    }

    public Tree( TreeNode rootNode, GamePresenter.PEG_SELECT_COLOUR rootColour) {
        mRootNode = rootNode;
        mRootColour = rootColour;
        mMaxCompleteLevel = 1;
    }

    public boolean addNode() {
        System.out.println("addNode(): enter");
        TreeNode incompleteNode = mRootNode.findIncomplete(mMaxCompleteLevel, 1, mRootColour);
        boolean retVal;

        System.out.println("addNode(): incompleteNode: " + incompleteNode);
        if (incompleteNode == null) {
            retVal = false;
        }
        else {
            int mc = mRootNode.missingChild();
            System.out.println("addNode(): mc: " + mc);
            if (mc < 0) {
                throw new RuntimeException("mc is out of range: " + mc);
            }

            TreeNode newNode = new TreeNode(mRootNode, mc);
            newNode.makeMove(mc, mRootColour);

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
    }

    public TreeNode getBestMove() {
        GamePresenter.PEG_SELECT_COLOUR oppCol = GamePresenter.PEG_SELECT_COLOUR.RED;
        if (mRootColour == GamePresenter.PEG_SELECT_COLOUR.RED) {
            oppCol = GamePresenter.PEG_SELECT_COLOUR.WHITE;
        }

        System.out.println("rootNode:\n" + mRootNode.getBoardDisplay());
        TreeNode bestMove = mRootNode.getBestMove(mRootColour, 1.0, oppCol, 0.9);
        System.out.println("bestMove:\n" + bestMove.getBoardDisplay());

        return bestMove;
    }

    public String toString() {
        return mRootNode.getBoardDisplay();
    }
}
