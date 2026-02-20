package com.sms.service;

import javafx.collections.ObservableList;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

// This class handles ALL validation rules
// Supports both fixed defaults (assignment requirements) and dynamic lists (Settings screen)
public class ValidationService {

    // Default valid levels (per assignment Section 6)
    private static final int[] DEFAULT_LEVELS = {100, 200, 300, 400, 500, 600, 700};

    // Default valid programmes (can be extended via Settings)
    private static final String[] DEFAULT_PROGRAMMES = {
            "Computer Science", "Business Administration", "Engineering",
            "Mathematics", "Physics"
    };

    // ==================== STUDENT ID ====================

    // Validate Student ID: 4-20 alphanumeric characters, unique (uniqueness checked in service)
    public static boolean isValidStudentId(String id) {
        if (id == null || id.trim().isEmpty()) return false;
        String trimmed = id.trim();
        return trimmed.matches("^[a-zA-Z0-9]{4,20}$");
    }

    // ==================== FULL NAME ====================

    // Validate Full Name: 2-60 characters, no digits, not just whitespace
    public static boolean isValidFullName(String name) {
        if (name == null) return false;
        String trimmed = name.trim();
        if (trimmed.length() < 2 || trimmed.length() > 60) return false;
        if (trimmed.matches(".*\\d.*")) return false; // no digits allowed
        return !trimmed.matches("^\\s+$"); // not just whitespace
    }

    // ==================== PROGRAMME ====================

    // Validate Programme: not null/empty (basic check)
    public static boolean isValidProgramme(String programme) {
        return programme != null && !programme.trim().isEmpty();
    }

    // Validate Programme against a list of valid programmes (from Settings)
    public static boolean isValidProgramme(String programme, ObservableList<String> validProgrammes) {
        if (!isValidProgramme(programme)) return false;
        if (validProgrammes == null || validProgrammes.isEmpty()) {
            // Fall back to defaults if no custom list provided
            for (String defaultProg : DEFAULT_PROGRAMMES) {
                if (defaultProg.equalsIgnoreCase(programme.trim())) {
                    return true;
                }
            }
            return false;
        }
        for (String valid : validProgrammes) {
            if (valid.equalsIgnoreCase(programme.trim())) {
                return true;
            }
        }
        return false;
    }

    // ==================== LEVEL ====================

    // Validate Level: must be one of the default values (assignment requirement)
    public static boolean isValidLevel(int level) {
        for (int defaultLevel : DEFAULT_LEVELS) {
            if (level == defaultLevel) return true;
        }
        return false;
    }

    // Validate Level against a custom list (from Settings screen)
    public static boolean isValidLevel(int level, ObservableList<Integer> validLevels) {
        if (validLevels == null || validLevels.isEmpty()) {
            // Fall back to defaults
            return isValidLevel(level);
        }
        return validLevels.contains(level);
    }

    // ==================== GPA ====================

    // Validate GPA: between 0.0 and 4.0 (inclusive)
    public static boolean isValidGpa(double gpa) {
        return !Double.isNaN(gpa) && gpa >= 0.0 && gpa <= 4.0;
    }

    // Validate GPA string (for CSV import)
    public static boolean isValidGpaString(String gpaStr) {
        if (gpaStr == null || gpaStr.trim().isEmpty()) return false;
        try {
            double gpa = Double.parseDouble(gpaStr.trim());
            return isValidGpa(gpa);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // ==================== EMAIL ====================

    // Validate Email: must contain @ and ., basic format check
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) return false;
        String trimmed = email.trim();
        if (!trimmed.contains("@") || !trimmed.contains(".")) return false;

        // Basic structure: something@something.something
        int atIndex = trimmed.indexOf('@');
        int dotIndex = trimmed.lastIndexOf('.');

        if (atIndex <= 0) return false; // @ must not be first
        if (dotIndex <= atIndex + 1) return false; // . must come after @ with at least one char
        if (dotIndex >= trimmed.length() - 1) return false; // . must not be last

        return true;
    }

    // ==================== PHONE NUMBER ====================

    // Validate Phone: 10-15 digits only, no spaces or special characters
    public static boolean isValidPhoneNumber(String phone) {
        if (phone == null) return false;
        String trimmed = phone.trim().replaceAll("[\\s\\-\\(\\)]", ""); // remove common formatting
        return trimmed.matches("^\\d{10,15}$");
    }

    // ==================== STATUS ====================

    // Validate Status: must be "Active" or "Inactive" (case-sensitive per assignment)
    public static boolean isValidStatus(String status) {
        return "Active".equals(status) || "Inactive".equals(status);
    }

    // Validate Status with case-insensitive option (for import flexibility)
    public static boolean isValidStatusFlexible(String status) {
        if (status == null) return false;
        String normalized = status.trim();
        return "Active".equalsIgnoreCase(normalized) || "Inactive".equalsIgnoreCase(normalized);
    }

    // ==================== DATE/TIME ====================

