package Shop;

import java.util.Random;

public class Client implements Runnable
{
    private static final int TIME_TO_PROCESS = 100;
    private final int MAX_DELAY = 400;
    private final int MIN_DELAY = 10;

    private final long id;
    private final Random random;

    public Client(long id)
    {
        this.id = id;
        random = new Random();
    }

    @Override
    public void run()
    {
        try
        {
            int delay = random.nextInt(MAX_DELAY - MIN_DELAY) + MIN_DELAY;
            Thread.sleep(delay);
        }
        catch (InterruptedException e)
        {
        }
    }
}
