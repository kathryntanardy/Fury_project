package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface activeUsersRepository extends JpaRepository<activeUsers, Integer>{
    List<activeUsers> findByYear(int year);
}
