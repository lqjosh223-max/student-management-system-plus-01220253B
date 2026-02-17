package com.sms.ui;

import com.sms.domain.Student;
import com.sms.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class AddStudentController {
    
    @FXML private TextField studentIdField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> programmeField;
    @FXML private ComboBox<Integer> levelField;
    @FXML private TextField gpaField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> statusField;
    @FXML private Button addButton;
    @FXML private Button cancelButton;
    
    private StudentService studentService;
    private Stage dialogStage;
    
    public void initialize() {
        // Initialize combo boxes
        programmeField.getItems().addAll("Computer Science", "Business", "Engineering", "Mathematics", "Physics");
        levelField.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
        statusField.getItems().addAll("Active", "Inactive");
        
        // Set default values
        statusField.setValue("Active");
        
        // Setup button actions
        cancelButton.setOnAction(e -> closeDialog());
        addButton.setOnAction(e -> addStudent());
    }
    
    public void setStudentService(StudentService service) {
        this.studentService = service;
    }
    
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    
    private void addStudent() {
        try {
            // Get values from fields
            String studentId = studentIdField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String programme = programmeField.getValue();
            Integer level = levelField.getValue();
            Double gpa = Double.parseDouble(gpaField.getText().trim());
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String status = statusField.getValue();
            
            // Validate required fields
            if (studentId.isEmpty()) {
                showError("Student ID is required");
                return;
            }
            if (fullName.isEmpty()) {
                showError("Full Name is required");
                return;
            }
            if (programme == null) {
                showError("Programme is required");
                return;
            }
            if (level == null) {
                showError("Level is required");
                return;
            }
            if (email.isEmpty()) {
                showError("Email is required");
                return;
            }
            if (phone.isEmpty()) {
                showError("Phone is required");
                return;
            }
            if (status == null) {
                showError("Status is required");
                return;
            }
            
            // Create student object
            Student student = new Student(
                studentId,
                fullName,
                programme,
                level,
                gpa,
                email,
                phone,
                LocalDateTime.now(),
                status
            );
            
            // Add to database (validation happens in service layer)
            studentService.addStudent(student);
            
            // Success
            showAlert("Success", "Student added successfully!");
            closeDialog();
            
        } catch (NumberFormatException e) {
            showError("Invalid GPA: Please enter a number (e.g., 3.5)");
        } catch (IllegalArgumentException e) {
            showError("Validation Error: " + e.getMessage());
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }
}