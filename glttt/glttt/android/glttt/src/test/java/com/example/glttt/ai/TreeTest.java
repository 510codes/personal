package com.example.glttt.ai;

import com.example.glttt.GamePresenter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class TreeTest {

    @Test
    public void testGetBestMove() {
        Tree tree = new Tree(GamePresenter.PEG_SELECT_COLOUR.RED);

        System.out.println("board:\n" + tree);

        while (tree.addNode()) {
            System.out.println("1. testGetBestMove(): added a node...");
        }

        // RED
        tree.makeMove(5);
        System.out.println("moved to 5, board:\n" + tree);

        while (tree.addNode()) {
            System.out.println("2. testGetBestMove(): added a node...");
        }

        // WHITE
        tree.makeMove(6);
        System.out.println("moved to 6, board:\n" + tree);

        while (tree.addNode()) {
            System.out.println("3. testGetBestMove(): added a node...");
        }

        // RED
        tree.makeMove(5);
        System.out.println("moved to 5, board:\n" + tree);

        while (tree.addNode()) {
            System.out.println("4. testGetBestMove(): added a node...");
        }

        // WHITE
        tree.makeMove(7);
        System.out.println("moved to 7, board:\n" + tree);

        while (tree.addNode()) {
            System.out.println("4. testGetBestMove(): added a node...");
        }

        TreeNode bestMove = tree.getBestMove();
        assertTrue(bestMove.isPegFull(5));
    }
}
