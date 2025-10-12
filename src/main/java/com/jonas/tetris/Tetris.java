package com.jonas.tetris;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.io.InputStream;  
import java.io.IOException;  

public class Tetris extends JFrame implements ActionListener, KeyListener {
    public static final int BOARD_WIDTH = 10;
    public static final int BOARD_HEIGHT = 20;
    public static final int TILE_SIZE = 35;
    public static final int PANEL_WIDTH = 250;
    
    private GameBoard gameBoard;
    private InfoPanel infoPanel;
    private Timer gameTimer;
    private boolean gameRunning = false;
    
    public Tetris() {
        setTitle("TETRIS");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        
        getContentPane().setBackground(new Color(15, 15, 25));
        
        // Painel do jogo
        gameBoard = new GameBoard();
        add(gameBoard, BorderLayout.CENTER);
        
        // Painel de informações
        infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.EAST);
        
        // Timer do jogo
        gameTimer = new Timer(500, this);
        
        addKeyListener(this);
        setFocusable(true);
        
        pack();
        setLocationRelativeTo(null);
        
        startGame();
    }
    
    private void startGame() {
        gameBoard.initGame();
        gameRunning = true;
        gameTimer.start();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            gameBoard.update();
            infoPanel.updateInfo(gameBoard.getScore(), gameBoard.getLevel(), gameBoard.getLines(), gameBoard.getNextPiece());
            repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (!gameRunning) return;
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                gameBoard.movePiece(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
                gameBoard.movePiece(1, 0);
                break;
            case KeyEvent.VK_DOWN:
                gameBoard.movePiece(0, 1);
                break;
            case KeyEvent.VK_UP:
                gameBoard.rotatePiece();
                break;
            case KeyEvent.VK_SPACE:
                gameBoard.hardDrop();
                break;
        }
        repaint();
    }
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Tetris().setVisible(true);
        });
    }
}

class Tetromino {
    public enum Type {
        I, O, T, S, Z, J, L
    }
    
    private Type type;
    private int[][] shape;
    private Color color;
    private int x, y;
    
    public Tetromino(Type type) {
        this.type = type;
        this.x = Tetris.BOARD_WIDTH / 2 - 2;
        this.y = 0;
        initShape();
        initColor();
    }
    
    private void initShape() {
        switch (type) {
            case I:
                shape = new int[][]{{1,1,1,1}};
                break;
            case O:
                shape = new int[][]{{1,1},{1,1}};
                break;
            case T:
                shape = new int[][]{{0,1,0},{1,1,1}};
                break;
            case S:
                shape = new int[][]{{0,1,1},{1,1,0}};
                break;
            case Z:
                shape = new int[][]{{1,1,0},{0,1,1}};
                break;
            case J:
                shape = new int[][]{{1,0,0},{1,1,1}};
                break;
            case L:
                shape = new int[][]{{0,0,1},{1,1,1}};
                break;
        }
    }
    
    private void initColor() {
        switch (type) {
            case I: color = new Color(0, 255, 255); break;    // Cyan
            case O: color = new Color(255, 255, 0); break;    // Yellow
            case T: color = new Color(128, 0, 255); break;    // Purple
            case S: color = new Color(0, 255, 0); break;      // Green
            case Z: color = new Color(255, 0, 0); break;      // Red
            case J: color = new Color(0, 0, 255); break;      // Blue
            case L: color = new Color(255, 165, 0); break;    // Orange
        }
    }
    
    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];
        
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                rotated[j][rows - 1 - i] = shape[i][j];
            }
        }
        shape = rotated;
    }
    
    // Getters
    public Type getType() { return type; }
    public int[][] getShape() { return shape; }
    public Color getColor() { return color; }
    public int getX() { return x; }
    public int getY() { return y; }
    public void setX(int x) { this.x = x; }
    public void setY(int y) { this.y = y; }
}

class GameBoard extends JPanel {
    private int[][] board;
    private Color[][] colors;
    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private Random random;
    private int score;
    private int level;
    private int lines;
    
    public GameBoard() {
        setPreferredSize(new Dimension(Tetris.BOARD_WIDTH * Tetris.TILE_SIZE, Tetris.BOARD_HEIGHT * Tetris.TILE_SIZE));
        setBackground(new Color(25, 25, 35));
        setBorder(BorderFactory.createLineBorder(new Color(100, 149, 237), 2));
        board = new int[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH];
        colors = new Color[Tetris.BOARD_HEIGHT][Tetris.BOARD_WIDTH];
        random = new Random();
    }
    
    public void initGame() {
        clearBoard();
        score = 0;
        level = 1;
        lines = 0;
        currentPiece = getRandomPiece();
        nextPiece = getRandomPiece();
    }
    
