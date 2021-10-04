package Counter;

public class CounterLock extends Counter
{
    private final Object lock = new Object();

    @Override
    public void increment()
    {
        synchronized (lock)
        {
            count++;
        }
    }

    @Override
    public synchronized void decrement()
    {
        synchronized (lock)
        {
            count--;
        }
    }
}
