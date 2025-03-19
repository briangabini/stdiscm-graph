package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;
import java.util.concurrent.*;

import static com.brngbn.pathfinder.PathFindingHelper.isPrime;

public class PrimePathThreadedStrategy implements PathFindingStrategy {

    /**
     * Uses recursive DFS to find the first simple path from source to dest with a prime total weight.
     */
    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        Set<String> visited = ConcurrentHashMap.newKeySet();
        try {
            List<GraphImpl.Edge> result = dfsFindPrimePathThreaded(graph, source, dest, visited, new ArrayList<>()).get();
            return result == null ? new ArrayList<>() : result;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private static Future<List<GraphImpl.Edge>> dfsFindPrimePathThreaded(GraphImpl graph, String current, String dest,
                                                                         Set<String> visited, List<GraphImpl.Edge> path) {
        ExecutorService executor = Executors.newCachedThreadPool();
        return executor.submit(() -> {
            try {
                if (current.equals(dest)) {
                    int total = path.stream().mapToInt(edge -> edge.weight).sum();
                    return isPrime(total) ? new ArrayList<>(path) : null;
                }

                visited.add(current);
                List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());
                List<Future<List<GraphImpl.Edge>>> futures = new ArrayList<>();

                for (GraphImpl.Edge edge : edges) {
                    if (!visited.contains(edge.neighbor)) {
                        path.add(edge);
                        futures.add(dfsFindPrimePathThreaded(graph, edge.neighbor, dest, visited, new ArrayList<>(path)));
                        path.remove(path.size() - 1);
                    }
                }
                visited.remove(current);

                for (Future<List<GraphImpl.Edge>> future : futures) {
                    try {
                        List<GraphImpl.Edge> result = future.get();
                        if (result != null) {
                            return result;
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }
}
