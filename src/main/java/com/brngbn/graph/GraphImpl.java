package com.brngbn.graph;

import com.brngbn.console.TimeMeasurer;
import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Getter
public class GraphImpl {
    private Map<String, List<String>> adjacencyList;        // default: when reading the graph config file

    // Threads
    private static final int maxNoOfThreads = 8;
    private final ExecutorService executorService = Executors.newFixedThreadPool(maxNoOfThreads);
    private final CompletionService<Boolean> completionService = new ExecutorCompletionService<>(executorService);

    public GraphImpl() {
        adjacencyList = new HashMap<>();
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

    // Time Complexity: O(n)
    public boolean hasNodeSerial(String node) {
        for (String key : adjacencyList.keySet()) {
            if (key.equals(node)) {
                return true;
            }
        }

        return false;
    }

    // Time Complexity: O(n + m), n - nodes, m - edges
    public boolean hasEdgeSerial(String source, String destination) {
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            if (entry.getKey().equals(source)) {
                for (String dest : entry.getValue()) {
                    if (dest.equals(destination)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Threaded versions
    public boolean hasNodeThreaded(String node) {
        List<String> nodes = new ArrayList<>(adjacencyList.keySet());
        int chunkSize = (int) Math.ceil((double) nodes.size() / 8);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, nodes.size());
            futures.add(CompletableFuture.supplyAsync(() -> {
                for (int j = start; j < end; j++) {
                    if (nodes.get(j).equals(node)) {
                        return true;
                    }
                }
                return false;
            }, executorService));
        }

        return futures.stream().anyMatch(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    /*public boolean hasEdgeThreaded(String source, String dest) {
        ArrayList<Map.Entry<String, List<String>>> entries = new ArrayList<>(adjacencyList.entrySet());
        int chunkSize = (int) Math.ceil((double) entries.size() / 8);
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();


        for (int i = 0; i < entries.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, entries.size());
            futures.add(CompletableFuture.supplyAsync(() -> {
                for (int j = start; j < end; j++) {
                    Map.Entry<String, List<String>> entry = entries.get(j);
                    if (entry.getKey().equals(source)) {
                        for (String destVal : entry.getValue()) {
                            if (destVal.equals(dest)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }, executorService));
        }

        TimeMeasurer.getInstance().calculateStartTime();

        boolean res = futures.stream().anyMatch(future -> {
            try {
                return future.get();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        });

        TimeMeasurer.getInstance().calculateEndTimeAndDuration();

        return res;
    }*/
    public boolean hasEdgeThreaded(String source, String dest) {
        ArrayList<Map.Entry<String, List<String>>> entries = new ArrayList<>(adjacencyList.entrySet());
        int chunkSize = (int) Math.ceil((double) entries.size() / 8);

        for (int i = 0; i < entries.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, entries.size());
            completionService.submit(() -> {
                for (int j = start; j < end; j++) {
                    Map.Entry<String, List<String>> entry = entries.get(j);
                    if (entry.getKey().equals(source)) {
                        for (String destVal : entry.getValue()) {
                            if (destVal.equals(dest)) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            });
        }

        TimeMeasurer timeMeasurer = TimeMeasurer.getInstance();
        timeMeasurer.calculateStartTime();

        boolean res = false;
        for (int i = 0; i < entries.size() / chunkSize; i++) {
            try {
                if (completionService.take().get()) {
                    res = true;
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        timeMeasurer.calculateEndTimeAndDuration();
        executorService.shutdown();
        return res;
    }

    // api call
    public boolean hasNode(String node) {
        return adjacencyList.containsKey(node);
    }

    public boolean hasEdge(String source, String destination) {
        return adjacencyList.containsKey(source) && adjacencyList.get(source).contains(destination);
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

    // print methods
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