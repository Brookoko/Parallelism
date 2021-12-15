import Matrix.Collective.CollectiveMatrixMultiplication;
import Matrix.Simple.SimpleMatrixMultiplication;
import mpi.MPI;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var simple = new SimpleMatrixMultiplication();
        var collective = new CollectiveMatrixMultiplication();
        PerformanceChecker performanceChecker = new PerformanceChecker(collective);
        var rank = MPI.COMM_WORLD.getRank();
        var time = performanceChecker.countTimeFor(1500);
//        var times = performanceChecker.countTimeRepeated(500, 500, 1000);
        if (rank == 0)
        {
            System.out.println(time);
//            for (var t : times)
//            {
//                System.out.println(t);
//            }
        }
        MPI.Finalize();
    }
}
