package com.example.demo.model;

import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "user_message")
public class userMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mid;
    private int fromUid;
    private String subject;
    private String content;
    private String solved;
    private LocalDate sentDate;
    public userMessage() {
    }

    public userMessage(int fromUid, String subject, String content, String solved, LocalDate sentDate) {
        this.fromUid = fromUid;
        this.subject = subject;
        this.content = content;
        this.solved = solved;
        this.sentDate = sentDate;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getFromUid() {
        return fromUid;
    }

    public void setFromUid(int fromUid) {
        this.fromUid = fromUid;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSolved() {
        return solved;
    }

    public void setSolved(String solved) {
        this.solved = solved;
    }

    public LocalDate getSentDate() {
        return sentDate;
    }

    public void setSentDate(LocalDate sentDate) {
        this.sentDate = sentDate;
    }
    
    
    
}
