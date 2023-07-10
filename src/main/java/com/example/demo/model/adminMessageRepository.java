package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface adminMessageRepository extends JpaRepository<adminMessage, Integer>{
    List<adminMessage> findByToUid(int uid);
    List<adminMessage> findByMid(int mid);
}
