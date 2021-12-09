package Matrix.Collective;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;
import mpi.MPIException;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class CollectiveMatrixMultiplication extends MatrixMultiplication
{
    private final static int MASTER = 0;
    private final static int FROM_MASTER = 1;
    private final static int FROM_WORKER = 2;

    private int[] offset = new int[1];
    private int[] rows = new int[1];

    @Override
    public void multiply(Matrix a, Matrix b, Matrix c) throws Exception
    {
        var rank = MPI.COMM_WORLD.getRank();
        var bufferA = MPI.newDoubleBuffer(0);
        var bufferRow = MPI.newDoubleBuffer(0);
        var rows = MPI.newIntBuffer(1);
        var extra = MPI.newIntBuffer(1);
        var NCA = MPI.newIntBuffer(1);
        var NCB = MPI.newIntBuffer(1);

        if (rank == 0)
        {
            var size = MPI.COMM_WORLD.getSize();
            rows.put(a.getHeight() / size);
            extra.put(a.getHeight() % size);
            NCA.put(a.getWidth());
            NCB.put(b.getWidth());
        }

        MPI.COMM_WORLD.bcast(rows, 1, MPI.INT, MASTER);
        MPI.COMM_WORLD.bcast(extra, 1, MPI.INT, MASTER);
        MPI.COMM_WORLD.bcast(NCA, 1, MPI.INT, MASTER);
        MPI.COMM_WORLD.bcast(NCB, 1, MPI.INT, MASTER);

        var bufferB = MPI.newDoubleBuffer(NCA.get(0) * NCB.get(0));
        if (rank == 0)
        {
            bufferB.put(Utils.Flatten(b.getData()));
            bufferA = MPI.newDoubleBuffer(a.getHeight() * NCA.get(0)).put(Utils.Flatten(a.getData()));
        }
        MPI.COMM_WORLD.bcast(bufferB, NCA.get(0) * NCB.get(0), MPI.DOUBLE, MASTER);

        var sendSize = rank == 0 ? a.getHeight() * rows.get(0) : 0;
        var rowsToReceive = rank < extra.get(0) ? rows.get(0) + 1 : rows.get(0);
        var receiveSize = rowsToReceive * NCA.get(0);
        bufferRow = MPI.newDoubleBuffer(receiveSize);
        MPI.COMM_WORLD.scatter(bufferA, sendSize, MPI.DOUBLE, bufferRow, receiveSize, MPI.DOUBLE, MASTER);

        var result = multiple(bufferRow, bufferB, rowsToReceive, NCA, NCB);
        var resultBuffer = MPI.newDoubleBuffer(receiveSize).put(Utils.Flatten(result));
        var bufferC = rank == 0 ? MPI.newDoubleBuffer(c.getHeight() * c.getWidth()) : null;
        MPI.COMM_WORLD.gather(resultBuffer, receiveSize, MPI.DOUBLE, bufferC, sendSize, MPI.DOUBLE, MASTER);

        if (rank == 0)
        {
            var dataC = Utils.Nest(toArray(bufferC), c.getHeight(), c.getWidth());
            c.setData(dataC);
        }
    }

    private double[][] multiple(DoubleBuffer bufferA, DoubleBuffer bufferB, int rows, IntBuffer NCA, IntBuffer NCB)
    {
        var a = Utils.Nest(toArray(bufferA), rows, NCA.get(0));
        var b = Utils.Nest(toArray(bufferB), NCA.get(0), NCB.get(0));
        var c = new double[rows][NCB.get(0)];
        for (var k = 0; k < NCB.get(0); k++)
        {
            for (var i = 0; i < rows; i++)
            {
                for (var j = 0; j < NCA.get(0); j++)
                {
                    c[i][k] = c[i][k] + a[i][j] * b[j][k];
                }
            }
        }
        return c;
    }

    private double[] toArray(DoubleBuffer buffer)
    {
        buffer.position(0);
        var result = new double[buffer.capacity()];
        buffer.get(result);
        return result;
    }
}
