package Bank;

public class BankSyncronized extends Bank
{
    private final int[] accounts;
    private long transactions;

    public BankSyncronized(int numberOfAccounts, int initialBalance)
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
    public synchronized void transfer(int from, int to, int amount) throws InterruptedException
    {
        accounts[from] -= amount;
        accounts[to] += amount;
        transactions++;
        if (transactions % TESTS == 0)
        {
            printData();
        }
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
