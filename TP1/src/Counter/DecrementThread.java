package Counter;

public class DecrementThread extends Thread
{
    private final Counter counter;
    private final int numberOfOperations;

    public DecrementThread(Counter counter, int numberOfOperations)
    {
        this.counter = counter;
        this.numberOfOperations = numberOfOperations;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < numberOfOperations; i++)
        {
            counter.decrement();
        }
    }
}
