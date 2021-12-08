package Counter;

import java.util.concurrent.locks.ReentrantLock;

public class CounterLock extends Counter
{
    private final ReentrantLock locker;

    public CounterLock()
    {
        locker = new ReentrantLock();
    }

    @Override
    public void increment()
    {
        locker.lock();
        count++;
        locker.unlock();
    }

    @Override
    public synchronized void decrement()
    {
        locker.lock();
        count--;
        locker.unlock();
    }
}
