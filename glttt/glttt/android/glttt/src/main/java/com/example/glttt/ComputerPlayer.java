package com.example.glttt;

import android.util.Log;

import com.example.glttt.ai.Tree;
import com.example.glttt.ai.TreeNode;

public class ComputerPlayer implements IPlayer {

    private final Tree mTree;
    private final String mName;

    private static final int MIN_BUILD_LEVEL = 5;
    private static final int MAX_BUILD_LEVEL = 6;

    public ComputerPlayer( GameBoard gameBoard, GamePresenter.PEG_SELECT_COLOUR rootColour, String name ) {
        mTree = new Tree(new TreeNode(gameBoard), rootColour);
        mName = name;
        final Thread t = new Thread() {
            @Override
            public void run() {
                treeBuilder();
            }
        };

        // wait for the thread to acquire a lock on mTree
        synchronized (this) {
            t.start();
            try {
                wait();
            }
            catch (InterruptedException e) {}
        }
    }

    @Override
    public int getMove(long currentTimeInNanos) {
        int move;

        Log.d("ComputerPlayer", "getMove(): trying to lock mTree....");
        synchronized (mTree) {
            Log.d("ComputerPlayer", "getMove(): locked mTree");
            move = mTree.getBestMove();
            Log.d("ComputerPlayer", "getMove(): selected move: " + move);
            mTree.makeMove(move);
            mTree.notify();
        }

        return move;
    }

    @Override
    public int getDelayAfterMoveInMillis() {
        return 500;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public void opponentMove( int peg ) {
        Log.d("ComputerPlayer", "opponentMove(): received opponent move: " + peg + ", locking mTree...");
        // TODO: questionable if the caller thread should be used here
        synchronized (mTree) {
            Log.d("ComputerPlayer", "opponentMove(): mTree locked, processing move");
            mTree.makeMove(peg);
            mTree.notify();
        }
    }

    private void treeBuilder() {
        while (true) {
            synchronized (mTree) {
                synchronized (this) {
                    notify();
                }
                boolean minBuild = false;
                while (!minBuild) {
                    while (mTree.addNode()) {

                    }

                    Log.d("ComputerPlayer", "treeBuilder(): completed tree level " + mTree.getMaxCompleteLevel() + ", size: " + mTree.size());
                    if (mTree.getMaxCompleteLevel() >= MIN_BUILD_LEVEL) {
                        minBuild = true;
                    }
                }
            }

            Log.d("ComputerPlayer", "treeBuilder(): min build is done, can accept move");

            while (mTree.getMaxCompleteLevel() < MAX_BUILD_LEVEL) {
                synchronized (mTree) {
                    mTree.addNode();
                }
            }

            synchronized (mTree) {
                if (mTree.getMaxCompleteLevel() >= MAX_BUILD_LEVEL) {
                    try {
                        Log.d("ComputerPlayer", "treeBuilder(): completed tree level " + mTree.getMaxCompleteLevel() + ", size: " + mTree.size() + ", going to sleep");
                        mTree.wait();
                        Log.d("ComputerPlayer", "treeBuilder(): woken up");
                    }
                    catch (InterruptedException e) {
                        Log.e("ComputerPlayer", "treeBuilder(): caught interruptedexception", e);
                    }
                }
            }
        }
    }
}
