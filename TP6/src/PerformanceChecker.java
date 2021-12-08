import Matrix.Matrix;
import Matrix.MatrixMultiplication;

public class PerformanceChecker
{
    private final MatrixMultiplication multiplication;

    public PerformanceChecker(MatrixMultiplication multiplication)
    {
        this.multiplication = multiplication;
    }

    public double countTimeFor(Matrix a, Matrix b) throws Exception
    {
        long start = System.currentTimeMillis();
        multiplication.multiply(a, b);
        long end = System.currentTimeMillis();
        return convertToSeconds(start, end);
    }

    public double[] countTimeRepeated(int startSize, int endSize, int step) throws Exception
    {
        int repetitions = (endSize - startSize) / step + 1;
        Matrix[] leftMatrices = new Matrix[repetitions];
        Matrix[] rightMatrices = new Matrix[repetitions];
        for (int i = 0; i < repetitions; i++)
        {
            var size = startSize + i * step;
            leftMatrices[i] = Utils.createRandomMatrix(size, size, 0, 1000);
            rightMatrices[i] = Utils.createRandomMatrix(size, size, 0, 1000);
        }

        double[] results = new double[repetitions];
        for (int i = 0; i < repetitions; i++)
        {
            long start = System.currentTimeMillis();
            multiplication.multiply(leftMatrices[i], rightMatrices[i]);
            long end = System.currentTimeMillis();
            results[i] = convertToSeconds(start, end);
        }

        return results;
    }

    private double convertToSeconds(long start, long end)
    {
        return (end - start) / 1000.0;
    }
}
