package com.brngbn.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client stub that exposes methods for each type of query.
 * Internally, these methods open a socket, send the query,
 * wait for a response, and return/print it.
 */
public class ClientStub {

    private String serverHost;
    private int serverPort;

    public ClientStub(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Sends a "prime-path a b" query to the server.
     */
    public String primePath(String a, String b) {
        String query = "prime-path " + a + " " + b;
        return sendQuery(query);
    }

    /**
     * Sends a "shortest-path a b" query to the server.
     */
    public String shortestPath(String a, String b) {
        String query = "shortest-path " + a + " " + b;
        return sendQuery(query);
    }

    /**
     * A general helper method to connect, send a query,
     * and read the server response.
     */
    private String sendQuery(String query) {
        String response = null;

        try (Socket socket = new Socket(serverHost, serverPort);
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            // Send the query
            out.println(query);

            // Read the response
            response = in.readLine();
        } catch (Exception e) {
            System.err.println("ClientStub encountered an error: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Simple test usage:
     *  1. primePath("3", "7")
     *  2. shortestPath("3", "7")
     */
    public static void main(String[] args) {
        // Adjust host/port as needed
        ClientStub clientStub = new ClientStub("localhost", 9999);

        String primePathResult = clientStub.primePath("3", "7");
        System.out.println("Response (prime-path): " + primePathResult);

        String shortestPathResult = clientStub.shortestPath("3", "7");
        System.out.println("Response (shortest-path): " + shortestPathResult);
    }
}
