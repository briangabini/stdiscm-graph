package com.brngbn.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Client stub that exposes methods for graph queries
 * Internally, these methods open a socket, send the query,
 * wait for a response, and return/print it.
 */
public class ClientStub {

    private final String serverHost;
    private final int serverPort;

    public ClientStub(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public String primePath(String a, String b) {
        String query = "prime-path " + a + " " + b;
        return sendQuery(query);
    }

    public String shortestPath(String a, String b) {
        String query = "shortest-path " + a + " " + b;
        return sendQuery(query);
    }

    public String sendQuery(String query) {
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
}
