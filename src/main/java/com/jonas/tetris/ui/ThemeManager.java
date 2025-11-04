package com.jonas.tetris.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final Preferences prefs = Preferences.userNodeForPackage(ThemeManager.class);
    private static final List<ThemeChangeListener> listeners = new ArrayList<>();

    private static Color backgroundColor = new Color(25, 25, 35);
    private static Color accentColor = new Color(100, 149, 237);
    private static Color gridColor = new Color(50, 50, 60);

    static {
        loadTheme();
    }

    public static void loadTheme() {
        if (prefs.getBoolean("custom_theme", false)) {
            backgroundColor = new Color(prefs.getInt("bg_color", backgroundColor.getRGB()));
            accentColor = new Color(prefs.getInt("accent_color", accentColor.getRGB()));
            gridColor = new Color(prefs.getInt("grid_color", gridColor.getRGB()));
        }
    }

    public static void saveTheme(Color bg, Color accent, Color grid) {
        prefs.putBoolean("custom_theme", true);
        prefs.putInt("bg_color", bg.getRGB());
        prefs.putInt("accent_color", accent.getRGB());
        prefs.putInt("grid_color", grid.getRGB());

        backgroundColor = bg;
        accentColor = accent;
        gridColor = grid;

        notifyListeners();
    }

    public static void resetToDefault() {
        prefs.putBoolean("custom_theme", false);
        backgroundColor = new Color(25, 25, 35);
        accentColor = new Color(100, 149, 237);
        gridColor = new Color(50, 50, 60);

        notifyListeners();
    }

    public static Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getAccentColor() {
        return accentColor;
    }

    public static Color getGridColor() {
        return gridColor;
    }

    public static void addThemeChangeListener(ThemeChangeListener listener) {
        listeners.add(listener);
    }

    public static void removeThemeChangeListener(ThemeChangeListener listener) {
        listeners.remove(listener);
    }

    private static void notifyListeners() {
        for (ThemeChangeListener listener : new ArrayList<>(listeners)) {
            listener.onThemeChanged();
        }
    }

    public interface ThemeChangeListener {
        void onThemeChanged();
    }
}
