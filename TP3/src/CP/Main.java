package CP;

public class Main
{
    public static void main(String[] args)
    {
        Drop drop = new Drop(100);
        (new Thread(new Producer(drop, 200))).start();
        (new Thread(new Consumer(drop))).start();
    }
}
