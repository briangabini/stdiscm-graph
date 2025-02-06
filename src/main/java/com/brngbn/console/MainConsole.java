package com.brngbn.console;

import com.brngbn.graph.GraphImpl;

import java.util.Scanner;

public class MainConsole {

    public static void handleUserQueries(GraphImpl graph) {
        asciiHeader();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your query: ");
            String query = scanner.nextLine();

            if (query.equals("exit")) {
                break;
            }

            processQuery(query, graph);
        }
    }

    private static void processQuery(String query, GraphImpl graph) {
        if (query.equals("nodes")) {
            graph.printNodes();
        } else if (query.startsWith("node ")) {
            handleNodeQuery(query.substring(5), graph);
        } else if (query.equals("edges")) {
            graph.printEdges();
        } else if (query.startsWith("edge ")) {
            handleEdgeQuery(query.substring(5), graph);
        } else {
            System.out.println("Invalid query.");
        }
    }

    private static void handleNodeQuery(String node, GraphImpl graph) {
        System.out.println("Node " + node + (graph.hasNode(node) ? " is" : " is not") + " in the graph.");
    }

    private static void handleEdgeQuery(String edgeQuery, GraphImpl graph) {
        String[] parts = edgeQuery.split(" ");
        if (parts.length == 2) {
            String source = parts[0];
            String destination = parts[1];
            String message = graph.hasEdge(source, destination) ?
                "Edge (" + source + ", " + destination + ") is in the graph." :
                "Edge (" + source + ", " + destination + ") is not in the graph.";
            System.out.println(message);
        } else {
            System.out.println("Invalid query format for edge.");
        }
    }

    private static void asciiHeader() {
        System.out.println("   _____ _____            _____  _    _  _____ ______          _____   _____ _    _ ");
        System.out.println("  / ____|  __ \\     /\\   |  __ \\| |  | |/ ____|  ____|   /\\   |  __ \\ / ____| |  | |");
        System.out.println(" | |  __| |__) |   /  \\  | |__) | |__| | (___ | |__     /  \\  | |__) | |    | |__| |");
        System.out.println(" | | |_ |  _  /   / /\\ \\ |  ___/|  __  |\\___ \\|  __|   / /\\ \\ |  _  /| |    |  __  |");
        System.out.println(" | |__| | | \\ \\  / ____ \\| |    | |  | |____) | |____ / ____ \\| | \\ \\| |____| |  | |");
        System.out.println("  \\_____|_|  \\_\\/_/    \\_\\_|    |_|  |_|_____/|______/_/    \\_\\_|  \\_\\\\_____|_|  |_|");
    }
}