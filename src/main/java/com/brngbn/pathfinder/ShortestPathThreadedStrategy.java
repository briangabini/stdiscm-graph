package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;
import java.util.concurrent.*;

public class ShortestPathThreadedStrategy implements PathFindingStrategy {

    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        ExecutorService executor = Executors.newCachedThreadPool();
        Map<String, Integer> dist = new ConcurrentHashMap<>();
        Map<String, String> prev = new ConcurrentHashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        try {
            for (String node : graph.getNodeList()) {
                dist.put(node, Integer.MAX_VALUE);
            }
            dist.put(source, -1);
            queue.add(source);

            while (!queue.isEmpty()) {
                String u = queue.poll();
                if (u.equals(dest)) break;
                List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(u, new LinkedList<>());

                List<Future<?>> futures = new ArrayList<>();
                for (GraphImpl.Edge edge : edges) {
                    futures.add(executor.submit(() -> {
                        String v = edge.neighbor;
                        int alt = dist.get(u) + edge.weight;
                        synchronized (dist) { // Synchronization ensures thread safety for distance updates
                            if (alt < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                                dist.put(v, alt);
                                prev.put(v, u);
                                synchronized (queue) {
                                    queue.remove(v); // Remove and re-add to maintain proper priority order
                                    queue.add(v);
                                }
                            }
                        }
                    }));
                }

                for (Future<?> future : futures) {
                    try {
                        future.get(); // Ensure all threads finish processing before proceeding
                    } catch (InterruptedException |
                             ExecutionException e) {
                        Thread.currentThread().interrupt(); // Preserve interrupt status
                        e.printStackTrace();
                    }
                }
            }

            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }

        List<GraphImpl.Edge> path = new ArrayList<>();
        if (!prev.containsKey(dest)) return path;
        String current = dest;
        while (!current.equals(source)) {
            String parent = prev.get(current);
            if (parent == null) return new ArrayList<>();
            GraphImpl.Edge foundEdge = null;
            for (GraphImpl.Edge edge : graph.getAdjacencyList().get(parent)) {
                if (edge.neighbor.equals(current)) {
                    foundEdge = edge;
                    break;
                }
            }
            if (foundEdge == null) foundEdge = new GraphImpl.Edge(current, -1);
            path.add(foundEdge);
            current = parent;
        }
        Collections.reverse(path);
        return path;
    }
}
