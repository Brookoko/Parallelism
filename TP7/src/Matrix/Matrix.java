package Matrix;

public class Matrix
{
    private final double[][] data;
    private final int height;
    private final int width;

    public Matrix(int height, int width)
    {
        this.height = height;
        this.width = width;
        data = new double[height][width];
    }

    public Matrix(double[][] data)
    {
        this.data = data;
        height = data.length;
        width = data[0].length;
    }

    public double get(int i, int j)
    {
        return data[i][j];
    }

    public void set(int i, int j, int value)
    {
        data[i][j] = value;
    }

    public int getHeight()
    {
        return height;
    }

    public int getWidth()
    {
        return width;
    }

    public double[][] getData()
    {
        return data;
    }

    public double[] getRow(int i)
    {
        return data[i];
    }

    public void setRow(int i, double[] row)
    {
        data[i] = row;
    }

    public void setData(double[][] data)
    {
        for (var i = 0; i < height; i++)
        {
            for (var j = 0; j < width; j++)
            {
                this.data[i][j] = data[i][j];
            }
        }
    }
}
