import java.awt.*;
import javax.swing.*;
import java.util.HashSet;
import java.awt.event.*;
import java.util.Random;

public class PacMan extends JPanel implements ActionListener, KeyListener{




    class Block{
        int x;
        int y;
        int width;
        int height;
        Image image;                                            //Declaring variables and generating a constructor

        int startX;
        int startY;

        char direction = 'U';    // 'U' 'D' 'L' 'R'
        int velocityX = 0;
        int velocityY = 0;

        public Block(int x, int y, int width, int height, Image image) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.image = image;
            this.startX = x;
            this.startY = y;
        }

        //Method for updating the direction
        void updateDirection(char direction){
            char prevDirection = this.direction;
            this.direction = direction;
            updateVelocity();

            this.x += this.velocityX;
            this.y += this.velocityY;

            //Check for collisions with walls
            for (Block wall : walls){
                if (collision(this, wall)){
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity(){
            if(this.direction == 'U'){              //Check if we are going upwards. x is zero and y has to be
                this.velocityX = 0;                 //negative because we are going towards zero on the y-axis
                this.velocityY = -tileSize/4;
            } else if (this.direction == 'D') {     //Check for down, x is still zero, but y is
                this.velocityX = 0;                //positive since we are moving away from zero on the y-axis
                this.velocityY = tileSize/4;
            } else if (this.direction == 'L') {    //Check for left, now x is negative and y is zero
                this.velocityX = -tileSize/4;
                this.velocityY = 0;
            } else if (this.direction == 'R') {
                velocityX = tileSize/4;
                velocityY = 0;
            }
        }

        //Reset to starting coordinates
        void reset(){
            this.x = this.startX;
            this.y = this.startY;
        }

    }

    private int rowCount = 21;
    private int columnCount = 19;
    private int tileSize = 32;
    private int boardWidth = columnCount * tileSize;
    private int boardHeight = rowCount * tileSize;

    private Image wallImage;
    private Image blueGhostImage;
    private Image orangeGhostImage;
    private Image pinkGhostImage;
    private Image redGhostImage;

    private Image pacmanUpImage;
    private Image pacmanDownImage;
    private Image pacmanLeftImage;
    private Image pacmanRightImage;

    private Image cherryImage;


    //X = wall, O = skip, P = pac man, ' ' = food
    //Ghosts: b = blue, o = orange, p = pink, r = red
    private String[] tileMap = {
            "XXXXXXXXXXXXXXXXXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X                 X",
            "X XX X XXXXX X XX X",
            "X    X       X    X",
            "XXXX XXXX XXXX XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXrXX X XXXX",
            "O       bpo       O",
            "XXXX X XXXXX X XXXX",
            "OOOX X       X XOOO",
            "XXXX X XXXXX X XXXX",
            "X        X        X",
            "X XX XXX X XXX XX X",
            "X  X     P     X  X",
            "XX X X XXXXX X X XX",
            "X    X   X   X    X",
            "X XXXXXX X XXXXXX X",
            "X                 X",
            "XXXXXXXXXXXXXXXXXXX"
    };

    HashSet<Block> walls;
    HashSet<Block> foods;                  //Creating hashsets to store the images
    HashSet<Block> ghosts;
    Block pacman;                          //No hashset for pacman because there is only one
    Block cherry;
    Timer gameLoop;

    char[] directions = {'U', 'D', 'L', 'R'};  //up, down, left, right
    Random random = new Random();

    int score = 0;
    int lives = 3;
    boolean gameOver = false;

    private char requestedDirection = ' ';  //Direction requested by key press


    //Constructor to initialize the game, load images and start the game loop
    PacMan(){
        setPreferredSize(new Dimension(boardWidth, boardHeight));     //Setting dimensions to be the width and height
        setBackground(Color.BLACK);                                   //and the background to be black
        addKeyListener(this);   //KeyListener detects the user input
        setFocusable(true);


        //Load the images:
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();

        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();


        cherryImage = new ImageIcon(getClass().getResource("./cherry.png")).getImage();

        //Load the game map with all objects
        loadMap();

        //Set up random movement for the ghosts
        for (Block ghost : ghosts){
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }


        gameLoop = new Timer(50,this);    //Every 50ms the screen will be "repainted" (equal to 20fps)
        gameLoop.start();
    }

    public void loadMap(){
        walls = new HashSet<Block>();
        foods = new HashSet<Block>();            //Initialize hashsets for storage
        ghosts = new HashSet<Block>();

        for (int r = 0; r < rowCount; r++){
            for (int c = 0; c < columnCount; c++){
                String row = tileMap[r];             //Get the current row
                char tileMapChar = row.charAt(c);    //Get the current character

                int x = c*tileSize;         //Get the x position
                int y = r*tileSize;         //Get the y position

                if(tileMapChar == 'X'){  //Block wall
                    Block wall = new Block(x, y, tileSize, tileSize, wallImage);
                    walls.add(wall);
                } else if (tileMapChar == 'b') {  //Blue ghost
                    Block ghost = new Block(x, y, tileSize, tileSize, blueGhostImage);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'o') {  //Orange ghost
                    Block ghost = new Block(x, y, tileSize, tileSize, orangeGhostImage);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'p') {  //Pink ghost
                    Block ghost = new Block(x, y, tileSize, tileSize, pinkGhostImage);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'r') {  //Red ghost
                    Block ghost = new Block(x, y, tileSize, tileSize, redGhostImage);
                    ghosts.add(ghost);
                } else if (tileMapChar == 'P') {   //Pacman
                    pacman = new Block(x, y, tileSize, tileSize, pacmanRightImage);
                } else if (tileMapChar == ' ') {   //Food
                    Block food = new Block(x + 14, y + 14, 4 , 4, null);
                    foods.add(food);
                }
            }
        }
        //Placing the cherry in the middle
        cherry = new Block(boardWidth/2 - tileSize/2, boardHeight/2 - tileSize/2 + tileSize, tileSize, tileSize, cherryImage);
    }

    public void paintComponent(Graphics g){
        super.paintComponent(g);
        draw(g);
    }

    //Draw all the images constructing the map:
    public void draw(Graphics g){
        g.drawImage(pacman.image ,pacman.x, pacman.y, pacman.width, pacman.height, null);

        for(Block ghost: ghosts){
            g.drawImage(ghost.image, ghost.x, ghost.y, ghost.width, ghost.height, null);
        }

        for(Block wall: walls){
            g.drawImage(wall.image, wall.x, wall.y, wall.width, wall.height, null);
        }

        g.setColor(Color.WHITE);
        for(Block food: foods){
            g.fillRect(food.x, food.y, food.width, food.height);
        }

        if (cherry != null){
            g.drawImage(cherry.image, cherry.x, cherry.y, cherry.width, cherry.height,null);
        }

        //Score
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        if(gameOver){
            g.drawString("Game Over: " + String.valueOf(score), tileSize/2, tileSize/2);
        } else {
            g.drawString("x" + String.valueOf(lives) + " Score: " + String.valueOf(score), tileSize/2, tileSize/2);
        }
    }

    //Handling movement
    public void move() {
        if (pacman.velocityX == 0 && pacman.velocityY == 0) {
            if (requestedDirection != ' ') {                    //Checking if the movement is set to 0, then updating to the requested direction
                pacman.updateDirection(requestedDirection);
            }
        }

        pacman.x += pacman.velocityX;   //Basically with this we take a step forward
        pacman.y += pacman.velocityY;   //and then with the for loop we check if there is a collision,
                                        //if there is we reverse the step forward that we took
        //Check wall collisions
        boolean collisionWithWall = false;
        for (Block wall : walls){
            if(collision(pacman, wall)){
                collisionWithWall = true;
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }
        }

        if (!collisionWithWall){
            updatePacManImage();
        }

        checkTeleport(pacman);   //Handle if pacman is teleporting in the middle row

        //Check ghost collisions
        for (Block ghost : ghosts){
            if (collision(ghost, pacman)){
                lives -= 1;       //Pacman loses a life when colliding with a ghost
                if (lives == 0){
                    gameOver = true;       //The game ends where there are no more lives
                    return;
                }
                resetPositions();       //Everything is reset after loss
            }

            //Handling the ghosts getting stuck in the middle by forcing them to go up. They get stuck since they can't teleport there like PacMan
            if (ghost.y == tileSize * 9 && ghost.direction != 'U' && ghost.direction != 'D'){
                ghost.updateDirection('U');
            }
            ghost.x += ghost.velocityX;            //Similarly for the ghosts, the program moves a step forward,
            ghost.y += ghost.velocityY;            //if it collides with a wall, it will change direction
            for (Block wall : walls){
                //Check if ghosts collide with wall or try to teleport in the middle row
                if (collision(ghost, wall) || ghost.x <= 0 || ghost.x + ghost.width >= boardWidth){
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    char newDirection = directions[random.nextInt(4)];
                    ghost.updateDirection(newDirection);
                }
            }
        }

        //Check food collision
        Block foodEaten = null;
        for (Block food : foods){
            if (collision(pacman, food)){
                foodEaten = food;
                score += 10;        //For every pellet eaten the score increases by 10
            }
        }
        foods.remove(foodEaten);    //And the pellets of course get removed when they are eaten

        //Check cherry collision
        if (cherry != null && collision(pacman, cherry)){
            score += 100;             //The cherry gives 100 points when eaten after which it gets removed until the next round
            cherry = null;
        }

        if (foods.isEmpty()){        //If PacMan eats all the pellets, the map is loaded once again and the positions are reset
            loadMap();
            resetPositions();
        }

    }

    //Handle the teleportation in the middle
    public void checkTeleport(Block block){
        int teleportRowY = 9 * tileSize;   //Row 9 (0-indexed), so y = 9*tileSize
        int teleportRange = tileSize;    //Allow some tolerance

        if (block.y >= teleportRowY - teleportRange && block.y <= teleportRowY + teleportRange){
            //If moving left off the screen
            if (block.x + block.width < 0){
                block.x = boardWidth;
            }
            //If moving right off the screen
            else if (block.x > boardWidth) {
                block.x = -block.width;
            }
        }
    }

    public boolean collision(Block a, Block b){    //Function to detect collision with wall or ghost
        return  a.x < b.x + b.width &&             //using a collision detection formula
                a.x + a.width > b.x &&
                a.y < b.y + b.height &&
                a.y + a.height > b.y;
    }

    //Reseting all positions
    public void resetPositions(){
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts){
            ghost.reset();
            char newDirection = directions[random.nextInt(4)];
            ghost.updateDirection(newDirection);
        }
    }

    //Main game loop
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver){
            gameLoop.stop();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {  //Not needed because it is only for letters, arrows will not be accepted
    }

    @Override
    public void keyPressed(KeyEvent e) {  //Good for any key, but it also allows the user to hold on to a key which we do not need
        if (gameOver){
            loadMap();
            resetPositions();
            lives = 3;
            score = 0;
            gameOver = false;
            gameLoop.start();
        }
        //System.out.println("KeyEvent: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_UP){
            pacman.updateDirection('U');
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {        //Updating the direction based on the user input key
            pacman.updateDirection('D');
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            pacman.updateDirection('L');
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            pacman.updateDirection('R');
        }


    }

    public void updatePacManImage(){
        if (pacman.direction == 'U'){
            pacman.image = pacmanUpImage;
        } else if (pacman.direction == 'D') {
            pacman.image = pacmanDownImage;
        } else if (pacman.direction == 'L') {
            pacman.image = pacmanLeftImage;
        } else if (pacman.direction == 'R') {
            pacman.image = pacmanRightImage;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
