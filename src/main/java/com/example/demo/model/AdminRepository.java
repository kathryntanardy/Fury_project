package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    List<Admin> findByUsernameAndPassword(String username, String password);
    List<Admin> findByUsername(String username);
}
