package com.example.GR3WORK;

/**
 * Client socket for connecting to the doctor server
 */
public class DoctorClientSocket extends ClientSocket {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int DOCTOR_PORT = 5002;
    private String doctorUsername;
    
    public DoctorClientSocket() {
        super(DEFAULT_HOST, DOCTOR_PORT);
    }
    
    public DoctorClientSocket(String host) {
        super(host, DOCTOR_PORT);
    }
    
    /**
     * Initialize connection with doctor username
     * @param username the doctor's username
     * @return true if initialization successful, false otherwise
     */
    public boolean initialize(String username) {
        if (!isConnected() && !connect()) {
            System.err.println("Failed to connect to doctor server");
            return false;
        }
        
        try {
            // Send doctor username first
            if (!sendMessage(username)) {
                return false;
            }
            
            this.doctorUsername = username;
            return true;
            
        } catch (Exception e) {
            System.err.println("Error initializing doctor connection: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * View medical history for a patient
     * @param patientId the patient ID
     * @return the medical history as a string, or null if error
     */
    public String viewHistory(int patientId) {
        if (!isConnected()) {
            System.err.println("Not connected to doctor server");
            return null;
        }
        
        try {
            // Send view history request
            if (!sendMessage("VIEW_HISTORY")) {
                return null;
            }
            
            // Send patient ID
            if (!sendInt(patientId)) {
                return null;
            }
            
            // Receive history
            String history = receiveMessage();
            return history;
            
        } catch (Exception e) {
            System.err.println("Error viewing history: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Add new medical history entry
     * @param patientId the patient ID
     * @param notes the medical notes
     * @return true if added successfully, false otherwise
     */
    public boolean addHistory(int patientId, String notes) {
        if (!isConnected()) {
            System.err.println("Not connected to doctor server");
            return false;
        }
        
        try {
            // Send add history request
            if (!sendMessage("ADD_HISTORY")) {
                return false;
            }
            
            // Send patient ID and notes
            if (!sendInt(patientId)) {
                return false;
            }
            
            if (!sendMessage(notes)) {
                return false;
            }
            
            // Receive response
            String response = receiveMessage();
            return response != null && response.equals("SUCCESS");
            
        } catch (Exception e) {
            System.err.println("Error adding history: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Update existing medical history entry
     * @param historyId the history entry ID
     * @param newNotes the updated notes
     * @return true if updated successfully, false otherwise
     */
    public boolean updateHistory(int historyId, String newNotes) {
        if (!isConnected()) {
            System.err.println("Not connected to doctor server");
            return false;
        }
        
        try {
            // Send update history request
            if (!sendMessage("UPDATE_HISTORY")) {
                return false;
            }
            
            // Send history ID and new notes
            if (!sendInt(historyId)) {
                return false;
            }
            
            if (!sendMessage(newNotes)) {
                return false;
            }
            
            // Receive response
            String response = receiveMessage();
            return response != null && response.equals("UPDATED");
            
        } catch (Exception e) {
            System.err.println("Error updating history: " + e.getMessage());
            return false;
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
    
    /**
     * Get the doctor username
     * @return the doctor username
     */
    public String getDoctorUsername() {
        return doctorUsername;
    }
} 