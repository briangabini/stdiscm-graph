package com.brngbn.console;

import com.brngbn.graph.GraphImpl;
import com.brngbn.pathfinder.DfsPathFinder;
import com.brngbn.pathfinder.DfsPathFinderThreaded;
import com.brngbn.pathfinder.PathFinder;
import com.brngbn.thread.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


import java.io.*;
import java.util.*;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class MainConsole {

    // Debug mode
    private static final boolean debug = true;

    // Flag to control parallel execution
    private static boolean useParallel = false;

    private final static String inputDirectory = "src/main/resources/inputs/";

    private static final ExecutorService executorService = Executors.newFixedThreadPool(6);
    // or
    // private static final ExecutorService executorService = Executors.newCachedThreadPool();

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
        } else {
            System.out.println("Invalid query.");
            return;
        }

        timeMeasurer.calculateAndPrintDuration(timerName);
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

        List<GraphImpl.Edge> path;
        switch (queryType) {
            case "path":
                PathFinder dfsPathFinder = useParallel ? new DfsPathFinderThreaded() : new DfsPathFinder();
                path = dfsPathFinder.findPath(graph, source, dest);
                break;
            case "prime-path":
                // Use the parallelized findPrimePath if useParallel is true, otherwise use the regular one
                path = useParallel ? findPrimePathWithThreadPool(graph, source, dest) : findPrimePath(graph, source, dest);
                break;
            case "shortest-path":
                path = findShortestPath(graph, source, dest);
                break;
            case "shortest-prime-path":
                path = findShortestPrimePath(graph, source, dest);
                break;
            default:
                System.out.println("Invalid path query type.");
                return;
        }

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

    /**
     * Uses recursive DFS to find the first simple path from source to dest with a prime total weight.
     */
    private static List<GraphImpl.Edge> findPrimePath(GraphImpl graph, String source, String dest) {
        Set<String> visited = new HashSet<>();
        List<GraphImpl.Edge> result = dfsFindPrimePath(graph, source, dest, visited, new ArrayList<>());
        return result == null ? new ArrayList<>() : result;
    }


    private static List<GraphImpl.Edge> dfsFindPrimePath(GraphImpl graph, String current, String dest,
                                                         Set<String> visited, List<GraphImpl.Edge> path) {
        if (current.equals(dest)) {
            int total = path.stream().mapToInt(edge -> edge.weight).sum();
            if (isPrime(total))
                return new ArrayList<>(path);
            else
                return null;
        }
        visited.add(current);
        List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());
        for (GraphImpl.Edge edge : edges) {
            if (!visited.contains(edge.neighbor)) {
                path.add(edge);
                List<GraphImpl.Edge> result = dfsFindPrimePath(graph, edge.neighbor, dest, visited, path);
                if (result != null)
                    return result;
                path.remove(path.size() - 1);
            }
        }
        visited.remove(current);
        return null;
    }

    /**
     * Uses recursive DFS to find the first simple path from source to dest with a prime total weight.
     */
    private static List<GraphImpl.Edge> findPrimePathWithThreadPool(GraphImpl graph, String source, String dest) {
        Set<String> visited = new HashSet<>();
        List<GraphImpl.Edge> result = dfsFindPrimePathWithThreadPool(graph, source, dest, visited, new ArrayList<>());
        return result == null ? new ArrayList<>() : result;
    }

    /**
     * Prime Path Parallel - Uses recursive DFS to find the first simple path from source to dest with a prime total weight.
     */
    private static List<GraphImpl.Edge> dfsFindPrimePathWithThreadPool(GraphImpl graph, String current, String dest,
                                                                       Set<String> visited, List<GraphImpl.Edge> path) {
        if (current.equals(dest)) {
            int total = path.stream().mapToInt(edge -> edge.weight).sum();
            if (isPrime(total))
                return new ArrayList<>(path);
            else
                return null;
        }

        visited.add(current);
        List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());

        List<Future<List<GraphImpl.Edge>>> futures = new ArrayList<>();

        // Submit each recursive DFS call as a separate task
        for (GraphImpl.Edge edge : edges) {
            if (!visited.contains(edge.neighbor)) {
                path.add(edge);
                // Submit the DFS task as a future
                futures.add(executorService.submit(() -> dfsFindPrimePathWithThreadPool(graph, edge.neighbor, dest, visited, path)));
            }
        }

        // Collect the results
        for (Future<List<GraphImpl.Edge>> future : futures) {
            try {
                List<GraphImpl.Edge> result = future.get();
                if (result != null) {
                    executorService.shutdownNow(); // Optionally, shut down if a valid result is found.
                    return result;
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        visited.remove(current);
        return null;
    }

    /**
     * Uses Dijkstra's algorithm to find the shortest path (by total weight) from source to dest.
     */
    private static List<GraphImpl.Edge> findShortestPath(GraphImpl graph, String source, String dest) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (String node : graph.getNodeList()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);
        queue.add(source);

        while (!queue.isEmpty()) {
            String u = queue.poll();
            if (u.equals(dest)) break;
            List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(u, new LinkedList<>());
            for (GraphImpl.Edge edge : edges) {
                String v = edge.neighbor;
                int alt = dist.get(u) + edge.weight;
                if (alt < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    queue.remove(v);
                    queue.add(v);
                }
            }
        }

        List<GraphImpl.Edge> path = new ArrayList<>();
        if (!prev.containsKey(dest)) return path;
        String current = dest;
        while (!current.equals(source)) {
            String parent = prev.get(current);
            if (parent == null) return new ArrayList<>();
            GraphImpl.Edge foundEdge = null;
            for (GraphImpl.Edge edge : graph.getAdjacencyList().get(parent)) {
                if (edge.neighbor.equals(current)) {
                    foundEdge = edge;
                    break;
                }
            }
            if (foundEdge == null) foundEdge = new GraphImpl.Edge(current, 0);
            path.add(foundEdge);
            current = parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Enumerates all simple paths from source to dest and returns the one with prime total weight and minimum weight.
     */
    private static List<GraphImpl.Edge> findShortestPrimePath(GraphImpl graph, String source, String dest) {
        List<List<GraphImpl.Edge>> allPaths = new ArrayList<>();
        dfsFindAllPaths(graph, source, dest, new HashSet<>(), new ArrayList<>(), allPaths);
        List<List<GraphImpl.Edge>> primePaths = new ArrayList<>();
        for (List<GraphImpl.Edge> path : allPaths) {
            int total = path.stream().mapToInt(edge -> edge.weight).sum();
            if (isPrime(total))
                primePaths.add(path);
        }
        if (primePaths.isEmpty()) return new ArrayList<>();
        List<GraphImpl.Edge> best = null;
        int bestWeight = Integer.MAX_VALUE;
        for (List<GraphImpl.Edge> path : primePaths) {
            int total = path.stream().mapToInt(edge -> edge.weight).sum();
            if (total < bestWeight) {
                bestWeight = total;
                best = path;
            }
        }
        return best == null ? new ArrayList<>() : best;
    }

    private static void dfsFindAllPaths(GraphImpl graph, String current, String dest,
                                        Set<String> visited, List<GraphImpl.Edge> currentPath,
                                        List<List<GraphImpl.Edge>> allPaths) {
        if (current.equals(dest)) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }
        visited.add(current);
        List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());
        for (GraphImpl.Edge edge : edges) {
            if (!visited.contains(edge.neighbor)) {
                currentPath.add(edge);
                dfsFindAllPaths(graph, edge.neighbor, dest, visited, currentPath, allPaths);
                currentPath.removeLast();
            }
        }
        visited.remove(current);
    }

    /**
     * Returns true if n is a prime number.
     */
    private static boolean isPrime(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0)
                return false;
        }
        return true;
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
