/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.GR3WORK;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Asus
 */
public class DoctorUIController implements Initializable {

    @FXML
    private Label myPatientQueueLabel;
    @FXML
    private TableView<Patient> queueTable;
    @FXML
    private TableColumn<Patient, String> patientNameColumn;
    @FXML
    private TableColumn<Patient, String> statusNameColumn;
    @FXML
    private Button startConsultationButton;
    @FXML
    private Button endConsultationButton;
    @FXML
    private Label patientRecordsLabel;
    @FXML
    private Label patientNameLabel;
    @FXML
    private Label dobLabel;
    @FXML
    private Label lastVisitLabel;
    @FXML
    private Label medicalHistoryLabel;
    @FXML
    private TextArea medicalHistoryArea;
    @FXML
    private Label newConsultationNotesLabel;
    @FXML
    private TextArea consultationNotesArea;
    @FXML
    private Button saveNotesButton;
    @FXML
    private Button logoutButton;
    
    // A placeholder ObservableList for the queue table
    private final ObservableList<Patient> patientQueue = FXCollections.observableArrayList();
    
    // Client socket for server communication
    private DoctorClientSocket clientSocket;
    
    // Current patient being consulted
    private Patient currentPatient;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize client socket
        clientSocket = new DoctorClientSocket();
        
        // Set up the TableView with data and cell factories using PropertyValueFactory.
        // This method looks for a getter method with the name "getFullName()" and "getStatus()"
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add some dummy data to the queue for demonstration
        // In a real application, this would be loaded from the server
        patientQueue.add(new Patient("Jane", "Doe", "21-05-1995", "Waiting"));
        patientQueue.add(new Patient("John", "Smith", "10-12-1988", "In Progress"));
        patientQueue.add(new Patient("Emily", "White", "04-03-2001", "Waiting"));

        queueTable.setItems(patientQueue);
        
        System.out.println("DoctorUIController initialized successfully.");
    }    

    @FXML
    private void handleStartConsultation(ActionEvent event) {
        System.out.println("Starting a new consultation.");
        
        // Get selected patient from queue
        currentPatient = queueTable.getSelectionModel().getSelectedItem();
        if (currentPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Patient Selected", "Please select a patient from the queue to start consultation.");
            return;
        }
        
        // Load patient's medical history
        loadPatientHistory(currentPatient);
        
        // Enable consultation notes area
        consultationNotesArea.setDisable(false);
        saveNotesButton.setDisable(false);
        
        showAlert(Alert.AlertType.INFORMATION, "Consultation Started", "Consultation started for " + currentPatient.getFullName());
    }

    @FXML
    private void handleEndConsultation(ActionEvent event) {
        System.out.println("Ending the current consultation.");
        
        if (currentPatient != null) {
            // Save any pending notes
            if (!consultationNotesArea.getText().trim().isEmpty()) {
                handleSaveNotes(null);
            }
            
            // Clear current patient
            currentPatient = null;
            
            // Disable consultation notes area
            consultationNotesArea.setDisable(true);
            saveNotesButton.setDisable(true);
            
            showAlert(Alert.AlertType.INFORMATION, "Consultation Ended", "Consultation ended successfully.");
        }
    }
    

    @FXML
    private void handleSaveNotes(MouseEvent event) {
        System.out.println("Saving consultation notes.");
        
        if (currentPatient == null) {
            showAlert(Alert.AlertType.WARNING, "No Patient Selected", "Please select a patient first.");
            return;
        }
        
        String notes = consultationNotesArea.getText().trim();
        if (notes.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Notes", "Please enter consultation notes before saving.");
            return;
        }
        
        // For now, we'll use a default patient ID of 1
        // In a real application, this would be the actual patient ID
        int patientId = 1;
        
        boolean success = clientSocket.addHistory(patientId, notes);
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Notes saved successfully!");
            consultationNotesArea.clear();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to save notes. Please try again.");
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("User logging out.");
        
        // Disconnect from server
        if (clientSocket != null) {
            clientSocket.exit();
        }
        
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        
        // You would typically redirect to a login screen here.
        // Parent root = FXMLLoader.load(getClass().getResource("/com/example/login/login.fxml"));
        // Stage newStage = new Stage();
        // newStage.setScene(new Scene(root));
        // newStage.setTitle("Login");
        // newStage.show();
    }

    /**
     * Load patient's medical history from server
     * @param patient the patient to load history for
     */
    private void loadPatientHistory(Patient patient) {
        // For now, we'll use a default patient ID of 1
        // In a real application, this would be the actual patient ID
        int patientId = 1;
        
        String history = clientSocket.viewHistory(patientId);
        if (history != null && !history.equals("NO_HISTORY")) {
            medicalHistoryArea.setText(history);
        } else {
            medicalHistoryArea.setText("No medical history available for this patient.");
        }
        
        // Update patient information labels
        patientNameLabel.setText(patient.getFullName());
        dobLabel.setText(patient.getDob());
        lastVisitLabel.setText("Last visit: " + java.time.LocalDate.now().toString());
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
    
}
