package Bank;

public abstract class Bank
{
    public static final int TESTS = 10000;

    public abstract void transfer(int from, int to, int amount) throws InterruptedException;

    public abstract void printData();

    public abstract int size();
}
