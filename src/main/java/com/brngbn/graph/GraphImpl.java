package com.brngbn.graph;

import com.brngbn.console.TimeMeasurer;
import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@Getter
public class GraphImpl {
    private Map<String, List<String>> adjacencyList;

    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;

    public GraphImpl() {
        adjacencyList = new HashMap<>();
    }

    // Method to initialize the thread pool
    public void initializeExecutorService() {
        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newFixedThreadPool(MAX_THREADS);
        }
    }

    // Method to shut down the thread pool
    public void shutdownExecutorService() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    public void readGraphConfig(String fileName) throws IOException {
        GraphConfigParser.initialize();
        GraphConfigParser.getInstance().readGraphConfig(fileName);
        adjacencyList = GraphConfigParser.getInstance().getAdjacencyList();
    }

    public List<String> getNodeList() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public List<Edge> getEdgeList() {
        List<Edge> edgeList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            String source = entry.getKey();
            for (String destination : entry.getValue()) {
                edgeList.add(new Edge(source, destination));
            }
        }
        return edgeList;
    }

    // Serial version
    public boolean hasNodeSerial(String node) {
        return adjacencyList.containsKey(node);
    }

    public boolean hasEdgeSerial(String source, String destination) {
        return adjacencyList.containsKey(source) && adjacencyList.get(source).contains(destination);
    }

    // Task Assignment
    private List<Callable<Boolean>> assignNodeTasks(String node) {
        List<String> nodes = new ArrayList<>(adjacencyList.keySet());
        int chunkSize = (int) Math.ceil((double) nodes.size() / MAX_THREADS);

        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < nodes.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, nodes.size());
            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    if (nodes.get(j).equals(node)) {
                        return true;
                    }
                }
                return false;
            });
        }
        return tasks;
    }

    private List<Callable<Boolean>> assignEdgeTasks(String source, String destination) {
        List<Map.Entry<String, List<String>>> entries = new ArrayList<>(adjacencyList.entrySet());
        int chunkSize = (int) Math.ceil((double) entries.size() / MAX_THREADS);

        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < entries.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, entries.size());
            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    Map.Entry<String, List<String>> entry = entries.get(j);
                    if (entry.getKey().equals(source) && entry.getValue().contains(destination)) {
                        return true;
                    }
                }
                return false;
            });
        }
        return tasks;
    }

    // Task Execution
    public boolean hasNodeThreaded(String node) {
        initializeExecutorService();
        List<Callable<Boolean>> tasks = assignNodeTasks(node);

        try {
            List<Future<Boolean>> futures = executorService.invokeAll(tasks);
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    return true;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean hasEdgeThreaded(String source, String destination) {
        initializeExecutorService();
        List<Callable<Boolean>> tasks = assignEdgeTasks(source, destination);

        try {
            List<Future<Boolean>> futures = executorService.invokeAll(tasks);
            for (Future<Boolean> future : futures) {
                if (future.get()) {
                    return true;
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(":");
            for (String edge : entry.getValue()) {
                sb.append(" -> ").append(edge);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void printNodes() {
        System.out.println("Nodes: " + getNodeList());
    }

    public void printEdges() {
        System.out.println("Edges: " + getEdgeList());
    }

    public record Edge(String source, String destination) {
        @Override
        public String toString() {
            return source + " -> " + destination;
        }
    }
}
