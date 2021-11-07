package Shop;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Shop
{
    private final int numberOfProcessors;
    private final int maxInQueue;
    private final ExecutorService executor;
    private final Queue<Runnable> queue;

    private int numberOfClients;
    private int totalClients;
    private int rejectedClients;

    public Shop(int numberOfProcessors, int maxInQueue)
    {
        this.numberOfProcessors = numberOfProcessors;
        this.maxInQueue = maxInQueue;
        executor = Executors.newFixedThreadPool(numberOfProcessors);
        queue = new ArrayDeque<>();
    }

    public synchronized void enqueue(Client client)
    {
        totalClients++;
        if (queue.size() >= maxInQueue)
        {
            rejectedClients++;
            return;
        }
        queue.offer(wrap(client));
        if (numberOfClients < numberOfProcessors)
        {
            scheduleNext();
        }
    }

    private synchronized Runnable wrap(Client client)
    {
        return new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    numberOfClients++;
                    client.run();
                }
                finally
                {
                    numberOfClients--;
                    scheduleNext();
                }
            }
        };
    }

    private synchronized void scheduleNext()
    {
        Runnable runnable = queue.poll();
        if (runnable != null && !executor.isShutdown())
        {
            executor.execute(runnable);
        }
    }

    public void shutdown()
    {
        executor.shutdownNow();
        rejectedClients += queue.size();
    }

    public int getTotalClients()
    {
        return totalClients;
    }

    public int getRejectedClients()
    {
        return rejectedClients;
    }

    public int getQueuedClients()
    {
        return queue.size();
    }
}
