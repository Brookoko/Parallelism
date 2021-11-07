package Bank;

class TransferThread extends Thread
{
    private static final int REPS = 1000;

    private final Bank bank;
    private final int fromAccount;
    private final int maxAmount;

    public TransferThread(Bank bank, int from, int max)
    {
        this.bank = bank;
        fromAccount = from;
        maxAmount = max;
    }

    public void run()
    {
        try
        {
            while (!interrupted())
            {
                for (int i = 0; i < REPS; i++)
                {
                    int toAccount = (int) (bank.size() * Math.random());
                    int amount = (int) (maxAmount * Math.random() / REPS);
                    bank.transfer(fromAccount, toAccount, amount);
                    Thread.sleep(1);
                }
            }
        }
        catch (InterruptedException e)
        {
        }
    }
}
