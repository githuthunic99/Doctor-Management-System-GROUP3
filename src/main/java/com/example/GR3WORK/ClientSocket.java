package com.example.GR3WORK;

import java.io.*;
import java.net.*;

/**
 * Base client socket class for connecting to the server
 */
public class ClientSocket {
    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected String serverHost;
    protected int serverPort;
    
    public ClientSocket(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }
    
    /**
     * Connect to the server
     * @return true if connection successful, false otherwise
     */
    public boolean connect() {
        try {
            socket = new Socket(serverHost, serverPort);
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());
            System.out.println("Connected to server at " + serverHost + ":" + serverPort);
            return true;
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Disconnect from the server
     */
    public void disconnect() {
        try {
            if (dis != null) dis.close();
            if (dos != null) dos.close();
            if (socket != null) socket.close();
            System.out.println("Disconnected from server");
        } catch (IOException e) {
            System.err.println("Error disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Send a string message to the server
     * @param message the message to send
     * @return true if sent successfully, false otherwise
     */
    protected boolean sendMessage(String message) {
        try {
            dos.writeUTF(message);
            return true;
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Receive a string message from the server
     * @return the received message, or null if error
     */
    protected String receiveMessage() {
        try {
            return dis.readUTF();
        } catch (IOException e) {
            System.err.println("Error receiving message: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Send an integer to the server
     * @param value the integer to send
     * @return true if sent successfully, false otherwise
     */
    protected boolean sendInt(int value) {
        try {
            dos.writeInt(value);
            return true;
        } catch (IOException e) {
            System.err.println("Error sending integer: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Receive an integer from the server
     * @return the received integer, or -1 if error
     */
    protected int receiveInt() {
        try {
            return dis.readInt();
        } catch (IOException e) {
            System.err.println("Error receiving integer: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Check if the socket is connected
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
} 