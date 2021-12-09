import Matrix.Collective.CollectiveMatrixMultiplication;
import Matrix.Matrix;
import Matrix.Simple.SimpleMatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;

public class Main
{
    private final static int SIZE = 3;

    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var simple = new SimpleMatrixMultiplication();
        var collective = new CollectiveMatrixMultiplication();
        PerformanceChecker performanceChecker = new PerformanceChecker(simple);
        var rank = MPI.COMM_WORLD.getRank();
        var a = new Matrix(0, 0);
        var b = new Matrix(0, 0);
        if (rank == 0)
        {
            a = Utils.createSequenceMatrix(SIZE, SIZE);
            b = Utils.createSequenceMatrix(SIZE, SIZE);
        }
        var c1 = new Matrix(SIZE, SIZE);
        var c2 = new Matrix(SIZE, SIZE);
        simple.multiply(a, b, c1);
        collective.multiply(a, b, c2);
        if (rank == 0)
        {
            Utils.print(c1);
            Utils.print(c2);
        }
        MPI.Finalize();
    }
}
