import java.util.ArrayList;
import java.util.List;

public class Drop
{
    private final List<Integer> buffer;
    private final int size;

    public Drop(int size)
    {
        this.size = size;
        buffer = new ArrayList<>(size);
    }

    public synchronized int take()
    {
        while (buffer.isEmpty())
        {
            try
            {
                wait();
            } catch (InterruptedException ignored)
            {
            }
        }
        return buffer.remove(0);
    }

    public synchronized void put(int number)
    {
        while (size == buffer.size())
        {
            try
            {
                wait();
            } catch (InterruptedException ignored)
            {
            }
        }
        buffer.add(number);
        notifyAll();
    }
}