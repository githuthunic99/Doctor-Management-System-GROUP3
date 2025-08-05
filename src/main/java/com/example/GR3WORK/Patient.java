/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.GR3WORK;

/**
 *
 * @author Asus
 */

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A simple model class to represent a Patient.
 * This class uses JavaFX properties to allow the TableView to observe changes.
 */
public class Patient {
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty status;
    private final StringProperty dob;
    private final ReadOnlyStringWrapper fullName;

    public Patient(String firstName, String lastName, String dob, String status) {
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);
        this.dob = new SimpleStringProperty(dob);
        this.status = new SimpleStringProperty(status);

        // A ReadOnlyStringWrapper is used for a computed property like fullName
        this.fullName = new ReadOnlyStringWrapper();
        this.fullName.bind(Bindings.concat(this.firstName, " ", this.lastName));
    }

    // Getters for the data
    public String getFirstName() { return firstName.get(); }
    public String getLastName() { return lastName.get(); }
    public String getDob() { return dob.get(); }
    public String getStatus() { return status.get(); }
    public String getFullName() { return fullName.get(); }

    // Property methods for the TableView
    public StringProperty firstNameProperty() { return firstName; }
    public StringProperty lastNameProperty() { return lastName; }
    public StringProperty dobProperty() { return dob; }
    public StringProperty statusProperty() { return status; }
    public ReadOnlyStringProperty fullNameProperty() { return fullName.getReadOnlyProperty(); }
}
