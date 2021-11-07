package Shop;

public class Main
{
    private static final int NUMBER_OF_QUEUES = 3;
    private static final int TIME_TO_PROCESS = 5000;
    private static final int TIME_TO_CHECK = 500;

    public static void main(String[] args) throws InterruptedException
    {
        Shop shop = new Shop(NUMBER_OF_QUEUES);
        var thread = new ClientThread(shop);
        thread.start();
        var shutdownThread = shop.processFor(TIME_TO_PROCESS, TIME_TO_CHECK);
        shutdownThread.start();
        shutdownThread.join();
        System.out.println(shop.getStatistic());
    }
}
