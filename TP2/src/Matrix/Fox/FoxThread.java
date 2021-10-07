package Matrix.Fox;

import Matrix.Matrix;

public class FoxThread extends Thread
{
    private final Matrix blockA;
    private final Matrix blockB;
    private final Matrix blockC;

    public FoxThread(Matrix blockA, Matrix blockB, Matrix blockC)
    {
        this.blockA = blockA;
        this.blockB = blockB;
        this.blockC = blockC;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < blockA.getHeight(); i++)
        {
            for (int j = 0; j < blockB.getWidth(); j++)
            {
                for (int k = 0; k < blockB.getHeight(); k++)
                {
                    int value = blockC.get(i, j) + blockA.get(i, k) * blockB.get(k, j);
                    blockC.set(i, j, value);
                }
            }
        }
    }
}
