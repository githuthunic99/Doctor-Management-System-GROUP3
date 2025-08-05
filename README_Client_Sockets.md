# Doctor Management System - Client Socket Implementation

This document describes the client socket implementation that connects the GUI to the server components of the Doctor Management System.

## Overview

The system uses a client-server architecture with three separate servers:
- **Login Server** (Port 5000): Handles user authentication
- **Receptionist Server** (Port 5001): Manages patient registration and queue operations
- **Doctor Server** (Port 5002): Handles medical history operations

## Client Socket Classes

### 1. Base ClientSocket Class
Located in `src/main/java/com/example/GR3WORK/ClientSocket.java`

Provides common socket functionality:
- Connection management
- Message sending/receiving
- Integer data transmission
- Connection status checking

### 2. LoginClientSocket Class
Located in `src/main/java/com/example/GR3WORK/LoginClientSocket.java`

Handles authentication with the login server:
- `login(String username, String password)`: Attempts login and returns role
- `isLoginSuccessful(String response)`: Checks if login was successful
- `getUserRole(String response)`: Extracts user role from response

### 3. ReceptionistClientSocket Class
Located in `src/main/java/com/example/GR3WORK/ReceptionistClientSocket.java`

Manages receptionist operations:
- `registerPatient(String firstName, String lastName, String dob)`: Registers new patient
- `addToQueue(int patientId, int doctorId)`: Adds patient to doctor's queue
- `viewQueue(int doctorId)`: Retrieves queue for specific doctor
- `exit()`: Disconnects from server

### 4. DoctorClientSocket Class
Located in `src/main/java/com/example/GR3WORK/DoctorClientSocket.java`

Handles doctor operations:
- `initialize(String username)`: Initializes connection with doctor username
- `viewHistory(int patientId)`: Retrieves patient's medical history
- `addHistory(int patientId, String notes)`: Adds new medical history entry
- `updateHistory(int historyId, String newNotes)`: Updates existing history entry
- `exit()`: Disconnects from server

## Usage Examples

### Login Process
```java
LoginClientSocket loginClient = new LoginClientSocket("10.66.42.237");
String response = loginClient.login("#dct001", "password123");

if (loginClient.isLoginSuccessful(response)) {
    String role = loginClient.getUserRole(response);
    // Load appropriate dashboard based on role
} else {
    // Show error message
}
```

### Receptionist Operations
```java
ReceptionistClientSocket client = new ReceptionistClientSocket("10.66.42.237");

// Register a new patient
boolean success = client.registerPatient("John", "Doe", "15-01-1990");

// Add patient to queue
success = client.addToQueue(1, 1);

// View queue
List<String> queue = client.viewQueue(1);
```

### Doctor Operations
```java
DoctorClientSocket client = new DoctorClientSocket("10.66.42.237");

// Initialize with doctor username
boolean initialized = client.initialize("#dct001");

if (initialized) {
    // View patient history
    String history = client.viewHistory(1);
    
    // Add new history entry
    boolean success = client.addHistory(1, "Patient shows symptoms of common cold.");
    
    // Update existing history
    success = client.updateHistory(1, "Updated notes with additional information.");
}
```

## GUI Integration

The client sockets are integrated into the GUI controllers:

### LoginController
- Uses `LoginClientSocket` for authentication
- Automatically loads appropriate dashboard based on user role

### ReceptionistUIController
- Uses `ReceptionistClientSocket` for patient registration and queue management
- Loads queue data from server on initialization
- Handles patient registration through server communication

### DoctorUIController
- Uses `DoctorClientSocket` for medical history operations
- Loads patient history when starting consultation
- Saves consultation notes to server

## Error Handling

All client socket classes include comprehensive error handling:
- Connection failures
- Network timeouts
- Invalid responses
- Server communication errors

## Testing

A test class `ClientTest.java` is provided to demonstrate the functionality:
```bash
# Run the test (make sure servers are running first)
java -cp . com.example.GR3WORK.ClientTest
```

## Server Requirements

Before using the client sockets, ensure the corresponding servers are running:
1. LoginServer (Port 5000)
2. ReceptionistServer (Port 5001)
3. DoctorServer (Port 5002)

## IP Configuration

The client sockets are configured to connect to IP address `10.66.42.237`. If you need to change this:
1. Update the `DEFAULT_HOST` constant in each client socket class
2. Update the constructor calls in the test class
3. Update the examples in this documentation

**Note:** The servers are also configured to connect to the MySQL database at `10.66.42.237:3306`. If you need to change this:
1. Update the `DB_URL` constant in each server class (LoginServer, ReceptionistServer, DoctorServer)
2. Ensure the MySQL server is running and accessible on the specified IP and port

## Database Configuration

The servers require a MySQL database named `mataibu_db` with the following tables:
- `doctors`: Doctor user accounts
- `receptionists`: Receptionist user accounts
- `patients`: Patient information
- `doctor_queue`: Patient queue for doctors
- `medical_history`: Patient medical history

## Security Notes

- Passwords should be hashed in production
- Consider implementing SSL/TLS for secure communication
- Add input validation and sanitization
- Implement proper session management

## Future Enhancements

- Add connection pooling for better performance
- Implement automatic reconnection on connection loss
- Add message encryption for sensitive data
- Implement heartbeat mechanism to detect connection status
- Add logging for debugging and monitoring 