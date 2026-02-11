package com.sms.repository;

import com.sms.domain.Student;
import java.util.List;

// This interface defines what our database can do
public interface StudentRepository {
    
    // Add a new student to the database
    void addStudent(Student student);
    
    // Get all students from the database
    List<Student> getAllStudents();
    
    // Find one student by their ID
    Student findStudentById(String studentId);
    
    // Update an existing student
    void updateStudent(Student student);
    
    // Delete a student (we'll mark as inactive instead of deleting)
    void deleteStudent(String studentId);
}