package com.jonas.tetris.util;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utilitário para carregar fontes customizadas do classpath.
 */
public class FontLoader {
    /**
     * Carrega uma fonte TrueType do classpath.
     *
     * @param path caminho da fonte no classpath (ex: "/font/font.ttf")
     * @param size tamanho da fonte em pontos
     * @return Font carregada ou Arial como fallback
     */
    public static Font loadFont(String path, float size) {
        try (InputStream is = FontLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                System.err.println("Fonte não encontrada: " + path);
                return new Font("Arial", Font.PLAIN, (int) size);
            }
            Font font = Font.createFont(Font.TRUETYPE_FONT, is);
            return font.deriveFont(size);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            return new Font("Arial", Font.PLAIN, (int) size);
        }
    }
}
