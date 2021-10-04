package Bounce;

public class BallThread extends Thread
{
    private final Ball ball;
    private final BallCanvas ballCanvas;
    private final int deltaTime;
    private final Thread thread;

    public BallThread(Ball ball, int deltaTime, BallCanvas ballCanvas, Thread thread)
    {
        this.ball = ball;
        this.deltaTime = deltaTime;
        this.ballCanvas = ballCanvas;
        this.thread = thread;
        setPriority(getPriorityFor(ball));
    }

    private int getPriorityFor(Ball ball)
    {
        BallType type = ball.getType();
        return type == BallType.Simple ? NORM_PRIORITY : MAX_PRIORITY;
    }

    @Override
    public void run()
    {
        try
        {
            if (thread != null) thread.join();
            while (ball.isAlive())
            {
                ball.move(ballCanvas.getWidth(), ballCanvas.getHeight());
                ballCanvas.checkIntersectionsFor(ball);
                Thread.sleep(deltaTime);
            }
        } catch (InterruptedException ex)
        {
        }
    }
}

