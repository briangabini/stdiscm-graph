package com.brngbn.graph;

import lombok.Getter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class GraphImpl {
    private Map<String, List<String>> adjacencyList;        // default: when reading the graph config file

    public GraphImpl() {
        adjacencyList = new HashMap<>();
    }

    public void readGraphConfig(String fileName) throws IOException {
        GraphConfigParser.initialize();
        GraphConfigParser.getInstance().readGraphConfig(fileName);
        adjacencyList = GraphConfigParser.getInstance().getAdjacencyList();
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

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            sb.append(entry.getKey()).append(":");
            for (String edge : entry.getValue()) {
                sb.append(" -> ").append(edge);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // print methods
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