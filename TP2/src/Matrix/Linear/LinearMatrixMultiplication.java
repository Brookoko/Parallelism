package Matrix.Linear;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;

public class LinearMatrixMultiplication extends MatrixMultiplication
{
    private final int threadsSize;

    public LinearMatrixMultiplication(int threadSize)
    {
        this.threadsSize = threadSize;
    }

    @Override
    public Matrix multiply(Matrix a, Matrix b) throws InterruptedException
    {
        LinearThread[] linearThread = new LinearThread[threadsSize];
        Matrix result = new Matrix(a.getHeight(), b.getWidth());
        int threadsSizeForMatrix = Math.min(threadsSize, a.getHeight());

        int rowsPerThread = a.getHeight() / threadsSizeForMatrix;
        for (int i = 0; i < threadsSizeForMatrix; i++)
        {
            int startIndex = rowsPerThread * i;
            Matrix copyA = new Matrix(rowsPerThread, a.getWidth());
            Matrix copyResult = new Matrix(rowsPerThread, result.getWidth());
            for (int j = 0; j < rowsPerThread; j++)
            {
                int index = startIndex + j;
                copyA.setRow(j, a.getRow(index));
                copyResult.setRow(j, result.getRow(index));
            }
            linearThread[i] = new LinearThread(copyA, b, copyResult, startIndex);
            linearThread[i].start();
        }

        for (LinearThread thread : linearThread)
        {
            thread.join();
        }

        return result;
    }
}
