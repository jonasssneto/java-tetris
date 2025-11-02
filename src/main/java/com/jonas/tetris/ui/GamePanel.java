package com.jonas.tetris.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.jonas.tetris.domain.Board;
import com.jonas.tetris.domain.Tetromino;
import com.jonas.tetris.engine.GameController;
import com.jonas.tetris.engine.GameState;

/**
 * Painel de renderização do jogo.
 * Desenha o tabuleiro, peças, e elementos visuais.
 */
public class GamePanel extends JPanel implements ThemeManager.ThemeChangeListener {
    private static final int TILE_SIZE = 35;
    private GameState currentState;
    private final GameController controller;

    public GamePanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(
                Board.BOARD_WIDTH * TILE_SIZE,
                Board.BOARD_HEIGHT * TILE_SIZE));
        updateThemeColors();

        // Registrar listener de tema
        ThemeManager.addThemeChangeListener(this);
    }

    private void updateThemeColors() {
        setBackground(ThemeManager.getBackgroundColor());
        setBorder(BorderFactory.createLineBorder(ThemeManager.getAccentColor(), 2));
    }

    @Override
    public void onThemeChanged() {
        SwingUtilities.invokeLater(() -> {
            updateThemeColors();
            repaint();
        });
    }

    public void updateState(GameState state) {
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

        // Desenhar grid
        drawGrid(g2d);

        // Desenhar blocos fixos no tabuleiro
        drawBoardBlocks(g2d, currentState);

        // Desenhar peça sombra (ghost)
        if (currentState.getShadowPiece() != null) {
            drawShadowPiece(g2d, currentState.getShadowPiece());
        }

        // Desenhar peça atual
        if (currentState.getCurrentPiece() != null) {
            drawPiece(g2d, currentState.getCurrentPiece(), false);
        }

        // Desenhar status
        if (currentState.getStatus() == GameState.GameStatus.PAUSED) {
            drawPauseOverlay(g2d);
        }
    }

    private void drawGrid(Graphics2D g2d) {
        g2d.setColor(ThemeManager.getGridColor());
        g2d.setStroke(new BasicStroke(1));

        int width = Board.BOARD_WIDTH * TILE_SIZE;
        int height = Board.BOARD_HEIGHT * TILE_SIZE;

        // Linhas verticais
        for (int i = 0; i <= Board.BOARD_WIDTH; i++) {
            int x = i * TILE_SIZE;
            g2d.drawLine(x, 0, x, height);
        }

        // Linhas horizontais
        for (int i = 0; i <= Board.BOARD_HEIGHT; i++) {
            int y = i * TILE_SIZE;
            g2d.drawLine(0, y, width, y);
        }
    }

    private void drawBoardBlocks(Graphics2D g2d, GameState state) {
        int[][] grid = state.getBoardGrid();
        Color[][] colors = state.getBoardColors();

        for (int y = 2; y < grid.length; y++) { // Pular buffer de spawn (primeiras 2 linhas)
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != 0 && colors[y][x] != null) {
                    int screenY = (y - 2) * TILE_SIZE; // Ajustar para cordinadas de tela
                    drawBlock(g2d, x * TILE_SIZE, screenY, colors[y][x]);
                }
            }
        }
    }

    private void drawPiece(Graphics2D g2d, Tetromino piece, boolean isShadow) {
        int[][] shape = piece.getShape();
        Color color = piece.getColor();

        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int screenX = (piece.getX() + j) * TILE_SIZE;
                    int screenY = ((piece.getY() + i) - 2) * TILE_SIZE; // Ajustar pelo buffer + linha da shape

                    if (isShadow) {
                        drawShadowBlock(g2d, screenX, screenY, color);
                    } else {
                        drawBlock(g2d, screenX, screenY, color);
                    }
                }
            }
        }
    }

    private void drawShadowPiece(Graphics2D g2d, Tetromino shadowPiece) {
        drawPiece(g2d, shadowPiece, true);
    }

    private void drawBlock(Graphics2D g2d, int x, int y, Color color) {
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillRoundRect(x + 3, y + 3, TILE_SIZE - 6, TILE_SIZE - 6, 6, 6);

        // Bloco principal
        g2d.setColor(color);
        g2d.fillRoundRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 6, 6);

        // Highlight
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillRoundRect(x + 1, y + 1, TILE_SIZE - 2, 8, 6, 6);

        // Borda
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x + 1, y + 1, TILE_SIZE - 2, TILE_SIZE - 2, 6, 6);
    }

    private void drawShadowBlock(Graphics2D g2d, int x, int y, Color color) {
        // Apenas borda da sombra
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 6, 6);

        // Preenchimento transparente
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        g2d.fillRoundRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4, 6, 6);
    }

    private void drawPauseOverlay(Graphics2D g2d) {
        // Overlay semi-transparente
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Texto
        g2d.setColor(Color.WHITE);
        Font font = new Font("Arial", Font.BOLD, 36);
        g2d.setFont(font);
        String text = "PAUSADO";
        FontMetrics fm = g2d.getFontMetrics(font);
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = getHeight() / 2;
        g2d.drawString(text, x, y);
    }
}
