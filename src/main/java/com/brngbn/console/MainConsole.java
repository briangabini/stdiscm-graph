package com.brngbn.console;

import com.brngbn.agent.AgentSimulator;
import com.brngbn.graph.GraphConfigParser;
import com.brngbn.graph.GraphImpl;
import com.brngbn.pathfinder.PathFindingService;
import com.brngbn.thread.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;


import java.io.*;
import java.util.*;
import java.util.Scanner;

@Slf4j
public class MainConsole {

    private static final boolean debug = true;

    private static boolean useParallel = false;

    private final static String inputDirectory = "src/main/resources/inputs/";

    public static void handleUserQueries(GraphImpl graph) {
        asciiHeader();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your query: ");
            String query = scanner.nextLine().trim();

            if (query.equalsIgnoreCase("exit")) {
                ThreadPoolManager.getInstance().shutdown();
                System.out.println("Exiting program...");
                break;
            } else if (query.startsWith("readCommands ")) {
                handleFileInput(query.substring(13), graph);
            } else {
                processQuery(query, graph);
            }
        }
    }

    private static void handleFileInput(String filename, GraphImpl graph) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputDirectory + filename))) {
            System.out.println("Processing queries from file: " + filename);
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    System.out.println("Executing: " + line);
                    processQuery(line, graph);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + filename);
        }
    }

    private static void processQuery(String query, GraphImpl graph) {
        TimeMeasurer timeMeasurer = TimeMeasurer.getInstance();
        String timerName = "query";

        if (query.equals("nodes")) {
            timeMeasurer.startTracking(timerName);
            graph.printNodes();
        } else if (query.startsWith("node ")) {
            timeMeasurer.startTracking(timerName);
            handleNodeQuery(query.substring(5), graph);
        } else if (query.equals("edges")) {
            timeMeasurer.startTracking(timerName);
            graph.printEdges();
        } else if (query.startsWith("edge ")) {
            timeMeasurer.startTracking(timerName);
            handleEdgeQuery(query.substring(5), graph);
        } else if (query.startsWith("path ") || query.startsWith("prime-path ")
                || query.startsWith("shortest-path ") || query.startsWith("shortest-prime-path ")) {
            timeMeasurer.startTracking(timerName);
            handlePathQuery(query, graph);
        } else if (query.startsWith("parallel ")) {
            timeMeasurer.startTracking(timerName);
            handleParallelCommand(query);
        } else if (query.equals("simulate-agents")) {
            handleSimulateAgents(graph);
        } else {
            System.out.println("Invalid query.");
            return;
        }

        timeMeasurer.calculateAndPrintDuration(timerName);
    }

    private static void handleSimulateAgents(GraphImpl graph) {

        Map<String, String> agentLocations = GraphConfigParser.getInstance().getAgentLocations();       // Retrieve the agent mapping (agent label -> starting node) from the parser.
        AgentSimulator.simulateAgents(graph, agentLocations);                                           // Start the simulation
    }


    private static void handleParallelCommand(String query) {
        try {
            int value = Integer.parseInt(query.split(" ")[1]);
            useParallel = value == 1;
            System.out.println("Parallel execution " + (useParallel ? "enabled" : "disabled"));
        } catch (Exception e) {
            System.out.println("Invalid format. Use: parallel 1 (enable) or parallel 0 (disable)");
        }
    }

    private static void handleNodeQuery(String node, GraphImpl graph) {
        boolean exists = useParallel ? graph.hasNodeThreaded(node) : graph.hasNodeSerial(node);
        System.out.println("Node " + node + (exists ? " is" : " is not") + " in the graph.");
    }

    private static void handleEdgeQuery(String edgeQuery, GraphImpl graph) {
        String[] parts = edgeQuery.split(" ");
        if (parts.length == 2) {
            String source = parts[0];
            String destination = parts[1];

            boolean exists = useParallel ? graph.hasEdgeThreaded(source, destination) : graph.hasEdgeSerial(source, destination);
            System.out.println("Edge (" + source + ", " + destination + ") " + (exists ? "is" : "is not") + " in the graph.");
        } else {
            System.out.println("Invalid query format for edge.");
        }
    }

    /**
     * Handles path queries:
     *   - "path x y": any path (DFS)
     *   - "prime-path x y": DFS search for a prime weighted path (tries alternative paths)
     *   - "shortest-path x y": shortest path (Dijkstra’s algorithm)
     *   - "shortest-prime-path x y": shortest path among those with prime total weight
     */
    private static void handlePathQuery(String pathQuery, GraphImpl graph) {
        String[] parts = pathQuery.split(" ");
        if (parts.length != 3) {
            System.out.println("Invalid query format for path queries.");
            return;
        }
        String queryType = parts[0];
        String source = parts[1];
        String dest = parts[2];

        PathFindingService service = new PathFindingService();
        List<GraphImpl.Edge> path = service.findPathQuery(graph, queryType, source, dest, useParallel);

        if (path.isEmpty()) {
            if (queryType.equals("prime-path") || queryType.equals("shortest-prime-path"))
                System.out.println("No prime path from " + source + " to " + dest);
            else
                System.out.println("No path found between " + source + " and " + dest);
            return;
        }

        int totalWeight = path.stream().mapToInt(edge -> edge.weight).sum();
        // For prime queries, our helper methods ensure that the returned path has prime weight.
        StringBuilder sb = new StringBuilder();
        sb.append(queryType).append(": ").append(source);
        for (GraphImpl.Edge edge : path) {
            sb.append(" -> ").append(edge.neighbor);
        }
        sb.append(" with weight/length = ").append(totalWeight);
        System.out.println(sb.toString());
    }

    private static void asciiHeader() {
        System.out.println("   _____ _____            _____  _    _  _____ ______          _____   _____ _    _ ");
        System.out.println("  / ____|  __ \\     /\\   |  __ \\| |  | |/ ____|  ____|   /\\   |  __ \\ / ____| |  | |");
        System.out.println(" | |  __| |__) |   /  \\  | |__) | |__| | (___ | |__     /  \\  | |__) | |    | |__| |");
        System.out.println(" | | |_ |  _  /   / /\\ \\ |  ___/|  __  |\\___ \\|  __|   / /\\ \\ |  _  /| |    |  __  |");
        System.out.println(" | |__| | | \\ \\  / ____ \\| |    | |  | |____) | |____ / ____ \\| | \\ \\| |____| |  | |");
        System.out.println("  \\_____|_|  \\_\\/_/    \\_\\_|    |_|  |_|_____/|______/_/    \\_\\_|  \\_\\\\_____|_|  |_|");
    }
}
