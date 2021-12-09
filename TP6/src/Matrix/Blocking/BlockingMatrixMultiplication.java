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

    private int[] offset = new int[1];
    private int[] rows = new int[1];
    private int[] NCA = new int[1];
    private int[] NCB = new int[1];

    @Override
    public void multiply(Matrix a, Matrix b, Matrix c) throws Exception
    {
        var rank = MPI.COMM_WORLD.getRank();
        if (IsMaster(rank))
        {
            ProcessMaster(a, b, c);
        }
        else
        {
            ProcessWorker();
        }
    }

    private boolean IsMaster(int rank)
    {
        return rank == MASTER;
    }

    private void ProcessMaster(Matrix a, Matrix b, Matrix c) throws MPIException
    {
        var numworkers = MPI.COMM_WORLD.getSize() - 1;
        var averow = a.getHeight() / numworkers;
        var extra = a.getHeight() % numworkers;
        NCA[0] = a.getWidth();
        NCB[0] = b.getWidth();
        offset[0] = 0;

        for (var dest = 1; dest <= numworkers; dest++)
        {
            rows[0] = dest <= extra ? averow + 1 : averow;
            var buffer = new double[rows[0]][a.getWidth()];
            for (var i = 0; i < rows[0]; i++)
            {
                buffer[i] = a.getRow(offset[0] + i);
            }
            MPI.COMM_WORLD.send(NCA, 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(NCB, 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(offset, 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(rows, 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(Utils.Flatten(buffer), rows[0] * a.getWidth(), MPI.DOUBLE, dest, FROM_MASTER);
            MPI.COMM_WORLD.send(Utils.Flatten(b.getData()), a.getWidth() * b.getWidth(), MPI.DOUBLE, dest, FROM_MASTER);
            offset[0] += rows[0];
        }

        for (var source = 1; source <= numworkers; source++)
        {
            MPI.COMM_WORLD.recv(offset, 1, MPI.INT, source, FROM_WORKER);
            MPI.COMM_WORLD.recv(rows, 1, MPI.INT, source, FROM_WORKER);
            var buffer = new double[rows[0] * b.getWidth()];
            MPI.COMM_WORLD.recv(buffer, rows[0] * b.getWidth(), MPI.DOUBLE, source, FROM_WORKER);
            var result = Utils.Nest(buffer, rows[0], b.getWidth());
            for (var i = 0; i < rows[0]; i++)
            {
                c.setRow(offset[0] + i, result[i]);
            }
        }
    }

    private void ProcessWorker() throws MPIException
    {
        MPI.COMM_WORLD.recv(NCA, 1, MPI.INT, MASTER, FROM_MASTER);
        MPI.COMM_WORLD.recv(NCB, 1, MPI.INT, MASTER, FROM_MASTER);
        MPI.COMM_WORLD.recv(offset, 1, MPI.INT, MASTER, FROM_MASTER);
        MPI.COMM_WORLD.recv(rows, 1, MPI.INT, MASTER, FROM_MASTER);

        var bufferA = new double[rows[0] * NCA[0]];
        var bufferB = new double[NCA[0] * NCB[0]];
        MPI.COMM_WORLD.recv(bufferA, rows[0] * NCA[0], MPI.DOUBLE, MASTER, FROM_MASTER);
        MPI.COMM_WORLD.recv(bufferB, NCA[0] * NCB[0], MPI.DOUBLE, MASTER, FROM_MASTER);

        var a = Utils.Nest(bufferA, rows[0], NCA[0]);
        var b = Utils.Nest(bufferB, NCA[0], NCB[0]);
        var c = multiple(a, b);

        MPI.COMM_WORLD.send(offset, 1, MPI.INT, MASTER, FROM_WORKER);
        MPI.COMM_WORLD.send(rows, 1, MPI.INT, MASTER, FROM_WORKER);
        MPI.COMM_WORLD.send(Utils.Flatten(c), rows[0] * NCB[0], MPI.DOUBLE, MASTER, FROM_WORKER);
    }

    private double[][] multiple(double[][] a, double[][] b)
    {
        var c = new double[rows[0]][NCB[0]];
        for (var k = 0; k < NCB[0]; k++)
        {
            for (var i = 0; i < rows[0]; i++)
            {
                for (var j = 0; j < NCA[0]; j++)
                {
                    c[i][k] = c[i][k] + a[i][j] * b[j][k];
                }
            }
        }
        return c;
    }
}
