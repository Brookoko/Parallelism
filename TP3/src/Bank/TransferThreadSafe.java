package Bank;

import java.util.concurrent.locks.ReentrantLock;

public class TransferThreadSafe extends Thread
{
    private static final int REPS = 1000;

    private final Bank bank;
    private final int fromAccount;
    private final int maxAmount;
    private final ReentrantLock lock;

    public TransferThreadSafe(Bank bank, int from, int max, ReentrantLock lock)
    {
        this.bank = bank;
        fromAccount = from;
        maxAmount = max;
        this.lock = lock;
    }

    public void run()
    {
        try
        {
            while (!interrupted())
            {
                for (int i = 0; i < REPS; i++)
                {
                    lock.lock();
                    int toAccount = (int) (bank.size() * Math.random());
                    int amount = (int) (maxAmount * Math.random() / REPS);
                    bank.transfer(fromAccount, toAccount, amount);
                    lock.unlock();
                    Thread.sleep(1);
                }
            }
        }
        catch (InterruptedException e)
        {
        }
    }
}
