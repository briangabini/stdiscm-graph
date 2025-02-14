package com.brngbn.graph;

import java.util.*;
import java.util.concurrent.Callable;

public class TaskAssignmentHelper {
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    public static List<Callable<Boolean>> assignNodeTasks(Map<String, List<String>> adjacencyList, String node) {
        List<String> nodes = new ArrayList<>(adjacencyList.keySet());
        int chunkSize = (int) Math.ceil((double) nodes.size() / MAX_THREADS);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, nodes.size());

            // Debug: Print assigned task range
            System.out.println("Task checking nodes from index " + start + " to " + end);

            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    if (nodes.get(j).equals(node)) {
                        System.out.println("Found node: " + node);
                        return true;  // Ensure we explicitly return true when found
                    }
                }
                return false;
            });
        }
        return tasks;
    }

    public static List<Callable<Boolean>> assignEdgeTasks(Map<String, List<String>> adjacencyList, String source, String destination) {
        List<Map.Entry<String, List<String>>> entries = new ArrayList<>(adjacencyList.entrySet());
        int chunkSize = (int) Math.ceil((double) entries.size() / MAX_THREADS);
        List<Callable<Boolean>> tasks = new ArrayList<>();

        for (int i = 0; i < entries.size(); i += chunkSize) {
            int start = i;
            int end = Math.min(i + chunkSize, entries.size());
            tasks.add(() -> {
                for (int j = start; j < end; j++) {
                    Map.Entry<String, List<String>> entry = entries.get(j);
                    if (entry.getKey().equals(source) && entry.getValue().contains(destination)) {
                        return true;
                    }
                }
                return false;
            });
        }
        return tasks;
    }
}
