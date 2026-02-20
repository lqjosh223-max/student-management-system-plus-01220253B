package com.sms.ui;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;

import java.awt.Desktop;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ImportExportController {

    // Import tab
    @FXML private Button chooseFileButton;
    @FXML private Label selectedFileName;
    @FXML private Button startImportButton;
    @FXML private Label successCountLabel;
    @FXML private Label errorCountLabel;
    @FXML private Button viewErrorReportButton;

    // Export tab
    @FXML private RadioButton exportAllStudentsRadio;
    @FXML private RadioButton exportActiveOnlyRadio;
    @FXML private RadioButton exportInactiveOnlyRadio;
    @FXML private ToggleGroup exportToggleGroup; // Add this for FXML binding
    @FXML private Button exportAllButton;
    @FXML private Button exportTopPerformersQuickButton;
    @FXML private Button exportAtRiskQuickButton;
    @FXML private Button exportSummaryQuickButton;

    // Navigation
    @FXML private Button backButton;
    @FXML private Button dashboardNavButton;
    @FXML private Button studentsNavButton;
    @FXML private Button reportsNavButton;
    @FXML private Button importExportNavButton;
    @FXML private Button settingsNavButton;

    private StudentService studentService;
    private Stage mainStage;
    private File selectedFile;
    private String lastImportErrorReport;

    public void initialize() {
        try {
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);

            // Setup ToggleGroup for RadioButtons (if not set in FXML)
            if (exportToggleGroup == null) {
                exportToggleGroup = new ToggleGroup();
                exportAllStudentsRadio.setToggleGroup(exportToggleGroup);
                exportActiveOnlyRadio.setToggleGroup(exportToggleGroup);
                exportInactiveOnlyRadio.setToggleGroup(exportToggleGroup);
                exportAllStudentsRadio.setSelected(true);
            }

            setupButtonActions();
            System.out.println("Import/Export screen loaded successfully");

        } catch (Exception e) {
            System.err.println("Error initializing Import/Export screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupButtonActions() {
        // Navigation
        backButton.setOnAction(e -> navigateToDashboard());
        dashboardNavButton.setOnAction(e -> navigateToDashboard());
        studentsNavButton.setOnAction(e -> navigateToStudents());
        reportsNavButton.setOnAction(e -> navigateToReports());
        importExportNavButton.setOnAction(e -> {}); // Already on this screen
        settingsNavButton.setOnAction(e -> showAlert("Info", "Settings screen coming soon"));

        // Import buttons
        chooseFileButton.setOnAction(e -> handleChooseFile());
        startImportButton.setOnAction(e -> handleImport());
        viewErrorReportButton.setOnAction(e -> handleViewErrorReport());

        // Export buttons
        exportAllButton.setOnAction(e -> handleExportAll());
        exportTopPerformersQuickButton.setOnAction(e -> handleExportTopPerformers());
        exportAtRiskQuickButton.setOnAction(e -> handleExportAtRisk());
        exportSummaryQuickButton.setOnAction(e -> handleExportSummary());
    }

    private void handleChooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File to Import");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        selectedFile = fileChooser.showOpenDialog(mainStage);
        if (selectedFile != null) {
            selectedFileName.setText(selectedFile.getName());
            startImportButton.setDisable(false);
        }
    }

    private void handleImport() {
        if (selectedFile == null) {
            showError("No File Selected", "Please choose a CSV file first");
            return;
        }

        try {
            // Ensure data folder exists
            Files.createDirectories(Paths.get("data"));

            // Perform import
            StudentService.ImportResult result = studentService.importStudentsFromCsv(selectedFile.getAbsolutePath());

            // Update UI
            successCountLabel.setText(String.valueOf(result.getSuccessCount()));
            errorCountLabel.setText(String.valueOf(result.getErrorCount()));

            // Save error report if there are errors
            if (!result.getErrors().isEmpty()) {
                saveImportErrorReport(result.getErrors());
                viewErrorReportButton.setDisable(false);
            }

            // Show summary
            String message = "Import completed!\n\n" +
                    "Successfully imported: " + result.getSuccessCount() + "\n" +
                    "Errors: " + result.getErrorCount();

            if (!result.getErrors().isEmpty()) {
                message += "\n\nError report saved to: data/import_errors_" +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
            }

            showAlert("Import Summary", message);

            // Log import
            logImportOperation(result.getSuccessCount(), result.getErrorCount());

        } catch (Exception e) {
            showError("Import Error", "Failed to import CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void saveImportErrorReport(List<String> errors) {
        try {
            Files.createDirectories(Paths.get("data"));
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "data/import_errors_" + timestamp + ".csv";
            lastImportErrorReport = filename;

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                writer.write("Error Description\n");
                for (String error : errors) {
                    writer.write(error + "\n");
                }
            }

        } catch (IOException e) {
            System.err.println("Error saving import error report: " + e.getMessage());
        }
    }

    private void handleViewErrorReport() {
        if (lastImportErrorReport != null && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(new File(lastImportErrorReport));
            } catch (IOException e) {
                showError("Error", "Could not open error report: " + e.getMessage());
            }
        } else {
            showError("Error", "Error report not available or desktop not supported");
        }
    }

    private void handleExportAll() {
        try {
            Files.createDirectories(Paths.get("data"));

            // Get all students from database
            List<Student> allStudents = studentService.getAllStudents();

            // Filter based on selected RadioButton
            List<Student> studentsToExport = allStudents;
            if (exportActiveOnlyRadio.isSelected()) {
                studentsToExport = allStudents.stream()
                        .filter(s -> "Active".equals(s.getStatus()))
                        .toList();
            } else if (exportInactiveOnlyRadio.isSelected()) {
                studentsToExport = allStudents.stream()
                        .filter(s -> "Inactive".equals(s.getStatus()))
                        .toList();
            }

            // Setup file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Students to CSV");
            fileChooser.setInitialDirectory(new File("data"));
            fileChooser.setInitialFileName("students_export_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(mainStage);
            if (file != null) {
                exportStudentsToCsv(studentsToExport, file);
                logExportOperation("All Students Export", studentsToExport.size());
                showAlert("Export Complete", "Exported " + studentsToExport.size() + " students to:\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            showError("Export Error", "Failed to export: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void exportStudentsToCsv(List<Student> students, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Write header
            writer.write("Student ID,Full Name,Programme,Level,GPA,Email,Phone,Date Added,Status\n");

            // Write data rows
            for (Student s : students) {
                writer.write(
                        escapeCsv(s.getStudentId()) + "," +
                                escapeCsv(s.getFullName()) + "," +
                                escapeCsv(s.getProgramme()) + "," +
                                s.getLevel() + "," +
                                s.getGpa() + "," +
                                escapeCsv(s.getEmail()) + "," +
                                escapeCsv(s.getPhoneNumber()) + "," +
                                s.getDateAdded() + "," +
                                escapeCsv(s.getStatus()) + "\n"
                );
            }
        }
    }

    // Helper method to escape CSV fields that contain commas or quotes
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void handleExportTopPerformers() {
        try {
            Files.createDirectories(Paths.get("data"));

            List<Student> topPerformers = studentService.getTopPerformers(null, null, 10);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Top Performers");
            fileChooser.setInitialDirectory(new File("data"));
            fileChooser.setInitialFileName("top_performers_export_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(mainStage);
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Rank,Student ID,Full Name,Programme,Level,GPA\n");
                    int rank = 1;
                    for (Student s : topPerformers) {
                        writer.write(rank++ + "," +
                                escapeCsv(s.getStudentId()) + "," +
                                escapeCsv(s.getFullName()) + "," +
                                escapeCsv(s.getProgramme()) + "," +
                                s.getLevel() + "," +
                                s.getGpa() + "\n");
                    }
                }
                logExportOperation("Top Performers Export", topPerformers.size());
                showAlert("Export Complete", "Exported " + topPerformers.size() + " top performers to:\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            showError("Export Error", "Failed to export top performers: " + e.getMessage());
        }
    }

    private void handleExportAtRisk() {
        try {
            Files.createDirectories(Paths.get("data"));

            List<Student> atRisk = studentService.getAtRiskStudents(2.0);

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export At Risk Students");
            fileChooser.setInitialDirectory(new File("data"));
            fileChooser.setInitialFileName("at_risk_export_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(mainStage);
            if (file != null) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Student ID,Full Name,Programme,Level,GPA,Status\n");
                    for (Student s : atRisk) {
                        writer.write(
                                escapeCsv(s.getStudentId()) + "," +
                                        escapeCsv(s.getFullName()) + "," +
                                        escapeCsv(s.getProgramme()) + "," +
                                        s.getLevel() + "," +
                                        s.getGpa() + "," +
                                        escapeCsv(s.getStatus()) + "\n"
                        );
                    }
                }
                logExportOperation("At Risk Export", atRisk.size());
                showAlert("Export Complete", "Exported " + atRisk.size() + " at-risk students to:\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            showError("Export Error", "Failed to export at-risk students: " + e.getMessage());
        }
    }

    private void handleExportSummary() {
        try {
            Files.createDirectories(Paths.get("data"));

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Export Programme Summary");
            fileChooser.setInitialDirectory(new File("data"));
            fileChooser.setInitialFileName("programme_summary_export_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(mainStage);
            if (file != null) {
                // Get summary inside the if block where file is selected
                var summary = studentService.getProgrammeSummary();

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("Programme,Total Students,Average GPA\n");
                    for (var entry : summary.entrySet()) {
                        writer.write(
                                escapeCsv(entry.getKey()) + "," +
                                        entry.getValue().getStudentCount() + "," +
                                        String.format("%.2f", entry.getValue().getAverageGpa()) + "\n"
                        );
                    }
                }
                logExportOperation("Programme Summary Export", summary.size());
                showAlert("Export Complete", "Exported programme summary to:\n" + file.getAbsolutePath());
            }

        } catch (Exception e) {
            showError("Export Error", "Failed to export programme summary: " + e.getMessage());
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
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/reports.fxml"));
            javafx.scene.Parent root = loader.load();

            ReportsController controller = loader.getController();
            controller.setMainStage(mainStage);

            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            mainStage.setScene(scene);

        } catch (Exception e) {
            System.err.println("Error navigating to reports screen: " + e.getMessage());
            e.printStackTrace();
            showError("Navigation Error", "Failed to open Reports screen: " + e.getMessage());
        }
    }

    private void navigateToScreen(String fxmlPath, String screenName) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource(fxmlPath));
            javafx.scene.Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setMainStage(mainStage);

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
    private void logImportOperation(int successCount, int errorCount) {
        try {
            Files.createDirectories(Paths.get("data"));
            String logFile = "data/app.log";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(timestamp + " - IMPORT - Success: " + successCount + ", Errors: " + errorCount + "\n");
            }

        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }

    private void logExportOperation(String exportType, int recordCount) {
        try {
            Files.createDirectories(Paths.get("data"));
            String logFile = "data/app.log";
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
                writer.write(timestamp + " - EXPORT - " + exportType + " - Records: " + recordCount + "\n");
            }

        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }

    // UI helpers
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    }
