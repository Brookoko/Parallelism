package Shop;

public class ShutdownThread extends Thread
{
    private final Shop shop;
    private final int executionTime;

    public ShutdownThread(Shop shop, int executionTime)
    {
        this.shop = shop;
        this.executionTime = executionTime;
    }

    @Override
    public void run()
    {
        try
        {
            Thread.sleep(executionTime);
            shop.shutdown();
        }
        catch (InterruptedException ignored)
        {
        }
    }
}
