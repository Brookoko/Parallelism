import Matrix.Collective.CollectiveMatrixMultiplication;
import Matrix.Simple.SimpleMatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var simple = new SimpleMatrixMultiplication();
        var collective = new CollectiveMatrixMultiplication();
        PerformanceChecker performanceChecker = new PerformanceChecker(simple);
        var a = Utils.createRandomMatrix(3, 3, 1, 10);
        var b = Utils.createRandomMatrix(3, 3, 1, 10);
        var c1 = simple.multiply(a, b);
        var c2 = collective.multiply(a, b);
        var rank = MPI.COMM_WORLD.getRank();
        if (rank == 0)
        {
            Utils.print(a);
//            Utils.print(c1);
//            Utils.print(c2);
        }
        MPI.Finalize();
    }
}
