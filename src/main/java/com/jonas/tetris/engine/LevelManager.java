package com.jonas.tetris.engine;

/**
 * Gerencia níveis e velocidade do jogo.
 * Cada nível tem uma velocidade (delay em ms para cada queda natural).
 */
public class LevelManager {
    /**
     * Tabela de velocidade por nível (em ms).
     * Baseada na NES Tetris oficial.
     * Nível sobe a cada 10 linhas limpas.
     */
    private static final int[] LEVEL_SPEEDS_MS = {
            500, // Nível 1
            475, // Nível 2
            450, // Nível 3
            425, // Nível 4
            400, // Nível 5
            375, // Nível 6
            350, // Nível 7
            325, // Nível 8
            300, // Nível 9
            275, // Nível 10
            250, // Nível 11
            225, // Nível 12
            200, // Nível 13
            175, // Nível 14
            150, // Nível 15
            125, // Nível 16
            100, // Nível 17
            75, // Nível 18
            50, // Nível 19
            30 // Nível 20+
    };

    private static final int LINES_PER_LEVEL = 10;

    /**
     * Calcula o nível baseado no número de linhas.
     *
     * @param totalLines total de linhas limpas
     * @return nível (começa em 1)
     */
    public static int calculateLevel(int totalLines) {
        return (totalLines / LINES_PER_LEVEL) + 1;
    }

    /**
     * Obtém a velocidade (delay em ms) para um nível específico.
     *
     * @param level nível (começa em 1)
     * @return delay em ms
     */
    public static int getSpeedForLevel(int level) {
        if (level < 1) {
            return LEVEL_SPEEDS_MS[0];
        }
        if (level > LEVEL_SPEEDS_MS.length) {
            return LEVEL_SPEEDS_MS[LEVEL_SPEEDS_MS.length - 1];
        }
        return LEVEL_SPEEDS_MS[level - 1];
    }

    /**
     * Verifica se deve subir de nível.
     *
     * @param oldLines linhas antes de limpar
     * @param newLines linhas depois de limpar
     * @return true se mudou de nível
     */
    public static boolean leveledUp(int oldLines, int newLines) {
        return calculateLevel(oldLines) != calculateLevel(newLines);
    }
}
