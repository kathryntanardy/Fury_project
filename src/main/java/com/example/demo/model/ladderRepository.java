package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ladderRepository extends JpaRepository<ladderBoard, Integer> {
    List<ladderBoard> findByUid(int uid);
}
