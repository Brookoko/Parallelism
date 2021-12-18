package com.example.demo;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MultiplicationController
{
    @PostMapping("/multiply-client")
    public MatrixResponse client(@RequestBody MatrixRequest request)
    {
        return new MatrixResponse(request.size);
    }

    @PostMapping("/multiply-server")
    public MatrixResponse server(@RequestBody MatrixRequest request)
    {
        return new MatrixResponse(request.size);
    }
}
