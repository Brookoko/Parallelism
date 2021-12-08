import Matrix.Blocking.BlockingMatrixMultiplication;
import Matrix.Nonblocking.NonblockingMatrixMultiplication;
import Matrix.Simple.SimpleMatrixMultiplication;
import mpi.MPI;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var simple = new SimpleMatrixMultiplication();
        var blocking = new BlockingMatrixMultiplication();
        var nonblocking = new NonblockingMatrixMultiplication();
        PerformanceChecker performanceChecker = new PerformanceChecker(blocking);
        double[] times = performanceChecker.countTimeRepeated(500, 500, 100);
        var rank = MPI.COMM_WORLD.getRank();
        if (rank == 0)
        {
            for (double time : times)
            {
                System.out.println(time);
            }
        }
        MPI.Finalize();
    }
}
