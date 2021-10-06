package Matrix.Fox;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;

public class FoxMatrixMultiplication extends MatrixMultiplication
{
    private final int blockHeight;
    private final int blockWidth;

    public FoxMatrixMultiplication(int blockHeight, int blockWidth)
    {
        this.blockHeight = blockHeight;
        this.blockWidth = blockWidth;
    }

    @Override
    public Matrix multiply(Matrix a, Matrix b) throws Exception
    {
        Matrix[][] blocksA = divideToBlocks(a);
        Matrix[][] blocksB = divideToBlocks(b);
        Matrix[][] blocksC = divideToBlocks(new Matrix(a.getHeight(), b.getWidth()));

        FoxThread[][] foxThreads = new FoxThread[a.getHeight() / blockHeight][];
        for (int i = 0; i < blocksC.length; i++)
        {
            foxThreads[i] = new FoxThread[blocksC[i].length];
        }

        boolean firstLoop = false;
        for (int stage = 0; stage < foxThreads.length; stage++)
        {
            for (int i = 0; i < foxThreads.length; i++)
            {
                for (int j = 0; j < foxThreads[i].length; j++)
                {
                    if (firstLoop)
                    {
                        foxThreads[i][j].join();
                    }
                    int index = i + stage;
                    int offset = index > foxThreads.length - 1 ? foxThreads.length : 0;
                    index -= offset;
                    foxThreads[i][j] = new FoxThread(blocksA[i][index], blocksB[index][j], blocksC[i][j]);
                    foxThreads[i][j].start();
                }
            }
            firstLoop = true;
        }

        for (FoxThread[] foxThread : foxThreads)
        {
            for (FoxThread thread : foxThread)
            {
                thread.join();
            }
        }
        return unionBlocks(blocksC);
    }

    private Matrix[][] divideToBlocks(Matrix matrix) throws Exception
    {
        if (matrix.getHeight() % blockHeight != 0 || matrix.getWidth() % blockWidth != 0)
        {
            throw new Exception("Matrix sizes is not a multiplier of block");
        }

        Matrix[][] dividedMatrix = new Matrix[matrix.getHeight() / blockHeight][];

        for (int i = 0; i < dividedMatrix.length; i++)
        {
            dividedMatrix[i] = new Matrix[matrix.getWidth() / blockWidth];
            for (int j = 0; j < dividedMatrix[i].length; j++)
            {
                int[][] data = new int[blockHeight][blockWidth];
                for (int k = blockHeight * i; k < blockHeight * (i + 1); k++)
                {
                    for (int n = blockHeight * j; n < blockWidth * (j + 1); n++)
                    {
                        data[k - (blockHeight * i)][n - (blockHeight * j)] = matrix.get(k, n);
                    }
                }
                dividedMatrix[i][j] = new Matrix(data);
            }
        }
        return dividedMatrix;
    }

    private Matrix unionBlocks(Matrix[][] blocks)
    {
        int size = blockHeight;
        Matrix unitedMatrix = new Matrix(blocks.length * size, blocks.length * size);

        for (int i = 0; i < blocks.length; i++)
        {
            for (int j = 0; j < blocks[i].length; j++)
            {
                for (int k = size * i; k < size * (i + 1); k++)
                {
                    for (int n = size * j; n < size * (j + 1); n++)
                    {
                        int value = blocks[i][j].get(k - (size * i), n - (size * j));
                        unitedMatrix.set(k, n, value);
                    }
                }
            }
        }
        return unitedMatrix;
    }
}
