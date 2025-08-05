package com.example.GR3WORK.Server;

import java.io.*;
import java.net.*;
import java.sql.*;
import java.util.*;

public class ReceptionistServer {
    private static final int RECEPTIONIST_PORT = 5001; 
    private static final String DB_URL = "jdbc:mysql://10.66.42.237:3306/mataibu_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password";

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(RECEPTIONIST_PORT)) {
            System.out.println("Receptionist server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Receptionist client connected!");

                new Thread(new ReceptionistHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static class ReceptionistHandler implements Runnable {
        private Socket socket;

        ReceptionistHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                DataInputStream dis = new DataInputStream(socket.getInputStream());
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)
            ) {
                boolean running = true;

                while (running) {
                    String request = dis.readUTF();

                    switch (request) {
                        case "REGISTER_PATIENT":
                            registerPatient(dis, dos, conn);
                            break;

                        case "ADD_TO_QUEUE":
                            addToQueue(dis, dos, conn);
                            break;

                        case "VIEW_QUEUE":
                            viewQueue(dis, dos, conn);
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

        private void registerPatient(DataInputStream dis, DataOutputStream dos, Connection conn) throws IOException, SQLException {
            String firstName = dis.readUTF();
            String lastName = dis.readUTF();
            String dob = dis.readUTF();

            String query = "INSERT INTO patients (first_name, last_name, dob) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, firstName);
                stmt.setString(2, lastName);
                stmt.setString(3, dob);

                int rows = stmt.executeUpdate();
                dos.writeUTF(rows > 0 ? "SUCCESS" : "FAIL");
            }
        }

        private void addToQueue(DataInputStream dis, DataOutputStream dos, Connection conn) throws IOException, SQLException {
            int patientId = dis.readInt();
            int doctorId = dis.readInt();

            String query = "INSERT INTO doctor_queue (doctor_id, patient_id, status) VALUES (?, ?, 'Waiting')";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, doctorId);
                stmt.setInt(2, patientId);

                int rows = stmt.executeUpdate();
                dos.writeUTF(rows > 0 ? "ADDED" : "FAIL");
            }
        }

        private void viewQueue(DataInputStream dis, DataOutputStream dos, Connection conn) throws IOException, SQLException {
            int doctorId = dis.readInt();

            String query = "SELECT p.first_name, p.last_name, q.status " +
                           "FROM doctor_queue q JOIN patients p ON q.patient_id = p.id " +
                           "WHERE q.doctor_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, doctorId);
                ResultSet rs = stmt.executeQuery();

                List<String> queue = new ArrayList<>();
                while (rs.next()) {
                    queue.add(rs.getString("first_name") + " " + rs.getString("last_name") +
                              " - " + rs.getString("status"));
                }

                dos.writeInt(queue.size());
                for (String item : queue) {
                    dos.writeUTF(item);
                }
            }
        }
    }
}

