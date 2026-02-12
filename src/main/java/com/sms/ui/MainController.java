package com.sms.ui;

import com.sms.domain.Student;
import com.sms.repository.SQLiteStudentRepository;
import com.sms.service.StudentService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MainController {

    @FXML private Label totalStudentsLabel;
    @FXML private Label activeStudentsLabel;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> programmeFilter;
    @FXML private ComboBox<Integer> levelFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button addStudentButton;
    @FXML private TableView<Student> studentTable;

    @FXML private TableColumn<Student, String> fullNameCol;
    @FXML private TableColumn<Student, String> regNumCol;
    @FXML private TableColumn<Student, String> programmeCol;
    @FXML private TableColumn<Student, Integer> levelCol;
    @FXML private TableColumn<Student, Double> gpaCol;
    @FXML private TableColumn<Student, String> emailCol;
    @FXML private TableColumn<Student, String> phoneCol;
    @FXML private TableColumn<Student, String> statusCol;
    @FXML private TableColumn<Student, String> actionsCol; // Added missing field

    private StudentService studentService;
    private ObservableList<Student> studentData;

    public void initialize() {
        try {
            // Initialize database connection
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);

            // Initialize combo boxes
            levelFilter.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
            statusFilter.getItems().addAll("Active", "Inactive");

            // Initialize table columns
            setupTableColumns();

            // Load initial data
            loadStudentData();

            // Setup search and filter listeners
            setupSearchAndFilter();

            System.out.println("UI loaded successfully with database connection!");

        } catch (Exception e) {
            System.err.println("Error initializing UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        fullNameCol.setCellValueFactory(cellData -> cellData.getValue().fullNameProperty());
        regNumCol.setCellValueFactory(cellData -> cellData.getValue().studentIdProperty());
        programmeCol.setCellValueFactory(cellData -> cellData.getValue().programmeProperty());
        levelCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getLevel()).asObject());
        gpaCol.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getGpa()).asObject());
        emailCol.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
        phoneCol.setCellValueFactory(cellData -> cellData.getValue().phoneNumberProperty());
        statusCol.setCellValueFactory(cellData -> cellData.getValue().statusProperty());
        actionsCol.setCellValueFactory(cellData -> cellData.getValue().actionsProperty()); // Added this line
    }

    private void loadStudentData() {
        try {
            // Get all students from database
            var students = studentService.getAllStudents();
            studentData = FXCollections.observableArrayList(students);
            studentTable.setItems(studentData);

            // Update stats
            totalStudentsLabel.setText(String.valueOf(students.size()));
            long activeCount = students.stream().filter(s -> "Active".equals(s.getStatus())).count();
            activeStudentsLabel.setText(String.valueOf(activeCount));

        } catch (Exception e) {
            System.err.println("Error loading student data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSearchAndFilter() {
        // Add listeners for search and filters
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterStudents();
        });

        programmeFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterStudents();
        });

        levelFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterStudents();
        });

        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterStudents();
        });
    }

    private void filterStudents() {
        // This is a basic implementation - you can enhance it later
        String searchTerm = searchField.getText().toLowerCase();
        String programme = programmeFilter.getValue();
        Integer level = levelFilter.getValue();
        String status = statusFilter.getValue();

        // For now, just reload data (you can optimize this later)
        loadStudentData();
    }
}