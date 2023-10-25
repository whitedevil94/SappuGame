import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Point;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class SappuGame extends JPanel implements ActionListener {
    private ArrayList<Point> snake;
    private Point food;
    private int direction;
    private boolean isEating;
    private boolean isGameOver;
    private int score;
    private int highScore;
    private static final int SCALE = 20; // Increased size
    private static final int WIDTH = 30;
    private static final int HEIGHT = 30;
    private static final int DELAY = 100;
    private Timer timer;

    private JButton restartButton; // Restart button

    public SappuGame() {
        snake = new ArrayList<>();
        snake.add(new Point(5, 5));
        food = new Point(15, 15);
        direction = KeyEvent.VK_RIGHT;
        isEating = false;
        isGameOver = false;
        score = 0;
        highScore = readHighScore();

        timer = new Timer(DELAY, this);
        timer.start();

        restartButton = new JButton("Restart");
        restartButton.setEnabled(false); // Initially, disable the restart button
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame(); // Restart the game when the button is clicked
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_LEFT) && (direction != KeyEvent.VK_RIGHT)) {
                    direction = KeyEvent.VK_LEFT;
                } else if ((key == KeyEvent.VK_RIGHT) && (direction != KeyEvent.VK_LEFT)) {
                    direction = KeyEvent.VK_RIGHT;
                } else if ((key == KeyEvent.VK_UP) && (direction != KeyEvent.VK_DOWN)) {
                    direction = KeyEvent.VK_UP;
                } else if ((key == KeyEvent.VK_DOWN) && (direction != KeyEvent.VK_UP)) {
                    direction = KeyEvent.VK_DOWN;
                }
            }
        });
        setFocusable(true);

        // Create a restart button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(restartButton);
        add(buttonPanel);
    }

    public void actionPerformed(ActionEvent e) {
        if (!isGameOver) {
            move();
            checkCollision();
            repaint();
        }
    }

    private void move() {
        Point head = snake.get(0);
        Point newHead;

        switch (direction) {
            case KeyEvent.VK_LEFT:
                newHead = new Point(head.x - 1, head.y);
                break;
            case KeyEvent.VK_RIGHT:
                newHead = new Point(head.x + 1, head.y);
                break;
            case KeyEvent.VK_UP:
                newHead = new Point(head.x, head.y - 1);
                break;
            case KeyEvent.VK_DOWN:
                newHead = new Point(head.x, head.y + 1);
                break;
            default:
                newHead = head;
        }

        snake.add(0, newHead);

        if (newHead.equals(food)) {
            isEating = true;
            generateFood();
            score++;
        } else {
            snake.remove(snake.size() - 1);
            isEating = false;
        }
    }

    private void generateFood() {
        Random rand = new Random();
        int randomX, randomY;
        do {
            randomX = rand.nextInt(WIDTH);
            randomY = rand.nextInt(HEIGHT);
        } while (snake.contains(new Point(randomX, randomY)));
        food.setLocation(randomX, randomY);
    }

    private void checkCollision() {
        Point head = snake.get(0);

        if (head.x < 0 || head.x >= WIDTH || head.y < 0 || head.y >= HEIGHT) {
            isGameOver = true;
            if (score > highScore) {
                highScore = score;
                writeHighScore();
            }
            timer.stop();
            restartButton.setEnabled(true); // Enable the restart button when the game ends
        }

        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                isGameOver = true;
                if (score > highScore) {
                    highScore = score;
                    writeHighScore();
                }
                timer.stop();
                restartButton.setEnabled(true); // Enable the restart button when the game ends
                break;
            }
        }
    }

    private int readHighScore() {
        try {
            File highScoreFile = new File("highscore.txt");
            if (highScoreFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(highScoreFile));
                return Integer.parseInt(reader.readLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void writeHighScore() {
        try {
            File highScoreFile = new File("highscore.txt");
            if (!highScoreFile.exists()) {
                highScoreFile.createNewFile();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(highScoreFile));
            writer.write(Integer.toString(highScore));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void restartGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        food.setLocation(15, 15);
        direction = KeyEvent.VK_RIGHT;
        isEating = false;
        isGameOver = false;
        score = 0;
        timer.start();
        restartButton.setEnabled(false); // Disable the button after restarting
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (isGameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over! Score: " + score + " High Score: " + highScore,
                    SCALE * WIDTH / 2 - 60, SCALE * HEIGHT / 2);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, SCALE * WIDTH, SCALE * HEIGHT);

            g.setColor(Color.RED);
            g.fillRect(food.x * SCALE, food.y * SCALE, SCALE, SCALE);

            g.setColor(Color.WHITE);
            for (Point point : snake) {
                g.fillRect(point.x * SCALE, point.y * SCALE, SCALE, SCALE);
            }

            g.setColor(Color.WHITE);
            g.drawString("Score: " + score, 10, 15);
            g.drawString("High Score: " + highScore, SCALE * WIDTH - 100, 15);
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sappu Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(SCALE * WIDTH, SCALE * HEIGHT);
        SappuGame sappuGame = new SappuGame();
        frame.add(sappuGame);
        frame.setVisible(true);
    }
}
