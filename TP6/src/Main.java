import Matrix.Blocking.BlockingMatrixMultiplication;
import Matrix.Simple.SimpleMatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var simple = new SimpleMatrixMultiplication();
        var blocking = new BlockingMatrixMultiplication();
        var nonblocking = new BlockingMatrixMultiplication();
        var a = Utils.createRandomMatrix(3, 3, 0, 10);
        var b = Utils.createRandomMatrix(3, 3, 0, 10);
        var c1 = simple.multiply(a, b);
        var c2 = blocking.multiply(a, b);
        var c3 = nonblocking.multiply(a, b);
        var rank = MPI.COMM_WORLD.getRank();
        if (rank == 0)
        {
            Utils.print(c1);
            Utils.print(c2);
            Utils.print(c3);
        }
        MPI.Finalize();
    }
}
