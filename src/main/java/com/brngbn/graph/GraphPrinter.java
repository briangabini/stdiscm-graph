package com.brngbn.graph;

import java.util.List;
import java.util.Map;

public class GraphPrinter {
    public static void printGraph(Graph graph) {
        System.out.println("Adjacency List:");
        printAdjacencyList(graph.getAdjacencyList());

        System.out.println("Node List:");
        for (String node : graph.getNodeList()) {
            System.out.println(node);
        }

        System.out.println("Edge List:");
        for (Graph.Edge edge : graph.getEdgeList()) {
            System.out.println(edge.source() + " -> " + edge.destination());
        }

        System.out.println("Adjacency Matrix:");
        for (int[] row : graph.getAdjacencyMatrix()) {
            for (int val : row) {
                System.out.print(val + " ");
            }
            System.out.println();
        }
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