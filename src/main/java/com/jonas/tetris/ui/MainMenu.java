package com.jonas.tetris.ui;

import com.jonas.tetris.engine.GameController;
import com.jonas.tetris.persistence.ScoreRepository;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Tela inicial (Main Menu) do Tetris.
 * Exibe opções para Jogar, Instruções, Opções e Sair.
 */
public class MainMenu extends JFrame {
    private GameWindow gameWindow;
    private int highScore;

    public MainMenu() {
        setTitle("TETRIS - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); // Sem bordas para fullscreen

        // Carregar recorde
        highScore = ScoreRepository.getHighScore();

        // Painel principal com layout centralizado
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(15, 15, 25));

        // Container para conteúdo centralizado
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(15, 15, 25));
        contentPanel.setOpaque(false);

        // Titulo
        JLabel titleLabel = new JLabel("TETRIS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 72));
        titleLabel.setForeground(new Color(100, 149, 237));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espaçador
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(30));

        // Recorde
        JLabel scoreLabel = new JLabel("Recorde: " + highScore);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        scoreLabel.setForeground(new Color(220, 220, 220));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(scoreLabel);
        contentPanel.add(Box.createVerticalStrut(50));

        // Botões
        JButton playButton = createButton("Jogar");
        playButton.addActionListener(e -> startGame());

        JButton instructionsButton = createButton("Instruções");
        instructionsButton.addActionListener(e -> showInstructions());

        JButton optionsButton = createButton("Opções");
        optionsButton.addActionListener(e -> showOptions());

        JButton exitButton = createButton("Sair");
        exitButton.addActionListener(e -> System.exit(0));

        contentPanel.add(playButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(instructionsButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(optionsButton);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(exitButton);

        // Adicionar contentPanel ao mainPanel (centralizado)
        mainPanel.add(contentPanel);

        // Adicionar key listener ao mainPanel
        mainPanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    System.exit(0);
                }
            }
        });
        mainPanel.setFocusable(true);

        add(mainPanel);

        // Configurar fullscreen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        setVisible(true);
        mainPanel.requestFocus();
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(250, 50));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(100, 149, 237));
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(120, 170, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
        });

        return button;
    }

    private void startGame() {
        // Sair do fullscreen do menu
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.getFullScreenWindow() == this) {
            gd.setFullScreenWindow(null);
        }

        GameController controller = new GameController();
        controller.startGame();

        gameWindow = new GameWindow(controller);
        gameWindow.setVisible(true);
        setVisible(false);
        dispose();
    }

    private void showInstructions() {
        String instructions = """
                === INSTRUÇÕES ===

                Controles:
                • Esquerda/Direita: Mover peça
                • Baixo: Soft drop (1 ponto/célula)
                • Espaço: Hard drop (2 pontos/célula)
                • Seta para cima: Rotacionar (clockwise)
                • C: Hold (trocar com peça guardada)
                • P: Pausar/Resumir
                • ESC: Voltar ao menu

                Pontuação:
                • 1 linha: 40 × nível
                • 2 linhas: 100 × nível
                • 3 linhas: 300 × nível
                • 4 linhas: 1200 × nível

                Níveis:
                • Sobe a cada 10 linhas
                • Cada nível aumenta a velocidade
                """;

        JTextArea textArea = new JTextArea(instructions);
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        textArea.setBackground(new Color(25, 25, 35));
        textArea.setForeground(new Color(220, 220, 220));

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));

        JOptionPane.showMessageDialog(this, scrollPane, "Instruções", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showOptions() {
        JPanel optionsPanel = new JPanel();
        optionsPanel.setLayout(new BoxLayout(optionsPanel, BoxLayout.Y_AXIS));

        JCheckBox soundCheckBox = new JCheckBox("Som habilitado");
        soundCheckBox.setSelected(true);

        JLabel volumeLabel = new JLabel("Volume:");
        JSlider volumeSlider = new JSlider(0, 100, 50);

        optionsPanel.add(soundCheckBox);
        optionsPanel.add(Box.createVerticalStrut(10));
        optionsPanel.add(volumeLabel);
        optionsPanel.add(volumeSlider);

        JOptionPane.showMessageDialog(this, optionsPanel, "Opções", JOptionPane.PLAIN_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
