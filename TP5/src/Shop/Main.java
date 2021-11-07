package Shop;

public class Main
{
    private static final int NUMBER_OF_SIMULATIONS = 10;

    public static void main(String[] args) throws InterruptedException
    {
        var threads = new Thread[NUMBER_OF_SIMULATIONS];
        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++)
        {
            var model = new Model();
            threads[i] = model;
            model.start();
        }
        for (int i = 0; i < NUMBER_OF_SIMULATIONS; i++)
        {
            threads[i].join();
        }
    }
}
