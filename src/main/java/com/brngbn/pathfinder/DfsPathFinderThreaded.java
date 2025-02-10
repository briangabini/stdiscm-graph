package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class DfsPathFinderThreaded implements PathFinder {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        List<String> nodeList = graph.getNodeList();
        for (int i = 0; i < nodeList.size(); i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }

        if (!nodeIndexMap.containsKey(source) || !nodeIndexMap.containsKey(dest)) {
            return new ArrayList<>(); // Return empty list if source or destination doesn't exist
        }

        int adjListSize = graph.getAdjacencyList().size();
        boolean[] visited = new boolean[adjListSize];
        List<GraphImpl.Edge> currentPath = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(8);
        Future<List<GraphImpl.Edge>> future = executor.submit(() -> {
            List<GraphImpl.Edge> resultPath = new ArrayList<>();
            if (DFSRec(graph.getAdjacencyList(), visited, nodeIndexMap.get(source), nodeIndexMap.get(dest), currentPath, nodeList, resultPath)) {
                return resultPath;
            }
            return new ArrayList<>();
        });

        List<GraphImpl.Edge> path = new ArrayList<>();
        try {
            path = future.get(); // Wait for DFS task to complete
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        return path;
    }

    // Recursive function for DFS traversal
    private static boolean DFSRec(Map<String, List<String>> adj, boolean[] visited, int sourceIndex, int destinationIndex, List<GraphImpl.Edge> currentPath, List<String> nodes, List<GraphImpl.Edge> resultPath) {
        visited[sourceIndex] = true;

        if (sourceIndex == destinationIndex) {
            resultPath.addAll(new ArrayList<>(currentPath));
            visited[sourceIndex] = false;
            return true; // Path found
        }

        List<String> neighbors = adj.get(nodes.get(sourceIndex));
        if (neighbors != null) {
            for (String neighbor : neighbors) {
                int neighborIndex = nodes.indexOf(neighbor);
                if (neighborIndex != -1 && !visited[neighborIndex]) {
                    currentPath.add(new GraphImpl.Edge(nodes.get(sourceIndex), neighbor));
                    if (DFSRec(adj, visited, neighborIndex, destinationIndex, currentPath, nodes, resultPath)) {
                        return true; // Stop once a path is found
                    }
                    currentPath.removeLast(); // Backtrack
                }
            }
        }

        visited[sourceIndex] = false; // Unmark the current node
        return false;
    }
}
