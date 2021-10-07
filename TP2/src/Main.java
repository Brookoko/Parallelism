import Matrix.Fox.FoxMatrixMultiplication;
import Matrix.Linear.LinearMatrixMultiplication;
import Matrix.MatrixMultiplication;
import Matrix.Simple.SimpleMatrixMultiplication;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MatrixMultiplication multiplication = new SimpleMatrixMultiplication();
        MatrixMultiplication linearMultiplication = new LinearMatrixMultiplication(200);
        MatrixMultiplication foxMultiplication = new FoxMatrixMultiplication(200, 200);
        PerformanceChecker performanceChecker = new PerformanceChecker(foxMultiplication);
        double[] times = performanceChecker.countTimeRepeated(1000, 5000, 1000);
        for (double time : times)
        {
            System.out.println(time);
        }
    }
}