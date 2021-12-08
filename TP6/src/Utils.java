import Matrix.Matrix;

import java.util.Random;

public class Utils
{
    public static Matrix createRandomMatrix(int height, int width, int from, int to)
    {
        Random random = new Random();
        int[][] data = new int[height][width];
        for (int i = 0; i < height; i++)
        {
            data[i] = new int[width];
            for (int j = 0; j < width; j++)
            {
                data[i][j] = random.nextInt(to - from) + from;
            }
        }
        return new Matrix(data);
    }

    public static void print(Matrix matrix)
    {
        for (int i = 0; i < matrix.getHeight(); i++)
        {
            for (int j = 0; j < matrix.getWidth(); j++)
            {
                System.out.printf("%7d", matrix.get(i, j));
            }
            System.out.println();
        }
        System.out.println();
    }
}
