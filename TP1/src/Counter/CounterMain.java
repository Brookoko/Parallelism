package Counter;

public class CounterMain
{
    private static final int NUMBER_OF_OPERATIONS = 100000;

    public static void main(String[] args) throws InterruptedException
    {
        Counter counter = new CounterSynchronized();
        Thread incrementThread = new IncrementThread(counter, NUMBER_OF_OPERATIONS);
        Thread decrementThread = new DecrementThread(counter, NUMBER_OF_OPERATIONS);
        incrementThread.start();
        decrementThread.start();
        incrementThread.join();
        decrementThread.join();
        System.out.println(counter.getCount());
    }
}
