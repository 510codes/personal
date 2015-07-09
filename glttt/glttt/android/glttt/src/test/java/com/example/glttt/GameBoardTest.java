package com.example.glttt;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
public class GameBoardTest {

    @Test
    public void testEmptyBoard() {
        GameBoard board = new GameBoard();
        assertFalse(board.isPegFull(0));
        assertFalse(board.isPegFull(7));
    }

    @Test
    public void testAddPegs() {
        GameBoard board = new GameBoard();

        assertFalse(board.isPegFull(0));

        board.moveOnPeg(0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertFalse(board.isPegFull(0));

        board.moveOnPeg(0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertFalse(board.isPegFull(0));

        board.moveOnPeg(0, GamePresenter.PEG_SELECT_COLOUR.RED);
        assertTrue(board.isPegFull(0));
    }
}
