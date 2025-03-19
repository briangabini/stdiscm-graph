package com.brngbn.pathfinder;

import com.brngbn.graph.GraphImpl;

import java.util.*;

public class ShortestPathStrategy implements PathFindingStrategy {

    /**
     * Uses Dijkstra's algorithm to find the shortest path (by total weight) from source to dest.
     */
    @Override
    public List<GraphImpl.Edge> findPath(GraphImpl graph, String source, String dest) {
        Map<String, Integer> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        PriorityQueue<String> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));

        for (String node : graph.getNodeList()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, -1);
        queue.add(source);

        while (!queue.isEmpty()) {
            String u = queue.poll();
            if (u.equals(dest)) break;
            List<GraphImpl.Edge> edges = graph.getAdjacencyList().getOrDefault(u, new LinkedList<>());
            for (GraphImpl.Edge edge : edges) {
                String v = edge.neighbor;
                int alt = dist.get(u) + edge.weight;
                if (alt < dist.getOrDefault(v, Integer.MAX_VALUE)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    queue.remove(v);
                    queue.add(v);
                }
            }
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
