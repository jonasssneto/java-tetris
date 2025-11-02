package com.jonas.tetris.engine;

import com.jonas.tetris.domain.Board;
import com.jonas.tetris.domain.Tetromino;

/**
 * Detecta colisões entre Tetrominos e o tabuleiro.
 */
public class CollisionDetector {

    /**
     * Verifica se um Tetromino pode ser movido para uma nova posição.
     *
     * @param tetromino tetromino a verificar
     * @param newX nova posição X
     * @param newY nova posição Y
     * @param board tabuleiro
     * @return true se pode mover, false caso contrário
     */
    public static boolean canMoveTo(Tetromino tetromino, int newX, int newY, Board board) {
        int[][] shape = tetromino.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = newX + j;
                    int boardY = newY + i;

                    // Verificar se está fora dos limites ou ocupado
                    if (boardX < 0 || boardX >= board.getWidth() ||
                        boardY >= board.getHeight() ||
                        board.isCellOccupied(boardX, boardY)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Verifica se um Tetromino pode ser rotacionado na posição atual.
     *
     * @param tetromino tetromino atual
     * @param rotatedTetromino tetromino após rotação
     * @param board tabuleiro
     * @return true se pode rotacionar
     */
    public static boolean canRotate(Tetromino tetromino, Tetromino rotatedTetromino, Board board) {
        return canMoveTo(rotatedTetromino, tetromino.getX(), tetromino.getY(), board);
    }

    /**
     * Calcula a posição Y final (hard drop) para um Tetromino.
     *
     * @param tetromino tetromino a verificar
     * @param board tabuleiro
     * @return posição Y final
     */
    public static int getHardDropY(Tetromino tetromino, Board board) {
        int dropY = tetromino.getY();

        // Continuar descendo até bater em algo
        while (canMoveTo(tetromino.moveTo(tetromino.getX(), dropY + 1), tetromino.getX(), dropY + 1, board)) {
            dropY++;
        }

        return dropY;
    }

    /**
     * Verifica se um Tetromino está completamente fora do tabuleiro visível.
     * Usado para detectar game over.
     *
     * @param tetromino tetromino a verificar
     * @param board tabuleiro
     * @return true se completamente acima
     */
    public static boolean isGameOver(Tetromino tetromino, Board board) {
        int[][] shape = tetromino.getShape();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardY = tetromino.getY() + i;

                    // Se qualquer bloco está na zona de spawn (acima da área visível) e ocupado
                    if (boardY < board.getVisibleHeight()) {
                        if (board.isCellOccupied(tetromino.getX() + j, boardY)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
