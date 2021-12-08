package Matrix.Nonblocking;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class NonblockingMatrixMultiplication extends MatrixMultiplication
{
    private final static int MASTER = 0;
    private final static int FROM_MASTER = 1;
    private final static int FROM_WORKER = 2;

    private int NRA;
    private int NCA;
    private int NCB;

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
        var offset = 0;

        for (var dest = 1; dest <= numworkers; dest++)
        {
            var rows = dest <= extra ? averow + 1 : averow;

            var buffer = new double[rows][NCA];
            for (var i = 0; i < rows; i++)
            {
                buffer[i] = a.getRow(offset + i);
            }

            MPI.COMM_WORLD.iSend(IntBuffer.allocate(1).put(offset), 1, MPI.INT, dest, FROM_MASTER);
            MPI.COMM_WORLD.iSend(IntBuffer.allocate(1).put(rows), 1, MPI.INT, dest, FROM_MASTER);

            var doubleBuffer = DoubleBuffer.wrap(Utils.Flatten(buffer));
            MPI.COMM_WORLD.iSend(doubleBuffer, rows * NCA, MPI.DOUBLE, dest, FROM_MASTER);

            doubleBuffer = DoubleBuffer.wrap(Utils.Flatten(b.getData()));
            MPI.COMM_WORLD.iSend(doubleBuffer, NCA * NCB, MPI.DOUBLE, dest, FROM_MASTER);

            offset += rows;
        }

        var workersRequests = new Request[numworkers];
        var workersBuffers = new DoubleBuffer[numworkers];
        var offsetRequests = new Request[numworkers];
        var offsetBuffers = new IntBuffer[numworkers];
        var rowsRequests = new Request[numworkers];
        var rowsBuffers = new IntBuffer[numworkers];

        for (var source = 1; source <= numworkers; source++)
        {
            var index = source - 1;
            offsetBuffers[index] = IntBuffer.allocate(1);
            rowsBuffers[index] = IntBuffer.allocate(1);
            offsetRequests[index] = MPI.COMM_WORLD.iRecv(offsetBuffers[index], 1, MPI.INT, source, FROM_WORKER);
            rowsRequests[index] = MPI.COMM_WORLD.iRecv(rowsBuffers[index], 1, MPI.INT, source, FROM_WORKER);
        }

        Request.waitAll(offsetRequests);
        Request.waitAll(rowsRequests);

        for (var source = 1; source <= numworkers; source++)
        {
            var index = source - 1;
            var rows = rowsBuffers[index].get(0);
            workersBuffers[index] = DoubleBuffer.allocate(rows * NCB);
            workersRequests[index] = MPI.COMM_WORLD.iRecv(workersBuffers[index], rows * NCB, MPI.DOUBLE, source, FROM_WORKER);
        }

        Request.waitAll(workersRequests);

        for (var source = 1; source <= numworkers; source++)
        {
            var index = source - 1;
            var rows = rowsBuffers[index].get(0);
            var offsetSource = offsetBuffers[index].get(0);
            var buffer = workersBuffers[index].array();
            var result = Utils.Nest(buffer, rows, NCB);
            for (var i = 0; i < rows; i++)
            {
                c.setRow(offsetSource + i, result[i]);
            }
        }

    }

    private void ProcessWorker() throws MPIException, InterruptedException
    {
        var offsetBuffer = IntBuffer.allocate(1);
        var rowsBuffer = IntBuffer.allocate(1);
        var bufferB = DoubleBuffer.allocate(NCA * NCB);

        var requests = new Request[4];

        requests[0] = MPI.COMM_WORLD.iRecv(offsetBuffer, 1, MPI.INT, MASTER, FROM_MASTER);
        requests[1] = MPI.COMM_WORLD.iRecv(rowsBuffer, 1, MPI.INT, MASTER, FROM_MASTER);
        requests[3] = MPI.COMM_WORLD.iRecv(bufferB, NCA * NCB, MPI.DOUBLE, MASTER, FROM_MASTER);
        requests[1].wait();

        var rows = rowsBuffer.get(0);
        var bufferA = DoubleBuffer.allocate(rows * NCA);
        requests[2] = MPI.COMM_WORLD.iRecv(bufferA, rows * NCA, MPI.DOUBLE, MASTER, FROM_MASTER);

        Request.waitAll(requests);

        var a = Utils.Nest(bufferA.array(), rows, NCA);
        var b = Utils.Nest(bufferB.array(), NCA, NCB);
        var c = multiple(a, b, rows);
        var offset = offsetBuffer.get(0);

        MPI.COMM_WORLD.send(offset, 1, MPI.INT, MASTER, FROM_WORKER);
        MPI.COMM_WORLD.send(rows, 1, MPI.INT, MASTER, FROM_WORKER);
        MPI.COMM_WORLD.send(Utils.Flatten(c), rows * NCB, MPI.INT, MASTER, FROM_WORKER);
    }

    private double[][] multiple(double[][] a, double[][] b, int rows)
    {
        var c = new double[rows][NCB];
        for (var k = 0; k < NCB; k++)
        {
            for (var i = 0; i < rows; i++)
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
