import Matrix.Fox.FoxMatrixMultiplication;
import Matrix.Linear.LinearMatrixMultiplication;
import Matrix.MatrixMultiplication;
import Matrix.Simple.SimpleMatrixMultiplication;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        MatrixMultiplication multiplication = new SimpleMatrixMultiplication();
        MatrixMultiplication linearMultiplication = new LinearMatrixMultiplication(100);
        MatrixMultiplication foxMultiplication = new FoxMatrixMultiplication(100, 100);
        PerformanceChecker performanceChecker = new PerformanceChecker(foxMultiplication);
        double time = performanceChecker.countTimeRepeated(10000, 10000, 1000);
        System.out.printf("Time: %.4f\n", time);
    }
}