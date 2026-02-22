package com.sms.service;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import org.junit.jupiter.api.*;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentServiceTest {
    
    private static StudentService studentService;
    private static SQLiteStudentRepository repository;
    
    @BeforeAll
    public static void setUp() {
        repository = new SQLiteStudentRepository();
        studentService = new StudentService(repository);
    }
    
    @Test @Order(1) @DisplayName("Test valid student creation")
    public void testValidStudentCreation() {
        Student student = new Student("TEST001", "John Doe", "Computer Science", 
                                     200, 3.5, "john@example.com", "0244567890", 
                                     LocalDateTime.now(), "Active");
        assertDoesNotThrow(() -> studentService.addStudent(student));
    }
    
    @Test @Order(2) @DisplayName("Test duplicate student ID rejection")
    public void testDuplicateStudentIdRejection() {
        Student student = new Student("TEST001", "Jane Doe", "Business", 
                                     300, 3.0, "jane@example.com", "0244123456", 
                                     LocalDateTime.now(), "Active");
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }
    
    @Test @Order(3) @DisplayName("Test invalid GPA rejection")
    public void testInvalidGpaRejection() {
        Student student = new Student("TEST002", "Invalid", "Engineering", 
                                     400, 5.0, "invalid@example.com", "0244789012", 
                                     LocalDateTime.now(), "Active");
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }
    
    @Test @Order(4) @DisplayName("Test invalid level rejection")
    public void testInvalidLevelRejection() {
        Student student = new Student("TEST003", "Invalid", "Mathematics", 
                                     150, 3.0, "level@example.com", "0244345678", 
                                     LocalDateTime.now(), "Active");
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }
    
    @Test @Order(5) @DisplayName("Test invalid email rejection")
    public void testInvalidEmailRejection() {
        Student student = new Student("TEST004", "Invalid", "Physics", 
                                     500, 3.2, "invalid-email", "0244901234", 
                                     LocalDateTime.now(), "Active");
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }
    
    @Test @Order(6) @DisplayName("Test invalid phone rejection")
    public void testInvalidPhoneRejection() {
        Student student = new Student("TEST005", "Invalid", "Computer Science", 
                                     600, 3.4, "phone@example.com", "12345", 
                                     LocalDateTime.now(), "Active");
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }
    
    @Test @Order(7) @DisplayName("Test get all students")
    public void testGetAllStudents() {
        List<Student> students = studentService.getAllStudents();
        assertNotNull(students);
    }
    
    @Test @Order(8) @DisplayName("Test find student by ID")
    public void testFindStudentById() {
        Student student = studentService.findStudentById("TEST001");
        assertNotNull(student);
        assertEquals("TEST001", student.getStudentId());
    }
    
    @Test @Order(9) @DisplayName("Test top performers report")
    public void testGetTopPerformers() {
        List<Student> top = studentService.getTopPerformers(null, null, 5);
        assertNotNull(top);
        assertTrue(top.size() <= 5);
    }
    
    @Test @Order(10) @DisplayName("Test at risk students report")
    public void testGetAtRiskStudents() {
        List<Student> atRisk = studentService.getAtRiskStudents(2.0);
        assertNotNull(atRisk);
        // All returned students should have GPA < 2.0
        for (Student s : atRisk) {
            assertTrue(s.getGpa() < 2.0);
        }
    }
    
    @Test @Order(11) @DisplayName("Test GPA distribution")
    public void testGetGpaDistribution() {
        var distribution = studentService.getGpaDistribution();
        assertNotNull(distribution);
        assertTrue(distribution.containsKey("0.0 - 1.0"));
    }
    
    @Test @Order(12) @DisplayName("Test programme summary")
    public void testGetProgrammeSummary() {
        var summary = studentService.getProgrammeSummary();
        assertNotNull(summary);
        assertFalse(summary.isEmpty());
    }
    
    @AfterAll
    public static void tearDown() {
        // Clean up test data
        try {
            repository.deleteStudent("TEST001");
            repository.deleteStudent("TEST002");
            repository.deleteStudent("TEST003");
            repository.deleteStudent("TEST004");
            repository.deleteStudent("TEST005");
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}