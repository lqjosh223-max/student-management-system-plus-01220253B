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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;

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
    @FXML private TableColumn<Student, String> actionsCol;

    private StudentService studentService;
    private ObservableList<Student> studentData;
    private Stage mainStage;

    public void initialize() {
        try {
            // Initialize database connection
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);

            // Initialize combo boxes
            levelFilter.getItems().addAll(100, 200, 300, 400, 500, 600, 700);
            statusFilter.getItems().addAll("Active", "Inactive");
            programmeFilter.getItems().addAll("Computer Science", "Business", "Engineering", "Mathematics", "Physics");

            // Initialize table columns
            setupTableColumns();

            // Load initial data
            loadStudentData();

            // Setup search and filter listeners
            setupSearchAndFilter();

            // Setup button actions
            addStudentButton.setOnAction(e -> handleAddStudent());

            // Get main stage reference
            mainStage = (Stage) studentTable.getScene().getWindow();

            System.out.println("UI loaded successfully with database connection!");

        } catch (Exception e) {
            System.err.println("Error initializing UI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        fullNameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        regNumCol.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        programmeCol.setCellValueFactory(new PropertyValueFactory<>("programme"));
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        gpaCol.setCellValueFactory(new PropertyValueFactory<>("gpa"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Actions column with clickable "View" button
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View");
            {
                btn.setStyle("-fx-background-color: #2a5d44; -fx-text-fill: white; -fx-cursor: hand;");
                btn.setOnAction(e -> handleViewStudent(getTableView().getItems().get(getIndex())));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
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
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterStudents());
        programmeFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterStudents());
        levelFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterStudents());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> filterStudents());
    }

    private void filterStudents() {
        loadStudentData(); // Basic implementation - reloads all data
    }

    @FXML
    private void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/add_student_dialog.fxml"));
            Parent root = loader.load();

            AddStudentController dialogController = loader.getController();
            dialogController.setStudentService(studentService);

            Stage dialogStage = new Stage();
            dialogController.setDialogStage(dialogStage);
            dialogStage.setTitle("Add New Student");
            dialogStage.setScene(new Scene(root));
            dialogStage.initOwner(mainStage);

            dialogStage.showAndWait();
            loadStudentData();

        } catch (IOException e) {
            System.err.println("Error loading add student dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleViewStudent(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText("Details for: " + student.getFullName());
        alert.setContentText(
                "ID: " + student.getStudentId() + "\n" +
                        "Programme: " + student.getProgramme() + "\n" +
                        "Level: " + student.getLevel() + "\n" +
                        "GPA: " + student.getGpa() + "\n" +
                        "Email: " + student.getEmail() + "\n" +
                        "Phone: " + student.getPhoneNumber() + "\n" +
                        "Status: " + student.getStatus()
        );
        alert.showAndWait();
    }
}