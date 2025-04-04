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

    private final Map<String, LinkedList<GraphImpl.Edge>> adjacencyList;        // Maps a node label to a list of weighted edges.

    private final Map<String, String> agentLocations;                           // Maps an agent label to the node it occupies.

    private static GraphConfigParser sharedInstance;

    private static final String DIRECTORY = "src/main/java/com/brngbn/test_cases/";

    public GraphConfigParser() {
        adjacencyList = new HashMap<>();
        agentLocations = new HashMap<>();
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
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] split = line.substring(1).trim().split("\\s+");
                if (line.startsWith("*")) {
                    if (split.length > 0) {
                        String nodeLabel = split[0];
                        adjacencyList.putIfAbsent(nodeLabel, new LinkedList<>());

                        // The second token represents the agent occupying the node.
                        if (split.length > 1) {
                            String agentLabel = split[1];
                            agentLocations.put(agentLabel, nodeLabel);
                        }
                    }
                } else if (line.startsWith("-")) {
                    if (split.length >= 3) {
                        String source = split[0];
                        String destination = split[1];
                        int weight = Integer.parseInt(split[2]);

                        // Ensure both source and destination nodes exist.
                        adjacencyList.putIfAbsent(source, new LinkedList<>());
                        adjacencyList.putIfAbsent(destination, new LinkedList<>());
                        GraphImpl.Edge edge = new GraphImpl.Edge(destination, weight);
                        adjacencyList.get(source).add(edge);
                    } else {
                        log.error("Invalid edge line format: {}", line);
                    }
                }
            }
        }
    }
}
