package com.jonas.tetris.domain;

import java.awt.*;

/**
 * Representa um Tetromino (peça de Tetris).
 * Cada Tetromino tem um tipo, forma, cor e posição no tabuleiro.
 */
public class Tetromino {
    public enum Type {
        I, O, T, S, Z, J, L
    }

    private final Type type;
    private final int[][] shape;
    private final Color color;
    private final int x;
    private final int y;

    /**
     * Cria um novo Tetromino.
     * A posição inicial é calculada como spawn padrão (centro superior do
     * tabuleiro).
     *
     * @param type tipo do Tetromino
     */
    public Tetromino(Type type) {
        this(type, 4, 0); // Posição padrão de spawn
    }

    /**
     * Cria um novo Tetromino com posição específica.
     */
    public Tetromino(Type type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.shape = initShape(type);
        this.color = initColor(type);
    }

    /**
     * Cria uma cópia do Tetromino com nova posição (imutabilidade).
     */
    public Tetromino moveTo(int newX, int newY) {
        return new Tetromino(this.type, newX, newY);
    }

    /**
     * Cria uma cópia rotacionada do Tetromino (90 graus no sentido horário).
     */
    public Tetromino rotated() {
        int[][] rotatedShape = rotateShape(shape);
        return new RotatedTetromino(this.type, this.x, this.y, rotatedShape);
    }

    /**
     * Subclasse privada para Tetromino rotacionado.
     */
    private static class RotatedTetromino extends Tetromino {
        private final int[][] rotatedShape;

        RotatedTetromino(Type type, int x, int y, int[][] rotatedShape) {
            super(type, x, y);
            this.rotatedShape = rotatedShape;
        }

        @Override
        public int[][] getShape() {
            return rotatedShape;
        }

        @Override
        public Tetromino moveTo(int newX, int newY) {
            // IMPORTANTE: Manter o shape rotacionado ao mover
            return new RotatedTetromino(getType(), newX, newY, rotatedShape);
        }

        @Override
        public Tetromino rotated() {
            // Rotacionar o shape atual (já rotacionado), não o original
            int[][] newRotatedShape = rotateShape(rotatedShape);
            return new RotatedTetromino(getType(), getX(), getY(), newRotatedShape);
        }
    }

    /**
     * Inicializa a forma do Tetromino baseado no tipo.
     */
    private static int[][] initShape(Type type) {
        if (type == Type.I)
            return new int[][] { { 1, 1, 1, 1 } };
        if (type == Type.O)
            return new int[][] { { 1, 1 }, { 1, 1 } };
        if (type == Type.T)
            return new int[][] { { 0, 1, 0 }, { 1, 1, 1 } };
        if (type == Type.S)
            return new int[][] { { 0, 1, 1 }, { 1, 1, 0 } };
        if (type == Type.Z)
            return new int[][] { { 1, 1, 0 }, { 0, 1, 1 } };
        if (type == Type.J)
            return new int[][] { { 1, 0, 0 }, { 1, 1, 1 } };
        if (type == Type.L)
            return new int[][] { { 0, 0, 1 }, { 1, 1, 1 } };
        return new int[][] { { 0 } };
    }

    /**
     * Inicializa a cor do Tetromino baseado no tipo (cores oficiais de Tetris).
     */
    private static Color initColor(Type type) {
        if (type == Type.I)
            return new Color(0, 255, 255); // Cyan
        if (type == Type.O)
            return new Color(255, 255, 0); // Yellow
        if (type == Type.T)
            return new Color(128, 0, 255); // Purple
        if (type == Type.S)
            return new Color(0, 255, 0); // Green
        if (type == Type.Z)
            return new Color(255, 0, 0); // Red
        if (type == Type.J)
            return new Color(0, 0, 255); // Blue
        if (type == Type.L)
            return new Color(255, 165, 0); // Orange
        return Color.WHITE;
    }

    /**
     * Rotaciona uma forma 90 graus no sentido horário.
     */
    private static int[][] rotateShape(int[][] shape) {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = shape[i][j];
            }
        }
        return rotated;
    }

    // Getters

    public Type getType() {
        return type;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
