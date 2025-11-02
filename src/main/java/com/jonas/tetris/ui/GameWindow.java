package com.jonas.tetris.ui;

import com.jonas.tetris.engine.GameController;
import com.jonas.tetris.engine.GameState;
import com.jonas.tetris.engine.LevelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Janela principal do jogo.
 * Contém o painel de jogo e o painel de informações lado a lado.
 */
public class GameWindow extends JFrame {
    private final GameController controller;
    private final GamePanel gamePanel;
    private final InfoPanel infoPanel;
    private Timer gameLoopTimer;
    private Timer renderTimer;

    private volatile int currentLevel = 1;
    private volatile boolean gameOverHandled = false;

    public GameWindow(GameController controller) {
        this.controller = controller;

        setTitle("TETRIS - Jogo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);

        // Painel principal
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(15, 15, 25));

        // Painel do jogo (centro)
        gamePanel = new GamePanel(controller);
        mainPanel.add(gamePanel, BorderLayout.CENTER);

        // Painel de info (direita)
        infoPanel = new InfoPanel();
        mainPanel.add(infoPanel, BorderLayout.EAST);

        add(mainPanel);
        pack();

        // Key listener para input
        addKeyListener(new GameKeyListener());
        setFocusable(true);
        requestFocus();

        // Game loop (separado da EDT)
        startGameLoop();

        // Render loop (EDT)
        startRenderLoop();
    }

    /**
     * Loop de lógica do jogo (EDT com Timer dinâmico).
     */
    private void startGameLoop() {
        gameLoopTimer = new Timer(LevelManager.getSpeedForLevel(currentLevel), e -> {
            controller.update();

            // Ajustar velocidade se o nível mudou
            GameState state = controller.getState();
            if (state.getLevel() != currentLevel) {
                currentLevel = state.getLevel();
                gameLoopTimer.setDelay(LevelManager.getSpeedForLevel(currentLevel));
            }
        });
        gameLoopTimer.start();
    }

    /**
     * Loop de renderização (EDT).
     */
    private void startRenderLoop() {
        renderTimer = new Timer(50, e -> {
            GameState state = controller.getState();

            gamePanel.updateState(state);
            infoPanel.updateInfo(state);

            if (state.getStatus() == GameState.GameStatus.GAME_OVER && !gameOverHandled) {
                gameOverHandled = true;
                handleGameOver(state);
            }
        });
        renderTimer.start();
    }

    private void handleGameOver(GameState state) {
        int result = JOptionPane.showConfirmDialog(
                this,
                "GAME OVER!\n\nPontuação Final: " + state.getScore() + "\nNível: " + state.getLevel(),
                "Fim de Jogo",
                JOptionPane.OK_CANCEL_OPTION);

        // Salvar recorde
        com.jonas.tetris.persistence.ScoreRepository.saveScore(state.getScore());

        if (result == JOptionPane.OK_OPTION) {
            // Novo jogo - resetar flag
            gameOverHandled = false;
            controller.startGame();
        } else {
            // Voltar ao menu
            stopTimers();
            setVisible(false);
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenu());
        }
    }

    private class GameKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    controller.moveLeft();
                    break;
                case KeyEvent.VK_RIGHT:
                    controller.moveRight();
                    break;
                case KeyEvent.VK_DOWN:
                    controller.softDrop();
                    break;
                case KeyEvent.VK_UP:
                    controller.rotate();
                    break;
                case KeyEvent.VK_SPACE:
                    controller.hardDrop();
                    break;
                case KeyEvent.VK_C:
                    controller.hold();
                    break;
                case KeyEvent.VK_P:
                    controller.pause();
                    break;
                case KeyEvent.VK_ESCAPE:
                    handleEscape();
                    break;
            }

            // Atualizar UI imediatamente (já estamos na EDT)
            GameState state = controller.getState();
            gamePanel.updateState(state);
            infoPanel.updateInfo(state);
        }
    }

    private void handleEscape() {
        int result = JOptionPane.showConfirmDialog(
                this,
                "Deseja voltar ao menu?",
                "Pausar",
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            stopTimers();
            setVisible(false);
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenu());
        }
    }

    private void stopTimers() {
        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
        if (renderTimer != null) {
            renderTimer.stop();
        }
    }
}
