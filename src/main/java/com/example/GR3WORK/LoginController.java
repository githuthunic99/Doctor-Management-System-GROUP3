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
    @FXML private Button LoginButton;
    
    private LoginClientSocket loginClient;

    @FXML
    private void handleLogin() throws IOException {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Login Error", "Please enter username and password.");
            return;
        }
        
        // Create login client and attempt login
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

    @FXML
    private void handleForgotLink() {
        showAlert(Alert.AlertType.INFORMATION, "Password Recovery", 
                "Please contact admin to reset your password.");
    }

    private void loadDashboard(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.show();
            LoginButton.getScene().getWindow().hide();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load dashboard: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.show();
    }

}
    