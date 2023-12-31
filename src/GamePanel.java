import java.awt.*;
import java.io.IOException;
import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class GamePanel extends JPanel implements Runnable{

    final int originalTileSize = 16; // 16x16 tile
    final int scale = 3;
    public final int tileSize = originalTileSize * scale; // 48x48 tile
    public final int maxScreenCol = 32;
    public final int maxScreenRow = 15;
    final int screenWidth = tileSize * maxScreenCol;
    final int screenHeight = tileSize * maxScreenRow;
    int FPS = 60;
    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler(this);
    Pacman pacman = new Pacman(this, keyHandler);
    public TileManager tileManager = new TileManager(this);
    public CollisionManager collisionManager = new CollisionManager(this);

    public Ghost ghost1 = new Ghost(this, "blue");
    public Ghost ghost2 = new Ghost(this, "green");
    public Ghost ghost3 = new Ghost(this, "brown");
    public Ghost ghost4 = new Ghost(this, "yellow");
    int maxScore = tileManager.numberOfCoin * 10 + tileManager.numberOfBigCoin * 100;
    boolean isPaused = false;

    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);
        this.setDoubleBuffered(true);
        this.setFocusable(true);
        this.addKeyListener(keyHandler);
        tileManager.startDoorTimer();
        this.setFocusable(true);
        this.requestFocusInWindow();
    }

    public void drawPauseMessage(Graphics2D g2) {
        if (isPaused) {
            g2.setColor(Color.YELLOW);
            g2.setFont(new Font("Consolas", Font.BOLD, 50));
            String pauseMessage = "Game Paused";
            int messageWidth = g2.getFontMetrics().stringWidth(pauseMessage);
            int x = (getWidth() - messageWidth) / 2 - 10;
            int y = getHeight() / 2  + 40;
            g2.drawString(pauseMessage, x, y);

            int padding = 20;
            int borderWidth = messageWidth + padding * 2;
            int borderHeight = g2.getFontMetrics().getHeight() + padding * 2;
            int borderX = x - padding;
            int borderY = y - g2.getFontMetrics().getAscent() - padding;


            g2.setColor(Color.YELLOW);

            g2.drawRect(borderX, borderY, borderWidth, borderHeight);
        }
    }


    public void startGameThread(){
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void updateScoreDisplay(int newScore) {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            frame.setTitle("Score: " + newScore);
        }
    }

    @Override
    public void run() {
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;
        while(gameThread != null){
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if(delta >= 1){
                update();
                repaint();
                delta--;
                drawCount++;
            }
            if(timer >= 1000000000){
                // System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }
    public void update(){
        pacman.update();
        ghost1.update(pacman.x, pacman.y);
        ghost2.update(pacman.x, pacman.y);
        ghost3.update(pacman.x, pacman.y);
        ghost4.update(pacman.x, pacman.y);
        if(pacman.score == maxScore){
            System.out.println("WIN");
            endGame();
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        tileManager.draw(g2);
        ghost1.draw(g2);
        ghost2.draw(g2);
        ghost3.draw(g2);
        ghost4.draw(g2);
        pacman.draw(g2);
        if(isPaused){
            pacman.setSpeed(0);
            ghost1.setSpeed(0);
            ghost2.setSpeed(0);
            ghost3.setSpeed(0);
            ghost4.setSpeed(0);
            drawPauseMessage(g2);
        }
        g2.dispose();

    }

    public void setPlayerNameForPacman(String name) {
        pacman.setPlayerName(name);
    }

    public void endGame() {
        System.out.println("Game Over");
        gameThread = null;

        String fileName = "leaderboard.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(pacman.getPlayerName() + ": " + pacman.getPlayerScore() + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            new HomePage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JFrame topLevelFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
        topLevelFrame.dispose();

    }
}
