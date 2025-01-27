package com.brngbn.graph;

import lombok.Getter;

import java.io.IOException;
import java.util.*;

@Getter
public class Graph {
    private Map<String, List<String>> adjacencyList;        // default: when reading the graph config file

    public Graph() {
        adjacencyList = new HashMap<>();
    }

    public void readGraphConfig(String fileName) throws IOException {
        GraphConfigReader.initialize();
        GraphConfigReader.getInstance().readGraphConfig(fileName);
        adjacencyList = GraphConfigReader.getInstance().getAdjacencyList();
    }

    public List<String> getNodeList() {
        return new ArrayList<>(adjacencyList.keySet());
    }

    public List<Edge> getEdgeList() {
        List<Edge> edgeList = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            String source = entry.getKey();
            for (String destination : entry.getValue()) {
                edgeList.add(new Edge(source, destination));
            }
        }
        return edgeList;
    }

    public boolean hasNode(String node) {
        return adjacencyList.containsKey(node);
    }

    public boolean hasEdge(String source, String destination) {
        return adjacencyList.containsKey(source) && adjacencyList.get(source).contains(destination);
    }

    public boolean hasEdge (Edge edge) {
        return adjacencyList.containsKey(edge.source()) && adjacencyList.get(edge.source()).contains(edge.destination());
    }

    public record Edge(String source, String destination) { }
}