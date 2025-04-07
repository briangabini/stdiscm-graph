package com.brngbn.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Server stub that listens for incoming queries.
 * On receiving a query, it parses the command and args,
 * calls the local procedure, and sends back the result.
 */
public class ServerStub {

    private int port;

    public ServerStub(int port) {
        this.port = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port + "...");

            // Simple loop accepting one client at a time in this example
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    // Read the query (e.g., "prime-path 3 7")
                    String query = in.readLine();
                    if (query == null) {
                        continue;
                    }
                    System.out.println("Received query: " + query);

                    // Parse query
                    String[] parts = query.split("\\s+");
                    if (parts.length < 3) {
                        out.println("Error: malformed query");
                        continue;
                    }

                    String command = parts[0];  // e.g. "prime-path" or "shortest-path"
                    String a = parts[1];        // e.g. "3"
                    String b = parts[2];        // e.g. "7"

                    // Dispatch to local procedure
                    String result;
                    switch (command) {
                        case "prime-path":
                            result = handlePrimePath(a, b);
                            break;
                        case "shortest-path":
                            result = handleShortestPath(a, b);
                            break;
                        default:
                            result = "Error: unknown command '" + command + "'";
                    }

                    // Send result back to client
                    out.println(result);
                    System.out.println("Response sent: " + result);

                } catch (Exception e) {
                    System.err.println("Error handling client: " + e.getMessage());
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            System.err.println("Server failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Placeholder method for a local parallel procedure for prime-path
    private String handlePrimePath(String a, String b) {
        // TODO: Implement your actual parallel algorithm here
        // For now, just return a mock result
        return "Computed prime-path for " + a + " -> " + b + " (placeholder result)";
    }

    // Placeholder method for a local parallel procedure for shortest-path
    private String handleShortestPath(String a, String b) {
        // TODO: Implement your actual parallel algorithm here
        // For now, just return a mock result
        return "Computed shortest-path for " + a + " -> " + b + " (placeholder result)";
    }

    /**
     * A simple entry point to start the server.
     */
    public static void main(String[] args) {
        ServerStub serverStub = new ServerStub(9999);
        serverStub.startServer();
    }
}
