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
    private JPanel overlayPanel;

    private volatile int currentLevel = 1;
    private volatile boolean gameOverHandled = false;

    public GameWindow(GameController controller) {
        this.controller = controller;

        setTitle("TETRIS - Jogo");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true); // Remove bordas para fullscreen

        // Painel principal com layout para centralizar
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(15, 15, 25));

        // Container do jogo (painel + info)
        JPanel gameContainer = new JPanel(new BorderLayout());
        gameContainer.setBackground(new Color(15, 15, 25));

        // Painel do jogo (centro)
        gamePanel = new GamePanel(controller);
        gameContainer.add(gamePanel, BorderLayout.CENTER);

        // Painel de info (direita)
        infoPanel = new InfoPanel();
        gameContainer.add(infoPanel, BorderLayout.EAST);

        // Adicionar gameContainer centralizado no mainPanel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(gameContainer, gbc);

        // Usar JLayeredPane para overlay
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));

        mainPanel.setOpaque(true);
        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);

        // Overlay invisível inicialmente
        overlayPanel = createOverlayPanel();
        overlayPanel.setVisible(false);
        layeredPane.add(overlayPanel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);

        // Configurar fullscreen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            // Fallback: maximizar janela
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        // Key listener para input
        addKeyListener(new GameKeyListener());
        setFocusable(true);
        requestFocus();

        // Game loop (separado da EDT)
        startGameLoop();

        // Render loop (EDT)
        startRenderLoop();
    }

    private JPanel createOverlayPanel() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(0, 0, 0, 200));
        overlay.setOpaque(false);
        return overlay;
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
        // Pausar timers
        if (gameLoopTimer != null)
            gameLoopTimer.stop();
        if (renderTimer != null)
            renderTimer.stop();

        // Criar overlay customizado
        overlayPanel.removeAll();
        overlayPanel.setOpaque(true);
        overlayPanel.setBackground(new Color(0, 0, 0, 220));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Título GAME OVER
        JLabel titleLabel = new JLabel("GAME OVER");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(new Color(255, 100, 100));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espaço
        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(50));

        // Pontuação
        JLabel scoreLabel = new JLabel("Pontuação Final: " + state.getScore());
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 36));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scoreLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Nível
        JLabel levelLabel = new JLabel("Nível: " + state.getLevel());
        levelLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        levelLabel.setForeground(new Color(220, 220, 220));
        levelLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(levelLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Linhas
        JLabel linesLabel = new JLabel("Linhas: " + state.getTotalLines());
        linesLabel.setFont(new Font("Arial", Font.PLAIN, 28));
        linesLabel.setForeground(new Color(220, 220, 220));
        linesLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(linesLabel);
        contentPanel.add(Box.createVerticalStrut(60));

        // Botões
        JButton retryButton = createOverlayButton("Jogar Novamente");
        retryButton.addActionListener(e -> {
            gameOverHandled = false;
            overlayPanel.setVisible(false);
            controller.startGame();
            if (gameLoopTimer != null)
                gameLoopTimer.start();
            if (renderTimer != null)
                renderTimer.start();
            requestFocus();
        });

        JButton menuButton = createOverlayButton("Voltar ao Menu");
        menuButton.addActionListener(e -> {
            stopTimers();
            setVisible(false);
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenu());
        });

        contentPanel.add(retryButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(menuButton);

        overlayPanel.add(contentPanel);
        overlayPanel.setVisible(true);
        overlayPanel.revalidate();
        overlayPanel.repaint();

        // Salvar recorde
        com.jonas.tetris.persistence.ScoreRepository.saveScore(state.getScore());
    }

    private JButton createOverlayButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(300, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(120, 170, 255));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
        });

        return button;
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
                case KeyEvent.VK_F11:
                    toggleFullscreen();
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

    private void toggleFullscreen() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        if (gd.getFullScreenWindow() == this) {
            // Sair do fullscreen
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setExtendedState(JFrame.NORMAL);
            setVisible(true);
            pack();
            setLocationRelativeTo(null);
        } else {
            // Entrar em fullscreen
            dispose();
            setUndecorated(true);
            setVisible(true);
            gd.setFullScreenWindow(this);
        }

        requestFocus();
    }

    private void stopTimers() {
        if (gameLoopTimer != null) {
            gameLoopTimer.stop();
        }
        if (renderTimer != null) {
            renderTimer.stop();
        }

        // Sair do fullscreen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.getFullScreenWindow() == this) {
            gd.setFullScreenWindow(null);
        }
    }
}
