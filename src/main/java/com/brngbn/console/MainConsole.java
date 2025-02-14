package com.brngbn.console;

import com.brngbn.graph.GraphImpl;
import com.brngbn.pathfinder.DfsPathFinder;
import com.brngbn.pathfinder.PathFinder;
import com.brngbn.thread.ThreadPoolManager;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

@Slf4j
public class MainConsole {

    // debug
    private static final boolean debug = true;

    public static void handleUserQueries(GraphImpl graph) {
        asciiHeader();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Enter your query: ");
            String query = scanner.nextLine();

            if (query.equals("exit")) {
                ThreadPoolManager.getInstance().shutdown();
                break;
            }

            processQuery(query, graph);
        }
    }

    private static void processQuery(String query, GraphImpl graph) {
        query = query.trim();
        TimeMeasurer timeMeasurer = TimeMeasurer.getInstance();

        if (query.equals("nodes")) {
            graph.printNodes();
        } else if (query.startsWith("node ")) {
            timeMeasurer.startTracking();
            handleNodeQuery(query.substring(5), graph);
        } else if (query.equals("edges")) {
            timeMeasurer.startTracking();
            graph.printEdges();
        } else if (query.startsWith("edge ")) {
            timeMeasurer.startTracking();
            handleEdgeQuery(query.substring(5), graph);
        } else if (query.startsWith("path ")) {
            timeMeasurer.startTracking();
            handlePathQuery(query, graph);
        }
        else {
            System.out.println("Invalid query.");
        }

        timeMeasurer.calculateAndPrintDuration();
    }

    private static void handlePathQuery(String pathQuery, GraphImpl graph) {
        long duration = 0;

        String[] parts = pathQuery.split(" ");
        if (parts.length == 3) {
            String source = parts[1];
            String dest = parts[2];
            PathFinder pathFinder = new DfsPathFinder();

            List<GraphImpl.Edge> path = pathFinder.findPath(graph, source, dest);

            if (path.isEmpty()) {
                System.out.println("No path found between " + source + " and " + dest);
            } else {
                System.out.println("Path between " + source + " and " + dest + ":");
                for (GraphImpl.Edge edge : path) {
                    System.out.print("(" + edge + ") ");
                }
                System.out.println();
            }
        } else {
            System.out.println("Invalid query format for path.");
        }

        if (debug) {
            log.debug("Query took {} milliseconds to process.", duration);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private static void handleNodeQuery(String node, GraphImpl graph) {
        System.out.println("Node " + node + (graph.hasNodeThreaded(node) ? " is" : " is not") + " in the graph.");
    }

    private static void handleEdgeQuery(String edgeQuery, GraphImpl graph) {
        String[] parts = edgeQuery.split(" ");
        if (parts.length == 2) {
            String source = parts[0];
            String destination = parts[1];
            String message = graph.hasEdgeThreaded(source, destination) ?
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