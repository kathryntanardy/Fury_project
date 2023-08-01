package com.example.demo.model;

import java.util.ArrayList;

import jakarta.persistence.*;

@Entity
@Table(name = "active_users")
public class activeUsers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int year;
    private ArrayList<Integer> monthUserNum;

    public activeUsers() {
    }

    public activeUsers(int year) {
        this.year = year;
        this.monthUserNum=new ArrayList<>();
        for(int i=0;i<12;i++){
            this.monthUserNum.add(0);
        }
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public ArrayList<Integer> getMonthUserNum() {
        return monthUserNum;
    }

    public void addMonthUserNum(int month){
        this.monthUserNum.set(month, this.monthUserNum.get(month-1)+1);
    }
    
}
