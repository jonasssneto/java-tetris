package com.jonas.tetris.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeListener;

/**
 * Tela de configurações do Tetris - Customização Visual
 */
public class SettingsScreen extends JFrame implements ThemeManager.ThemeChangeListener {

    private final Color bgColor = new Color(15, 15, 25);
    private final Color accentColor = new Color(100, 149, 237);
    private final Color textColor = new Color(220, 220, 220);

    private final Map<String, ThemeColors> themes = new HashMap<>();
    private ThemeColors currentTheme;

    private JPanel themePreviewPanel;
    private JLayeredPane layeredPane;
    private JPanel mainContent;

    public SettingsScreen() {
        setTitle("TETRIS - Customização Visual");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        initializeThemes();
        loadCurrentTheme();

        // Registrar listener de tema
        ThemeManager.addThemeChangeListener(this);

        // Usar JLayeredPane para overlays
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(null);

        mainContent = new JPanel(new BorderLayout());
        mainContent.setBackground(bgColor);
        mainContent.setBorder(new EmptyBorder(40, 60, 40, 60));

        mainContent.add(createHeader(), BorderLayout.NORTH);
        mainContent.add(createCenterPanel(), BorderLayout.CENTER);
        mainContent.add(createFooter(), BorderLayout.SOUTH);

        add(layeredPane);

        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        if (gd.isFullScreenSupported()) {
            gd.setFullScreenWindow(this);
        } else {
            setExtendedState(JFrame.MAXIMIZED_BOTH);
        }

        // Adicionar listener para redimensionar
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateLayeredPaneBounds();
            }
        });

        setVisible(true);
        updateLayeredPaneBounds();

        // Fade in suave
        ScreenTransition.fadeIn(this);
    }

    private void updateLayeredPaneBounds() {
        Dimension size = getSize();
        mainContent.setBounds(0, 0, size.width, size.height);
        layeredPane.removeAll();
        layeredPane.add(mainContent, JLayeredPane.DEFAULT_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private void initializeThemes() {
        themes.put("Clássico", new ThemeColors(new Color(25, 25, 35), new Color(100, 149, 237), new Color(50, 50, 60)));
        themes.put("Neon", new ThemeColors(new Color(10, 10, 20), new Color(0, 255, 255), new Color(255, 0, 255)));
        themes.put("Matrix", new ThemeColors(new Color(0, 0, 0), new Color(0, 255, 0), new Color(0, 100, 0)));
        themes.put("Sunset", new ThemeColors(new Color(30, 20, 40), new Color(255, 100, 150), new Color(255, 150, 50)));
        themes.put("Ocean", new ThemeColors(new Color(10, 20, 40), new Color(50, 150, 255), new Color(30, 100, 180)));
        themes.put("Dark Purple",
                new ThemeColors(new Color(20, 10, 30), new Color(150, 50, 255), new Color(100, 30, 150)));
    }

    private void loadCurrentTheme() {
        currentTheme = new ThemeColors(
                ThemeManager.getBackgroundColor(),
                ThemeManager.getAccentColor(),
                ThemeManager.getGridColor());
    }

    @Override
    public void onThemeChanged() {
        // Atualizar preview quando tema mudar
        updatePreview();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(bgColor);
        header.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = new JLabel("CUSTOMIZAÇÃO VISUAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 56));
        titleLabel.setForeground(accentColor);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Personalize cores e temas do Tetris");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 20));
        subtitleLabel.setForeground(textColor);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(10));
        header.add(subtitleLabel);

        return header;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(bgColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);

        JPanel leftPanel = createThemesPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        gbc.fill = GridBagConstraints.BOTH;
        center.add(leftPanel, gbc);

        JPanel rightPanel = createCustomizationPanel();
        gbc.gridx = 1;
        center.add(rightPanel, gbc);

        return center;
    }

    private JPanel createThemesPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel sectionTitle = new JLabel("Temas Predefinidos");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 28));
        sectionTitle.setForeground(accentColor);
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(20));

        for (String themeName : themes.keySet()) {
            JButton themeButton = createThemeButton(themeName);
            panel.add(themeButton);
            panel.add(Box.createVerticalStrut(15));
        }

        return panel;
    }

    private JButton createThemeButton(String themeName) {
        ThemeColors theme = themes.get(themeName);
        JButton button = new JButton(themeName);
        button.setMaximumSize(new Dimension(400, 60));
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(theme.background);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createLineBorder(theme.accent, 2));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(theme.accent.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(theme.background);
            }
        });

        button.addActionListener(e -> applyTheme(themeName));
        return button;
    }

    private JPanel createCustomizationPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(20, 20, 30));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                new EmptyBorder(20, 20, 20, 20)));

        JLabel sectionTitle = new JLabel("Customização Manual");
        sectionTitle.setFont(new Font("Arial", Font.BOLD, 28));
        sectionTitle.setForeground(accentColor);
        sectionTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(sectionTitle);
        panel.add(Box.createVerticalStrut(20));

        themePreviewPanel = createPreviewPanel();
        themePreviewPanel.setMaximumSize(new Dimension(400, 150));
        themePreviewPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(themePreviewPanel);
        panel.add(Box.createVerticalStrut(25));

        panel.add(createColorOption("Cor de Fundo:", currentTheme.background,
                color -> {
                    currentTheme.background = color;
                    updatePreview();
                }));
        panel.add(Box.createVerticalStrut(15));

        panel.add(createColorOption("Cor de Destaque:", currentTheme.accent,
                color -> {
                    currentTheme.accent = color;
                    updatePreview();
                }));
        panel.add(Box.createVerticalStrut(15));

        panel.add(createColorOption("Cor da Grade:", currentTheme.grid,
                color -> {
                    currentTheme.grid = color;
                    updatePreview();
                }));

        return panel;
    }

    private JPanel createPreviewPanel() {
        return new JPanel() {
            {
                setPreferredSize(new Dimension(400, 150));
                setBorder(BorderFactory.createLineBorder(accentColor, 2));
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                g2.setColor(currentTheme.background);
                g2.fillRect(0, 0, w, h);

                g2.setColor(currentTheme.grid);
                int cellSize = 20;
                for (int i = 0; i <= w / cellSize; i++) {
                    g2.drawLine(i * cellSize, 0, i * cellSize, h);
                }
                for (int i = 0; i <= h / cellSize; i++) {
                    g2.drawLine(0, i * cellSize, w, i * cellSize);
                }

                g2.setColor(currentTheme.accent);
                g2.fillRoundRect(50, 30, cellSize, cellSize, 5, 5);
                g2.fillRoundRect(70, 30, cellSize, cellSize, 5, 5);
                g2.fillRoundRect(90, 30, cellSize, cellSize, 5, 5);
                g2.fillRoundRect(70, 50, cellSize, cellSize, 5, 5);

                g2.setColor(textColor);
                g2.setFont(new Font("Arial", Font.BOLD, 16));
                g2.drawString("Preview", 10, h - 10);

                g2.dispose();
            }
        };
    }

    private JPanel createColorOption(String label, Color initialColor, ColorChangeListener listener) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(20, 20, 30));
        panel.setMaximumSize(new Dimension(500, 50));

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.PLAIN, 18));
        labelComponent.setForeground(textColor);
        labelComponent.setPreferredSize(new Dimension(150, 40));

        JButton colorButton = new JButton();
        colorButton.setPreferredSize(new Dimension(100, 40));
        colorButton.setBackground(initialColor);
        colorButton.setForeground(getContrastColor(initialColor));
        colorButton.setText(String.format("#%02X%02X%02X",
                initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue()));
        colorButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        colorButton.setFocusPainted(false);
        colorButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        colorButton.addActionListener(e -> {
            showCustomColorPicker(label, colorButton.getBackground(), newColor -> {
                colorButton.setBackground(newColor);
                colorButton.setForeground(getContrastColor(newColor));
                colorButton.setText(String.format("#%02X%02X%02X",
                        newColor.getRed(), newColor.getGreen(), newColor.getBlue()));
                listener.onColorChanged(newColor);
            });
        });

        panel.add(labelComponent);
        panel.add(colorButton);

        return panel;
    }

    private Color getContrastColor(Color color) {
        double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
        return luminance > 0.5 ? Color.BLACK : Color.WHITE;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        footer.setBackground(bgColor);
        footer.setBorder(new EmptyBorder(30, 0, 0, 0));

        JButton saveButton = createStyledButton("Salvar Tema", accentColor);
        saveButton.addActionListener(e -> saveTheme());

        JButton resetButton = createStyledButton("Restaurar Padrão", new Color(200, 100, 100));
        resetButton.addActionListener(e -> resetToDefault());

        JButton backButton = createStyledButton("Voltar", new Color(100, 100, 120));
        backButton.addActionListener(e -> goBack());

        footer.add(saveButton);
        footer.add(resetButton);
        footer.add(backButton);

        return footer;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 55));
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void applyTheme(String themeName) {
        currentTheme = new ThemeColors(themes.get(themeName));
        updatePreview();
    }

    private void updatePreview() {
        themePreviewPanel.repaint();
    }

    private void saveTheme() {
        ThemeManager.saveTheme(currentTheme.background, currentTheme.accent, currentTheme.grid);
        showFeedbackMessage("Tema aplicado com sucesso!\nTodas as telas foram atualizadas.", accentColor);
    }

    private void resetToDefault() {
        applyTheme("Clássico");
        ThemeManager.resetToDefault();
        showFeedbackMessage("Tema restaurado para o padrão!", accentColor);
    }

    private void showFeedbackMessage(String message, Color color) {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(0, 0, 0, 180));
        overlay.setBounds(0, 0, getWidth(), getHeight());

        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBackground(new Color(20, 20, 30));
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 3),
                new EmptyBorder(30, 40, 30, 40)));

        for (String line : message.split("\n")) {
            JLabel label = new JLabel(line);
            label.setFont(new Font("Arial", Font.BOLD, 20));
            label.setForeground(textColor);
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            messagePanel.add(label);
            messagePanel.add(Box.createVerticalStrut(5));
        }

        messagePanel.add(Box.createVerticalStrut(10));

        JButton okButton = createStyledButton("OK", color);
        okButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        okButton.setPreferredSize(new Dimension(120, 45));
        okButton.addActionListener(e -> {
            layeredPane.remove(overlay);
            layeredPane.revalidate();
            layeredPane.repaint();
        });
        messagePanel.add(okButton);

        overlay.add(messagePanel);
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private void showCustomColorPicker(String title, Color initialColor, ColorChangeListener listener) {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(new Color(0, 0, 0, 180));
        overlay.setBounds(0, 0, getWidth(), getHeight());

        JPanel pickerPanel = new JPanel(new BorderLayout(15, 15));
        pickerPanel.setBackground(new Color(20, 20, 30));
        pickerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 3),
                new EmptyBorder(25, 25, 25, 25)));

        // Título
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(accentColor);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        pickerPanel.add(titleLabel, BorderLayout.NORTH);

        // Painel central com sliders
        JPanel slidersPanel = new JPanel();
        slidersPanel.setLayout(new BoxLayout(slidersPanel, BoxLayout.Y_AXIS));
        slidersPanel.setBackground(new Color(20, 20, 30));

        int[] rgb = { initialColor.getRed(), initialColor.getGreen(), initialColor.getBlue() };
        String[] labels = { "Vermelho (R):", "Verde (G):", "Azul (B):" };
        Color[] sliderColors = { new Color(255, 100, 100), new Color(100, 255, 100), new Color(100, 100, 255) };

        JPanel previewPanel = new JPanel();
        previewPanel.setPreferredSize(new Dimension(300, 80));
        previewPanel.setBackground(initialColor);
        previewPanel.setBorder(BorderFactory.createLineBorder(textColor, 2));

        JLabel hexLabel = new JLabel(String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]));
        hexLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        hexLabel.setForeground(getContrastColor(initialColor));
        hexLabel.setHorizontalAlignment(SwingConstants.CENTER);
        previewPanel.add(hexLabel);

        slidersPanel.add(previewPanel);
        slidersPanel.add(Box.createVerticalStrut(20));

        ChangeListener colorUpdateListener = e -> {
            Color newColor = new Color(rgb[0], rgb[1], rgb[2]);
            previewPanel.setBackground(newColor);
            hexLabel.setText(String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]));
            hexLabel.setForeground(getContrastColor(newColor));
        };

        for (int i = 0; i < 3; i++) {
            final int index = i;

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Arial", Font.BOLD, 16));
            label.setForeground(textColor);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            slidersPanel.add(label);
            slidersPanel.add(Box.createVerticalStrut(5));

            JPanel sliderPanel = new JPanel(new BorderLayout(10, 0));
            sliderPanel.setBackground(new Color(20, 20, 30));
            sliderPanel.setMaximumSize(new Dimension(450, 40));

            JSlider slider = new JSlider(0, 255, rgb[i]);
            slider.setBackground(new Color(20, 20, 30));
            slider.setForeground(sliderColors[i]);
            slider.setPreferredSize(new Dimension(350, 40));

            JLabel valueLabel = new JLabel(String.valueOf(rgb[i]));
            valueLabel.setFont(new Font("Monospaced", Font.BOLD, 16));
            valueLabel.setForeground(textColor);
            valueLabel.setPreferredSize(new Dimension(50, 40));

            slider.addChangeListener(e -> {
                rgb[index] = slider.getValue();
                valueLabel.setText(String.valueOf(rgb[index]));
                colorUpdateListener.stateChanged(e);
            });

            sliderPanel.add(slider, BorderLayout.CENTER);
            sliderPanel.add(valueLabel, BorderLayout.EAST);
            sliderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            slidersPanel.add(sliderPanel);
            slidersPanel.add(Box.createVerticalStrut(15));
        }

        pickerPanel.add(slidersPanel, BorderLayout.CENTER);

        // Botões
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonsPanel.setBackground(new Color(20, 20, 30));

        JButton confirmButton = createStyledButton("Confirmar", accentColor);
        confirmButton.setPreferredSize(new Dimension(150, 45));
        confirmButton.addActionListener(e -> {
            listener.onColorChanged(new Color(rgb[0], rgb[1], rgb[2]));
            layeredPane.remove(overlay);
            layeredPane.revalidate();
            layeredPane.repaint();
        });

        JButton cancelButton = createStyledButton("Cancelar", new Color(120, 120, 120));
        cancelButton.setPreferredSize(new Dimension(150, 45));
        cancelButton.addActionListener(e -> {
            layeredPane.remove(overlay);
            layeredPane.revalidate();
            layeredPane.repaint();
        });

        buttonsPanel.add(confirmButton);
        buttonsPanel.add(cancelButton);
        pickerPanel.add(buttonsPanel, BorderLayout.SOUTH);

        overlay.add(pickerPanel);
        layeredPane.add(overlay, JLayeredPane.POPUP_LAYER);
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    private void goBack() {
        ScreenTransition.fadeOut(this, () -> {
            ThemeManager.removeThemeChangeListener(this);
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gd.getFullScreenWindow() == this) {
                gd.setFullScreenWindow(null);
            }
            setVisible(false);
            dispose();
            SwingUtilities.invokeLater(() -> new MainMenu());
        });
    }

    // Classes internas
    private static class ThemeColors {
        Color background;
        Color accent;
        Color grid;

        ThemeColors(Color background, Color accent, Color grid) {
            this.background = background;
            this.accent = accent;
            this.grid = grid;
        }

        ThemeColors(ThemeColors other) {
            this(other.background, other.accent, other.grid);
        }
    }

    interface ColorChangeListener {
        void onColorChanged(Color color);
    }

    // Métodos públicos para obter tema (delegam para ThemeManager)
    public static Color getBackgroundColor() {
        return ThemeManager.getBackgroundColor();
    }

    public static Color getAccentColor() {
        return ThemeManager.getAccentColor();
    }

    public static Color getGridColor() {
        return ThemeManager.getGridColor();
    }
}
