package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface userMessageRepository extends JpaRepository<userMessage, Integer> {
    List<userMessage> findBySolved(int Solved);
}
