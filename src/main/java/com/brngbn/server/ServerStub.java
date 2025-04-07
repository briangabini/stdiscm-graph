package com.brngbn.server;

import com.brngbn.graph.GraphImpl;
import com.brngbn.pathfinder.PathFindingService;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerStub {

    private final int port;
    private GraphImpl cachedGraph = null; // Cached graph after upload

    public ServerStub(int port) {
        this.port = port;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port + "...");
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                     ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
                    ) {

                    oos.flush();
                    Object requestObj = ois.readObject();

                    if (requestObj instanceof String command) {

                        System.out.println("Received command: " + command);

                        if (command.equals("UPLOAD_GRAPH")) {

                            Object graphObj = ois.readObject();
                            if (graphObj instanceof GraphImpl) {
                                cachedGraph = (GraphImpl) graphObj;
                                oos.writeObject("Server: Graph uploaded and cached successfully.");
                                oos.flush();
                                System.out.println("Graph received and cached.");
                            } else {
                                oos.writeObject("Error: Uploaded object is not a GraphImpl.");
                                oos.flush();
                            }
                        } else if (isPathQuery(command)) {
                            if (cachedGraph == null) {
                                oos.writeObject("Error: No cached graph. Please upload a graph first.");
                                oos.flush();
                            } else {
                                String response = handlePathQuery(command);
                                oos.writeObject(response);
                                oos.flush();
                            }
                        } else {
                            oos.writeObject("Error: Unknown command.");
                            oos.flush();
                        }
                    } else {
                        oos.writeObject("Error: Expected a String command.");
                        oos.flush();
                    }
                    oos.close();
                    ois.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isPathQuery(String command) {
        String[] parts = command.split("\\s+");
        if (parts.length != 3) return false;
        String qType = parts[0];
        return qType.equals("prime-path") || qType.equals("shortest-path");
    }

    /**
     * Processes a query command ("prime-path A B" or "shortest-path A B") using the cached graph.
     */
    private String handlePathQuery(String command) {
        String[] parts = command.split("\\s+");
        String queryType = parts[0];
        String source = parts[1];
        String dest = parts[2];

        PathFindingService service = new PathFindingService();
        List<GraphImpl.Edge> path = service.findPathQuery(cachedGraph, queryType, source, dest, true);

        if (path.isEmpty()) {
            return "No path found for " + queryType + " " + source + " " + dest;
        }

        int totalWeight = path.stream().mapToInt(e -> e.weight).sum();
        StringBuilder sb = new StringBuilder();
        sb.append(queryType).append(": ").append(source);
        for (GraphImpl.Edge edge : path) {
            sb.append(" -> ").append(edge.neighbor);
        }
        sb.append(" [weight=").append(totalWeight).append("]");
        return sb.toString();
    }

    public static void main(String[] args) {
        ServerStub server = new ServerStub(8080);
        server.startServer();
    }
}
