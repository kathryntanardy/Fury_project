package com.example.demo.model;

import java.time.LocalDate;
import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
@Table(name = "ladder_board")
public class ladderBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int uid;
    private String username;
    private float averageWPM;
    private int rank;

    public ladderBoard() {
    }

    public ladderBoard(int uid, String username, float averageWPM) {
        this.uid = uid;
        this.username = username;
        this.averageWPM = averageWPM;
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

    public float getAverageWPM() {
        return averageWPM;
    }

    public void setAverageWPM(float averageWPM) {
        this.averageWPM = averageWPM;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
    
    
}
