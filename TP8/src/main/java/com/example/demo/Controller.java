package com.example.demo;

import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.stereotype.Controller
public class Controller
{

    @RequestMapping("/")
    public String root()
    {
        return "root";
    }

    @RequestMapping("/client")
    public String clientGet()
    {
        return "client";
    }

    @RequestMapping("/server")
    public String server()
    {
        return "server";
    }
}
