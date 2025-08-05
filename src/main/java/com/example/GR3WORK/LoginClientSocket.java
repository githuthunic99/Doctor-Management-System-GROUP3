package com.example.GR3WORK;

/**
 * Client socket for connecting to the login server
 */
public class LoginClientSocket extends ClientSocket {
    
    private static final String DEFAULT_HOST = "localhost";
    private static final int LOGIN_PORT = 5000;
    
    public LoginClientSocket() {
        super(DEFAULT_HOST, LOGIN_PORT);
    }
    
    public LoginClientSocket(String host) {
        super(host, LOGIN_PORT);
    }
    
    /**
     * Attempt to login with username and password
     * @param username the username
     * @param password the password
     * @return the role if login successful, "INVALID" if failed, null if error
     */
    public String login(String username, String password) {
        if (!isConnected() && !connect()) {
            System.err.println("Failed to connect to login server");
            return null;
        }
        
        try {
            // Send username and password to server
            if (!sendMessage(username)) {
                return null;
            }
            
            if (!sendMessage(password)) {
                return null;
            }
            
            // Receive response from server
            String response = receiveMessage();
            if (response == null) {
                return null;
            }
            
            return response;
            
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Check if the login was successful
     * @param response the response from the server
     * @return true if login successful, false otherwise
     */
    public boolean isLoginSuccessful(String response) {
        return response != null && !response.equals("INVALID");
    }
    
    /**
     * Get the user role from login response
     * @param response the response from the server
     * @return the role (Doctor, Receptionist) or null if invalid
     */
    public String getUserRole(String response) {
        if (isLoginSuccessful(response)) {
            return response;
        }
        return null;
    }
} 