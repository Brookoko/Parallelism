package Shop;

public class Model extends Thread
{
    private static final int NUMBER_OF_QUEUES = 3;
    private static final int MAX_IN_QUEUE = 50;
    private static final int CLIENT_RATE = 100;
    private static final int TIME_TO_PROCESS = 60000;
    private static final int TIME_TO_PRINT_STATISTIC = 5000;

    public Model()
    {
    }

    @Override
    public void run()
    {
        Shop shop = new Shop(NUMBER_OF_QUEUES, MAX_IN_QUEUE);
        var clientThread = new ClientThread(shop, CLIENT_RATE);
        var shutdownThread = new ShutdownThread(shop, TIME_TO_PROCESS);
        var statisticThread = new StatisticThread(shop, TIME_TO_PRINT_STATISTIC);
        clientThread.start();
        shutdownThread.start();
        statisticThread.start();
        try
        {
            shutdownThread.join();
        }
        catch (InterruptedException ignored)
        {
        }
        clientThread.interrupt();
        statisticThread.interrupt();
    }
}
