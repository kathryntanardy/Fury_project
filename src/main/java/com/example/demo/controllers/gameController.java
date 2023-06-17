package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class gameController {
    
    @GetMapping("/canvas")
    public String toReady(){
        return "/testGame/ready";
    }


    @GetMapping("/ready")
    public String setTest(){
        return "/testGame/start";
    }

}
