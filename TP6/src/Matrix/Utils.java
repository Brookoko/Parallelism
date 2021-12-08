package Matrix;

import Matrix.Matrix;

import java.util.Random;

public class Utils
{
    public static Matrix createRandomMatrix(int height, int width, int from, int to)
    {
        Random random = new Random();
        double[][] data = new double[height][width];
        for (int i = 0; i < height; i++)
        {
            data[i] = new double[width];
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
                System.out.printf("%.1f ", matrix.get(i, j));
            }
            System.out.println();
        }
        System.out.println();
    }

    public static double[] Flatten(double[][] matrix)
    {
        var result = new double[matrix.length * matrix[0].length];
        for (var i = 0; i < matrix.length; i++)
        {
            for (var j = 0; j < matrix[i].length; j++)
            {
                result[i * matrix[0].length + j] = matrix[i][j];
            }
        }
        return result;
    }

    public static double[][] Nest(double[] array, int height, int width)
    {
        var result = new double[height][width];
        for (var i = 0; i < height; i++)
        {
            for (var j = 0; j < width; j++)
            {
                result[i][j] = array[i * width + j];
            }
        }
        return result;
    }
}
