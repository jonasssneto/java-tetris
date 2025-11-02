package com.jonas.tetris.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

public class BoardTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testClearCompleteLines() {
        // Preencher a última linha completamente
        for (int x = 0; x < Board.BOARD_WIDTH; x++) {
            board.setCell(x, Board.BOARD_HEIGHT + 1, java.awt.Color.RED);
        }

        int cleared = board.clearCompleteLines();
        assertEquals(1, cleared);
    }

    @Test
    public void testNoLinesClearedWhenEmpty() {
        int cleared = board.clearCompleteLines();
        assertEquals(0, cleared);
    }

    @Test
    public void testMultipleLinesClear() {
        // Preencher as duas últimas linhas
        for (int y = Board.BOARD_HEIGHT; y <= Board.BOARD_HEIGHT + 1; y++) {
            for (int x = 0; x < Board.BOARD_WIDTH; x++) {
                board.setCell(x, y, java.awt.Color.BLUE);
            }
        }

        int cleared = board.clearCompleteLines();
        assertEquals(2, cleared);
    }

    @Test
    public void testCellOccupancy() {
        board.setCell(5, 10, java.awt.Color.GREEN);
        assertTrue(board.isCellOccupied(5, 10));
        assertFalse(board.isCellOccupied(5, 11));
    }

    @Test
    public void testOutOfBoundsCellsConsideredOccupied() {
        // Fora dos limites = ocupado
        assertTrue(board.isCellOccupied(-1, 10));
        assertTrue(board.isCellOccupied(Board.BOARD_WIDTH, 10));
        assertTrue(board.isCellOccupied(5, Board.BOARD_HEIGHT + 2));
    }

    @Test
    public void testBoardClear() {
        board.setCell(5, 10, java.awt.Color.RED);
        board.setCell(3, 15, java.awt.Color.BLUE);
        assertTrue(board.isCellOccupied(5, 10));
        assertTrue(board.isCellOccupied(3, 15));

        board.clear();

        assertFalse(board.isCellOccupied(5, 10));
        assertFalse(board.isCellOccupied(3, 15));
    }
}
