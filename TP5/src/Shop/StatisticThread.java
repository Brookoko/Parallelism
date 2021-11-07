package Shop;

public class StatisticThread extends Thread
{
    private final Shop shop;
    private final int checkRate;

    public StatisticThread(Shop shop, int checkRate)
    {
        this.shop = shop;
        this.checkRate = checkRate;
    }

    @Override
    public void run()
    {
        try
        {
            while (!isInterrupted())
            {
                Thread.sleep(checkRate);
                System.out.println(getStatistic());
            }
        }
        catch (InterruptedException ignored)
        {
        }
    }

    private String getStatistic()
    {
        var builder = new StringBuilder();

        var totalClients = shop.getTotalClients();
        var rejectedClients = shop.getRejectedClients();
        var queuedClients = shop.getQueuedClients();
        var rejectionPercent = (double) rejectedClients / totalClients;

        builder.append(totalClients);
        builder.append(",");

        builder.append(totalClients - rejectedClients);
        builder.append(",");

        builder.append(rejectedClients);
        builder.append(",");

        builder.append(queuedClients);
        builder.append(",");

        builder.append(String.format("%.2f", rejectionPercent));

        return builder.toString();
    }
}
