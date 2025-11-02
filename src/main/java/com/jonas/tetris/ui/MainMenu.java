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
    private static final int WIDTH = 400;
    private static final int HEIGHT = 600;

    private GameWindow gameWindow;
    private int highScore;

    public MainMenu() {
        setTitle("TETRIS - Menu Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        setLocationRelativeTo(null);

        // Carregar recorde
        highScore = ScoreRepository.getHighScore();

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(15, 15, 25));

        // Titulo
        JLabel titleLabel = new JLabel("TETRIS");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(new Color(100, 149, 237));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Espaçador
        mainPanel.add(Box.createVerticalStrut(40));
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Recorde
        JLabel scoreLabel = new JLabel("Recorde: " + highScore);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        scoreLabel.setForeground(new Color(220, 220, 220));
        scoreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(scoreLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Botões
        JButton playButton = createButton("Jogar");
        playButton.addActionListener(e -> startGame());

        JButton instructionsButton = createButton("Instruções");
        instructionsButton.addActionListener(e -> showInstructions());

        JButton optionsButton = createButton("Opções");
        optionsButton.addActionListener(e -> showOptions());

        JButton exitButton = createButton("Sair");
        exitButton.addActionListener(e -> System.exit(0));

        mainPanel.add(playButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(instructionsButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(optionsButton);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(exitButton);

        mainPanel.add(Box.createVerticalGlue());

        // Adicionar key listener
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
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(200, 40));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 16));
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
        GameController controller = new GameController();
        controller.startGame();

        gameWindow = new GameWindow(controller);
        gameWindow.setVisible(true);
        setVisible(false);
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
