package com.sms.ui;

import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SettingsController {

    // At-Risk Threshold
    @FXML private TextField atRiskThresholdField;
    @FXML private Button saveThresholdButton;
    @FXML private Label thresholdStatus;
    
    // Programme Management
    @FXML private TextField newProgrammeField;
    @FXML private Button addProgrammeButton;
    @FXML private ListView<String> programmeListView;
    @FXML private Button removeProgrammeButton;
    @FXML private Button restoreProgrammesButton;
    
    // Level Management
    @FXML private TextField newLevelField;
    @FXML private Button addLevelButton;
    @FXML private ListView<Integer> levelListView;
    @FXML private Button removeLevelButton;
    @FXML private Button restoreLevelsButton;
    
    // Database
    @FXML private Button backupDatabaseButton;
    @FXML private Button viewLogButton;
    
    // Application Info
    @FXML private Label javaVersionLabel;
    @FXML private Label totalStudentsSettingLabel;
    
    // Navigation
    @FXML private Button backButton;
    @FXML private Button dashboardNavButton;
    @FXML private Button studentsNavButton;
    @FXML private Button reportsNavButton;
    @FXML private Button importExportNavButton;
    @FXML private Button settingsNavButton;
    
    private StudentService studentService;
    private Stage mainStage;
    private double atRiskThreshold = 2.0;
    private ObservableList<String> programmeList;
    private ObservableList<Integer> levelList;
    private List<String> defaultProgrammes;
    private List<Integer> defaultLevels;

    public void initialize() {
        try {
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);
            
            // Initialize default values
            defaultProgrammes = List.of("Computer Science", "Business Administration", 
                                       "Engineering", "Mathematics", "Physics");
            defaultLevels = List.of(100, 200, 300, 400, 500, 600, 700);
            
            // Initialize lists
            programmeList = FXCollections.observableArrayList();
            levelList = FXCollections.observableArrayList();
            
            programmeListView.setItems(programmeList);
            levelListView.setItems(levelList);
            
            // Load settings
            loadSettings();
            
            // Setup button actions
            setupButtonActions();
            
            // Display Java version
            javaVersionLabel.setText(System.getProperty("java.version"));
            
            System.out.println("Settings screen loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing Settings screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadSettings() {
        try {
            File settingsFile = new File("data/settings.properties");
            if (settingsFile.exists()) {
                Properties props = new Properties();
                try (FileInputStream fis = new FileInputStream(settingsFile)) {
                    props.load(fis);
                }
                
                // Load threshold
                String thresholdStr = props.getProperty("atRiskThreshold", "2.0");
                atRiskThreshold = Double.parseDouble(thresholdStr);
                atRiskThresholdField.setText(String.valueOf(atRiskThreshold));
                thresholdStatus.setText("Current threshold: " + atRiskThreshold);
                
                // Load programmes
                String programmesStr = props.getProperty("programmes", "");
                if (!programmesStr.isEmpty()) {
                    String[] programmes = programmesStr.split(",");
                    for (String prog : programmes) {
                        if (!prog.trim().isEmpty()) {
                            programmeList.add(prog.trim());
                        }
                    }
                }
                if (programmeList.isEmpty()) {
                    programmeList.addAll(defaultProgrammes);
                }
                
                // Load levels
                String levelsStr = props.getProperty("levels", "");
                if (!levelsStr.isEmpty()) {
                    String[] levels = levelsStr.split(",");
                    for (String level : levels) {
                        if (!level.trim().isEmpty()) {
                            levelList.add(Integer.parseInt(level.trim()));
                        }
                    }
                }
                if (levelList.isEmpty()) {
                    levelList.addAll(defaultLevels);
                }
                
            } else {
                // Use defaults
                atRiskThreshold = 2.0;
                atRiskThresholdField.setText("2.0");
                programmeList.addAll(defaultProgrammes);
                levelList.addAll(defaultLevels);
            }
            
            // Update total students count
            var students = studentService.getAllStudents();
            totalStudentsSettingLabel.setText(String.valueOf(students.size()));
            
        } catch (Exception e) {
            System.err.println("Error loading settings: " + e.getMessage());
            // Use defaults
            atRiskThreshold = 2.0;
            atRiskThresholdField.setText("2.0");
            programmeList.addAll(defaultProgrammes);
            levelList.addAll(defaultLevels);
        }
    }
    
    private void saveSettings() {
        try {
            Files.createDirectories(Paths.get("data"));
            File settingsFile = new File("data/settings.properties");
            
            Properties props = new Properties();
            props.setProperty("atRiskThreshold", String.valueOf(atRiskThreshold));
            props.setProperty("lastUpdated", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            
            // Save programmes
            StringBuilder programmesStr = new StringBuilder();
            for (int i = 0; i < programmeList.size(); i++) {
                if (i > 0) programmesStr.append(",");
                programmesStr.append(programmeList.get(i));
            }
            props.setProperty("programmes", programmesStr.toString());
            
            // Save levels
            StringBuilder levelsStr = new StringBuilder();
            for (int i = 0; i < levelList.size(); i++) {
                if (i > 0) levelsStr.append(",");
                levelsStr.append(levelList.get(i));
            }
            props.setProperty("levels", levelsStr.toString());
            
            try (FileOutputStream fos = new FileOutputStream(settingsFile)) {
                props.store(fos, "Student Management System Settings");
            }
            
            System.out.println("Settings saved successfully");
            
        } catch (Exception e) {
            System.err.println("Error saving settings: " + e.getMessage());
        }
    }
    
    private void setupButtonActions() {
        // Navigation
        backButton.setOnAction(e -> navigateToDashboard());
        dashboardNavButton.setOnAction(e -> navigateToDashboard());
        studentsNavButton.setOnAction(e -> navigateToStudents());
        reportsNavButton.setOnAction(e -> navigateToReports());
        importExportNavButton.setOnAction(e -> navigateToImportExport());
        settingsNavButton.setOnAction(e -> {}); // Already on this screen
        
        // At-Risk Threshold
        saveThresholdButton.setOnAction(e -> saveThreshold());
        
        // Programme Management
        addProgrammeButton.setOnAction(e -> addProgramme());
        removeProgrammeButton.setOnAction(e -> removeProgramme());
        restoreProgrammesButton.setOnAction(e -> restoreDefaultProgrammes());
        
        // Level Management
        addLevelButton.setOnAction(e -> addLevel());
        removeLevelButton.setOnAction(e -> removeLevel());
        restoreLevelsButton.setOnAction(e -> restoreDefaultLevels());
        
        // Database
        backupDatabaseButton.setOnAction(e -> backupDatabase());
        viewLogButton.setOnAction(e -> viewLogFile());
    }
    
    private void saveThreshold() {
        try {
            double newThreshold = Double.parseDouble(atRiskThresholdField.getText().trim());
            
            if (newThreshold < 0.0 || newThreshold > 4.0) {
                showError("Invalid Threshold", "GPA threshold must be between 0.0 and 4.0");
                return;
            }
            
            atRiskThreshold = newThreshold;
            saveSettings();
            
            thresholdStatus.setText("Current threshold: " + newThreshold);
            showAlert("Success", "At-risk threshold saved successfully!\nNew threshold: " + newThreshold);
            
            logSettingChange("At-Risk Threshold", newThreshold);
            
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter a valid number (e.g., 2.0)");
        } catch (Exception e) {
            showError("Error", "Failed to save settings: " + e.getMessage());
        }
    }
    
    private void addProgramme() {
        String programme = newProgrammeField.getText().trim();
        
        if (programme.isEmpty()) {
            showError("Invalid Input", "Please enter a programme name");
            return;
        }
        
        if (programmeList.contains(programme)) {
            showError("Duplicate", "Programme already exists");
            return;
        }
        
        programmeList.add(programme);
        newProgrammeField.clear();
        saveSettings();
        showAlert("Success", "Programme added successfully");
    }
    
    private void removeProgramme() {
        String selected = programmeListView.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showError("No Selection", "Please select a programme to remove");
            return;
        }
        
        programmeList.remove(selected);
        saveSettings();
        showAlert("Success", "Programme removed successfully");
    }
    
    private void restoreDefaultProgrammes() {
        programmeList.clear();
        programmeList.addAll(defaultProgrammes);
        saveSettings();
        showAlert("Success", "Default programmes restored");
    }
    
    private void addLevel() {
        try {
            String levelStr = newLevelField.getText().trim();
            
            if (levelStr.isEmpty()) {
                showError("Invalid Input", "Please enter a level");
                return;
            }
            
            int level = Integer.parseInt(levelStr);
            
            if (level < 100 || level > 900) {
                showError("Invalid Level", "Level must be between 100 and 900");
                return;
            }
            
            if (levelList.contains(level)) {
                showError("Duplicate", "Level already exists");
                return;
            }
            
            levelList.add(level);
            levelList.sort(Integer::compareTo);
            newLevelField.clear();
            saveSettings();
            showAlert("Success", "Level added successfully");
            
        } catch (NumberFormatException e) {
            showError("Invalid Input", "Please enter a valid number (e.g., 100)");
        } catch (Exception e) {
            showError("Error", "Failed to add level: " + e.getMessage());
        }
    }
    
    private void removeLevel() {
        Integer selected = levelListView.getSelectionModel().getSelectedItem();
        
        if (selected == null) {
            showError("No Selection", "Please select a level to remove");
            return;
        }
        
        if (levelList.size() <= 1) {
            showError("Cannot Remove", "At least one level must remain");
            return;
        }
        
        levelList.remove(selected);
        saveSettings();
        showAlert("Success", "Level removed successfully");
    }
    
    private void restoreDefaultLevels() {
        levelList.clear();
        levelList.addAll(defaultLevels);
        saveSettings();
        showAlert("Success", "Default levels restored");
    }
    
    private void backupDatabase() {
        try {
            Files.createDirectories(Paths.get("data/backups"));
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            File source = new File("data/students.db");
            File backup = new File("data/backups/students_backup_" + timestamp + ".db");
            
            if (!source.exists()) {
                showError("Error", "Database file not found");
                return;
            }
            
            Files.copy(source.toPath(), backup.toPath(), StandardCopyOption.REPLACE_EXISTING);
            showAlert("Backup Complete", "Database backed up to:\n" + backup.getAbsolutePath());
            
        } catch (Exception e) {
            showError("Backup Error", "Failed to backup database: " + e.getMessage());
        }
    }
    
    private void viewLogFile() {
        try {
            File logFile = new File("data/app.log");
            
            if (!logFile.exists()) {
                showError("No Log File", "No log file found. Perform some operations first.");
                return;
            }
            
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(logFile);
            } else {
                showAlert("Log File Location", "Log file location:\n" + logFile.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Error", "Could not open log file: " + e.getMessage());
        }
    }
    
    // Navigation methods
    private void navigateToDashboard() {
        navigateToScreen("/fxml/main.fxml", "Dashboard");
    }
    
    private void navigateToStudents() {
        navigateToScreen("/fxml/main.fxml", "Students");
    }
    
    private void navigateToReports() {
        navigateToScreen("/fxml/reports.fxml", "Reports");
    }
    
    private void navigateToImportExport() {
        navigateToScreen("/fxml/import_export.fxml", "Import/Export");
    }
    
    private void navigateToScreen(String fxmlPath, String screenName) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof MainController) {
                ((MainController) controller).setMainStage(mainStage);
            } else if (controller instanceof ReportsController) {
                ((ReportsController) controller).setMainStage(mainStage);
            } else if (controller instanceof ImportExportController) {
                ((ImportExportController) controller).setMainStage(mainStage);
            }
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            mainStage.setScene(scene);
            
        } catch (Exception e) {
            System.err.println("Error navigating to " + screenName + ": " + e.getMessage());
            e.printStackTrace();
            showError("Navigation Error", "Failed to open " + screenName + " screen: " + e.getMessage());
        }
    }
    
    // Logging
    private void logSettingChange(String setting, Object value) {
        try {
            Files.createDirectories(Paths.get("data"));
            String logFile = "data/app.log";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(timestamp + " - SETTINGS - " + setting + " changed to: " + value + "\n");
            }
            
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }
    
    // UI helpers
    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public double getAtRiskThreshold() {
        return atRiskThreshold;
    }
    
    public ObservableList<String> getProgrammeList() {
        return programmeList;
    }
    
    public ObservableList<Integer> getLevelList() {
        return levelList;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
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
}