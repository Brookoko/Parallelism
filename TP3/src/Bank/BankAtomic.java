package Bank;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BankAtomic extends Bank
{
    public static final int TESTS = 10000;

    private final AtomicInteger[] accounts;
    private AtomicLong transactions;

    public BankAtomic(int numberOfAccounts, int initialBalance)
    {
        accounts = new AtomicInteger[numberOfAccounts];
        int i;
        for (i = 0; i < accounts.length; i++)
        {
            accounts[i] = new AtomicInteger(initialBalance);
        }
        transactions = new AtomicLong(0);
    }

    @Override
    public void transfer(int from, int to, int amount) throws InterruptedException
    {
        accounts[from].addAndGet(-amount);
        accounts[to].addAndGet(amount);
        if (transactions.incrementAndGet() % TESTS == 0)
        {
            printData();
        }
    }

    @Override
    public void printData()
    {
        int sum = 0;
        for (AtomicInteger account : accounts)
        {
            sum += account.get();
        }
        System.out.println("Transactions:" + transactions + " Sum: " + sum);
    }

    @Override
    public int size()
    {
        return accounts.length;
    }
}
