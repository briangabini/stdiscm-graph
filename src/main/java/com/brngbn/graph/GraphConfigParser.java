package com.brngbn.graph;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

@Slf4j
@Getter
public class GraphConfigParser {
    // Maps a node to a linked list of weighted edges.
    private final Map<String, LinkedList<GraphImpl.Edge>> adjacencyList;

    private static GraphConfigParser sharedInstance;
    private static final String DIRECTORY = "src/main/java/com/brngbn/test_cases/";

    public GraphConfigParser() {
        adjacencyList = new HashMap<>();
    }

    public static void initialize() {
        if (sharedInstance == null) {
            sharedInstance = new GraphConfigParser();
        }
    }

    public static void destroy() {
        sharedInstance = null;
    }

    public static GraphConfigParser getInstance() {
        if (sharedInstance == null) {
            throw new IllegalStateException("GraphConfigParser is not initialized. Call initialize() first.");
        }
        return sharedInstance;
    }

    public void readGraphConfig(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(DIRECTORY + filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("*")) {
                    String node = line.substring(2).trim();
                    adjacencyList.putIfAbsent(node, new LinkedList<>());
                } else if (line.startsWith("-")) {
                    String[] parts = line.substring(2).trim().split(" ");
                    String source = parts[0];
                    String destination = parts[1];
                    int weight = Integer.parseInt(parts[2]);

                    // Ensure both nodes exist in the map.
                    adjacencyList.putIfAbsent(source, new LinkedList<>());
                    adjacencyList.putIfAbsent(destination, new LinkedList<>());

                    GraphImpl.Edge edge = new GraphImpl.Edge(destination, weight);
                    adjacencyList.get(source).add(edge);
                }
            }
        }
    }
}
