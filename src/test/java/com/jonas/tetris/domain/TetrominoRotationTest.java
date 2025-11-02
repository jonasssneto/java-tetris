package com.jonas.tetris.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import org.junit.Test;

public class TetrominoRotationTest {

    @Test
    public void testITetrominoRotation() {
        Tetromino piece = new Tetromino(Tetromino.Type.I);
        int[][] originalShape = piece.getShape();

        System.out.println("Original I shape:");
        printShape(originalShape);
        assertEquals(1, originalShape.length); // 1 linha
        assertEquals(4, originalShape[0].length); // 4 colunas

        Tetromino rotated = piece.rotated();
        int[][] rotatedShape = rotated.getShape();

        System.out.println("Rotated I shape:");
        printShape(rotatedShape);
        assertEquals(4, rotatedShape.length); // 4 linhas
        assertEquals(1, rotatedShape[0].length); // 1 coluna
    }

    @Test
    public void testTTetrominoRotation() {
        Tetromino piece = new Tetromino(Tetromino.Type.T);
        int[][] originalShape = piece.getShape();

        System.out.println("Original T shape:");
        printShape(originalShape);

        Tetromino rotated = piece.rotated();
        int[][] rotatedShape = rotated.getShape();

        System.out.println("Rotated T shape:");
        printShape(rotatedShape);

        // Deve ter mudado de dimensão
        assertNotEquals(originalShape.length, rotatedShape.length);
    }

    @Test
    public void testMultipleRotations() {
        Tetromino piece = new Tetromino(Tetromino.Type.T, 5, 5);

        Tetromino r1 = piece.rotated();
        Tetromino r2 = r1.rotated();
        Tetromino r3 = r2.rotated();
        Tetromino r4 = r3.rotated();

        // 4 rotações devem voltar ao estado original
        int[][] original = piece.getShape();
        int[][] afterFour = r4.getShape();

        System.out.println("Original:");
        printShape(original);
        System.out.println("After 4 rotations:");
        printShape(afterFour);

        assertEquals(original.length, afterFour.length);
        assertEquals(original[0].length, afterFour[0].length);
    }

    @Test
    public void testRotationPersistsAfterMovement() {
        // Este teste verifica o bug crítico: rotação deve persistir após moveTo()
        Tetromino piece = new Tetromino(Tetromino.Type.I, 5, 5);

        // I original: 1x4 (horizontal)
        assertEquals(1, piece.getShape().length);
        assertEquals(4, piece.getShape()[0].length);

        // Rotacionar: deve ser 4x1 (vertical)
        Tetromino rotated = piece.rotated();
        assertEquals(4, rotated.getShape().length);
        assertEquals(1, rotated.getShape()[0].length);

        System.out.println("Rotated I (before move):");
        printShape(rotated.getShape());

        // Mover para baixo (simula o game loop)
        Tetromino moved = rotated.moveTo(5, 6);

        System.out.println("Rotated I (after move):");
        printShape(moved.getShape());

        // CRÍTICO: A forma rotacionada DEVE ser preservada após moveTo()
        assertEquals("Rotação perdida após moveTo()!", 4, moved.getShape().length);
        assertEquals("Rotação perdida após moveTo()!", 1, moved.getShape()[0].length);

        // Verificar posição foi atualizada
        assertEquals(5, moved.getX());
        assertEquals(6, moved.getY());
    }

    private void printShape(int[][] shape) {
        for (int[] row : shape) {
            for (int cell : row) {
                System.out.print(cell == 1 ? "█" : "·");
            }
            System.out.println();
        }
        System.out.println();
    }
}
