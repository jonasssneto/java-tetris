package com.jonas.tetris.engine;

import com.jonas.tetris.domain.Board;
import com.jonas.tetris.domain.Tetromino;

import java.util.*;

/**
 * Controlador central do jogo.
 * Orquestra a lógica de jogo, input, e state management.
 * Separa completamente a lógica da renderização (UI).
 */
public class GameController {
    private final Board board;
    private final Random random;

    private Tetromino currentPiece;
    private final Queue<Tetromino> nextQueue;
    private Tetromino holdPiece;
    private boolean canHoldThisTurn;

    private int score;
    private int level;
    private int totalLines;
    private long gameStartTime;
    private int totalPieces;

    private GameState.GameStatus status;
    private long pauseStartTime;
    private long totalPausedTime;

    // Observers para eventos
    private final List<GameEventListener> listeners;

    public interface GameEventListener {
        void onLineCleared(int lineCount, int points);

        void onLevelUp(int newLevel);

        void onGameOver();

        void onPieceSpawned(Tetromino piece);
    }

    public GameController() {
        this.board = new Board();
        this.random = new Random();
        this.nextQueue = new LinkedList<>();
        this.listeners = new ArrayList<>();
        this.status = GameState.GameStatus.NOT_STARTED;
        this.totalPausedTime = 0;

        // Inicializar fila com 3 peças
        for (int i = 0; i < 3; i++) {
            nextQueue.add(getRandomPiece());
        }
    }

    /**
     * Inicia um novo jogo.
     */
    public void startGame() {
        board.clear();
        score = 0;
        level = 1;
        totalLines = 0;
        totalPieces = 0;
        gameStartTime = System.currentTimeMillis();
        totalPausedTime = 0;
        status = GameState.GameStatus.PLAYING;
        holdPiece = null;

        spawnNextPiece();
    }

    /**
     * Spawna a próxima peça ou game over.
     */
    private void spawnNextPiece() {
        currentPiece = nextQueue.poll();
        nextQueue.add(getRandomPiece());
        canHoldThisTurn = true;
        totalPieces++;

        // Verificar game over
        if (CollisionDetector.isGameOver(currentPiece, board)) {
            status = GameState.GameStatus.GAME_OVER;
            notifyGameOver();
            return;
        }

        notifyPieceSpawned(currentPiece);
    }

    /**
     * Move a peça para o lado.
     */
    public void moveLeft() {
        if (status != GameState.GameStatus.PLAYING)
            return;

        if (CollisionDetector.canMoveTo(currentPiece, currentPiece.getX() - 1, currentPiece.getY(), board)) {
            currentPiece = currentPiece.moveTo(currentPiece.getX() - 1, currentPiece.getY());
        }
    }

    public void moveRight() {
        if (status != GameState.GameStatus.PLAYING)
            return;

        if (CollisionDetector.canMoveTo(currentPiece, currentPiece.getX() + 1, currentPiece.getY(), board)) {
            currentPiece = currentPiece.moveTo(currentPiece.getX() + 1, currentPiece.getY());
        }
    }

    /**
     * Soft drop: move a peça para baixo um passo (com bonus de score).
     */
    public void softDrop() {
        if (status != GameState.GameStatus.PLAYING)
            return;

        if (CollisionDetector.canMoveTo(currentPiece, currentPiece.getX(), currentPiece.getY() + 1, board)) {
            currentPiece = currentPiece.moveTo(currentPiece.getX(), currentPiece.getY() + 1);
            score += ScoreCalculator.calculateSoftDrop(1);
        } else {
            placePiece();
        }
    }

    /**
     * Hard drop: move a peça até o fundo imediatamente.
     */
    public void hardDrop() {
        if (status != GameState.GameStatus.PLAYING)
            return;

        int finalY = CollisionDetector.getHardDropY(currentPiece, board);
        int cellsFallen = finalY - currentPiece.getY();

        currentPiece = currentPiece.moveTo(currentPiece.getX(), finalY);
        score += ScoreCalculator.calculateHardDrop(cellsFallen);

        placePiece();
    }

    /**
     * Rotaciona a peça.
     */
    public void rotate() {
        if (status != GameState.GameStatus.PLAYING)
            return;

        Tetromino rotated = currentPiece.rotated();

        if (CollisionDetector.canRotate(currentPiece, rotated, board)) {
            currentPiece = rotated;
        }
        // TODO: Implementar wall kick (SRS) futuramente
    }

