package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.User;
import com.example.demo.model.UserRepository;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @GetMapping("/signUp")
    public String signUp(@RequestParam Map<String, String> account, HttpServletResponse response, Model model) {
        List<User> alreadyExist = userRepo.findByUsername(account.get("username"));
        if (alreadyExist.isEmpty()) {
            String username = account.get("username");
            String password = account.get("password");
            userRepo.save(new User(username, password));
            response.setStatus(201);
            return "/user/signin";
        }
        return "/user/usernameTaken";
    }

    @PostMapping("/login")
    public String login(@RequestParam Map<String, String> info) {
        List<User> user = userRepo.findByUsernameAndPassword(info.get("username"), info.get("password"));
        if (user.isEmpty()) {
            return "/user/loginFailed";
        }
        return "user/game";
    }

}
