package Bounce;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.Random;

public class BallCanvas extends JPanel
{
    private static final int BALL_SPEED = 1;
    private static final int BALL_RADIUS = 20;
    private static final int POCKET_RADIUS = 40;
    private static final int DELTA_TIME = 5;

    private final ArrayList<Ball> balls = new ArrayList<>();
    private final Pocket pocket;
    private final JLabel label;

    private BallThread lastThread;
    private int ballsInPocket;

    public BallCanvas()
    {
        super();
        label = new JLabel();
        add(label, BorderLayout.NORTH);
        pocket = new Pocket(0, 0, POCKET_RADIUS);
    }

    public void createBall(BallType type)
    {
        Random random = new Random();
        double x = random.nextInt(getWidth() - 2 * BALL_RADIUS) + BALL_RADIUS;
        double y = random.nextInt(getHeight() - 2 * BALL_RADIUS) + BALL_RADIUS;
        double dx = random.nextBoolean() ? BALL_SPEED : -BALL_SPEED;
        double dy = random.nextBoolean() ? BALL_SPEED : -BALL_SPEED;
        Ball ball = new Ball(x, y, BALL_RADIUS, dx, dy, type);
        add(ball);
    }

    public void createBallAtBottomRight(BallType type)
    {
        double x = getWidth() - 2 * BALL_RADIUS;
        double y = getHeight() - BALL_RADIUS;
        double dx = -BALL_SPEED;
        double dy = -BALL_SPEED;
        Ball ball = new Ball(x, y, BALL_RADIUS, dx, dy, type);
        add(ball);
    }

    public void add(Ball ball)
    {
        this.balls.add(ball);
        BallThread thread = new BallThread(ball, DELTA_TIME, this, lastThread);
        lastThread = thread;
        thread.start();
        System.out.println("Thread name = " + thread.getName());
    }

    public void checkIntersectionsFor(Ball ball)
    {
        if (pocket.isIntersectedWith(ball))
        {
            ball.makeDead();
            ballsInPocket++;
        }
    }

    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (Ball ball : balls)
        {
            draw(ball, g2);
        }
        draw(pocket, g2);
        label.setText("Balls in pocket: " + ballsInPocket);
        repaint();
    }

    private void draw(Ball ball, Graphics2D g2)
    {
        if (ball.isAlive())
        {
            BallType type = ball.getType();
            Color color = type == BallType.Simple ? Color.blue : Color.red;
            g2.setColor(color);
            g2.fill(new Ellipse2D.Double(ball.getX(), ball.getY(), ball.getRadius(), ball.getRadius()));
        }
    }

    private void draw(Pocket pocket, Graphics2D g2)
    {
        g2.setColor(Color.lightGray);
        g2.fill(new Ellipse2D.Double(pocket.getX(), pocket.getY(), pocket.getRadius(), pocket.getRadius()));
    }
}