    /**
     * Hold: troca a peça atual com a guardada.
     */
    public void hold() {
        if (status != GameState.GameStatus.PLAYING || !canHoldThisTurn)
            return;

        Tetromino temp = holdPiece;
        holdPiece = currentPiece;

        if (temp != null) {
            currentPiece = new Tetromino(temp.getType());
        } else {
            spawnNextPiece();
        }

        canHoldThisTurn = false;
    }

    /**
     * Pausa o jogo.
     */
    public void pause() {
        if (status == GameState.GameStatus.PLAYING) {
            status = GameState.GameStatus.PAUSED;
            pauseStartTime = System.currentTimeMillis();
        } else if (status == GameState.GameStatus.PAUSED) {
            totalPausedTime += System.currentTimeMillis() - pauseStartTime;
            status = GameState.GameStatus.PLAYING;
        }
    }

    /**
     * Atualiza o estado do jogo (chamado pelo game loop).
     * Retorna o novo GameState.
     */
    public void update() {
        if (status != GameState.GameStatus.PLAYING)
            return;

        // Tentar mover a peça para baixo
        if (CollisionDetector.canMoveTo(currentPiece, currentPiece.getX(), currentPiece.getY() + 1, board)) {
            currentPiece = currentPiece.moveTo(currentPiece.getX(), currentPiece.getY() + 1);
        } else {
            // Peça não pode descer: colocar no tabuleiro
            placePiece();
        }
    }

    /**
     * Coloca a peça no tabuleiro.
     */
    private void placePiece() {
        int[][] shape = currentPiece.getShape();
        java.awt.Color color = currentPiece.getColor();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = currentPiece.getX() + j;
                    int boardY = currentPiece.getY() + i;

                    if (boardY >= 0) {
                        board.setCell(boardX, boardY, color);
                    }
                }
            }
        }

        // Verificar linhas completas
        int linesCleared = board.clearCompleteLines();
        if (linesCleared > 0) {
            totalLines += linesCleared;
            int points = ScoreCalculator.calculateLinesCleared(linesCleared, level);
            score += points;
            int newLevel = LevelManager.calculateLevel(totalLines);
            if (newLevel > level) {
                level = newLevel;
                notifyLevelUp(level);
            }
            notifyLineCleared(linesCleared, points);
        }

        // Spawn próxima peça
        spawnNextPiece();
    }

    /**
     * Retorna o estado atual do jogo (imutável).
     */
    public GameState getState() {
        Tetromino shadowPiece = currentPiece.moveTo(
                currentPiece.getX(),
                CollisionDetector.getHardDropY(currentPiece, board));

        return new GameState(
                board, currentPiece, shadowPiece,
                new ArrayList<>(nextQueue), holdPiece,
                score, level, totalLines,
                getTotalElapsedTime(),
                status, totalPieces);
    }

    private long getTotalElapsedTime() {
        if (status == GameState.GameStatus.NOT_STARTED) {
            return 0;
        }
        if (status == GameState.GameStatus.PAUSED) {
            return pauseStartTime - gameStartTime - totalPausedTime;
        }
        return System.currentTimeMillis() - gameStartTime - totalPausedTime;
    }

    private Tetromino getRandomPiece() {
        Tetromino.Type[] types = Tetromino.Type.values();
        return new Tetromino(types[random.nextInt(types.length)]);
    }

    // Observer methods

    public void addListener(GameEventListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameEventListener listener) {
        listeners.remove(listener);
    }

    private void notifyLineCleared(int lineCount, int points) {
        for (GameEventListener listener : listeners) {
            listener.onLineCleared(lineCount, points);
        }
    }

    private void notifyLevelUp(int newLevel) {
        for (GameEventListener listener : listeners) {
            listener.onLevelUp(newLevel);
        }
    }

    private void notifyGameOver() {
        for (GameEventListener listener : listeners) {
            listener.onGameOver();
        }
    }

    private void notifyPieceSpawned(Tetromino piece) {
        for (GameEventListener listener : listeners) {
            listener.onPieceSpawned(piece);
        }
    }
}
