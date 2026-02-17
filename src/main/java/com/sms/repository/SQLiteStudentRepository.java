package com.sms.repository;

import com.sms.domain.Student;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// This class actually talks to the SQLite database
public class SQLiteStudentRepository implements StudentRepository {

    private Connection connection;

    // Constructor - runs when we create this object
    public SQLiteStudentRepository() {
        try {
            // Create data folder if missing
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("data"));

            // Connect to database (this will create students.db automatically)
            connection = DriverManager.getConnection("jdbc:sqlite:data/students.db");

            // Create table with CHECK constraints (required by assignment Section 7)
            createTable();

            System.out.println(" Database ready!");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    // Create the students table with CHECK constraints
    private void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS students (
                student_id TEXT PRIMARY KEY,
                full_name TEXT NOT NULL,
                programme TEXT NOT NULL,
                level INTEGER NOT NULL CHECK (level IN (100, 200, 300, 400, 500, 600, 700)),
                gpa REAL NOT NULL CHECK (gpa >= 0.0 AND gpa <= 4.0),
                email TEXT NOT NULL,
                phone_number TEXT NOT NULL,
                date_added TEXT NOT NULL,
                status TEXT NOT NULL CHECK (status IN ('Active', 'Inactive'))
            )
            """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println(" Table created Successfully");
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
            throw new RuntimeException("Failed to create database table", e);
        }
    }

    @Override
    public void addStudent(Student student) {
        String sql = """
            INSERT INTO students (student_id, full_name, programme, level, gpa, 
                                 email, phone_number, date_added, status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getStudentId());
            pstmt.setString(2, student.getFullName());
            pstmt.setString(3, student.getProgramme());
            pstmt.setInt(4, student.getLevel());
            pstmt.setDouble(5, student.getGpa());
            pstmt.setString(6, student.getEmail());
            pstmt.setString(7, student.getPhoneNumber());
            pstmt.setString(8, student.getDateAdded().toString());
            pstmt.setString(9, student.getStatus());

            pstmt.executeUpdate();
            System.out.println(" Student added successfully");

        } catch (SQLException e) {
            System.err.println("Error adding student: " + e.getMessage());
            throw new RuntimeException("Failed to add student", e);
        }
    }

    @Override
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY full_name ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Student student = new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        LocalDateTime.parse(rs.getString("date_added")),
                        rs.getString("status")
                );
                students.add(student);
            }

        } catch (SQLException e) {
            System.err.println("Error getting students: " + e.getMessage());
            throw new RuntimeException("Failed to get students", e);
        }

        return students;
    }

    @Override
    public Student findStudentById(String studentId) {
        String sql = "SELECT * FROM students WHERE student_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Student(
                        rs.getString("student_id"),
                        rs.getString("full_name"),
                        rs.getString("programme"),
                        rs.getInt("level"),
                        rs.getDouble("gpa"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        LocalDateTime.parse(rs.getString("date_added")),
                        rs.getString("status")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error finding student: " + e.getMessage());
            throw new RuntimeException("Failed to find student", e);
        }

        return null; // Not found
    }

    @Override
    public void updateStudent(Student student) {
        String sql = """
            UPDATE students 
            SET full_name = ?, programme = ?, level = ?, gpa = ?, 
                email = ?, phone_number = ?, status = ?
            WHERE student_id = ?
            """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, student.getFullName());
            pstmt.setString(2, student.getProgramme());
            pstmt.setInt(3, student.getLevel());
            pstmt.setDouble(4, student.getGpa());
            pstmt.setString(5, student.getEmail());
            pstmt.setString(6, student.getPhoneNumber());
            pstmt.setString(7, student.getStatus());
            pstmt.setString(8, student.getStudentId());

            pstmt.executeUpdate();
            System.out.println(" Student updated successfully");

        } catch (SQLException e) {
            System.err.println("Error updating student: " + e.getMessage());
            throw new RuntimeException("Failed to update student", e);
        }
    }

    @Override
    public void deleteStudent(String studentId) {
        // Soft delete - mark as "Inactive" instead of deleting
        String sql = "UPDATE students SET status = 'Inactive' WHERE student_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.executeUpdate();
            System.out.println(" Student marked as inactive");

        } catch (SQLException e) {
            System.err.println("Error deleting student: " + e.getMessage());
            throw new RuntimeException("Failed to delete student", e);
        }
    }
}