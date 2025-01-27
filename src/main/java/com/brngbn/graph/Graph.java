package com.brngbn.graph;

import lombok.Getter;

import java.io.IOException;
import java.util.*;

@Getter
public class Graph {
    private Map<String, List<String>> adjacencyList;        // default: when reading the graph config file
    private final List<String> nodeList;
    private final List<Edge> edgeList;
    private int[][] adjacencyMatrix;

    public Graph() {
        adjacencyList = new HashMap<>();
        nodeList = new ArrayList<>();
        edgeList = new ArrayList<>();
    }

    public void readGraphConfig(String fileName) throws IOException {
        GraphConfigReader.initialize();
        GraphConfigReader.getInstance().readGraphConfig(fileName);
        adjacencyList = GraphConfigReader.getInstance().getAdjacencyList();
        buildNodeAndEdgeLists();
        buildAdjacencyMatrix();
    }

    private void buildNodeAndEdgeLists() {
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            String node = entry.getKey();
            nodeList.add(node);
            for (String destination : entry.getValue()) {
                edgeList.add(new Edge(node, destination));
            }
        }
    }

    private void buildAdjacencyMatrix() {
        int size = nodeList.size();
        adjacencyMatrix = new int[size][size];
        Map<String, Integer> nodeIndexMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            nodeIndexMap.put(nodeList.get(i), i);
        }
        for (Edge edge : edgeList) {
            int sourceIndex = nodeIndexMap.get(edge.source());
            int destinationIndex = nodeIndexMap.get(edge.destination());
            adjacencyMatrix[sourceIndex][destinationIndex] = 1;
        }
    }

    public record Edge(String source, String destination) { }
}