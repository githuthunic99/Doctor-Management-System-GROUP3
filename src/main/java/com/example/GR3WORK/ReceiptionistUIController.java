/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.GR3WORK;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Asus
 */
public class ReceiptionistUIController implements Initializable {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField dobField;
    @FXML
    private Button registerButton;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private VBox searchResultsVBox;
    @FXML
    private Button logOutButton;
    @FXML
    private VBox queueHeaderLabel;
    @FXML
    private TableView<Patient> queueTable;
    @FXML
    private TableColumn<Patient, String> patientNameColumn;
    @FXML
    private TableColumn<Patient, String> statusColumn;
    @FXML
    private Button removeFromQueueButton;
    @FXML
    private Button markAsSeenButton;
    
    // Observable list to hold the patients in the queue
    private final ObservableList<Patient> patientQueue = FXCollections.observableArrayList();
    
    // Client socket for server communication
    private ReceptionistClientSocket clientSocket;

    // Define the date format to be used
    private static final DateTimeFormatter DOB_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /**
     * Initializes the controller class.
     * 
     * @param url
     * @param rb
     */
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize client socket
        clientSocket = new ReceptionistClientSocket();
        
        // Set up the table columns to bind to the Patient model's properties
        // We use PropertyValueFactory for simplicity and direct mapping to the model's getters
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Set the table's items to the observable list
        queueTable.setItems(patientQueue);
        
        // Load queue data from server
        loadQueueData();
        
        // You can also set a specific doctor's name here
        queueHeaderLabel.setText("Patient Queue for Dr. Williams");
    }

    /**
     * Handles the "Register Patient" button action.
     * This method registers a new patient via the server.
     */
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
            // Attempt to parse the date with the specified format
            LocalDate dob = LocalDate.parse(dobString, DOB_FORMATTER);

            // Register patient via server
            boolean success = clientSocket.registerPatient(firstName, lastName, dobString);
            
            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient registered successfully!");
                
                // Clear the fields after successful registration
                firstNameField.clear();
                lastNameField.clear();
                dobField.clear();
            } else {
                showErrorAlert("Registration Error", "Failed to register patient. Please try again.");
            }
        } catch (DateTimeParseException e) {
            // Show an alert if the date format is incorrect
            showErrorAlert("Invalid Date Format", "The date of birth must be in dd-MM-yyyy format. Example: 15-01-1990");
        }
    }

    /**
     * Handles the "Search" button action.
     * This method would query a database for a patient by name or ID.
     */
    @FXML
    private void handleSearchPatient() {
        String searchQuery = searchField.getText();
        if (searchQuery.isEmpty()) {
            System.out.println("Error: Search field cannot be empty.");
            return;
        }

        // Dummy logic: clear previous results and display a simulated result
        searchResultsVBox.getChildren().clear();
        System.out.println("Searching for: " + searchQuery);

        // For a real application, this would involve a database query.
        // For demonstration, we'll just add a label to the search results VBox.
        Label resultLabel = new Label("Found: " + searchQuery + " (ID: 12345)");
        searchResultsVBox.getChildren().add(resultLabel);
    }
    
    /**
     * Handles the "Remove From Queue" button action.
     */
    @FXML
    private void handleRemoveFromQueue() {
        Patient selectedPatient = queueTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            patientQueue.remove(selectedPatient);
            System.out.println("Removed patient from queue: " + selectedPatient.getFullName());
        } else {
            System.out.println("No patient selected to remove.");
        }
    }

    /**
     * Handles the "Mark as Seen" button action.
     */
    @FXML
    private void handleMarkAsSeen() {
        Patient selectedPatient = queueTable.getSelectionModel().getSelectedItem();
        if (selectedPatient != null) {
            // Here you would update the patient's status
            selectedPatient.statusProperty().set("Seen");
            System.out.println("Marked patient as seen: " + selectedPatient.getFullName());
        } else {
            System.out.println("No patient selected to mark as seen.");
        }
    }
    
    /**
     * Handles the "Logout" button action.
     */
    @FXML
    private void handleLogout() throws Exception {
        System.out.println("User logging out from Receptionist Dashboard...");
        
        // Disconnect from server
        if (clientSocket != null) {
            clientSocket.exit();
        }
        
        Stage stage = (Stage) logOutButton.getScene().getWindow();
        stage.close();
        
        // This is a placeholder for redirecting to a login screen
        // Parent root = FXMLLoader.load(getClass().getResource("/com/example/login/login.fxml"));
        // Stage newStage = new Stage();
        // newStage.setScene(new Scene(root));
        // newStage.setTitle("Login");
        // newStage.show();
    }

    /**
     * Load queue data from server
     */
    private void loadQueueData() {
        // For now, we'll use a default doctor ID of 1
        // In a real application, this would be the logged-in doctor's ID
        int doctorId = 1;
        
        List<String> queueData = clientSocket.viewQueue(doctorId);
        if (queueData != null) {
            patientQueue.clear();
            for (String queueItem : queueData) {
                // Parse queue item format: "FirstName LastName - Status"
                String[] parts = queueItem.split(" - ");
                if (parts.length == 2) {
                    String[] nameParts = parts[0].split(" ");
                    if (nameParts.length >= 2) {
                        String firstName = nameParts[0];
                        String lastName = nameParts[1];
                        String status = parts[1];
                        patientQueue.add(new Patient(firstName, lastName, "", status));
                    }
                }
            }
        }
    }
    
    /**
     * Show an alert to the user.
     * @param type The type of alert.
     * @param title The title of the alert.
     * @param message The message to display.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Utility method to show an error alert to the user.
     * @param title The title of the alert.
     * @param message The message to display.
     */
    private void showErrorAlert(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, message);
    }
}
