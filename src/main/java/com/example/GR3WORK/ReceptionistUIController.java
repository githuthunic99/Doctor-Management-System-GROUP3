/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.GR3WORK;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.cell.PropertyValueFactory;

public class ReceptionistUIController implements Initializable {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField dobField;
    @FXML private Button registerButton;
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private VBox searchResultsVBox;
    @FXML private Button logOutButton;

    @FXML private Label queueHeaderLabel; 
    @FXML private TableView<Patient> queueTable;
    @FXML private TableColumn<Patient, String> patientNameColumn;
    @FXML private TableColumn<Patient, String> statusColumn;
    @FXML private Button removeFromQueueButton;
    @FXML private Button markAsSeenButton;

    private final ObservableList<Patient> patientQueue = FXCollections.observableArrayList();
    private ReceptionistClientSocket clientSocket;

    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        clientSocket = new ReceptionistClientSocket();

        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        queueTable.setItems(patientQueue);

        loadQueueData();
        queueHeaderLabel.setText("Patient Queue for Dr. Williams"); // ✅ Now works
    }

    @FXML
    private void handleRegisterPatient() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String dobString = dobField.getText();

        if (firstName.isEmpty() || lastName.isEmpty() || dobString.isEmpty()) {
            showErrorAlert("Validation Error", "Please fill out all fields.");
            return;
        }

        try {
            LocalDate.parse(dobString, DOB_FORMATTER);
            boolean success = clientSocket.registerPatient(firstName, lastName, dobString);
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient registered successfully!");
                firstNameField.clear(); lastNameField.clear(); dobField.clear();
            } else {
                showErrorAlert("Registration Error", "Failed to register patient. Please try again.");
            }
        } catch (DateTimeParseException e) {
            showErrorAlert("Invalid Date Format", "Use dd-MM-yyyy, e.g., 15-01-1990");
        }
    }

    @FXML
    private void handleSearchPatient() {
        String searchQuery = searchField.getText();
        if (searchQuery.isEmpty()) {
            showErrorAlert("Search Error", "Search field cannot be empty.");
            return;
        }

        searchResultsVBox.getChildren().clear();
        Label resultLabel = new Label("Found: " + searchQuery + " (ID: 12345)");
        searchResultsVBox.getChildren().add(resultLabel);
    }

    @FXML
    private void handleRemoveFromQueue() {
        Patient selectedPatient = queueTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            patientQueue.remove(selectedPatient);
        } else {
            showErrorAlert("No Selection", "Please select a patient to remove.");
        }
    }

    @FXML
    private void handleMarkAsSeen() {
        Patient selectedPatient = queueTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            selectedPatient.statusProperty().set("Seen");
            queueTable.refresh();
        } else {
            showErrorAlert("No Selection", "Please select a patient to mark as seen.");
        }
    }

    @FXML
    private void handleLogout() throws Exception {
        if (clientSocket != null) clientSocket.exit();
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        stage.close();
    }

    private void loadQueueData() {
        int doctorId = 1;
        List<String> queueData = clientSocket.viewQueue(doctorId);
        if (queueData != null) {
            patientQueue.clear();
            for (String queueItem : queueData) {
                String[] parts = queueItem.split(" - ");
                if (parts.length == 2) {
                    String[] nameParts = parts[0].split(" ");
                    if (nameParts.length >= 2) {
                        patientQueue.add(new Patient(nameParts[0], nameParts[1], "", parts[1]));
                    }
                }
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
}
