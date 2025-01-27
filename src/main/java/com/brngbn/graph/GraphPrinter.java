package com.brngbn.graph;

import java.util.List;
import java.util.Map;

public class GraphPrinter {
    public static void printGraph(Graph graph) {
        System.out.println("Adjacency List:");
        printAdjacencyList(graph.getAdjacencyList());
    }

    private static void printAdjacencyList(Map<String, List<String>> adjacencyList) {
        for (Map.Entry<String, List<String>> entry : adjacencyList.entrySet()) {
            System.out.print(entry.getKey() + ":");
            for (String edge : entry.getValue()) {
                System.out.print(" -> " + edge);
            }
            System.out.println();
        }
    }
}