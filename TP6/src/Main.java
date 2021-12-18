import mpi.MPI;

import java.util.Arrays;

public class Main
{
    private final static int MASTER = 0;

    public static void main(String[] args) throws Exception
    {
        MPI.Init(args);
        var a = new int[DATA_LENGTH];
        var rank = MPI.COMM_WORLD.getRank();
        var size = MPI.COMM_WORLD.getSize();
        var rows = MPI.newIntBuffer(1);
        var extra = MPI.newIntBuffer(1);
        var bufferA = MPI.newDoubleBuffer(0);
        var bufferRow = MPI.newDoubleBuffer(0);

        if (rank == 0)
        {
            rows.put(a.length / size);
            extra.put(a.length % size);
        }

        MPI.COMM_WORLD.bcast(rows, 1, MPI.INT, MASTER);
        MPI.COMM_WORLD.bcast(extra, 1, MPI.INT, MASTER);

        var sendSize = rank == 0 ? a.length : 0;
        var rowsToReceive = rank < extra.get(0) ? rows.get(0) + 1 : rows.get(0);
        var receiveSize = rowsToReceive * a.length;
        bufferRow = MPI.newDoubleBuffer(receiveSize);

        MPI.COMM_WORLD.scatter(bufferA, sendSize, MPI.DOUBLE, bufferRow, receiveSize, MPI.DOUBLE, MASTER);
        var sum = Arrays.stream(bufferRow.array()).sum();

        var sumBuffer = MPI.newDoubleBuffer(1).put(sum);
        var resultBuffer = MPI.newDoubleBuffer(1);
        MPI.COMM_WORLD.reduce(sumBuffer, resultBuffer, 1, MPI.DOUBLE, MPI.MAX, MASTER);

        if (rank == 0)
        {
            var max = resultBuffer.get(0);
        }

        MPI.Finalize();
    }

    public static void дфіе(String[] args) throws Exception
    {
        MPI.Init(args);
        var rank = MPI.COMM_WORLD.getRank();
        var data = rank + 1;
        var sendBuffer = MPI.newIntBuffer(1).put(data);
        var receiveBuffer = MPI.newIntBuffer(1);
        MPI.COMM_WORLD.reduce(sendBuffer, receiveBuffer, 1, MPI.INT, MPI.PROD, MASTER);
        if (rank == 0)
        {
            var result = receiveBuffer.get(0);
        }
        MPI.Finalize();
    }

    private static final int DATA_LENGTH = 5;
    private final static int TAG = 1;
    private final static int FROM_WORKER = 1;
    private final static int TO_WORKER = 4;

    public static void old(String[] args) throws Exception
    {
        MPI.Init(args);
        var rank = MPI.COMM_WORLD.getRank();
        var data = new int[DATA_LENGTH];
        var dataBuffer = MPI.newIntBuffer(data.length);
        if (rank == 1)
        {
            FillData(data);
            dataBuffer.put(data);
            MPI.COMM_WORLD.iSend(dataBuffer, data.length, MPI.INT, TO_WORKER, TAG);
        }
        else if (rank == 4)
        {
            var request = MPI.COMM_WORLD.iRecv(dataBuffer, data.length, MPI.INT, FROM_WORKER, TAG);
            request.waitFor();
            data = dataBuffer.array();
        }
        MPI.Finalize();
    }
}
