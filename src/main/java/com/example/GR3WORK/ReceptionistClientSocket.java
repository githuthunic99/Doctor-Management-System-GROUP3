package com.example.GR3WORK;

import java.util.ArrayList;
import java.util.List;

/**
 * Client socket for connecting to the receptionist server
 */
public class ReceptionistClientSocket extends ClientSocket {
    
    private static final String DEFAULT_HOST = "10.66.42.237";
    private static final int RECEPTIONIST_PORT = 5001;
    
    public ReceptionistClientSocket() {
        super(DEFAULT_HOST, RECEPTIONIST_PORT);
    }
    
    public ReceptionistClientSocket(String host) {
        super(host, RECEPTIONIST_PORT);
    }
    
    /**
     * Register a new patient
     * @param firstName patient's first name
     * @param lastName patient's last name
     * @param dob patient's date of birth
     * @return true if registration successful, false otherwise
     */
    public boolean registerPatient(String firstName, String lastName, String dob) {
        if (!isConnected() && !connect()) {
            System.err.println("Failed to connect to receptionist server");
            return false;
        }
        
        try {
            // Send registration request
            if (!sendMessage("REGISTER_PATIENT")) {
                return false;
            }
            
            // Send patient details
            if (!sendMessage(firstName)) {
                return false;
            }
            
            if (!sendMessage(lastName)) {
                return false;
            }
            
            if (!sendMessage(dob)) {
                return false;
            }
            
            // Receive response
            String response = receiveMessage();
            return response != null && response.equals("SUCCESS");
            
        } catch (Exception e) {
            System.err.println("Error registering patient: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Add a patient to a doctor's queue
     * @param patientId the patient ID
     * @param doctorId the doctor ID
     * @return true if added successfully, false otherwise
     */
    public boolean addToQueue(int patientId, int doctorId) {
        if (!isConnected() && !connect()) {
            System.err.println("Failed to connect to receptionist server");
            return false;
        }
        
        try {
            // Send add to queue request
            if (!sendMessage("ADD_TO_QUEUE")) {
                return false;
            }
            
            // Send patient and doctor IDs
            if (!sendInt(patientId)) {
                return false;
            }
            
            if (!sendInt(doctorId)) {
                return false;
            }
            
            // Receive response
            String response = receiveMessage();
            return response != null && response.equals("ADDED");
            
        } catch (Exception e) {
            System.err.println("Error adding to queue: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * View the queue for a specific doctor
     * @param doctorId the doctor ID
     * @return list of queue items, or null if error
     */
    public List<String> viewQueue(int doctorId) {
        if (!isConnected() && !connect()) {
            System.err.println("Failed to connect to receptionist server");
            return null;
        }
        
        try {
            // Send view queue request
            if (!sendMessage("VIEW_QUEUE")) {
                return null;
            }
            
            // Send doctor ID
            if (!sendInt(doctorId)) {
                return null;
            }
            
            // Receive queue size
            int queueSize = receiveInt();
            if (queueSize < 0) {
                return null;
            }
            
            // Receive queue items
            List<String> queue = new ArrayList<>();
            for (int i = 0; i < queueSize; i++) {
                String item = receiveMessage();
                if (item != null) {
                    queue.add(item);
                }
            }
            
            return queue;
            
        } catch (Exception e) {
            System.err.println("Error viewing queue: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Disconnect from the server
     */
    public void exit() {
        if (isConnected()) {
            sendMessage("EXIT");
            disconnect();
        }
    }
} 