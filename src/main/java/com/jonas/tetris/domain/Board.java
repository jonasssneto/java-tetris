package com.jonas.tetris.domain;

import java.awt.*;

/**
 * Representa o tabuleiro de jogo.
 * Mantém o estado das células (blocos fixos) com suas cores.
 */
public class Board {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    private static final int BUFFER_HEIGHT = 2; // Linhas de buffer para spawn acima do tabuleiro visível

    private final int[][] grid; // 0 = vazio, 1 = ocupado
    private final Color[][] colors; // cor de cada célula

    public Board() {
        this.grid = new int[BOARD_HEIGHT + BUFFER_HEIGHT][BOARD_WIDTH];
        this.colors = new Color[BOARD_HEIGHT + BUFFER_HEIGHT][BOARD_WIDTH];
    }

    /**
     * Verifica se a posição está dentro dos limites.
     */
    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < BOARD_WIDTH && y >= 0 && y < BOARD_HEIGHT + BUFFER_HEIGHT;
    }

    /**
     * Verifica se uma posição está ocupada.
     */
    public boolean isCellOccupied(int x, int y) {
        if (!isWithinBounds(x, y)) {
            // Fora dos limites (lateral ou inferior) = ocupado
            return y >= BOARD_HEIGHT + BUFFER_HEIGHT || x < 0 || x >= BOARD_WIDTH;
        }
        return grid[y][x] != 0;
    }

    /**
     * Coloca um bloco no tabuleiro.
     */
    public void setCell(int x, int y, Color color) {
        if (isWithinBounds(x, y)) {
            grid[y][x] = 1;
            colors[y][x] = color;
        }
    }

    /**
     * Limpa todas as linhas completas e retorna quantas foram limpas.
     * O método também desloca as linhas superiores para baixo.
     */
    public int clearCompleteLines() {
        int linesCleared = 0;

        // Verificar de baixo para cima
        for (int y = BOARD_HEIGHT + BUFFER_HEIGHT - 1; y >= 0; y--) {
            if (isLineFull(y)) {
                // Remover linha e deslocar tudo acima
                removeLineAt(y);
                linesCleared++;
                y++; // Recheck this row as it now contains the line above
            }
        }

        return linesCleared;
    }

    /**
     * Verifica se uma linha está completamente preenchida.
     */
    private boolean isLineFull(int y) {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            if (grid[y][x] == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Remove uma linha e desloca todas as acima para baixo.
     */
    private void removeLineAt(int y) {
        // Deslocar tudo acima uma linha para baixo
        for (int moveY = y; moveY > 0; moveY--) {
            for (int x = 0; x < BOARD_WIDTH; x++) {
                grid[moveY][x] = grid[moveY - 1][x];
                colors[moveY][x] = colors[moveY - 1][x];
            }
        }
        // Limpar linha superior
        for (int x = 0; x < BOARD_WIDTH; x++) {
            grid[0][x] = 0;
            colors[0][x] = null;
        }
    }

    /**
     * Retorna uma cópia da grade para renderização.
     */
    public int[][] getGridCopy() {
        int[][] copy = new int[BOARD_HEIGHT + BUFFER_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT + BUFFER_HEIGHT; i++) {
            System.arraycopy(grid[i], 0, copy[i], 0, BOARD_WIDTH);
        }
        return copy;
    }

    /**
     * Retorna uma cópia das cores para renderização.
     */
    public Color[][] getColorsCopy() {
        Color[][] copy = new Color[BOARD_HEIGHT + BUFFER_HEIGHT][BOARD_WIDTH];
        for (int i = 0; i < BOARD_HEIGHT + BUFFER_HEIGHT; i++) {
            System.arraycopy(colors[i], 0, copy[i], 0, BOARD_WIDTH);
        }
        return copy;
    }

    /**
     * Limpa o tabuleiro completamente.
     */
    public void clear() {
        for (int i = 0; i < BOARD_HEIGHT + BUFFER_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                grid[i][j] = 0;
                colors[i][j] = null;
            }
        }
    }

    public int getWidth() {
        return BOARD_WIDTH;
    }

    public int getHeight() {
        return BOARD_HEIGHT + BUFFER_HEIGHT;
    }

    public int getVisibleHeight() {
        return BOARD_HEIGHT;
    }
}
