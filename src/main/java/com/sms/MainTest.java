package com.sms;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import java.time.LocalDateTime;

public class MainTest {
    public static void main(String[] args) {
        try {
            // Create repository
            SQLiteStudentRepository repo = new SQLiteStudentRepository();

            // Create test student
            Student student = new Student(
                    "TEST001",
                    "Test Student",
                    "Computer Science",
                    200,
                    3.5,
                    "test@example.com",
                    "1234567890",
                    LocalDateTime.now(),
                    "Active"
            );

            // Add to database
            repo.addStudent(student);
            System.out.println(" Student added!");

            // Read back
            Student found = repo.findStudentById("TEST001");
            if (found != null) {
                System.out.println(" Found student: " + found.getFullName());
            } else {
                System.out.println(" Student not found!");
            }

        } catch (Exception e) {
            System.err.println(" Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}