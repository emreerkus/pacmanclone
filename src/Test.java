import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Test extends Entity implements KeyListener{
    // For sequential purposes, following class will be tested sequentially
    // HomePage -> LeaderboardPage -> TileManager -> KeyHandler -> Ghost -> Pacman -> GamePanel -> CollisionManager
    // Testing for PacmanClone will be done in this class

    GamePanel gp = new GamePanel();
    TileManager tm = new TileManager(gp);
    public Tile[] tileTest;
    int numberOfCoin = 0;
    public int[][] mapTileNumber;
    String[] directions = {"right", "left", "down", "up"};
    String color;
    public boolean upPressed, downPressed, leftPressed, rightPressed;
    KeyHandler kh;
    public int scoreTest;
    String requestDirection;
    Pacman p = new Pacman(gp,kh);
    BufferedImage pacman_img;



    Ghost testGhost = new Ghost(gp, color);

    // 1) ---------------------------------------HomePage Class Testing---------------------------------------
    // 2) ---------------------------------------LeaderboardPage Testing---------------------------------------


    // 3) ---------------------------------------TileManager Testing---------------------------------------
    public void loadMapTest(String myPathTest){
        try{
            InputStream ts = getClass().getResourceAsStream(myPathTest);
            assert ts != null;
            BufferedReader br = new BufferedReader(new InputStreamReader(ts));

            int col = 0;
            int row = 0;

            while(col< gp.maxScreenCol && row < gp.maxScreenRow){
                String lineTest = br.readLine();
                while(col< gp.maxScreenCol){
                    String[] numbers = lineTest.split(" ");
                    int number = Integer.parseInt(numbers[col]);
                    if(number == 2){
                        numberOfCoin += 1;
                    }
                    mapTileNumber[col][row] = number;
                    col++;
                }
                if(col == gp.maxScreenCol){
                    col = 0;
                    row++;
                }
            }
            br.close();;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void getTileImageTest(){
        try{
            tileTest[0] = new Tile();
            tileTest[0].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/freeway.png")));


            tileTest[1] = new Tile();
            tileTest[1].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/obstacle.png")));
            tileTest[1].collision = true;

            tileTest[2] = new Tile();
            tileTest[2].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/coin.png")));
            tileTest[2].collision = true;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void drawTest(Graphics2D g2){
        int col = 0;
        int row = 0;
        int x = 0;
        int y = 0;

        while(col < gp.maxScreenCol && row < gp.maxScreenRow){
            int tileNum = mapTileNumber[col][row];
            g2.drawImage(tileTest[tileNum].image, x, y, gp.tileSize, gp.tileSize, null);
            col++;
            x += gp.tileSize;
            if(col == gp.maxScreenCol){
                col = 0;
                x = 0;
                row++;
                y += gp.tileSize;
            }
        }
    }

    // 4) ---------------------------------------KeyHandler Testing---------------------------------------
    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int codeTest = e.getKeyCode();
        // Different keyboard component will be tested.
        if(codeTest == KeyEvent.VK_T){
            upPressed = true;
        }
        else if(codeTest == KeyEvent.VK_G){
            downPressed = true;
        }
        else if(codeTest == KeyEvent.VK_F){
            leftPressed = true;
        }
        else if(codeTest == KeyEvent.VK_H){
            rightPressed = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        int codeTest = e.getKeyCode();

        if(codeTest == KeyEvent.VK_T){
            upPressed = false;
        }
        else if(codeTest == KeyEvent.VK_G){
            downPressed = false;
        }
        else if(codeTest == KeyEvent.VK_F){
            leftPressed = false;
        }
        else if(codeTest == KeyEvent.VK_H){
            rightPressed = false;
        }

    }
    // 5) ---------------------------------------Ghost Class Testing---------------------------------------

    public void setDefaultPositionTest(int x,int y){
        this.x = x;
        this.y = y;
        speed = 1;
        Random random = new Random();
        int randomIndexNew = random.nextInt(directions.length);
        direction = directions[randomIndexNew];
    }

    public void getGhostImage(){
        try{
            if(color.equals("blue")){
                ghost_img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/blue_ghost.png")));

            }else if(color.equals("green")){
                ghost_img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/green_ghost.png")));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void getTriggeredGhostImage(){
        try{
            if(color.equals("blue")){
                ghost_img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/blue_ghost.png")));
            }else if(color.equals("green")) {
                ghost_img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/green_ghost.png")));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public boolean atCrossTest(){
        int count = 0;
        boolean right = false;
        boolean left = false;
        boolean up = false;
        boolean down = false;

        if (gp.collisionManager.isOkToMove(x, y, "right")) {
            right = true;
            count++;
        }
        if (gp.collisionManager.isOkToMove(x, y, "left")) {
            left = true;
            count++;
        }
        if (gp.collisionManager.isOkToMove(x, y, "down")) {
            down = true;
            count++;
        }
        if (gp.collisionManager.isOkToMove(x, y, "up")) {
            up = true;
            count++;
        }

        if(count > 2){
            return true;
        }else if(count < 2){
            return false;
        }else{
            if(direction.equals("right")){
                return (down || up) && left;
            }else if(direction.equals("left")){
                return (down || up) && right;
            }else if(direction.equals("down")){
                return (right || left) && up;
            }else if(direction.equals("up")){
                return (right || left) && down;
            }
            return false;
        }
    }

    public void moveRandomTest(){

        getGhostImage();

        Random random = new Random();
        int randomIndex = random.nextInt(directions.length);
        String requestDirection = directions[randomIndex];

        if(!atCrossTest()){
            if(!gp.collisionManager.isOkToMove(x, y, direction)){
                randomIndex = random.nextInt(directions.length);
                direction = directions[randomIndex];
            }
            if (direction.equals("right") && gp.collisionManager.isOkToMove(x, y, direction)) {
                x += speed;
            } else if (direction.equals("left") && gp.collisionManager.isOkToMove(x, y, direction)) {
                x -= speed;
            }

            if (direction.equals("down") && gp.collisionManager.isOkToMove(x, y, direction)) {
                y += speed;
            } else if (direction.equals("up") && gp.collisionManager.isOkToMove(x, y, direction)) {
                y -= speed;
            }
        }else{
            if(!gp.collisionManager.isOkToMove(x, y, requestDirection)){
                System.out.println(requestDirection);
                randomIndex = random.nextInt(directions.length);
                requestDirection = directions[randomIndex];
            }
            direction = requestDirection;
            if(direction.equals("right") && gp.collisionManager.isOkToMove(x, y, direction)){
                x += speed;
            }else if(direction.equals("left") && gp.collisionManager.isOkToMove(x, y, direction)){
                x -= speed;
            }else if(direction.equals("down") && gp.collisionManager.isOkToMove(x, y, direction)){
                y += speed;
            }else if(direction.equals("up") && gp.collisionManager.isOkToMove(x, y, direction)){
                y -= speed;
            }
        }
    }

    public void followPlayerTest(int player_a, int player_b){

        getTriggeredGhostImage();

        if (x < player_a && gp.collisionManager.isOkToMove(x, y, "right")) {
            x += speed;
            direction = "right";
        }else if (x > player_a && gp.collisionManager.isOkToMove(x, y, "left")) {
            x -= speed;
            direction = "left";
        }

        if (y < player_b && gp.collisionManager.isOkToMove(x, y, "down")) {
            y += speed;
            direction = "down";
        }else if (y > player_b && gp.collisionManager.isOkToMove(x, y, "up")) {
            y -= speed;
            direction = "up";
        }
    }
    public void updateTest(int player_a, int player_b){

        double distance = Math.sqrt(Math.pow((x - player_a), 2) + Math.pow((y - player_b), 2));

        if(distance < 200){
            followPlayerTest(player_a, player_b);
        }else{
            moveRandomTest();
        }
    }
    public void drawGhost(Graphics2D g2){
        g2.drawImage(ghost_img, x, y, gp.tileSize, gp.tileSize, null);
    }


    // 6) ---------------------------------------Pacman Class Testing---------------------------------------

    public void setDefaultPositionTest(){
        //This will be tested with different axes to provide different scenario
        x = 200;
        y = 36;
        speed = 2;
        size = 36;
        direction = "left";
        requestDirection = "left";
        scoreTest = 0;
    }

    public void getPlayerImageTest(){
        try {
            //pacman_img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/pacman.png")));
            pacman_img = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/pacman_right.png")));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void movePacman() {
        // Directions will stay same for every possibility.
        switch (requestDirection) {
            case "up":
                if (gp.collisionManager.isOkToMove(x, y, requestDirection)) {
                    direction = "up";
                }
                break;
            case "down":
                if (gp.collisionManager.isOkToMove(x, y, requestDirection)) {
                    direction = "down";
                }
                break;
            case "right":
                if (gp.collisionManager.isOkToMove(x, y, requestDirection)) {
                    direction = "right";
                }
                break;
            case "left":
                if (gp.collisionManager.isOkToMove(x, y, requestDirection)) {
                    direction = "left";
                }
                break;
            default:
                break;
        }
        switch(direction){
            case "up":
                if(gp.collisionManager.isOkToMove(x, y, direction)){
                    y -= speed;
                }
                break;
            case "down":
                if(gp.collisionManager.isOkToMove(x, y, direction)){
                    y += speed;
                }
                break;
            case "right":
                if(gp.collisionManager.isOkToMove(x, y, direction)){
                    x += speed;
                }
                break;
            case "left":
                if(gp.collisionManager.isOkToMove(x, y, direction)){
                    x -= speed;
                }
                break;
            default:
                break;
        }
    }

    public void updatePacman(){

        if(gp.collisionManager.collisionWithGhost(x, y, gp.ghost2.x, gp.ghost2.y) ||
                gp.collisionManager.collisionWithGhost(x, y, gp.ghost1.x, gp.ghost1.y)){
            gp.endGame();
        }

        if(kh.upPressed){
            requestDirection = "up";
        }
        else if(kh.downPressed){
            requestDirection = "down";
        }
        else if(kh.rightPressed){
            requestDirection = "right";
        }
        else if(kh.leftPressed){
            requestDirection = "left";
        }

        movePacman();

        if(gp.collisionManager.canCollectedCoin(x, y, direction)){
            scoreTest += 10;
            int col = x / gp.tileSize;
            int row = y / gp.tileSize;
            if(direction.equals("right")){
                col = (x+36) / gp.tileSize;
            }else if(direction.equals("down")){
                row = (y+36) / gp.tileSize;
            }
            if(gp.tileManager.mapTileNumber[col][row] == 2){
                gp.tileManager.mapTileNumber[col][row] = 0;
                System.out.println("score: " + scoreTest);
            }
        }
    }

    public void drawPacman(Graphics2D g2){
        BufferedImage image = null;

        g2.drawImage(pacman_img, x, y, gp.tileSize, gp.tileSize, null);

    }
    //GamePanel Class Testing
    Thread testGamePanelInitialization;
    public void testGamePanelInitialization() {
            GamePanel gamePanel = new GamePanel();
            System.out.println("Expected width: " + (gamePanel.tileSize * gamePanel.maxScreenCol) + ", Actual width: " + gamePanel.getPreferredSize().width);
            System.out.println("Expected height: " + (gamePanel.tileSize * gamePanel.maxScreenRow) + ", Actual height: " + gamePanel.getPreferredSize().height);
            System.out.println("Background color is black: " + (Color.black.equals(gamePanel.getBackground())));//this simply makes the background color black
         }

    Thread testingGameThread;
    public void startGameThreadTest(){
        testingGameThread = new Thread(gp);
        testingGameThread.start();

    }

    Thread CollisionManagerTest;
    public void CollisionManagerTest() {
        GamePanel gamePanel = new GamePanel();
        CollisionManager collisionManager= new CollisionManager(gamePanel);
        boolean collisionWithWall = collisionManager.isOkToMove(0, 0, "right");
        System.out.println("Collision with wall (expected true): " + collisionWithWall);
        boolean collisionWithGhost = collisionManager.collisionWithGhost(32, 32, 64, 32);
        System.out.println("Collision with ghost (expected true): " + collisionWithGhost);
        boolean canCollectCoin = collisionManager.canCollectedCoin(32, 32, "down");
        System.out.println("Can collect coin (expected true): " + canCollectCoin);
    }





    // Unit Testing approach will be applied by hand.
    public static void main(String[] args){
        int x = 200;
        int y = 40;
        JFrame frame = new JFrame();
        int id = KeyEvent.KEY_PRESSED;
        long when = System.currentTimeMillis();
        int modifiers = 0;
        int keyCode = KeyEvent.VK_T;
        char keyChar = 'T';
        KeyEvent e = new KeyEvent(frame, id, when, modifiers, keyCode, keyChar);
//        int keycode = e.getKeyCode();



        String color = "blue";
        GamePanel gp = new GamePanel();
        TileManager tm = new TileManager(gp);
        KeyHandler kh = new KeyHandler();
        Pacman p = new Pacman(gp,kh);
        //Ghost testGhost = new Ghost(gp,x,y,color);
        Ghost testGhost = new Ghost(gp, color);
        tm.getTileImage();
        kh.keyPressed(e);
    }

}

