package com.example.GR3WORK;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App2 extends Application {

    // The start method is the main entry point for all JavaFX applications.
    @Override
    public void start(Stage primaryStage) throws IOException {
        // Load the FXML file. The FXMLLoader reads the file and creates a Parent object.
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ReceptionistUI.fxml"));
        Parent root = loader.load();

        // The controller is automatically linked by the FXMLLoader based on the fx:controller attribute
        // in your FXML file, so you don't need to manually create an instance here.

        // Create a new scene with the loaded root node.
        Scene scene = new Scene(root);

        // Set the scene on the primary stage.
        primaryStage.setTitle("Receptionist Dashboard");
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


