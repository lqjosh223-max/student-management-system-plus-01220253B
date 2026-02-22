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

    // Test data - use unique IDs with timestamp to avoid conflicts
    private static final String TEST_ID_PREFIX = "UTEST" + System.currentTimeMillis() % 10000;
    private static String testId1, testId2, testId3, testId4, testId5, findTestId;

    @BeforeAll
    public static void setUp() {
        repository = new SQLiteStudentRepository();
        studentService = new StudentService(repository);

        // Generate unique test IDs
        testId1 = TEST_ID_PREFIX + "001";
        testId2 = TEST_ID_PREFIX + "002";
        testId3 = TEST_ID_PREFIX + "003";
        testId4 = TEST_ID_PREFIX + "004";
        testId5 = TEST_ID_PREFIX + "005";
        findTestId = TEST_ID_PREFIX + "FIND";

        // Clean up any leftover test data from previous runs
        cleanupTestData();
    }

    @BeforeEach
    public void beforeEach() {
        // Ensure clean state before each test
        cleanupTestData();
    }

    private static void cleanupTestData() {
        try {
            // Hard delete for tests (bypass soft delete)
            repository.hardDeleteStudent(testId1);
            repository.hardDeleteStudent(testId2);
            repository.hardDeleteStudent(testId3);
            repository.hardDeleteStudent(testId4);
            repository.hardDeleteStudent(testId5);
            repository.hardDeleteStudent(findTestId);
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }

    @Test
    @Order(1)
    @DisplayName("Test valid student creation")
    public void testValidStudentCreation() {
        Student student = new Student(
                testId1,
                "John Doe",
                "Computer Science",
                200,
                3.5,
                "john@example.com",
                "0244567890",
                LocalDateTime.now(),
                "Active"
        );
        assertDoesNotThrow(() -> studentService.addStudent(student));
    }

    @Test
    @Order(2)
    @DisplayName("Test duplicate student ID rejection")
    public void testDuplicateStudentIdRejection() {
        // First add a student
        Student student1 = new Student(
                testId1,
                "John Doe",
                "Computer Science",
                200,
                3.5,
                "john@example.com",
                "0244567890",
                LocalDateTime.now(),
                "Active"
        );
        studentService.addStudent(student1);

        // Try to add duplicate
        Student student2 = new Student(
                testId1,
                "Jane Doe",
                "Business",
                300,
                3.0,
                "jane@example.com",
                "0244123456",
                LocalDateTime.now(),
                "Active"
        );
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student2));
    }

    @Test
    @Order(3)
    @DisplayName("Test invalid GPA rejection")
    public void testInvalidGpaRejection() {
        Student student = new Student(
                testId2,
                "Invalid GPA",
                "Engineering",
                400,
                5.0,
                "invalid@example.com",
                "0244789012",
                LocalDateTime.now(),
                "Active"
        );
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    @Order(4)
    @DisplayName("Test invalid level rejection")
    public void testInvalidLevelRejection() {
        Student student = new Student(
                testId3,
                "Invalid Level",
                "Mathematics",
                150,
                3.0,
                "level@example.com",
                "0244345678",
                LocalDateTime.now(),
                "Active"
        );
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    @Order(5)
    @DisplayName("Test invalid email rejection")
    public void testInvalidEmailRejection() {
        Student student = new Student(
                testId4,
                "Invalid Email",
                "Physics",
                500,
                3.2,
                "invalid-email",
                "0244901234",
                LocalDateTime.now(),
                "Active"
        );
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    @Order(6)
    @DisplayName("Test invalid phone rejection")
    public void testInvalidPhoneRejection() {
        Student student = new Student(
                testId5,
                "Invalid Phone",
                "Computer Science",
                600,
                3.4,
                "phone@example.com",
                "12345",
                LocalDateTime.now(),
                "Active"
        );
        assertThrows(IllegalArgumentException.class, () -> studentService.addStudent(student));
    }

    @Test
    @Order(7)
    @DisplayName("Test get all students")
    public void testGetAllStudents() {
        List<Student> students = studentService.getAllStudents();
        assertNotNull(students);
    }

    @Test
    @Order(8)
    @DisplayName("Test find student by ID")
    public void testFindStudentById() {
        // Add a test student first
        Student testStudent = new Student(
                findTestId,
                "Find Me",
                "Computer Science",
                200,
                3.5,
                "find@example.com",
                "0244567890",
                LocalDateTime.now(),
                "Active"
        );
        studentService.addStudent(testStudent);

        Student found = studentService.findStudentById(findTestId);
        assertNotNull(found);
        assertEquals(findTestId, found.getStudentId());

        // Cleanup
        repository.hardDeleteStudent(findTestId);
    }

    @Test
    @Order(9)
    @DisplayName("Test top performers report")
    public void testGetTopPerformers() {
        List<Student> top = studentService.getTopPerformers(null, null, 5);
        assertNotNull(top);
        assertTrue(top.size() <= 5);
    }

    @Test
    @Order(10)
    @DisplayName("Test at risk students report")
    public void testGetAtRiskStudents() {
        List<Student> atRisk = studentService.getAtRiskStudents(2.0);
        assertNotNull(atRisk);
        // All returned students should have GPA < 2.0
        for (Student s : atRisk) {
            assertTrue(s.getGpa() < 2.0);
        }
    }

    @Test
    @Order(11)
    @DisplayName("Test GPA distribution")
    public void testGetGpaDistribution() {
        var distribution = studentService.getGpaDistribution();
        assertNotNull(distribution);
        assertTrue(distribution.containsKey("0.0 - 1.0"));
    }

    @Test
    @Order(12)
    @DisplayName("Test programme summary")
    public void testGetProgrammeSummary() {
        var summary = studentService.getProgrammeSummary();
        assertNotNull(summary);
        assertFalse(summary.isEmpty());
    }

    @AfterAll
    public static void tearDown() {
        // Final cleanup
        cleanupTestData();
    }
}