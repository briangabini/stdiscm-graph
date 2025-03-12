package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;

import static com.brngbn.pathfinder.PathFindingHelper.isPrime;

public class ShortestPrimePathStrategy implements PathFindingStrategy {

    /**
     * Enumerates all simple paths from source to dest and returns the one with prime total weight and minimum weight.
     */
    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
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

    private void dfsFindAllPaths(GraphImpl graph, String current, String dest,
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
}
