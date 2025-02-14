package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;

public class DfsPathFinder implements PathFinder {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        if (!graph.getAdjacencyList().containsKey(source) || !graph.getAdjacencyList().containsKey(dest)) {
            return new ArrayList<>(); // Return empty if nodes don't exist
        }

        return iterativeDFS(graph, source, dest);
    }

    /**
     * Iterative DFS using an explicit stack to prevent recursion depth issues.
     */
    private List<GraphImpl.Edge> iterativeDFS(GraphImpl graph, String source, String destination) {
        Map<String, String> parentMap = new HashMap<>(); // Tracks path traversal
        Stack<String> stack = new Stack<>();
        Set<String> visited = new HashSet<>();

        stack.push(source);
        visited.add(source);

        while (!stack.isEmpty()) {
            String current = stack.pop();

            if (current.equals(destination)) {
                return constructPath(parentMap, source, destination);
            }

            List<String> neighbors = graph.getAdjacencyList().getOrDefault(current, Collections.emptyList());
            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current); // Track the path
                    stack.push(neighbor);
                }
            }
        }

        return new ArrayList<>(); // No path found
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
                return new ArrayList<>(); // Path reconstruction failed
            }
            path.add(new GraphImpl.Edge(parent, current));
            current = parent;
        }

        Collections.reverse(path); // Reverse to correct order
        return path;
    }
}
