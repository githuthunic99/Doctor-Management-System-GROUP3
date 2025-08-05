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

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         // Set up the TableView with data and cell factories using PropertyValueFactory.
       // This method looks for a getter method with the name "getFullName()" and "getStatus()"
        patientNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Add some dummy data to the queue for demonstration
        patientQueue.add(new Patient("Jane", "Doe", "21-05-1995", "Waiting"));
        patientQueue.add(new Patient("John", "Smith", "10-12-1988", "In Progress"));
        patientQueue.add(new Patient("Emily", "White", "04-03-2001", "Waiting"));

        queueTable.setItems(patientQueue);
        
        System.out.println("DoctorUIController initialized successfully.");
        // TODO
    }    

    @FXML
    private void handleStartConsultation(ActionEvent event) {
         System.out.println("Starting a new consultation.");
        // Add logic to handle starting a consultation, e.g., enabling text fields.
    }

    @FXML
    private void handleEndConsultation(ActionEvent event) {
        System.out.println("Ending the current consultation.");
        // Add logic to handle ending a consultation, e.g., disabling text fields.
    }
    

    @FXML
    private void handleSaveNotes(MouseEvent event) {
         System.out.println("Saving consultation notes.");
        // Add logic to save the notes to a database.
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("User logging out.");
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        
        // You would typically redirect to a login screen here.
        // Parent root = FXMLLoader.load(getClass().getResource("/com/example/login/login.fxml"));
        // Stage newStage = new Stage();
        // newStage.setScene(new Scene(root));
        // newStage.setTitle("Login");
        // newStage.show();
    }

    private static class statusColumn {

        private static void setCellValueFactory(PropertyValueFactory<Object, Object> propertyValueFactory) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        public statusColumn() {
        }
    }
    
}
