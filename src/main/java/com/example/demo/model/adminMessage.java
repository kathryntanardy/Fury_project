package com.example.demo.model;
import jakarta.persistence.*;

@Entity
@Table(name = "admin_message")
public class adminMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int mid;
    private int toUid;
    private String subject;
    private String content;
    
    public adminMessage() {
    }

    public adminMessage(int toUid, String subject, String content) {
        this.toUid = toUid;
        this.subject = subject;
        this.content = content;
    }

    public int getMid() {
        return mid;
    }

    public void setMid(int mid) {
        this.mid = mid;
    }

    public int getToUid() {
        return toUid;
    }

    public void setToUid(int toUid) {
        this.toUid = toUid;
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
    

}
