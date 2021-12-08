package Matrix.Nonblocking;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;
import Matrix.Utils;
import mpi.MPI;
import mpi.MPIException;
import mpi.Request;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;

public class NonblockingMatrixMultiplication extends MatrixMultiplication
{
    private final static int MASTER = 0;
    private final static int FROM_MASTER = 1;
    private final static int FROM_WORKER = 10;
    private final static int OFFSET = 1;
    private final static int ROWS = 2;
    private final static int A = 3;
    private final static int B = 4;
    private final static int C = 5;

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

            MPI.COMM_WORLD.iSend(allocateDirectInt(1).put(offset), 1, MPI.INT, dest, FROM_MASTER + OFFSET);
            MPI.COMM_WORLD.iSend(allocateDirectInt(1).put(rows), 1, MPI.INT, dest, FROM_MASTER + ROWS);

            var doubleBuffer = allocateDirectDouble(rows * NCA).put(Utils.Flatten(buffer));
            MPI.COMM_WORLD.iSend(doubleBuffer, rows * NCA, MPI.DOUBLE, dest, FROM_MASTER + A);

            doubleBuffer = allocateDirectDouble(b.getHeight() * b.getWidth()).put(Utils.Flatten(b.getData()));
            MPI.COMM_WORLD.iSend(doubleBuffer, NCA * NCB, MPI.DOUBLE, dest, FROM_MASTER + B);

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
            offsetBuffers[index] = allocateDirectInt(1);
            rowsBuffers[index] = allocateDirectInt(1);
            offsetRequests[index] = MPI.COMM_WORLD.iRecv(offsetBuffers[index], 1, MPI.INT, source, FROM_WORKER + OFFSET);
            rowsRequests[index] = MPI.COMM_WORLD.iRecv(rowsBuffers[index], 1, MPI.INT, source, FROM_WORKER + ROWS);
        }

        Request.waitAll(offsetRequests);
        Request.waitAll(rowsRequests);

        for (var source = 1; source <= numworkers; source++)
        {
            var index = source - 1;
            var rows = rowsBuffers[index].get(0);
            workersBuffers[index] = allocateDirectDouble(rows * NCB);
            workersRequests[index] = MPI.COMM_WORLD.iRecv(workersBuffers[index], rows * NCB, MPI.DOUBLE, source, FROM_WORKER + C);
        }

        Request.waitAll(workersRequests);

        for (var source = 1; source <= numworkers; source++)
        {
            var index = source - 1;
            var rows = rowsBuffers[index].get(0);
            var offsetSource = offsetBuffers[index].get(0);
            var buffer = toArray(workersBuffers[index]);
            var result = Utils.Nest(buffer, rows, NCB);
            for (var i = 0; i < rows; i++)
            {
                c.setRow(offsetSource + i, result[i]);
            }
        }

    }

    private void ProcessWorker() throws MPIException
    {
        var offsetBuffer = allocateDirectInt(1);
        var rowsBuffer = allocateDirectInt(1);
        var bufferB = allocateDirectDouble(NCA * NCB);

        var requests = new Request[3];

        requests[0] = MPI.COMM_WORLD.iRecv(offsetBuffer, 1, MPI.INT, MASTER, FROM_MASTER + OFFSET);
        requests[1] = MPI.COMM_WORLD.iRecv(bufferB, NCA * NCB, MPI.DOUBLE, MASTER, FROM_MASTER + B);
        MPI.COMM_WORLD.iRecv(rowsBuffer, 1, MPI.INT, MASTER, FROM_MASTER + ROWS).waitFor();

        var rows = rowsBuffer.get(0);
        var bufferA = allocateDirectDouble(rows * NCA);
        requests[2] = MPI.COMM_WORLD.iRecv(bufferA, rows * NCA, MPI.DOUBLE, MASTER, FROM_MASTER + A);

        Request.waitAll(requests);

        var a = Utils.Nest(toArray(bufferA), rows, NCA);
        var b = Utils.Nest(toArray(bufferB), NCA, NCB);
        var c = multiple(a, b, rows);
        var offset = offsetBuffer.get(0);

        var result = allocateDirectDouble(rows * NCB).put(Utils.Flatten(c));
        MPI.COMM_WORLD.iSend(allocateDirectInt(1).put(offset), 1, MPI.INT, MASTER, FROM_WORKER + OFFSET);
        MPI.COMM_WORLD.iSend(allocateDirectInt(1).put(rows), 1, MPI.INT, MASTER, FROM_WORKER + ROWS);
        MPI.COMM_WORLD.iSend(result, rows * NCB, MPI.DOUBLE, MASTER, FROM_WORKER + C);
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

    private IntBuffer allocateDirectInt(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * 4);
        bb.order(ByteOrder.nativeOrder());
        return bb.asIntBuffer();
    }

    private DoubleBuffer allocateDirectDouble(int size)
    {
        ByteBuffer bb = ByteBuffer.allocateDirect(size * 8);
        bb.order(ByteOrder.nativeOrder());
        return bb.asDoubleBuffer();
    }

    private double[] toArray(DoubleBuffer buffer)
    {
        var result = new double[buffer.capacity()];
        buffer.get(result);
        return result;
    }
}
