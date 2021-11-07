package Bank;

import java.util.concurrent.Semaphore;

public class BankSemaphore extends Bank
{
    private final int[] accounts;
    private long transactions;

    private final Semaphore semaphore = new Semaphore(1);

    public BankSemaphore(int numberOfAccounts, int initialBalance)
    {
        accounts = new int[numberOfAccounts];
        int i;
        for (i = 0; i < accounts.length; i++)
        {
            accounts[i] = initialBalance;
        }
        transactions = 0;
    }

    @Override
    public void transfer(int from, int to, int amount) throws InterruptedException
    {
        semaphore.acquire();
        accounts[from] -= amount;
        accounts[to] += amount;
        transactions++;
        if (transactions % TESTS == 0)
        {
            printData();
        }
        semaphore.release();
    }

    @Override
    public void printData()
    {
        int sum = 0;
        for (int account : accounts)
        {
            sum += account;
        }
        System.out.println("Transactions:" + transactions + " Sum: " + sum);
    }

    @Override
    public int size()
    {
        return accounts.length;
    }
}