    private void clearBoard() {
        for (int i = 0; i < Tetris.BOARD_HEIGHT; i++) {
            for (int j = 0; j < Tetris.BOARD_WIDTH; j++) {
                board[i][j] = 0;
                colors[i][j] = null;
            }
        }
    }
    
    private Tetromino getRandomPiece() {
        Tetromino.Type[] types = Tetromino.Type.values();
        return new Tetromino(types[random.nextInt(types.length)]);
    }
    
    public void update() {
        if (canMove(currentPiece, 0, 1)) {
            currentPiece.setY(currentPiece.getY() + 1);
        } else {
            placePiece();
            clearLines();
            currentPiece = nextPiece;
            nextPiece = getRandomPiece();
        }
    }
    
    public void movePiece(int dx, int dy) {
        if (canMove(currentPiece, dx, dy)) {
            currentPiece.setX(currentPiece.getX() + dx);
            currentPiece.setY(currentPiece.getY() + dy);
        }
    }
    
    public void rotatePiece() {
        Tetromino temp = new Tetromino(currentPiece.getType());
        temp.setX(currentPiece.getX());
        temp.setY(currentPiece.getY());
        
        // Copiar rotação atual
        for (int i = 0; i < 4; i++) {
            temp.rotate();
            if (java.util.Arrays.deepEquals(temp.getShape(), currentPiece.getShape())) {
                break;
            }
        }
        temp.rotate();
        
        if (canMove(temp, 0, 0)) {
            currentPiece.rotate();
        }
    }
    
    public void hardDrop() {
        while (canMove(currentPiece, 0, 1)) {
            currentPiece.setY(currentPiece.getY() + 1);
        }
    }
    
