package CP;

import java.util.Random;

public class Consumer implements Runnable
{
    private Drop drop;

    public Consumer(Drop drop)
    {
        this.drop = drop;
    }

    public void run()
    {
        Random random = new Random();
        for (int number = drop.take(); number >= 0; number = drop.take())
        {
            System.out.println(number);
            try
            {
                Thread.sleep(random.nextInt(10));
            } catch (InterruptedException ignored)
            {
            }
        }
    }
}
