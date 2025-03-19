package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.brngbn.pathfinder.PathFindingHelper.isPrime;

public class ShortestPrimePathThreadedStrategy implements PathFindingStrategy {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        ExecutorService executor = Executors.newCachedThreadPool();
        List<List<GraphImpl.Edge>> allPaths = new ArrayList<>();
        dfsFindAllPaths(graph, source, dest, new HashSet<>(), new ArrayList<>(), allPaths);

        List<Future<List<GraphImpl.Edge>>> futures = new ArrayList<>();

        for (List<GraphImpl.Edge> path : allPaths) {
            futures.add(executor.submit(() -> isPrime(path.stream().mapToInt(edge -> edge.weight).sum()) ? path : null));
        }

        List<List<GraphImpl.Edge>> primePaths = new ArrayList<>();
        for (Future<List<GraphImpl.Edge>> future : futures) {
            try {
                List<GraphImpl.Edge> path = future.get();
                if (path != null) primePaths.add(path);
            } catch (InterruptedException |
                     ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();

        if (primePaths.isEmpty()) return new ArrayList<>();

        return primePaths.stream().min(Comparator.comparingInt(path -> path.stream().mapToInt(edge -> edge.weight).sum())).orElse(new ArrayList<>());
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
