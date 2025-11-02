package com.jonas.tetris.persistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Repositório para persistência de recordes em arquivo.
 * Usa formato simples de texto.
 */
public class ScoreRepository {
    private static final String SCORES_FILE = "tetris_scores.txt";
    private static final String HIGH_SCORE_KEY = "high_score=";

    /**
     * Retorna o recorde salvo, ou 0 se não existir.
     */
    public static int getHighScore() {
        try {
            if (Files.exists(Paths.get(SCORES_FILE))) {
                String content = Files.readString(Paths.get(SCORES_FILE));
                if (content.startsWith(HIGH_SCORE_KEY)) {
                    String scoreStr = content.substring(HIGH_SCORE_KEY.length()).trim();
                    return Integer.parseInt(scoreStr);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Erro ao ler recordes: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Salva o recorde se for maior que o atual.
     */
    public static void saveScore(int score) {
        int currentHighScore = getHighScore();
        if (score > currentHighScore) {
            try {
                String content = HIGH_SCORE_KEY + score;
                Files.writeString(Paths.get(SCORES_FILE), content);
                System.out.println("Novo recorde salvo: " + score);
            } catch (IOException e) {
                System.err.println("Erro ao salvar recorde: " + e.getMessage());
            }
        }
    }

    /**
     * Limpa o arquivo de recordes (para testes).
     */
    public static void clearScores() {
        try {
            Files.deleteIfExists(Paths.get(SCORES_FILE));
        } catch (IOException e) {
            System.err.println("Erro ao limpar recordes: " + e.getMessage());
        }
    }
}
