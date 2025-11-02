package com.jonas.tetris.ui;

import com.jonas.tetris.engine.GameState;
import com.jonas.tetris.util.FontLoader;

import javax.swing.*;
import java.awt.*;

/**
 * Painel lateral exibindo informações do jogo (score, level, próximas peças,
 * etc).
 */
public class InfoPanel extends JPanel {
    private static final int PANEL_WIDTH = 250;
    private static final int TILE_SIZE = 35;

    private final Color bgColor = new Color(25, 25, 35);
    private final Color textColor = new Color(220, 220, 220);
    private final Color accentColor = new Color(100, 149, 237);

    private GameState currentState;

    public InfoPanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, 20 * TILE_SIZE));
        setBackground(bgColor);
        setBorder(BorderFactory.createLineBorder(accentColor, 2));
    }

    public void updateInfo(GameState state) {
        this.currentState = state;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (currentState == null) {
            return;
        }

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int padding = panelWidth / 15;
        int y = padding;

        // Fonte
        float fontSize = panelWidth / 10f;
        Font titleFont = FontLoader.loadFont("/font/font.ttf", fontSize);
        if (titleFont == null) {
            titleFont = new Font("Arial", Font.BOLD, (int) fontSize);
        }

        // Título "PRÓXIMA"
        g2d.setFont(titleFont);
        g2d.setColor(textColor);
        g2d.drawString("PRÓXIMA", padding, y + (int) fontSize);
        y += (int) fontSize + padding;

        // Área da próxima peça
        int squareSize = panelWidth - 2 * padding;
        g2d.setColor(accentColor);
        g2d.fillRect(padding, y, squareSize, squareSize);
        g2d.setColor(bgColor);
        g2d.fillRect(padding + 2, y + 2, squareSize - 4, squareSize - 4);

        // Desenhar próxima peça
        if (!currentState.getNextQueue().isEmpty()) {
            drawNextPiece(g2d, currentState.getNextQueue().get(0), padding + 2, y + 2, squareSize - 4);
        }

        y += squareSize + padding * 3;

        // Informações numéricas
        float infoFontSize = panelWidth / 12f;
        g2d.setFont(titleFont.deriveFont(infoFontSize));
        g2d.setColor(textColor);

        g2d.drawString("Score: " + currentState.getScore(), padding, y);
        y += (int) infoFontSize + padding;

        g2d.drawString("Level: " + currentState.getLevel(), padding, y);
        y += (int) infoFontSize + padding;

        g2d.drawString("Lines: " + currentState.getTotalLines(), padding, y);
        y += (int) infoFontSize + padding * 2;

        g2d.drawString("Time: " + currentState.getFormattedTime(), padding, y);
        y += (int) infoFontSize + padding;

        g2d.drawString("Pieces: " + currentState.getTotalPieces(), padding, y);
        y += (int) infoFontSize + padding;

        g2d.drawString("PPS: " + String.format("%.2f", currentState.getPiecesPerSecond()), padding, y);

        // Hold
        y += (int) infoFontSize + padding * 2;
        g2d.drawString("HOLD", padding, y);
        y += (int) infoFontSize + padding / 2;

        int holdSize = Math.min(60, squareSize / 2);
        g2d.setColor(accentColor);
        g2d.drawRect(padding, y, holdSize, holdSize);

        if (currentState.getHoldPiece() != null) {
            drawNextPiece(g2d, currentState.getHoldPiece(), padding + 2, y + 2, holdSize - 4);
        }
    }

    private void drawNextPiece(Graphics2D g2d, com.jonas.tetris.domain.Tetromino piece, int x, int y, int size) {
        if (piece == null)
            return;

        int[][] shape = piece.getShape();
        Color color = piece.getColor();

        int rows = shape.length;
        int cols = shape[0].length;

        int innerPadding = Math.max(2, size / 12);
        int available = size - innerPadding * 2;
        int blockSize = Math.max(1, Math.min(available / Math.max(1, cols), available / Math.max(1, rows)));

        int pieceWidth = blockSize * cols;
        int pieceHeight = blockSize * rows;
        int startX = x + (size - pieceWidth) / 2;
        int startY = y + (size - pieceHeight) / 2;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (shape[i][j] != 0) {
                    int bx = startX + j * blockSize;
                    int by = startY + i * blockSize;

                    // Sombra
                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.fillRect(bx + Math.max(1, blockSize / 8), by + Math.max(1, blockSize / 8),
                            blockSize - Math.max(2, blockSize / 6), blockSize - Math.max(2, blockSize / 6));

                    // Bloco
                    g2d.setColor(color);
                    g2d.fillRect(bx, by, blockSize - 1, blockSize - 1);

                    // Highlight
                    g2d.setColor(new Color(255, 255, 255, 80));
                    g2d.fillRect(bx, by, blockSize - 1, Math.max(2, blockSize / 4));

                    // Borda
                    g2d.setColor(color.darker());
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRect(bx, by, blockSize - 1, blockSize - 1);
                }
            }
        }
    }
}
