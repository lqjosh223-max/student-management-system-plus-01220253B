package com.sms;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import java.time.LocalDateTime;

// Simple test class to verify Service + Repository layers work
public class MainTest {
    public static void main(String[] args) {
        try {
            // Setup database and service
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            StudentService service = new StudentService(repo);

            System.out.println("=== TESTING VALID STUDENT ADDITION ===");

            // Test 1: Add VALID student
            Student validStudent = new Student(
                    "CS2024001",      // Valid ID (4-20 alphanumeric)
                    "Alice Johnson",   // Valid name (2-60 chars, no numbers)
                    "Computer Science",
                    200,              // Valid level
                    3.8,              // Valid GPA (0.0-4.0)
                    "alice@example.com", // Valid email
                    "1234567890",     // Valid phone (10-15 digits)
                    LocalDateTime.now(),
                    "Active"          // Valid status
            );

            service.addStudent(validStudent);
            System.out.println(" Valid student added successfully!");

            // Verify it was saved
            Student found = repo.findStudentById("CS2024001");
            if (found != null) {
                System.out.println(" Verified in database: " + found.getFullName());
            }

            System.out.println("\n=== TESTING INVALID STUDENT ADDITION ===");

            // Test 2: Try to add INVALID student (should fail)
            try {
                Student invalidStudent = new Student(
                        "CS1",            // Too short! (needs 4+ chars)
                        "Alice123",       // Has numbers! (not allowed)
                        "CS",
                        150,              // Invalid level!
                        5.0,              // Invalid GPA! (>4.0)
                        "bad-email",      // Invalid email! (no @ or .)
                        "123",            // Too short! (needs 10+ digits)
                        LocalDateTime.now(),
                        "Deleted"         // Invalid status!
                );

                service.addStudent(invalidStudent);
                System.out.println(" ERROR: Should have thrown exception!");

            } catch (IllegalArgumentException e) {
                System.out.println(" Validation caught error: " + e.getMessage());
            }

            System.out.println("\n=== TESTING DUPLICATE STUDENT ID ===");

            // Test 3: Try to add duplicate student ID (should fail)
            try {
                Student duplicateStudent = new Student(
                        "CS2024001",      // Same ID as before!
                        "Bob Smith",
                        "Business",
                        300,
                        3.2,
                        "bob@example.com",
                        "0987654321",
                        LocalDateTime.now(),
                        "Active"
                );

                service.addStudent(duplicateStudent);
                System.out.println(" ERROR: Should have blocked duplicate ID!");

            } catch (IllegalArgumentException e) {
                System.out.println(" Duplicate ID prevented: " + e.getMessage());
            }

            System.out.println("\n=== TESTING STUDENT UPDATE ===");

            // Test 4: Update existing student
            try {
                Student updatedStudent = new Student(
                        "CS2024001",      // Same ID (must exist)
                        "Alice Smith",    // Updated name
                        "Computer Science",
                        300,              // Updated level
                        3.9,              // Updated GPA
                        "alice.smith@example.com", // Updated email
                        "0987654321",     // Updated phone
                        LocalDateTime.now(),
                        "Active"          // Same status
                );

                service.updateStudent(updatedStudent);
                System.out.println(" Student updated successfully!");

                // Verify update
                Student updated = repo.findStudentById("CS2024001");
                if (updated != null && updated.getLevel() == 300) {
                    System.out.println(" Verified update: Level = " + updated.getLevel());
                }

            } catch (Exception e) {
                System.err.println(" Update failed: " + e.getMessage());
            }

            System.out.println("\n=== TESTING UPDATE NON-EXISTENT STUDENT ===");

            // Test 5: Try to update non-existent student (should fail)
            try {
                Student ghostStudent = new Student(
                        "GHOST001",       // Doesn't exist!
                        "Ghost Student",
                        "Physics",
                        400,
                        3.5,
                        "ghost@example.com",
                        "1111111111",
                        LocalDateTime.now(),
                        "Active"
                );

                service.updateStudent(ghostStudent);
                System.out.println(" ERROR: Should have blocked non-existent update!");

            } catch (IllegalArgumentException e) {
                System.out.println(" Non-existent student update prevented: " + e.getMessage());
            }

            System.out.println("\n ALL TESTS COMPLETED SUCCESSFULLY!");

        } catch (Exception e) {
            System.err.println(" UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
}