    // Validate date string format for CSV import (ISO_LOCAL_DATE_TIME)
    public static boolean isValidDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) return false;
        try {
            LocalDateTime.parse(dateTimeStr.trim());
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    // ==================== COMPREHENSIVE STUDENT VALIDATION ====================

    // Validate a complete student object (for add/update operations)
    public static ValidationResult validateStudent(
            String studentId, String fullName, String programme, int level,
            double gpa, String email, String phone, String status) {

        ValidationResult result = new ValidationResult();

        if (!isValidStudentId(studentId)) {
            result.addError("Student ID must be 4-20 alphanumeric characters");
        }

        if (!isValidFullName(fullName)) {
            result.addError("Full Name must be 2-60 characters with no digits");
        }

        if (!isValidProgramme(programme)) {
            result.addError("Programme is required");
        }

        if (!isValidLevel(level)) {
            result.addError("Level must be 100, 200, 300, 400, 500, 600, or 700");
        }

        if (!isValidGpa(gpa)) {
            result.addError("GPA must be between 0.0 and 4.0");
        }

        if (!isValidEmail(email)) {
            result.addError("Email must contain @ and .");
        }

        if (!isValidPhoneNumber(phone)) {
            result.addError("Phone must be 10-15 digits");
        }

        if (!isValidStatus(status)) {
            result.addError("Status must be 'Active' or 'Inactive'");
        }

        return result;
    }

    // Validate student with dynamic programme/level lists (from Settings)
    public static ValidationResult validateStudent(
            String studentId, String fullName, String programme, int level,
            double gpa, String email, String phone, String status,
            ObservableList<String> validProgrammes, ObservableList<Integer> validLevels) {

        ValidationResult result = new ValidationResult();

        if (!isValidStudentId(studentId)) {
            result.addError("Student ID must be 4-20 alphanumeric characters");
        }

        if (!isValidFullName(fullName)) {
            result.addError("Full Name must be 2-60 characters with no digits");
        }

        if (!isValidProgramme(programme, validProgrammes)) {
            result.addError("Invalid Programme: " + programme);
        }

        if (!isValidLevel(level, validLevels)) {
            result.addError("Invalid Level: " + level);
        }

        if (!isValidGpa(gpa)) {
            result.addError("GPA must be between 0.0 and 4.0");
        }

        if (!isValidEmail(email)) {
            result.addError("Email must contain @ and .");
        }

        if (!isValidPhoneNumber(phone)) {
            result.addError("Phone must be 10-15 digits");
        }

        if (!isValidStatus(status)) {
            result.addError("Status must be 'Active' or 'Inactive'");
        }

        return result;
    }

    // ==================== HELPER CLASS FOR VALIDATION RESULTS ====================

    // Simple class to collect validation errors
    public static class ValidationResult {
        private final java.util.List<String> errors = new java.util.ArrayList<>();

        public void addError(String error) {
            if (error != null && !error.trim().isEmpty()) {
                errors.add(error);
            }
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public java.util.List<String> getErrors() {
            return new java.util.ArrayList<>(errors); // Return copy for safety
        }

        public String getErrorMessage() {
            if (errors.isEmpty()) return "";
            return String.join("; ", errors);
        }
    }

    // ==================== CSV IMPORT VALIDATION ====================

    // Validate a CSV row for student import
    public static ValidationResult validateCsvRow(String[] fields) {
        ValidationResult result = new ValidationResult();

        if (fields == null || fields.length < 9) {
            result.addError("Insufficient fields in CSV row");
            return result;
        }

        String studentId = fields[0].trim();
        String fullName = fields[1].trim();
        String programme = fields[2].trim();
        String levelStr = fields[3].trim();
        String gpaStr = fields[4].trim();
        String email = fields[5].trim();
        String phone = fields[6].trim();
        String dateAdded = fields[7].trim();
        String status = fields[8].trim();

        // Validate each field
        if (!isValidStudentId(studentId)) {
            result.addError("Invalid Student ID: " + studentId);
        }

        if (!isValidFullName(fullName)) {
            result.addError("Invalid Full Name: " + fullName);
        }

        if (!isValidProgramme(programme)) {
            result.addError("Invalid Programme: " + programme);
        }

        try {
            int level = Integer.parseInt(levelStr);
            if (!isValidLevel(level)) {
                result.addError("Invalid Level: " + levelStr);
            }
        } catch (NumberFormatException e) {
            result.addError("Level must be a number: " + levelStr);
        }

        if (!isValidGpaString(gpaStr)) {
            result.addError("Invalid GPA: " + gpaStr);
        }

        if (!isValidEmail(email)) {
            result.addError("Invalid Email: " + email);
        }

        if (!isValidPhoneNumber(phone)) {
            result.addError("Invalid Phone: " + phone);
        }

        if (!isValidDateTime(dateAdded)) {
            result.addError("Invalid Date Format: " + dateAdded);
        }

        if (!isValidStatusFlexible(status)) {
            result.addError("Invalid Status: " + status);
        }

        return result;
    }
}