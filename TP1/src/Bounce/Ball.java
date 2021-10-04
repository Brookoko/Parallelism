package Bounce;

class Ball
{
    private double x;
    private double y;
    private double dx;
    private double dy;
    private final double radius;
    private final BallType type;

    private boolean isAlive = true;

    public Ball(double x, double y, double radius, double dx, double dy, BallType type)
    {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.type = type;
        this.dx = dx;
        this.dy = dy;
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

    public boolean isAlive()
    {
        return isAlive;
    }

    public void makeDead()
    {
        isAlive = false;
    }

    public BallType getType()
    {
        return type;
    }

    public void move(int width, int height)
    {
        x += dx;
        y += dy;
        if (x < 0)
        {
            x = 0;
            dx = -dx;
        }
        if (x + radius >= width)
        {
            x = width - radius;
            dx = -dx;
        }
        if (y < 0)
        {
            y = 0;
            dy = -dy;
        }
        if (y + radius >= height)
        {
            y = height - radius;
            dy = -dy;
        }
    }
}
