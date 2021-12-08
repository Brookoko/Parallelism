package Matrix;

public class Matrix
{
    private final int[][] data;
    private final int height;
    private final int width;

    public Matrix(int height, int width)
    {
        this.height = height;
        this.width = width;
        data = new int[height][width];
    }

    public Matrix(int[][] data)
    {
        this.data = data;
        height = data.length;
        width = data[0].length;
    }

    public int get(int i, int j)
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

    public int[][] getData()
    {
        return data;
    }

    public int[] getRow(int i)
    {
        return data[i];
    }

    public void setRow(int i, int[] row)
    {
        data[i] = row;
    }
}
