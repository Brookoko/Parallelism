package Shop;

import java.util.Random;

public class ClientThread extends Thread
{
    private final Shop shop;
    private final int createRate;
    private final Random random;

    private int count;

    public ClientThread(Shop shop, int createRate)
    {
        this.shop = shop;
        this.createRate = createRate;
        random = new Random();
    }

    @Override
    public void run()
    {
        try
        {
            while (!isInterrupted())
            {
                Thread.sleep(createRate);
                shop.enqueue(createClient());
            }
        }
        catch (InterruptedException ignored)
        {
        }
    }

    private Client createClient()
    {
        return new Client(count++);
    }
}
