package Shop;

import java.util.Random;

public class ClientThread extends Thread
{
    private final Shop shop;
    private final Random random;

    private int count;

    public ClientThread(Shop shop)
    {
        this.shop = shop;
        random = new Random();
    }

    @Override
    public void run()
    {
        try
        {
            while (true)
            {
                Thread.sleep(random.nextInt(100));
                shop.enqueue(createClient());
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private Client createClient()
    {
        return new Client(count++);
    }
}
