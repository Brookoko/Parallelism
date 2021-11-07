package Shop;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Shop
{
    private final ExecutorService executor;
    private final Queue<Runnable> queue;
    private final int numberOfProcessors;

    private int numberOfClients;
    private int enqueuedClients;
    private int rejectedClients;
    private List<Double> averageClientsInQueue;

    public Shop(int numberOfProcessors)
    {
        this.numberOfProcessors = numberOfProcessors;
        executor = Executors.newFixedThreadPool(numberOfProcessors);
        queue = new ArrayDeque<>();
        averageClientsInQueue = new ArrayList<>();
    }

    public synchronized void enqueue(Client client)
    {
        enqueuedClients++;
        queue.offer(new Runnable()
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
        });
        if (numberOfClients < numberOfProcessors)
        {
            scheduleNext();
        }
    }

    private synchronized void scheduleNext()
    {
        Runnable runnable = queue.poll();
        if (runnable != null && !executor.isShutdown())
        {
            executor.execute(runnable);
        }
    }


    public Thread processFor(int executionTime, int checkRate)
    {
        return new ShutdownThread(executionTime, checkRate);
    }

    private void checkQueue()
    {
        double size = queue.size();
        averageClientsInQueue.add(size);
    }

    private void shutdown()
    {
        executor.shutdown();
        rejectedClients = queue.size();
        try
        {
            executor.awaitTermination(100, TimeUnit.MILLISECONDS);
        }
        catch (InterruptedException ignored)
        {
        }
    }

    public String getStatistic()
    {
        var builder = new StringBuilder();

        builder.append("Clients: ");
        builder.append(enqueuedClients);
        builder.append("\n");

        builder.append("Processed: ");
        builder.append(enqueuedClients - rejectedClients);
        builder.append("\n");

        builder.append("Rejected: ");
        builder.append(rejectedClients);
        builder.append("\n");

        builder.append("Average queue: ");
        builder.append(averageClientsInQueue.stream().mapToDouble(d -> d).average().getAsDouble());
        builder.append("\n");

        return builder.toString();
    }

    private class ShutdownThread extends Thread
    {
        private final int executionTime;
        private final int checkRate;

        public ShutdownThread(int executionTime, int checkRate)
        {

            this.executionTime = executionTime;
            this.checkRate = checkRate;
        }

        @Override
        public void run()
        {
            try
            {
                int time = 0;
                while (time < executionTime)
                {
                    Thread.sleep(checkRate);
                    checkQueue();
                    time += checkRate;
                }
                shutdown();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }
}
