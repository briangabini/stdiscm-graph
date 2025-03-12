package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class DfsPathThreadedStrategy implements PathFindingStrategy {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        Set<String> visited = ConcurrentHashMap.newKeySet();
        try {
            List<GraphImpl.Edge> result = dfsFindPathThreaded(graph, source, dest, visited, new ArrayList<>()).get();
            return result == null ? new ArrayList<>() : result;
        } catch (InterruptedException |
                 ExecutionException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private Future<List<GraphImpl.Edge>> dfsFindPathThreaded(GraphImpl graph, String current, String dest,
                                                             Set<String> visited, List<GraphImpl.Edge> path) {
        ExecutorService executor = Executors.newCachedThreadPool();
        return executor.submit(() -> {
            if (current.equals(dest)) {
                return new ArrayList<>(path);
            }
            visited.add(current);
            List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(current, new LinkedList<>());
            List<Future<List<GraphImpl.Edge>>> futures = new ArrayList<>();
            for (GraphImpl.Edge edge : edges) {
                if (!visited.contains(edge.neighbor)) {
                    path.add(edge);
                    futures.add(dfsFindPathThreaded(graph, edge.neighbor, dest, visited, new ArrayList<>(path)));
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
            return null;
        });
    }
}
