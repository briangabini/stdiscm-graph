package com.brngbn.server;

import com.brngbn.graph.GraphImpl;
import com.brngbn.pathfinder.PathFindingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class ServerStub {

    private final int port;
    private final GraphImpl graph;
    private boolean useParallel = true;                 // By default, the server will use threaded logic

    public ServerStub(int port, GraphImpl graph) {
        this.port = port;
        this.graph = graph;
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server listening on port " + port + "...");

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                    String query = in.readLine();
                    if (query == null) {
                        continue;
                    }
                    System.out.println("Received: " + query);

                    // Currently handling only "prime-path" and "shortest-path" queries``
                    String[] parts = query.split("\\s+");
                    if (parts.length == 3 && (parts[0].equals("prime-path") || parts[0].equals("shortest-path"))) {
                        String response = handlePathQuery(parts[0], parts[1], parts[2]);
                        out.println(response);
                    } else {
                        out.println("Error: Server only handles prime-path / shortest-path RPC");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String handlePathQuery(String queryType, String source, String dest) {

        PathFindingService service = new PathFindingService();
        List<GraphImpl.Edge> path = service.findPathQuery(graph, queryType, source, dest, useParallel);

        if (path.isEmpty()) {
            return "No path found for " + queryType + " " + source + " " + dest;
        }

        int totalWeight = path.stream().mapToInt(edge -> edge.weight).sum();
        StringBuilder sb = new StringBuilder();
        sb.append(queryType).append(": ").append(source);
        for (GraphImpl.Edge edge : path) {
            sb.append(" -> ").append(edge.neighbor);
        }
        sb.append(" [weight=").append(totalWeight).append("]");
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        GraphImpl graph = new GraphImpl();
        graph.readGraphConfig("ps3/sample.txt");

        ServerStub server = new ServerStub(8080, graph);
        server.startServer();
    }
}
