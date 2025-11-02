package com.jonas.tetris.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utilitário para criar transições suaves entre telas
 */
public class ScreenTransition {

    /**
     * Realiza fade out na janela atual e depois executa ação
     */
    public static void fadeOut(JFrame frame, Runnable onComplete) {
        fadeOut(frame, 300, onComplete);
    }

    public static void fadeOut(JFrame frame, int durationMs, Runnable onComplete) {
        if (!frame.isUndecorated()) {
            // Se não for fullscreen, executar imediatamente
            onComplete.run();
            return;
        }

        class FadePanel extends JPanel {
            private float opacity = 0f;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }

            public void setOpacity(float opacity) {
                this.opacity = opacity;
                repaint();
            }
        }

        FadePanel fadePanel = new FadePanel();
        fadePanel.setOpaque(false);
        fadePanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        if (frame.getRootPane().getLayeredPane().getComponentCount() > 0) {
            frame.getRootPane().getLayeredPane().add(fadePanel, JLayeredPane.DRAG_LAYER);
        }

        int steps = 30;
        int delay = durationMs / steps;

        Timer fadeTimer = new Timer(delay, null);
        final int[] step = { 0 };

        fadeTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                step[0]++;
                float opacity = (float) step[0] / steps;
                fadePanel.setOpacity(opacity);

                if (step[0] >= steps) {
                    fadeTimer.stop();
                    onComplete.run();
                }
            }
        });

        fadeTimer.start();
    }

    /**
     * Realiza fade in na janela
     */
    public static void fadeIn(JFrame frame) {
        fadeIn(frame, 300);
    }

    public static void fadeIn(JFrame frame, int durationMs) {
        if (!frame.isUndecorated()) {
            return;
        }

        class FadePanel extends JPanel {
            private float opacity = 1f;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }

            public void setOpacity(float opacity) {
                this.opacity = opacity;
                repaint();
            }
        }

        FadePanel fadePanel = new FadePanel();
        fadePanel.setOpaque(false);

        // Esperar frame ser visível
        SwingUtilities.invokeLater(() -> {
            fadePanel.setBounds(0, 0, frame.getWidth(), frame.getHeight());
            frame.getRootPane().getLayeredPane().add(fadePanel, JLayeredPane.DRAG_LAYER);

            int steps = 30;
            int delay = durationMs / steps;

            Timer fadeTimer = new Timer(delay, null);
            final int[] step = { 0 };

            fadeTimer.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    step[0]++;
                    float opacity = 1f - ((float) step[0] / steps);
                    fadePanel.setOpacity(opacity);

                    if (step[0] >= steps) {
                        fadeTimer.stop();
                        frame.getRootPane().getLayeredPane().remove(fadePanel);
                        frame.getRootPane().getLayeredPane().revalidate();
                        frame.getRootPane().getLayeredPane().repaint();
                    }
                }
            });

            fadeTimer.start();
        });
    }
}
