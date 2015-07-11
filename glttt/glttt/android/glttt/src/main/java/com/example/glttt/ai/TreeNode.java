package com.example.glttt.ai;

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

    public TreeNode( GameBoard board, String name ) {
        mGameBoard = board;
        mChild = new TreeNode[8];
        mName = name;
        System.out.println("building treenode: " + super.toString() + ", name: " + name);
        printStackTrace(new Exception().getStackTrace());
    }
    public TreeNode( GameBoard board ) {
        this(board, null);
        System.out.println("treenode: " + super.toString() + ", parent: " + mParent);
    }

    public TreeNode( TreeNode parent, int childIndex, GameBoard board ) {
        this(parent, childIndex, board, null);
    }

    public TreeNode( TreeNode parent, int childIndex, GameBoard board, String name ) {
        this(board, name);
        if (parent.mChild[childIndex] != null) {
            throw new RuntimeException("child at index " + childIndex + " already exists");
        }
        mParent = parent;
        System.out.println("treenode: " + super.toString() + ", parent: " + mParent);
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

        System.out.println("mChild[mc]: " + mChild[mc] + ", mGameBoard: " + mGameBoard);
        while (mc < 8 && (mChild[mc] != null || mGameBoard.isPegFull(mc))) {
            mc++;
        }

        return (mc < 8 ? mc : -1);
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
                        System.out.println("TreeNode.findIncomplete(): about to build a treenode, this: " + this);
                        mChild[i] = new TreeNode(this, i, mGameBoard);
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

    public int makeMove( int peg, GamePresenter.PEG_SELECT_COLOUR colour ) {
        return mGameBoard.moveOnPeg(peg, colour);
    }
}
