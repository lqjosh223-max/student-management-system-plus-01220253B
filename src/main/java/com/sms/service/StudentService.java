package com.sms.service;

import com.sms.domain.Student;
import com.sms.repository.StudentRepository;

// This class handles business operations with validation
public class StudentService {
    
    private final StudentRepository repository;
    
    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }
    
    // Add student with full validation
    public void addStudent(Student student) {
        // Validate Student ID: 4-20 alphanumeric characters
        if (!ValidationService.isValidStudentId(student.getStudentId())) {
            throw new IllegalArgumentException(
                "Invalid Student ID: Must be 4-20 alphanumeric characters");
        }
        
        // Validate Full Name: 2-60 characters, no numbers
        if (!ValidationService.isValidFullName(student.getFullName())) {
            throw new IllegalArgumentException(
                "Invalid Full Name: Must be 2-60 characters with no numbers");
        }
        
        // Validate Level: must be 100, 200, 300, 400, 500, 600, or 700
        if (!ValidationService.isValidLevel(student.getLevel())) {
            throw new IllegalArgumentException(
                "Invalid Level: Must be 100, 200, 300, 400, 500, 600, or 700");
        }
        
        // Validate GPA: between 0.0 and 4.0
        if (!ValidationService.isValidGpa(student.getGpa())) {
            throw new IllegalArgumentException(
                "Invalid GPA: Must be between 0.0 and 4.0");
        }
        
        // Validate Email: basic format check
        if (!ValidationService.isValidEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                "Invalid Email: Must contain @ and .");
        }
        
        // Validate Phone: 10-15 digits only
        if (!ValidationService.isValidPhoneNumber(student.getPhoneNumber())) {
            throw new IllegalArgumentException(
                "Invalid Phone: Must be 10-15 digits");
        }
        
        // Validate Status: "Active" or "Inactive"
        if (!ValidationService.isValidStatus(student.getStatus())) {
            throw new IllegalArgumentException(
                "Invalid Status: Must be 'Active' or 'Inactive'");
        }
        
        // Check for duplicate Student ID
        if (repository.findStudentById(student.getStudentId()) != null) {
            throw new IllegalArgumentException(
                "Student ID already exists: " + student.getStudentId());
        }
        
        // All validation passed - save to database
        repository.addStudent(student);
    }
    
    // Update student with full validation (no duplicate check needed)
    public void updateStudent(Student student) {
        // Validate Student ID: 4-20 alphanumeric characters
        if (!ValidationService.isValidStudentId(student.getStudentId())) {
            throw new IllegalArgumentException(
                "Invalid Student ID: Must be 4-20 alphanumeric characters");
        }
        
        // Validate Full Name: 2-60 characters, no numbers
        if (!ValidationService.isValidFullName(student.getFullName())) {
            throw new IllegalArgumentException(
                "Invalid Full Name: Must be 2-60 characters with no numbers");
        }
        
        // Validate Level: must be 100, 200, 300, 400, 500, 600, or 700
        if (!ValidationService.isValidLevel(student.getLevel())) {
            throw new IllegalArgumentException(
                "Invalid Level: Must be 100, 200, 300, 400, 500, 600, or 700");
        }
        
        // Validate GPA: between 0.0 and 4.0
        if (!ValidationService.isValidGpa(student.getGpa())) {
            throw new IllegalArgumentException(
                "Invalid GPA: Must be between 0.0 and 4.0");
        }
        
        // Validate Email: basic format check
        if (!ValidationService.isValidEmail(student.getEmail())) {
            throw new IllegalArgumentException(
                "Invalid Email: Must contain @ and .");
        }
        
        // Validate Phone: 10-15 digits only
        if (!ValidationService.isValidPhoneNumber(student.getPhoneNumber())) {
            throw new IllegalArgumentException(
                "Invalid Phone: Must be 10-15 digits");
        }
        
        // Validate Status: "Active" or "Inactive"
        if (!ValidationService.isValidStatus(student.getStatus())) {
            throw new IllegalArgumentException(
                "Invalid Status: Must be 'Active' or 'Inactive'");
        }
        
        // Check if student exists (can't update non-existent student)
        if (repository.findStudentById(student.getStudentId()) == null) {
            throw new IllegalArgumentException(
                "Student not found: " + student.getStudentId());
        }
        
        // All validation passed - update in database
        repository.updateStudent(student);
    }
}