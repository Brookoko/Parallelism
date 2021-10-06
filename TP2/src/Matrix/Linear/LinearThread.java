package Matrix.Linear;

import Matrix.Matrix;

public class LinearThread extends Thread
{

    private final Matrix a;
    private final Matrix b;
    private final Matrix result;

    private final int startIndex;

    public LinearThread(Matrix a, Matrix b, Matrix result, int startIndex)
    {
        this.a = a;
        this.b = b;
        this.result = result;
        this.startIndex = startIndex;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < a.getHeight(); i++)
        {
            for (int j = 0; j < a.getWidth(); j++)
            {
                int index = (startIndex + i) - j;
                int offset = index < 0 ? b.getHeight() : 0;
                index += offset;
                for (int k = 0; k < b.getWidth(); k++)
                {
                    int value = result.get(i, k) + a.get(i, index) * b.get(index, k);
                    result.set(i, k, value);
                }
            }
        }
    }
}