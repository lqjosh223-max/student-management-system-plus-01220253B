package com.sms.domain;

import java.time.LocalDateTime;

public class Student {
    private String studentId;
    private String fullName;
    private String programme;
    private int level;
    private double gpa;
    private String email;
    private String phoneNumber;
    private LocalDateTime dateAdded;
    private String status;
    private String actions; // For "View" button in Actions column

    // Constructor
    public Student(String studentId, String fullName, String programme, int level,
                   double gpa, String email, String phoneNumber, LocalDateTime dateAdded,
                   String status) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.programme = programme;
        this.level = level;
        this.gpa = gpa;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.dateAdded = dateAdded;
        this.status = status;
        this.actions = "View >"; // Default action button text
    }

    // GETTERS
    public String getStudentId() { return studentId; }
    public String getFullName() { return fullName; }
    public String getProgramme() { return programme; }
    public int getLevel() { return level; }
    public double getGpa() { return gpa; }
    public String getEmail() { return email; }
    public String getPhoneNumber() { return phoneNumber; }
    public LocalDateTime getDateAdded() { return dateAdded; }
    public String getStatus() { return status; }
    public String getActions() { return actions; }

    // SETTERS
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setProgramme(String programme) { this.programme = programme; }
    public void setLevel(int level) { this.level = level; }
    public void setGpa(double gpa) { this.gpa = gpa; }
    public void setEmail(String email) { this.email = email; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }
    public void setStatus(String status) { this.status = status; }
    public void setActions(String actions) { this.actions = actions; }

    // JavaFX Property Methods (for TableView binding)
    public javafx.beans.property.StringProperty studentIdProperty() {
        return new javafx.beans.property.SimpleStringProperty(studentId);
    }

    public javafx.beans.property.StringProperty fullNameProperty() {
        return new javafx.beans.property.SimpleStringProperty(fullName);
    }

    public javafx.beans.property.StringProperty programmeProperty() {
        return new javafx.beans.property.SimpleStringProperty(programme);
    }

    public javafx.beans.property.IntegerProperty levelProperty() {
        return new javafx.beans.property.SimpleIntegerProperty(level);
    }

    public javafx.beans.property.DoubleProperty gpaProperty() {
        return new javafx.beans.property.SimpleDoubleProperty(gpa);
    }

    public javafx.beans.property.StringProperty emailProperty() {
        return new javafx.beans.property.SimpleStringProperty(email);
    }

    public javafx.beans.property.StringProperty phoneNumberProperty() {
        return new javafx.beans.property.SimpleStringProperty(phoneNumber);
    }

    public javafx.beans.property.StringProperty statusProperty() {
        return new javafx.beans.property.SimpleStringProperty(status);
    }

    public javafx.beans.property.StringProperty actionsProperty() {
        return new javafx.beans.property.SimpleStringProperty(actions);
    }
}