    private boolean canMove(Tetromino piece, int dx, int dy) {
        int newX = piece.getX() + dx;
        int newY = piece.getY() + dy;
        int[][] shape = piece.getShape();
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = newX + j;
                    int boardY = newY + i;
                    
                    if (boardX < 0 || boardX >= Tetris.BOARD_WIDTH || 
                        boardY >= Tetris.BOARD_HEIGHT || 
                        (boardY >= 0 && board[boardY][boardX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private void placePiece() {
        int[][] shape = currentPiece.getShape();
        Color color = currentPiece.getColor();
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = currentPiece.getX() + j;
                    int boardY = currentPiece.getY() + i;
                    
                    if (boardY >= 0) {
                        board[boardY][boardX] = 1;
                        colors[boardY][boardX] = color;
                    }
                }
            }
        }
    }
    
    private void clearLines() {
        int linesCleared = 0;
        
        for (int i = Tetris.BOARD_HEIGHT - 1; i >= 0; i--) {
            boolean fullLine = true;
            for (int j = 0; j < Tetris.BOARD_WIDTH; j++) {
                if (board[i][j] == 0) {
                    fullLine = false;
                    break;
                }
            }
            
            if (fullLine) {
                // Mover todas as linhas para baixo
                for (int k = i; k > 0; k--) {
                    for (int j = 0; j < Tetris.BOARD_WIDTH; j++) {
                        board[k][j] = board[k-1][j];
                        colors[k][j] = colors[k-1][j];
                    }
                }
                // Limpar linha superior
                for (int j = 0; j < Tetris.BOARD_WIDTH; j++) {
                    board[0][j] = 0;
                    colors[0][j] = null;
                }
                linesCleared++;
                i++; // Verificar a mesma linha novamente
            }
        }
        
        if (linesCleared > 0) {
            lines += linesCleared;
            score += linesCleared * 100 * level;
            level = (lines / 10) + 1;
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Desenhar grade
        g2d.setColor(new Color(50, 50, 60));
        for (int i = 0; i <= Tetris.BOARD_WIDTH; i++) {
            g2d.drawLine(i * Tetris.TILE_SIZE, 0, i * Tetris.TILE_SIZE, Tetris.BOARD_HEIGHT * Tetris.TILE_SIZE);
        }
        for (int i = 0; i <= Tetris.BOARD_HEIGHT; i++) {
            g2d.drawLine(0, i * Tetris.TILE_SIZE, Tetris.BOARD_WIDTH * Tetris.TILE_SIZE, i * Tetris.TILE_SIZE);
        }
        
        // Desenhar peças fixas
        for (int i = 0; i < Tetris.BOARD_HEIGHT; i++) {
            for (int j = 0; j < Tetris.BOARD_WIDTH; j++) {
                if (board[i][j] != 0 && colors[i][j] != null) {
                    drawBlock(g2d, j * Tetris.TILE_SIZE, i * Tetris.TILE_SIZE, colors[i][j]);
                }
            }
        }
        
        // Desenhar sombra da peça (ghost piece)
        if (currentPiece != null) {
            drawShadowPiece(g2d, currentPiece);
        }
        
        // Desenhar peça atual
        if (currentPiece != null) {
            drawPiece(g2d, currentPiece);
        }
    }
    
    private void drawPiece(Graphics2D g2d, Tetromino piece) {
        int[][] shape = piece.getShape();
        Color color = piece.getColor();
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = (piece.getX() + j) * Tetris.TILE_SIZE;
                    int y = (piece.getY() + i) * Tetris.TILE_SIZE;
                    drawBlock(g2d, x, y, color);
                }
            }
        }
    }
    
    private void drawShadowPiece(Graphics2D g2d, Tetromino piece) {
        // Calcular posição final onde a peça vai cair
        int shadowY = getShadowY(piece);
        
        int[][] shape = piece.getShape();
        Color color = piece.getColor();
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int x = (piece.getX() + j) * Tetris.TILE_SIZE;
                    int y = (shadowY + i) * Tetris.TILE_SIZE;
                    drawShadowBlock(g2d, x, y, color);
                }
            }
        }
    }
    
    private int getShadowY(Tetromino piece) {
        int shadowY = piece.getY();
        
        // Continuar descendo até não poder mais
        while (canMoveToPosition(piece, piece.getX(), shadowY + 1)) {
            shadowY++;
        }
        
        return shadowY;
    }
    
    private boolean canMoveToPosition(Tetromino piece, int newX, int newY) {
        int[][] shape = piece.getShape();
        
        for (int i = 0; i < shape.length; i++) {
            for (int j = 0; j < shape[i].length; j++) {
                if (shape[i][j] != 0) {
                    int boardX = newX + j;
                    int boardY = newY + i;
                    
                    if (boardX < 0 || boardX >= Tetris.BOARD_WIDTH || 
                        boardY >= Tetris.BOARD_HEIGHT || 
                        (boardY >= 0 && board[boardY][boardX] != 0)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private void drawShadowBlock(Graphics2D g2d, int x, int y, Color color) {
        // Desenhar apenas borda da sombra
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 80));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRoundRect(x + 2, y + 2, Tetris.TILE_SIZE - 4, Tetris.TILE_SIZE - 4, 6, 6);
        
        // Preenchimento transparente
        g2d.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
        g2d.fillRoundRect(x + 2, y + 2, Tetris.TILE_SIZE - 4, Tetris.TILE_SIZE - 4, 6, 6);
    }
    
    private void drawBlock(Graphics2D g2d, int x, int y, Color color) {
        // Sombra
        g2d.setColor(new Color(0, 0, 0, 60));
        g2d.fillRoundRect(x + 3, y + 3, Tetris.TILE_SIZE - 6, Tetris.TILE_SIZE - 6, 6, 6);
        
        // Bloco principal
        g2d.setColor(color);
        g2d.fillRoundRect(x + 1, y + 1, Tetris.TILE_SIZE - 2, Tetris.TILE_SIZE - 2, 6, 6);
        
        // Highlight
        g2d.setColor(new Color(255, 255, 255, 80));
        g2d.fillRoundRect(x + 1, y + 1, Tetris.TILE_SIZE - 2, 8, 6, 6);
        
        // Borda
        g2d.setColor(color.darker());
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(x + 1, y + 1, Tetris.TILE_SIZE - 2, Tetris.TILE_SIZE - 2, 6, 6);
    }
    
    public int getScore() { return score; }
    public int getLevel() { return level; }
    public int getLines() { return lines; }
    public Tetromino getNextPiece() { return nextPiece; }
}

// Painel de informações laterais
class InfoPanel extends JPanel {
    private final Color bgColor = new Color(25, 25, 35);
    private final Color textColor = new Color(220, 220, 220);
    private final Color accentColor = new Color(100, 149, 237);
    
    private int score, level, lines;
    private Tetromino next;
    
    public InfoPanel() {
        setPreferredSize(new Dimension(Tetris.PANEL_WIDTH, Tetris.BOARD_HEIGHT * Tetris.TILE_SIZE));
        setBackground(bgColor);
        setBorder(BorderFactory.createLineBorder(accentColor, 2));
    }
    
    public void updateInfo(int score, int level, int lines, Tetromino next) {
        this.score = score;
        this.level = level;
        this.lines = lines;
        this.next = next;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Espaços relativos
        int padding = panelWidth / 35;
        int y = padding;

        // Fonte proporcional ao tamanho do painel
        float fontSize = panelWidth / 10f;
        Font titleFont = FontLoader.loadFont("/font/font.ttf", fontSize);
        g2d.setFont(titleFont);
        g2d.setColor(textColor);

        // Texto "PRÓXIMA PEÇA"
        g2d.drawString("PRÓXIMA PEÇA", padding, y + fontSize);
        y += fontSize + padding;

        // Área da próxima peça (quadrado proporcional)
        int squareSize = panelWidth - 2 * padding; // usa quase toda largura do painel
        g2d.setColor(accentColor);
        g2d.fillRect(padding, y, squareSize, squareSize);
        g2d.setColor(bgColor);
        g2d.fillRect(padding + 2, y + 2, squareSize - 4, squareSize - 4);

        // Desenhar próxima peça dentro do quadrado (com pequeno padding interno)
        drawNextPiece(g2d, next, padding + 2, y + 2, squareSize - 4);

        y += squareSize + padding * 4;
        g2d.setColor(textColor);
        float infoFontSize = panelWidth / 12f;
        g2d.setFont(titleFont.deriveFont(infoFontSize));
        g2d.drawString("Pontuação: " + score, padding, y);
        y += infoFontSize + padding;
        g2d.drawString("Nível: " + level, padding, y);
        y += infoFontSize + padding;
        g2d.drawString("Linhas: " + lines, padding, y);
    }
    
    private void drawSimpleNumber(Graphics2D g2d, int number, int x, int y) {
        g2d.setColor(textColor);
        String numStr = String.valueOf(number);
        int digitWidth = 12;
        
        for (int i = 0; i < numStr.length(); i++) {
            int digit = Character.getNumericValue(numStr.charAt(i));
            drawDigit(g2d, digit, x + i * digitWidth, y);
        }
    }
    
    private void drawDigit(Graphics2D g2d, int digit, int x, int y) {
        // Desenhar dígitos usando retângulos simples (representação de 7 segmentos)
        int w = 8, h = 4;
        
        switch (digit) {
            case 0:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x, y, h, w);           // left top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x, y+w-h, h, w);       // left bottom
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
            case 1:
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                break;
            case 2:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x, y+w-h, h, w);       // left bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
            case 3:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
            case 4:
                g2d.fillRect(x, y, h, w);           // left top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                break;
            case 5:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x, y, h, w);           // left top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
            case 6:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x, y, h, w);           // left top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x, y+w-h, h, w);       // left bottom
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
            case 7:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                break;
            case 8:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x, y, h, w);           // left top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x, y+w-h, h, w);       // left bottom
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
            case 9:
                g2d.fillRect(x, y, w, h);           // top
                g2d.fillRect(x, y, h, w);           // left top
                g2d.fillRect(x+w-h, y, h, w);       // right top
                g2d.fillRect(x, y+w-h/2, w, h);     // middle
                g2d.fillRect(x+w-h, y+w-h, h, w);   // right bottom
                g2d.fillRect(x, y+2*w-h, w, h);     // bottom
                break;
        }
    }
    
    // Desenha a próxima peça centralizada e escalada dentro do retângulo (x,y,size,size)
    private void drawNextPiece(Graphics2D g2d, Tetromino piece, int x, int y, int size) {
        if (piece == null) return;

        int[][] shape = piece.getShape();
        Color color = piece.getColor();

        int rows = shape.length;
        int cols = shape[0].length;

        // Calcular padding interno e tamanho do bloco
        int innerPadding = Math.max(4, size / 12);
        int available = size - innerPadding * 2;
        int blockSize = Math.max(1, Math.min(available / Math.max(1, cols), available / Math.max(1, rows)));

        // Centralizar
        int pieceWidth = blockSize * cols;
        int pieceHeight = blockSize * rows;
        int startX = x + (size - pieceWidth) / 2;
        int startY = y + (size - pieceHeight) / 2;

        // Desenhar cada bloco com sombra, highlight e borda (versão simplificada)
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (shape[i][j] != 0) {
                    int bx = startX + j * blockSize;
                    int by = startY + i * blockSize;

                    // Sombra
                    g2d.setColor(new Color(0, 0, 0, 80));
                    g2d.fillRect(bx + Math.max(1, blockSize/8), by + Math.max(1, blockSize/8), blockSize - Math.max(2, blockSize/6), blockSize - Math.max(2, blockSize/6));

                    // Bloco principal
                    g2d.setColor(color);
                    g2d.fillRect(bx, by, blockSize - 1, blockSize - 1);

                    // Highlight
                    g2d.setColor(new Color(255, 255, 255, 80));
                    g2d.fillRect(bx, by, blockSize - 1, Math.max(2, blockSize/4));

                    // Borda
                    g2d.setColor(color.darker());
                    g2d.setStroke(new BasicStroke(1));
                    g2d.drawRect(bx, by, blockSize - 1, blockSize - 1);
                }
            }
        }
    }
}

class FontLoader  {
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