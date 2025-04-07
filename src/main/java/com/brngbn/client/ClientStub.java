package com.brngbn.client;

import com.brngbn.graph.GraphImpl;

import java.io.*;
import java.net.Socket;

public class ClientStub {

    private final String serverHost;
    private final int serverPort;

    public ClientStub(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    /**
     * Uploads a GraphImpl to the server.
     * Uses Object streams to send a command ("UPLOAD_GRAPH") and the serialized graph.
     */
    public String sendGraph(GraphImpl graph) {
        String serverResponse = null;
        try (Socket socket = new Socket(serverHost, serverPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ) {
            oos.flush();

            // Send the upload command
            oos.writeObject("UPLOAD_GRAPH");
            oos.flush();

            // Send the GraphImpl object
            oos.writeObject(graph);
            oos.flush();

            // Read the server response (expected to be a String)
            Object responseObj = ois.readObject();
            if (responseObj instanceof String) {
                serverResponse = (String) responseObj;
            }
        } catch (Exception e) {
            System.err.println("ClientStub encountered an error while uploading graph: " + e.getMessage());
            e.printStackTrace();
        }
        return serverResponse;
    }

    /**
     * Sends a query (e.g. "prime-path A B" or "shortest-path A B") to the server.
     * Communication is done using object streams (sending and receiving String objects).
     */
    public String sendQuery(String query) {
        String response = null;
        try (Socket socket = new Socket(serverHost, serverPort);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            ) {
            oos.flush();

            // Send the query command as an object
            oos.writeObject(query);
            oos.flush();

            // Read the response (as a String)
            Object responseObj = ois.readObject();
            if (responseObj instanceof String) {
                response = (String) responseObj;
            }
        } catch (Exception e) {
            System.err.println("ClientStub encountered an error while sending query: " + e.getMessage());
            e.printStackTrace();
        }
        return response;
    }
}
