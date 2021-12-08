import Matrix.Blocking.BlockingMatrixMultiplication;
import Matrix.Matrix;
import Matrix.Nonblocking.NonblockingMatrixMultiplication;
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
        var nonblocking = new NonblockingMatrixMultiplication();
        var a = new Matrix(3, 3);
        a.set(0, 0, 0);
        a.set(0, 1, 1);
        a.set(0, 2, 2);
        a.set(1, 0, 3);
        a.set(1, 1, 4);
        a.set(1, 2, 5);
        a.set(2, 0, 6);
        a.set(2, 1, 7);
        a.set(2, 2, 8);
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
