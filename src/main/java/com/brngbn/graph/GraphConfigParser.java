package com.brngbn.graph;

import lombok.Getter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Getter
public class GraphConfigParser {
    private final Map<String, List<String>> adjacencyList;

    // Constants
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
            throw new IllegalStateException("GraphConfigReader is not initialized. Call initialize() first.");
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
                    adjacencyList.get(source).add(destination);
                }
            }
        }
    }

}