package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;

import static com.brngbn.pathfinder.PathFindingHelper.isPrime;

public class PrimePathStrategy implements PathFindingStrategy {

    /**
     * Uses recursive DFS to find the first simple path from source to dest with a prime total weight.
     */
    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
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
}
