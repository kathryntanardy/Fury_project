package com.example.demo.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByUsernameAndPassword(String username, String password);

    List<User> findByUsername(String username);

    List<User> findByUid(int uid);

    List<User> findByEmailAddress(String emailAddress);
}
