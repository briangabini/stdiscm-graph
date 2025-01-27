package com.brngbn.console;

import com.brngbn.graph.Graph;

import java.util.Scanner;

public class MainConsole {
    public static void handleUserQueries(Graph graph) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter your query: ");
            String query = scanner.nextLine();
            if (query.equals("exit")) {
                break;
            } else if (query.equals("nodes")) {
                System.out.println("Nodes: " + graph.getNodeList());
            } else if (query.startsWith("node ")) {
                String node = query.substring(5);
                if (graph.hasNode(node)) {
                    System.out.println("Node " + node + " is in the graph.");
                } else {
                    System.out.println("Node " + node + " is not in the graph.");
                }
            } else if (query.equals("edges")) {
                System.out.println("Edges: " + graph.getEdgeList());
            } else if (query.startsWith("edge ")) {
                String[] parts = query.substring(5).split(" ");
                if (parts.length == 2) {
                    String source = parts[0];
                    String destination = parts[1];
                    if (graph.hasEdge(source, destination)) {
                        System.out.println("Edge (" + source + ", " + destination + ") is in the graph.");
                    } else {
                        System.out.println("Edge (" + source + ", " + destination + ") is not in the graph.");
                    }
                } else {
                    System.out.println("Invalid query format for edge.");
                }
            } else {
                System.out.println("Invalid query.");
            }
        }
    }
}