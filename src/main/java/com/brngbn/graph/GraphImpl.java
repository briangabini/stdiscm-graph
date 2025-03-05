package com.brngbn.graph;

import com.brngbn.thread.ThreadPoolManager;
import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@Getter
public class GraphImpl {
    // Each node maps to a linked list of weighted edges.
    private final Map<String, LinkedList<Edge>> adjacencyList;

    public GraphImpl() {
        adjacencyList = new HashMap<>();
    }

    public void readGraphConfig(String fileName) throws IOException {
        GraphConfigParser.initialize();
        GraphConfigParser.getInstance().readGraphConfig(fileName);
        adjacencyList.putAll(GraphConfigParser.getInstance().getAdjacencyList());
    }

    // Returns the list of nodes (the keys of the map).
    public List<String> getNodeList() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    // Reconstructs a “full” edge that includes the source.
    public List<FullEdge> getEdgeList() {
        List<FullEdge> edgeList = new ArrayList<>();
        for (Map.Entry<String, LinkedList<Edge>> entry : adjacencyList.entrySet()) {
            String source = entry.getKey();
            for (Edge edge : entry.getValue()) {
                edgeList.add(new FullEdge(source, edge.neighbor, edge.weight));
            }
        }
        return edgeList;
    }

    public boolean hasNodeSerial(String node) {
        return adjacencyList.containsKey(node);
    }

    // Checks for an edge by iterating over the weighted edges for the given source.
    public boolean hasEdgeSerial(String source, String destination) {
        if (!adjacencyList.containsKey(source)) return false;
        for (Edge edge : adjacencyList.get(source)) {
            if (edge.neighbor.equals(destination)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasNodeThreaded(String node) {
        List<Callable<Boolean>> tasks = TaskAssignmentHelper.assignNodeTasks(adjacencyList, node);
        return ThreadPoolManager.getInstance().executeTasks(tasks);
    }

    public boolean hasEdgeThreaded(String source, String destination) {
        List<Callable<Boolean>> tasks = TaskAssignmentHelper.assignEdgeTasks(adjacencyList, source, destination);
        return ThreadPoolManager.getInstance().executeTasks(tasks);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, LinkedList<Edge>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            for (Edge edge : entry.getValue()) {
                sb.append(" -> ").append(edge);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void printNodes() {
        System.out.println("Nodes: " + getNodeList());
    }

    public void printEdges() {
        System.out.println("Edges: " + getEdgeList());
    }

    // Represents a weighted edge from the source (implicit) to a neighbor.
    public static class Edge {
        public final String neighbor;
        public final int weight;

        public Edge(String neighbor, int weight) {
            this.neighbor = neighbor;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "(" + neighbor + ", " + weight + ")";
        }
    }

    // For visualization: this record holds the source along with destination and weight.
    public static class FullEdge {
        public final String source;
        public final String destination;
        public final int weight;

        public FullEdge(String source, String destination, int weight) {
            this.source = source;
            this.destination = destination;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return source + " -> " + destination + " (" + weight + ")";
        }
    }
}
