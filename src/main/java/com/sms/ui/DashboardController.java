package com.sms.ui;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DashboardController {

    // Stats Labels
    @FXML private Label totalStudentsLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private Label inactiveStudentsLabel;
    @FXML private Label averageGpaLabel;
    @FXML private Label currentUserLabel;
    
    // Navigation Buttons
    @FXML private Button dashboardNavButton;
    @FXML private Button studentsNavButton;
    @FXML private Button reportsNavButton;
    @FXML private Button importExportNavButton;
    @FXML private Button settingsNavButton;
    
    // Quick Action Buttons
    @FXML private Button quickStudentsButton;
    @FXML private Button quickReportsButton;
    @FXML private Button quickImportExportButton;
    @FXML private Button quickSettingsButton;
    
    // Activity Log
    @FXML private ListView<String> activityListView;
    
    private StudentService studentService;
    private Stage mainStage;
    private ObservableList<String> activityLog;

    public void initialize() {
        try {
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);
            
            // Initialize activity log
            activityLog = FXCollections.observableArrayList();
            activityListView.setItems(activityLog);
            
            // Load dashboard stats
            loadDashboardStats();
            
            // Setup navigation
            setupNavigation();
            
            // Log dashboard view
            logActivity("Dashboard viewed");
            
            System.out.println("Dashboard loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing Dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadDashboardStats() {
        try {
            List<Student> allStudents = studentService.getAllStudents();
            
            totalStudentsLabel.setText(String.valueOf(allStudents.size()));
            
            long activeCount = allStudents.stream()
                .filter(s -> "Active".equals(s.getStatus()))
                .count();
            long inactiveCount = allStudents.size() - activeCount;
            
            activeStudentsLabel.setText(String.valueOf(activeCount));
            inactiveStudentsLabel.setText(String.valueOf(inactiveCount));
            
            double avgGpa = allStudents.stream()
                .mapToDouble(Student::getGpa)
                .average()
                .orElse(0.0);
            
            averageGpaLabel.setText(String.format("%.2f", avgGpa));
            
        } catch (Exception e) {
            System.err.println("Error loading dashboard stats: " + e.getMessage());
        }
    }
    
    private void setupNavigation() {
        // Sidebar navigation
        dashboardNavButton.setOnAction(e -> {}); // Already on dashboard
        studentsNavButton.setOnAction(e -> navigateToStudents());
        reportsNavButton.setOnAction(e -> navigateToReports());
        importExportNavButton.setOnAction(e -> navigateToImportExport());
        settingsNavButton.setOnAction(e -> navigateToSettings());
        
        // Quick action buttons
        quickStudentsButton.setOnAction(e -> navigateToStudents());
        quickReportsButton.setOnAction(e -> navigateToReports());
        quickImportExportButton.setOnAction(e -> navigateToImportExport());
        quickSettingsButton.setOnAction(e -> navigateToSettings());
    }
    
    private void navigateToStudents() {
        navigateToScreen("/fxml/students.fxml", "Students");
        logActivity("Navigated to Students screen");
    }
    
    private void navigateToReports() {
        navigateToScreen("/fxml/reports.fxml", "Reports");
        logActivity("Navigated to Reports screen");
    }
    
    private void navigateToImportExport() {
        navigateToScreen("/fxml/import_export.fxml", "Import/Export");
        logActivity("Navigated to Import/Export screen");
    }
    
    private void navigateToSettings() {
        navigateToScreen("/fxml/settings.fxml", "Settings");
        logActivity("Navigated to Settings screen");
    }
    
    private void navigateToScreen(String fxmlPath, String screenName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof StudentsController) {
                ((StudentsController) controller).setMainStage(mainStage);
                ((StudentsController) controller).setDashboardController(this);
            } else if (controller instanceof ReportsController) {
                ((ReportsController) controller).setMainStage(mainStage);
            } else if (controller instanceof ImportExportController) {
                ((ImportExportController) controller).setMainStage(mainStage);
            } else if (controller instanceof SettingsController) {
                ((SettingsController) controller).setMainStage(mainStage);
            }
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            mainStage.setScene(scene);
            
        } catch (IOException e) {
            System.err.println("Error navigating to " + screenName + ": " + e.getMessage());
            e.printStackTrace();
            showError("Navigation Error", "Failed to open " + screenName + " screen");
        }
    }
    
    private void logActivity(String activity) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        activityLog.add(0, "[" + timestamp + "] " + activity);
        
        // Keep only last 10 activities
        if (activityLog.size() > 10) {
            activityLog.remove(activityLog.size() - 1);
        }
    }
    
    public void refreshStats() {
        loadDashboardStats();
    }
    
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}