package com.brngbn.graph;

import com.brngbn.thread.ThreadPoolManager;
import lombok.Getter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;

@Getter
public class GraphImpl {
    private final Map<String, List<String>> adjacencyList;

    public GraphImpl() {
        adjacencyList = new HashMap<>();
    }

    public void readGraphConfig(String fileName) throws IOException {
        GraphConfigParser.initialize();
        GraphConfigParser.getInstance().readGraphConfig(fileName);
        adjacencyList.putAll(GraphConfigParser.getInstance().getAdjacencyList());
    }

    public List<String> getNodeList() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public List<Edge> getEdgeList() {
        List<Edge> edgeList = new ArrayList<>();
        adjacencyList.forEach((source, destinations) ->
                destinations.forEach(destination -> edgeList.add(new Edge(source, destination))));
        return edgeList;
    }

    public boolean hasNodeSerial(String node) {
        return adjacencyList.containsKey(node);
    }

    public boolean hasEdgeSerial(String source, String destination) {
        return adjacencyList.containsKey(source) && adjacencyList.get(source).contains(destination);
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
        adjacencyList.forEach((key, values) -> {
            sb.append(key).append(": ");
            values.forEach(value -> sb.append(" -> ").append(value));
            sb.append("\n");
        });
        return sb.toString();
    }

    public void printNodes() {
        System.out.println("Nodes: " + getNodeList());
    }

    public void printEdges() {
        System.out.println("Edges: " + getEdgeList());
    }

    public record Edge(String source, String destination) {
        @Override
        public String toString() {
            return source + " -> " + destination;
        }
    }
}
