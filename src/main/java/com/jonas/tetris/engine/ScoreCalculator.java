package com.jonas.tetris.engine;

/**
 * Calculadora de pontuação oficial de Tetris.
 * 
 * Referência: https://tetris.wiki/Tetris_(NES)/Scoring
 * - Single (1 linha): 40 × nível
 * - Double (2 linhas): 100 × nível
 * - Triple (3 linhas): 300 × nível
 * - Tetris (4 linhas): 1200 × nível
 * - Soft drop: 1 ponto por célula
 * - Hard drop: 2 pontos por célula
 */
public class ScoreCalculator {
    /**
     * Calcula pontos por linhas limpas.
     *
     * @param linesClearedCount número de linhas limpas consecutivas (1-4)
     * @param level nível atual (usado como multiplicador)
     * @return pontos ganhos
     */
    public static int calculateLinesCleared(int linesClearedCount, int level) {
        if (linesClearedCount <= 0 || linesClearedCount > 4) {
            return 0;
        }

        int baseScore = 0;
        if (linesClearedCount == 1) {
            baseScore = 40;
        } else if (linesClearedCount == 2) {
            baseScore = 100;
        } else if (linesClearedCount == 3) {
            baseScore = 300;
        } else if (linesClearedCount == 4) {
            baseScore = 1200;
        }

        return baseScore * level;
    }

    /**
     * Calcula pontos por soft drop.
     *
     * @param cellsFallen número de células que a peça caiu
     * @return pontos ganhos (1 por célula)
     */
    public static int calculateSoftDrop(int cellsFallen) {
        return Math.max(0, cellsFallen);
    }

    /**
     * Calcula pontos por hard drop.
     *
     * @param cellsFallen número de células que a peça caiu
     * @return pontos ganhos (2 por célula)
     */
    public static int calculateHardDrop(int cellsFallen) {
        return Math.max(0, cellsFallen * 2);
    }
}
