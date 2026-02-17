package com.sms.ui;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportsController {

    // Top Performers tab
    @FXML private ComboBox<String> topPerformerProgrammeFilter;
    @FXML private ComboBox<String> topPerformerLevelFilter;
    @FXML private TableView<TopPerformer> topPerformersTable;
    
    // At Risk tab
    @FXML private TextField atRiskThresholdField;
    @FXML private TableView<Student> atRiskTable;
    
    // GPA Distribution tab
    @FXML private BarChart<String, Number> gpaChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    // Programme Summary tab
    @FXML private TableView<ProgrammeSummaryRow> programmeSummaryTable;

    // Report-specific buttons
    @FXML private Button refreshTopPerformersButton;
    @FXML private Button exportTopPerformersButton;
    @FXML private Button applyThresholdButton;
    @FXML private Button exportAtRiskButton;
    @FXML private Button refreshChartButton;
    @FXML private Button exportChartDataButton;
    @FXML private Button refreshSummaryButton;
    @FXML private Button exportSummaryButton;
    
    // Navigation buttons
    @FXML private Button backButton;
    @FXML private Button dashboardNavButton;
    @FXML private Button studentsNavButton;
    @FXML private Button importNavButton;
    @FXML private Button exportNavButton;
    @FXML private Button settingsNavButton;
    
    private StudentService studentService;
    private Stage mainStage;


    public void initialize() {
        try {
            // Initialize service
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);
            
            // Setup filters
            setupFilters();
            
            // Setup tables
            setupTables();
            
            // Load initial data
            loadTopPerformers();
            loadAtRiskStudents();
            loadGpaDistribution();
            loadProgrammeSummary();
            
            // Setup button actions
            setupButtonActions();
            
            System.out.println("Reports screen loaded successfully");
            
        } catch (Exception e) {
            System.err.println("Error initializing reports screen: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupFilters() {
        topPerformerProgrammeFilter.getItems().addAll("All", "Computer Science", "Business", "Engineering", "Mathematics", "Physics");
        topPerformerProgrammeFilter.setValue("All");
        
        topPerformerLevelFilter.getItems().addAll("All", "100", "200", "300", "400", "500", "600", "700");
        topPerformerLevelFilter.setValue("All");
    }
    
    private void setupTables() {
        // Top performers table
        topPerformersTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("rank"));
        topPerformersTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        topPerformersTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        topPerformersTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("programme"));
        topPerformersTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("level"));
        topPerformersTable.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("gpa"));
        
        // At risk table
        atRiskTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("fullName"));
        atRiskTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentId"));
        atRiskTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("programme"));
        atRiskTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("level"));
        atRiskTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("gpa"));
        atRiskTable.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Programme summary table
        programmeSummaryTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("programme"));
        programmeSummaryTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("studentCount"));
        programmeSummaryTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("averageGpa"));
    }
    
    private void setupButtonActions() {
        backButton.setOnAction(e -> navigateToStudentsScreen());
        dashboardNavButton.setOnAction(e -> showAlert("Info", "Dashboard screen coming soon"));
        studentsNavButton.setOnAction(e -> navigateToStudentsScreen());
        importNavButton.setOnAction(e -> showAlert("Info", "Import screen coming soon"));
        exportNavButton.setOnAction(e -> showAlert("Info", "Export screen coming soon"));
        settingsNavButton.setOnAction(e -> showAlert("Info", "Settings screen coming soon"));
        
        // Report-specific buttons
        topPerformerProgrammeFilter.valueProperty().addListener((o, oldV, newV) -> loadTopPerformers());
        topPerformerLevelFilter.valueProperty().addListener((o, oldV, newV) -> loadTopPerformers());

        refreshTopPerformersButton.setOnAction(e -> loadTopPerformers());
        exportTopPerformersButton.setOnAction(e -> exportTopPerformersToCsv());
        
        applyThresholdButton.setOnAction(e -> loadAtRiskStudents());
        exportAtRiskButton.setOnAction(e -> exportAtRiskToCsv());
        
        refreshChartButton.setOnAction(e -> loadGpaDistribution());
        exportChartDataButton.setOnAction(e -> exportGpaDistributionToCsv());
        
        refreshSummaryButton.setOnAction(e -> loadProgrammeSummary());
        exportSummaryButton.setOnAction(e -> exportProgrammeSummaryToCsv());
    }
    
    private void loadTopPerformers() {
        try {
            String programme = topPerformerProgrammeFilter.getValue();
            if ("All".equals(programme)) programme = null;
            
            String levelStr = topPerformerLevelFilter.getValue();
            Integer level = "All".equals(levelStr) ? null : Integer.parseInt(levelStr);
            
            List<Student> students = studentService.getTopPerformers(programme, level, 10);
            
            ObservableList<TopPerformer> performers = FXCollections.observableArrayList();
            int rank = 1;
            for (Student s : students) {
                performers.add(new TopPerformer(rank++, s.getFullName(), s.getStudentId(), 
                                               s.getProgramme(), s.getLevel(), s.getGpa()));
            }
            
            topPerformersTable.setItems(performers);
            
        } catch (Exception e) {
            System.err.println("Error loading top performers: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadAtRiskStudents() {
        try {
            double threshold = Double.parseDouble(atRiskThresholdField.getText());
            List<Student> atRisk = studentService.getAtRiskStudents(threshold);
            atRiskTable.setItems(FXCollections.observableArrayList(atRisk));
            
        } catch (NumberFormatException e) {
            showError("Invalid Threshold", "Please enter a valid number (e.g., 2.0)");
        } catch (Exception e) {
            System.err.println("Error loading at-risk students: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadGpaDistribution() {
        try {
            Map<String, Long> distribution = studentService.getGpaDistribution();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Student Count");
            
            for (Map.Entry<String, Long> entry : distribution.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }
            
            gpaChart.getData().clear();
            gpaChart.getData().add(series);
            
        } catch (Exception e) {
            System.err.println("Error loading GPA distribution: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadProgrammeSummary() {
        try {
            Map<String, StudentService.ProgrammeStats> summary = studentService.getProgrammeSummary();
            
            ObservableList<ProgrammeSummaryRow> rows = FXCollections.observableArrayList();
            for (Map.Entry<String, StudentService.ProgrammeStats> entry : summary.entrySet()) {
                rows.add(new ProgrammeSummaryRow(entry.getKey(), 
                                                entry.getValue().getStudentCount(),
                                                entry.getValue().getAverageGpa()));
            }
            
            programmeSummaryTable.setItems(rows);
            
        } catch (Exception e) {
            System.err.println("Error loading programme summary: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Export methods
    private void exportTopPerformersToCsv() {
        exportTableToCsv(topPerformersTable.getItems(), "top_performers_export.csv",
            "Rank,Full Name,Student ID,Programme,Level,GPA\n");
    }
    
    private void exportAtRiskToCsv() {
        exportStudentsToCsv(atRiskTable.getItems(), "at_risk_export.csv",
            "Full Name,Student ID,Programme,Level,GPA,Status\n");
    }
    
    private void exportGpaDistributionToCsv() {
        try {
            Map<String, Long> distribution = studentService.getGpaDistribution();
            File file = new File("data/gpa_distribution_export.csv");
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("GPA Range,Student Count\n");
                for (Map.Entry<String, Long> entry : distribution.entrySet()) {
                    writer.write(entry.getKey() + "," + entry.getValue() + "\n");
                }
                showAlert("Export Complete", "GPA distribution exported to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Export Error", "Failed to export GPA distribution: " + e.getMessage());
        }
    }
    
    private void exportProgrammeSummaryToCsv() {
        try {
            Map<String, StudentService.ProgrammeStats> summary = studentService.getProgrammeSummary();
            File file = new File("data/programme_summary_export.csv");
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("Programme,Total Students,Average GPA\n");
                for (Map.Entry<String, StudentService.ProgrammeStats> entry : summary.entrySet()) {
                    writer.write(entry.getKey() + "," + 
                               entry.getValue().getStudentCount() + "," +
                               String.format("%.2f", entry.getValue().getAverageGpa()) + "\n");
                }
                showAlert("Export Complete", "Programme summary exported to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Export Error", "Failed to export programme summary: " + e.getMessage());
        }
    }
    
    // Generic export helpers
    private void exportTableToCsv(ObservableList<TopPerformer> items, String filename, String header) {
        try {
            File file = new File("data/" + filename);
            file.getParentFile().mkdirs(); // Ensure data folder exists
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                for (TopPerformer p : items) {
                    writer.write(p.getRank() + "," + 
                               p.getFullName() + "," +
                               p.getStudentId() + "," +
                               p.getProgramme() + "," +
                               p.getLevel() + "," +
                               p.getGpa() + "\n");
                }
                showAlert("Export Complete", "Report exported to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Export Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    private void exportStudentsToCsv(ObservableList<Student> items, String filename, String header) {
        try {
            File file = new File("data/" + filename);
            file.getParentFile().mkdirs();
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(header);
                for (Student s : items) {
                    writer.write(s.getFullName() + "," +
                               s.getStudentId() + "," +
                               s.getProgramme() + "," +
                               s.getLevel() + "," +
                               s.getGpa() + "," +
                               s.getStatus() + "\n");
                }
                showAlert("Export Complete", "Report exported to: " + file.getAbsolutePath());
            }
            
        } catch (Exception e) {
            showError("Export Error", "Failed to export report: " + e.getMessage());
        }
    }
    
    // Navigation
    private void navigateToStudentsScreen() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            javafx.scene.Parent root = loader.load();
            
            MainController controller = loader.getController();
            controller.setMainStage(mainStage);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            mainStage.setScene(scene);
            
        } catch (Exception e) {
            System.err.println("Error navigating to students screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper classes for table views
    public static class TopPerformer {
        private final IntegerProperty rank;
        private final StringProperty fullName;
        private final StringProperty studentId;
        private final StringProperty programme;
        private final IntegerProperty level;
        private final DoubleProperty gpa;

        public TopPerformer(int rank, String fullName, String studentId,
                            String programme, int level, double gpa) {
            this.rank = new SimpleIntegerProperty(rank);
            this.fullName = new SimpleStringProperty(fullName);
            this.studentId = new SimpleStringProperty(studentId);
            this.programme = new SimpleStringProperty(programme);
            this.level = new SimpleIntegerProperty(level);
            this.gpa = new SimpleDoubleProperty(gpa);
        }

        public int getRank() { return rank.get(); }
        public IntegerProperty rankProperty() { return rank; }

        public String getFullName() { return fullName.get(); }
        public StringProperty fullNameProperty() { return fullName; }

        public String getStudentId() { return studentId.get(); }
        public StringProperty studentIdProperty() { return studentId; }

        public String getProgramme() { return programme.get(); }
        public StringProperty programmeProperty() { return programme; }

        public int getLevel() { return level.get(); }
        public IntegerProperty levelProperty() { return level; }

        public double getGpa() { return gpa.get(); }
        public DoubleProperty gpaProperty() { return gpa; }
    }

    public static class ProgrammeSummaryRow {
        private final String programme;
        private final int studentCount;
        private final double averageGpa;
        
        public ProgrammeSummaryRow(String programme, int studentCount, double averageGpa) {
            this.programme = programme;
            this.studentCount = studentCount;
            this.averageGpa = averageGpa;
        }
        
        public String getProgramme() { return programme; }
        public int getStudentCount() { return studentCount; }
        public double getAverageGpa() { return averageGpa; }
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