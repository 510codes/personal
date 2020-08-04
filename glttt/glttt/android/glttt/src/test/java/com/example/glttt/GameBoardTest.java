package com.example.glttt;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

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
