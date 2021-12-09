import Matrix.Matrix;
import Matrix.MatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;

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
        var c = new Matrix(a.getHeight(), b.getWidth());
        multiplication.multiply(a, b, c);
        long end = System.currentTimeMillis();
        return convertToSeconds(start, end);
    }

    public double countTimeFor(int size) throws Exception
    {
        return countTimeRepeated(size, size, size)[0];
    }

    public double[] countTimeRepeated(int startSize, int endSize, int step) throws Exception
    {
        int repetitions = (endSize - startSize) / step + 1;
        Matrix[] leftMatrices = new Matrix[repetitions];
        Matrix[] rightMatrices = new Matrix[repetitions];
        if (MPI.COMM_WORLD.getRank() == 0)
        {
            for (int i = 0; i < repetitions; i++)
            {
                var size = startSize + i * step;
                leftMatrices[i] = Utils.createRandomMatrix(size, size, 0, 1000);
                rightMatrices[i] = Utils.createRandomMatrix(size, size, 0, 1000);
            }
        }

        double[] results = new double[repetitions];
        for (int i = 0; i < repetitions; i++)
        {
            long start = System.currentTimeMillis();
            var result = MPI.COMM_WORLD.getRank() == 0 ?
                    new Matrix(leftMatrices[i].getHeight(), rightMatrices[i].getWidth()) :
                    new Matrix(0, 0);
            multiplication.multiply(leftMatrices[i], rightMatrices[i], result);
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
