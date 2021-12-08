package Matrix.Blocking;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;
import mpi.MPIException;

public class BlockingMatrixMultiplication extends MatrixMultiplication
{
    private final static int MASTER = 0;
    private final static int FROM_MASTER = 1;
    private final static int FROM_WORKER = 2;

    private int NRA;
    private int NCA;
    private int NCB;

    private int[] offset = new int[1];
    private int[] rows = new int[1];

    @Override
    public Matrix multiply(Matrix a, Matrix b) throws Exception
    {
        NRA = a.getHeight();
        NCA = a.getWidth();
        NCB = b.getWidth();
        var c = new Matrix(a.getHeight(), b.getWidth());
        var rank = MPI.COMM_WORLD.getRank();
        if (IsMaster(rank))
        {
            ProcessMaster(a, b, c);
        }
        else
        {
            ProcessWorker();
        }
        return c;
    }

    private boolean IsMaster(int rank)
    {
        return rank == MASTER;
    }

    private void ProcessMaster(Matrix a, Matrix b, Matrix c) throws MPIException
    {
        var numworkers = MPI.COMM_WORLD.getSize() - 1;
        var averow = NRA / numworkers;
        var extra = NRA % numworkers;
        offset[0] = 0;

        for (var dest = 1; dest <= numworkers; dest++)
        {
            rows[0] = dest <= extra ? averow + 1 : averow;
            var buffer = new double[rows[0]][NCA];
            for (var i = 0; i < rows[0]; i++)
            {
                buffer[i] = a.getRow(offset[0] + i);
            }
            MPI.COMM_WORLD.send(offset, 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(rows, 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(Utils.Flatten(buffer), rows[0] * NCA, MPI.DOUBLE, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(Utils.Flatten(b.getData()), NCA * NCB, MPI.DOUBLE, dest, FROM_MASTER);
            offset[0] += rows[0];
        }

        for (var source = 1; source <= numworkers; source++)
        {
            MPI.COMM_WORLD.recv(offset, 1, MPI.INT, source, FROM_WORKER);
            MPI.COMM_WORLD.recv(rows, 1, MPI.INT, source, FROM_WORKER);
            var buffer = new double[rows[0] * NCB];
            MPI.COMM_WORLD.recv(buffer, rows[0] * NCB, MPI.DOUBLE, source, FROM_WORKER);
            var result = Utils.Nest(buffer, rows[0], NCB);
            for (var i = 0; i < rows[0]; i++)
            {
                c.setRow(offset[0] + i, result[i]);
            }
        }
    }

    private void ProcessWorker() throws MPIException
    {
        MPI.COMM_WORLD.recv(offset, 1, MPI.INT, MASTER, FROM_MASTER);
        MPI.COMM_WORLD.recv(rows, 1, MPI.INT, MASTER, FROM_MASTER);

        var bufferA = new double[rows[0] * NCA];
        var bufferB = new double[NCA * NCB];
        MPI.COMM_WORLD.recv(bufferA, rows[0] * NCA, MPI.DOUBLE, MASTER, FROM_MASTER);
        MPI.COMM_WORLD.recv(bufferB, NCA * NCB, MPI.DOUBLE, MASTER, FROM_MASTER);

        var a = Utils.Nest(bufferA, rows[0], NCA);
        var b = Utils.Nest(bufferB, NCA, NCB);
        var c = multiple(a, b);

        MPI.COMM_WORLD.send(offset, 1, MPI.INT, MASTER, FROM_WORKER);
        MPI.COMM_WORLD.send(rows, 1, MPI.INT, MASTER, FROM_WORKER);
        MPI.COMM_WORLD.send(Utils.Flatten(c), rows[0] * NCB, MPI.DOUBLE, MASTER, FROM_WORKER);
    }

    private double[][] multiple(double[][] a, double[][] b)
    {
        var c = new double[rows[0]][NCB];
        for (var k = 0; k < NCB; k++)
        {
            for (var i = 0; i < rows[0]; i++)
            {
                for (var j = 0; j < NCA; j++)
                {
                    c[i][k] = c[i][k] + a[i][j] * b[j][k];
                }
            }
        }
        return c;
    }
}
