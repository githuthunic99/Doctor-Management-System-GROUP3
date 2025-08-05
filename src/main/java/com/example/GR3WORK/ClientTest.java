package com.example.GR3WORK;

/**
 * Test class to demonstrate client socket functionality
 */
public class ClientTest {
    
    public static void main(String[] args) {
        System.out.println("=== Doctor Management System Client Test ===\n");
        
        // Test Login Client
        testLoginClient();
        
        // Test Receptionist Client
        testReceptionistClient();
        
        // Test Doctor Client
        testDoctorClient();
    }
    
    private static void testLoginClient() {
        System.out.println("--- Testing Login Client ---");
        
        LoginClientSocket loginClient = new LoginClientSocket("10.66.42.237");
        
        // Test valid doctor login
        String response = loginClient.login("#dct001", "password123");
        System.out.println("Doctor login response: " + response);
        
        // Test valid receptionist login
        response = loginClient.login("#rcp001", "password123");
        System.out.println("Receptionist login response: " + response);
        
        // Test invalid login
        response = loginClient.login("invalid", "wrong");
        System.out.println("Invalid login response: " + response);
        
        loginClient.disconnect();
        System.out.println();
    }
    
    private static void testReceptionistClient() {
        System.out.println("--- Testing Receptionist Client ---");
        
        ReceptionistClientSocket receptionistClient = new ReceptionistClientSocket("10.66.42.237");
        
        // Test patient registration
        boolean success = receptionistClient.registerPatient("John", "Doe", "15-01-1990");
        System.out.println("Patient registration: " + (success ? "SUCCESS" : "FAILED"));
        
        // Test adding to queue
        success = receptionistClient.addToQueue(1, 1);
        System.out.println("Add to queue: " + (success ? "SUCCESS" : "FAILED"));
        
        // Test viewing queue
        java.util.List<String> queue = receptionistClient.viewQueue(1);
        if (queue != null) {
            System.out.println("Queue items: " + queue.size());
            for (String item : queue) {
                System.out.println("  - " + item);
            }
        } else {
            System.out.println("Failed to load queue");
        }
        
        receptionistClient.exit();
        System.out.println();
    }
    
    private static void testDoctorClient() {
        System.out.println("--- Testing Doctor Client ---");
        
        DoctorClientSocket doctorClient = new DoctorClientSocket("10.66.42.237");
        
        // Initialize with doctor username
        boolean initialized = doctorClient.initialize("#dct001");
        System.out.println("Doctor client initialized: " + initialized);
        
        if (initialized) {
            // Test viewing history
            String history = doctorClient.viewHistory(1);
            System.out.println("Patient history: " + (history != null ? history.substring(0, Math.min(50, history.length())) + "..." : "null"));
            
            // Test adding history
            boolean success = doctorClient.addHistory(1, "Patient shows symptoms of common cold. Prescribed rest and fluids.");
            System.out.println("Add history: " + (success ? "SUCCESS" : "FAILED"));
            
            // Test updating history
            success = doctorClient.updateHistory(1, "Updated notes: Patient shows symptoms of common cold. Prescribed rest, fluids, and vitamin C.");
            System.out.println("Update history: " + (success ? "SUCCESS" : "FAILED"));
        }
        
        doctorClient.exit();
        System.out.println();
    }
} 