/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.example.GR3WORK;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Hyperlink forgotLink;
    @FXML private Button LoginButton; // Ensure this fx:id matches your FXML for the login button
    
    // Assuming LoginClientSocket is a class you have defined for client-server communication
    private LoginClientSocket loginClient;

    /**
     * Handles the login button action. This method is called when the login button
     * (linked via onAction="#handleLogin" in FXML) is clicked.
     * It must be public or protected to be accessible from FXML.
     * @throws java.io.IOException
     */
    @FXML // Keep @FXML if it's directly linked from FXML via onAction
    public void handleLogin() throws IOException { // Changed to public
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter username and password.");
            return;
        }
        
        // Initialize loginClient only if it's null (lazy initialization)
        // Or, if it's meant to be a new connection each time, keep it as is.
        // For simplicity in this example, we'll create a new one.
        loginClient = new LoginClientSocket();
        String response = loginClient.login(username, password);
        
        if (response == null) {
            showAlert(Alert.AlertType.ERROR, "Connection Error", "Failed to connect to server. Please try again.");
            return;
        }
        
        if (loginClient.isLoginSuccessful(response)) {
            String role = loginClient.getUserRole(response);
            
            // Load appropriate dashboard based on role
            if ("Doctor".equals(role)) {
                loadDashboard("/com/example/GR3WORK/DoctorUI.fxml", "Doctor Dashboard");
            } else if ("Receptionist".equals(role)) {
                loadDashboard("/com/example/GR3WORK/ReceptionistUI.fxml", "Receptionist Dashboard");
            } else {
                showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid user role.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Invalid username or password.");
        }
    }    

    /**
     * Handles the "Forgot Password" hyperlink action.
     */
    @FXML
    private void handleforgotLink() {
        showAlert(Alert.AlertType.INFORMATION, "Password Recovery", 
                  "Please contact admin to reset your password.");
    }

    /**
     * Helper method to load a new dashboard scene.
     * @param fxmlPath The path to the FXML file for the dashboard.
     * @param title The title for the new stage.
     */
    private void loadDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            
            // Hide the current login window
            LoginButton.getScene().getWindow().hide();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
            
        }
    }

    /**
     * Helper method to display an alert dialog.
     * @param type The type of alert (e.g., ERROR, INFORMATION).
     * @param title The title of the alert dialog.
     * @param content The main message content of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(content);
        alert.showAndWait(); // showAndWait makes the alert modal
    }
}
