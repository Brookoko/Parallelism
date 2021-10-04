package Bounce;

public class Pocket
{
    private final double x;
    private final double y;
    private final double radius;

    public Pocket(double x, double y, double radius)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
    }

    public double getX()
    {
        return x;
    }

    public double getY()
    {
        return y;
    }

    public double getRadius()
    {
        return radius;
    }

    public boolean isIntersectedWith(Ball ball)
    {
        double ballX = ball.getX();
        double ballY = ball.getY();
        double sqrtDistance = Math.pow(ballX - x, 2) + Math.pow(ballY - y, 2);
        return sqrtDistance <= radius * radius;
    }
}
