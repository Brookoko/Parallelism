package Counter;

public class CounterSynchronizedBlock extends Counter
{
    @Override
    public void increment()
    {
        synchronized (this)
        {
            count++;
        }
    }

    @Override
    public synchronized void decrement()
    {
        synchronized (this)
        {
            count--;
        }
    }
}
