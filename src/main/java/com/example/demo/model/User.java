package com.example.demo.model;

import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
@Table(name = "User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int uid;
    private String username;
    private String password;
    private String emailAddress;
    private ArrayList<Float> records;
    private float averageRecord;
    private float bestRecord;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.emailAddress = "";
        this.records = new ArrayList<Float>();
        this.averageRecord = 0;
        this.bestRecord = 0;
    }

    public User(String username, String password, String emailAddress, ArrayList<Float> records, float averageRecord,
            float bestRecord) {
        this.username = username;
        this.password = password;
        this.emailAddress = emailAddress;
        this.records = records;
        this.averageRecord = averageRecord;
        this.bestRecord = bestRecord;
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

    public float getAverageRecord() {
        return averageRecord;
    }

    public void setAverageRecord(float averageRecord) {
        this.averageRecord = averageRecord;
    }

    public float getBestRecord() {
        return bestRecord;
    }

    public void setBestRecord(float bestRecord) {
        this.bestRecord = bestRecord;
    }
}
