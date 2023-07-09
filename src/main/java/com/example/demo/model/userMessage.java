package com.example.demo.model;

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
    private int solved;
    public userMessage() {
    }


    public userMessage(int fromUid, String subject, String content, int solved) {
        this.fromUid = fromUid;
        this.subject = subject;
        this.content = content;
        this.solved = solved;
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

    public int getSolved() {
        return solved;
    }

    public void setSolved(int solved) {
        this.solved = solved;
    }
    
    
    
}
