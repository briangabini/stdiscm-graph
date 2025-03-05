package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;
import java.util.concurrent.*;

public class DfsPathFinderThreaded implements PathFinder {

    private static final int MAX_THREADS = 6;

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        if (!graph.getAdjacencyList().containsKey(source) || !graph.getAdjacencyList().containsKey(dest)) {
            return new ArrayList<>(); // Return empty if nodes don't exist
        }

        return parallelDFS(graph, source, dest);
    }

    /**
     * Parallel DFS using a thread pool to handle deep graphs without recursion depth issues.
     */
    private List<GraphImpl.Edge> parallelDFS(GraphImpl graph, String source, String destination) {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
        ConcurrentMap<String, String> parentMap = new ConcurrentHashMap<>(); // Tracks path traversal
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        Set<String> visited = Collections.newSetFromMap(new ConcurrentHashMap<>());

        queue.add(source);
        visited.add(source);

        List<Future<Void>> futures = new ArrayList<>();

        while (!queue.isEmpty()) {
            List<Callable<Void>> tasks = new ArrayList<>();
            while (!queue.isEmpty()) {
                String current = queue.poll();
                if (current.equals(destination)) {
                    executor.shutdown();
                    return constructPath(parentMap, source, destination);
                }

                List<String> neighbors = graph.getAdjacencyList().getOrDefault(current, Collections.emptyList());
                for (String neighbor : neighbors) {
                    if (visited.add(neighbor)) { // Add only if not visited
                        parentMap.put(neighbor, current);
                        queue.add(neighbor);
                        tasks.add(() -> {
                            processNeighbor(neighbor, current);
                            return null;
                        });
                    }
                }
            }

            try {
                futures.addAll(executor.invokeAll(tasks)); // Process tasks in parallel
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
        return new ArrayList<>(); // No path found
    }

    private void processNeighbor(String neighbor, String parent) {
        // This is a placeholder for potential logging or additional processing
    }

    /**
     * Constructs the path from source to destination using parent mapping.
     */
    private List<GraphImpl.Edge> constructPath(Map<String, String> parentMap, String source, String destination) {
        List<GraphImpl.Edge> path = new ArrayList<>();
        String current = destination;

        while (!current.equals(source)) {
            String parent = parentMap.get(current);
            if (parent == null) {
                return new ArrayList<>();
            }
            path.add(new GraphImpl.Edge(parent, current));
            current = parent;
        }

        Collections.reverse(path);
        return path;
    }
}
