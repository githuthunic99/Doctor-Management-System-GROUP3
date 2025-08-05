package com.example.GR3WORK.Server;

import java.io.*;
import java.net.*;
import java.sql.*;

public class LoginServer {
    private static final int LOGIN_PORT = 5000;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/mataibu_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(LOGIN_PORT)) {
            System.out.println("Login Server started on port " + LOGIN_PORT + ". Waiting for GUI...");

            while (true) {
                // 1. Accept a GUI connection
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected for login: " + clientSocket.getInetAddress());

                // 2. Handle client in a new thread
                new Thread(() -> handleLoginClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleLoginClient(Socket socket) {
        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
        ) {
            // 3. Read login details from GUI
            String username = dis.readUTF();
            String password = dis.readUTF();

            // 4. Detect role based on prefix
            String role = detectRoleFromUsername(username);

            if (role.equals("Unknown")) {
                dos.writeUTF("INVALID");
                return;
            }

            // 5. Choose the right table based on role
            String tableName = role.equals("Receptionist") ? "receptionists" : "doctors";
            String query = "SELECT * FROM " + tableName + " WHERE username=? AND password=?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    dos.writeUTF(role); // Tell GUI to open the correct dashboard
                    System.out.println("Login success for " + username + " as " + role);
                } else {
                    dos.writeUTF("INVALID");
                    System.out.println("Login failed for " + username);
                }
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // Detect role based on username prefix
    private static String detectRoleFromUsername(String username) {
        if (username.toLowerCase().startsWith("#rcp")) {
            return "Receptionist";
        } else if (username.toLowerCase().startsWith("#dct")) {
            return "Doctor";
        } else {
            return "Unknown";
        }
    }
}
    
