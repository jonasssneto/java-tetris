package com.jonas.tetris.engine;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;

import com.jonas.tetris.domain.Board;
import com.jonas.tetris.domain.Tetromino;

public class CollisionDetectorTest {
    private Board board;

    @Before
    public void setUp() {
        board = new Board();
    }

    @Test
    public void testCanMoveLeftWithoutCollision() {
        Tetromino piece = new Tetromino(Tetromino.Type.I, 5, 5);
        assertTrue(CollisionDetector.canMoveTo(piece, 4, 5, board));
    }

    @Test
    public void testCannotMoveLeftBeyondBoundary() {
        Tetromino piece = new Tetromino(Tetromino.Type.I, 0, 5);
        assertFalse(CollisionDetector.canMoveTo(piece, -1, 5, board));
    }

    @Test
    public void testCannotMoveRightBeyondBoundary() {
        Tetromino piece = new Tetromino(Tetromino.Type.I, Board.BOARD_WIDTH - 1, 5);
        assertFalse(CollisionDetector.canMoveTo(piece, Board.BOARD_WIDTH, 5, board));
    }

    @Test
    public void testCanMoveDownWithoutCollision() {
        Tetromino piece = new Tetromino(Tetromino.Type.O, 5, 10);
        assertTrue(CollisionDetector.canMoveTo(piece, 5, 11, board));
    }

    @Test
    public void testCannotMoveBelowFloor() {
        Tetromino piece = new Tetromino(Tetromino.Type.O, 5, Board.BOARD_HEIGHT + 1);
        assertFalse(CollisionDetector.canMoveTo(piece, 5, Board.BOARD_HEIGHT + 2, board));
    }

    @Test
    public void testCannotMoveIntoPlacedBlock() {
        // Colocar um bloco no tabuleiro
        board.setCell(5, 10, java.awt.Color.RED);

        Tetromino piece = new Tetromino(Tetromino.Type.T, 4, 9);
        // O T pode colidir com o bloco em (5, 10)
        assertFalse(CollisionDetector.canMoveTo(piece, 4, 10, board));
    }

    @Test
    public void testHardDropCalculatesCorrectly() {
        Tetromino piece = new Tetromino(Tetromino.Type.O, 5, 0);
        int hardDropY = CollisionDetector.getHardDropY(piece, board);

        // O piece tem altura 2, então a máxima posição Y é BOARD_HEIGHT + BUFFER_HEIGHT
        // - 2
        assertTrue(hardDropY >= 0);
        // Hard drop Y deveria ser bem perto do fundo
        assertTrue(hardDropY > 10);
    }

    @Test
    public void testGameOverDetection() {
        // Preencher a primeira linha (zona de spawn) da coluna 5
        for (int x = 5; x < 7; x++) {
            board.setCell(x, 0, java.awt.Color.RED);
        }

        // Tentar spawnar um I piece no topo (coluna 4-7)
        Tetromino piece = new Tetromino(Tetromino.Type.I, 4, 0);
        assertTrue(CollisionDetector.isGameOver(piece, board));
    }

    @Test
    public void testNoGameOverWhenSpaceIsAvailable() {
        Tetromino piece = new Tetromino(Tetromino.Type.O, 5, 0);
        assertFalse(CollisionDetector.isGameOver(piece, board));
    }
}
