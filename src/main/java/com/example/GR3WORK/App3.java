package com.example.GR3WORK;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App3 extends Application {

    // The start method is the main entry point for all JavaFX applications.
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file for the Doctor's Dashboard.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DoctorUI.fxml"));
        Parent root = loader.load();

        // Create a new scene with the loaded root node.
        Scene scene = new Scene(root);

        // Set the scene on the primary stage.
        primaryStage.setTitle("Doctor's Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The main method is used to launch the application.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
