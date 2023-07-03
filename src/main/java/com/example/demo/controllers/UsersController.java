package com.example.demo.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;

import jakarta.servlet.http.HttpServletResponse;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;
    
    @GetMapping("/signUp")
    public String signUp(@RequestParam Map<String, String> account, HttpServletResponse response) {
        String username = account.get("username");
        String password = account.get("password");
        userRepo.save(new User(username, password));
        response.setStatus(201);
        // add an endpoint
        return ""; 
    }    
}
