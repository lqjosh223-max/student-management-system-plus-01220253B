package com.sms.service;

import com.sms.domain.Student;
import com.sms.repository.StudentRepository;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

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

    public List<Student> getAllStudents() {
        return repository.getAllStudents();
    }
    // Add to StudentService.java (after getAllStudents method)

    // Top 10 performers by GPA
    public List<Student> getTopPerformers(String programmeFilter, Integer levelFilter, int limit) {
        return repository.getAllStudents().stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .filter(s -> programmeFilter == null || programmeFilter.equals("All") || s.getProgramme().equals(programmeFilter))
                .filter(s -> levelFilter == null || s.getLevel() == levelFilter)
                .sorted(Comparator.comparingDouble(Student::getGpa).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    // At-risk students (GPA below threshold)
    public List<Student> getAtRiskStudents(double threshold) {
        return repository.getAllStudents().stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .filter(s -> s.getGpa() < threshold)
                .sorted(Comparator.comparingDouble(Student::getGpa))
                .collect(Collectors.toList());
    }

    // GPA distribution bands
    public Map<String, Long> getGpaDistribution() {
        List<Student> activeStudents = repository.getAllStudents().stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .collect(Collectors.toList());

        Map<String, Long> distribution = new LinkedHashMap<>();
        distribution.put("0.0 - 1.0", activeStudents.stream().filter(s -> s.getGpa() >= 0.0 && s.getGpa() < 1.0).count());
        distribution.put("1.0 - 2.0", activeStudents.stream().filter(s -> s.getGpa() >= 1.0 && s.getGpa() < 2.0).count());
        distribution.put("2.0 - 3.0", activeStudents.stream().filter(s -> s.getGpa() >= 2.0 && s.getGpa() < 3.0).count());
        distribution.put("3.0 - 4.0", activeStudents.stream().filter(s -> s.getGpa() >= 3.0 && s.getGpa() <= 4.0).count());

        return distribution;
    }

    // Programme summary
    public Map<String, ProgrammeStats> getProgrammeSummary() {
        Map<String, List<Student>> grouped = repository.getAllStudents().stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .collect(Collectors.groupingBy(Student::getProgramme));

        Map<String, ProgrammeStats> summary = new LinkedHashMap<>();
        for (Map.Entry<String, List<Student>> entry : grouped.entrySet()) {
            String programme = entry.getKey();
            List<Student> students = entry.getValue();
            double avgGpa = students.stream().mapToDouble(Student::getGpa).average().orElse(0.0);
            summary.put(programme, new ProgrammeStats(students.size(), avgGpa));
        }
        return summary;
    }

    // Helper class for programme stats
    public static class ProgrammeStats {
        private final int studentCount;
        private final double averageGpa;

        public ProgrammeStats(int studentCount, double averageGpa) {
            this.studentCount = studentCount;
            this.averageGpa = averageGpa;
        }

        public int getStudentCount() { return studentCount; }
        public double getAverageGpa() { return averageGpa; }
    }

    public Student findStudentById(String studentId) {
        return repository.findStudentById(studentId);
    }
}