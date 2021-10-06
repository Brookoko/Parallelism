package Matrix.Simple;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;

public class SimpleMatrixMultiplication extends MatrixMultiplication
{
    @Override
    public Matrix multiply(Matrix a, Matrix b)
    {
        var height = a.getHeight();
        var width = b.getHeight();
        Matrix result = new Matrix(height, width);

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                int value = 0;
                for (int k = 0; k < b.getHeight(); k++)
                {
                    value += a.get(i, k) * b.get(k, j);
                }
                result.set(i, j, value);
            }
        }

        return result;
    }
}
