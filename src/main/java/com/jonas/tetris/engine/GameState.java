package com.jonas.tetris.engine;

import com.jonas.tetris.domain.Board;
import com.jonas.tetris.domain.Tetromino;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Estado imutável do jogo.
 * Contém todas as informações necessárias para renderizar a tela e processar
 * lógica.
 */
public class GameState {
    public enum GameStatus {
        PLAYING, PAUSED, GAME_OVER, NOT_STARTED
    }

    private final int[][] boardGrid;
    private final java.awt.Color[][] boardColors;
    private final Tetromino currentPiece;
    private final Tetromino shadowPiece; // Posição de hard drop
    private final List<Tetromino> nextQueue; // Próximas peças
    private final Tetromino holdPiece; // Peça guardada
    private final int score;
    private final int level;
    private final int totalLines;
    private final long elapsedTimeMs;
    private final GameStatus status;
    private final int totalPieces;

    public GameState(Board board, Tetromino currentPiece, Tetromino shadowPiece,
            List<Tetromino> nextQueue, Tetromino holdPiece,
            int score, int level, int totalLines, long elapsedTimeMs,
            GameStatus status, int totalPieces) {
        this.boardGrid = board.getGridCopy();
        this.boardColors = board.getColorsCopy();
        this.currentPiece = currentPiece;
        this.shadowPiece = shadowPiece;
        this.nextQueue = Collections.unmodifiableList(new ArrayList<>(nextQueue));
        this.holdPiece = holdPiece;
        this.score = score;
        this.level = level;
        this.totalLines = totalLines;
        this.elapsedTimeMs = elapsedTimeMs;
        this.status = status;
        this.totalPieces = totalPieces;
    }

    // Getters (todos retornam valores imutáveis)

    public int[][] getBoardGrid() {
        return boardGrid;
    }

    public java.awt.Color[][] getBoardColors() {
        return boardColors;
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public Tetromino getShadowPiece() {
        return shadowPiece;
    }

    public List<Tetromino> getNextQueue() {
        return nextQueue;
    }

    public Tetromino getHoldPiece() {
        return holdPiece;
    }

    public int getScore() {
        return score;
    }

    public int getLevel() {
        return level;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public long getElapsedTimeMs() {
        return elapsedTimeMs;
    }

    public GameStatus getStatus() {
        return status;
    }

    public int getTotalPieces() {
        return totalPieces;
    }

    public String getFormattedTime() {
        long seconds = elapsedTimeMs / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    public double getPiecesPerSecond() {
        double elapsedSeconds = elapsedTimeMs / 1000.0;
        if (elapsedSeconds < 1.0) {
            return 0.0;
        }
        return totalPieces / elapsedSeconds;
    }
}
