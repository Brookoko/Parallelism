package com.multiply;

import com.multiply.matrix.Matrix;
import com.multiply.matrix.MatrixMultiplication;
import com.multiply.matrix.PerformanceChecker;
import com.multiply.matrix.Utils;
import com.multiply.matrix.linear.LinearMatrixMultiplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiplicationController
{
    private MatrixMultiplication multiplication = new LinearMatrixMultiplication(100);
    private PerformanceChecker performanceChecker = new PerformanceChecker(multiplication);

    @PostMapping("/multiply-client")
    public MatrixResponse client(@RequestBody MatrixRequest request) throws Exception
    {
        var matrixA = new Matrix(request.matrixA);
        var matrixB = new Matrix(request.matrixB);
        var time = performanceChecker.countTimeFor(matrixA, matrixB);
        return new MatrixResponse(time);
    }

    @PostMapping("/multiply-server")
    public MatrixResponse server(@RequestBody MatrixRequest request) throws Exception
    {
        var matrixA = Utils.createRandomMatrix(request.size);
        var matrixB = Utils.createRandomMatrix(request.size);
        var time = performanceChecker.countTimeFor(matrixA, matrixB);
        return new MatrixResponse(time);
    }
}
