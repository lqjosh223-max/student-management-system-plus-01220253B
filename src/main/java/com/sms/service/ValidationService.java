package com.sms.service;

// This class handles ALL validation rules
public class ValidationService {
    
    // Validate Student ID: 4-20 alphanumeric characters
    public static boolean isValidStudentId(String id) {
        if (id == null) return false;
        return id.matches("^[a-zA-Z0-9]{4,20}$");
    }
    
    // Validate Full Name: 2-60 characters, no numbers
    public static boolean isValidFullName(String name) {
        if (name == null) return false;
        return name.length() >= 2 && name.length() <= 60 
               && !name.matches(".*\\d.*"); // no digits allowed
    }
    
    // Validate Level: must be 100, 200, 300, 400, 500, 600, or 700
    public static boolean isValidLevel(int level) {
        return level == 100 || level == 200 || level == 300 || 
               level == 400 || level == 500 || level == 600 || level == 700;
    }
    
    // Validate GPA: between 0.0 and 4.0
    public static boolean isValidGpa(double gpa) {
        return gpa >= 0.0 && gpa <= 4.0;
    }
    
    // Validate Email: basic format check
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        return email.contains("@") && email.contains(".");
    }
    
    // Validate Phone: 10-15 digits only
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        return phone.matches("^\\d{10,15}$");
    }
    
    // Validate Status: "Active" or "Inactive"
    public static boolean isValidStatus(String status) {
        return "Active".equals(status) || "Inactive".equals(status);
    }
}