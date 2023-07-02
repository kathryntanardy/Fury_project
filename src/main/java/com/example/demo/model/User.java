package com.example.demo.model;

import java.util.ArrayList;

import jakarta.persistence.*;

public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;
    private String username;
    private String password;
    private String emailAddress;
    private ArrayList<Float> records;

    public User() {
    }

    public User(String username, String password, String emailAddress, ArrayList<Float> records) {
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
        this.records = records;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public ArrayList<Float> getRecords() {
        return records;
    }

    public void setRecords(ArrayList<Float> records) {
        this.records = records;
    }
}
