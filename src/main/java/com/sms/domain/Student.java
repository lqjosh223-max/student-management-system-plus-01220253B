package com.sms.domain;

import java.time.LocalDateTime;

public class Student {

    // private fields to store data :
    private String studentID;
    private String fullName;
    private String programme;
    private int level;
    private double gpa;
    private String email;
    private String phoneNumber;
    private LocalDateTime dateAdded;
    private String status; // to show if the student is active or inactive

// Constructor for creating new Student Object

public Student (String studentID, String fullName, String programme, int level, double gpa,
                String email, String phoneNumber, LocalDateTime dateAdded, String status){
    this.studentID = studentID;
    this.fullName = fullName;
    this.programme = programme;
    this.level = level;
    this.gpa = gpa;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.dateAdded = dateAdded;
    this.status = status;
}
// Getter methods to let us read data
    public String getStudentId(){
        return studentID;
    }

    public String getFullName(){
        return fullName;
    }

    public String getProgramme() {
        return programme;
    }

    public int getLevel() {
        return level;
    }

    public double getGpa() {
        return gpa;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public String getStatus() {
        return status;
    }

    // Setter method to let us change data


    public void setStudentID(String studentID) {
        this.studentID = studentID;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}