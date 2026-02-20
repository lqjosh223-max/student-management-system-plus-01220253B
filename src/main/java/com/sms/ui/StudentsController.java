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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class StudentsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> programmeFilter;
    @FXML private ComboBox<String> levelFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Button addStudentButton;
    @FXML private Button editStudentButton;
    @FXML private Button deleteStudentButton;
    @FXML private Button refreshButton;
    @FXML private Button backButton;
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

    @FXML private Button dashboardNavButton;
    @FXML private Button studentsNavButton;
    @FXML private Button reportsNavButton;
    @FXML private Button importExportNavButton;
    @FXML private Button settingsNavButton;

    private StudentService studentService;
    private ObservableList<Student> studentData;
    private Stage mainStage;
    private DashboardController dashboardController;

    public void initialize() {
        try {
            SQLiteStudentRepository repo = new SQLiteStudentRepository();
            studentService = new StudentService(repo);

            levelFilter.getItems().addAll("All", "100", "200", "300", "400", "500", "600", "700");
            levelFilter.setValue("All");
            
            statusFilter.getItems().addAll("All", "Active", "Inactive");
            statusFilter.setValue("All");
            
            programmeFilter.getItems().addAll("All", "Computer Science", "Business Administration", "Engineering", "Mathematics", "Physics");
            programmeFilter.setValue("All");

            setupTableColumns();
            setupButtonActions();
            setupSearchAndFilter();
            loadStudentData();

            System.out.println("Students screen loaded successfully");

        } catch (Exception e) {
            System.err.println("Error initializing Students screen: " + e.getMessage());
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
        
        fullNameCol.setSortable(true);
        gpaCol.setSortable(true);
        
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final HBox container = new HBox(5);
            private final Button viewBtn = new Button("View");
            private final Button editBtn = new Button("Edit");
            
            {
                viewBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 11px;");
                editBtn.setStyle("-fx-background-color: #2a5d44; -fx-text-fill: white; -fx-font-size: 11px;");
                
                viewBtn.setOnAction(e -> handleViewStudent(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e -> handleEditStudent(getTableView().getItems().get(getIndex())));
                
                container.getChildren().addAll(viewBtn, editBtn);
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
    }

    private void loadStudentData() {
        try {
            List<Student> allStudents = studentService.getAllStudents();
            
            String programme = programmeFilter.getValue();
            String levelStr = levelFilter.getValue();
            String status = statusFilter.getValue();
            String searchTerm = searchField.getText().toLowerCase();
            
            List<Student> filtered = allStudents.stream()
                .filter(student -> {
                    if (!"All".equals(programme) && !student.getProgramme().equals(programme)) {
                        return false;
                    }
                    if (!"All".equals(levelStr)) {
                        int level = Integer.parseInt(levelStr);
                        if (student.getLevel() != level) {
                            return false;
                        }
                    }
                    if (!"All".equals(status) && !student.getStatus().equals(status)) {
                        return false;
                    }
                    if (!searchTerm.isEmpty()) {
                        return student.getStudentId().toLowerCase().contains(searchTerm) ||
                               student.getFullName().toLowerCase().contains(searchTerm);
                    }
                    return true;
                })
                .collect(Collectors.toList());
            
            studentData = FXCollections.observableArrayList(filtered);
            studentTable.setItems(studentData);
            
        } catch (Exception e) {
            System.err.println("Error loading student data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupSearchAndFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadStudentData());
        programmeFilter.valueProperty().addListener((observable, oldValue, newValue) -> loadStudentData());
        levelFilter.valueProperty().addListener((observable, oldValue, newValue) -> loadStudentData());
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> loadStudentData());
    }

    private void setupButtonActions() {
        backButton.setOnAction(e -> navigateToDashboard());
        dashboardNavButton.setOnAction(e -> navigateToDashboard());
        studentsNavButton.setOnAction(e -> {});
        reportsNavButton.setOnAction(e -> navigateToReports());
        importExportNavButton.setOnAction(e -> navigateToImportExport());
        settingsNavButton.setOnAction(e -> navigateToSettings());
        
        addStudentButton.setOnAction(e -> handleAddStudent());
        editStudentButton.setOnAction(e -> handleEditSelectedStudent());
        deleteStudentButton.setOnAction(e -> handleDeleteStudent());
        refreshButton.setOnAction(e -> loadStudentData());
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();
            
            DashboardController controller = loader.getController();
            controller.setMainStage(mainStage);
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());
            
            mainStage.setScene(scene);
            
            // Refresh dashboard stats
            controller.refreshStats();
            
        } catch (IOException e) {
            System.err.println("Error navigating to dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToReports() {
        navigateToScreen("/fxml/reports.fxml", "Reports");
    }

    private void navigateToImportExport() {
        navigateToScreen("/fxml/import_export.fxml", "Import/Export");
    }

    private void navigateToSettings() {
        navigateToScreen("/fxml/settings.fxml", "Settings");
    }

    private void navigateToScreen(String fxmlPath, String screenName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof ReportsController) {
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
        }
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
            showError("Dialog Error", "Failed to open add student dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditStudent(Student student) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/edit_student_dialog.fxml"));
            Parent root = loader.load();

            EditStudentController dialogController = loader.getController();
            dialogController.setStudentService(studentService);
            dialogController.setStudent(student);

            Stage dialogStage = new Stage();
            dialogController.setDialogStage(dialogStage);
            dialogStage.setTitle("Edit Student: " + student.getFullName());
            dialogStage.setScene(new Scene(root));
            dialogStage.initOwner(mainStage);

            dialogStage.showAndWait();
            loadStudentData();

        } catch (IOException e) {
            System.err.println("Error loading edit student dialog: " + e.getMessage());
            e.printStackTrace();
            showError("Dialog Error", "Failed to open edit student dialog: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditSelectedStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection Error", "Please select a student to edit");
            return;
        }
        handleEditStudent(selected);
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Selection Error", "Please select a student to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Student: " + selected.getFullName());
        confirm.setContentText("This will mark the student as 'Inactive' (soft delete). Are you sure?");
        confirm.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    // Soft delete - update status to Inactive
                    Student inactiveStudent = new Student(
                            selected.getStudentId(),
                            selected.getFullName(),
                            selected.getProgramme(),
                            selected.getLevel(),
                            selected.getGpa(),
                            selected.getEmail(),
                            selected.getPhoneNumber(),
                            selected.getDateAdded(),
                            "Inactive"
                    );
                    studentService.updateStudent(inactiveStudent);
                    showAlert("Success", "Student marked as Inactive");
                    loadStudentData();
                } catch (Exception e) {
                    System.err.println("Error deleting student: " + e.getMessage());
                    e.printStackTrace();
                    showError("Delete Error", "Failed to delete student: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleViewStudent(Student student) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Student Details");
        alert.setHeaderText("Details for: " + student.getFullName());
        alert.setContentText(
                "Student ID: " + student.getStudentId() + "\n" +
                        "Programme: " + student.getProgramme() + "\n" +
                        "Level: " + student.getLevel() + "\n" +
                        "GPA: " + student.getGpa() + "\n" +
                        "Email: " + student.getEmail() + "\n" +
                        "Phone: " + student.getPhoneNumber() + "\n" +
                        "Status: " + student.getStatus() + "\n" +
                        "Date Added: " + student.getDateAdded()
        );
        alert.showAndWait();
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

    public void setMainStage(Stage stage) {
        this.mainStage = stage;
    }

    public void setDashboardController(DashboardController controller) {
        this.dashboardController = controller;
    }
}