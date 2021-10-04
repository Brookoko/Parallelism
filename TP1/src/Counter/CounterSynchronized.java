package Counter;

public class CounterSynchronized extends Counter
{
    @Override
    public synchronized void increment()
    {
        count++;
    }

    @Override
    public synchronized void decrement()
    {
        count--;
    }
}
