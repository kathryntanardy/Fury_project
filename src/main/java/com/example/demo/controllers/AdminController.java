package com.example.demo.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.Admin;
import com.example.demo.model.AdminRepository;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepo;

    @PostMapping("/adminLogin")
    public String adminLogin(@RequestParam Map<String, String> info) {
        // List<Admin> admin = adminRepo.findByUsernameAndPassword(info.get("username"),
        // info.get("password"));

        System.out.println(info.get("username"));

        if (!info.get("username").equals("adminaccount")) {
            return "/admin/failed";
        }
        if (!info.get("password").equals("furygroup18")) {
            return "/admin/failed";
        } else
            return "/admin/dashboard";
    }
}
