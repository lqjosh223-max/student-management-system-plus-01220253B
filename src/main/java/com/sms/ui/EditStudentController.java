package com.sms.ui;

import com.sms.domain.Student;
import com.sms.service.StudentService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class EditStudentController {
    
    @FXML private TextField studentIdField;
    @FXML private TextField fullNameField;
    @FXML private ComboBox<String> programmeField;
    @FXML private ComboBox<Integer> levelField;
    @FXML private TextField gpaField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> statusField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;
    
    private StudentService studentService;
    private Stage dialogStage;
    private Student originalStudent;
    
    public void initialize() {
        programmeField.getItems().addAll("Computer Science", "Business", "Engineering", "Mathematics", "Physics");
        levelField.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
        statusField.getItems().addAll("Active", "Inactive");
        
        cancelButton.setOnAction(e -> closeDialog());
        saveButton.setOnAction(e -> saveChanges());
    }
    
    public void setStudentService(StudentService service) {
        this.studentService = service;
    }
    
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
    }
    
    public void setStudent(Student student) {
        this.originalStudent = student;
        studentIdField.setText(student.getStudentId());
        fullNameField.setText(student.getFullName());
        programmeField.setValue(student.getProgramme());
        levelField.setValue(student.getLevel());
        gpaField.setText(String.valueOf(student.getGpa()));
        emailField.setText(student.getEmail());
        phoneField.setText(student.getPhoneNumber());
        statusField.setValue(student.getStatus());
    }
    
    private void saveChanges() {
        try {
            Student updatedStudent = new Student(
                studentIdField.getText().trim(),
                fullNameField.getText().trim(),
                programmeField.getValue(),
                levelField.getValue(),
                Double.parseDouble(gpaField.getText().trim()),
                emailField.getText().trim(),
                phoneField.getText().trim(),
                originalStudent.getDateAdded(), // Keep original date
                statusField.getValue()
            );
            
            studentService.updateStudent(updatedStudent);
            showAlert("Success", "Student updated successfully!");
            closeDialog();
            
        } catch (NumberFormatException e) {
            showError("Invalid GPA: Please enter a number");
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