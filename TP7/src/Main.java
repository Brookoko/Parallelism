import Matrix.Simple.SimpleMatrixMultiplication;
import mpi.MPI;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var simple = new SimpleMatrixMultiplication();
        PerformanceChecker performanceChecker = new PerformanceChecker(simple);
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
