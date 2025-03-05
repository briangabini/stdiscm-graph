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
     * Iterative DFS using an explicit stack.
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
                return constructPath(graph, parentMap, source, destination);
            }

            // Use a new LinkedList as default so the type matches
            List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());
            for (GraphImpl.Edge edge : edges) {
                String neighbor = edge.neighbor; // Use the neighbor field
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
     * Reconstructs the path from source to destination using the parent mapping.
     * For each (parent, child) pair, it looks up the corresponding weighted edge.
     */
    private List<GraphImpl.Edge> constructPath(GraphImpl graph, Map<String, String> parentMap, String source, String destination) {
        List<GraphImpl.Edge> path = new ArrayList<>();
        String current = destination;

        while (!current.equals(source)) {
            String parent = parentMap.get(current);
            if (parent == null) {
                return new ArrayList<>(); // Path reconstruction failed
            }
            // Look up the edge from parent to current.
            GraphImpl.Edge foundEdge = null;
            for (GraphImpl.Edge edge : graph.getAdjacencyList().get(parent)) {
                if (edge.neighbor.equals(current)) {
                    foundEdge = edge;
                    break;
                }
            }
            // If for some reason the edge is not found, create a default one (weight 0)
            if (foundEdge == null) {
                foundEdge = new GraphImpl.Edge(current, 0);
            }
            path.add(foundEdge);
            current = parent;
        }
        Collections.reverse(path); // Reverse to get correct order from source to destination
        return path;
    }
}
