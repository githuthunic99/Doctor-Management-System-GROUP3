package com.example.GR3WORK.Server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DoctorServer {
    private static final int PORT = 5002; // separate port for doctors
    private static final String DB_URL = "jdbc:mysql://10.66.42.237:3306/mataibu_db";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "your_password";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Doctor Server running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Doctor client connected.");

                new Thread(() -> handleDoctorClient(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleDoctorClient(Socket socket) {
        try (
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)
        ) {
            boolean running = true;
            String doctorUsername = dis.readUTF(); // doctor sends username first

            while (running) {
                String request = dis.readUTF();

                switch (request) {
                    case "VIEW_HISTORY":
                        viewHistory(dis, dos, conn);
                        break;

                    case "ADD_HISTORY":
                        addHistory(dis, dos, conn, doctorUsername);
                        break;

                    case "UPDATE_HISTORY":
                        updateHistory(dis, dos, conn, doctorUsername);
                        break;

                    case "EXIT":
                        running = false;
                        break;

                    default:
                        dos.writeUTF("ERROR: Unknown request");
                }
            }

        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    // 1. View all history for a patient
    private static void viewHistory(DataInputStream dis, DataOutputStream dos, Connection conn) throws IOException {
        try {
            int patientId = dis.readInt();
            String sql = "SELECT history_id, doctor_username, notes, created_at FROM medical_history WHERE patient_id=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            StringBuilder result = new StringBuilder();
            while (rs.next()) {
                result.append("[").append(rs.getInt("history_id")).append("] ")
                      .append(rs.getString("doctor_username")).append(": ")
                      .append(rs.getString("notes")).append(" (")
                      .append(rs.getTimestamp("created_at")).append(")\n");
            }

            dos.writeUTF(result.length() > 0 ? result.toString() : "NO_HISTORY");

        } catch (SQLException e) {
            dos.writeUTF("ERROR: " + e.getMessage());
        }
    }

    // 2. Add new history
    private static void addHistory(DataInputStream dis, DataOutputStream dos, Connection conn, String doctorUsername) throws IOException {
        try {
            int patientId = dis.readInt();
            String notes = dis.readUTF();

            String sql = "INSERT INTO medical_history (patient_id, doctor_username, notes, created_at) VALUES (?,?,?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, patientId);
            stmt.setString(2, doctorUsername);
            stmt.setString(3, notes);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rows = stmt.executeUpdate();
            dos.writeUTF(rows > 0 ? "SUCCESS" : "FAIL");

        } catch (SQLException e) {
            dos.writeUTF("ERROR: " + e.getMessage());
        }
    }

    // 3. Update history (only if current doctor created it)
    private static void updateHistory(DataInputStream dis, DataOutputStream dos, Connection conn, String doctorUsername) throws IOException {
        try {
            int historyId = dis.readInt();
            String newNotes = dis.readUTF();

            String checkSql = "SELECT doctor_username FROM medical_history WHERE history_id=?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setInt(1, historyId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                if (!rs.getString("doctor_username").equals(doctorUsername)) {
                    dos.writeUTF("DENIED: Not your entry to update");
                    return;
                }
            } else {
                dos.writeUTF("NOT_FOUND");
                return;
            }

            String updateSql = "UPDATE medical_history SET notes=? WHERE history_id=?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newNotes);
            updateStmt.setInt(2, historyId);

            int rows = updateStmt.executeUpdate();
            dos.writeUTF(rows > 0 ? "UPDATED" : "FAIL");

        } catch (SQLException e) {
            dos.writeUTF("ERROR: " + e.getMessage());
        }
    }
}

