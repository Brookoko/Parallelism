package Matrix.Simple;

import Matrix.Matrix;
import Matrix.MatrixMultiplication;

public class SimpleMatrixMultiplication extends MatrixMultiplication
{
    @Override
    public void multiply(Matrix a, Matrix b, Matrix c)
    {
        var height = a.getHeight();
        var width = b.getHeight();

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                int value = 0;
                for (int k = 0; k < b.getHeight(); k++)
                {
                    value += a.get(i, k) * b.get(k, j);
                }
                c.set(i, j, value);
            }
        }
    }
}
