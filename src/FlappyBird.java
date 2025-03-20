import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // Images
    Image backgroundImage;     Image mybackgroundImage;
    Image birdImage; Image sukuna;
    Image topPipeImage;
    Image bottomPipeImage;

    // Bird
    int birdX = boardWidth/8;
    int birdY = boardHeight/2;
    int birdWidth = 34; 
    int birdHeight = 24;

    
    class Bird {
        
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img; //Image field
        
        Bird(Image img) {
            this.img = img;
        }
        
    }
    
    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;
    
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        
        boolean passed = false;
        
        Pipe(Image img) {
            this.img = img;
        }
        
    }
    
    // Game Logic
    Bird bird;
    double velocityX = -2;
    double velocityY = 0;
    double gravity = 0.2;
    
    ArrayList<Pipe> pipes;
    Random random = new Random();
    
    //Pipes
    
    

    Timer gameloop;
    Timer placePipesTimer;

    boolean gameOver = false;
    double score = 0;
    
    FlappyBird() {     

        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true);
        addKeyListener(this);

        // Load images
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        mybackgroundImage = new ImageIcon(getClass().getResource("./mybg.jpg")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeImage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();
        sukuna = new ImageIcon(getClass().getResource("./sukuna.jpeg")).getImage();

        // bird
        bird = new Bird(sukuna);
        pipes = new ArrayList<Pipe>();
        
        
        //Place pipes
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // Game timer
        //Should be 16.6 But the constructor is not able to receive doubles for this
        // So i used 17
        gameloop = new Timer(100/6, this);
        gameloop.start();
    }
    
    public void paintComponent(Graphics g) {
        
        super.paintComponent(g);
        draw(g);
        
    }
    
    public void draw(Graphics g) {
        
        // Background
        g.drawImage(mybackgroundImage, 0, 0, this.boardWidth, this.   boardHeight, null);
        
        // Bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        
        //Pipes
        for ( int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 32));
        if (gameOver) {
            g.drawString("Game Over " + String.valueOf(( int ) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }

    }

    boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&   
               a.x + a.width > b.x &&   
               a.y < b.y + b.height &&  
               a.y + a.height > b.y;
    }

    public void move() {
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        //Pipes
        for ( int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }

        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    public void placePipes() {
        int randomPipeY = ( int ) ( pipeY - pipeHeight/4 - Math.random() * pipeHeight / 2 );
        int openingSpace = boardHeight / 4;



        Pipe topPipe = new Pipe(topPipeImage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y  + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameloop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY -= 6;
        }
        if (gameOver) {
            //restart game by resetting conditions
            bird.y = birdY;
            velocityY = 0;
            pipes.clear();
            gameOver = false;
            score = 0;
            gameloop.start();
            placePipesTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent key) {
    }

    @Override
    public void keyTyped(KeyEvent key) {
    }
    
}
